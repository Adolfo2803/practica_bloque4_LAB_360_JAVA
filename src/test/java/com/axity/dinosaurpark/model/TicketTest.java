package com.axity.dinosaurpark.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    @Test
    void ticket_storesAllFields() {
        LocalDateTime now = LocalDateTime.of(2026, 5, 21, 10, 0);
        Ticket ticket = new Ticket(1L, 42, 25.0, "GENERAL", now);

        assertEquals(1L, ticket.getId());
        assertEquals(42, ticket.getTouristId());
        assertEquals(25.0, ticket.getPrice());
        assertEquals("GENERAL", ticket.getCategory());
        assertEquals(now, ticket.getIssuedAt());
    }
}
