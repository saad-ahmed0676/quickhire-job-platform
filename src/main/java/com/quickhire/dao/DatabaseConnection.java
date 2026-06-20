package com.quickhire.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DatabaseConnection — Singleton gateway to the persistence layer.
 *
 * All DAO classes call {@code DatabaseConnection.getInstance().getConnection()}
 * to obtain a JDBC connection.
 *
 * This class no longer calls DriverManager directly. Instead it delegates
 * to {@link PersistenceFactory} which creates the appropriate
 * {@link PersistenceHandler} — demonstrating the Factory Method pattern.
 *
 * Pattern: Singleton (connection pool) + Factory Method (via PersistenceFactory)
 * Layer:   Database / Persistence Layer
 *
 * Flow:
 *   DAO → DatabaseConnection.getInstance() → PersistenceFactory
 *       → SqlServerPersistenceHandler → JDBC → SQL Server
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;

    // The factory decides which concrete handler to create
    private static final PersistenceFactory factory = new PersistenceFactory();

    // The active handler — created once, reused across all DAO calls
    private final PersistenceHandler persistenceHandler;

    private DatabaseConnection() throws SQLException {
        // "SqlServer" is the only supported type for QuickHire
        this.persistenceHandler = factory.createPersistenceHandler("SqlServer");

        // Eagerly open the connection to fail fast on misconfiguration
        this.persistenceHandler.getConnection();
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || !instance.persistenceHandler.isConnected()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Returns the active JDBC connection.
     * All DAO classes use this method — they never see PersistenceHandler directly.
     */
    public Connection getConnection() throws SQLException {
        return persistenceHandler.getConnection();
    }

    public void closeConnection() {
        persistenceHandler.closeConnection();
    }
}