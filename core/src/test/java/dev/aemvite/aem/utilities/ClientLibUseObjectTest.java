package dev.aemvite.aem.utilities;

import com.day.cq.wcm.commons.RequestHelper;
import dev.aemvite.aem.ComponentBaseTest;
import io.wcm.testing.mock.aem.junit5.AemContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ClientLibUseObject.class, RequestHelper.class})
public class ClientLibUseObjectTest extends ComponentBaseTest<ClientLibUseObject> {
    private final TestLogger logger = TestLoggerFactory.getTestLogger(ClientLibUseObject.class);

    @BeforeEach
    void setup() {
        TestLoggerFactory.clear();
    }

    @Test
    void testInit() {
        Bindings bindings = new SimpleBindings();

        component.init(bindings);

        List<LoggingEvent> events = logger.getLoggingEvents();
        Assertions.assertEquals(0, events.size());

//        LoggingEvent event = events.get(0);
//        Assertions.assertEquals(Level.DEBUG, event.getLevel());
    }

    @Test
    void testInclude() throws Exception {
        Bindings bindings = new SimpleBindings();

        PowerMockito.doCallRealMethod().when(component).init(bindings);

//        String output = PowerMockito.doCallRealMethod().when(component).include();

        List<LoggingEvent> events = logger.getLoggingEvents();
        Assertions.assertEquals(0, events.size());
    }
}
