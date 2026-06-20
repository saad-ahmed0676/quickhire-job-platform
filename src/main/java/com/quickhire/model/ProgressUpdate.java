package com.quickhire.model;

import java.time.LocalDateTime;

public class ProgressUpdate {
    private int updateId;
    private int jobId;
    private String note;
    private LocalDateTime timestamp;

    public ProgressUpdate() {}
    public ProgressUpdate(int updateId, int jobId, String note, LocalDateTime timestamp) {
        this.updateId  = updateId;
        this.jobId     = jobId;
        this.note      = note;
        this.timestamp = timestamp;
    }

    public int getUpdateId()                    { return updateId; }
    public void setUpdateId(int id)             { this.updateId = id; }
    public int getJobId()                       { return jobId; }
    public void setJobId(int jobId)             { this.jobId = jobId; }
    public String getNote()                     { return note; }
    public void setNote(String note)            { this.note = note; }
    public LocalDateTime getTimestamp()         { return timestamp; }
    public void setTimestamp(LocalDateTime t)   { this.timestamp = t; }
}