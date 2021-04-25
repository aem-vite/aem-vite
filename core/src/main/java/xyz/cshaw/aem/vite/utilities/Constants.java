package xyz.cshaw.aem.vite.utilities;

import java.util.regex.Pattern;

public class Constants {
    public static final boolean DEFAULT_AUTOMATIC_INJECTION = true;
    public static final String DEFAULT_MANUAL_INJECTOR_SELECTOR = "vite";
    public static final String DEFAULT_DEVSERVER_PROTOCOL = "http";
    public static final String DEFAULT_DEVSERVER_HOSTNAME = "localhost";
    public static final boolean DEFAULT_DEVSERVER_DOCKER = false;
    public static final int DEFAULT_DEVSERVER_PORT = 3000;
    public static final boolean DEFAULT_USING_REACT = false;

    public static final String BODY_END_TAG = "</body>";
    public static final Pattern BODY_END_TAG_PATTERN = Pattern.compile(BODY_END_TAG);

    public static final String CLIENT_ENTRY_POINT_SCRIPT = "<script type=\"module\" src=\"$devServer/$entryPoint\"></script>";

    public static final String CLIENT_HTML_SCRIPT = "<script type=\"module\" src=\"$devServer/@vite/client\"></script>";

    public static final String CLIENT_HTML_REACT_SCRIPT = "<script type=\"module\">\n" +
            "  import RefreshRuntime from '$devServer/@react-refresh'\n" +
            "  RefreshRuntime.injectIntoGlobalHook(window)\n" +
            "  window.$RefreshReg$ = () => {}\n" +
            "  window.$RefreshSig$ = () => (type) => type\n" +
            "  window.__vite_plugin_react_preamble_installed__ = true\n" +
            "</script>";

    public static final String CLIENTLIB_TAG_JAVASCRIPT = "<script src=\"%s\"%s></script>";
    public static final String CLIENTLIB_TAG_STYLESHEET = "<link rel=\"stylesheet\" href=\"%s\"%s>";

    public static final String CLIENTLIB_BINDINGS_CATEGORIES = "categories";
    public static final String CLIENTLIB_BINDINGS_MODE = "mode";
    public static final String CLIENTLIB_BINDINGS_MODULE = "module";
    public static final String CLIENTLIB_BINDINGS_MODULE_FALLBACK = "moduleFallback";
    public static final String CLIENTLIB_ES_SELECTOR = ".es";
    public static final String CLIENTLIB_MODULE_TYPE_ATTRIBUTE = " type=\"module\"";
    public static final String CLIENTLIB_NOMODULE_TYPE_ATTRIBUTE = " nomodule";

    public static final String DOCKER_INTERNAL_HOSTNAME = "host.docker.internal";

    public Constants() {
    }
}
