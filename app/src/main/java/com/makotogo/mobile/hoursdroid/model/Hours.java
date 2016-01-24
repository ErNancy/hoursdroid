package com.makotogo.mobile.hoursdroid.model;

import com.makotogo.mobile.framework.ModelObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sperry on 1/12/16.
 */
public class Hours implements ModelObject, Serializable {
    // TODO: add fields and junk
    private Integer mId;
    private Date mBegin;
    private Date mEnd;
    private Long mBreak;
    private String mDescription;
    private Boolean mDeleted;
    private Date mWhenCreated;
    private Job mJob;
    private Project mProject;

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public Date getBegin() {
        return mBegin;
    }

    public void setBegin(Date begin) {
        mBegin = begin;
    }

    public Date getEnd() {
        return mEnd;
    }

    public void setEnd(Date end) {
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

    public Date getWhenCreated() {
        return mWhenCreated;
    }

    public void setWhenCreated(Date whenCreated) {
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
