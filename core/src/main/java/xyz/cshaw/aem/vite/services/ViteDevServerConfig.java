package xyz.cshaw.aem.vite.services;

public interface ViteDevServerConfig {
    boolean automaticInjection();

    String[] clientlibCategories();

    String[] contentPaths();

    String manualInjectionSelector();

    String devServerProtocol();

    String devServerHostname();

    int devServerPort();

    String devServerUrl();

    String[] devServerEntryPoints();

    boolean usingReact();
}
