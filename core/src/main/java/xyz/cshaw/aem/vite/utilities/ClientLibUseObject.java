package xyz.cshaw.aem.vite.utilities;

import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.scripting.sightly.pojo.Use;
import org.apache.sling.xss.XSSAPI;
import org.slf4j.Logger;

import javax.script.Bindings;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;

import static xyz.cshaw.aem.vite.utilities.Constants.CLIENTLIB_BINDINGS_CATEGORIES;
import static xyz.cshaw.aem.vite.utilities.Constants.CLIENTLIB_BINDINGS_MODE;
import static xyz.cshaw.aem.vite.utilities.Constants.CLIENTLIB_BINDINGS_MODULE;
import static xyz.cshaw.aem.vite.utilities.Constants.CLIENTLIB_BINDINGS_MODULE_FALLBACK;
import static xyz.cshaw.aem.vite.utilities.Constants.CLIENTLIB_ES_SELECTOR;
import static xyz.cshaw.aem.vite.utilities.Constants.CLIENTLIB_MODULE_TYPE_ATTRIBUTE;
import static xyz.cshaw.aem.vite.utilities.Constants.CLIENTLIB_NOMODULE_TYPE_ATTRIBUTE;
import static xyz.cshaw.aem.vite.utilities.Constants.CLIENTLIB_TAG_JAVASCRIPT;
import static xyz.cshaw.aem.vite.utilities.Constants.CLIENTLIB_TAG_STYLESHEET;

public class ClientLibUseObject implements Use {
    protected String[] categories;
    protected String mode;
    protected Boolean useEsModule;
    protected Boolean useEsModuleFallback;

    protected HtmlLibraryManager htmlLibraryManager = null;
    protected Logger log;
    protected SlingHttpServletRequest request;
    protected Resource resource;
    protected ResourceResolver resourceResolver;
    protected XSSAPI xss;

    public void init(Bindings bindings) {
        final Object categoriesObject = bindings.get(CLIENTLIB_BINDINGS_CATEGORIES);
        log = (Logger) bindings.get(SlingBindings.LOG);

        resource = (Resource) bindings.get("resource");
        resourceResolver = resource.getResourceResolver();

        if (categoriesObject != null) {
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

            if (categories != null && categories.length > 0) {
                mode = (String) bindings.get(CLIENTLIB_BINDINGS_MODE);
                useEsModule = (Boolean) bindings.getOrDefault(CLIENTLIB_BINDINGS_MODULE, false);
                useEsModuleFallback = (Boolean) bindings.getOrDefault(CLIENTLIB_BINDINGS_MODULE_FALLBACK, false);
                request = (SlingHttpServletRequest) bindings.get(SlingBindings.REQUEST);
                SlingScriptHelper sling = (SlingScriptHelper) bindings.get(SlingBindings.SLING);
                htmlLibraryManager = sling.getService(HtmlLibraryManager.class);
                xss = sling.getService(XSSAPI.class);
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
     * Construct the HTML markup for the script and link elements.
     *
     * @param out         {@link PrintWriter} object responsible for writing the HTML
     * @param libraryType {@link LibraryType} which is either CSS or JS
     */
    private void includeLibraries(final PrintWriter out, final LibraryType libraryType) {
        if (htmlLibraryManager != null && libraryType != null && xss != null) {
            Collection<ClientLibrary> libs = htmlLibraryManager.getLibraries(categories, libraryType, false, false);

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
     * Update the provided input path so it points to the '/etc' proxy path.
     *
     * @param path {@link String} path to the {@link ClientLibrary} resource
     * @return updated proxy path
     */
    protected final String getClientLibProxyPath(final String path) {
        return "/etc.clientlibs" + path.substring(5);
    }

    /**
     * Determines if the provided binding conditions prove {@code true}.
     *
     * @param lib              {@link ClientLibrary} instance
     * @param bindingCondition binding condition to lookup
     * @param conditionToMatch a pre-populated variable as a control
     * @return {@code true} when the condition matches, otherwise {@code false}
     */
    protected final boolean isBindingActive(ClientLibrary lib, String bindingCondition, boolean conditionToMatch) {
        Resource libResource = resourceResolver.resolve(lib.getPath());

        if (!ResourceUtil.isNonExistingResource(libResource)) {
            ValueMap libProps = libResource.getValueMap();

            return Boolean.TRUE.equals(libProps.get(bindingCondition, Boolean.class))
                    && Boolean.TRUE.equals(conditionToMatch);
        }

        return false;
    }

    /**
     * Retrieve the ES module path based on the state of {@code useEsModuleFallback}.
     *
     * @param path {@link String} path to the {@link ClientLibrary} resource
     * @return path to the {@link ClientLibrary} resource
     */
    protected final String getEsModulePath(String path) {
        if (useEsModuleFallback) {
            return path.replaceFirst(
                    "(\\w+)(\\/\\w+" + LibraryType.JS.extension.replace(".", "\\.") + "$)",
                    "$1" + CLIENTLIB_ES_SELECTOR + "$2");
        }

        return path;
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
        String libAttribute = getLibraryTypeAttributes(libraryType);
        String tag = libraryType.equals(LibraryType.CSS) ? CLIENTLIB_TAG_STYLESHEET : CLIENTLIB_TAG_JAVASCRIPT;

        if (libraryType.equals(LibraryType.JS) && isBindingActive(lib, "esModule", useEsModule)) {
            String modulePath = getEsModulePath(path);

            out.format(CLIENTLIB_TAG_JAVASCRIPT, modulePath, libAttribute + CLIENTLIB_MODULE_TYPE_ATTRIBUTE);

            if (useEsModuleFallback) {
                libAttribute += CLIENTLIB_NOMODULE_TYPE_ATTRIBUTE;
            } else {
                return;
            }
        }

        out.format(tag, path, libAttribute);
    }

    /**
     * Retrieve any attributes required for the provided library type.
     *
     * @param libraryType {@link LibraryType} which is either CSS or JS
     * @return a string containing HTML attributes
     */
    protected String getLibraryTypeAttributes(LibraryType libraryType) {
        return StringUtils.EMPTY;
    }
}
