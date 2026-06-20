package com.quickhire.model;

import java.time.LocalDateTime;

public class JobProvider extends User {

    public JobProvider() {
        super();
        this.role = "PROVIDER";
    }

    public JobProvider(int userId, String name, String email, String passwordHash,
                       String bio, String contactDetails, double averageRating,
                       LocalDateTime createdAt) {
        super(userId, name, email, passwordHash, "PROVIDER",
                bio, contactDetails, averageRating, createdAt);
    }

    @Override
    public String getDashboardTitle() { return "Job Provider Dashboard"; }

    @Override
    public String getRoleLabel() { return "Job Provider"; }
}