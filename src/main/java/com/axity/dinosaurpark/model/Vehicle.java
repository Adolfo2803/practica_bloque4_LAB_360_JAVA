package com.axity.dinosaurpark.model;

public class Vehicle {

    private final int id;
    private VehicleStatus status;
    private final int repairSteps;
    private int repairCountdown;

    public Vehicle(int id, int repairSteps) {
        this.id = id;
        this.repairSteps = repairSteps;
        this.status = VehicleStatus.AVAILABLE;
        this.repairCountdown = 0;
    }

    public void use() {
        this.status = VehicleStatus.IN_USE;
    }

    public void free() {
        this.status = VehicleStatus.AVAILABLE;
    }

    public void markBroken() {
        this.status = VehicleStatus.BROKEN;
        this.repairCountdown = repairSteps;
    }

    public void tick() {
        if (status == VehicleStatus.BROKEN) {
            repairCountdown--;
            if (repairCountdown <= 0) {
                this.status = VehicleStatus.AVAILABLE;
            }
        }
    }

    public int getId() {
        return id;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public int getRepairCountdown() {
        return repairCountdown;
    }
}
