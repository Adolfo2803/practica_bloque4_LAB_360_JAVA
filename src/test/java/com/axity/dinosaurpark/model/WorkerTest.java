package com.axity.dinosaurpark.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkerTest {

    @Test
    void guard_hasCorrectRole() {
        Guard guard = new Guard(1, "Luis", 150.0);
        assertEquals("GUARD", guard.getRole());
        assertEquals(1, guard.getId());
        assertEquals("Luis", guard.getName());
        assertEquals(150.0, guard.getDailySalary());
    }

    @Test
    void technician_hasCorrectRole() {
        Technician tech = new Technician(2, "Rosa", 150.0);
        assertEquals("TECHNICIAN", tech.getRole());
    }

    @Test
    void guard_recapturesEscapedDinosaurs() {
        Guard guard = new Guard(1, "Luis", 150.0);
        CarnivoreDinosaur dino1 = new CarnivoreDinosaur(1, "Rex", "T-Rex");
        HerbivoreDinosaur dino2 = new HerbivoreDinosaur(2, "Tri", "Triceratops");
        dino1.escape();

        List<Dinosaur> dinosaurs = Arrays.asList(dino1, dino2);
        guard.recaptureEscapedDinosaurs(dinosaurs);

        assertEquals(DinosaurStatus.IN_ENCLOSURE, dino1.getStatus());
        assertEquals(DinosaurStatus.IN_ENCLOSURE, dino2.getStatus());
    }

    @Test
    void technician_repairsWhenVehicleAvailable() {
        Technician tech = new Technician(1, "Rosa", 150.0);
        Vehicle vehicle = new Vehicle(1, 5);
        List<Vehicle> vehicles = List.of(vehicle);

        boolean repaired = tech.repairIfNeeded(false, vehicles);

        assertTrue(repaired);
        assertEquals(VehicleStatus.AVAILABLE, vehicle.getStatus());
    }

    @Test
    void technician_doesNotRepairWhenPlantOperational() {
        Technician tech = new Technician(1, "Rosa", 150.0);
        Vehicle vehicle = new Vehicle(1, 5);
        List<Vehicle> vehicles = List.of(vehicle);

        boolean repaired = tech.repairIfNeeded(true, vehicles);

        assertFalse(repaired);
    }

    @Test
    void technician_cannotRepairWithoutAvailableVehicle() {
        Technician tech = new Technician(1, "Rosa", 150.0);
        Vehicle vehicle = new Vehicle(1, 5);
        vehicle.markBroken();
        List<Vehicle> vehicles = List.of(vehicle);

        boolean repaired = tech.repairIfNeeded(false, vehicles);

        assertFalse(repaired);
    }
}
