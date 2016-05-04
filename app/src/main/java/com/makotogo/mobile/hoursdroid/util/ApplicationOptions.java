package com.makotogo.mobile.hoursdroid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

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
    public static final String PREFS_KEY_SHOW_BILLED_HOURS_RECORDS = "PREFS_KEY_SHOW_BILLED_HOURS_RECORDS";
    public static final String PREFS_KEY_ROUNDING = "PREFS_KEY_ROUNDING";
    private static final String TAG = ApplicationOptions.class.getSimpleName();
    private static ApplicationOptions mInstance;
    private Context mContext;
    private Boolean mShowNotifications;
    private Boolean mShowInactiveJobs;
    private Boolean mShowBilledHoursRecords;
    private Integer mRounding;
    // Application-derived values (not available through Preferences)
    private Integer mLastUsedProjectId;
    private Date mReportSummaryBeginDate;
    private Date mReportSummaryEndDate;
    private Job mReportSummaryLastSelectedJob;

    protected ApplicationOptions(Context context) {
        mContext = context;
    }

    public static ApplicationOptions instance(Context context) {
        if (mInstance == null) {
            mInstance = new ApplicationOptions(context);
        }
        return mInstance;
    }

    public static void release() {
        mInstance = null;
    }

    public boolean showNotifications() {
        if (mShowNotifications == null) {
            mShowNotifications = showNotifications(false);
        }
        return mShowNotifications;
    }

    private boolean showNotifications(boolean defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getBoolean(PREFS_KEY_SHOW_NOTIFICATIONS, defaultValue);
    }

    public boolean showInactiveJobs() {
        if (mShowInactiveJobs == null) {
            mShowInactiveJobs = showInactiveJobs(true);
        }
        return mShowInactiveJobs;
    }

    private boolean showInactiveJobs(boolean defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getBoolean(PREFS_KEY_SHOW_INACTIVE_JOBS, defaultValue);
    }

    public boolean showBilledHoursRecords() {
        if (mShowBilledHoursRecords == null) {
            mShowBilledHoursRecords = showBilledHoursRecords(false);
        }
        return mShowBilledHoursRecords;
    }

    private boolean showBilledHoursRecords(boolean defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getBoolean(PREFS_KEY_SHOW_BILLED_HOURS_RECORDS, defaultValue);
    }

    private String computeKeyForLastUsedProjectId(int jobId) {
        return PREFS_KEY_LAST_USED_PROJECT_ID + "_JOBID_" + Integer.toString(jobId);
    }

    public int getRounding() {
        if (mRounding == null) {
            mRounding = getRounding(0);
        }
        return mRounding;
    }

    private int getRounding(int defaultValue) {
        int ret = defaultValue;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String value = sharedPreferences.getString(PREFS_KEY_ROUNDING, Integer.toString(defaultValue));
        if (value != null) {
            try {
                ret = Integer.valueOf(value);
            } catch (NumberFormatException e) {
                // Default: 0
                Log.d(TAG, "Unable to format '" + value + "' as an int. Going with the default value of 0");
                ret = 0;
            }
        }
        Log.d(TAG, "Rounding amount is: " + ret);
        return ret;
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
        // Optimization
        mLastUsedProjectId = Integer.valueOf(lastUsedProjectId);
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
        // Optimization
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
        // Optimization
        mReportSummaryEndDate = endDate;
    }

    public Job getReportSummaryLastSelectedJob() {
        if (mReportSummaryLastSelectedJob == null) {
            mReportSummaryLastSelectedJob = Job.ALL_JOBS;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            int jobId = sharedPreferences.getInt(PREFS_KEY_REPORT_SUMMARY_LAST_SELECTED_JOB_ID, -238);
            if (jobId != -238) {
                mReportSummaryLastSelectedJob = DataStore.instance(mContext).getJob(jobId);
            }
        }
        return mReportSummaryLastSelectedJob;
    }

    public void saveReportSummaryLastSelectedJobId(Job job) {
        if (job != null && !job.equals(Job.ALL_JOBS)) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putInt(PREFS_KEY_REPORT_SUMMARY_LAST_SELECTED_JOB_ID, job.getId());
            sharedPreferencesEditor.commit();
            // Optimization
            mReportSummaryLastSelectedJob = job;
        }
    }

    public String getDateFormatString() {
        // TODO: externalize
        return "MM/dd/yyyy hh:mm a";
    }
}
