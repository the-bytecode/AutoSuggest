package com.techexplore.autosuggest.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * FileUtils: Utility methods for file processing.
 *
 * Created by chandrashekar.v on 9/13/2017.
 */
public final class FileUtils {

    private static final Logger LOG = LogManager.getLogger(FileUtils.class);

    /**
     * Loads resources from the given class path location.
     *
     * @param location
     * @return
     */
    public static Optional<InputStream> loadFileFromClasspath(final String location) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        try {
            Resource resource = resourceLoader.getResource("classpath:" + location);
            return Optional.of(resource.getInputStream());
        } catch (IOException ex) {
            LOG.error("Unable to load data from location:" + location + "Error Message: " + ex.getMessage());
        } catch (Exception ex) {
            LOG.error("Unexpected error while loading data from location:" + location + "Error Message: " + ex.getMessage());
        }

        return Optional.empty();

    }

}
