package com.axity.dinosaurpark.monitoring;

import com.axity.dinosaurpark.model.*;
import com.axity.dinosaurpark.persistence.DatabaseService;
import com.axity.dinosaurpark.simulation.ParkState;
import com.axity.dinosaurpark.zone.PowerPlant;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ParkMonitorTest {

    @Test
    void displaySnapshot_printsAllFiveMetrics() {
        DatabaseService db = Mockito.mock(DatabaseService.class);
        Tourist t1 = new Tourist(1, "Ana");
        t1.setStatus(TouristStatus.IN_PARK);
        Tourist t2 = new Tourist(2, "Pedro");
        t2.setStatus(TouristStatus.WAITING);

        List<Dinosaur> dinos = new ArrayList<>();
        dinos.add(new CarnivoreDinosaur(1, "Rex", "T-Rex"));

        Vehicle v1 = new Vehicle(1, 5);
        v1.markBroken();
        List<Vehicle> vehicles = new ArrayList<>(List.of(v1, new Vehicle(2, 5)));

        PowerPlant plant = new PowerPlant(100, 1.5, 0.05, 200, 500);
        ParkState state = new ParkState(List.of(t1, t2), dinos, vehicles, plant, db, new Random());
        state.addActiveEvent("ESCAPE_DINOSAURIO");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        ParkMonitor.displaySnapshot(state);

        System.setOut(System.out);
        String output = out.toString();

        assertTrue(output.contains("Turistas activos: 1"));
        assertTrue(output.contains("Dinos en recinto: 1"));
        assertTrue(output.contains("Energia:"));
        assertTrue(output.contains("ESCAPE_DINOSAURIO"));
        assertTrue(output.contains("Vehiculos no disponibles: 1"));
    }
}
