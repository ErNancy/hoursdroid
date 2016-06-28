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

import java.util.Date;

/**
 * Created by sperry on 1/16/16.
 */
public class HoursCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public HoursCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Hours getHours() {
        int id = getInt(getColumnIndexOrThrow(HoursDbSchema.HoursTable.Column.ID));
        long millis = getLong(getColumnIndexOrThrow(HoursDbSchema.HoursTable.Column.BEGIN));
        Date begin = (millis > 0) ? new Date(millis) : null;
        millis = getLong(getColumnIndexOrThrow(HoursDbSchema.HoursTable.Column.END));
        Date end = (millis > 0) ? new Date(millis) : null;
        long breakDuration = getLong(getColumnIndexOrThrow(HoursDbSchema.HoursTable.Column.BREAK));
        String description = getString(getColumnIndexOrThrow(HoursDbSchema.HoursTable.Column.DESCRIPTION));
        boolean billed = (getInt(getColumnIndexOrThrow(HoursDbSchema.HoursTable.Column.BILLED)) == 1) ? true : false;
        millis = getLong(getColumnIndexOrThrow(HoursDbSchema.JobTable.Column.WHEN_CREATED));
        Date whenCreated = (millis > 0) ? new Date(millis) : null;
        int jobId = getInt(getColumnIndexOrThrow(HoursDbSchema.HoursTable.Column.JOB_ID));
        Job job = DataStore.instance().getJob(jobId);
        int projectId = getInt(getColumnIndexOrThrow(HoursDbSchema.HoursTable.Column.PROJECT_ID));
        Project project = DataStore.instance().getProject(projectId);

        Hours ret = new Hours();
        ret.setId(id);
        ret.setBegin(begin);
        ret.setEnd(end);
        ret.setBreak(breakDuration);
        ret.setDescription(description);
        ret.setWhenCreated(whenCreated);
        ret.setJob(job);
        ret.setProject(project);
        ret.setBilled(billed);
        return ret;
    }
}
