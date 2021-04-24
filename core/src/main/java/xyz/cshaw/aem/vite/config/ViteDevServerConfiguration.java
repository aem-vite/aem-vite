package xyz.cshaw.aem.vite.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

import static xyz.cshaw.aem.vite.Constants.DEFAULT_AUTOMATIC_INJECTION;
import static xyz.cshaw.aem.vite.Constants.DEFAULT_DEVSERVER_HOSTNAME;
import static xyz.cshaw.aem.vite.Constants.DEFAULT_DEVSERVER_PORT;
import static xyz.cshaw.aem.vite.Constants.DEFAULT_DEVSERVER_PROTOCOL;
import static xyz.cshaw.aem.vite.Constants.DEFAULT_MANUAL_INJECTOR_SELECTOR;
import static xyz.cshaw.aem.vite.Constants.DEFAULT_USING_REACT;

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
