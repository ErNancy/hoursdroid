package com.makotogo.mobile.hoursdroid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by sperry on 1/9/16.
 */
public class ApplicationOptions {

    public static final String PREFS_KEY_SHOW_INACTIVE_JOBS = "PREFS_KEY_SHOW_INACTIVE_JOBS";
    public static final String PREFS_KEY_SHOW_NOTIFICATIONS = "PREFS_KEY_SHOW_NOTIFICATIONS";
    public static final String PREFS_KEY_LAST_USED_PROJECT_ID = "PREFS_KEY_LAST_USED_PROJECT_ID";

    private static ApplicationOptions mInstance;
    private Context mContext;
    private Boolean mShowNotifications;
    private Boolean mShowInactiveJobs;
    private Integer mLastUsedProjectId;

    protected ApplicationOptions(Context context) {
        mContext = context;
    }

    public static ApplicationOptions instance(Context context) {
        if (mInstance == null) {
            mInstance = new ApplicationOptions(context);
        }
        return mInstance;
    }

    public boolean showNotifications(boolean defaultValue) {
        if (mShowNotifications == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            mShowNotifications = sharedPreferences.getBoolean(PREFS_KEY_SHOW_NOTIFICATIONS, defaultValue);
        }
        return mShowNotifications;
    }

    public boolean showInactiveJobs(boolean defaultValue) {
        if (mShowInactiveJobs == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            mShowInactiveJobs = sharedPreferences.getBoolean(PREFS_KEY_SHOW_INACTIVE_JOBS, defaultValue);
        }
        return mShowInactiveJobs;
    }

    private String computeKeyForLastUsedProjectId(int jobId) {
        return PREFS_KEY_LAST_USED_PROJECT_ID + "_JOBID_" + Integer.toString(jobId);
    }

    public int getLastUsedProjectId(int jobId) {
        // Sanity check
        if (jobId <= 0) {
            // Complain. Loudly.
            throw new RuntimeException("Job ID must be greater than zero!");
        }
        if (mLastUsedProjectId == null) {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            mLastUsedProjectId = sharedPreferences.getInt(computeKeyForLastUsedProjectId(jobId), -100);
        }
        return mLastUsedProjectId;
    }

    public void saveLastUsedProjectId(int jobId, int lastUsedProjectId) {
        // Sanity check
        if (jobId <= 0) {
            // Complain. Loudly.
            throw new RuntimeException("Job ID must be greater than zero!");
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putInt(computeKeyForLastUsedProjectId(jobId), lastUsedProjectId);
        sharedPreferencesEditor.commit();
    }

}
