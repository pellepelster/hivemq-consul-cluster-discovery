package io.pelle.hivemq.plugin;

import com.hivemq.spi.HiveMQPluginModule;
import com.hivemq.spi.PluginEntryPoint;
import com.hivemq.spi.plugin.meta.Information;
import com.orbitz.consul.Consul;

@Information(name = "Consul Cluster Discovery Plugin", author = "Christian Pelster", version = "0.0.1")
public class ConsulDiscoveryPluginModule extends HiveMQPluginModule {

    @Override
    protected void configurePlugin() {
        bind(Consul.class).toProvider(ConsulClientProvider.class);
    }

    @Override
    protected Class<? extends PluginEntryPoint> entryPointClass() {
        return ConsulDiscoveryPluginEntryPoint.class;
    }
}
