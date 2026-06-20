package com.quickhire.service;

import com.quickhire.dao.UserDAO;
import com.quickhire.dao.SkillTagDAO;
import com.quickhire.model.*;
import java.security.MessageDigest;
import java.util.List;

public class UserService {

    private final UserDAO userDAO       = new UserDAO();
    private final SkillTagDAO skillTagDAO = new SkillTagDAO();

    // UC01 — Register new user
    public User registerUser(String name, String email, String password, String role)
            throws Exception {
        // Input validation
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Name cannot be empty.");
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Invalid email address.");
        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        if (!role.equals("SEEKER") && !role.equals("PROVIDER"))
            throw new IllegalArgumentException("Role must be SEEKER or PROVIDER.");

        // Check if email already exists
        if (userDAO.getUserByEmail(email) != null)
            throw new Exception("An account with this email already exists.");

        String hash = hashPassword(password);

        User user;
        if ("SEEKER".equals(role)) {
            user = new JobSeeker();
        } else {
            user = new JobProvider();
        }
        user.setName(name.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPasswordHash(hash);
        user.setRole(role);

        int generatedId = userDAO.insertUser(user);
        user.setUserId(generatedId);
        return user;
    }

    // Login
    public User loginUser(String email, String password) throws Exception {
        if (email == null || password == null)
            throw new IllegalArgumentException("Email and password required.");

        User user = userDAO.getUserByEmail(email.trim().toLowerCase());
        if (user == null)
            throw new Exception("No account found with this email.");
        if (!user.getPasswordHash().equals(hashPassword(password)))
            throw new Exception("Incorrect password.");

        // Load skill tags if seeker
        if (user instanceof JobSeeker) {
            List<SkillTag> tags = skillTagDAO.getTagsByUserId(user.getUserId());
            ((JobSeeker) user).setSkillTags(tags);
        }
        return user;
    }

    // UC11 — Update profile
    public void updateProfile(int userId, String bio, String contactDetails,
                              List<String> skillTagNames) throws Exception {
        if (bio != null && bio.length() > 500)
            throw new IllegalArgumentException("Bio cannot exceed 500 characters.");

        userDAO.updateUserProfile(userId, bio, contactDetails);

        // Refresh skill tags for seekers
        skillTagDAO.deleteAllTagsForUser(userId);
        if (skillTagNames != null) {
            for (String tagName : skillTagNames) {
                if (!tagName.trim().isEmpty()) {
                    SkillTag tag = new SkillTag(0, userId, tagName.trim());
                    skillTagDAO.insertTag(tag);
                }
            }
        }
    }

    public User getUserById(int userId) throws Exception {
        return userDAO.getUserById(userId);
    }

    // Simple SHA-256 hash — fine for academic project
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }
}