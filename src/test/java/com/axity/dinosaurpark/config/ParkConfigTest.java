package com.axity.dinosaurpark.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParkConfigTest {

    @BeforeEach
    void setUp() {
        ParkConfig.resetForTesting();
    }

    @Test
    void getInstance_returnsSameInstance() {
        ParkConfig first = ParkConfig.getInstance();
        ParkConfig second = ParkConfig.getInstance();
        assertSame(first, second);
    }

    @Test
    void getInt_returnsConfiguredValue() {
        ParkConfig config = ParkConfig.getInstance();
        assertEquals(50, config.getInt("tourists", 0));
    }

    @Test
    void getInt_returnsDefaultWhenKeyMissing() {
        ParkConfig config = ParkConfig.getInstance();
        assertEquals(99, config.getInt("nonexistent.key", 99));
    }

    @Test
    void getDouble_returnsConfiguredValue() {
        ParkConfig config = ParkConfig.getInstance();
        assertEquals(25.0, config.getDouble("arrival.ticketPrice", 0.0));
    }

    @Test
    void getDouble_returnsDefaultWhenKeyMissing() {
        ParkConfig config = ParkConfig.getInstance();
        assertEquals(7.5, config.getDouble("nonexistent.key", 7.5));
    }

    @Test
    void getString_returnsConfiguredValue() {
        ParkConfig config = ParkConfig.getInstance();
        assertEquals("./data/parkdb", config.getString("db.path", ""));
    }

    @Test
    void getString_returnsDefaultWhenKeyMissing() {
        ParkConfig config = ParkConfig.getInstance();
        assertEquals("default", config.getString("nonexistent.key", "default"));
    }

    @Test
    void getTotalSteps_returnsConfiguredValue() {
        ParkConfig config = ParkConfig.getInstance();
        assertEquals(100, config.getTotalSteps());
    }

    @Test
    void resetForTesting_createsNewInstance() {
        ParkConfig first = ParkConfig.getInstance();
        ParkConfig.resetForTesting();
        ParkConfig second = ParkConfig.getInstance();
        assertNotSame(first, second);
    }
}
