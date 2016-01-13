package com.makotogo.mobile.hoursdroid.model;

/**
 * Created by sperry on 1/12/16.
 */
public class Hours {
    // TODO: add fields and junk
    private Integer mId;
    private Long mBegin;
    private Long mEnd;
    private Long mBreak;
    private String mDescription;
    private Boolean mDeleted;
    private Long mWhenCreated;
    private Job mJob;
    private Project mProject;

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public Long getBegin() {
        return mBegin;
    }

    public void setBegin(Long begin) {
        mBegin = begin;
    }

    public Long getEnd() {
        return mEnd;
    }

    public void setEnd(Long end) {
        mEnd = end;
    }

    public Long getBreak() {
        return mBreak;
    }

    public void setBreak(Long aBreak) {
        mBreak = aBreak;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Boolean isDeleted() {
        return mDeleted;
    }

    public void setDeleted(Boolean deleted) {
        mDeleted = deleted;
    }

    public Long getWhenCreated() {
        return mWhenCreated;
    }

    public void setWhenCreated(Long whenCreated) {
        mWhenCreated = whenCreated;
    }

    public Job getJob() {
        return mJob;
    }

    public void setJob(Job job) {
        mJob = job;
    }

    public Project getProject() {
        return mProject;
    }

    public void setProject(Project project) {
        mProject = project;
    }
}
