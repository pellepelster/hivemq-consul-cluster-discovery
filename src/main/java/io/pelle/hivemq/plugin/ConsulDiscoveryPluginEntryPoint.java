/**
 *
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package io.pelle.hivemq.plugin;

import com.hivemq.spi.PluginEntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class ConsulDiscoveryPluginEntryPoint extends PluginEntryPoint {

    private final ConsulDiscoveryCallback discoveryCallback;
    private final HealthCheckEndpoint healthcheckEndpoint;

    private final Logger log = LoggerFactory.getLogger(ConsulDiscoveryPluginEntryPoint.class);


    @Inject
    public ConsulDiscoveryPluginEntryPoint(final ConsulDiscoveryCallback discoveryCallback, final HealthCheckEndpoint healthcheckEndpoint) {
        this.discoveryCallback = discoveryCallback;
        this.healthcheckEndpoint = healthcheckEndpoint;
    }

    @PostConstruct
    public void postConstruct() {
        getCallbackRegistry().addCallback(discoveryCallback); getCallbackRegistry().addCallback(healthcheckEndpoint);
    }

}
