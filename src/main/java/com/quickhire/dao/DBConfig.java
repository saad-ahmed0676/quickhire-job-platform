package com.quickhire.dao;

/**
 * DBConfig — Centralized database connection configuration.
 *
 * Credentials are read from environment variables so that no secrets
 * are committed to source control. Sensible local-development defaults
 * are provided as fallbacks for the URL and username (these are not
 * secrets), but the password MUST be supplied via the DB_PASSWORD
 * environment variable.
 *
 * To run locally:
 *   Windows (PowerShell): $env:DB_PASSWORD="your_password"
 *   macOS/Linux:          export DB_PASSWORD=your_password
 *
 * Optionally override DB_URL / DB_USER the same way if your setup differs
 * from the defaults below.
 */
public class DBConfig {
    public static final String URL = System.getenv().getOrDefault(
            "DB_URL",
            "jdbc:sqlserver://localhost:1433;databaseName=QuickHireDB;encrypt=false;trustServerCertificate=true;"
    );
    public static final String USER = System.getenv().getOrDefault("DB_USER", "sa");
    public static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "");
}