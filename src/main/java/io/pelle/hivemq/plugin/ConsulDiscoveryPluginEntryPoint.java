package io.pelle.hivemq.plugin;

import com.hivemq.spi.PluginEntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class ConsulDiscoveryPluginEntryPoint extends PluginEntryPoint {

    private final ConsulDiscoveryCallback discoveryCallback;

    Logger log = LoggerFactory.getLogger(ConsulDiscoveryPluginEntryPoint.class);

    @Inject
    public ConsulDiscoveryPluginEntryPoint(final ConsulDiscoveryCallback discoveryCallback) {
        this.discoveryCallback = discoveryCallback;
    }

    @PostConstruct
    public void postConstruct() {
        getCallbackRegistry().addCallback(discoveryCallback);
    }

}
