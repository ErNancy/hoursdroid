package com.makotogo.mobile.hoursdroid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.makotogo.mobile.hoursdroid.model.DataStore;
import com.makotogo.mobile.hoursdroid.model.Job;

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
    public static final String PREFS_KEY_REPORT_SUMMARY_LAST_SELECTED_JOB_ID = "PREFS_KEY_REPORT_SUMMARY_LAST_SELECTED_JOB_ID";

    private static ApplicationOptions mInstance;
    private Context mContext;

    protected ApplicationOptions(Context context) {
        mContext = context;
    }

    public static ApplicationOptions instance(Context context) {
        if (mInstance == null) {
            mInstance = new ApplicationOptions(context);
        }
        return mInstance;
    }

    public boolean showNotifications() {
        return showNotifications(false);
    }

    public boolean showNotifications(boolean defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getBoolean(PREFS_KEY_SHOW_NOTIFICATIONS, defaultValue);
    }

    public boolean showInactiveJobs() {
        return showInactiveJobs(true);
    }

    public boolean showInactiveJobs(boolean defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getBoolean(PREFS_KEY_SHOW_INACTIVE_JOBS, defaultValue);
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getInt(computeKeyForLastUsedProjectId(jobId), -100);
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        long beginDate = sharedPreferences.getLong(PREFS_KEY_REPORT_SUMMARY_BEGIN_DATE, (defaultBeginDate == null) ? null : defaultBeginDate.getTime());
        return new Date(beginDate);
    }

    public Date getReportSummaryEndDate(Date defaultEndDate) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        long endDate = sharedPreferences.getLong(PREFS_KEY_REPORT_SUMMARY_END_DATE, (defaultEndDate == null) ? null : defaultEndDate.getTime());
        return new Date(endDate);
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
    }

    public Job getReportSummaryLastSelectedJobId() {
        Job ret = Job.ALL_JOBS;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        int jobId = sharedPreferences.getInt(PREFS_KEY_REPORT_SUMMARY_LAST_SELECTED_JOB_ID, -238);
        if (jobId != -238) {
            ret = DataStore.instance(mContext).getJob(jobId);
        }
        return ret;
    }

    public void saveReportSummaryLastSelectedJobId(Job job) {
        if (job != null && !job.equals(Job.ALL_JOBS)) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putInt(PREFS_KEY_REPORT_SUMMARY_LAST_SELECTED_JOB_ID, job.getId());
            sharedPreferencesEditor.commit();
        }
    }

    public String getDateFormatString() {
        // TODO: externalize
        return "MM/dd/yyyy hh:mm a";
    }
}
