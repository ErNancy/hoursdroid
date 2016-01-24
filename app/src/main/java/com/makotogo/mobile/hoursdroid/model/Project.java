package com.makotogo.mobile.hoursdroid.model;

import com.makotogo.mobile.framework.ModelObject;

import java.io.Serializable;

/**
 * Created by sperry on 1/12/16.
 */
public class Project implements ModelObject, Serializable {

    public static final String DEFAULT_PROJECT_NAME = "Default";
    public static final String DEFAULT_PROJECT_DESCRIPTION = "The Default Project";

    private Integer mId;
    private String mName;
    private String mDescription;
    private Job mJob;
    private Boolean mDefaultForJob;

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

    public Job getJob() {
        return mJob;
    }

    public void setJob(Job job) {
        mJob = job;
    }

    public Boolean getDefaultForJob() {
        return mDefaultForJob;
    }

    public void setDefaultForJob(Boolean defaultForJob) {
        mDefaultForJob = defaultForJob;
    }

}
