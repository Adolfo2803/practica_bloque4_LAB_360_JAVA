package com.axity.dinosaurpark.event;

import com.axity.dinosaurpark.model.Vehicle;
import com.axity.dinosaurpark.model.VehicleStatus;
import com.axity.dinosaurpark.persistence.EventRecord;
import com.axity.dinosaurpark.simulation.ParkState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class VehicleFailureEvent implements SimulationEvent {

    private final double probability;
    private String lastAffected = "";

    public VehicleFailureEvent(double probability) {
        this.probability = probability;
    }

    @Override
    public String getName() {
        return "FALLA_VEHICULO";
    }

    @Override
    public String getDescription() {
        return "Un vehiculo de mantenimiento ha fallado";
    }

    @Override
    public double getProbability() {
        return probability;
    }

    @Override
    public void execute(ParkState state, Random rng) {
        List<Vehicle> available = state.getVehicles().stream()
                .filter(v -> v.getStatus() == VehicleStatus.AVAILABLE)
                .collect(Collectors.toList());

        if (available.isEmpty()) {
            return;
        }

        Vehicle broken = available.get(rng.nextInt(available.size()));
        broken.markBroken();
        lastAffected = "Vehicle-" + broken.getId();

        state.getDb().appendEvent(toRecord(state.getCurrentStep()));
    }

    @Override
    public EventRecord toRecord(long step) {
        return new EventRecord(step, getName(), getDescription(), lastAffected, LocalDateTime.now());
    }
}
