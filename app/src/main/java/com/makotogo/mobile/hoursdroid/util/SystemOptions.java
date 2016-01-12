package com.makotogo.mobile.hoursdroid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by sperry on 1/9/16.
 */
public class SystemOptions {

    public static final String PREFS_KEY_SHOW_INACTIVE_JOBS = "PREFS_KEY_SHOW_INACTIVE_JOBS";
    public static final String PREFS_KEY_SHOW_NOTIFICATIONS = "PREFS_KEY_SHOW_NOTIFICATIONS";

    private static SystemOptions mInstance;
    private Context mContext;

    public static SystemOptions instance(Context context) {
        if (mInstance == null) {
            mInstance = new SystemOptions(context);
        }
        return mInstance;
    }

    private SystemOptions(Context context) {
        mContext = context;
    }

    private Boolean mShowNotifications;

    public boolean showNotifications() {
        boolean defaultValue = true;
        if (mShowNotifications == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            mShowNotifications = sharedPreferences.getBoolean(PREFS_KEY_SHOW_NOTIFICATIONS, defaultValue);
        }
        return mShowNotifications;
    }

    private Boolean mShowInactiveJobs;

    public boolean showInactiveJobs() {
        boolean defaultValue = false;
        if (mShowInactiveJobs == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            mShowInactiveJobs = sharedPreferences.getBoolean(PREFS_KEY_SHOW_INACTIVE_JOBS, defaultValue);
        }
        return mShowInactiveJobs;
    }

}
