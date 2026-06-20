package com.quickhire.model;

import java.time.LocalDateTime;

public class MicroJob {

    // Job lifecycle constants (use these everywhere — never raw strings)
    public static final String STATUS_OPEN            = "OPEN";
    public static final String STATUS_IN_PROGRESS     = "IN_PROGRESS";
    public static final String STATUS_PENDING_PAYMENT = "PENDING_PAYMENT";
    public static final String STATUS_SETTLED         = "SETTLED";
    public static final String STATUS_CANCELLED       = "CANCELLED";

    private int jobId;
    private int providerId;
    private String title;
    private String category;
    private String description;
    private String location;
    private double hourlyRate;
    private String status;
    private LocalDateTime postedDate;

    // Transient (not stored, populated by JOIN) — provider name for display
    private String providerName;

    public MicroJob() { this.status = STATUS_OPEN; }

    public MicroJob(int jobId, int providerId, String title, String category,
                    String description, String location, double hourlyRate,
                    String status, LocalDateTime postedDate) {
        this.jobId = jobId;
        this.providerId = providerId;
        this.title = title;
        this.category = category;
        this.description = description;
        this.location = location;
        this.hourlyRate = hourlyRate;
        this.status = status;
        this.postedDate = postedDate;
    }

    // Business rule: can a job be edited?
    public boolean isEditable() {
        return STATUS_OPEN.equals(this.status);
    }

    // Business rule: can a seeker apply?
    public boolean isOpen() {
        return STATUS_OPEN.equals(this.status);
    }

    // Getters & Setters
    public int getJobId()                       { return jobId; }
    public void setJobId(int jobId)             { this.jobId = jobId; }
    public int getProviderId()                  { return providerId; }
    public void setProviderId(int id)           { this.providerId = id; }
    public String getTitle()                    { return title; }
    public void setTitle(String title)          { this.title = title; }
    public String getCategory()                 { return category; }
    public void setCategory(String category)    { this.category = category; }
    public String getDescription()              { return description; }
    public void setDescription(String d)        { this.description = d; }
    public String getLocation()                 { return location; }
    public void setLocation(String location)    { this.location = location; }
    public double getHourlyRate()               { return hourlyRate; }
    public void setHourlyRate(double rate)      { this.hourlyRate = rate; }
    public String getStatus()                   { return status; }
    public void setStatus(String status)        { this.status = status; }
    public LocalDateTime getPostedDate()        { return postedDate; }
    public void setPostedDate(LocalDateTime d)  { this.postedDate = d; }
    public String getProviderName()             { return providerName; }
    public void setProviderName(String name)    { this.providerName = name; }

    @Override
    public String toString() {
        return "[" + jobId + "] " + title + " (" + category + ") — " + status;
    }
}