package com.quickhire.dao;

import com.quickhire.model.MicroJob;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JobDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private MicroJob mapRow(ResultSet rs) throws SQLException {
        MicroJob job = new MicroJob();
        job.setJobId(rs.getInt("jobId"));
        job.setProviderId(rs.getInt("providerId"));
        job.setTitle(rs.getString("title"));
        job.setCategory(rs.getString("category"));
        job.setDescription(rs.getString("description"));
        job.setLocation(rs.getString("location"));
        job.setHourlyRate(rs.getDouble("hourlyRate"));
        job.setStatus(rs.getString("status"));
        job.setPostedDate(rs.getTimestamp("postedDate").toLocalDateTime());
        // providerName may be available if a JOIN was used
        try { job.setProviderName(rs.getString("providerName")); }
        catch (SQLException ignored) {}
        return job;
    }

    public int insertJob(MicroJob job) throws SQLException {
        String sql = "INSERT INTO MicroJobs (providerId, title, category, description, " +
                "location, hourlyRate, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, job.getProviderId());
            ps.setString(2, job.getTitle());
            ps.setString(3, job.getCategory());
            ps.setString(4, job.getDescription());
            ps.setString(5, job.getLocation());
            ps.setDouble(6, job.getHourlyRate());
            ps.setString(7, job.getStatus());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("Failed to get generated jobId");
        }
    }

    public MicroJob getJobById(int jobId) throws SQLException {
        String sql = "SELECT j.*, u.name AS providerName FROM MicroJobs j " +
                "JOIN Users u ON j.providerId = u.userId WHERE j.jobId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        }
    }

    public List<MicroJob> getJobsByStatus(String status) throws SQLException {
        String sql = "SELECT j.*, u.name AS providerName FROM MicroJobs j " +
                "JOIN Users u ON j.providerId = u.userId WHERE j.status = ? " +
                "ORDER BY j.postedDate DESC";
        List<MicroJob> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // UC03 — Filter: all parameters are optional (pass null/"" to ignore)
    public List<MicroJob> filterJobs(String category, String location, double minRate)
            throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT j.*, u.name AS providerName FROM MicroJobs j " +
                        "JOIN Users u ON j.providerId = u.userId WHERE j.status = 'OPEN'");
        if (category != null && !category.isEmpty()) sql.append(" AND j.category = ?");
        if (location  != null && !location.isEmpty())  sql.append(" AND j.location LIKE ?");
        if (minRate > 0)                               sql.append(" AND j.hourlyRate >= ?");
        sql.append(" ORDER BY j.postedDate DESC");

        List<MicroJob> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql.toString())) {
            int idx = 1;
            if (category != null && !category.isEmpty()) ps.setString(idx++, category);
            if (location  != null && !location.isEmpty())  ps.setString(idx++, "%" + location + "%");
            if (minRate > 0)                               ps.setDouble(idx, minRate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<MicroJob> getJobsByProvider(int providerId) throws SQLException {
        String sql = "SELECT j.*, u.name AS providerName FROM MicroJobs j " +
                "JOIN Users u ON j.providerId = u.userId WHERE j.providerId = ? " +
                "ORDER BY j.postedDate DESC";
        List<MicroJob> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, providerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // Jobs where the seeker was accepted
    public List<MicroJob> getJobsBySeeker(int seekerId) throws SQLException {
        String sql = "SELECT j.*, u.name AS providerName FROM MicroJobs j " +
                "JOIN Users u ON j.providerId = u.userId " +
                "JOIN JobApplications a ON a.jobId = j.jobId " +
                "WHERE a.seekerId = ? AND a.status = 'ACCEPTED' " +
                "ORDER BY j.postedDate DESC";
        List<MicroJob> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, seekerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public void updateJob(MicroJob job) throws SQLException {
        String sql = "UPDATE MicroJobs SET title = ?, category = ?, description = ?, " +
                "location = ?, hourlyRate = ? WHERE jobId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, job.getTitle());
            ps.setString(2, job.getCategory());
            ps.setString(3, job.getDescription());
            ps.setString(4, job.getLocation());
            ps.setDouble(5, job.getHourlyRate());
            ps.setInt(6, job.getJobId());
            ps.executeUpdate();
        }
    }

    public void updateJobStatus(int jobId, String newStatus) throws SQLException {
        String sql = "UPDATE MicroJobs SET status = ? WHERE jobId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, jobId);
            ps.executeUpdate();
        }
    }
}