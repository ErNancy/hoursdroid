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
    private Boolean mActive = Boolean.TRUE;
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

        if (mId != null ? !mId.equals(job.mId) : job.mId != null) return false;
        if (mName != null ? !mName.equals(job.mName) : job.mName != null) return false;
        if (mDescription != null ? !mDescription.equals(job.mDescription) : job.mDescription != null)
            return false;
        if (mRate != null ? !mRate.equals(job.mRate) : job.mRate != null) return false;
        if (mActive != null ? !mActive.equals(job.mActive) : job.mActive != null) return false;
        if (mWhenCreated != null ? !mWhenCreated.equals(job.mWhenCreated) : job.mWhenCreated != null)
            return false;
        return !(mContainsActiveHours != null ? !mContainsActiveHours.equals(job.mContainsActiveHours) : job.mContainsActiveHours != null);

    }

    @Override
    public int hashCode() {
        int result = mId != null ? mId.hashCode() : 0;
        result = 31 * result + (mName != null ? mName.hashCode() : 0);
        result = 31 * result + (mDescription != null ? mDescription.hashCode() : 0);
        result = 31 * result + (mRate != null ? mRate.hashCode() : 0);
        result = 31 * result + (mActive != null ? mActive.hashCode() : 0);
        result = 31 * result + (mWhenCreated != null ? mWhenCreated.hashCode() : 0);
        result = 31 * result + (mContainsActiveHours != null ? mContainsActiveHours.hashCode() : 0);
        return result;
    }
}
