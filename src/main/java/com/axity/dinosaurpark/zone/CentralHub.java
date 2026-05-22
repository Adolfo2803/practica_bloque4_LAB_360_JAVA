package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.Tourist;
import com.axity.dinosaurpark.persistence.DatabaseService;
import com.axity.dinosaurpark.persistence.RevenueRecord;

import java.time.LocalDateTime;
import java.util.Random;

public class CentralHub implements ParkZone {

    private final double souvenirPrice;
    private final double souvenirProbability;
    private int currentOccupancy;

    public CentralHub(double souvenirPrice, double souvenirProbability) {
        this.souvenirPrice = souvenirPrice;
        this.souvenirProbability = souvenirProbability;
        this.currentOccupancy = 0;
    }

    public void visit(Tourist tourist, Random rng, DatabaseService db, double discount) {
        tourist.recordVisit(getName());
        currentOccupancy++;

        if (rng.nextDouble() < souvenirProbability) {
            double price = souvenirPrice * (1.0 - discount);
            tourist.spend(price);
            db.appendRevenue(new RevenueRecord(0, "SOUVENIR", price,
                    tourist.getId(), getName(), LocalDateTime.now()));
        }

        currentOccupancy--;
    }

    @Override
    public String getName() {
        return "Hub Central";
    }

    @Override
    public boolean hasCapacity() {
        return true;
    }

    @Override
    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    @Override
    public int getMaxCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void enter(Tourist tourist) {
        currentOccupancy++;
    }

    @Override
    public void exit(Tourist tourist) {
        currentOccupancy--;
    }
}
