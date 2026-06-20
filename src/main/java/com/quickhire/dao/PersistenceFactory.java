package com.quickhire.dao;

/**
 * PersistenceFactory — Factory for creating PersistenceHandler instances.
 *
 * Mirrors the pattern from the lecture example:
 *
 *   class PersistenceFactory {
 *       PersistenceHandler service;
 *       public PersistenceHandler createPersistenceHandler(String servName) {
 *           if (service == null) {
 *               if (servName.equals("SqlServer"))  service = new SqlServerPersistenceHandler();
 *               else if (servName.equals("MySQL")) service = new MySQLPersistenceHandler();
 *           }
 *           return service;
 *       }
 *   }
 *
 * The caller (DatabaseConnection) passes "SqlServer" and gets back a
 * fully ready {@link PersistenceHandler} without knowing the concrete class.
 *
 * Pattern: Factory Method (GoF)
 * Layer:   Database / Persistence Layer
 *
 * Supported values for {@code dbType}:
 *   "SqlServer"  — Microsoft SQL Server (active, fully implemented)
 *
 * Only "SqlServer" is implemented because QuickHire uses SQL Server.
 * The factory structure makes it trivial to add further implementations
 * in future without changing any other class.
 */
public class PersistenceFactory {

    // Singleton handler — one connection per type per session
    private PersistenceHandler handler;

    /**
     * Returns a {@link PersistenceHandler} for the requested database type.
     *
     * @param dbType the database type identifier (e.g. "SqlServer")
     * @return a PersistenceHandler ready to provide connections
     * @throws IllegalArgumentException if the requested type is not supported
     */
    public PersistenceHandler createPersistenceHandler(String dbType) {
        if (handler == null) {
            if ("SqlServer".equals(dbType)) {
                handler = new SqlServerPersistenceHandler();
            } else {
                throw new IllegalArgumentException(
                        "Unsupported persistence type: '" + dbType + "'. "
                                + "QuickHire currently supports: SqlServer"
                );
            }
        }
        return handler;
    }
}