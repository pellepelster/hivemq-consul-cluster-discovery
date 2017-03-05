/**
 *
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package io.pelle.hivemq.plugin;

public interface Constants {

    String CONSUL_SERVICE_NAME = "hivemq-service-discovery";

    String CONFIG_FILENAME = "consuldiscovery.properties";

    String CONSUL_HOSTNAME_KEY = "consul-hostname";
    String CONSUL_HOSTNAME_DEFAULT = "consul";

    String CONSUL_PORT_KEY = "consul-port";
    long CONSUL_PORT_DEFAULT = 8500;

    String CONSUL_SERVICE_CHECK_TTL_KEY = "consul-check-ttl";
    long CONSUL_SERVICE_CHECK_TTL_DEFAULT = 120;

    String CONSUL_SERVICE_UPDATE_INTERVAL_KEY = "consul-update-interval";
    long CONSUL_SERVICE_UPDATE_INTERVAL_DEFAULT = 60;

    String CONSUL_TOKEN_ENVIRONMENT = "CONSUL_HTTP_TOKEN";
}
