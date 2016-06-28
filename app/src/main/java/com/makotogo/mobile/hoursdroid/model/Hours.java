/*
 *     Copyright 2016 Makoto Consulting Group, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package com.makotogo.mobile.hoursdroid.model;

import com.makotogo.mobile.framework.ModelObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sperry on 1/12/16.
 */
public class Hours implements ModelObject, Serializable {

    private Integer mId;
    private Date mBegin;
    private Date mEnd;
    private Long mBreak;
    private String mDescription;
    //private Boolean mDeleted;// TODO: Remove this field
    private Date mWhenCreated;
    private Job mJob;
    private Project mProject;
    private Boolean mBilled = Boolean.FALSE;

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

    public Boolean isBilled() {
        return mBilled;
    }

    public void setBilled(Boolean billed) {
        mBilled = billed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hours hours = (Hours) o;

        if (mId != null ? !mId.equals(hours.mId) : hours.mId != null) return false;
        if (mBegin != null ? !mBegin.equals(hours.mBegin) : hours.mBegin != null) return false;
        if (mEnd != null ? !mEnd.equals(hours.mEnd) : hours.mEnd != null) return false;
        if (mBreak != null ? !mBreak.equals(hours.mBreak) : hours.mBreak != null) return false;
        if (mDescription != null ? !mDescription.equals(hours.mDescription) : hours.mDescription != null)
            return false;
        if (mWhenCreated != null ? !mWhenCreated.equals(hours.mWhenCreated) : hours.mWhenCreated != null)
            return false;
        if (mJob != null ? !mJob.equals(hours.mJob) : hours.mJob != null) return false;
        if (mProject != null ? !mProject.equals(hours.mProject) : hours.mProject != null)
            return false;
        return mBilled != null ? mBilled.equals(hours.mBilled) : hours.mBilled == null;

    }

    @Override
    public int hashCode() {
        int result = mId != null ? mId.hashCode() : 0;
        result = 31 * result + (mBegin != null ? mBegin.hashCode() : 0);
        result = 31 * result + (mEnd != null ? mEnd.hashCode() : 0);
        result = 31 * result + (mBreak != null ? mBreak.hashCode() : 0);
        result = 31 * result + (mDescription != null ? mDescription.hashCode() : 0);
        result = 31 * result + (mWhenCreated != null ? mWhenCreated.hashCode() : 0);
        result = 31 * result + (mJob != null ? mJob.hashCode() : 0);
        result = 31 * result + (mProject != null ? mProject.hashCode() : 0);
        result = 31 * result + (mBilled != null ? mBilled.hashCode() : 0);
        return result;
    }
}
