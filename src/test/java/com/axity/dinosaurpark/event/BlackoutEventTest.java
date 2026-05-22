package com.axity.dinosaurpark.event;

import com.axity.dinosaurpark.persistence.DatabaseService;
import com.axity.dinosaurpark.simulation.ParkState;
import com.axity.dinosaurpark.zone.PowerPlant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class BlackoutEventTest {

    private BlackoutEvent event;
    private ParkState state;
    private DatabaseService db;

    @BeforeEach
    void setUp() {
        event = new BlackoutEvent(0.03);
        db = Mockito.mock(DatabaseService.class);
        PowerPlant plant = new PowerPlant(100, 1.5, 0.05, 200, 500);
        state = new ParkState(List.of(), List.of(), List.of(), plant, db, new Random());
    }

    @Test
    void getName_returnsCorrectName() {
        assertEquals("APAGON_MASIVO", event.getName());
    }

    @Test
    void getProbability_returnsConfiguredValue() {
        assertEquals(0.03, event.getProbability());
    }

    @Test
    void execute_triggersPlantFailure() {
        event.execute(state, new Random());

        assertFalse(state.getPowerPlant().isOperational());
    }

    @Test
    void execute_recordsExpenseAndEvent() {
        event.execute(state, new Random());

        verify(db, times(2)).appendExpense(any());
        verify(db).appendEvent(any());
        assertEquals(2000.0, state.getTotalExpenses());
    }
}
