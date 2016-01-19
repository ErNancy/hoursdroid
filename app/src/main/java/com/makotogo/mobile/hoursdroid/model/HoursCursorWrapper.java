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
        int id = getInt(getColumnIndex(HoursDbSchema.HoursTable.Column.ID));
        Date begin = new Date(getInt(getColumnIndex(HoursDbSchema.HoursTable.Column.BEGIN)));
        Date end = new Date(getInt(getColumnIndex(HoursDbSchema.HoursTable.Column.END)));
        long breakDuration = getLong(getColumnIndex(HoursDbSchema.HoursTable.Column.BREAK));
        String description = getString(getColumnIndex(HoursDbSchema.HoursTable.Column.DESCRIPTION));
        boolean deleted = (getInt(getColumnIndex(HoursDbSchema.HoursTable.Column.DELETED)) == 1) ? true : false;
        Date whenCreated = new Date(getInt(getColumnIndex(HoursDbSchema.JobTable.Column.WHEN_CREATED)));
        int jobId = getInt(getColumnIndex(HoursDbSchema.HoursTable.Column.JOB_ID));
        Job job = DataStore.instance().getJob(jobId);
        int projectId = getInt(getColumnIndex(HoursDbSchema.HoursTable.Column.PROJECT_ID));
        Project project = DataStore.instance().getProject(projectId);

        Hours ret = new Hours();
        ret.setId(id);
        ret.setBegin(begin);
        ret.setEnd(end);
        ret.setBreak(breakDuration);
        ret.setDescription(description);
        ret.setDeleted(deleted);
        ret.setWhenCreated(whenCreated);
        ret.setJob(job);
        ret.setProject(project);
        return ret;
    }
}
