package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.Tourist;
import com.axity.dinosaurpark.model.TouristStatus;
import com.axity.dinosaurpark.persistence.DatabaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CentralHubTest {

    private CentralHub hub;
    private DatabaseService db;

    @BeforeEach
    void setUp() {
        hub = new CentralHub(15.0, 0.4);
        db = Mockito.mock(DatabaseService.class);
    }

    @Test
    void visit_recordsZoneVisit() {
        Tourist tourist = new Tourist(1, "Ana");
        tourist.setStatus(TouristStatus.IN_PARK);
        Random rng = new Random(99);

        hub.visit(tourist, rng, db, 0.0);

        assertTrue(tourist.getVisitedZones().contains("Hub Central"));
    }

    @Test
    void visit_touristBuysSouvenirWhenProbabilityHits() {
        Tourist tourist = new Tourist(1, "Ana");
        tourist.setStatus(TouristStatus.IN_PARK);
        Random rng = Mockito.mock(Random.class);
        when(rng.nextDouble()).thenReturn(0.1);

        hub.visit(tourist, rng, db, 0.0);

        assertEquals(15.0, tourist.getMoneySpent());
        verify(db).appendRevenue(any());
    }

    @Test
    void visit_touristDoesNotBuyWhenProbabilityMisses() {
        Tourist tourist = new Tourist(1, "Ana");
        tourist.setStatus(TouristStatus.IN_PARK);
        Random rng = Mockito.mock(Random.class);
        when(rng.nextDouble()).thenReturn(0.9);

        hub.visit(tourist, rng, db, 0.0);

        assertEquals(0.0, tourist.getMoneySpent());
        verify(db, never()).appendRevenue(any());
    }

    @Test
    void visit_appliesDiscount() {
        Tourist tourist = new Tourist(1, "Ana");
        tourist.setStatus(TouristStatus.IN_PARK);
        Random rng = Mockito.mock(Random.class);
        when(rng.nextDouble()).thenReturn(0.1);

        hub.visit(tourist, rng, db, 0.30);

        assertEquals(10.5, tourist.getMoneySpent(), 0.01);
    }

    @Test
    void hasCapacity_alwaysTrue() {
        assertTrue(hub.hasCapacity());
    }
}
