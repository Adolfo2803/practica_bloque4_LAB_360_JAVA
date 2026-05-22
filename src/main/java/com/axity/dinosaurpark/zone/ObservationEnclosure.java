package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.Tourist;
import com.axity.dinosaurpark.persistence.DatabaseService;
import com.axity.dinosaurpark.persistence.RevenueRecord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ObservationEnclosure implements ParkZone {

    private final String name;
    private final ExperienceType experienceType;
    private final int maxVisitors;
    private final double entryFee;
    private final List<Tourist> visitors;

    public ObservationEnclosure(String name, ExperienceType experienceType, int maxVisitors, double entryFee) {
        this.name = name;
        this.experienceType = experienceType;
        this.maxVisitors = maxVisitors;
        this.entryFee = entryFee;
        this.visitors = new ArrayList<>();
    }

    public boolean visit(Tourist tourist, Random rng, DatabaseService db, double discount) {
        if (!hasCapacity()) {
            return false;
        }

        visitors.add(tourist);
        double price = entryFee * (1.0 - discount);
        tourist.spend(price);
        tourist.recordVisit(getName());

        db.appendRevenue(new RevenueRecord(0, "ENTRADA_RECINTO", price,
                tourist.getId(), getName(), LocalDateTime.now()));

        conductSurvey(tourist, rng);
        visitors.remove(tourist);
        return true;
    }

    public int conductSurvey(Tourist tourist, Random rng) {
        int range = experienceType.getMaxScore() - experienceType.getMinScore() + 1;
        return experienceType.getMinScore() + rng.nextInt(range);
    }

    public ExperienceType getExperienceType() {
        return experienceType;
    }

    public double getEntryFee() {
        return entryFee;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasCapacity() {
        return visitors.size() < maxVisitors;
    }

    @Override
    public int getCurrentOccupancy() {
        return visitors.size();
    }

    @Override
    public int getMaxCapacity() {
        return maxVisitors;
    }

    @Override
    public void enter(Tourist tourist) {
        visitors.add(tourist);
    }

    @Override
    public void exit(Tourist tourist) {
        visitors.remove(tourist);
    }
}
