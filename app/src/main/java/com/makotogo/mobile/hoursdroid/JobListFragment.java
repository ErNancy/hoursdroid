package com.makotogo.mobile.hoursdroid;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.makotogo.mobile.framework.AbstractArrayAdapter;
import com.makotogo.mobile.framework.AbstractFragment;
import com.makotogo.mobile.framework.ViewBinder;
import com.makotogo.mobile.hoursdroid.model.DataStore;
import com.makotogo.mobile.hoursdroid.model.Job;
import com.makotogo.mobile.hoursdroid.model.Project;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class JobListFragment extends AbstractFragment {

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
            job.setActive(true);
            intent.putExtra(JobDetailActivity.EXTRA_JOB, job);
            // Start the activity
            startActivity(intent);
            // Handled
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Configure the UI.
     */
    @Override
    protected void configureUI(View view) {
        final String METHOD = "configureUI(" + view + "): ";
        // Populate ListView with Job information
        ListView listView = (ListView) view.findViewById(R.id.job_list_view);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Access the Job backing store singleton
        DataStore dataStore = DataStore.instance(getActivity());
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
        DataStore dataStore = DataStore.instance(getActivity());
        // Now refresh the view
        List<Job> jobs = dataStore.getJobs();
        if (getListViewAdapter() != null) {
            getListViewAdapter().clear();
            getListViewAdapter().addAll(jobs);
            getListViewAdapter().notifyDataSetChanged();
        }
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
        View view = getView();
        if (view == null) {
            throw new RuntimeException("View has not yet been configured. Cannot invoke getListView()!");
        }
        return (ListView) view.findViewById(R.id.job_list_view);
    }

    private AbstractArrayAdapter<Job> getListViewAdapter() {
        AbstractArrayAdapter<Job> ret = null;
        if (getListView() == null) {
            throw new RuntimeException("ListView has not been configured!");
        } else {
            ret = (AbstractArrayAdapter<Job>) getListView().getAdapter();
        }
        return ret;
    }

    private void configureListView(final ListView listView) {
        // Set the Adapter property of the RecyclerView to the JobAdapter
        listView.setAdapter(new AbstractArrayAdapter<Job>(getActivity(), R.layout.job_list_row) {
            @Override
            protected ViewBinder<Job> createViewBinder() {
                return new JobViewBinder();
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
                        Project defaultProject = DataStore.instance().getDefaultProject(job);
                        if (defaultProject == null && multipleActiveProjectsForJob(job)) {
                            // Display Select Project dialog, which forwards to
                            /// the HoursListActivity
                            // TODO: create the Select Project dialog
                        } else {
                            // Go with the default project directly to the HoursList Activity
                            Intent intent = new Intent(getActivity(), HoursListActivity.class);
                            intent.putExtra(HoursListActivity.EXTRA_PROJECT, defaultProject);
                            startActivity(intent);
                        }
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
    private class JobViewBinder implements ViewBinder<Job> {

        private TextView getNameTextView(View view) {
            return (TextView) view.findViewById(R.id.job_list_row_name);
        }

        private TextView getDescriptionTextView(View view) {
            return (TextView) view.findViewById(R.id.job_list_row_description);
        }

        //        private TextView getIdTextView(View view) {
//            return (TextView) view.findViewById(R.id.job_list_row_id);
//        }
//
        private View getIsJobActiveView(View view) {
            return (View) view.findViewById(R.id.job_list_row_active);
        }

        public void bind(Job job, View view) {
            getNameTextView(view).setText(job.getName());
            getDescriptionTextView(view).setText(job.getDescription());
            if (job.isActive()) {
                getIsJobActiveView(view).setBackgroundColor(view.getContext().getResources().getColor(android.R.color.holo_green_dark));
            } else {
                getIsJobActiveView(view).setBackgroundColor(view.getContext().getResources().getColor(android.R.color.holo_red_light));
            }
//            getIdTextView(view).setText((job.getId() == null) ? "UNSAVED" : "ID=" + job.getId().toString());
        }

    }

    /**
     * Custom implementation of ArrayAdapter<T> that provides its own
     * custom View.
     */
//    private class JobAdapter extends ArrayAdapter<Job> {
//
//        public JobAdapter() {
//            super(getActivity(), 0);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            final String METHOD = "getView(" + position + ", " + convertView + ", " + parent + "): ";
//            // Check to see if we need to create a view, or if it is recycled
//            if (convertView == null) {
//                convertView = getActivity().getLayoutInflater().inflate(R.layout.job_list_row, null);
//                JobViewBinder jobViewBinder = new JobViewBinder();
//                convertView.setTag(jobViewBinder);
//            }
//            Job job = getItem(position);
//            JobViewBinder jobViewBinder = (JobViewBinder) convertView.getTag();
//            jobViewBinder.bindJobData(job, convertView);
//            return convertView;
//        }
//
//    }
}
