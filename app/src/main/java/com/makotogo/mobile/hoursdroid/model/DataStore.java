package com.makotogo.mobile.hoursdroid.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.makotogo.mobile.hoursdroid.util.ApplicationOptions;
import com.makotogo.mobile.hoursdroid.util.HoursDbHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class serves as the Data Store helper for the application.
 * It is a singleton, and should last as long as the application record.
 * However, because we do not want to leak any objects out of this class,
 * and we are never really sure if the application will go away or not,
 * we have to be careful about opening and closing the DB.
 * <p/>
 * Created by sperry on 12/30/15.
 */
public class DataStore {

    private static final String TAG = DataStore.class.getSimpleName();

    private static final boolean TEST_MODE = false;

    /**
     * The Singleton
     */
    private static DataStore mDataStore;

    /**
     * The ApplicationContext, retrieved from the Context object
     * passed in by the first caller.
     */
    private Context mContext;

    /**
     * Reference to the SQLiteDatabase.
     */
    private SQLiteDatabase mDatabase;

    public static DataStore instance() {
        if (mDataStore == null) {
            throw new RuntimeException("DataStore singleton not properly initialized!");
        }
        return mDataStore;
    }

    /**
     * Call this method to retrieve an instance of DataStore, which is a singleton.
     *
     * @param context The Context object from the caller.
     * @return DataStore - a reference to the DataStore singleton.
     */
    public static DataStore instance(Context context) {
        if (mDataStore == null) {
            mDataStore = new DataStore(context);
        }
        return mDataStore;
    }

    /**
     * Cleanup method. Called when the application thinks
     * it might be destroyed, so we do not leak a DB
     * reference.
     */
    public void close() {
        // Make sure we do not leak anything
        if (mDatabase != null && mDatabase.isOpen()) {
            Log.d(TAG, "Closing DataStore...");
            mDatabase.close();
        }
        mDatabase = null;
        // Ensure the Singleton will be recreated if necessary
        mDataStore = null;
    }

    /**
     * Constructor - private. This is a Singleton, so we do not want anybody
     * to be able to call the constructor but the class itself.
     *
     * @param context The Context of the first caller to retrieve the Singleton.
     */
    private DataStore(Context context) {
        mContext = context.getApplicationContext();
        // Initialize the DB
        getDatabase();
    }

    /**
     * Private getter for Database attribute. Performs lazy initialiation.
     *
     * @return SQLiteDatabase - a Writeable SQLiteDatabase instance.
     */
    private SQLiteDatabase getDatabase() {
        if (mDatabase == null || !mDatabase.isOpen()) {
            Log.d(TAG, "Creating/Opening DataStore...");
            mDatabase = new HoursDbHelper(mContext).getWritableDatabase();
            Log.d(TAG, "Done.");
        }
        return mDatabase;
    }

    /////////////////////////////////////////////////////////////////////////////////
    //                              J    O    B                                    //
    /////////////////////////////////////////////////////////////////////////////////
    /**
     * List of Job objects.
     */
    List<Job> mJobs = new ArrayList<>();

    /**
     * Returns a list of Job objects from the DB.
     *
     * @return List<Job> a list of Job objects, fresh from the DB.
     */
    public List<Job> getJobs() {
        mJobs.clear();
        // TODO: Reload from DB on background thread! DO NOT RUN THIS ON THE UI THREAD!
        // TODO: Use AsyncTask for this!
        String orderByClause = HoursDbSchema.JobTable.Column.ACTIVE + " desc, " + HoursDbSchema.JobTable.Column.NAME;
        String whereClause = computeGetJobsWhereClause();
        Cursor cursor = mDatabase.query(HoursDbSchema.JobTable.NAME,
                null,// select *
                whereClause,// computed
                null,// no WHERE args either
                null,// no GROUP BY
                null,// no HAVING either
                orderByClause// no ORDER BY
        );
        JobCursorWrapper cursorWrapper = new JobCursorWrapper(cursor);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                mJobs.add(cursorWrapper.getJob());
                cursorWrapper.moveToNext();
            }
        } finally {
            cursorWrapper.close();
        }
        return mJobs;
    }

    /**
     * C is for Create.
     *
     * @param job
     */
    public Job create(Job job) {
        Job ret = null;
        if (job.getWhenCreated() == null) {
            job.setWhenCreated(new Date());
        }
        ContentValues contentValues = getContentValues(job);
        // Do the INSERT
        long rowId = getDatabase().insert(HoursDbSchema.JobTable.NAME, null, contentValues);
        if (rowId != -1) {
            // For completeness
            job.setId((int) rowId);
            Project project = createDefaultProject(job);
            create(project);
            ret = job;
        }
        return ret;
    }

    /**
     * R is for Read.
     *
     * @param jobId The ID of the Job record to retrieve.
     *
     * @return Job
     */
    public Job getJob(int jobId) {
        Job ret = null;
        Cursor cursor = mDatabase.query(HoursDbSchema.JobTable.NAME,
                null,// select *
                HoursDbSchema.JobTable.Column.ID + "=" + jobId,// computed
                null,// no WHERE args either
                null,// no GROUP BY
                null,// no HAVING either
                null// no ORDER BY
        );
        JobCursorWrapper cursorWrapper = new JobCursorWrapper(cursor);
        try {
            if (cursorWrapper.moveToFirst()) {
                ret = cursorWrapper.getJob();
            }
        } finally {
            cursorWrapper.close();
        }
        return ret;
    }

    /**
     * U is for Update.
     *
     * @param job
     */
    public int update(Job job) {
        int numRowsUpdated = 0;
        ContentValues contentValues = getContentValues(job);
        String whereClause = HoursDbSchema.JobTable.Column.ID + " = " + Integer.toString(job.getId());
        // Do the UPDATE
        try {
            numRowsUpdated =
                    getDatabase().update(HoursDbSchema.JobTable.NAME, contentValues, whereClause, null);
            Log.d(TAG, "UPDATE: row ID = " + job.getId());
        } catch (SQLiteConstraintException e) {
            Log.e(TAG, "Update failed: ", e);
        }
        return numRowsUpdated;
    }

    /**
     * D is for Delete
     *
     * @param job
     */
    public void delete(Job job) {
        String whereClause = HoursDbSchema.JobTable.Column.ID + " = " + Integer.toString(job.getId());
        // Do the DELETE
        // TODO: check to see if the Job has TimeRecords associated with it. If so, then
        /// TODO: all we can do is mark it deactivated.
        getDatabase().delete(HoursDbSchema.JobTable.NAME, whereClause, null);
        Log.d(TAG, "DELETE: row ID = " + job.getId());
    }

    /**
     * Given the specified Job object, returns a ContentValues object suitable
     * for doing an INSERT/UPDATE to the DB.
     *
     * @param job The Job object to be inserted or updated in the DB.
     * @return ContentValues - a wrapper object used by SQLite to wrangle the
     * values into the DB.
     */
    private static ContentValues getContentValues(Job job) {
        ContentValues ret = new ContentValues();
        ret.put(HoursDbSchema.JobTable.Column.ID, job.getId());
        ret.put(HoursDbSchema.JobTable.Column.NAME, job.getName());
        ret.put(HoursDbSchema.JobTable.Column.DESCRIPTION, job.getDescription());
        ret.put(HoursDbSchema.JobTable.Column.RATE, job.getRate());
        ret.put(HoursDbSchema.JobTable.Column.ACTIVE, job.isActive());
        ret.put(HoursDbSchema.JobTable.Column.WHEN_CREATED, job.getWhenCreated().getTime());
        return ret;
    }

    private String computeGetJobsWhereClause() {
        String ret = null;
        // TODO: check the system prefs and see if we ignore inactive jobs
        if (ApplicationOptions.instance(mContext).showInactiveJobs(true)) {
            ret = null;
        } else {
            ret = HoursDbSchema.JobTable.Column.ACTIVE + " = 1";
        }
        return ret;
    }

    /////////////////////////////////////////////////////////////////////////////////
    //                  P     R     O     J     E     C    T                       //
    /////////////////////////////////////////////////////////////////////////////////

    private List<Project> mProjects = new ArrayList<>();

    public List<Project> getProjects(Job job) {
        mProjects.clear();
        // WHERE JOB_ID = job.getId()
        String whereClause = HoursDbSchema.ProjectTable.Column.JOB_ID + " = " + job.getId();
        Cursor cursor = mDatabase.query(HoursDbSchema.ProjectTable.NAME,
                null,// select *
                whereClause,
                null,// no WHERE args either
                null,// no GROUP BY
                null,// no HAVING either
                null// no ORDER BY
        );
        ProjectCursorWrapper cursorWrapper = new ProjectCursorWrapper(cursor);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                mProjects.add(cursorWrapper.getProject());
                cursorWrapper.moveToNext();
            }
        } finally {
            cursorWrapper.close();
        }
        return mProjects;
    }

    private Project createDefaultProject(Job job) {
        Project ret = new Project();
        ret.setJob(job);
        ret.setName(Project.DEFAULT_PROJECT_NAME);
        ret.setDescription(Project.DEFAULT_PROJECT_DESCRIPTION);
        ret.setDefaultForJob(true);
        return ret;
    }

    public Project getDefaultProject(Job job) {
        Project ret = null;
        // WHERE JOB_ID = job.getId() AND DEFAULT_FOR_JOB = 1 (true)
        String whereClause = HoursDbSchema.ProjectTable.Column.JOB_ID + " = " + job.getId() +
                " AND " + HoursDbSchema.ProjectTable.Column.DEFAULT_FOR_JOB + " = 1";
        Cursor cursor = mDatabase.query(HoursDbSchema.ProjectTable.NAME,
                null,// select *
                whereClause,
                null,// no WHERE args either
                null,// no GROUP BY
                null,// no HAVING either
                null// no ORDER BY
        );
        ProjectCursorWrapper cursorWrapper = new ProjectCursorWrapper(cursor);
        try {
            if (cursorWrapper.moveToFirst()) {
                ret = cursorWrapper.getProject();
            } else {
                // No default Project for the specified Job
                Log.w(TAG, "No default project for Job '" + job.getName() + " and ID = " + job.getId() + ".");
            }
        } finally {
            cursorWrapper.close();
        }
        return ret;
    }

    /**
     * R is for Read.
     *
     * @param projectId The ID of the Project record to retrieve.
     * @return Job
     */
    public Project getProject(int projectId) {
        Project ret = null;
        Cursor cursor = mDatabase.query(HoursDbSchema.JobTable.NAME,
                null,// select *
                HoursDbSchema.ProjectTable.Column.ID + "=" + projectId,// computed
                null,// no WHERE args either
                null,// no GROUP BY
                null,// no HAVING either
                null// no ORDER BY
        );
        ProjectCursorWrapper cursorWrapper = new ProjectCursorWrapper(cursor);
        try {
            if (cursorWrapper.moveToFirst()) {
                ret = cursorWrapper.getProject();
            }
        } finally {
            cursorWrapper.close();
        }
        return ret;
    }

    public void create(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Project object cannot be null!");
        }
        ContentValues contentValues = getContentValues(project);
        // Do the INSERT
        long rowId = getDatabase().insert(HoursDbSchema.ProjectTable.NAME, null, contentValues);
        Log.d(TAG, "Created project: " + project.getName() + " (id=" + rowId + ")");
    }

    private static ContentValues getContentValues(Project project) {
        ContentValues ret = new ContentValues();
        ret.put(HoursDbSchema.ProjectTable.Column.ID, project.getId());
        ret.put(HoursDbSchema.ProjectTable.Column.NAME, project.getName());
        ret.put(HoursDbSchema.ProjectTable.Column.DESCRIPTION, project.getDescription());
        ret.put(HoursDbSchema.ProjectTable.Column.JOB_ID, project.getJob().getId());
        ret.put(HoursDbSchema.ProjectTable.Column.DEFAULT_FOR_JOB, project.getDefaultForJob());
        return ret;
    }

    /////////////////////////////////////////////////////////////////////////////////
    //                       H     O     U     R     S                             //
    /////////////////////////////////////////////////////////////////////////////////

    private List<Hours> mHours = new ArrayList<>();

    public List<Hours> getHours(Job job) {
        mHours.clear();
        String orderByClause = HoursDbSchema.HoursTable.Column.BEGIN + " desc";
        String whereClause = computeGetHoursWhereClause(job);
        Cursor cursor = mDatabase.query(HoursDbSchema.HoursTable.NAME,
                null,// select *
                whereClause,// computed
                null,// no WHERE args either
                null,// no GROUP BY
                null,// no HAVING either
                orderByClause// no ORDER BY
        );
        HoursCursorWrapper cursorWrapper = new HoursCursorWrapper(cursor);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                mHours.add(cursorWrapper.getHours());
                cursorWrapper.moveToNext();
            }
        } finally {
            cursorWrapper.close();
        }
        return mHours;
    }

    private static ContentValues getContentValues(Hours hours) {
        ContentValues ret = new ContentValues();
        ret.put(HoursDbSchema.HoursTable.Column.ID, hours.getId());
        ret.put(HoursDbSchema.HoursTable.Column.BEGIN, hours.getBegin().getTime());
        ret.put(HoursDbSchema.HoursTable.Column.END, hours.getEnd().getTime());
        ret.put(HoursDbSchema.HoursTable.Column.BREAK, hours.getBreak());
        ret.put(HoursDbSchema.HoursTable.Column.DESCRIPTION, hours.getDescription());
        ret.put(HoursDbSchema.HoursTable.Column.DELETED, hours.isDeleted());
        ret.put(HoursDbSchema.HoursTable.Column.WHEN_CREATED, hours.getWhenCreated().getTime());
        ret.put(HoursDbSchema.HoursTable.Column.JOB_ID, hours.getJob().getId());
        ret.put(HoursDbSchema.HoursTable.Column.PROJECT_ID, hours.getProject().getId());
        return ret;
    }

    /**
     * C is for Create.
     *
     * @param hours
     */
    public Hours create(Hours hours) {
        Hours ret = null;
        if (hours.getWhenCreated() == null) {
            hours.setWhenCreated(new Date());
        }
        ContentValues contentValues = getContentValues(hours);
        // Do the INSERT
        long rowId = getDatabase().insert(HoursDbSchema.HoursTable.NAME, null, contentValues);
        if (rowId != -1) {
            // For completeness
            hours.setId((int) rowId);
            ret = hours;
        }
        return ret;
    }

    /**
     * U is for Update.
     *
     * @param hours
     */
    public int update(Hours hours) {
        int numRowsUpdated = 0;
        ContentValues contentValues = getContentValues(hours);
        String whereClause = HoursDbSchema.JobTable.Column.ID + " = " + Integer.toString(hours.getId());
        // Do the UPDATE
        try {
            numRowsUpdated =
                    getDatabase().update(HoursDbSchema.HoursTable.NAME, contentValues, whereClause, null);
            Log.d(TAG, "UPDATE: row ID = " + hours.getId());
        } catch (SQLiteConstraintException e) {
            Log.e(TAG, "Update failed: ", e);
        }
        return numRowsUpdated;
    }

    /**
     * D is for Delete
     *
     * @param hours
     */
    public void delete(Hours hours) {
        String whereClause = HoursDbSchema.HoursTable.Column.ID + " = " + Integer.toString(hours.getId());
        // Do the DELETE
        getDatabase().delete(HoursDbSchema.JobTable.NAME, whereClause, null);
        Log.d(TAG, "DELETE: row ID = " + hours.getId());
    }

    private String computeGetHoursWhereClause(Job job) {
        String ret = null;
        //
        ret = HoursDbSchema.HoursTable.Column.JOB_ID + " = " + job.getId();
        return ret;
    }

}
