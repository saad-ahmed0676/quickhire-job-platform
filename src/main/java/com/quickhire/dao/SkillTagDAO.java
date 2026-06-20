package com.quickhire.dao;

import com.quickhire.model.SkillTag;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkillTagDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public void insertTag(SkillTag tag) throws SQLException {
        String sql = "INSERT INTO SkillTags (userId, tagName) VALUES (?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, tag.getUserId());
            ps.setString(2, tag.getTagName());
            ps.executeUpdate();
        }
    }

    public List<SkillTag> getTagsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM SkillTags WHERE userId = ?";
        List<SkillTag> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new SkillTag(
                        rs.getInt("tagId"),
                        rs.getInt("userId"),
                        rs.getString("tagName")
                ));
            }
        }
        return list;
    }

    public void deleteAllTagsForUser(int userId) throws SQLException {
        String sql = "DELETE FROM SkillTags WHERE userId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }
}