package com.axity.dinosaurpark.simulation;

import com.axity.dinosaurpark.model.Dinosaur;
import com.axity.dinosaurpark.model.Tourist;
import com.axity.dinosaurpark.model.TouristStatus;
import com.axity.dinosaurpark.model.Vehicle;
import com.axity.dinosaurpark.model.VehicleStatus;
import com.axity.dinosaurpark.persistence.DatabaseService;
import com.axity.dinosaurpark.zone.PowerPlant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParkState {

    private final List<Tourist> tourists;
    private final List<Dinosaur> dinosaurs;
    private final List<Vehicle> vehicles;
    private final PowerPlant powerPlant;
    private final DatabaseService db;
    private final Random rng;
    private final List<String> activeEventNames;
    private double totalRevenue;
    private double totalExpenses;
    private boolean dealsHourActive;
    private double currentDiscount;
    private int currentStep;

    public ParkState(List<Tourist> tourists, List<Dinosaur> dinosaurs, List<Vehicle> vehicles,
                     PowerPlant powerPlant, DatabaseService db, Random rng) {
        this.tourists = tourists;
        this.dinosaurs = dinosaurs;
        this.vehicles = vehicles;
        this.powerPlant = powerPlant;
        this.db = db;
        this.rng = rng;
        this.activeEventNames = new ArrayList<>();
        this.totalRevenue = 0.0;
        this.totalExpenses = 0.0;
        this.dealsHourActive = false;
        this.currentDiscount = 0.0;
        this.currentStep = 0;
    }

    public void clearActiveEvents() {
        activeEventNames.clear();
        dealsHourActive = false;
        currentDiscount = 0.0;
    }

    public void addActiveEvent(String eventName) {
        activeEventNames.add(eventName);
    }

    public void addRevenue(double amount) {
        totalRevenue += amount;
    }

    public void addExpense(double amount) {
        totalExpenses += amount;
    }

    public void incrementStep() {
        currentStep++;
    }

    public int countActiveTourists() {
        return (int) tourists.stream()
                .filter(t -> t.getStatus() == TouristStatus.IN_PARK)
                .count();
    }

    public int countDinosaursInEnclosure() {
        return (int) dinosaurs.stream()
                .filter(d -> d.getStatus() == com.axity.dinosaurpark.model.DinosaurStatus.IN_ENCLOSURE)
                .count();
    }

    public int countVehiclesInUse() {
        return (int) vehicles.stream()
                .filter(v -> v.getStatus() != VehicleStatus.AVAILABLE)
                .count();
    }

    public List<Tourist> getTourists() {
        return tourists;
    }

    public List<Dinosaur> getDinosaurs() {
        return dinosaurs;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public PowerPlant getPowerPlant() {
        return powerPlant;
    }

    public DatabaseService getDb() {
        return db;
    }

    public Random getRng() {
        return rng;
    }

    public List<String> getActiveEventNames() {
        return activeEventNames;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public boolean isDealsHourActive() {
        return dealsHourActive;
    }

    public void setDealsHourActive(boolean dealsHourActive) {
        this.dealsHourActive = dealsHourActive;
    }

    public double getCurrentDiscount() {
        return currentDiscount;
    }

    public void setCurrentDiscount(double currentDiscount) {
        this.currentDiscount = currentDiscount;
    }

    public int getCurrentStep() {
        return currentStep;
    }
}
