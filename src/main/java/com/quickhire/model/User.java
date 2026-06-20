package com.quickhire.model;

import java.time.LocalDateTime;

public abstract class User {
    protected int userId;
    protected String name;
    protected String email;
    protected String passwordHash;
    protected String role;
    protected String bio;
    protected String contactDetails;
    protected double averageRating;
    protected LocalDateTime createdAt;

    public User() {}

    public User(int userId, String name, String email, String passwordHash,
                String role, String bio, String contactDetails,
                double averageRating, LocalDateTime createdAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.bio = bio;
        this.contactDetails = contactDetails;
        this.averageRating = averageRating;
        this.createdAt = createdAt;
    }

    public abstract String getDashboardTitle();
    public abstract String getRoleLabel();

    public boolean isProvider() { return "PROVIDER".equals(this.role); }
    public boolean isSeeker()   { return "SEEKER".equals(this.role); }

    public int getUserId()                    { return userId; }
    public void setUserId(int userId)         { this.userId = userId; }
    public String getName()                   { return name; }
    public void setName(String name)          { this.name = name; }
    public String getEmail()                  { return email; }
    public void setEmail(String email)        { this.email = email; }
    public String getPasswordHash()           { return passwordHash; }
    public void setPasswordHash(String ph)    { this.passwordHash = ph; }
    public String getRole()                   { return role; }
    public void setRole(String role)          { this.role = role; }
    public String getBio()                    { return bio; }
    public void setBio(String bio)            { this.bio = bio; }
    public String getContactDetails()         { return contactDetails; }
    public void setContactDetails(String cd)  { this.contactDetails = cd; }
    public double getAverageRating()          { return averageRating; }
    public void setAverageRating(double r)    { this.averageRating = r; }
    public LocalDateTime getCreatedAt()       { return createdAt; }
    public void setCreatedAt(LocalDateTime t) { this.createdAt = t; }

    @Override
    public String toString() { return name + " (" + role + ")"; }
}