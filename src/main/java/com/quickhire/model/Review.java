package com.quickhire.model;

import java.time.LocalDateTime;

public class Review {
    private int reviewId;
    private int jobId;
    private int reviewerId;
    private int revieweeId;
    private int rating;       // 1–5
    private String comment;
    private LocalDateTime reviewDate;

    // Transient
    private String reviewerName;
    private String revieweeName;

    public Review() {}

    public Review(int reviewId, int jobId, int reviewerId, int revieweeId,
                  int rating, String comment, LocalDateTime reviewDate) {
        this.reviewId   = reviewId;
        this.jobId      = jobId;
        this.reviewerId = reviewerId;
        this.revieweeId = revieweeId;
        this.rating     = rating;
        this.comment    = comment;
        this.reviewDate = reviewDate;
    }

    public int getReviewId()                    { return reviewId; }
    public void setReviewId(int id)             { this.reviewId = id; }
    public int getJobId()                       { return jobId; }
    public void setJobId(int jobId)             { this.jobId = jobId; }
    public int getReviewerId()                  { return reviewerId; }
    public void setReviewerId(int id)           { this.reviewerId = id; }
    public int getRevieweeId()                  { return revieweeId; }
    public void setRevieweeId(int id)           { this.revieweeId = id; }
    public int getRating()                      { return rating; }
    public void setRating(int rating)           { this.rating = rating; }
    public String getComment()                  { return comment; }
    public void setComment(String comment)      { this.comment = comment; }
    public LocalDateTime getReviewDate()        { return reviewDate; }
    public void setReviewDate(LocalDateTime d)  { this.reviewDate = d; }
    public String getReviewerName()             { return reviewerName; }
    public void setReviewerName(String name)    { this.reviewerName = name; }
    public String getRevieweeName()             { return revieweeName; }
    public void setRevieweeName(String name)    { this.revieweeName = name; }
}