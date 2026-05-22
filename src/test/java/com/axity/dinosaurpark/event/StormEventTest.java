package com.axity.dinosaurpark.event;

import com.axity.dinosaurpark.model.Tourist;
import com.axity.dinosaurpark.model.TouristStatus;
import com.axity.dinosaurpark.persistence.DatabaseService;
import com.axity.dinosaurpark.simulation.ParkState;
import com.axity.dinosaurpark.zone.PowerPlant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class StormEventTest {

    private StormEvent event;
    private DatabaseService db;

    @BeforeEach
    void setUp() {
        event = new StormEvent(0.04);
        db = Mockito.mock(DatabaseService.class);
    }

    @Test
    void getName_returnsCorrectName() {
        assertEquals("TORMENTA_TORRENCIAL", event.getName());
    }

    @Test
    void getProbability_returnsConfiguredValue() {
        assertEquals(0.04, event.getProbability());
    }

    @Test
    void execute_evacuatesAllTouristsInPark() {
        Tourist t1 = new Tourist(1, "Ana");
        t1.setStatus(TouristStatus.IN_PARK);
        Tourist t2 = new Tourist(2, "Pedro");
        t2.setStatus(TouristStatus.IN_PARK);
        Tourist t3 = new Tourist(3, "Luis");
        t3.setStatus(TouristStatus.WAITING);

        List<Tourist> tourists = new ArrayList<>(List.of(t1, t2, t3));
        PowerPlant plant = new PowerPlant(100, 1.5, 0.05, 200, 500);
        ParkState state = new ParkState(tourists, List.of(), List.of(), plant, db, new Random());

        event.execute(state, new Random());

        assertTrue(t1.getVisitedZones().contains("Evacuacion"));
        assertTrue(t2.getVisitedZones().contains("Evacuacion"));
        assertFalse(t3.getVisitedZones().contains("Evacuacion"));
    }

    @Test
    void execute_recordsExpenseAndEvent() {
        PowerPlant plant = new PowerPlant(100, 1.5, 0.05, 200, 500);
        ParkState state = new ParkState(List.of(), List.of(), List.of(), plant, db, new Random());

        event.execute(state, new Random());

        verify(db).appendExpense(any());
        verify(db).appendEvent(any());
        assertEquals(500.0, state.getTotalExpenses());
    }
}
