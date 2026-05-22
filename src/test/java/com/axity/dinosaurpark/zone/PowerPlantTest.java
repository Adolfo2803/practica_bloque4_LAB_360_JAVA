package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.persistence.DatabaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PowerPlantTest {

    private PowerPlant plant;
    private DatabaseService db;

    @BeforeEach
    void setUp() {
        plant = new PowerPlant(100.0, 1.5, 0.05, 200.0, 500.0);
        db = Mockito.mock(DatabaseService.class);
    }

    @Test
    void tick_consumesEnergy() {
        Random rng = Mockito.mock(Random.class);
        when(rng.nextDouble()).thenReturn(0.9);

        plant.tick(rng, db);

        assertEquals(98.5, plant.getEnergy());
    }

    @Test
    void tick_triggersFailureWhenProbabilityHits() {
        Random rng = Mockito.mock(Random.class);
        when(rng.nextDouble()).thenReturn(0.01);

        plant.tick(rng, db);

        assertFalse(plant.isOperational());
    }

    @Test
    void tick_doesNothingWhenNotOperational() {
        plant.triggerFailure(db);
        double energyBefore = plant.getEnergy();
        Random rng = Mockito.mock(Random.class);

        plant.tick(rng, db);

        assertEquals(energyBefore, plant.getEnergy());
    }

    @Test
    void repair_restoresOperational() {
        plant.triggerFailure(db);
        assertFalse(plant.isOperational());

        plant.repair();

        assertTrue(plant.isOperational());
    }

    @Test
    void triggerFailure_recordsExpense() {
        plant.triggerFailure(db);

        verify(db).appendExpense(any());
        assertFalse(plant.isOperational());
    }

    @Test
    void getName_returnsCorrectName() {
        assertEquals("Planta de Energia", plant.getName());
    }

    @Test
    void hasCapacity_alwaysFalse() {
        assertFalse(plant.hasCapacity());
    }

    @Test
    void getMaxCapacity_returnsZero() {
        assertEquals(0, plant.getMaxCapacity());
    }

    @Test
    void getCurrentOccupancy_returnsZero() {
        assertEquals(0, plant.getCurrentOccupancy());
    }

    @Test
    void getEnergyPercentage_returnsCurrentEnergy() {
        assertEquals(100.0, plant.getEnergyPercentage());
    }

    @Test
    void tick_energyDoesNotGoBelowZero() {
        PowerPlant lowPlant = new PowerPlant(1.0, 5.0, 0.0, 200, 500);
        Random rng = Mockito.mock(Random.class);
        when(rng.nextDouble()).thenReturn(0.9);

        lowPlant.tick(rng, db);

        assertEquals(0.0, lowPlant.getEnergy());
    }
}
