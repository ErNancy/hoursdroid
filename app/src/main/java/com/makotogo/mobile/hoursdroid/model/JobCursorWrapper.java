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
 * Created by sperry on 1/6/16.
 */
public class JobCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public JobCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Job getJob() {
        int id = getInt(getColumnIndexOrThrow(HoursDbSchema.JobTable.Column.ID));
        String name = getString(getColumnIndexOrThrow(HoursDbSchema.JobTable.Column.NAME));
        String description = getString(getColumnIndexOrThrow(HoursDbSchema.JobTable.Column.DESCRIPTION));
        boolean active = (getInt(getColumnIndexOrThrow(HoursDbSchema.JobTable.Column.ACTIVE)) == 1) ? true : false;
        Date whenCreated = new Date(getInt(getColumnIndexOrThrow(HoursDbSchema.JobTable.Column.WHEN_CREATED)));

        Job ret = new Job();
        ret.setId(id);
        ret.setName(name);
        ret.setDescription(description);
        ret.setActive(active);
        ret.setWhenCreated(whenCreated);
        return ret;
    }
}
