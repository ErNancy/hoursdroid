package com.makotogo.mobile.hoursdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.makotogo.mobile.framework.AbstractArrayAdapter;
import com.makotogo.mobile.framework.AbstractFragment;
import com.makotogo.mobile.framework.ViewBinder;
import com.makotogo.mobile.hoursdroid.model.DataStore;
import com.makotogo.mobile.hoursdroid.model.Job;
import com.makotogo.mobile.hoursdroid.util.ApplicationOptions;

import org.joda.time.Interval;

import java.util.Date;
import java.util.List;

/**
 * Created by sperry on 2/6/16.
 */
public class ReportingSummaryFragment extends AbstractFragment {

    private static final String STATE_REPORTING_INTERVAL = "state.reporting." + Interval.class.getName();
    private static final String STATE_JOB = "state." + Job.class.getName();

    private static final int REQUEST_BEGIN_DATE = 100;
    private static final int REQUEST_END_DATE = 200;

    /**
     * The reporting interval. From date and To date.
     * Nothing fancy. Purely for convenience.
     */
    private Interval mReportingInterval;

    /**
     * The currently selected Job
     */
    private Job mJob;

    /**
     * Process any arguments passed to this Fragment by its creator
     */
    @Override
    protected void processFragmentArguments() {
        // No arguments.
    }

    /**
     * Configure the UI as a whole. Most likely delegates to other configureXxx() methods.
     *
     * @param view
     */
    @Override
    protected void configureUI(View view) {
        // Job Spinner
        Spinner jobSpinner = (Spinner) view.findViewById(R.id.spinner_reporting_summary_job);
        configureJobSpinner(jobSpinner);
        // Begin Date TextView
        TextView beginDateTextView = (TextView) view.findViewById(R.id.textview_reporting_summary_begin_date);
        configureBeginDateTextView(beginDateTextView);
        // End Date TextField
        TextView endDateTextView = (TextView) view.findViewById(R.id.textview_reporting_summary_end_date);
        configureEndDateTextView(endDateTextView);
        // Total TextField
        TextView totalTextView = (TextView) view.findViewById(R.id.textview_reporting_summary_total);
        // Hours ListView
        ListView hoursListView = (ListView) view.findViewById(R.id.listview_reporting_summary_hours);
        configureHoursListView(hoursListView);
        // Read any information from SharedPreferences
        readSharedPreferences();
    }

    /**
     * Update the UI as a whole. Most likely delegates to other updateXxx() methods
     */
    @Override
    protected void updateUI() {
        // Pull the list of jobs
        DataStore dataStore = DataStore.instance(getActivity());
        List<Job> jobList = dataStore.getJobs();
        jobList.add(Job.ALL_JOBS);
        updateJobSpinner(jobList);
    }

    /**
     * Called whenever an invoked Fragment (e.g., Filter Settings) returns
     * a result.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Read SharedPreferences for any information we want to save between incarnations
     * of this Fragment.
     */
    private void readSharedPreferences() {
        // Restore options from SharedPreferences
        ApplicationOptions applicationOptions = ApplicationOptions.instance(getActivity());
        Date beginDate = applicationOptions.getReportSummaryBeginDate();
        Date endDate = applicationOptions.getReportSummaryEndDate();
        mReportingInterval = new Interval(beginDate.getTime(), endDate.getTime());
        super.onResume();
    }

    @Override
    protected void updateSharedPreferences() {
        super.updateSharedPreferences();
        ApplicationOptions applicationOptions = ApplicationOptions.instance(getActivity());
        // Begin Date
        Date beginDate = new Date(mReportingInterval.getStartMillis());
        applicationOptions.saveReportSummaryBeginDate(beginDate);
        // End Date
        Date endDate = new Date(mReportingInterval.getEndMillis());
        applicationOptions.saveReportSummaryEndDate(endDate);
    }

    @Override
    protected void saveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_REPORTING_INTERVAL, mReportingInterval);
        outState.putSerializable(STATE_JOB, mJob);
    }

    @Override
    protected void restoreInstanceState(Bundle savedInstanceState) {
        mReportingInterval = (Interval) savedInstanceState.getSerializable(STATE_REPORTING_INTERVAL);
        mJob = (Job) savedInstanceState.getSerializable(STATE_JOB);
    }

    @Override
    protected boolean validate(View view) {
        // Nothing to do...
        return true;
    }

    // Configure/Update Pairs
    // TODO: Spray this pattern throughout the application!

    private void configureHoursListView(ListView hoursListView) {

    }

    private void updateHoursListView() {

    }

    private void configureEndDateTextView(TextView endDateTextView) {

    }

    private void updateEndDateTextView() {

    }

    private void configureBeginDateTextView(TextView beginDateTextView) {
    }

    private void updateBeginDateTextView() {

    }

    private void configureJobSpinner(Spinner jobSpinner) {
        //
        jobSpinner.setAdapter(new JobSpinnerAdapter(getActivity(), R.layout.job_list_row));
        jobSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateJobSpinner(List<Job> jobList) {
        // Update the Spinner
        Spinner jobSpinner = (Spinner) getView().findViewById(R.id.spinner_reporting_summary_job);
        JobSpinnerAdapter jobSpinnerAdapter = (JobSpinnerAdapter) jobSpinner.getAdapter();
        jobSpinnerAdapter.clear();
        jobSpinnerAdapter.addAll(jobList);
        jobSpinnerAdapter.notifyDataSetChanged();
        if (mJob == null) {
            mJob = Job.ALL_JOBS;
        }
        int selectedIndex = 0;
        // Search for the current job (mJob) and make sure it is selected
        for (int aa = 0; aa < jobSpinnerAdapter.getCount(); aa++) {
            if (jobSpinnerAdapter.getItem(aa).equals(mJob)) {
                selectedIndex = aa;
                break;
            }
        }
        // Select the Job at the selectedIndex
        jobSpinner.setSelection(selectedIndex);
    }

    private class JobSpinnerAdapter extends AbstractArrayAdapter<Job> implements ViewBinder<Job> {

        /**
         * The one-and-only way to create this class.
         *
         * @param activity
         * @param resource
         */
        public JobSpinnerAdapter(Activity activity, int resource) {
            super(activity, resource);
        }

        @Override
        protected ViewBinder<Job> createViewBinder() {
            return this;
        }

        @Override
        public void initView(View view) {

        }

        @Override
        public void bind(Job job, View view) {

        }
    }

}
