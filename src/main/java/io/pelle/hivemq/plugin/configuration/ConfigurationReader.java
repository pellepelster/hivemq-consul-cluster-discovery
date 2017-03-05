/**
 *
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package io.pelle.hivemq.plugin.configuration;

import com.google.inject.Inject;
import com.hivemq.spi.config.SystemInformation;
import io.pelle.hivemq.plugin.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationReader {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationReader.class);

    final Properties properties = new Properties();

    private final SystemInformation systemInformation;

    @Inject
    public ConfigurationReader(SystemInformation systemInformation) {
        this.systemInformation = systemInformation;
    }

    @PostConstruct
    public void postConstruct() {
        final File configFolder = systemInformation.getConfigFolder();

        final File pluginFile = new File(configFolder, Constants.CONFIG_FILENAME);

        if (!pluginFile.canRead()) {
            log.error("could not read configuration file {}", pluginFile.getAbsolutePath());
            return;
        }

        try (InputStream is = new FileInputStream(pluginFile)) {
            log.debug("reading configuration file {}", pluginFile.getAbsolutePath());
            properties.load(is);
        } catch (IOException e) {
            log.error("an error occurred while reading the configuration file {}", pluginFile.getAbsolutePath(), e);
        }
    }

    public Properties getProperties() {
        return properties;
    }

}