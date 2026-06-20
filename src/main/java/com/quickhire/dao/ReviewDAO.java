package com.quickhire.dao;

import com.quickhire.model.Review;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Review mapRow(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getInt("reviewId"));
        review.setJobId(rs.getInt("jobId"));
        review.setReviewerId(rs.getInt("reviewerId"));
        review.setRevieweeId(rs.getInt("revieweeId"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        review.setReviewDate(rs.getTimestamp("reviewDate").toLocalDateTime());
        try { review.setReviewerName(rs.getString("reviewerName")); }
        catch (SQLException ignored) {}
        try { review.setRevieweeName(rs.getString("revieweeName")); }
        catch (SQLException ignored) {}
        return review;
    }

    public int insertReview(Review review) throws SQLException {
        String sql = "INSERT INTO Reviews (jobId, reviewerId, revieweeId, rating, comment) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, review.getJobId());
            ps.setInt(2, review.getReviewerId());
            ps.setInt(3, review.getRevieweeId());
            ps.setInt(4, review.getRating());
            ps.setString(5, review.getComment());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("Failed to get generated reviewId");
        }
    }

    public boolean hasReviewed(int jobId, int reviewerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Reviews WHERE jobId = ? AND reviewerId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ps.setInt(2, reviewerId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public double calculateAverageRating(int revieweeId) throws SQLException {
        String sql = "SELECT AVG(CAST(rating AS FLOAT)) FROM Reviews WHERE revieweeId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, revieweeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
            return 0.0;
        }
    }

    public List<Review> getReviewsByRevieweeId(int revieweeId) throws SQLException {
        String sql = "SELECT r.*, u.name AS reviewerName " +
                "FROM Reviews r JOIN Users u ON r.reviewerId = u.userId " +
                "WHERE r.revieweeId = ? ORDER BY r.reviewDate DESC";
        List<Review> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, revieweeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }
}