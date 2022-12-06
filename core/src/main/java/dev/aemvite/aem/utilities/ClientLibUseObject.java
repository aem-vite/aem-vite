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
package dev.aemvite.aem.utilities;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static dev.aemvite.aem.utilities.Constants.*;

public class ClientLibUseObject extends WCMUsePojo {
    protected String[] categories;
    protected String mode;
    protected Boolean esModule;

    protected HtmlLibraryManager htmlLibraryManager = null;
    protected Logger log;
    protected SlingHttpServletRequest request;
    protected Resource resource;

    public static final Map<String, Object> AUTH_INFO =
            Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "aemViteClientLibsService");

    @Override
    public void activate() {
        final Object categoriesObject = get(CLIENTLIB_BINDINGS_CATEGORIES, Object.class);

        log = get(SlingBindings.LOG, Logger.class);
        resource = get("resource", Resource.class);
        request = get(SlingBindings.REQUEST, SlingHttpServletRequest.class);

        if (categoriesObject != null) {
            getCategoriesFromBinding(categoriesObject);

            if (categories != null && categories.length > 0) {
                mode = get(CLIENTLIB_BINDINGS_MODE, String.class);
                esModule = get(CLIENTLIB_BINDINGS_ESMODULE, Boolean.class);
                SlingScriptHelper sling = get(SlingBindings.SLING, SlingScriptHelper.class);

                if (sling != null) {
                    htmlLibraryManager = sling.getService(HtmlLibraryManager.class);
                } else {
                    throw new IllegalStateException("Unable to get an instance of SlingScriptHelper. " +
                            "This is required to get an instance of HtmlLibraryManager.");
                }
            }
        }
    }

    public final String include() {
        final StringWriter sw = new StringWriter();

        if (categories == null || categories.length == 0) {
            log.error("'categories' option might be missing from the invocation of the "
                    + "/apps/granite/sightly/templates/clientlib.html client libraries template library. "
                    + "Please provide a CSV list or an array of categories to include.");
        } else {
            PrintWriter out = new PrintWriter(sw);

            if ("js".equalsIgnoreCase(mode)) {
                includeLibraries(out, LibraryType.JS);
            } else if ("css".equalsIgnoreCase(mode)) {
                includeLibraries(out, LibraryType.CSS);
            } else {
                includeLibraries(out, LibraryType.CSS);
                includeLibraries(out, LibraryType.JS);
            }
        }

        return sw.toString();
    }

    /**
     * Parse through the ClientLib categories from the HTL binding.
     *
     * @param categoriesObject list of categories
     */
    private void getCategoriesFromBinding(final Object categoriesObject) {
        if (categoriesObject instanceof Object[]) {
            Object[] categoriesArray = (Object[]) categoriesObject;
            categories = new String[categoriesArray.length];

            int i = 0;
            for (Object o : categoriesArray) {
                if (o instanceof String) {
                    categories[i++] = ((String) o).trim();
                }
            }
        } else if (categoriesObject instanceof String) {
            categories = ((String) categoriesObject).split(",");

            int i = 0;
            for (String c : categories) {
                categories[i++] = c.trim();
            }
        }
    }

    /**
     * Retrieve the parsed categories list.
     *
     * @return a list of ClientLib categories
     */
    public String[] getCategories() {
        return categories;
    }

    /**
     * Construct the HTML markup for the script and link elements.
     *
     * @param out         {@link PrintWriter} object responsible for writing the HTML
     * @param libraryType {@link LibraryType} which is either CSS or JS
     */
    private void includeLibraries(final PrintWriter out, final LibraryType libraryType) {
        if (htmlLibraryManager != null && libraryType != null) {
            Collection<ClientLibrary> libs = htmlLibraryManager.getLibraries(categories, libraryType, false, true);

            for (ClientLibrary lib : libs) {
                String path = getIncludePath(request, lib, libraryType, htmlLibraryManager.isMinifyEnabled());

                if (path != null) {
                    generateOutputForClientLib(lib, path, libraryType, out);
                }
            }
        }
    }

    /**
     * Returns the include path for the given lib and libraryType, respecting the proxy settings.
     *
     * @param request     {@link SlingHttpServletRequest} instance
     * @param lib         {@link ClientLibrary} instance
     * @param libraryType {@link LibraryType} which is either CSS or JS
     * @param minify      {@code true} for minify, {@code false} for no minification
     * @return the public path of the {@link ClientLibrary}
     */
    protected final String getIncludePath(
            final SlingHttpServletRequest request,
            final ClientLibrary lib,
            final LibraryType libraryType,
            final boolean minify
    ) {
        String path = lib.getIncludePath(libraryType, minify);

        if (needsProxy(lib, path)) {
            path = getClientLibProxyPath(path);
        } else {
            // check if request session has access (GRANITE-4429)
            if (request.getResourceResolver().getResource(lib.getPath()) == null) {
                path = null;
            }
        }

        return path;
    }

    /**
     * Checks if the provided {@link ClientLibrary} should use the '/etc.clientlibs' proxy path.
     *
     * @param lib  {@link ClientLibrary} instance
     * @param path {@link String} path to the {@link ClientLibrary} resource
     * @return {@code true} when the proxy is needed, otherwise {@code false}
     */
    protected final boolean needsProxy(final ClientLibrary lib, final String path) {
        return lib.allowProxy() && (path.startsWith("/libs/") || path.startsWith("/apps/"));
    }

    /**
     * Update the provided input path, so it points to the '/etc' proxy path.
     *
     * @param path {@link String} path to the {@link ClientLibrary} resource
     * @return updated proxy path
     */
    protected final String getClientLibProxyPath(final String path) {
        return "/etc.clientlibs" + path.substring(5);
    }

    /**
     * Determines if the provided property exists on the {@link ClientLibrary}.
     *
     * @param lib      {@link ClientLibrary} instance
     * @param property {@link ClientLibrary} property to lookup
     * @return {@code true} when the property exists, otherwise {@code false}
     */
    protected final boolean clientlibHasProperty(ClientLibrary lib, String property) {
        SlingScriptHelper slingScriptHelper = getSlingScriptHelper();

        if (slingScriptHelper == null) {
            return false;
        }

        ResourceResolverFactory factory = slingScriptHelper.getService(ResourceResolverFactory.class);

        if (factory == null) {
            return false;
        }

        try (ResourceResolver resourceResolver = factory.getServiceResourceResolver(AUTH_INFO)) {
            Resource libResource = resourceResolver.resolve(lib.getPath());

            if (!ResourceUtil.isNonExistingResource(libResource)) {
                ValueMap libProps = libResource.getValueMap();

                return Boolean.TRUE.equals(libProps.get(property, Boolean.class));
            }
        } catch (LoginException ex) {
            log.error("Unable to retrieve resource resolver for 'aem-vite-clientlibs'.");
            log.error(String.valueOf(ex));
        }

        return false;
    }

    /**
     * Generate the HTML tag for the provided {@link ClientLibrary}.
     *
     * @param lib         {@link ClientLibrary} instance
     * @param path        {@link String} path to the {@link ClientLibrary} resource
     * @param libraryType {@link LibraryType} which is either CSS or JS
     * @param out         {@link PrintWriter} object responsible for writing the HTML
     */
    protected void generateOutputForClientLib(
            final ClientLibrary lib,
            final String path,
            final LibraryType libraryType,
            final PrintWriter out
    ) {
        String attributes = getLibraryTypeAttributes(lib, libraryType).toString();
        String tag = libraryType.equals(LibraryType.CSS) ? CLIENTLIB_TAG_STYLESHEET : CLIENTLIB_TAG_JAVASCRIPT;

        out.format(tag, path, attributes);
    }

    /**
     * Retrieve any attributes required for the provided library type.
     *
     * @param lib         {@link ClientLibrary} instance
     * @param libraryType {@link LibraryType} which is either CSS or JS
     * @return a string containing HTML attributes
     */
    protected StringBuilder getLibraryTypeAttributes(final ClientLibrary lib, final LibraryType libraryType) {
        StringBuilder attributes = new StringBuilder();

        if (libraryType.equals(LibraryType.JS)) {
            if (clientlibHasProperty(lib, CLIENTLIB_PROPERTY_ESMODULE)) {
                attributes.append(CLIENTLIB_MODULE_TYPE_ATTRIBUTE);
            }

            if (clientlibHasProperty(lib, CLIENTLIB_PROPERTY_NOMODULE)) {
                attributes.append(CLIENTLIB_NOMODULE_TYPE_ATTRIBUTE);
            }
        }

        return attributes;
    }
}
