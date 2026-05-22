package com.axity.dinosaurpark.event;

import com.axity.dinosaurpark.model.Dinosaur;
import com.axity.dinosaurpark.model.DinosaurStatus;
import com.axity.dinosaurpark.model.Tourist;
import com.axity.dinosaurpark.model.TouristStatus;
import com.axity.dinosaurpark.persistence.EventRecord;
import com.axity.dinosaurpark.simulation.ParkState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DinosaurEscapeEvent implements SimulationEvent {

    private final double probability;
    private String lastAffected = "";

    public DinosaurEscapeEvent(double probability) {
        this.probability = probability;
    }

    @Override
    public String getName() {
        return "ESCAPE_DINOSAURIO";
    }

    @Override
    public String getDescription() {
        return "Un dinosaurio ha escapado de su recinto";
    }

    @Override
    public double getProbability() {
        return probability;
    }

    @Override
    public void execute(ParkState state, Random rng) {
        List<Dinosaur> inEnclosure = state.getDinosaurs().stream()
                .filter(d -> d.getStatus() == DinosaurStatus.IN_ENCLOSURE)
                .collect(Collectors.toList());

        if (inEnclosure.isEmpty()) {
            return;
        }

        Dinosaur escaped = inEnclosure.get(rng.nextInt(inEnclosure.size()));
        escaped.escape();
        lastAffected = escaped.getName();

        if (rng.nextDouble() < escaped.getDangerLevel()) {
            List<Tourist> inPark = state.getTourists().stream()
                    .filter(t -> t.getStatus() == TouristStatus.IN_PARK)
                    .collect(Collectors.toList());

            if (!inPark.isEmpty()) {
                Tourist attacked = inPark.get(rng.nextInt(inPark.size()));
                attacked.setStatus(TouristStatus.ATTACKED);
                lastAffected += ", Tourist-" + attacked.getId();
            }
        }

        state.getDb().appendEvent(toRecord(state.getCurrentStep()));
    }

    @Override
    public EventRecord toRecord(long step) {
        return new EventRecord(step, getName(), getDescription(), lastAffected, LocalDateTime.now());
    }
}
