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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hours hours = (Hours) o;

        if (!getId().equals(hours.getId())) return false;
        if (!getBegin().equals(hours.getBegin())) return false;
        if (getEnd() != null ? !getEnd().equals(hours.getEnd()) : hours.getEnd() != null)
            return false;
        if (getBreak() != null ? !getBreak().equals(hours.getBreak()) : hours.getBreak() != null)
            return false;
        if (getDescription() != null ? !getDescription().equals(hours.getDescription()) : hours.getDescription() != null)
            return false;
        if (mDeleted != null ? !mDeleted.equals(hours.mDeleted) : hours.mDeleted != null)
            return false;
        if (getWhenCreated() != null ? !getWhenCreated().equals(hours.getWhenCreated()) : hours.getWhenCreated() != null)
            return false;
        if (!getJob().equals(hours.getJob())) return false;
        return getProject().equals(hours.getProject());

    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getBegin().hashCode();
        result = 31 * result + (getEnd() != null ? getEnd().hashCode() : 0);
        result = 31 * result + (getBreak() != null ? getBreak().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (mDeleted != null ? mDeleted.hashCode() : 0);
        result = 31 * result + (getWhenCreated() != null ? getWhenCreated().hashCode() : 0);
        result = 31 * result + getJob().hashCode();
        result = 31 * result + getProject().hashCode();
        return result;
    }
}
