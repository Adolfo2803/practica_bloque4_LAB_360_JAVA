package com.axity.dinosaurpark.event;

import com.axity.dinosaurpark.model.Vehicle;
import com.axity.dinosaurpark.model.VehicleStatus;
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
import static org.mockito.Mockito.*;

class VehicleFailureEventTest {

    private VehicleFailureEvent event;
    private DatabaseService db;

    @BeforeEach
    void setUp() {
        event = new VehicleFailureEvent(0.06);
        db = Mockito.mock(DatabaseService.class);
    }

    @Test
    void getName_returnsCorrectName() {
        assertEquals("FALLA_VEHICULO", event.getName());
    }

    @Test
    void getProbability_returnsConfiguredValue() {
        assertEquals(0.06, event.getProbability());
    }

    @Test
    void execute_breaksAvailableVehicle() {
        Vehicle v1 = new Vehicle(1, 5);
        Vehicle v2 = new Vehicle(2, 5);
        List<Vehicle> vehicles = new ArrayList<>(List.of(v1, v2));
        PowerPlant plant = new PowerPlant(100, 1.5, 0.05, 200, 500);
        ParkState state = new ParkState(List.of(), List.of(), vehicles, plant, db, new Random());

        Random rng = Mockito.mock(Random.class);
        when(rng.nextInt(2)).thenReturn(0);

        event.execute(state, rng);

        assertEquals(VehicleStatus.BROKEN, v1.getStatus());
        verify(db).appendEvent(any());
    }

    @Test
    void execute_doesNothingWhenNoAvailableVehicles() {
        Vehicle v1 = new Vehicle(1, 5);
        v1.markBroken();
        List<Vehicle> vehicles = new ArrayList<>(List.of(v1));
        PowerPlant plant = new PowerPlant(100, 1.5, 0.05, 200, 500);
        ParkState state = new ParkState(List.of(), List.of(), vehicles, plant, db, new Random());

        event.execute(state, new Random());

        verify(db, never()).appendEvent(any());
    }
}
