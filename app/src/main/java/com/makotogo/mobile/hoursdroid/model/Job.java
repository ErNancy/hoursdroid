package com.makotogo.mobile.hoursdroid.model;

import com.makotogo.mobile.framework.ModelObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sperry on 12/30/15.
 */
public class Job implements ModelObject, Serializable {

    public static final Job ALL_JOBS = new Job("All", "Selects All Jobs");

    private Integer mId;
    private String mName;
    private String mDescription;
    private Float mRate;
    private Boolean mActive;
    private Date mWhenCreated;
    private Boolean mContainsActiveHours;

    public Job() {
        // Nothing to do
    }

    public Job(String name, String description) {
        mName = name;
        mDescription = description;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Float getRate() {
        return mRate;
    }

    public void setRate(Float rate) {
        mRate = rate;
    }

    public Boolean isActive() {
        return mActive;
    }

    public void setActive(Boolean active) {
        mActive = active;
    }

    public Date getWhenCreated() {
        return mWhenCreated;
    }

    public void setWhenCreated(Date whenCreated) {
        mWhenCreated = whenCreated;
    }

    public Boolean isContainsActiveHours() {
        return mContainsActiveHours;
    }

    public void setContainsActiveHours(Boolean value) {
        mContainsActiveHours = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Job job = (Job) o;

        if (!getId().equals(job.getId())) return false;
        if (!getName().equals(job.getName())) return false;
        if (getDescription() != null ? !getDescription().equals(job.getDescription()) : job.getDescription() != null)
            return false;
        if (getRate() != null ? !getRate().equals(job.getRate()) : job.getRate() != null)
            return false;
        if (mActive != null ? !mActive.equals(job.mActive) : job.mActive != null) return false;
        return !(getWhenCreated() != null ? !getWhenCreated().equals(job.getWhenCreated()) : job.getWhenCreated() != null);

    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getRate() != null ? getRate().hashCode() : 0);
        result = 31 * result + (mActive != null ? mActive.hashCode() : 0);
        result = 31 * result + (getWhenCreated() != null ? getWhenCreated().hashCode() : 0);
        return result;
    }
}
