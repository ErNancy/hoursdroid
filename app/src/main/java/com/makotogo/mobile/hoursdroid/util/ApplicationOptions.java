package com.makotogo.mobile.hoursdroid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Date;

/**
 * Created by sperry on 1/9/16.
 */
public class ApplicationOptions {

    public static final String PREFS_KEY_SHOW_INACTIVE_JOBS = "PREFS_KEY_SHOW_INACTIVE_JOBS";
    public static final String PREFS_KEY_SHOW_NOTIFICATIONS = "PREFS_KEY_SHOW_NOTIFICATIONS";
    public static final String PREFS_KEY_LAST_USED_PROJECT_ID = "PREFS_KEY_LAST_USED_PROJECT_ID";
    public static final String PREFS_KEY_REPORT_SUMMARY_BEGIN_DATE = "PREFS_KEY_REPORT_SUMMARY_BEGIN_DATE";
    public static final String PREFS_KEY_REPORT_SUMMARY_END_DATE = "PREFS_KEY_REPORT_SUMMARY_END_DATE";

    private static ApplicationOptions mInstance;
    private Context mContext;
    private Boolean mShowNotifications;
    private Boolean mShowInactiveJobs;
    private Integer mLastUsedProjectId;
    private Date mReportSummaryBeginDate;
    private Date mReportSummaryEndDate;

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

    public Date getReportSummaryBeginDate(Date defaultBeginDate) {
        if (mReportSummaryBeginDate == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            long beginDate = sharedPreferences.getLong(PREFS_KEY_REPORT_SUMMARY_BEGIN_DATE, (defaultBeginDate == null) ? null : defaultBeginDate.getTime());
            mReportSummaryBeginDate = new Date(beginDate);
        }
        return mReportSummaryBeginDate;
    }

    public Date getReportSummaryEndDate(Date defaultEndDate) {
        if (mReportSummaryEndDate == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            long endDate = sharedPreferences.getLong(PREFS_KEY_REPORT_SUMMARY_END_DATE, (defaultEndDate == null) ? null : defaultEndDate.getTime());
            mReportSummaryEndDate = new Date(endDate);
        }
        return mReportSummaryEndDate;
    }

    public void saveReportSummaryBeginDate(Date beginDate) {
        // Sanity check
        if (beginDate == null) {
            throw new RuntimeException("Begin Date cannot be null!");
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putLong(PREFS_KEY_REPORT_SUMMARY_BEGIN_DATE, beginDate.getTime());
        sharedPreferencesEditor.commit();
        mReportSummaryBeginDate = beginDate;
    }

    public void saveReportSummaryEndDate(Date endDate) {
        // Sanity check
        if (endDate == null) {
            throw new RuntimeException("End Date cannot be null!");
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putLong(PREFS_KEY_REPORT_SUMMARY_END_DATE, endDate.getTime());
        sharedPreferencesEditor.commit();
        mReportSummaryEndDate = endDate;
    }

    public String getDateFormatString() {
        // TODO: externalize
        return "MM/dd/yyyy hh:mm a";
    }
}
