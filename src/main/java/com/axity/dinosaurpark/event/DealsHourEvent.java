package com.axity.dinosaurpark.event;

import com.axity.dinosaurpark.persistence.EventRecord;
import com.axity.dinosaurpark.simulation.ParkState;

import java.time.LocalDateTime;
import java.util.Random;

public class DealsHourEvent implements SimulationEvent {

    private static final double DISCOUNT = 0.30;
    private final double probability;

    public DealsHourEvent(double probability) {
        this.probability = probability;
    }

    @Override
    public String getName() {
        return "HORA_DE_OFERTAS";
    }

    @Override
    public String getDescription() {
        return "Hora de ofertas con 30% de descuento";
    }

    @Override
    public double getProbability() {
        return probability;
    }

    @Override
    public void execute(ParkState state, Random rng) {
        state.setDealsHourActive(true);
        state.setCurrentDiscount(DISCOUNT);
        state.getDb().appendEvent(toRecord(state.getCurrentStep()));
    }

    @Override
    public EventRecord toRecord(long step) {
        return new EventRecord(step, getName(), getDescription(), "Todas las zonas comerciales",
                LocalDateTime.now());
    }
}
