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
import static org.mockito.Mockito.verify;

class DealsHourEventTest {

    private DealsHourEvent event;
    private ParkState state;
    private DatabaseService db;

    @BeforeEach
    void setUp() {
        event = new DealsHourEvent(0.08);
        db = Mockito.mock(DatabaseService.class);
        PowerPlant plant = new PowerPlant(100, 1.5, 0.05, 200, 500);
        state = new ParkState(List.of(), List.of(), List.of(), plant, db, new Random());
    }

    @Test
    void getName_returnsCorrectName() {
        assertEquals("HORA_DE_OFERTAS", event.getName());
    }

    @Test
    void getProbability_returnsConfiguredValue() {
        assertEquals(0.08, event.getProbability());
    }

    @Test
    void execute_activatesDealsAndDiscount() {
        event.execute(state, new Random());

        assertTrue(state.isDealsHourActive());
        assertEquals(0.30, state.getCurrentDiscount());
    }

    @Test
    void execute_recordsEvent() {
        event.execute(state, new Random());

        verify(db).appendEvent(any());
    }
}
