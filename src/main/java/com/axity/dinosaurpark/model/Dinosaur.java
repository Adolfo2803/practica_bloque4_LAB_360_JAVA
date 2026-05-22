package com.axity.dinosaurpark.model;

public abstract class Dinosaur {

    private final int id;
    private final String name;
    private final String species;
    private DinosaurStatus status;
    private final double feedingCostPerDay;

    protected Dinosaur(int id, String name, String species, double feedingCostPerDay) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.status = DinosaurStatus.IN_ENCLOSURE;
        this.feedingCostPerDay = feedingCostPerDay;
    }

    public abstract String getDiet();

    public abstract double getDangerLevel();

    public void escape() {
        this.status = DinosaurStatus.ESCAPED;
    }

    public void recapture() {
        this.status = DinosaurStatus.RECAPTURED;
    }

    public void returnToEnclosure() {
        this.status = DinosaurStatus.IN_ENCLOSURE;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpecies() {
        return species;
    }

    public DinosaurStatus getStatus() {
        return status;
    }

    public double getFeedingCostPerDay() {
        return feedingCostPerDay;
    }
}
