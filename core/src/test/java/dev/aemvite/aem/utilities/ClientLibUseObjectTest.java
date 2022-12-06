package dev.aemvite.aem.utilities;

import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import dev.aemvite.aem.context.AemContextBuilder;
import dev.aemvite.aem.context.mocks.MockHtmlLibraryManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.testing.resourceresolver.MockResourceResolverFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import javax.script.Bindings;

import static dev.aemvite.aem.utilities.ClientLibUseObject.AUTH_INFO;
import static dev.aemvite.aem.utilities.Constants.CLIENTLIB_BINDINGS_CATEGORIES;
import static dev.aemvite.aem.utilities.Constants.CLIENTLIB_BINDINGS_ESMODULE;
import static dev.aemvite.aem.utilities.Constants.CLIENTLIB_BINDINGS_MODE;
import static dev.aemvite.aem.utilities.Constants.CLIENTLIB_MODULE_TYPE_ATTRIBUTE;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ClientLibUseObjectTest {
    private static final AemContext context = new AemContextBuilder()
            .registerService(ResourceResolverFactory.class, new MockResourceResolverFactory())
            .registerService(HtmlLibraryManager.class, new MockHtmlLibraryManager())
            .build();

    @Mock(lenient = true)
    private Bindings mockBindings;
    @Mock
    private ClientLibrary mockClientLibrary;
    @Mock
    private Resource mockResource;
    @Mock
    private SlingScriptHelper mockSlingScriptHelper;
    @Mock
    private SlingHttpServletRequest mockSlingHttpServletRequest;

    private ClientLibUseObject clientLibUseObject;
    private ResourceResolverFactory mockResolverFactory;
    private ResourceResolver mockResourceResolver;

    private static final String LIB_PATH = "/apps/aem-vite/clientlibs/test";

    @BeforeEach
    void setUp() {
        clientLibUseObject = new ClientLibUseObject();
        mockResolverFactory = context.getService(ResourceResolverFactory.class);
        mockResourceResolver = context.resourceResolver();

        reset(mockClientLibrary);

        when(mockBindings.get(CLIENTLIB_BINDINGS_CATEGORIES)).thenReturn("test.clientlib");

        when(mockBindings.get(SlingBindings.LOG)).thenReturn(mock(Logger.class));
        when(mockBindings.get(SlingBindings.REQUEST)).thenReturn(mockSlingHttpServletRequest);
        when(mockBindings.get(SlingBindings.RESOURCE)).thenReturn(mockResource);
        when(mockBindings.get(SlingBindings.SLING)).thenReturn(mockSlingScriptHelper);

        lenient().when(mockSlingScriptHelper.getService(ResourceResolverFactory.class))
                .thenReturn(mockResolverFactory);

        lenient().when(mockSlingScriptHelper.getService(HtmlLibraryManager.class))
                .thenReturn(context.getService(HtmlLibraryManager.class));

        lenient().when(mockClientLibrary.getPath()).thenReturn(LIB_PATH);
    }

    @Test
    @DisplayName("should return an empty attributes string for JS")
    void testEmptyJsAttributes() {
        clientLibUseObject.init(mockBindings);

        Assertions.assertEquals(StringUtils.EMPTY,
                clientLibUseObject.getLibraryTypeAttributes(mockClientLibrary, LibraryType.JS).toString());
    }

    @Test
    @DisplayName("should return an empty attributes string for CSS")
    void testEmptyCssAttributes() {
        clientLibUseObject.init(mockBindings);

        Assertions.assertEquals(StringUtils.EMPTY,
                clientLibUseObject.getLibraryTypeAttributes(mockClientLibrary, LibraryType.CSS).toString());
    }

    @Test
    @DisplayName("at least one clientlib category exists")
    void testHasOneClientLibCategory() {
        clientLibUseObject.init(mockBindings);

        Assertions.assertArrayEquals(new String[]{"test.clientlib"}, clientLibUseObject.getCategories());
    }

    @Test
    @DisplayName("can generate clientlib includes")
    void testCanGenerateIncludes() {
        when(mockBindings.get("prefetch")).thenReturn(false);
        when(mockBindings.get("mode")).thenReturn("js");

        clientLibUseObject.init(mockBindings);

        Assertions.assertEquals(
                "<script src=\"/etc.clientlibs/aem-vite/clientlibs/test.js\"></script>\n",
                clientLibUseObject.include());
    }

    @Test
    @DisplayName("should return a 'module' attribute")
    void testJsModuleAttribute() throws LoginException {
        ResourceResolverFactory mockFactory = spy(context.getService(ResourceResolverFactory.class));

        when(mockSlingScriptHelper.getService(ResourceResolverFactory.class)).thenReturn(mockFactory);
        when(mockFactory.getServiceResourceResolver(AUTH_INFO)).thenReturn(mockResourceResolver);

        context.create().resource(LIB_PATH, "esModule", true);

        clientLibUseObject.init(mockBindings);

        Assertions.assertEquals(CLIENTLIB_MODULE_TYPE_ATTRIBUTE,
                clientLibUseObject.getLibraryTypeAttributes(mockClientLibrary, LibraryType.JS).toString());
    }
}
