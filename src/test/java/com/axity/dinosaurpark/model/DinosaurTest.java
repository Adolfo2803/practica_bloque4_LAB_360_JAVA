package com.axity.dinosaurpark.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DinosaurTest {

    @Test
    void carnivoreDinosaur_hasCorrectProperties() {
        CarnivoreDinosaur dino = new CarnivoreDinosaur(1, "Rex", "T-Rex");
        assertEquals("CARNIVORE", dino.getDiet());
        assertEquals(0.9, dino.getDangerLevel());
        assertEquals(500.0, dino.getFeedingCostPerDay());
        assertEquals(DinosaurStatus.IN_ENCLOSURE, dino.getStatus());
    }

    @Test
    void herbivoreDinosaur_hasCorrectProperties() {
        HerbivoreDinosaur dino = new HerbivoreDinosaur(2, "Tri", "Triceratops");
        assertEquals("HERBIVORE", dino.getDiet());
        assertEquals(0.2, dino.getDangerLevel());
        assertEquals(200.0, dino.getFeedingCostPerDay());
    }

    @Test
    void escape_changesStatusToEscaped() {
        CarnivoreDinosaur dino = new CarnivoreDinosaur(1, "Rex", "T-Rex");
        dino.escape();
        assertEquals(DinosaurStatus.ESCAPED, dino.getStatus());
    }

    @Test
    void recapture_changesStatusToRecaptured() {
        CarnivoreDinosaur dino = new CarnivoreDinosaur(1, "Rex", "T-Rex");
        dino.escape();
        dino.recapture();
        assertEquals(DinosaurStatus.RECAPTURED, dino.getStatus());
    }

    @Test
    void returnToEnclosure_changesStatusBack() {
        HerbivoreDinosaur dino = new HerbivoreDinosaur(1, "Tri", "Triceratops");
        dino.escape();
        dino.returnToEnclosure();
        assertEquals(DinosaurStatus.IN_ENCLOSURE, dino.getStatus());
    }

    @Test
    void getters_returnCorrectValues() {
        CarnivoreDinosaur dino = new CarnivoreDinosaur(3, "Veloci", "Velociraptor");
        assertEquals(3, dino.getId());
        assertEquals("Veloci", dino.getName());
        assertEquals("Velociraptor", dino.getSpecies());
    }
}
