package com.axity.dinosaurpark.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseServiceTest {

    private DatabaseService db;

    @BeforeEach
    void setUp() {
        db = new DatabaseService("./data/test-" + System.currentTimeMillis());
    }

    @AfterEach
    void tearDown() {
        db.close();
    }

    @Test
    void constructor_createsTablesViaLiquibase() throws Exception {
        Statement stmt = db.getConnection().createStatement();

        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM revenues");
        assertTrue(rs.next());
        assertEquals(0, rs.getInt(1));

        rs = stmt.executeQuery("SELECT COUNT(*) FROM expenses");
        assertTrue(rs.next());
        assertEquals(0, rs.getInt(1));

        rs = stmt.executeQuery("SELECT COUNT(*) FROM events");
        assertTrue(rs.next());
        assertEquals(0, rs.getInt(1));

        rs.close();
        stmt.close();
    }

    @Test
    void appendRevenue_insertsRecord() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        db.appendRevenue(new RevenueRecord(0, "BOLETO", 25.0, 1, "Zona de Llegada", now));

        Statement stmt = db.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("SELECT type, amount, tourist_id, zone FROM revenues");
        assertTrue(rs.next());
        assertEquals("BOLETO", rs.getString("type"));
        assertEquals(25.0, rs.getDouble("amount"));
        assertEquals(1, rs.getInt("tourist_id"));
        assertEquals("Zona de Llegada", rs.getString("zone"));
        assertFalse(rs.next());

        rs.close();
        stmt.close();
    }

    @Test
    void appendExpense_insertsRecord() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        db.appendExpense(new ExpenseRecord(0, "MANTENIMIENTO", 200.0, "Mantenimiento diario", now));

        Statement stmt = db.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("SELECT type, amount, description FROM expenses");
        assertTrue(rs.next());
        assertEquals("MANTENIMIENTO", rs.getString("type"));
        assertEquals(200.0, rs.getDouble("amount"));
        assertEquals("Mantenimiento diario", rs.getString("description"));
        assertFalse(rs.next());

        rs.close();
        stmt.close();
    }

    @Test
    void appendEvent_insertsRecord() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        db.appendEvent(new EventRecord(5, "ESCAPE_DINOSAURIO", "Rex escapo", "Rex, Tourist-1", now));

        Statement stmt = db.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("SELECT step, event_name, description, affected_entities FROM events");
        assertTrue(rs.next());
        assertEquals(5, rs.getLong("step"));
        assertEquals("ESCAPE_DINOSAURIO", rs.getString("event_name"));
        assertEquals("Rex escapo", rs.getString("description"));
        assertEquals("Rex, Tourist-1", rs.getString("affected_entities"));
        assertFalse(rs.next());

        rs.close();
        stmt.close();
    }

    @Test
    void appendRevenue_multipleRecords() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        db.appendRevenue(new RevenueRecord(0, "BOLETO", 25.0, 1, "Llegada", now));
        db.appendRevenue(new RevenueRecord(0, "SOUVENIR", 15.0, 2, "Hub", now));
        db.appendRevenue(new RevenueRecord(0, "SPA", 20.0, 3, "Banos", now));

        Statement stmt = db.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM revenues");
        assertTrue(rs.next());
        assertEquals(3, rs.getInt(1));

        rs.close();
        stmt.close();
    }
}
