package com.axity.dinosaurpark.event;

import com.axity.dinosaurpark.persistence.EventRecord;
import com.axity.dinosaurpark.persistence.ExpenseRecord;
import com.axity.dinosaurpark.simulation.ParkState;

import java.time.LocalDateTime;
import java.util.Random;

public class BlackoutEvent implements SimulationEvent {

    private static final double BLACKOUT_COST = 2000.0;
    private final double probability;

    public BlackoutEvent(double probability) {
        this.probability = probability;
    }

    @Override
    public String getName() {
        return "APAGON_MASIVO";
    }

    @Override
    public String getDescription() {
        return "Apagon masivo en el parque";
    }

    @Override
    public double getProbability() {
        return probability;
    }

    @Override
    public void execute(ParkState state, Random rng) {
        state.getPowerPlant().triggerFailure(state.getDb());
        state.addExpense(BLACKOUT_COST);
        state.getDb().appendExpense(new ExpenseRecord(0, "APAGON", BLACKOUT_COST,
                "Apagon masivo en el parque", LocalDateTime.now()));
        state.getDb().appendEvent(toRecord(state.getCurrentStep()));
    }

    @Override
    public EventRecord toRecord(long step) {
        return new EventRecord(step, getName(), getDescription(), "Planta de Energia", LocalDateTime.now());
    }
}
