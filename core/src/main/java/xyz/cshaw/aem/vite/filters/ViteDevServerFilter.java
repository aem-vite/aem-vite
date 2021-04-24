/*
 *  Copyright 2021 Chris Shaw
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package xyz.cshaw.aem.vite.filters;

import com.adobe.acs.commons.util.BufferedHttpServletResponse;
import com.adobe.acs.commons.util.BufferedServletOutput;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.servlets.annotations.SlingServletFilter;
import org.apache.sling.servlets.annotations.SlingServletFilterScope;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceVendor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cshaw.aem.vite.services.ViteDevServerConfig;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xyz.cshaw.aem.vite.Constants.BODY_END_TAG;
import static xyz.cshaw.aem.vite.Constants.BODY_END_TAG_PATTERN;
import static xyz.cshaw.aem.vite.Constants.CLIENT_ENTRY_POINT_SCRIPT;
import static xyz.cshaw.aem.vite.Constants.CLIENT_HTML_REACT_SCRIPT;
import static xyz.cshaw.aem.vite.Constants.CLIENT_HTML_SCRIPT;

@Component(immediate = true)
@SlingServletFilter(
        methods = {HttpConstants.METHOD_GET},
        scope = SlingServletFilterScope.REQUEST
)
@ServiceDescription("Vite DevServer script injection filter.")
@ServiceVendor("Chris Shaw")
public class ViteDevServerFilter implements Filter {
    private final Logger log = LoggerFactory.getLogger(ViteDevServerFilter.class);

    private final List<ViteDevServerConfig> devServerConfigurations = new LinkedList<>();
    private final List<Function<AtomicReference<String>, String>> responseCallbacks = new ArrayList<>();

    @Reference
    private HtmlLibraryManager htmlLibraryManager;

    @Reference(
            cardinality = ReferenceCardinality.MULTIPLE,
            name = "configurationFactory",
            policy = ReferencePolicy.DYNAMIC
    )
    protected synchronized void bindConfigurationFactory(final ViteDevServerConfig config) {
        log.info("bindConfigurationFactory: {}", config.devServerUrl());

        devServerConfigurations.add(config);
    }

    protected synchronized void unbindConfigurationFactory(final ViteDevServerConfig config) {
        log.info("unbindConfigurationFactory: {}", config.devServerUrl());

        devServerConfigurations.remove(config);
    }

    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;

        log.info("request for {}, with selector {}", slingRequest
                .getRequestPathInfo().getResourcePath(), slingRequest
                .getRequestPathInfo().getSelectorString());

        responseCallbacks.clear();

        boolean useCapturedResponse = false;

        for (ViteDevServerConfig config : devServerConfigurations) {
            if (!accepts(request, config)) {
                log.info("Configuration does not accept this request!");
                log.info("Content paths: {}", (Object) config.contentPaths());

                continue;
            }

            // Short-circuit for testing if the Vite DevServer is available. Whenever an exception occurs, we can assume
            // that it is unavailable as normal responses will generally return a 404 status code rather than throwing.
            try {
                devServerActive(config.devServerUrl());
            } catch (Exception ex) {
                log.info("DevServer is not running!");
                log.info("URL: {}", config.devServerUrl());

                continue;
            }

            useCapturedResponse = true;

            responseCallbacks.add((content) -> handleResponseModificationForDevServer(
                    content.get(),
                    config,
                    slingRequest));
        }

        if (useCapturedResponse) {
            try (BufferedHttpServletResponse capturedResponse = new BufferedHttpServletResponse(response, new StringWriter(), null)) {
                filterChain.doFilter(request, capturedResponse);

                final String contents = capturedResponse.getBufferedServletOutput().getWriteMethod() == BufferedServletOutput.ResponseWriteMethod.WRITER
                        ? capturedResponse.getBufferedServletOutput().getBufferedString()
                        : null;

                if (contents != null && StringUtils.contains(response.getContentType(), "html")) {
                    if (contents.contains("</head>") && contents.contains("</body>")) {
                        // prevent the captured response from being given out a 2nd time via the implicit close()
                        capturedResponse.setFlushBufferOnClose(false);

                        final PrintWriter printWriter = response.getWriter();

                        AtomicReference<String> contentsToModify = new AtomicReference<>(
                                String.copyValueOf(contents.toCharArray()));

                        responseCallbacks.forEach((callback) -> contentsToModify.set(callback.apply(contentsToModify)));

                        printWriter.write(contentsToModify.get());
                    }
                }
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("Vite DevServer filter initialised...");
    }

    @Override
    public void destroy() {
        log.info("Vite DevServer filter has been destroyed.");
    }

    private void devServerActive(final String devServerUrl)
            throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        // A custom SSL context/factory is needed on the chance that someone is testing using a self-signed cert
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build());

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(500)
                .setConnectionRequestTimeout(500)
                .setSocketTimeout(500)
                .build();

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build()) {
            HttpGet request = new HttpGet(devServerUrl);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                log.info("Successfully connected to Vite DevServer... {} ({})", devServerUrl, response.getStatusLine());
            }
        }
    }

    private boolean accepts(final HttpServletRequest request, final ViteDevServerConfig config) {
        if (StringUtils.equals(request.getHeader("X-Requested-With"), "XMLHttpRequest")) {
            // Do not inject into XHR requests
            return false;
        } else if (!acceptsContentPath(request, config)) {
            // Do not inject into content paths that haven't been specified
            return false;
        } else if (!config.automaticInjection() && !requestHasManualInjectionSelector(request, config)) {
            // Only allow injection when a certain selector is present
            return false;
        }

        return true;
    }

    private boolean acceptsContentPath(final HttpServletRequest request, final ViteDevServerConfig config) {
        String requestUri = request.getRequestURI();

        for (String contentPath : config.contentPaths()) {
            // TODO: Try and resolve the 'requestUri' to an actual JCR node so we can match against any resolver mappings
            if (requestUri.startsWith(contentPath)) {
                return true;
            }
        }

        return false;
    }

    private boolean requestHasManualInjectionSelector(
            final HttpServletRequest request,
            final ViteDevServerConfig config) {
        return ArrayUtils.contains(((SlingHttpServletRequest) request).getRequestPathInfo().getSelectors(),
                config.manualInjectionSelector());
    }

    private String handleResponseModificationForDevServer(
            String content,
            final ViteDevServerConfig config,
            final SlingHttpServletRequest slingRequest) {
        log.info("Running callback for: {}", config.devServerUrl());
        log.info("ClientLibs: {}", (Object) config.clientlibCategories());

        try {
            Collection<String> includes = getClientLibraryIncludes(config.clientlibCategories(), slingRequest);

            for (String include : includes) {
                Matcher includeMatches = getClientLibPattern(include).matcher(content);

                while (includeMatches.find()) {
                    content = content.replaceAll(includeMatches.group(), StringUtils.EMPTY);
                }
            }
        } catch (IOException ex) {
            log.warn("Unable to modify response as the ClientLibs generator returned an exception!", ex);
        }

        return injectDevServerClient(content, config);
    }

    private String injectDevServerClient(String content, final ViteDevServerConfig config) {
        List<String> entryPoints = new ArrayList<>();

        for (String entryPoint : config.devServerEntryPoints()) {
            entryPoints.add(String.copyValueOf(CLIENT_ENTRY_POINT_SCRIPT.toCharArray())
                    .replace("$entryPoint", entryPoint));
        }

        StringBuilder clientScripts = new StringBuilder();

        if (config.usingReact()) {
            clientScripts.append(CLIENT_HTML_REACT_SCRIPT);
        }

        clientScripts.append(String.format("%s\n%s", CLIENT_HTML_SCRIPT, String.join("\n", entryPoints))
                .replaceAll("\\$devServer", config.devServerUrl()));

        return BODY_END_TAG_PATTERN.matcher(content).replaceFirst(String.format("%s\n\n%s", clientScripts, BODY_END_TAG));
    }

    private Collection<String> getClientLibraryIncludes(
            final String[] categories,
            final SlingHttpServletRequest slingRequest) throws IOException {
        Collection<String> includes = new ArrayList<>();
        Writer writer = new StringWriter();

        htmlLibraryManager.writeIncludes(slingRequest, writer, categories);

        Matcher includeMatches = getClientLibPattern(null).matcher(writer.toString().trim());

        while (includeMatches.find()) {
            includes.add(includeMatches.group(1));
        }

        return includes;
    }

    private Pattern getClientLibPattern(String customExpression) {
        String expression = StringUtils.isNotEmpty(customExpression) ? customExpression : "(.*\\.(?:css|js))";

        return Pattern.compile(String.format("<(?:script|link).*(?:src|href)=\"%s\".*>", expression));
    }
}
