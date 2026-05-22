package com.axity.dinosaurpark.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {

    @Test
    void newVehicle_isAvailable() {
        Vehicle vehicle = new Vehicle(1, 5);
        assertEquals(VehicleStatus.AVAILABLE, vehicle.getStatus());
        assertEquals(1, vehicle.getId());
    }

    @Test
    void use_changesStatusToInUse() {
        Vehicle vehicle = new Vehicle(1, 5);
        vehicle.use();
        assertEquals(VehicleStatus.IN_USE, vehicle.getStatus());
    }

    @Test
    void free_changesStatusToAvailable() {
        Vehicle vehicle = new Vehicle(1, 5);
        vehicle.use();
        vehicle.free();
        assertEquals(VehicleStatus.AVAILABLE, vehicle.getStatus());
    }

    @Test
    void markBroken_setsStatusAndCountdown() {
        Vehicle vehicle = new Vehicle(1, 5);
        vehicle.markBroken();
        assertEquals(VehicleStatus.BROKEN, vehicle.getStatus());
        assertEquals(5, vehicle.getRepairCountdown());
    }

    @Test
    void tick_decrementsCountdownWhenBroken() {
        Vehicle vehicle = new Vehicle(1, 3);
        vehicle.markBroken();
        vehicle.tick();
        assertEquals(2, vehicle.getRepairCountdown());
        assertEquals(VehicleStatus.BROKEN, vehicle.getStatus());
    }

    @Test
    void tick_repairsVehicleWhenCountdownReachesZero() {
        Vehicle vehicle = new Vehicle(1, 2);
        vehicle.markBroken();
        vehicle.tick();
        vehicle.tick();
        assertEquals(VehicleStatus.AVAILABLE, vehicle.getStatus());
    }

    @Test
    void tick_doesNothingWhenAvailable() {
        Vehicle vehicle = new Vehicle(1, 5);
        vehicle.tick();
        assertEquals(VehicleStatus.AVAILABLE, vehicle.getStatus());
    }
}
