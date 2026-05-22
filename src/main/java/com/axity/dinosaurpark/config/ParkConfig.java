package com.axity.dinosaurpark.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ParkConfig {

    private static ParkConfig instance;
    private final Properties properties;

    private ParkConfig() {
        properties = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("park.properties")) {
            if (is == null) {
                throw new IllegalStateException("No se encontro park.properties en el classpath");
            }
            properties.load(is);
        } catch (IOException e) {
            throw new IllegalStateException("Error al cargar park.properties", e);
        }
    }

    public static ParkConfig getInstance() {
        if (instance == null) {
            instance = new ParkConfig();
        }
        return instance;
    }

    public int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Integer.parseInt(value.trim());
    }

    public double getDouble(String key, double defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Double.parseDouble(value.trim());
    }

    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getTotalSteps() {
        return getInt("simulation.totalSteps", 100);
    }

    public static void resetForTesting() {
        instance = null;
    }
}
