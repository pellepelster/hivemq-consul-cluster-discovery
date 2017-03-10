/**
 *
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package io.pelle.hivemq.plugin;

import com.hivemq.spi.HiveMQPluginModule;
import com.hivemq.spi.PluginEntryPoint;
import com.hivemq.spi.plugin.meta.Information;
import com.orbitz.consul.Consul;

@Information(name = "Consul Cluster Discovery Plugin", author = "Christian Pelster", version = "SNAPSHOT")
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
