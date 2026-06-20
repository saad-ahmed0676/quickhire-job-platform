package com.quickhire.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * PersistenceHandler — Abstraction interface for all database operations.
 *
 * Defines the contract that any persistence implementation must fulfil.
 * This follows the Factory Method pattern: the application code depends
 * only on this interface, never on a concrete DB driver.
 *
 * Pattern: Factory Method (GoF)
 * Layer:   Database / Persistence Layer
 *
 * Currently implemented by:
 *   - SqlServerPersistenceHandler  (active implementation — SQL Server via JDBC)
 *
 * To switch databases in future, implement this interface for the new DB
 * and change one line in PersistenceFactory — no other code changes needed.
 */
public interface PersistenceHandler {

    /**
     * Establish and return a live connection to the underlying data store.
     *
     * @return a valid, open {@link Connection}
     * @throws SQLException if the connection cannot be established
     */
    Connection getConnection() throws SQLException;

    /**
     * Close the connection and release any held resources.
     */
    void closeConnection();

    /**
     * Test whether the handler currently holds an open connection.
     *
     * @return true if connected and ready for queries
     */
    boolean isConnected();
}