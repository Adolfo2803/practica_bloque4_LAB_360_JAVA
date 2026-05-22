package com.axity.dinosaurpark.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TouristTest {

    @Test
    void newTourist_hasWaitingStatus() {
        Tourist tourist = new Tourist(1, "Juan");
        assertEquals(TouristStatus.WAITING, tourist.getStatus());
    }

    @Test
    void spend_accumulatesMoney() {
        Tourist tourist = new Tourist(1, "Ana");
        tourist.spend(25.0);
        tourist.spend(15.0);
        assertEquals(40.0, tourist.getMoneySpent());
    }

    @Test
    void recordVisit_addsZone() {
        Tourist tourist = new Tourist(1, "Pedro");
        tourist.recordVisit("Hub Central");
        tourist.recordVisit("Recinto Basico");
        assertEquals(2, tourist.getVisitedZones().size());
        assertEquals("Hub Central", tourist.getVisitedZones().get(0));
    }

    @Test
    void setStatus_changesStatus() {
        Tourist tourist = new Tourist(1, "Maria");
        tourist.setStatus(TouristStatus.IN_PARK);
        assertEquals(TouristStatus.IN_PARK, tourist.getStatus());
    }

    @Test
    void getters_returnCorrectValues() {
        Tourist tourist = new Tourist(5, "Carlos");
        assertEquals(5, tourist.getId());
        assertEquals("Carlos", tourist.getName());
    }
}
