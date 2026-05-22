package com.axity.dinosaurpark;

import com.axity.dinosaurpark.config.ParkConfig;
import com.axity.dinosaurpark.simulation.SimulationEngine;

public class Main {

    public static void main(String[] args) {
        ParkConfig config = ParkConfig.getInstance();
        new SimulationEngine(config).run();
    }
}
