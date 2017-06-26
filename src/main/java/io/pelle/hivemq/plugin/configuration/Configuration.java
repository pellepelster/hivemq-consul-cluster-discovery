/**
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package io.pelle.hivemq.plugin.configuration;

import com.google.inject.Inject;
import io.pelle.hivemq.plugin.Constants;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class Configuration {

    private static final Logger log = LoggerFactory.getLogger(Configuration.class);

    private final Properties properties;
    private final String consulServiceId = Integer.toString(RandomUtils.nextInt(1000,Integer.MAX_VALUE));

    @Inject
    public Configuration(ConfigurationReader configurationReader) {
        properties = configurationReader.getProperties();
    }

    public String getConsulUrl() {
        return getPropertyWithOverride(Constants.CONSUL_URL_KEY, Constants.CONSUL_URL_DEFAULT);
    }

    public String getConsulServiceName() {
        return getPropertyWithOverride(Constants.CONSUL_SERVICE_NAME_KEY, Constants.CONSUL_SERVICE_NAME_DEFAULT);
    }

    public String getConsulServiceId() {
        return consulServiceId;
    }

    public String getNodeAddress() {
        return getPropertyWithOverride(Constants.HIVEMQ_NODE_ADDRESS_KEY, Constants.HIVEMQ_NODE_ADDRESS_DEFAULT);
    }

    public long getConsulCheckTTL() {
        return getLong(Constants.CONSUL_SERVICE_CHECK_TTL_KEY, Constants.CONSUL_SERVICE_CHECK_TTL_DEFAULT);
    }

    public long getConsulUpdateInterval() {
        return getLong(Constants.CONSUL_SERVICE_UPDATE_INTERVAL_KEY, Constants.CONSUL_SERVICE_UPDATE_INTERVAL_DEFAULT);
    }

    public long getConsulRegisterInterval() {
        return getLong(Constants.CONSUL_SERVICE_REGISTER_INTERVAL_KEY, Constants.CONSUL_SERVICE_REGISTER_INTERVAL_DEFAULT);
    }

    private String getPropertyWithOverride(String key, String defaultValue) {
        String envName = getEnvironmentVariableNameForKey(key);
        if (System.getenv(envName) != null) {
            return System.getenv(envName);
        } else {
            return this.properties.getProperty(key, defaultValue);
        }
    }

    private String getEnvironmentVariableNameForKey(String key) {
        return Constants.ENVIRONMENT_VARIABLE_PREFIX + key.toUpperCase().replaceAll("-", "_");
    }

    private long getLong(String key, long defaultValue) {
        final String value = getPropertyWithOverride(key, Long.toString(defaultValue));

        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            log.error(String.format("could not parse value '%s' for configuration key '%s'", value, key));
            return defaultValue;
        }
    }

}
