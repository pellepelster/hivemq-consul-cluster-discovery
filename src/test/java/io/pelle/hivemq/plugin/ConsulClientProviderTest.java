package io.pelle.hivemq.plugin;

import com.hivemq.spi.exceptions.UnrecoverableException;
import io.pelle.hivemq.plugin.configuration.Configuration;
import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConsulClientProviderTest {

    @Test(expected = UnrecoverableException.class)
    public void shutdownHiveMQIfConsulNotAvailable() {

        Configuration configuration = mock(Configuration.class);
        when(configuration.getConsulUrl()).thenReturn("https://localhost:443");

        new ConsulClientProvider(configuration).get();
    }

}
