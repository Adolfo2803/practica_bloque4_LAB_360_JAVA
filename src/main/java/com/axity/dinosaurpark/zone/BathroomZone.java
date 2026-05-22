package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.Tourist;
import com.axity.dinosaurpark.persistence.DatabaseService;
import com.axity.dinosaurpark.persistence.RevenueRecord;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class BathroomZone implements ParkZone {

    private final int maxCapacity;
    private final int useDurationSteps;
    private final double spaPrice;
    private final double spaProbability;
    private final Queue<Integer> occupancySlots;

    public BathroomZone(int maxCapacity, int useDurationSteps, double spaPrice, double spaProbability) {
        this.maxCapacity = maxCapacity;
        this.useDurationSteps = useDurationSteps;
        this.spaPrice = spaPrice;
        this.spaProbability = spaProbability;
        this.occupancySlots = new LinkedList<>();
    }

    public boolean tryEnter(Tourist tourist, Random rng, DatabaseService db, double discount) {
        if (!hasCapacity()) {
            return false;
        }

        occupancySlots.add(useDurationSteps);
        tourist.recordVisit(getName());

        if (rng.nextDouble() < spaProbability) {
            double price = spaPrice * (1.0 - discount);
            tourist.spend(price);
            db.appendRevenue(new RevenueRecord(0, "SPA", price,
                    tourist.getId(), getName(), LocalDateTime.now()));
        }

        return true;
    }

    public void tick() {
        Queue<Integer> updated = new LinkedList<>();
        for (Integer remaining : occupancySlots) {
            int next = remaining - 1;
            if (next > 0) {
                updated.add(next);
            }
        }
        occupancySlots.clear();
        occupancySlots.addAll(updated);
    }

    @Override
    public String getName() {
        return "Banos";
    }

    @Override
    public boolean hasCapacity() {
        return occupancySlots.size() < maxCapacity;
    }

    @Override
    public int getCurrentOccupancy() {
        return occupancySlots.size();
    }

    @Override
    public int getMaxCapacity() {
        return maxCapacity;
    }

    @Override
    public void enter(Tourist tourist) {
        occupancySlots.add(useDurationSteps);
    }

    @Override
    public void exit(Tourist tourist) {
        occupancySlots.poll();
    }
}
