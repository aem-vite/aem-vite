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
package dev.aemvite.aem.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

import static dev.aemvite.aem.utilities.Constants.DEFAULT_AUTOMATIC_INJECTION;
import static dev.aemvite.aem.utilities.Constants.DEFAULT_DEVSERVER_DOCKER;
import static dev.aemvite.aem.utilities.Constants.DEFAULT_DEVSERVER_HOSTNAME;
import static dev.aemvite.aem.utilities.Constants.DEFAULT_DEVSERVER_PORT;
import static dev.aemvite.aem.utilities.Constants.DEFAULT_DEVSERVER_PROTOCOL;
import static dev.aemvite.aem.utilities.Constants.DEFAULT_MANUAL_INJECTOR_SELECTOR;
import static dev.aemvite.aem.utilities.Constants.DEFAULT_USING_REACT;

@ObjectClassDefinition(
        name = "Vite DevServer",
        description = "Control how the Vite DevServer behaves."
)
public @interface ViteDevServerConfiguration {
    @AttributeDefinition(
            description = "Use automatic injection which detects Vite's DevServer and strips away any defined ClientLibs along with injecting Vite's client module. (default: true)",
            name = "Automatic injection?",
            type = AttributeType.BOOLEAN
    )
    boolean automatic_injection() default DEFAULT_AUTOMATIC_INJECTION;

    @AttributeDefinition(
            description = "Define a list of ClientLib categories that will be used to strip certain CSS/JS from the page.",
            name = "ClientLib categories"
    )
    String[] clientlib_categories() default {};

    @AttributeDefinition(
            description = "Define a list of content paths where injection should occur.",
            name = "Content paths"
    )
    String[] content_paths() default {};

    @AttributeDefinition(
            description = "Define a custom selector to use when automatic injection is disabled. (default: vite)",
            name = "Manual injection selector"
    )
    String manual_injection_selector() default DEFAULT_MANUAL_INJECTOR_SELECTOR;

    @AttributeDefinition(
            description = "Which protocol should be used to contact Vite's DevServer? (default: http)",
            name = "DevServer protocol",
            options = {
                    @Option(label = "HTTP", value = "http"),
                    @Option(label = "HTTPS", value = "https"),
            }
    )
    String devserver_protocol() default DEFAULT_DEVSERVER_PROTOCOL;

    @AttributeDefinition(
            description = "Which hostname should be used to contact Vite's DevServer? (default: localhost)",
            name = "DevServer hostname"
    )
    String devserver_hostname() default DEFAULT_DEVSERVER_HOSTNAME;

    @AttributeDefinition(
            description = "Check this option if your AEM instance is running within Docker. This will ensure that the DevServer check works correctly via http://host.docker.internal.",
            name = "DevServer Docker"
    )
    boolean devserver_docker() default DEFAULT_DEVSERVER_DOCKER;

    @AttributeDefinition(
            description = "Which port should be used to contact Vite's DevServer? (default: 3000)",
            name = "DevServer port",
            type = AttributeType.INTEGER
    )
    int devserver_port() default DEFAULT_DEVSERVER_PORT;

    @AttributeDefinition(
            description = "Define a list of entry points to inject onto the page.",
            name = "DevServer entrypoints"
    )
    String[] devserver_entrypoints() default {};

    @AttributeDefinition(
            description = "Ensure this option is enabled when using React to ensure things work correctly.",
            name = "Using React?"
    )
    boolean using_react() default DEFAULT_USING_REACT;
}
