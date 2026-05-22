package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.Tourist;
import com.axity.dinosaurpark.persistence.DatabaseService;
import com.axity.dinosaurpark.persistence.ExpenseRecord;

import java.time.LocalDateTime;
import java.util.Random;

public class PowerPlant implements ParkZone {

    private double energy;
    private final double consumptionPerStep;
    private final double failureProbability;
    private final double maintenanceCost;
    private final double repairCost;
    private boolean operational;

    public PowerPlant(double initialEnergy, double consumptionPerStep, double failureProbability,
                      double maintenanceCost, double repairCost) {
        this.energy = initialEnergy;
        this.consumptionPerStep = consumptionPerStep;
        this.failureProbability = failureProbability;
        this.maintenanceCost = maintenanceCost;
        this.repairCost = repairCost;
        this.operational = true;
    }

    public void tick(Random rng, DatabaseService db) {
        if (!operational) {
            return;
        }

        energy -= consumptionPerStep;
        if (energy < 0) {
            energy = 0;
        }

        db.appendExpense(new ExpenseRecord(0, "MANTENIMIENTO", maintenanceCost,
                "Mantenimiento energetico por step", LocalDateTime.now()));

        if (rng.nextDouble() < failureProbability) {
            triggerFailure(db);
        }
    }

    public void triggerFailure(DatabaseService db) {
        this.operational = false;
        db.appendExpense(new ExpenseRecord(0, "REPARACION", repairCost,
                "Falla en planta de energia", LocalDateTime.now()));
    }

    public void repair() {
        this.operational = true;
    }

    public boolean isOperational() {
        return operational;
    }

    public double getEnergy() {
        return energy;
    }

    public double getEnergyPercentage() {
        return energy;
    }

    @Override
    public String getName() {
        return "Planta de Energia";
    }

    @Override
    public boolean hasCapacity() {
        return false;
    }

    @Override
    public int getCurrentOccupancy() {
        return 0;
    }

    @Override
    public int getMaxCapacity() {
        return 0;
    }

    @Override
    public void enter(Tourist tourist) {
        // No aplica para la planta de energia
    }

    @Override
    public void exit(Tourist tourist) {
        // No aplica para la planta de energia
    }
}
