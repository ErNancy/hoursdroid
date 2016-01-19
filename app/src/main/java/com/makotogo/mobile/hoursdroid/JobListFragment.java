package com.makotogo.mobile.hoursdroid;

import android.app.Fragment;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.makotogo.mobile.hoursdroid.model.DataStore;
import com.makotogo.mobile.hoursdroid.model.Job;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class JobListFragment extends Fragment {

    private static final String TAG = JobListFragment.class.getSimpleName();

    private ListView mListView;
    private JobAdapter mJobAdapter;

    private static final boolean IN_ACTION_MODE = true;
    private static final boolean NOT_IN_ACTION_MODE = false;
    private boolean mInActionMode;

    public static JobListFragment newInstance() {
        return new JobListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_list, container, false);

        // Populate ListView with Job information
        mListView = (ListView) view.findViewById(R.id.job_list_view);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Now finish configuring the UI
        configureUI();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_job_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Handle New Job
        if (id == R.id.menu_item_job_new) {
            // Fire off Job Edit Activity
            Intent intent = new Intent(getActivity(), JobDetailActivity.class);
            // Create an empty Job object to be used by JobDetailActivity
            Job job = new Job();
            job.setName("");
            job.setDescription("");
            job.setActive(Boolean.TRUE);
            intent.putExtra(JobDetailActivity.EXTRA_JOB, job);
            // Start the activity
            startActivity(intent);
            // Handled
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    /**
     * Configure the UI. 1pr stuff.
     */
    private void configureUI() {
        // Access the Job backing store singleton
        DataStore dataStore = DataStore.instance(getActivity());
        // Create a new JobAdapter
        mJobAdapter = new JobAdapter(dataStore.getJobs());
        configureListView();

    }

    /**
     * Update the UI:
     * - Reload the list of Jobs from the DataStore
     * - Update the JobAdapter with the refreshed List
     * - Update the View
     */
    private void updateUI() {
        DataStore dataStore = DataStore.instance(getActivity());
        // Now refresh the view
        mJobAdapter.updateJobs(dataStore.getJobs());
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

    private void configureListView() {
        // Set the Adapter property of the RecyclerView to the JobAdapter
        mListView.setAdapter(mJobAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // Create the OnItemLongClickListener, used to bring up the contextual
        /// actions for the List View
        createOnItemLongClickListener();
        // Create the
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mInActionMode) {
                    Job job = (Job) mListView.getAdapter().getItem(position);
                    if (job.isActive()) {
                        Intent intent = new Intent(getActivity(), HoursListActivity.class);
                        intent.putExtra(HoursListActivity.EXTRA_JOB, job);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(),
                                "Hours cannot be added for inactive Jobs.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
            }
        });
    }

    private void createOnItemLongClickListener() {
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
                        boolean ret;
                        Job job = (Job) mListView.getAdapter().getItem(position);
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
                                DataStore.instance(getActivity()).delete(job);
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
    private class JobViewHolder {

        private TextView getNameTextView(View view) {
            return (TextView) view.findViewById(R.id.job_list_row_name);
        }

        private TextView getDescriptionTextView(View view) {
            return (TextView) view.findViewById(R.id.job_list_row_description);
        }

        private TextView getIdTextView(View view) {
            return (TextView) view.findViewById(R.id.job_list_row_id);
        }

        private View getIsJobActiveView(View view) {
            return (View) view.findViewById(R.id.job_list_row_active);
        }

        public void bindJobData(Job job, View view) {
            getNameTextView(view).setText(job.getName());
            getDescriptionTextView(view).setText(job.getDescription());
            if (job.isActive()) {
                getIsJobActiveView(view).setBackgroundColor(view.getContext().getResources().getColor(android.R.color.holo_green_dark));
            } else {
                getIsJobActiveView(view).setBackgroundColor(view.getContext().getResources().getColor(android.R.color.holo_red_light));
            }
            getIdTextView(view).setText((job.getId() == null) ? "UNSAVED" : "ID=" + job.getId().toString());
        }

    }

    /**
     * Custom implementation of ArrayAdapter<T> that provides its own
     * custom View.
     */
    private class JobAdapter extends ArrayAdapter<Job> {

        // The Job backing store
        private List<Job> mJobs = new ArrayList<Job>();

        public JobAdapter(List<Job> jobs) {
            super(getActivity(), 0, jobs);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Check to see if we need to create a view, or if it is recycled
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.job_list_row, null);
                JobViewHolder jobViewHolder = new JobViewHolder();
                convertView.setTag(jobViewHolder);
            }
            Job job = getItem(position);
            JobViewHolder jobViewHolder = (JobViewHolder) convertView.getTag();
            jobViewHolder.bindJobData(job, convertView);
            return convertView;
        }

        public void updateJobs(List<Job> jobs) {
            if (mJobs != null) {
                mJobs.clear();
            }
            mJobs.addAll(jobs);
            notifyDataSetChanged();
        }

    }
}
