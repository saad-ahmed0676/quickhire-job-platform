package com.quickhire.dao;

import com.quickhire.model.Payment;
import java.sql.*;

public class PaymentDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("paymentId"));
        payment.setInvoiceId(rs.getInt("invoiceId"));
        payment.setAmount(rs.getDouble("amount"));
        payment.setPaymentMethod(rs.getString("paymentMethod"));
        payment.setPaymentDate(rs.getTimestamp("paymentDate").toLocalDateTime());
        return payment;
    }

    public int insertPayment(Payment payment) throws SQLException {
        String sql = "INSERT INTO Payments (invoiceId, amount, paymentMethod) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, payment.getInvoiceId());
            ps.setDouble(2, payment.getAmount());
            ps.setString(3, payment.getPaymentMethod());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("Failed to get generated paymentId");
        }
    }

    public Payment getPaymentByInvoiceId(int invoiceId) throws SQLException {
        String sql = "SELECT * FROM Payments WHERE invoiceId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        }
    }
}