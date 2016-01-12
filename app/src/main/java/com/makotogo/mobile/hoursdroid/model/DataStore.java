package com.makotogo.mobile.hoursdroid.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.makotogo.mobile.hoursdroid.util.HoursDbHelper;
import com.makotogo.mobile.hoursdroid.util.SystemOptions;

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
     * List of Job objects.
     */
    List<Job> mJobs;

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
     * Returns a list of Job objects from the DB.
     *
     * @return List<Job> a list of Job objects, fresh from the DB.
     */
    public List<Job> getJobs() {
        if (mJobs == null) {
            mJobs = new ArrayList<>();
        } else {
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
        }
        return mJobs;
    }

    /**
     * C is for Create.
     *
     * @param job
     */
    public void addJob(Job job) {
        if (job.getWhenCreated() == null) {
            job.setWhenCreated(new Date());
        }
        ContentValues contentValues = getContentValues(job);
        // Do the INSERT
        getDatabase().insert(HoursDbSchema.JobTable.NAME, null, contentValues);
    }

    /**
     * U is for Update.
     *
     * @param job
     */
    public void updateJob(Job job) {
        ContentValues contentValues = getContentValues(job);
        String whereClause = HoursDbSchema.JobTable.Column.ID + " = " + Integer.toString(job.getId());
        // Do the UPDATE
        getDatabase().update(HoursDbSchema.JobTable.NAME, contentValues, whereClause, null);
        Log.d(TAG, "UPDATE: row ID = " + job.getId());
    }

    /**
     * D is for Delete (soft delete in this case).
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
        if (TEST_MODE) {
            mJobs = createTestData();
        } else {
            // Initialize the DB
            getDatabase();
        }
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
        if (SystemOptions.instance(mContext).showInactiveJobs()) {
            ret = null;
        } else {
            ret = HoursDbSchema.JobTable.Column.ACTIVE + " = 1";
        }
        return ret;
    }

    /**
     * Creates a bunch of fake data for testing.
     *
     * @return
     */
    private List<Job> createTestData() {
        List<Job> ret = new ArrayList<>();
        for (int aa = 0; aa < 100; aa++) {
            Job job = new Job();
            job.setName("Job Name #" + aa);
            ret.add(job);
        }
        return ret;
    }
}
