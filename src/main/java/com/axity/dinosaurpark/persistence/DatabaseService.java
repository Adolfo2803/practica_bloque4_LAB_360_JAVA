package com.axity.dinosaurpark.persistence;

public class DatabaseService {

    private final String dbPath;

    public DatabaseService(String dbPath) {
        this.dbPath = dbPath;
    }

    public void appendRevenue(RevenueRecord record) {
        // Implementacion completa en Fase 4 (H2 + Liquibase)
    }

    public void appendExpense(ExpenseRecord record) {
        // Implementacion completa en Fase 4 (H2 + Liquibase)
    }

    public void appendEvent(EventRecord record) {
        // Implementacion completa en Fase 4 (H2 + Liquibase)
    }

    public void close() {
        // Implementacion completa en Fase 4
    }

    public String getDbPath() {
        return dbPath;
    }
}
