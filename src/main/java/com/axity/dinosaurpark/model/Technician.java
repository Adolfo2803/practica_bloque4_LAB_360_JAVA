package com.axity.dinosaurpark.model;

import java.util.List;

public class Technician extends Worker {

    public Technician(int id, String name, double dailySalary) {
        super(id, name, dailySalary);
    }

    @Override
    public String getRole() {
        return "TECHNICIAN";
    }

    public boolean repairIfNeeded(boolean plantOperational, List<Vehicle> vehicles) {
        if (plantOperational) {
            return false;
        }
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getStatus() == VehicleStatus.AVAILABLE) {
                vehicle.use();
                vehicle.free();
                return true;
            }
        }
        return false;
    }
}
