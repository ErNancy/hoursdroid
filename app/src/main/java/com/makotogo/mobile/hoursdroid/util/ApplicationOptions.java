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

    private static ApplicationOptions mInstance;
    private Context mContext;

    public static ApplicationOptions instance(Context context) {
        if (mInstance == null) {
            mInstance = new ApplicationOptions(context);
        }
        return mInstance;
    }

    protected ApplicationOptions(Context context) {
        mContext = context;
    }

    private Boolean mShowNotifications;

    public boolean showNotifications(boolean defaultValue) {
        if (mShowNotifications == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            mShowNotifications = sharedPreferences.getBoolean(PREFS_KEY_SHOW_NOTIFICATIONS, defaultValue);
        }
        return mShowNotifications;
    }

    private Boolean mShowInactiveJobs;

    public boolean showInactiveJobs(boolean defaultValue) {
        if (mShowInactiveJobs == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            mShowInactiveJobs = sharedPreferences.getBoolean(PREFS_KEY_SHOW_INACTIVE_JOBS, defaultValue);
        }
        return mShowInactiveJobs;
    }

}
