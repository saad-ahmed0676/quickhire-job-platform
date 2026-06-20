package com.quickhire.model;

import java.time.LocalDateTime;

public class JobApplication {

    public static final String STATUS_PENDING  = "PENDING";
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_REJECTED = "REJECTED";

    private int applicationId;
    private int jobId;
    private int seekerId;
    private String applicationNote;
    private LocalDateTime submissionDate;
    private String status;

    // Transient — populated by JOIN
    private String seekerName;
    private double seekerRating;

    // Extra fields populated by notification queries
    private String jobTitle;
    private String providerName;

    public String getJobTitle()              { return jobTitle; }
    public void setJobTitle(String t)        { this.jobTitle = t; }
    public String getProviderName()          { return providerName; }
    public void setProviderName(String n)    { this.providerName = n; }

    public JobApplication() { this.status = STATUS_PENDING; }

    public JobApplication(int applicationId, int jobId, int seekerId,
                          String applicationNote, LocalDateTime submissionDate,
                          String status) {
        this.applicationId = applicationId;
        this.jobId = jobId;
        this.seekerId = seekerId;
        this.applicationNote = applicationNote;
        this.submissionDate = submissionDate;
        this.status = status;
    }

    // Getters & Setters
    public int getApplicationId()                   { return applicationId; }
    public void setApplicationId(int id)            { this.applicationId = id; }
    public int getJobId()                           { return jobId; }
    public void setJobId(int jobId)                 { this.jobId = jobId; }
    public int getSeekerId()                        { return seekerId; }
    public void setSeekerId(int id)                 { this.seekerId = id; }
    public String getApplicationNote()              { return applicationNote; }
    public void setApplicationNote(String note)     { this.applicationNote = note; }
    public LocalDateTime getSubmissionDate()        { return submissionDate; }
    public void setSubmissionDate(LocalDateTime d)  { this.submissionDate = d; }
    public String getStatus()                       { return status; }
    public void setStatus(String status)            { this.status = status; }
    public String getSeekerName()                   { return seekerName; }
    public void setSeekerName(String name)          { this.seekerName = name; }
    public double getSeekerRating()                 { return seekerRating; }
    public void setSeekerRating(double r)           { this.seekerRating = r; }
}