package io.pelle.hivemq.plugin.configuration;

import com.google.inject.Inject;
import io.pelle.hivemq.plugin.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static java.lang.Math.toIntExact;

public class Configuration {

    private static final Logger log = LoggerFactory.getLogger(Configuration.class);

    private final Properties properties;

    @Inject
    public Configuration(ConfigurationReader configurationReader) {
        properties = configurationReader.getProperties();
    }

    public String getConsulHostname() {
        return properties.getProperty(Constants.CONSUL_HOSTNAME_KEY, Constants.CONSUL_HOSTNAME_DEFAULT);
    }

    public int getConsulPort() {
        return toIntExact(getLong(Constants.CONSUL_PORT_KEY, Constants.CONSUL_PORT_DEFAULT));
    }

    public long getConsulCheckTTL() {
        return getLong(Constants.CONSUL_SERVICE_CHECK_TTL_KEY, Constants.CONSUL_SERVICE_CHECK_TTL_DEFAULT);
    }

    public long getConsulUpdateInterval() {
        return getLong(Constants.CONSUL_SERVICE_UPDATE_INTERVAL_KEY, Constants.CONSUL_SERVICE_UPDATE_INTERVAL_DEFAULT);
    }

    private long getLong(String key, long defaultValue) {
        final String value = properties.getProperty(key);

        if (value == null) {
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
