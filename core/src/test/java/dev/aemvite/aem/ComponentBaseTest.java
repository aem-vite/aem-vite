package dev.aemvite.aem;

import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.powermock.api.mockito.PowerMockito;

import java.lang.reflect.ParameterizedType;

public class ComponentBaseTest<ComponentClass> {
    protected final Class componentClass =
            (Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    protected final AemContext context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    protected ComponentClass component;
    protected Resource resource;

    @BeforeEach
    public void setUp() {
        component = (ComponentClass) PowerMockito.mock(componentClass);
        resource = PowerMockito.mock(Resource.class);
    }
}
