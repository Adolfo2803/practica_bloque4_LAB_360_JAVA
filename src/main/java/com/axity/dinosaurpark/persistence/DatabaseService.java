package com.axity.dinosaurpark.persistence;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DatabaseService {

    private final String dbPath;
    private Connection connection;

    public DatabaseService(String dbPath) {
        this.dbPath = dbPath;
        try {
            runLiquibase(dbPath);
            connection = DriverManager.getConnection("jdbc:h2:" + dbPath, "sa", "");
        } catch (Exception e) {
            throw new IllegalStateException("Error al inicializar la base de datos", e);
        }
    }

    private void runLiquibase(String dbPath) throws Exception {
        try (Connection lbConn = DriverManager.getConnection("jdbc:h2:" + dbPath, "sa", "")) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(lbConn));
            try (Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(), database)) {
                liquibase.update("");
            }
        }
    }

    public void appendRevenue(RevenueRecord record) {
        String sql = "INSERT INTO revenues (type, amount, tourist_id, zone, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, record.type());
            ps.setDouble(2, record.amount());
            ps.setInt(3, record.touristId());
            ps.setString(4, record.zone());
            ps.setTimestamp(5, Timestamp.valueOf(record.timestamp()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Error al insertar revenue", e);
        }
    }

    public void appendExpense(ExpenseRecord record) {
        String sql = "INSERT INTO expenses (type, amount, description, timestamp) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, record.type());
            ps.setDouble(2, record.amount());
            ps.setString(3, record.description());
            ps.setTimestamp(4, Timestamp.valueOf(record.timestamp()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Error al insertar expense", e);
        }
    }

    public void appendEvent(EventRecord record) {
        String sql = "INSERT INTO events (step, event_name, description, affected_entities, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, record.step());
            ps.setString(2, record.eventName());
            ps.setString(3, record.description());
            ps.setString(4, record.affectedEntities());
            ps.setTimestamp(5, Timestamp.valueOf(record.timestamp()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Error al insertar event", e);
        }
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new IllegalStateException("Error al cerrar la conexion", e);
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public String getDbPath() {
        return dbPath;
    }
}
