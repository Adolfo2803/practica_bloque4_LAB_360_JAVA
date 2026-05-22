package com.axity.dinosaurpark.event;

import com.axity.dinosaurpark.model.*;
import com.axity.dinosaurpark.persistence.DatabaseService;
import com.axity.dinosaurpark.simulation.ParkState;
import com.axity.dinosaurpark.zone.PowerPlant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DinosaurEscapeEventTest {

    private DinosaurEscapeEvent event;
    private ParkState state;
    private DatabaseService db;
    private Random rng;

    @BeforeEach
    void setUp() {
        event = new DinosaurEscapeEvent(0.05);
        db = Mockito.mock(DatabaseService.class);
        rng = Mockito.mock(Random.class);
    }

    @Test
    void getName_returnsCorrectName() {
        assertEquals("ESCAPE_DINOSAURIO", event.getName());
    }

    @Test
    void getProbability_returnsConfiguredValue() {
        assertEquals(0.05, event.getProbability());
    }

    @Test
    void execute_dinosaurEscapes() {
        CarnivoreDinosaur dino = new CarnivoreDinosaur(1, "Rex", "T-Rex");
        Tourist tourist = new Tourist(1, "Ana");
        tourist.setStatus(TouristStatus.IN_PARK);

        List<Dinosaur> dinos = new ArrayList<>(List.of(dino));
        List<Tourist> tourists = new ArrayList<>(List.of(tourist));
        PowerPlant plant = new PowerPlant(100, 1.5, 0.05, 200, 500);

        state = new ParkState(tourists, dinos, List.of(), plant, db, rng);

        when(rng.nextInt(1)).thenReturn(0);
        when(rng.nextDouble()).thenReturn(0.99);

        event.execute(state, rng);

        assertEquals(DinosaurStatus.ESCAPED, dino.getStatus());
        assertEquals(TouristStatus.IN_PARK, tourist.getStatus());
        verify(db).appendEvent(any());
    }

    @Test
    void execute_attacksTouristWhenDangerHits() {
        CarnivoreDinosaur dino = new CarnivoreDinosaur(1, "Rex", "T-Rex");
        Tourist tourist = new Tourist(1, "Ana");
        tourist.setStatus(TouristStatus.IN_PARK);

        List<Dinosaur> dinos = new ArrayList<>(List.of(dino));
        List<Tourist> tourists = new ArrayList<>(List.of(tourist));
        PowerPlant plant = new PowerPlant(100, 1.5, 0.05, 200, 500);

        state = new ParkState(tourists, dinos, List.of(), plant, db, rng);

        when(rng.nextInt(anyInt())).thenReturn(0);
        when(rng.nextDouble()).thenReturn(0.1);

        event.execute(state, rng);

        assertEquals(DinosaurStatus.ESCAPED, dino.getStatus());
        assertEquals(TouristStatus.ATTACKED, tourist.getStatus());
    }

    @Test
    void execute_doesNothingWhenNoDinosaursInEnclosure() {
        CarnivoreDinosaur dino = new CarnivoreDinosaur(1, "Rex", "T-Rex");
        dino.escape();

        List<Dinosaur> dinos = new ArrayList<>(List.of(dino));
        PowerPlant plant = new PowerPlant(100, 1.5, 0.05, 200, 500);
        state = new ParkState(List.of(), dinos, List.of(), plant, db, rng);

        event.execute(state, rng);

        verify(db, never()).appendEvent(any());
    }
}
