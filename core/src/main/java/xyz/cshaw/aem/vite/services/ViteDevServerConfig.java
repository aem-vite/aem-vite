package xyz.cshaw.aem.vite.services;

public interface ViteDevServerConfig {
    boolean automaticInjection();

    String[] clientlibCategories();

    String[] contentPaths();

    String manualInjectionSelector();

    String devServerProtocol();

    String devServerHostname();

    boolean devServerDocker();

    int devServerPort();

    String devServerUrl();

    String devServerUrl(String hostname);

    String[] devServerEntryPoints();

    boolean usingReact();
}
