/**
 *
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package io.pelle.hivemq.plugin;

public interface Constants {

    String CONFIG_FILENAME = "consuldiscovery.properties";

    String CONSUL_URL_KEY = "consul-url";

    String CONSUL_URL_DEFAULT = "https://localhost:443";

    String CONSUL_SERVICE_NAME_KEY = "consul-service-name";

    String CONSUL_SERVICE_NAME_DEFAULT = "cluster-discovery-hivemq";

    String CONSUL_SERVICE_ID_FROM_NODENAME_KEY = "consul-service-id-from-nodename";

    boolean CONSUL_SERVICE_ID_FROM_NODENAME_DEFAULT = false;

    String CONSUL_SERVICE_CHECK_TTL_KEY = "consul-check-ttl";
    long CONSUL_SERVICE_CHECK_TTL_DEFAULT = 120;

    String CONSUL_SERVICE_UPDATE_INTERVAL_KEY = "consul-update-interval";
    long CONSUL_SERVICE_UPDATE_INTERVAL_DEFAULT = 60;

    String CONSUL_TOKEN_ENVIRONMENT = "CONSUL_HTTP_TOKEN";
}
