package com.quickhire.dao;

import com.quickhire.model.*;
import java.sql.*;
import java.time.LocalDateTime;

public class UserDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // Factory Method pattern — decides which User subclass to create
    private User mapRow(ResultSet rs) throws SQLException {
        int userId             = rs.getInt("userId");
        String name            = rs.getString("name");
        String email           = rs.getString("email");
        String passwordHash    = rs.getString("passwordHash");
        String role            = rs.getString("role");
        String bio             = rs.getString("bio");
        String contactDetails  = rs.getString("contactDetails");
        double averageRating   = rs.getDouble("averageRating");
        LocalDateTime createdAt = rs.getTimestamp("createdAt").toLocalDateTime();

        if ("SEEKER".equals(role)) {
            return new JobSeeker(userId, name, email, passwordHash,
                    bio, contactDetails, averageRating, createdAt);
        } else {
            return new JobProvider(userId, name, email, passwordHash,
                    bio, contactDetails, averageRating, createdAt);
        }
    }

    // INSERT a new user, returns the auto-generated userId
    public int insertUser(User user) throws SQLException {
        String sql = "INSERT INTO Users (name, email, passwordHash, role, bio, contactDetails) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getBio());
            ps.setString(6, user.getContactDetails());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("Failed to retrieve generated userId");
        }
    }

    // SELECT by email — used by login
    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Users WHERE email = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        }
    }

    // SELECT by ID — used wherever we need to reload a user
    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM Users WHERE userId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        }
    }

    // UPDATE bio and contactDetails — used by UC11 (Profile)
    public void updateUserProfile(int userId, String bio, String contactDetails)
            throws SQLException {
        String sql = "UPDATE Users SET bio = ?, contactDetails = ? WHERE userId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, bio);
            ps.setString(2, contactDetails);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    // UPDATE averageRating — called by ReviewService after a new review is submitted
    public void updateAverageRating(int userId, double rating) throws SQLException {
        String sql = "UPDATE Users SET averageRating = ? WHERE userId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setDouble(1, rating);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }
}