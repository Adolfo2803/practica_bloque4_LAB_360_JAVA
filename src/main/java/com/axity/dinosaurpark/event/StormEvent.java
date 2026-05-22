package com.axity.dinosaurpark.event;

import com.axity.dinosaurpark.model.Tourist;
import com.axity.dinosaurpark.model.TouristStatus;
import com.axity.dinosaurpark.persistence.EventRecord;
import com.axity.dinosaurpark.persistence.ExpenseRecord;
import com.axity.dinosaurpark.simulation.ParkState;

import java.time.LocalDateTime;
import java.util.Random;

public class StormEvent implements SimulationEvent {

    private static final double STORM_COST = 500.0;
    private final double probability;

    public StormEvent(double probability) {
        this.probability = probability;
    }

    @Override
    public String getName() {
        return "TORMENTA_TORRENCIAL";
    }

    @Override
    public String getDescription() {
        return "Tormenta torrencial azota el parque";
    }

    @Override
    public double getProbability() {
        return probability;
    }

    @Override
    public void execute(ParkState state, Random rng) {
        for (Tourist tourist : state.getTourists()) {
            if (tourist.getStatus() == TouristStatus.IN_PARK) {
                tourist.recordVisit("Evacuacion");
            }
        }

        state.addExpense(STORM_COST);
        state.getDb().appendExpense(new ExpenseRecord(0, "TORMENTA", STORM_COST,
                "Danos por tormenta torrencial", LocalDateTime.now()));
        state.getDb().appendEvent(toRecord(state.getCurrentStep()));
    }

    @Override
    public EventRecord toRecord(long step) {
        return new EventRecord(step, getName(), getDescription(), "Todos los turistas", LocalDateTime.now());
    }
}
