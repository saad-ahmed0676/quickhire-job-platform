package com.quickhire.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * SqlServerPersistenceHandler — Concrete persistence implementation for SQL Server.
 *
 * Implements {@link PersistenceHandler} using Microsoft SQL Server via JDBC.
 * Connection credentials are read from {@link DBConfig}.
 *
 * This class is never instantiated directly by application code —
 * it is only created through {@link PersistenceFactory}.
 *
 * Pattern: Factory Method (concrete product)
 * Layer:   Database / Persistence Layer
 */
public class SqlServerPersistenceHandler implements PersistenceHandler {

    private Connection connection;

    /**
     * Package-private constructor — only {@link PersistenceFactory} may call this.
     */
    SqlServerPersistenceHandler() {}

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                    DBConfig.URL,
                    DBConfig.USER,
                    DBConfig.PASSWORD
            );
            System.out.println("[PersistenceHandler] SQL Server connection established.");
        }
        return connection;
    }

    @Override
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[PersistenceHandler] SQL Server connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}