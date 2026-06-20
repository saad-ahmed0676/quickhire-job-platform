package com.quickhire.dao;

import com.quickhire.model.ProgressUpdate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgressUpdateDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private ProgressUpdate mapRow(ResultSet rs) throws SQLException {
        ProgressUpdate update = new ProgressUpdate();
        update.setUpdateId(rs.getInt("updateId"));
        update.setJobId(rs.getInt("jobId"));
        update.setNote(rs.getString("note"));
        update.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        return update;
    }

    public int insertUpdate(ProgressUpdate update) throws SQLException {
        String sql = "INSERT INTO ProgressUpdates (jobId, note) VALUES (?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, update.getJobId());
            ps.setString(2, update.getNote());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("Failed to get generated updateId");
        }
    }

    public List<ProgressUpdate> getUpdatesByJobId(int jobId) throws SQLException {
        String sql = "SELECT * FROM ProgressUpdates WHERE jobId = ? ORDER BY timestamp DESC";
        List<ProgressUpdate> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }
}