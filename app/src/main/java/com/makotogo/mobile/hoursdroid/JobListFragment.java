package com.makotogo.mobile.hoursdroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.makotogo.mobile.framework.AbstractArrayAdapter;
import com.makotogo.mobile.framework.AbstractFragment;
import com.makotogo.mobile.framework.ViewBinder;
import com.makotogo.mobile.hoursdroid.model.DataStore;
import com.makotogo.mobile.hoursdroid.model.Job;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class JobListFragment extends AbstractFragment {

    public static final String HOURS_CANNOT_BE_ADDED_FOR_INACTIVE_JOBS = "Hours cannot be added for inactive Jobs.";
    private static final String TAG = JobListFragment.class.getSimpleName();
    private static final boolean IN_ACTION_MODE = true;
    private static final boolean NOT_IN_ACTION_MODE = false;
    private transient boolean mInActionMode;

    @Override
    protected void processFragmentArguments() {
        // Nothing to do (no fragment arguments)
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // If this Fragment is being recreated, we want to rematerialize
        /// its state
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_list, container, false);

        // Now finish configuring the UI
        configureUI(view);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_job_list, menu);
    }

    @Override
    public void saveInstanceState(Bundle outState) {
        // Nothing to do
    }

    @Override
    public void restoreInstanceState(Bundle savedInstanceState) {
        // Nothing to do
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret;
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Handle New Job
        switch (id) {
            case R.id.menu_item_job_new: {
                // Fire off Job Edit Activity
                Intent intent = new Intent(getActivity(), JobDetailActivity.class);
                // Create an empty Job object to be used by JobDetailActivity
                Job job = new Job();
                job.setActive(true);
                intent.putExtra(JobDetailActivity.EXTRA_JOB, job);
                // Start the activity
                startActivity(intent);
                // Handled
                ret = true;
                break;
            }
            case R.id.menu_item_job_reporting_summary: {
                // Fire off Reporting Summary Activity
                Intent intent = new Intent(getActivity(), ReportingSummaryActivity.class);
                startActivity(intent);
                ret = true;
                break;
            }
            default: {
                ret = super.onOptionsItemSelected(item);
                break;
            }
        }
        return ret;
    }

    /**
     * Configure the UI.
     */
    @Override
    protected void configureUI(View view) {
        final String METHOD = "configureUI(" + view + "): ";
        // Populate ListView with Job information
        ListView listView = (ListView) view.findViewById(R.id.listview_job_list);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Configure the List View
        configureListView(listView);
    }

    /**
     * Update the UI:
     * - Reload the list of Jobs from the DataStore
     * - Update the JobAdapter with the refreshed List
     * - Update the View
     */
    @Override
    protected void updateUI() {
        final String METHOD = "updateUI(): ";
        Log.d(TAG, METHOD + "Starting AsyncTask...");
        // Now refresh the view - use AsyncTask
        new AsyncTask<Void, Void, List<Job>>() {

            @Override
            protected List<Job> doInBackground(Void... params) {
                final String METHOD = "doInBackground(): ";
                DataStore dataStore = DataStore.instance(getActivity());
                Log.d(TAG, METHOD);
                return dataStore.getJobs();
            }

            @Override
            protected void onPostExecute(List<Job> jobs) {
                final String METHOD = "onPostExecute(" + jobs + "): ";
                Log.d(TAG, METHOD + "Adding " + jobs.size() + " to ListView adapter...");
                if (getListViewAdapter() != null) {
                    getListViewAdapter().clear();
                    getListViewAdapter().addAll(jobs);
                    getListViewAdapter().notifyDataSetChanged();
                }
                Log.d(TAG, METHOD + "Done.");
            }
        }.execute();
    }

    @Override
    protected boolean validate(View view) {
        // TODO: Add validation logic here
        return true;
    }

    /**
     * I really hate stuff like this, but I don't see any better way around it. I want to be
     * able to handle short (regular) and long press on the list view. Unfortunately, both
     * are triggered on a long press, so it's impossible to tell which is which. Except that
     * it *appears* that the Long press is always handled first, which gives us the chance to
     * set some bullshit state variables like this one (yuck yuck yuck).
     * <p/>
     * If the "InActionMode" attribute is true, it indicates that the Activity is currently
     * in Action Mode, which will cause the List View to ignore short/regular press (by checking
     * this state value (again yuck yuck yuck)).
     *
     * @param value
     */
    private void setInActionMode(boolean value) {
        mInActionMode = value;
    }

    private ListView getListView() {
        ListView ret = null;
        View view = getView();
        if (view != null) {
            ret = (ListView) view.findViewById(R.id.listview_job_list);
        }
        return ret;
    }

    private AbstractArrayAdapter<Job> getListViewAdapter() {
        AbstractArrayAdapter<Job> ret = null;
        if (getListView() != null) {
            ret = (AbstractArrayAdapter<Job>) getListView().getAdapter();
        }
        return ret;
    }

    private void configureListView(final ListView listView) {
        // Set the Adapter property of the RecyclerView to the JobAdapter
        listView.setAdapter(new AbstractArrayAdapter<Job>(getActivity(), R.layout.job_list_row) {
            @Override
            protected ViewBinder<Job> createViewBinder() {
                return new JobViewBinder(JobListFragment.this);
            }
        });
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // Create the OnItemLongClickListener, used to bring up the contextual
        /// actions for the List View
        createOnItemLongClickListener(listView);
        // Create the
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mInActionMode) {
                    Job job = (Job) listView.getAdapter().getItem(position);
                    if (job.isActive()) {
                        // If there are multiple projects for this Job,
                        /// then go to Select Project dialog
                        // Go with the default project directly to the HoursList Activity
                        Intent intent = new Intent(getActivity(), HoursListActivity.class);
                        intent.putExtra(HoursListActivity.EXTRA_JOB, job);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(),
                                HOURS_CANNOT_BE_ADDED_FOR_INACTIVE_JOBS,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
            }
        });
    }

    private boolean multipleActiveProjectsForJob(Job job) {
        return false;
    }

    private void createOnItemLongClickListener(ListView listView) {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                boolean ret = false;
                // Long press... It will be handled first. Set the state variable (yuck).
                setInActionMode(IN_ACTION_MODE);
                getActivity().startActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        // Inflate the menu
                        MenuInflater menuInflater = mode.getMenuInflater();
                        menuInflater.inflate(R.menu.menu_job_ca, menu);
                        // Must return true here or the ART will abort creation of the menu!
                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        final String METHOD = "onActionItemClicked(ActionMode, MenuItem): ";
                        boolean ret;
                        Job job = (Job) getListViewAdapter().getItem(position);
                        // Switch on the menu item ID
                        switch (item.getItemId()) {
                            case R.id.menu_item_job_edit:
                                // Fire off the JobDetailActivity
                                Intent intent = new Intent(getActivity(), JobDetailActivity.class);
                                intent.putExtra(JobDetailActivity.EXTRA_JOB, job);
                                // Start the activity
                                startActivity(intent);
                                ret = true;// handled
                                break;
                            case R.id.menu_item_job_delete:
                                Log.d(TAG, METHOD + "Attempting to delete a Job...");
                                DataStore dataStore = DataStore.instance(getActivity());
                                if (dataStore.hasHours(job)) {
                                    Toast.makeText(getActivity(), "Cannot delete a Job that has Hours.", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.d(TAG, METHOD + "Job has no Hours objects for it... deleting...");
                                    dataStore.delete(job);
                                }
                                ret = true;// handled
                                break;
                            default:
                                // Not necessary, but included for completeness
                                ret = false;
                                break;
                        }
                        mode.finish();
                        // The UI probably needs updating.
                        updateUI();
                        return ret;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        final String METHOD = "onPrepareActionMode(" + mode + ", " + menu + "): ";
                        Log.d(TAG, METHOD + "...");
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        final String METHOD = "onDestroyActionMode(" + mode + "): ";
                        Log.d(TAG, METHOD + "...");
                        // Through with Action Mode. The list view can go back to handling
                        /// short press.
                        setInActionMode(NOT_IN_ACTION_MODE);
                    }
                });
                return ret;
            }
        });
    }
    /**
     * Binds the View to the Job object displayed in the View.
     * Not so much of a Holder as a Binder. Mainly because circular
     * references are evil.
     */
    private class JobViewBinder implements ViewBinder<Job> {

        private JobListFragment mJobListFragment;

        public JobViewBinder(JobListFragment jobListFragment) {
            mJobListFragment = jobListFragment;
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

        @Override
        public void initView(View view) {
            getNameTextView(view).setText("");
            getDescriptionTextView(view).setText("");
            getIsJobActiveView(view).setSelected(false);
            getActiveHours(view).setVisibility(View.INVISIBLE);
        }

        public void bind(Job job, View view) {
            getNameTextView(view).setText(job.getName());
            getDescriptionTextView(view).setText(job.getDescription());
            if (job.isActive()) {
                getIsJobActiveView(view).setBackgroundColor(view.getContext().getResources().getColor(android.R.color.holo_green_dark));
            } else {
                getIsJobActiveView(view).setBackgroundColor(view.getContext().getResources().getColor(android.R.color.holo_red_light));
            }
            if (DataStore.instance(mJobListFragment.getActivity()).hasActiveHours(job)) {
                getActiveHours(view).setVisibility(View.VISIBLE);
            }
        }

    }

}
