package dev.aemvite.aem.filters;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;

@ExtendWith(AemContextExtension.class)
public class ViteDevServerFilterTest {
    private final ViteDevServerFilter fixture = new ViteDevServerFilter();
    private final TestLogger logger = TestLoggerFactory.getTestLogger(fixture.getClass());

    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;
    private MockRequestPathInfo requestPathInfo;

    private void setRequestContext(AemContext context) {
        request = context.request();
        response = context.response();

        requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setResourcePath("/content/test");
        requestPathInfo.setSelectorString("selectors");
    }

    @BeforeEach
    void setup() {
        TestLoggerFactory.clear();

        request = null;
        response = null;
        requestPathInfo = null;
    }

    @Test
    void doFilterWithoutConfigurations(AemContext context) throws IOException, ServletException {
        setRequestContext(context);

        fixture.init(mock(FilterConfig.class));
        fixture.doFilter(request, response, mock(FilterChain.class));
        fixture.destroy();

        List<LoggingEvent> events = logger.getLoggingEvents();
        assertEquals(2, events.size());

        LoggingEvent event = events.get(0);

        assertEquals(Level.INFO, event.getLevel());
        assertEquals(0, event.getArguments().size());
        assertEquals("/content/test", event.getArguments().get(0));
        assertEquals("selectors", event.getArguments().get(1));
    }
}
