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
package dev.aemvite.aem.impl;

import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dev.aemvite.aem.config.ViteDevServerConfiguration;
import dev.aemvite.aem.services.ViteDevServerConfig;

import static dev.aemvite.aem.utilities.Constants.DEFAULT_AUTOMATIC_INJECTION;
import static dev.aemvite.aem.utilities.Constants.DEFAULT_DEVSERVER_DOCKER;
import static dev.aemvite.aem.utilities.Constants.DEFAULT_DEVSERVER_HOSTNAME;
import static dev.aemvite.aem.utilities.Constants.DEFAULT_DEVSERVER_PORT;
import static dev.aemvite.aem.utilities.Constants.DEFAULT_DEVSERVER_PROTOCOL;
import static dev.aemvite.aem.utilities.Constants.DEFAULT_MANUAL_INJECTOR_SELECTOR;
import static dev.aemvite.aem.utilities.Constants.DEFAULT_USING_REACT;

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
    public boolean devServerDocker() {
        return PropertiesUtil.toBoolean(configuration.devserver_docker(), DEFAULT_DEVSERVER_DOCKER);
    }

    @Override
    public int devServerPort() {
        return PropertiesUtil.toInteger(configuration.devserver_port(), DEFAULT_DEVSERVER_PORT);
    }

    @Override
    public String devServerUrl() {
        return devServerUrl(String.format("%s://%s", devServerProtocol(), devServerHostname()));
    }

    @Override
    public String devServerUrl(final String hostname) {
        return String.format("%s:%d", hostname, devServerPort());
    }

    @Override
    public String[] devServerEntryPoints() {
        return PropertiesUtil.toStringArray(configuration.devserver_entrypoints(), new String[]{});
    }

    @Override
    public boolean usingReact() {
        return PropertiesUtil.toBoolean(configuration.using_react(), DEFAULT_USING_REACT);
    }
}
