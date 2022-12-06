package dev.aemvite.aem.utilities;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import dev.aemvite.aem.context.AemContextBuilder;
import dev.aemvite.aem.context.mocks.MockHtmlLibraryManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.testing.resourceresolver.MockResourceResolverFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.script.Bindings;
import java.util.Collections;
import java.util.List;

import static dev.aemvite.aem.utilities.ClientLibUseObject.AUTH_INFO;
import static dev.aemvite.aem.utilities.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private HtmlLibraryManager mockHtmlLibraryManager;
    private ResourceResolverFactory mockResolverFactory;
    private ResourceResolver mockResourceResolver;

    private final TestLogger logger = TestLoggerFactory.getTestLogger(ClientLibUseObject.class);
    private final TestLogger parentLogger = TestLoggerFactory.getTestLogger(WCMUsePojo.class);

    private static final String LIB_PATH = "/apps/aem-vite/clientlibs/test";

    @BeforeEach
    void setUp(TestInfo testInfo) throws LoginException {
        clientLibUseObject = new ClientLibUseObject();
        mockHtmlLibraryManager = spy(context.getService(HtmlLibraryManager.class));
        mockResolverFactory = spy(context.getService(ResourceResolverFactory.class));
        mockResourceResolver = spy(context.resourceResolver());

        when(mockBindings.get(CLIENTLIB_BINDINGS_CATEGORIES)).thenReturn(
                testInfo.getTags().contains("WithoutCategories") ? null : "test.clientlib");

        when(mockBindings.get(SlingBindings.REQUEST)).thenReturn(mockSlingHttpServletRequest);
        when(mockBindings.get(SlingBindings.RESOURCE)).thenReturn(mockResource);

        when(mockBindings.get(SlingBindings.SLING)).thenReturn(
                testInfo.getTags().contains("WithoutSlingScriptHelper") ? null : mockSlingScriptHelper);

        lenient().when(mockSlingScriptHelper.getService(ResourceResolverFactory.class))
                .thenReturn(mockResolverFactory);

        lenient().when(mockSlingScriptHelper.getService(HtmlLibraryManager.class))
                .thenReturn(mockHtmlLibraryManager);

        lenient().when(mockClientLibrary.getPath()).thenReturn(LIB_PATH);

        if (testInfo.getTags().contains("NeedsValidResourceResolver")) {
            when(mockSlingScriptHelper.getService(ResourceResolverFactory.class)).thenReturn(mockResolverFactory);
            when(mockResolverFactory.getServiceResourceResolver(AUTH_INFO)).thenReturn(mockResourceResolver);
        }
    }

    @AfterEach
    void tearDown() {
        logger.clearAll();
        parentLogger.clearAll();

        reset(mockClientLibrary);
    }

    @Test
    @DisplayName("error logged when no valid categories are provided")
    @Tags({@Tag("WithoutCategories")})
    void testInvalidCategoriesLogsError() {
        clientLibUseObject.init(mockBindings);

        assertEquals(StringUtils.EMPTY, clientLibUseObject.include());

        List<LoggingEvent> events = logger.getLoggingEvents();

        assertEquals(1, events.size());

        assertEquals(Level.ERROR, events.get(0).getLevel());
        assertEquals("'categories' option might be missing from the invocation of the "
                + "/apps/granite/sightly/templates/clientlib.html client libraries template library. "
                + "Please provide a CSV list or an array of categories to include.", events.get(0).getMessage());
    }

    @Test
    @DisplayName("exception is thrown when SlingScriptHelper is null")
    @Tags({@Tag("WithoutSlingScriptHelper")})
    void testInvalidSlingBindingThrowsException() {
        clientLibUseObject.init(mockBindings);

        List<LoggingEvent> events = parentLogger.getLoggingEvents();

        assertEquals(1, events.size());

        assertEquals(Level.ERROR, events.get(0).getLevel());
        assertEquals("Failed to activate Use class", events.get(0).getMessage());
        assertEquals("Unable to get an instance of SlingScriptHelper. This is required to get an instance of HtmlLibraryManager.",
                events.get(0).getThrowable().get().getMessage());
    }

    @Test
    @DisplayName("warning logged when no resource resolver factory service is available")
    void testInvalidResourceResolverFactoryLogsWarning() {
        when(mockSlingScriptHelper.getService(ResourceResolverFactory.class)).thenReturn(null);

        clientLibUseObject.init(mockBindings);

        assertEquals(StringUtils.EMPTY,
                clientLibUseObject.getLibraryTypeAttributes(mockClientLibrary, LibraryType.JS).toString());

        List<LoggingEvent> events = logger.getLoggingEvents();

        assertEquals(2, events.size());

        assertEquals(Level.WARN, events.get(0).getLevel());
        assertEquals("No valid ResourceResolverFactory service could be retrieved", events.get(0).getMessage());

        assertEquals(Level.WARN, events.get(1).getLevel());
        assertEquals("No valid ResourceResolverFactory service could be retrieved", events.get(0).getMessage());
    }

    @Test
    @DisplayName("error logged when no authenticated resource resolver is available")
    void testInvalidResourceResolverLogsError() throws LoginException {
        when(mockSlingScriptHelper.getService(ResourceResolverFactory.class)).thenReturn(mockResolverFactory);
        when(mockResolverFactory.getServiceResourceResolver(AUTH_INFO)).thenThrow(LoginException.class);

        clientLibUseObject.init(mockBindings);

        assertEquals(StringUtils.EMPTY,
                clientLibUseObject.getLibraryTypeAttributes(mockClientLibrary, LibraryType.JS).toString());

        List<LoggingEvent> events = logger.getLoggingEvents();

        assertEquals(4, events.size());

        assertEquals(Level.ERROR, events.get(0).getLevel());
        assertEquals("Unable to retrieve resource resolver for 'aem-vite-clientlibs'.", events.get(0).getMessage());
    }

    @Test
    @DisplayName("clientlib includes are empty when 'allowProxy' is false")
    void testEmptyIncludesWhenClientLibProxyIsFalse() {
        doReturn(Collections.singletonList(mockClientLibrary)).when(mockHtmlLibraryManager)
                .getLibraries(new String[]{"test.clientlib"}, LibraryType.JS, false, true);

        when(mockClientLibrary.allowProxy()).thenReturn(false);
        when(mockClientLibrary.getIncludePath(LibraryType.JS, false)).thenReturn(LIB_PATH + LibraryType.JS.extension);
        when(mockClientLibrary.getPath()).thenReturn(LIB_PATH);

        when(mockSlingHttpServletRequest.getResourceResolver()).thenReturn(mockResourceResolver);
        when(mockResourceResolver.getResource(LIB_PATH)).thenReturn(null);

        when(mockBindings.get("mode")).thenReturn("js");

        clientLibUseObject.init(mockBindings);

        assertEquals(StringUtils.EMPTY, clientLibUseObject.include());
    }

    @Test
    @DisplayName("should return an empty attributes string for JS")
    void testEmptyJsAttributes() {
        clientLibUseObject.init(mockBindings);

        assertEquals(StringUtils.EMPTY,
                clientLibUseObject.getLibraryTypeAttributes(mockClientLibrary, LibraryType.JS).toString());
    }

    @Test
    @DisplayName("should return an empty attributes string for CSS")
    void testEmptyCssAttributes() {
        clientLibUseObject.init(mockBindings);

        assertEquals(StringUtils.EMPTY,
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

        assertEquals(
                "<script src=\"/etc.clientlibs/aem-vite/clientlibs/test.js\"></script>\n",
                clientLibUseObject.include());
    }

    @Test
    @DisplayName("should return a 'module' attribute")
    @Tags({@Tag("NeedsValidResourceResolver")})
    void testJsModuleAttribute() {
        context.create().resource(LIB_PATH, CLIENTLIB_PROPERTY_ESMODULE, true);

        clientLibUseObject.init(mockBindings);

        assertEquals(CLIENTLIB_MODULE_TYPE_ATTRIBUTE,
                clientLibUseObject.getLibraryTypeAttributes(mockClientLibrary, LibraryType.JS).toString());
    }

    @Test
    @DisplayName("should return a 'nomodule' attribute")
    @Tags({@Tag("NeedsValidResourceResolver")})
    void testJsNoModuleAttribute() throws PersistenceException {
        Resource clientLibResource = mockResourceResolver.resolve(LIB_PATH);

        // NOTE: Temporary workaround to ensure the resource is re-created correctly
        if (!ResourceUtil.isNonExistingResource(clientLibResource)) {
            mockResourceResolver.delete(clientLibResource);
        }

        context.create().resource(LIB_PATH, CLIENTLIB_PROPERTY_NOMODULE, true);

        clientLibUseObject.init(mockBindings);

        assertEquals(CLIENTLIB_NOMODULE_TYPE_ATTRIBUTE,
                clientLibUseObject.getLibraryTypeAttributes(mockClientLibrary, LibraryType.JS).toString());
    }
}
