package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.Tourist;

public interface ParkZone {

    String getName();

    boolean hasCapacity();

    int getCurrentOccupancy();

    int getMaxCapacity();

    void enter(Tourist tourist);

    void exit(Tourist tourist);
}
