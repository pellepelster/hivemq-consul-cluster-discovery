package io.pelle.hivemq.plugin;

import com.google.common.net.HostAndPort;
import com.hivemq.spi.callback.cluster.ClusterNodeAddress;
import com.hivemq.spi.services.PluginExecutorService;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.NotRegisteredException;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.agent.Registration;
import com.orbitz.consul.model.health.*;
import io.pelle.hivemq.plugin.configuration.Configuration;
import io.pelle.hivemq.plugin.configuration.ConfigurationReader;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.net.ssl.SSLContext;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ConsulDiscoveryCallbackTest {

    @Test
    public void fullPluginLifecycleWithDefaultConfiguration() throws ExecutionException, InterruptedException {

        // dummy consul client
        Consul consul = mock(Consul.class);
        AgentClient agentClient = mock(AgentClient.class);
        when(consul.agentClient()).thenReturn(agentClient);

        // empty config
        ConfigurationReader configurationReader = mock(ConfigurationReader.class);
        when(configurationReader.getProperties()).thenReturn(new Properties());
        Configuration configuration = new Configuration(configurationReader);

        PluginExecutorService pluginExecutorService = mock(PluginExecutorService.class);

        ConsulDiscoveryCallback callback = new ConsulDiscoveryCallback(consul, configuration, pluginExecutorService);
        callback.init("clusternode1", new ClusterNodeAddress("clusternode1-hostname", 1234));

        // verify service registration
        ArgumentCaptor<Registration> argument = ArgumentCaptor.forClass(Registration.class);
        verify(agentClient).register(argument.capture());

        Registration registration = argument.getValue();
        assertEquals("cluster-discovery-hivemq", registration.getName());
        assertEquals("clusternode1-hostname", registration.getAddress().get());
        assertEquals(Integer.valueOf(1234), registration.getPort().get());
        assertEquals("cluster-discovery-hivemq-clusternode1", registration.getId());
        assertEquals(1, registration.getChecks().size());

        Registration.RegCheck regCheck = registration.getChecks().get(0);
        assertEquals("120s", regCheck.getTtl().get());

        // check if updater jobs gets scheduled
        ArgumentCaptor<Runnable> runnableArgument = ArgumentCaptor.forClass(Runnable.class);
        verify(pluginExecutorService).scheduleAtFixedRate(runnableArgument.capture(), eq(0l), eq(60l), eq(TimeUnit.SECONDS));

        // run updater job and check consul service pass call
        runnableArgument.getValue().run();
        try {
            verify(agentClient).pass(eq("cluster-discovery-hivemq-clusternode1"));
        } catch (NotRegisteredException e) {
            throw new RuntimeException(e);
        }

        HealthClient healthClient = mock(HealthClient.class);
        List<ServiceHealth> nodes = new ArrayList<>();

        Service service = ImmutableService.builder()
                .address("host1")
                .id("not important")
                .service("doesnt matter")
                .port(5678).build();

        Node node = ImmutableNode.builder().node("node1").address("address1").build();
        ServiceHealth serviceHealth = ImmutableServiceHealth.builder().service(service).node(node).build();

        nodes.add(serviceHealth);
        ConsulResponse response = new ConsulResponse(nodes, 0, true, BigInteger.ZERO);
        when(healthClient.getHealthyServiceInstances(any())).thenReturn(response);
        when(consul.healthClient()).thenReturn(healthClient);

        List<ClusterNodeAddress> addresses = callback.getNodeAddresses().get();
        assertEquals(1, addresses.size());
        assertEquals("host1", addresses.get(0).getHost());
        assertEquals(5678, addresses.get(0).getPort());

        callback.destroy();
        verify(agentClient).deregister(eq("cluster-discovery-hivemq-clusternode1"));
    }

}
