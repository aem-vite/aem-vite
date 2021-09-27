package dev.aemvite.aem.context;

import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.sling.testing.mock.sling.ResourceResolverType;

import java.util.HashMap;
import java.util.Map;

public class AemContextBuilder {
  private final AemContext aemContext;

  public AemContextBuilder() {
    this.aemContext = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);
  }

  public AemContextBuilder(final ResourceResolverType resourceResolverType) {
    this.aemContext = new AemContext(resourceResolverType);
  }

  public AemContext build() {
    return this.aemContext;
  }

  public AemContextBuilder loadResource(final String classPathResource, final String destinationPath) {
    this.aemContext.load().json(classPathResource, destinationPath);
    return this;
  }

  public <T> AemContextBuilder registerService(final Class<T> serviceClass, final T service) {
    this.aemContext.registerService(serviceClass, service, new HashMap<>());
    return this;
  }

  public <T> AemContextBuilder registerInjectActivateService(final T osgiService) {
    this.aemContext.registerInjectActivateService(osgiService, new HashMap<>());
    return this;
  }

  public <T> AemContextBuilder registerInjectActivateService(final T osgiService, final Map<String, Object> config) {
    this.aemContext.registerInjectActivateService(osgiService, config);
    return this;
  }
}
