package xyz.cshaw.aem.vite.impl;

import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cshaw.aem.vite.config.ViteDevServerConfiguration;
import xyz.cshaw.aem.vite.services.ViteDevServerConfig;

import static xyz.cshaw.aem.vite.Constants.DEFAULT_AUTOMATIC_INJECTION;
import static xyz.cshaw.aem.vite.Constants.DEFAULT_DEVSERVER_HOSTNAME;
import static xyz.cshaw.aem.vite.Constants.DEFAULT_DEVSERVER_PORT;
import static xyz.cshaw.aem.vite.Constants.DEFAULT_DEVSERVER_PROTOCOL;
import static xyz.cshaw.aem.vite.Constants.DEFAULT_MANUAL_INJECTOR_SELECTOR;

@Component(
        configurationPolicy = ConfigurationPolicy.REQUIRE,
        immediate = true,
        service = ViteDevServerConfig.class
)
@Designate(factory = true, ocd = ViteDevServerConfiguration.class)
public class ViteDevServerConfigImpl implements ViteDevServerConfig {
    private static final Logger log = LoggerFactory.getLogger(ViteDevServerConfigImpl.class);

    private ViteDevServerConfiguration configuration;

    @Activate
    @Modified
    private void activate(ViteDevServerConfiguration configuration) {
        log.info("ViteDevServerConfig booted and ready!");

        this.configuration = configuration;
    }

    @Override
    public boolean automaticInjection() {
        return PropertiesUtil.toBoolean(configuration.automatic_injection(), DEFAULT_AUTOMATIC_INJECTION);
    }

    @Override
    public String[] clientlibCategories() {
        return PropertiesUtil.toStringArray(configuration.clientlib_categories(), new String[]{});
    }

    @Override
    public String[] contentPaths() {
        return PropertiesUtil.toStringArray(configuration.content_paths(), new String[]{});
    }

    @Override
    public String manualInjectionSelector() {
        return PropertiesUtil.toString(configuration.manual_injection_selector(), DEFAULT_MANUAL_INJECTOR_SELECTOR);
    }

    @Override
    public String devServerProtocol() {
        return PropertiesUtil.toString(configuration.devserver_protocol(), DEFAULT_DEVSERVER_PROTOCOL);
    }

    @Override
    public String devServerHostname() {
        return PropertiesUtil.toString(configuration.devserver_hostname(), DEFAULT_DEVSERVER_HOSTNAME);
    }

    @Override
    public int devServerPort() {
        return PropertiesUtil.toInteger(configuration.devserver_port(), DEFAULT_DEVSERVER_PORT);
    }

    @Override
    public String devServerUrl() {
        return String.format("%s://%s:%d", devServerProtocol(), devServerHostname(), devServerPort());
    }

    @Override
    public String[] devServerEntryPoints() {
        return PropertiesUtil.toStringArray(configuration.devserver_entrypoints(), new String[]{});
    }
}
