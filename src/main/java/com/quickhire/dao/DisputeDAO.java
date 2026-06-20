package com.quickhire.dao;

import com.quickhire.model.Dispute;
import java.sql.*;

public class DisputeDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Dispute mapRow(ResultSet rs) throws SQLException {
        Dispute d = new Dispute();
        d.setDisputeId(rs.getInt("disputeId"));
        d.setInvoiceId(rs.getInt("invoiceId"));
        d.setReason(rs.getString("reason"));
        d.setRaisedDate(rs.getTimestamp("raisedDate").toLocalDateTime());
        d.setResolutionStatus(rs.getString("resolutionStatus"));
        d.setRoundCount(rs.getInt("roundCount"));
        d.setRaisedBy(rs.getString("raisedBy"));
        d.setLastChallenge(rs.getString("lastChallenge"));
        double proposed = rs.getDouble("proposedAmount");
        d.setProposedAmount(rs.wasNull() ? null : proposed);
        return d;
    }

    public int insertDispute(Dispute dispute) throws SQLException {
        String sql = "INSERT INTO Disputes " +
                "(invoiceId, reason, resolutionStatus, roundCount, raisedBy) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, dispute.getInvoiceId());
            ps.setString(2, dispute.getReason());
            ps.setString(3, dispute.getResolutionStatus());
            ps.setInt(4, dispute.getRoundCount());
            ps.setString(5, dispute.getRaisedBy());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("Failed to get generated disputeId");
        }
    }

    /** Seeker challenges: sets status=CHALLENGED, stores their counter-argument */
    public void challengeDispute(int disputeId, String challenge,
                                 double proposedAmount) throws SQLException {
        String sql = "UPDATE Disputes SET resolutionStatus = ?, lastChallenge = ?, " +
                "proposedAmount = ?, roundCount = roundCount + 1 " +
                "WHERE disputeId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, Dispute.STATUS_CHALLENGED);
            ps.setString(2, challenge);
            ps.setDouble(3, proposedAmount);
            ps.setInt(4, disputeId);
            ps.executeUpdate();
        }
    }

    /** Provider re-disputes: sets status=RE_DISPUTED, updates reason */
    public void reDispute(int disputeId, String newReason) throws SQLException {
        String sql = "UPDATE Disputes SET resolutionStatus = ?, reason = ?, " +
                "roundCount = roundCount + 1 WHERE disputeId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, Dispute.STATUS_RE_DISPUTED);
            ps.setString(2, newReason);
            ps.setInt(3, disputeId);
            ps.executeUpdate();
        }
    }

    /** Mark dispute resolved */
    public void resolveDispute(int disputeId) throws SQLException {
        String sql = "UPDATE Disputes SET resolutionStatus = ? WHERE disputeId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, Dispute.STATUS_RESOLVED);
            ps.setInt(2, disputeId);
            ps.executeUpdate();
        }
    }

    public Dispute getDisputeByInvoiceId(int invoiceId) throws SQLException {
        String sql = "SELECT * FROM Disputes WHERE invoiceId = ? " +
                "ORDER BY raisedDate DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        }
    }

    public Dispute getDisputeById(int disputeId) throws SQLException {
        String sql = "SELECT * FROM Disputes WHERE disputeId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, disputeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        }
    }
}