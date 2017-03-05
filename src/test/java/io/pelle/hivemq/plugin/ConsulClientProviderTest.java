package io.pelle.hivemq.plugin;

import com.hivemq.spi.exceptions.UnrecoverableException;
import io.pelle.hivemq.plugin.configuration.Configuration;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConsulClientProviderTest {

    @Test(expected = UnrecoverableException.class)
    public void shutdownHiveMQIfConsulNotAvailable() {

        Configuration configuration = mock(Configuration.class);
        when(configuration.getConsulHostname()).thenReturn("localhost");
        when(configuration.getConsulPort()).thenReturn(8500);

        new ConsulClientProvider(configuration).get();

    }
}
