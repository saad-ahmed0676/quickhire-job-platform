package com.quickhire.model;

public class SkillTag {
    private int tagId;
    private int userId;
    private String tagName;

    public SkillTag() {}
    public SkillTag(int tagId, int userId, String tagName) {
        this.tagId = tagId;
        this.userId = userId;
        this.tagName = tagName;
    }

    public int getTagId()              { return tagId; }
    public void setTagId(int tagId)    { this.tagId = tagId; }
    public int getUserId()             { return userId; }
    public void setUserId(int userId)  { this.userId = userId; }
    public String getTagName()         { return tagName; }
    public void setTagName(String t)   { this.tagName = t; }

    @Override
    public String toString() { return tagName; }
}