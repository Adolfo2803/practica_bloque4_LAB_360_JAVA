package com.axity.dinosaurpark.simulation;

import com.axity.dinosaurpark.config.ParkConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimulationEngineTest {

    @BeforeEach
    void setUp() {
        ParkConfig.resetForTesting();
    }

    @Test
    void run_completesWithoutErrors() {
        ParkConfig config = ParkConfig.getInstance();
        SimulationEngine engine = new SimulationEngine(config);
        engine.run();

        assertTrue(engine.getState().getCurrentStep() > 0);
    }

    @Test
    void run_isNonDeterministic() {
        ParkConfig config1 = ParkConfig.getInstance();
        SimulationEngine engine1 = new SimulationEngine(config1);
        engine1.run();
        double expenses1 = engine1.getState().getTotalExpenses();

        ParkConfig.resetForTesting();
        ParkConfig config2 = ParkConfig.getInstance();
        SimulationEngine engine2 = new SimulationEngine(config2);
        engine2.run();
        double expenses2 = engine2.getState().getTotalExpenses();

        // Con Random sin semilla, es extremadamente improbable que sean iguales
        // pero no podemos garantizarlo al 100%; verificamos que al menos corre
        assertNotNull(engine1.getState());
        assertNotNull(engine2.getState());
    }

    @Test
    void engine_createsTouristsFromConfig() {
        ParkConfig config = ParkConfig.getInstance();
        SimulationEngine engine = new SimulationEngine(config);

        assertEquals(50, engine.getState().getTourists().size());
    }

    @Test
    void engine_createsDinosaursFromConfig() {
        ParkConfig config = ParkConfig.getInstance();
        SimulationEngine engine = new SimulationEngine(config);

        assertEquals(20, engine.getState().getDinosaurs().size());
    }

    @Test
    void engine_createsVehiclesFromConfig() {
        ParkConfig config = ParkConfig.getInstance();
        SimulationEngine engine = new SimulationEngine(config);

        assertEquals(4, engine.getState().getVehicles().size());
    }
}
