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
        Project ret = null;
        int id = getInt(getColumnIndex(HoursDbSchema.ProjectTable.Column.ID));
        String description = getString(getColumnIndex(HoursDbSchema.ProjectTable.Column.DESCRIPTION));
        String name = getString(getColumnIndex(HoursDbSchema.ProjectTable.Column.NAME));
        int jobId = getInt(getColumnIndex(HoursDbSchema.ProjectTable.Column.JOB_ID));
        Job job = DataStore.instance().getJob(jobId);

        ret.setId(id);
        ret.setName(name);
        ret.setDescription(description);
        ret.setJob(job);
        return ret;
    }
}
