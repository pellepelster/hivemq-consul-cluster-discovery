/**
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package io.pelle.hivemq.plugin;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.hivemq.spi.callback.cluster.ClusterDiscoveryCallback;
import com.hivemq.spi.callback.cluster.ClusterNodeAddress;
import com.hivemq.spi.services.PluginExecutorService;
import com.orbitz.consul.Consul;
import com.orbitz.consul.NotRegisteredException;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import com.orbitz.consul.model.health.ServiceHealth;
import com.orbitz.consul.option.ImmutableQueryOptions;
import io.pelle.hivemq.plugin.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ConsulDiscoveryCallback implements ClusterDiscoveryCallback {

    private static final Logger log = LoggerFactory.getLogger(ConsulDiscoveryCallback.class);

    private final Configuration configuration;
    private final PluginExecutorService pluginExecutorService;
    private final Consul consul;

    private String clusterId;
    private Optional<String> nodeAddress;

    @Inject
    public ConsulDiscoveryCallback(final Consul consul,
                                   final Configuration configuration,
                                   final PluginExecutorService pluginExecutorService) {
        this.consul = consul;
        this.configuration = configuration;
        this.pluginExecutorService = pluginExecutorService;
    }

    private String getServiceId() {
        return String.format("%s", configuration.getConsulServiceId());
    }

    private String getServiceName() {
        return String.format("%s", configuration.getConsulServiceName());
    }

    @Override
    public void init(final String clusterId, final ClusterNodeAddress ownAddress) {

        log.debug(String.format("consul discovery plugin initialized, HiveMQ cluster node address is %s:%s", ownAddress.getHost(), ownAddress.getPort()));
        this.clusterId = clusterId;

        pluginExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    tryRegisterConsulService(ownAddress);
                } catch (Exception e) {
                    log.error("error registering service", e);
                }
            }
        }, 0, configuration.getConsulRegisterInterval(), TimeUnit.SECONDS);

        pluginExecutorService.scheduleAtFixedRate(() -> {
            try {
                consul.agentClient().pass(getServiceId());
            } catch (NotRegisteredException e) {
                log.error("error updating ttl, service '{}' is not registered", getServiceId());
            }
        }, 10, configuration.getConsulUpdateInterval(), TimeUnit.SECONDS);

    }

    private void tryRegisterConsulService(ClusterNodeAddress ownAddress) {

        if (!consul.agentClient().isRegistered(configuration.getConsulServiceId())) {
            log.debug("service {} not registered, will try to do so", configuration.getConsulServiceName());

            Registration.RegCheck ttlCheck = Registration.RegCheck.ttl(configuration.getConsulCheckTTL());
            Registration serviceRegistration = ImmutableRegistration.builder()
                    .name(getServiceName())
                    .address(getNodeAddress(ownAddress))
                    .port(ownAddress.getPort())
                    .id(getServiceId())
                    .addChecks(ttlCheck).build();
            consul.agentClient().register(serviceRegistration, getQueryOptions());

            log.info("registered service {}, registration status now is {}", configuration.getConsulServiceName(), consul.agentClient().isRegistered(configuration.getConsulServiceId()));
        } else {
            log.debug("service {} already registered", configuration.getConsulServiceName());
        }
    }

    @Override
    public ListenableFuture<List<ClusterNodeAddress>> getNodeAddresses() {

        final List<ClusterNodeAddress> addresses = new ArrayList<>();

        List<ServiceHealth> nodes = consul.healthClient()
                .getHealthyServiceInstances(configuration.getConsulServiceName(), getQueryOptions())
                .getResponse();

        for (ServiceHealth node : nodes) {
            addresses.add(new ClusterNodeAddress(node.getService().getAddress(), node.getService().getPort()));
        }

        log.debug(String.format("consul returned %s cluster nodes (%s)",
                addresses.size(),
                addresses.stream().map(a -> a.getHost() + ":" + a.getPort())
                        .collect(Collectors.joining(", "))));
        return Futures.immediateFuture(addresses);
    }

    @Override
    public void destroy() {
        consul.agentClient().deregister(getServiceId());
    }

    private ImmutableQueryOptions getQueryOptions() {

        ImmutableQueryOptions.Builder queryOptions = ImmutableQueryOptions.builder();

        if (System.getenv(Constants.CONSUL_TOKEN_ENVIRONMENT) != null) {
            queryOptions.token(System.getenv(Constants.CONSUL_TOKEN_ENVIRONMENT));
            log.debug("using token for query options");
        }

        return queryOptions.build();
    }

    private String getNodeAddress(ClusterNodeAddress ownAddress) {
        if (StringUtils.isNoneEmpty(configuration.getNodeAddress())) {
            return configuration.getNodeAddress();
        } else {
            return ownAddress.getHost();
        }
    }
}
