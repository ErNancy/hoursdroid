package com.makotogo.mobile.hoursdroid;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Spinner;

import com.makotogo.mobile.hoursdroid.model.DataStore;
import com.makotogo.mobile.hoursdroid.model.Hours;
import com.makotogo.mobile.hoursdroid.model.Job;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sperry on 1/12/16.
 */
public class HoursListFragment extends Fragment {

    private static final String TAG = HoursListFragment.class.getSimpleName();

    public static final String ARG_JOB_HOURS_LIST = "arg." + HoursListFragment.class.getSimpleName();

    private Job mJob;
    private ListView mListView;
    private HoursAdapter mHoursAdapter;

    private static final boolean IN_ACTION_MODE = true;
    private static final boolean NOT_IN_ACTION_MODE = false;
    private boolean mInActionMode;


    public static HoursListFragment newInstance(Job parentJob) {
        HoursListFragment ret = new HoursListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_JOB_HOURS_LIST, parentJob);
        ret.setArguments(args);
        return ret;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the view
        View ret = inflater.inflate(R.layout.fragment_hours_list, container, false);
        // Process Fragment arguments.
        // 1 - Job object
        Bundle arguments = getArguments();
        mJob = (Job) arguments.getSerializable(ARG_JOB_HOURS_LIST);
        mListView = (ListView) ret.findViewById(R.id.hours_list_view);
        if (mJob != null) {
            // Get a list of the Hours for this Job
            configureUI();
        } else {
            Log.e(TAG, "Job detail object cannot be null!");
        }
        return ret;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_hours_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Handle New Job
        if (id == R.id.menu_item_hours_new) {
            // Fire off Job Edit Activity
            Intent intent = new Intent(getActivity(), HoursDetailActivity.class);
            // Create an empty Job object to be used by HoursDetailActivity
            Hours hours = new Hours();
            intent.putExtra(HoursDetailActivity.EXTRA_HOURS, hours);
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
        mHoursAdapter = new HoursAdapter(dataStore.getHours(mJob));
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
        mHoursAdapter.updateHours(dataStore.getHours(mJob));
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
        mListView.setAdapter(mHoursAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // Create the OnItemLongClickListener, used to bring up the contextual
        /// actions for the List View
        createOnItemLongClickListener();
        // Create the
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mInActionMode) {
                    Log.d(TAG, "Clicked item " + position);
                    Hours hours = (Hours) mListView.getAdapter().getItem(position);
                    Intent intent = new Intent(getActivity(), HoursDetailActivity.class);
                    intent.putExtra(HoursDetailActivity.EXTRA_HOURS, hours);
                    startActivity(intent);
                }
            }
        });
    }

    private void createOnItemLongClickListener() {
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                boolean ret = false;
                Log.d(TAG, "LONG Clicked item " + position);
                // Long press... It will be handled first. Set the state variable (yuck).
                setInActionMode(IN_ACTION_MODE);
                getActivity().startActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        // Inflate the menu
                        MenuInflater menuInflater = mode.getMenuInflater();
                        menuInflater.inflate(R.menu.menu_hours_ca, menu);
                        // Must return true here or the ART will abort creation of the menu!
                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        boolean ret;
                        Hours hours = (Hours) mListView.getAdapter().getItem(position);
                        // Switch on the menu item ID
                        switch (item.getItemId()) {
                            case R.id.menu_item_job_delete:
                                DataStore.instance(getActivity()).delete(hours);
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
                        Log.d(TAG, "Preparing Action Mode...");
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        Log.d(TAG, "Destroying Action Mode...");
                        // Through with Action Mode. The list view can go back to handling
                        /// short press.
                        setInActionMode(NOT_IN_ACTION_MODE);
                    }
                });
                return ret;
            }
        });
    }
    private class HoursViewHolder {

        private Spinner mJobSpinner;

    }

    private class HoursAdapter extends ArrayAdapter<Hours> {

        private List<Hours> mHours = new ArrayList<>();

        public HoursAdapter(List<Hours> objects) {
            super(getActivity(), 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return super.getView(position, convertView, parent);
        }

        public void updateHours(List<Hours> hours) {
            mHours.clear();
            mHours.addAll(hours);
            notifyDataSetChanged();
        }
    }
}
