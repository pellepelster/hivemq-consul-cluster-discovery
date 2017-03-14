/**
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package io.pelle.hivemq.plugin;

import com.hivemq.spi.exceptions.UnrecoverableException;
import com.orbitz.consul.Consul;
import com.orbitz.consul.ConsulException;
import io.pelle.hivemq.plugin.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;

public class ConsulClientProvider implements Provider<Consul> {

    private static final Logger log = LoggerFactory.getLogger(ConsulClientProvider.class);

    private final Configuration configuration;

    @Inject
    public ConsulClientProvider(final Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Consul get() {

        String consulUrl = configuration.getConsulUrl();

        try {
            Consul.Builder builder = Consul.builder().withUrl(consulUrl);

            if (System.getenv(Constants.CONSUL_TOKEN_ENVIRONMENT) != null) {
                builder.withAclToken(Constants.CONSUL_TOKEN_ENVIRONMENT);
                log.info("connecting to consul with url '{}' using http token", consulUrl);
            } else {
                log.info("connecting to consul with url '{}'", consulUrl);
            }

            return builder.build();

        } catch (ConsulException e) {
            log.error("unable to connect to consul", e);
            throw new UnrecoverableException();
        }
    }
}