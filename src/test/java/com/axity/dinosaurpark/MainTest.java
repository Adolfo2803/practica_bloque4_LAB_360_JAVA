package com.axity.dinosaurpark;

import com.axity.dinosaurpark.config.ParkConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @BeforeEach
    void setUp() {
        ParkConfig.resetForTesting();
    }

    @Test
    void main_runsSimulationWithoutErrors() {
        assertDoesNotThrow(() -> Main.main(new String[]{}));
    }
}
