/**
 *
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package io.pelle.hivemq.plugin;

public interface Constants {

    String CONFIG_FILENAME = "consuldiscovery.properties";

    String CONSUL_TOKEN_ENVIRONMENT = "CONSUL_HTTP_TOKEN";

    String ENVIRONMENT_VARIABLE_PREFIX = "CLUSTER_DISCOVERY_";

    String CONSUL_URL_KEY = "consul-url";
    String CONSUL_URL_DEFAULT = "https://consul:443";

    String HIVEMQ_NODE_ADDRESS_KEY = "hivemq-node-address";
    String HIVEMQ_NODE_ADDRESS_DEFAULT = "";

    String CONSUL_SERVICE_NAME_KEY = "consul-service-name";
    String CONSUL_SERVICE_NAME_DEFAULT = "cluster-discovery-hivemq";

    String CONSUL_SERVICE_CHECK_TTL_KEY = "consul-check-ttl";
    long CONSUL_SERVICE_CHECK_TTL_DEFAULT = 120;

    String CONSUL_SERVICE_UPDATE_INTERVAL_KEY = "consul-update-interval";
    long CONSUL_SERVICE_UPDATE_INTERVAL_DEFAULT = 60;

}
