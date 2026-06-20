package com.quickhire.dao;

import com.quickhire.model.JobApplication;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private JobApplication mapRow(ResultSet rs) throws SQLException {
        JobApplication app = new JobApplication();
        app.setApplicationId(rs.getInt("applicationId"));
        app.setJobId(rs.getInt("jobId"));
        app.setSeekerId(rs.getInt("seekerId"));
        app.setApplicationNote(rs.getString("applicationNote"));
        app.setSubmissionDate(rs.getTimestamp("submissionDate").toLocalDateTime());
        app.setStatus(rs.getString("status"));
        try { app.setSeekerName(rs.getString("seekerName")); }
        catch (SQLException ignored) {}
        try { app.setSeekerRating(rs.getDouble("averageRating")); }
        catch (SQLException ignored) {}
        return app;
    }

    public int insertApplication(JobApplication app) throws SQLException {
        String sql = "INSERT INTO JobApplications (jobId, seekerId, applicationNote, status) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, app.getJobId());
            ps.setInt(2, app.getSeekerId());
            ps.setString(3, app.getApplicationNote());
            ps.setString(4, app.getStatus());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("Failed to get generated applicationId");
        }
    }

    public boolean hasApplied(int jobId, int seekerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM JobApplications WHERE jobId = ? AND seekerId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ps.setInt(2, seekerId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public List<JobApplication> getApplicationsForJob(int jobId) throws SQLException {
        String sql = "SELECT a.*, u.name AS seekerName, u.averageRating " +
                "FROM JobApplications a JOIN Users u ON a.seekerId = u.userId " +
                "WHERE a.jobId = ? ORDER BY a.submissionDate";
        List<JobApplication> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public JobApplication getApplicationById(int applicationId) throws SQLException {
        String sql = "SELECT * FROM JobApplications WHERE applicationId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, applicationId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        }
    }

    public void updateApplicationStatus(int applicationId, String status)
            throws SQLException {
        String sql = "UPDATE JobApplications SET status = ? WHERE applicationId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, applicationId);
            ps.executeUpdate();
        }
    }

    public void rejectOtherApplications(int jobId, int acceptedApplicationId)
            throws SQLException {
        String sql = "UPDATE JobApplications SET status = 'REJECTED' " +
                "WHERE jobId = ? AND applicationId != ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ps.setInt(2, acceptedApplicationId);
            ps.executeUpdate();
        }
    }

    public int getAcceptedSeekerId(int jobId) throws SQLException {
        String sql = "SELECT seekerId FROM JobApplications " +
                "WHERE jobId = ? AND status = 'ACCEPTED'";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("seekerId");
            throw new SQLException("No accepted seeker found for job " + jobId);
        }
    }

    public List<JobApplication> getAcceptedApplicationsForSeeker(int seekerId) throws SQLException {
        String sql = "SELECT a.*, j.title AS jobTitle, u.name AS providerName " +
                "FROM JobApplications a " +
                "JOIN MicroJobs j ON a.jobId = j.jobId " +
                "JOIN Users u ON j.providerId = u.userId " +
                "WHERE a.seekerId = ? AND a.status = 'ACCEPTED'";
        List<JobApplication> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, seekerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JobApplication app = mapRow(rs);
                try { app.setJobTitle(rs.getString("jobTitle")); }       catch (SQLException ignored) {}
                try { app.setProviderName(rs.getString("providerName")); } catch (SQLException ignored) {}
                list.add(app);
            }
        }
        return list;
    }

    /**
     * Returns all PENDING applications for any job owned by this provider —
     * used to show the red dot on login.
     */
    public List<JobApplication> getPendingApplicationsForProvider(int providerId) throws SQLException {
        String sql = "SELECT a.*, j.title AS jobTitle, u.name AS seekerName " +
                "FROM JobApplications a " +
                "JOIN MicroJobs j ON a.jobId = j.jobId " +
                "JOIN Users u ON a.seekerId = u.userId " +
                "WHERE j.providerId = ? AND a.status = 'PENDING'";
        List<JobApplication> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, providerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JobApplication app = mapRow(rs);
                try { app.setJobTitle(rs.getString("jobTitle")); }     catch (SQLException ignored) {}
                try { app.setSeekerName(rs.getString("seekerName")); } catch (SQLException ignored) {}
                list.add(app);
            }
        }
        return list;
    }
}