package com.axity.dinosaurpark.simulation;

import com.axity.dinosaurpark.model.*;
import com.axity.dinosaurpark.persistence.DatabaseService;
import com.axity.dinosaurpark.zone.PowerPlant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ParkStateTest {

    private ParkState state;

    @BeforeEach
    void setUp() {
        DatabaseService db = Mockito.mock(DatabaseService.class);
        Tourist t1 = new Tourist(1, "Ana");
        t1.setStatus(TouristStatus.IN_PARK);
        Tourist t2 = new Tourist(2, "Pedro");
        t2.setStatus(TouristStatus.WAITING);

        List<Dinosaur> dinos = new ArrayList<>();
        dinos.add(new CarnivoreDinosaur(1, "Rex", "T-Rex"));
        CarnivoreDinosaur escaped = new CarnivoreDinosaur(2, "Blue", "Velociraptor");
        escaped.escape();
        dinos.add(escaped);

        Vehicle v1 = new Vehicle(1, 5);
        v1.markBroken();
        List<Vehicle> vehicles = new ArrayList<>(List.of(v1, new Vehicle(2, 5)));

        PowerPlant plant = new PowerPlant(100, 1.5, 0.05, 200, 500);
        state = new ParkState(List.of(t1, t2), dinos, vehicles, plant, db, new Random());
    }

    @Test
    void countActiveTourists_countsOnlyInPark() {
        assertEquals(1, state.countActiveTourists());
    }

    @Test
    void countDinosaursInEnclosure_countsOnlyInEnclosure() {
        assertEquals(1, state.countDinosaursInEnclosure());
    }

    @Test
    void countVehiclesInUse_countsNonAvailable() {
        assertEquals(1, state.countVehiclesInUse());
    }

    @Test
    void addActiveEvent_andClear() {
        state.addActiveEvent("APAGON_MASIVO");
        state.addActiveEvent("TORMENTA");
        assertEquals(2, state.getActiveEventNames().size());

        state.clearActiveEvents();
        assertTrue(state.getActiveEventNames().isEmpty());
        assertEquals(0.0, state.getCurrentDiscount());
        assertFalse(state.isDealsHourActive());
    }

    @Test
    void incrementStep_advancesStep() {
        assertEquals(0, state.getCurrentStep());
        state.incrementStep();
        assertEquals(1, state.getCurrentStep());
    }

    @Test
    void addRevenue_andAddExpense() {
        state.addRevenue(100.0);
        state.addRevenue(50.0);
        assertEquals(150.0, state.getTotalRevenue());

        state.addExpense(30.0);
        assertEquals(30.0, state.getTotalExpenses());
    }

    @Test
    void dealsHour_setsDiscountCorrectly() {
        state.setDealsHourActive(true);
        state.setCurrentDiscount(0.30);
        assertTrue(state.isDealsHourActive());
        assertEquals(0.30, state.getCurrentDiscount());
    }
}
