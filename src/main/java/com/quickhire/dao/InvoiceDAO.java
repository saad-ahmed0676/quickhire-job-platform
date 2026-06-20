package com.quickhire.dao;

import com.quickhire.model.Invoice;
import java.sql.*;
import java.time.LocalDateTime;

public class InvoiceDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Invoice mapRow(ResultSet rs) throws SQLException {
        Invoice inv = new Invoice();
        inv.setInvoiceId(rs.getInt("invoiceId"));
        inv.setJobId(rs.getInt("jobId"));
        inv.setHoursWorked(rs.getDouble("hoursWorked"));
        inv.setSupplyCost(rs.getDouble("supplyCost"));
        inv.setTotalAmount(rs.getDouble("totalAmount"));
        inv.setDateGenerated(rs.getTimestamp("dateGenerated").toLocalDateTime());
        inv.setConfirmedBySeeker(rs.getBoolean("confirmedBySeeker"));
        return inv;
    }

    /**
     * Inserts a new invoice, or updates the existing one if an invoice
     * already exists for this jobId. This prevents the UNIQUE KEY violation
     * when the seeker edits figures and re-generates.
     */
    public int saveInvoice(Invoice invoice) throws SQLException {
        // Check if an invoice already exists for this job
        Invoice existing = getInvoiceByJobId(invoice.getJobId());

        if (existing != null) {
            // UPDATE existing row
            String sql = "UPDATE Invoices SET hoursWorked = ?, supplyCost = ?, " +
                    "totalAmount = ?, confirmedBySeeker = ? WHERE invoiceId = ?";
            try (PreparedStatement ps = getConn().prepareStatement(sql)) {
                ps.setDouble(1, invoice.getHoursWorked());
                ps.setDouble(2, invoice.getSupplyCost());
                ps.setDouble(3, invoice.getTotalAmount());
                ps.setBoolean(4, invoice.isConfirmedBySeeker());
                ps.setInt(5, existing.getInvoiceId());
                ps.executeUpdate();
            }
            return existing.getInvoiceId();
        } else {
            // INSERT new row
            String sql = "INSERT INTO Invoices (jobId, hoursWorked, supplyCost, " +
                    "totalAmount, confirmedBySeeker) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = getConn().prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, invoice.getJobId());
                ps.setDouble(2, invoice.getHoursWorked());
                ps.setDouble(3, invoice.getSupplyCost());
                ps.setDouble(4, invoice.getTotalAmount());
                ps.setBoolean(5, invoice.isConfirmedBySeeker());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("Failed to get generated invoiceId");
            }
        }
    }

    // Keep insertInvoice as a thin wrapper so InvoiceService compiles unchanged
    public int insertInvoice(Invoice invoice) throws SQLException {
        return saveInvoice(invoice);
    }

    public Invoice getInvoiceById(int invoiceId) throws SQLException {
        String sql = "SELECT * FROM Invoices WHERE invoiceId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        }
    }

    public Invoice getInvoiceByJobId(int jobId) throws SQLException {
        String sql = "SELECT * FROM Invoices WHERE jobId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        }
    }

    public void setConfirmedBySeeker(int invoiceId, boolean confirmed) throws SQLException {
        String sql = "UPDATE Invoices SET confirmedBySeeker = ? WHERE invoiceId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setBoolean(1, confirmed);
            ps.setInt(2, invoiceId);
            ps.executeUpdate();
        }
    }
}