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
        int id = getInt(getColumnIndex(HoursDbSchema.JobTable.Column.ID));
        String name = getString(getColumnIndex(HoursDbSchema.JobTable.Column.NAME));
        String description = getString(getColumnIndex(HoursDbSchema.JobTable.Column.DESCRIPTION));
        boolean active = (getInt(getColumnIndex(HoursDbSchema.JobTable.Column.ACTIVE)) == 1) ? true : false;
        Date whenCreated = new Date(getInt(getColumnIndex(HoursDbSchema.JobTable.Column.WHEN_CREATED)));

        Job ret = new Job();
        ret.setId(id);
        ret.setName(name);
        ret.setDescription(description);
        ret.setActive(active);
        ret.setWhenCreated(whenCreated);
        return ret;
    }
}
