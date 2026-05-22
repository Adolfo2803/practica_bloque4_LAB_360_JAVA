package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.Ticket;
import com.axity.dinosaurpark.model.Tourist;
import com.axity.dinosaurpark.model.TouristStatus;
import com.axity.dinosaurpark.persistence.DatabaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArrivalZoneTest {

    private ArrivalZone arrivalZone;
    private DatabaseService db;

    @BeforeEach
    void setUp() {
        arrivalZone = new ArrivalZone(30, 25.0);
        db = Mockito.mock(DatabaseService.class);
    }

    @Test
    void processBatch_sellsTicketsAndChangesStatus() {
        Tourist t1 = new Tourist(1, "Ana");
        Tourist t2 = new Tourist(2, "Pedro");
        arrivalZone.addToQueue(t1);
        arrivalZone.addToQueue(t2);

        List<Ticket> tickets = arrivalZone.processBatch(5, db);

        assertEquals(2, tickets.size());
        assertEquals(TouristStatus.IN_PARK, t1.getStatus());
        assertEquals(TouristStatus.IN_PARK, t2.getStatus());
        assertEquals(25.0, t1.getMoneySpent());
    }

    @Test
    void processBatch_respectsBatchSize() {
        for (int i = 0; i < 10; i++) {
            arrivalZone.addToQueue(new Tourist(i, "T" + i));
        }

        List<Ticket> tickets = arrivalZone.processBatch(3, db);

        assertEquals(3, tickets.size());
        assertEquals(7, arrivalZone.getCurrentOccupancy());
    }

    @Test
    void hasCapacity_respectsMaxCapacity() {
        ArrivalZone small = new ArrivalZone(2, 25.0);
        small.addToQueue(new Tourist(1, "A"));
        small.addToQueue(new Tourist(2, "B"));
        assertFalse(small.hasCapacity());
    }

    @Test
    void getName_returnsCorrectName() {
        assertEquals("Zona de Llegada", arrivalZone.getName());
    }

    @Test
    void enter_addsTouristToQueue() {
        Tourist tourist = new Tourist(1, "Ana");
        arrivalZone.enter(tourist);
        assertEquals(1, arrivalZone.getCurrentOccupancy());
    }

    @Test
    void exit_removesTouristFromQueue() {
        Tourist tourist = new Tourist(1, "Ana");
        arrivalZone.addToQueue(tourist);
        arrivalZone.exit(tourist);
        assertEquals(0, arrivalZone.getCurrentOccupancy());
    }

    @Test
    void getMaxCapacity_returnsConfiguredValue() {
        assertEquals(30, arrivalZone.getMaxCapacity());
    }
}
