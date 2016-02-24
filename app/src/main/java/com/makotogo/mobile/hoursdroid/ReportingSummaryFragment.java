package com.makotogo.mobile.hoursdroid;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.makotogo.mobile.framework.AbstractArrayAdapter;
import com.makotogo.mobile.framework.AbstractFragment;
import com.makotogo.mobile.framework.ViewBinder;
import com.makotogo.mobile.hoursdroid.model.DataStore;
import com.makotogo.mobile.hoursdroid.model.Hours;
import com.makotogo.mobile.hoursdroid.model.Job;
import com.makotogo.mobile.hoursdroid.util.ApplicationOptions;

import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Date;
import java.util.List;

/**
 * Created by sperry on 2/6/16.
 */
public class ReportingSummaryFragment extends AbstractFragment {

    private static final String TAG = ReportingSummaryFragment.class.getSimpleName();

    private static final long DEFAULT_BEGIN_TIME_MILLIS = new LocalDateTime(2015, 1, 1, 0, 0).toDate().getTime();// 1/1/2015
    private static final Date DEFAULT_BEGIN_DATE = new Date(DEFAULT_BEGIN_TIME_MILLIS);
    private static final long DEFAULT_END_TIME_MILLIS = new LocalDateTime(3000, 1, 1, 0, 0).toDate().getTime();// 1/1/3000
    private static final Date DEFAULT_END_DATE = new Date(DEFAULT_END_TIME_MILLIS);

    private static final String STATE_BEGIN_DATE = "state.begin" + Date.class.getName();
    private static final String STATE_END_DATE = "state.end" + Date.class.getName();
    private static final String STATE_JOB = "state." + Job.class.getName();

    private static final int REQUEST_BEGIN_DATE = 100;
    private static final int REQUEST_END_DATE = 200;

    private static final String DATE_FORMAT_STRING = "MM/dd/yy hh:mm a";
    private static final String DATE_NONE = "NONE";

    private static PeriodFormatter sPeriodFormatter = new PeriodFormatterBuilder()
            .printZeroNever()
            .appendDays().appendSuffix("d")
            .appendSeparator(", ")
            .appendHours().appendSuffix("h")
            .appendSeparator(": ")
            .appendMinutes().appendSuffix("m")
//            .appendSeparator(": ")
//            .appendSeconds().appendSuffix("s")
            .toFormatter();

    /**
     * The reporting interval. From date and To date.
     * Nothing fancy. Purely for convenience.
     */
    private Date mBeginDate;
    private Date mEndDate;

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
     * @param layoutInflater
     * @param container
     * @param savedInstanceState
     */
    @Override
    protected View configureUI(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        final String METHOD = "configureUI(...): ";
        Log.d(TAG, METHOD + "BEGIN");
        View view = layoutInflater.inflate(R.layout.fragment_reporting_summary, container, false);
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
        configureTotalTextView(totalTextView);
        // Hours ListView
        ListView hoursListView = (ListView) view.findViewById(R.id.listview_reporting_summary_hours);
        configureHoursListView(hoursListView);
        Log.d(TAG, METHOD + "END");
        return view;
    }

    /**
     * Update the UI as a whole. Most likely delegates to other updateXxx() methods
     */
    @Override
    protected void updateUI() {
        final String METHOD = "updateUI(): ";
        Log.d(TAG, METHOD + "BEGIN");
        // Pull the list of jobs
        DataStore dataStore = DataStore.instance(getActivity());
        List<Job> jobList = dataStore.getJobs();
        jobList.add(Job.ALL_JOBS);
        updateJobSpinner(jobList);
        // Update the Hours ListView based on the selected Job and Interval
        List<Hours> hoursList = dataStore.getHours(mJob, mBeginDate, mEndDate);
        updateHoursListView(hoursList);
        // Update other controls
        updateBeginDateTextView();
        updateEndDateTextView();
        updateTotalTextView();
        Log.d(TAG, METHOD + "END");
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
        final String METHOD = "onActivityResult(" + requestCode + ", " + resultCode + ", " + data + "): ";
        Log.d(TAG, METHOD + "BEGIN");
        // If resultCode is OK, then figure out which Fragment we spun off
        /// is reporting back
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_BEGIN_DATE:
                    processBeginDateRequestResult(data);
                    break;
                case REQUEST_END_DATE:
                    processEndDateRequestResult(data);
                    break;
                default:
                    String message = "Cannot handle requestCode (" + requestCode + "). User pressed the Back button, maybe?";
                    Log.w(TAG, METHOD + message);
                    break;
            }
            // Update the UI
            updateUI();
        } else {
            String message = "Cannot handle resultCode (" + resultCode + "). User pressed the Back button, maybe?";
            Log.w(TAG, METHOD + message);
        }
        Log.d(TAG, METHOD + "END");
    }

    /**
     * Read SharedPreferences for any information we want to save between incarnations
     * of this Fragment.
     */
    @Override
    protected void readSharedPreferences() {
        // Restore options from SharedPreferences
        ApplicationOptions applicationOptions = ApplicationOptions.instance(getActivity());
        mBeginDate = applicationOptions.getReportSummaryBeginDate(DEFAULT_BEGIN_DATE);
        mEndDate = applicationOptions.getReportSummaryEndDate(DEFAULT_END_DATE);
    }

    @Override
    protected void updateSharedPreferences() {
        super.updateSharedPreferences();
        ApplicationOptions applicationOptions = ApplicationOptions.instance(getActivity());
        // Begin Date
        if (mBeginDate != null) {
            applicationOptions.saveReportSummaryBeginDate(mBeginDate);
        }
        // End Date
        if (mEndDate != null) {
            applicationOptions.saveReportSummaryEndDate(mEndDate);
        }
    }

    @Override
    protected void saveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_BEGIN_DATE, mBeginDate);
        outState.putSerializable(STATE_END_DATE, mEndDate);
        outState.putSerializable(STATE_JOB, mJob);
    }

    @Override
    protected void restoreInstanceState(Bundle savedInstanceState) {
        mBeginDate = (Date) savedInstanceState.getSerializable(STATE_BEGIN_DATE);
        mEndDate = (Date) savedInstanceState.getSerializable(STATE_END_DATE);
        mJob = (Job) savedInstanceState.getSerializable(STATE_JOB);
    }

    @Override
    protected boolean validate(View view) {
        // Nothing to do...
        return true;
    }

    private void processBeginDateRequestResult(Intent data) {
        String METHOD = "processBeginDateRequestResult(Intent): ";
        Date beginDate = (Date) data.getSerializableExtra(DateTimePickerFragment.RESULT_DATE_TIME);
        long now = System.currentTimeMillis();
        long begin = beginDate.getTime();
        long end = (mEndDate != null) ? mEndDate.getTime() : DEFAULT_END_TIME_MILLIS;
        if (begin > now) {
            // Sanity Check #1 - Begin must be before now
            // It is not. Error message.
            String message = "Begin date/time cannot be later than current date/time (i.e., \"now\"). Please try again.";
            Log.e(TAG, METHOD + message);
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        } else {
            if (end >= begin) {
                // Sanity Check #2 - Begin must be before or equal to End.
                // It is. Set the begin date the user chose
                mBeginDate = beginDate;
                // Update the new filter setting
                updateSharedPreferences();
            } else {
                String message = "Begin date/time cannot be later than End date/time. Please try again.";
                Log.e(TAG, METHOD + message);
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void processEndDateRequestResult(Intent data) {
        String METHOD = "processEndDateRequestResult(Intent): ";
        Date endDate = (Date) data.getSerializableExtra(DateTimePickerFragment.RESULT_DATE_TIME);
        long now = System.currentTimeMillis();
        long end = endDate.getTime();
        long begin = (mBeginDate != null) ? mBeginDate.getTime() : DEFAULT_BEGIN_TIME_MILLIS;
        if (end < now) {
            // Sanity Check #1 - End must be after now
            // It is not. Error message.
            String message = "End date/time cannot be earlier than current date/time (i.e., \"now\"). Please try again.";
            Log.e(TAG, METHOD + message);
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        } else {
            if (begin <= end) {
                // Sanity Check #2 - Begin must be before or equal to End.
                // It is. set the end date the user chose
                mEndDate = endDate;
                // Update the new filter setting
                updateSharedPreferences();
            } else {
                String message = "Begin date/time cannot be later than End date/time. Please try again.";
                Log.e(TAG, METHOD + message);
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    }

    // Configure/Update Pairs
    // TODO: Spray this pattern throughout the application!

    private void configureHoursListView(ListView hoursListView) {
        // Create the HoursAdapter
        HoursListViewAdapter hoursListViewAdapter =
                new HoursListViewAdapter(getActivity(), R.layout.reporting_summary_row);
        // Set it
        hoursListView.setAdapter(hoursListViewAdapter);
        // TODO: OnClickListener - Gives the user the ability to tweak the
        /// Hours record they select. Have to work out the hierarchical navigation though.
    }

    private void updateHoursListView(List<Hours> hoursList) {
        // Update the ListView
        ListView hoursListView = (ListView) getView().findViewById(R.id.listview_reporting_summary_hours);
        HoursListViewAdapter hoursListViewAdapter = (HoursListViewAdapter) hoursListView.getAdapter();
        hoursListViewAdapter.clear();
        hoursListViewAdapter.addAll(hoursList);
        hoursListViewAdapter.notifyDataSetChanged();
    }

    private void configureBeginDateTextView(TextView beginDateTextView) {
        // Set the OnClick listener so the user can tweak the End Date
        beginDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date dateToShow = (mBeginDate != null) ? mBeginDate : DEFAULT_BEGIN_DATE;
                // Display the DateTimePickerFragment
                DateTimePickerFragment dateTimePickerFragment =
                        FragmentFactory.createDatePickerFragment(
                                dateToShow, // The date to show
                                "Begin",      // The Caption
                                DateTimePickerFragment.DATE// The initial choice
                        );
                FragmentManager fragmentManager = getFragmentManager();
                dateTimePickerFragment.setTargetFragment(ReportingSummaryFragment.this, REQUEST_BEGIN_DATE);
                dateTimePickerFragment.show(fragmentManager, DateTimePickerFragment.DIALOG_TAG);
            }
        });
    }

    private void updateBeginDateTextView() {
        // Take the End part of the Interval setting and display it
        TextView beginDateTextView = (TextView) getView().findViewById(R.id.textview_reporting_summary_begin_date);
        if (mBeginDate != null) {
            beginDateTextView.setText(new LocalDateTime(mBeginDate.getTime()).toString(DATE_FORMAT_STRING));
        } else {
            beginDateTextView.setText(DATE_NONE);
        }
    }

    private void configureEndDateTextView(TextView endDateTextView) {
        // Set the OnClick listener so the user can tweak the End Date
        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date dateToShow = (mEndDate != null) ? mEndDate : DEFAULT_END_DATE;
                // Display the DateTimePickerFragment
                DateTimePickerFragment dateTimePickerFragment =
                        FragmentFactory.createDatePickerFragment(
                                dateToShow, // The date to show
                                "End",      // The Caption
                                DateTimePickerFragment.DATE// The initial choice
                        );
                FragmentManager fragmentManager = getFragmentManager();
                dateTimePickerFragment.setTargetFragment(ReportingSummaryFragment.this, REQUEST_END_DATE);
                dateTimePickerFragment.show(fragmentManager, DateTimePickerFragment.DIALOG_TAG);
            }
        });
    }

    private void updateEndDateTextView() {
        // Take the End part of the Interval setting and display it
        TextView endDateTextView = (TextView) getView().findViewById(R.id.textview_reporting_summary_end_date);
        if (mEndDate != null) {
            endDateTextView.setText(new LocalDateTime(mEndDate.getTime()).toString(DATE_FORMAT_STRING));
        } else {
            endDateTextView.setText(DATE_NONE);
        }
    }

    private void configureTotalTextView(TextView totalTextView) {
        // Nothing to configure
    }

    private void updateTotalTextView() {
        // Get the total from the Adapter, then convert it
        TextView totalTextView = (TextView) getView().findViewById(R.id.textview_reporting_summary_total);
        ListView hoursListView = (ListView) getView().findViewById(R.id.listview_reporting_summary_hours);
        HoursListViewAdapter hoursListViewAdapter = (HoursListViewAdapter) hoursListView.getAdapter();
        long totalMillisForPeriod = hoursListViewAdapter.getTotalMillisForPeriod();
        totalTextView.setText(formatPeriod(totalMillisForPeriod));
    }

    private void configureJobSpinner(final Spinner jobSpinner) {
        //
        jobSpinner.setAdapter(new JobSpinnerAdapter(getActivity(), R.layout.job_list_row));
        jobSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Store a reference to the selected Job
                mJob = (Job) jobSpinner.getAdapter().getItem(position);
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing to do
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
        int selectedIndex = 0;// Default: first item in the list
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

    private String formatDate(Date dateToFormat) {
        String ret = "";
        if (dateToFormat != null) {
            ret = new LocalDateTime(dateToFormat.getTime()).toString(DATE_FORMAT_STRING);
        }
        return ret;
    }

    private String formatPeriod(Long periodInMillis) {
        String ret = "";
        if (periodInMillis != null) {
            Period period = new Period(periodInMillis.longValue());
            ret = sPeriodFormatter.print(period);
        }
        return ret;
    }

    private class JobSpinnerAdapter extends AbstractArrayAdapter<Job> implements ViewBinder<Job> {

        /**
         * The one-and-only way to create this class.
         *
         * @param context
         * @param resource
         */
        public JobSpinnerAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        protected ViewBinder<Job> createViewBinder() {
            return this;
        }

        @Override
        public void initView(View view) {
            getNameTextView(view).setText("");
            getDescriptionTextView(view).setText("");
            getIsJobActiveView(view).setSelected(false);
            getActiveHours(view).setVisibility(View.INVISIBLE);
        }

        @Override
        public void bind(Job job, View view) {
            getNameTextView(view).setText(job.getName());
            getDescriptionTextView(view).setText(job.getDescription());
            if (job.isActive()) {
                getIsJobActiveView(view).setBackgroundColor(view.getContext().getResources().getColor(android.R.color.holo_green_dark));
            } else {
                getIsJobActiveView(view).setBackgroundColor(view.getContext().getResources().getColor(android.R.color.holo_red_light));
            }
            if (DataStore.instance(getContext()).hasActiveHours(job)) {
                getActiveHours(view).setVisibility(View.VISIBLE);
            }
        }

        private TextView getNameTextView(View view) {
            return (TextView) view.findViewById(R.id.textview_job_list_row_name);
        }

        private TextView getDescriptionTextView(View view) {
            return (TextView) view.findViewById(R.id.textview_job_list_row_description);
        }

        private View getIsJobActiveView(View view) {
            return (View) view.findViewById(R.id.job_list_row_active);
        }

        private ImageView getActiveHours(View view) {
            return (ImageView) view.findViewById(R.id.imageview_job_list_row_active_hours);
        }

    }

    private class HoursListViewAdapter extends AbstractHoursAdapter implements ViewBinder<Hours> {

        /**
         * The total number of milliseconds for all records within this Adapter
         */
        private long mTotalMillis;

        public HoursListViewAdapter(Context context, int layoutResourceId) {
            super(context, layoutResourceId);
        }

        public long getTotalMillisForPeriod() {
            return mTotalMillis;
        }

        @Override
        protected ViewBinder<Hours> createViewBinder() {
            return this;
        }

        @Override
        public void initView(View view) {
            mTotalMillis = 0;
            getBeginDate(view).setText("");
            getEndDate(view).setText("");
            getBreak(view).setText("");
            getTotal(view).setText("");
            getActiveHours(view).setVisibility(View.INVISIBLE);
        }

        @Override
        public void bind(Hours hours, View view) {
            TextView beginDateTextView = getBeginDate(view);
            beginDateTextView.setText(formatDate(hours.getBegin()));
            TextView endDateTextView = getEndDate(view);
            endDateTextView.setText(formatDate(hours.getEnd()));
            TextView breakTextView = getBreak(view);
            breakTextView.setText(formatPeriod(hours.getBreak()));
            ImageView activeHoursImageView = getActiveHours(view);
            activeHoursImageView.setVisibility((hours.getEnd() == null) ? View.VISIBLE : View.INVISIBLE);
            TextView jobTextView = getJob(view);
            jobTextView.setText(mJob.getName());
            // Add to Total
            long now = System.currentTimeMillis();
            long begin = hours.getBegin().getTime();
            long end = (hours.getEnd() != null) ? hours.getEnd().getTime() : now;
            long breakTime = (hours.getBreak() != null) ? hours.getBreak() : 0L;
            // Now compute the total for this Hours record and add it to the grand total
            mTotalMillis += (end - begin - breakTime);
        }

        private TextView getBeginDate(View view) {
            return (TextView) view.findViewById(R.id.textview_reporting_summary_row_begin_date);
        }

        private TextView getEndDate(View view) {
            return (TextView) view.findViewById(R.id.textview_reporting_summary_row_end_date);
        }

        private TextView getBreak(View view) {
            return (TextView) view.findViewById(R.id.textview_reporting_summary_row_break);
        }

        private TextView getTotal(View view) {
            return (TextView) view.findViewById(R.id.textview_reporting_summary_row_total);
        }

        private ImageView getActiveHours(View view) {
            return (ImageView) view.findViewById(R.id.imageview_reporting_summary_row_active_hours);
        }

        private TextView getJob(View view) {
            return (TextView) view.findViewById(R.id.textview_reporting_summary_row_job);
        }

    }

    // A    T    T    I    C
//    private Interval computeReportingInterval(Date beginDate, Date endDate) {
//        Interval ret = null;
//        if (beginDate != null && endDate != null) {
//            ret = new Interval(beginDate.getTime(), endDate.getTime());
//        } else if (beginDate == null && endDate != null) {
//            ret = new Interval(DEFAULT_BEGIN_TIME_MILLIS, endDate.getTime());
//        } else if (beginDate != null && endDate == null) {
//            ret = new Interval(beginDate.getTime(), DEFAULT_END_TIME_MILLIS);
//        } else {
//            ret = new Interval(DEFAULT_BEGIN_TIME_MILLIS, DEFAULT_END_TIME_MILLIS);
//        }
//        return null;
//    }
//
}
