package com.axity.dinosaurpark.event;

import com.axity.dinosaurpark.persistence.EventRecord;
import com.axity.dinosaurpark.simulation.ParkState;

import java.util.Random;

public interface SimulationEvent {

    String getName();

    String getDescription();

    double getProbability();

    void execute(ParkState state, Random rng);

    EventRecord toRecord(long step);
}
