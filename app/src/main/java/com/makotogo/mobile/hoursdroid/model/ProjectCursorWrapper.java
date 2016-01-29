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