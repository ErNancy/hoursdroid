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

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Created by sperry on 1/16/16.
 */
public class ProjectCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public ProjectCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Project getProject() {
        Project ret = new Project();
        int id = getInt(getColumnIndexOrThrow(HoursDbSchema.ProjectTable.Column.ID));
        String description = getString(getColumnIndexOrThrow(HoursDbSchema.ProjectTable.Column.DESCRIPTION));
        String name = getString(getColumnIndexOrThrow(HoursDbSchema.ProjectTable.Column.NAME));
        int jobId = getInt(getColumnIndexOrThrow(HoursDbSchema.ProjectTable.Column.JOB_ID));
        Job job = DataStore.instance().getJob(jobId);
        boolean defaultForJob = (getInt(getColumnIndexOrThrow(HoursDbSchema.ProjectTable.Column.DEFAULT_FOR_JOB)) == 1) ? true : false;

        ret.setId(id);
        ret.setName(name);
        ret.setDescription(description);
        ret.setJob(job);
        ret.setDefaultForJob(defaultForJob);
        return ret;
    }
}
