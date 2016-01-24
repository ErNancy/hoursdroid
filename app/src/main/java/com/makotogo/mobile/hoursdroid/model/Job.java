package com.makotogo.mobile.hoursdroid.model;

import com.makotogo.mobile.framework.ModelObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sperry on 12/30/15.
 */
public class Job implements ModelObject, Serializable {

    private Integer mId;
    private String mName;
    private String mDescription;
    private Float mRate;
    private Boolean mActive;
    private Date mWhenCreated;
    private Boolean mContainsActiveHours;

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
}
