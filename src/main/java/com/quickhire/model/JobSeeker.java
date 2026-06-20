package com.quickhire.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JobSeeker extends User {

    private List<SkillTag> skillTags;

    public JobSeeker() {
        super();
        this.role = "SEEKER";
        this.skillTags = new ArrayList<>();
    }

    public JobSeeker(int userId, String name, String email, String passwordHash,
                     String bio, String contactDetails, double averageRating,
                     LocalDateTime createdAt) {
        super(userId, name, email, passwordHash, "SEEKER",
                bio, contactDetails, averageRating, createdAt);
        this.skillTags = new ArrayList<>();
    }

    @Override
    public String getDashboardTitle() { return "Job Seeker Dashboard"; }

    @Override
    public String getRoleLabel() { return "Job Seeker"; }

    public void addSkillTag(SkillTag tag)         { skillTags.add(tag); }
    public void removeSkillTag(SkillTag tag)       { skillTags.remove(tag); }
    public List<SkillTag> getSkillTags()           { return skillTags; }
    public void setSkillTags(List<SkillTag> tags)  { this.skillTags = tags; }
}