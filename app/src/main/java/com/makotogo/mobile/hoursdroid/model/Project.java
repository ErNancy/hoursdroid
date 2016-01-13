package com.makotogo.mobile.hoursdroid.model;

/**
 * Created by sperry on 1/12/16.
 */
public class Project {
    private Integer mId;
    private String mName;
    private String mDescription;
    private Job mJob;

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

}
