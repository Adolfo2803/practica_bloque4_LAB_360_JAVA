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

class BathroomZoneTest {

    private BathroomZone bathroom;
    private DatabaseService db;

    @BeforeEach
    void setUp() {
        bathroom = new BathroomZone(10, 3, 20.0, 0.2);
        db = Mockito.mock(DatabaseService.class);
    }

    @Test
    void tryEnter_succeedsWhenCapacityAvailable() {
        Tourist tourist = new Tourist(1, "Ana");
        tourist.setStatus(TouristStatus.IN_PARK);
        Random rng = Mockito.mock(Random.class);
        when(rng.nextDouble()).thenReturn(0.9);

        boolean entered = bathroom.tryEnter(tourist, rng, db, 0.0);

        assertTrue(entered);
        assertEquals(1, bathroom.getCurrentOccupancy());
    }

    @Test
    void tryEnter_failsWhenFull() {
        Random rng = Mockito.mock(Random.class);
        when(rng.nextDouble()).thenReturn(0.9);

        for (int i = 0; i < 10; i++) {
            Tourist t = new Tourist(i, "T" + i);
            t.setStatus(TouristStatus.IN_PARK);
            bathroom.tryEnter(t, rng, db, 0.0);
        }

        Tourist extra = new Tourist(99, "Extra");
        assertFalse(bathroom.tryEnter(extra, rng, db, 0.0));
    }

    @Test
    void tryEnter_buysSpaWhenProbabilityHits() {
        Tourist tourist = new Tourist(1, "Ana");
        tourist.setStatus(TouristStatus.IN_PARK);
        Random rng = Mockito.mock(Random.class);
        when(rng.nextDouble()).thenReturn(0.1);

        bathroom.tryEnter(tourist, rng, db, 0.0);

        assertEquals(20.0, tourist.getMoneySpent());
        verify(db).appendRevenue(any());
    }

    @Test
    void tick_decrementsAndFreesSlots() {
        Random rng = Mockito.mock(Random.class);
        when(rng.nextDouble()).thenReturn(0.9);

        Tourist t = new Tourist(1, "Ana");
        t.setStatus(TouristStatus.IN_PARK);
        BathroomZone shortBathroom = new BathroomZone(10, 2, 20.0, 0.2);
        shortBathroom.tryEnter(t, rng, db, 0.0);

        assertEquals(1, shortBathroom.getCurrentOccupancy());
        shortBathroom.tick();
        assertEquals(1, shortBathroom.getCurrentOccupancy());
        shortBathroom.tick();
        assertEquals(0, shortBathroom.getCurrentOccupancy());
    }

    @Test
    void getName_returnsCorrectName() {
        assertEquals("Banos", bathroom.getName());
    }
}
