package com.makotogo.mobile.hoursdroid;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.makotogo.mobile.framework.AbstractArrayAdapter;
import com.makotogo.mobile.framework.AbstractFragment;
import com.makotogo.mobile.framework.ViewBinder;
import com.makotogo.mobile.hoursdroid.model.DataStore;
import com.makotogo.mobile.hoursdroid.model.Hours;
import com.makotogo.mobile.hoursdroid.model.Project;

import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Date;
import java.util.List;

/**
 * Created by sperry on 1/12/16.
 */
public class HoursListFragment extends AbstractFragment {

    private static final String TAG = HoursListFragment.class.getSimpleName();

    /**
     * This is the Project for which a new Hours record will be started if the
     * user clicks the Start button
     */
    private transient Project mProject;

    private static final String STATE_ACTIVE_HOURS = "state." + Hours.class.getName();

    /**
     * This is the Hours object that is "active" meaning it has been started
     * but not yet stopped. Note: it may be paused, but if this Fragment gets
     * completely destroyed (under low memory conditions, say), there is really
     * no good way to know that. However, should the Fragment be recreated
     * because, say, the device was rotated, then we want to keep up with that.
     * <p/>
     * Saved to Bundle: yes
     */
    private Hours mActiveHours;

    private static final boolean IN_ACTION_MODE = true;
    private static final boolean NOT_IN_ACTION_MODE = false;
    private transient boolean mInActionMode;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final String METHOD = "onCreateView(" + inflater + ", " + container + ", " + savedInstanceState + "): ";
        Log.d(TAG, METHOD + "...");
        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_hours_list, container, false);
        // Process Fragment arguments.
        processFragmentArguments();
        configureUI(view);
        return view;
    }

    @Override
    protected void processFragmentArguments() {
        Bundle arguments = getArguments();
        mProject = (Project) arguments.getSerializable(FragmentFactory.FRAG_ARG_PROJECT);
        if (mProject == null) {
            throw new RuntimeException("Fragment argument (Job) cannot be null!");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final String METHOD = "onViewCreated(View, Bundle): ";
        Log.d(TAG, METHOD + "...");
        super.onViewCreated(view, savedInstanceState);
        updateUI();
        Log.d(TAG, METHOD + "DONE.");
    }

    @Override
    public void saveInstanceState(Bundle outState) {
        // Save the Active Hours object
        outState.putSerializable(STATE_ACTIVE_HOURS, mActiveHours);
    }

    @Override
    public void restoreInstanceState(Bundle savedInstanceState) {
        // Restore the Active Hours object
        mActiveHours = (Hours) savedInstanceState.getSerializable(STATE_ACTIVE_HOURS);
    }

    /**
     * Configure the UI.
     */
    protected void configureUI(View view) {
        Log.d(TAG, "configureUI()...");
        Spinner projectSpinner = (Spinner) view.findViewById(R.id.spinner_hours_list_project);
        projectSpinner.setAdapter(new AbstractArrayAdapter(getActivity(), R.layout.project_list_row) {
            @Override
            protected ViewBinder<Project> createViewBinder() {
                return new ProjectViewBinder();
            }
        });
        ListView listView = (ListView) view.findViewById(R.id.hours_list_view);
        // Access the Job backing store singleton
        DataStore dataStore = DataStore.instance(getActivity());
        configureListView(listView);
        // Create Start/Stop button
        createStartStopButton(view);
        Log.d(TAG, "configureUI()... DONE");
    }

    /**
     * Update the UI:
     * - Reload the list of Jobs from the DataStore
     * - Update the JobAdapter with the refreshed List
     * - Update the View
     */
    protected void updateUI() {
        final String METHOD = "updateUI()...";
        Log.d(TAG, METHOD);
        DataStore dataStore = DataStore.instance(getActivity());
        // Now refresh the view
        List<Hours> hours = dataStore.getHours(mProject);
        if (getHoursListViewAdapter() != null) {
            Log.d(TAG, METHOD + "Adding " + hours.size() + " Hours objects to the list...");
            getHoursListViewAdapter().clear();
            getHoursListViewAdapter().addAll(hours);
            getHoursListViewAdapter().notifyDataSetChanged();
        }
        List<Project> projects = dataStore.getProjects(mProject.getJob());
        AbstractArrayAdapter<Project> projectListAdapter = getProjectListAdapter();
        if (projectListAdapter != null) {
            projectListAdapter.clear();
            projectListAdapter.addAll(projects);
            projectListAdapter.notifyDataSetChanged();
            // TODO: Figure out which selection corresponds to the active project
            getProjectSpinner().setSelection(0, true);
        }
        if (isActive()) {
            if (getView() != null) {
                Log.d(TAG, METHOD + "Setting Start/Stop button text...");
                ((Button) getView().findViewById(R.id.button_hours_start_stop))
                        .setText(getActivity().getResources().getText(R.string.stop_work));
            }
        }
        Log.d(TAG, "updateUI()...DONE");
    }

    @Override
    protected boolean validate(View view) {
        // TODO: Add validation logic here
        return true;
    }

    private Spinner getProjectSpinner() {
        View view = getView();
        if (view == null) {
            throw new RuntimeException("View has not yet been configured. Cannot invoke getProjectSpinner()!");
        }
        return (Spinner) view.findViewById(R.id.spinner_hours_list_project);
    }

    private AbstractArrayAdapter<Project> getProjectListAdapter() {
        AbstractArrayAdapter<Project> ret = null;
        if (getProjectSpinner() == null) {
            throw new RuntimeException("Project Spinner has not been configured!");
        }
        ret = (AbstractArrayAdapter<Project>) getProjectSpinner().getAdapter();
        return ret;
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

    private ListView getHoursListView() {
        View view = getView();
        if (view == null) {
            throw new RuntimeException("View has not yet been configured. Cannot invoke getHoursListView()!");
        }
        return (ListView) view.findViewById(R.id.hours_list_view);
    }

    private HoursAdapter getHoursListViewAdapter() {
        HoursAdapter ret = null;
        if (getHoursListView() == null) {
            throw new RuntimeException("ListView has not been configured!");
        }
        ret = (HoursAdapter) getHoursListView().getAdapter();
        return ret;
    }

    private void configureListView(final ListView listView) {
        // Create a new JobAdapter
        listView.setAdapter(new HoursAdapter(R.layout.hours_list_row));
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // Create the OnItemLongClickListener, used to bring up the contextual
        /// actions for the List View
        createOnItemLongClickListener(listView);
        // Create the
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String METHOD = "onItemClick(): ";
                if (!mInActionMode) {
                    Log.d(TAG, "Clicked item " + position);
                    Hours hours = (Hours) listView.getAdapter().getItem(position);
                    if (hours.equals(mActiveHours)) {
                        String message = "Cannot edit the active Hours record.";
                        Log.w(TAG, METHOD + message);
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(getActivity(), HoursDetailActivity.class);
                        intent.putExtra(HoursDetailActivity.EXTRA_HOURS, hours);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void createOnItemLongClickListener(final ListView listView) {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//                final String METHOD = "onItemLongClick(" + parent + ", " + view + ", " + position + ", " + id + "): ";
                final String METHOD = "onItemLongClick(): ";
                boolean ret = false;
                // If the selected item is the active one, then do nothing (except display
                /// a Toast)
                final Hours hours = (Hours) listView.getAdapter().getItem(position);
                if (hours.equals(mActiveHours)) {
                    String message = "Cannot edit the active Hours record.";
                    Log.w(TAG, METHOD + message);
                } else {
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
                            final String METHOD = "onActionItemClicked(" + mode + ", " + item + "): ";
                            boolean ret;
                            // Switch on the menu item ID
                            switch (item.getItemId()) {
                                case R.id.menu_item_hours_delete:
                                    Log.d(TAG, METHOD + "Deleting item...");
                                    DataStore.instance(getActivity()).delete(hours);
                                    Log.d(TAG, METHOD + "Done.");
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
                } // End else
                return ret;
            }
        });
    }

    private void createStartStopButton(View view) {
        final Button startStopButton = (Button) view.findViewById(R.id.button_hours_start_stop);
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Button was clicked. Now what?
                startOrStopHours();
                startStopButton.setText(
                        getActivity().getResources().getText(
                                (isActive()) ? R.string.stop_work : R.string.start_work
                        )
                );
            }
        });
    }

    private void setStartStopButtonTextToStop() {
        final String METHOD = "setStartStopButtonTextToStop(): ";
        if (getView() != null) {
            Button startStopButton = (Button) getView().findViewById(R.id.button_hours_start_stop);
            startStopButton.setText(getActivity().getResources().getText(R.string.stop_work));
        } else {
            Log.d(TAG, METHOD + "Sadly, unable to do this...");
        }
    }

    private void setStartStopButtonTextToStart() {
        final String METHOD = "setStartStopButtonTextToStart(): ";
        if (getView() != null) {
            Button startStopButton = (Button) getView().findViewById(R.id.button_hours_start_stop);
            startStopButton.setText(getActivity().getResources().getText(R.string.start_work));
        } else {
            Log.d(TAG, METHOD + "Sadly, unable to do this...");
        }
    }

    private boolean isActive() {
        return mActiveHours != null;
    }

    private void startOrStopHours() {
        DataStore dataStore = DataStore.instance(getActivity());
        if (mActiveHours == null) {
            // No active Hours, start one
            Hours hours = new Hours();
            hours.setBegin(new Date());
            hours.setProject(mProject);
            hours.setJob(mProject.getJob());
            mActiveHours = dataStore.create(hours);
            // Add mActiveHours to the List
        } else {
            // Active hours, stop it now
            mActiveHours.setEnd(new Date());
            dataStore.update(mActiveHours);
            mActiveHours = null;
        }
        // Now update the UI
        updateUI();
    }

    /**
     * Note: do not refactor this class outside of HoursListFragment, because
     * it needs to infer the "active" Hours object, and set a reference to
     * mActiveHours on the parent. Once refactored outside, that is no longer
     * possible, and that inference cannot be made.
     */
    private class HoursViewBinder implements ViewBinder<Hours> {

        private TextView getBeginDate(View view) {
            return (TextView) view.findViewById(R.id.textview_hours_list_row_begin_date);
        }

        private TextView getEndDate(View view) {
            return (TextView) view.findViewById(R.id.textview_hours_list_row_end_date);
        }

        private TextView getBreak(View view) {
            return (TextView) view.findViewById(R.id.textview_hours_list_row_break);
        }

        private TextView getTotal(View view) {
            return (TextView) view.findViewById(R.id.textview_hours_list_row_total);
        }

        @Override
        public void initView(View view) {
            getBeginDate(view).setText("");
            getEndDate(view).setText("");
            getBreak(view).setText("");
            getTotal(view).setText("");
        }

        @Override
        public void bind(Hours hours, View view) {
            final String METHOD = "bind(" + hours + ", View): ";
            PeriodFormatter periodFormatter = new PeriodFormatterBuilder()
                    .printZeroNever()
                    .appendDays().appendSuffix("d")
                    .appendSeparator(", ")
                    .appendHours().appendSuffix("h")
                    .appendSeparator(": ")
                    .appendMinutes().appendSuffix("m")
                    .appendSeparator(": ")
                    .appendSeconds().appendSuffix("s")
                    .toFormatter();
            long beginTime = hours.getBegin().getTime();
            long breakTime = hours.getBreak();
            String dateTimeFormatString = "M/d/yy h:mm a";
            // Begin Date
            LocalDateTime beginDateTime = new LocalDateTime(beginTime);
            getBeginDate(view).setText(beginDateTime.toString(dateTimeFormatString));
            if (hours.getEnd() == null) {
                // Found the active record
                Log.d(TAG, METHOD + "Found the active hours record...");
                // NOTE: DO NOT REFACTOR THIS CLASS OUTSIDE OF ITS CONTAINING CLASS!!
                mActiveHours = hours;
                setStartStopButtonTextToStop();
                // Start the animation for this row
                // TODO: Find the root layout, and set it to blinking, man!
            } else {
                long endTime = hours.getEnd().getTime();
                LocalDateTime endDateTime = new LocalDateTime(endTime);
                // End Date
                getEndDate(view).setText(endDateTime.toString(dateTimeFormatString));
                // Total
                long elapsedTime = endTime - beginTime - breakTime;
                Period period = new Period(elapsedTime);
                getTotal(view).setText(periodFormatter.print(period));
                // Break
                period = new Period(breakTime);
                getBreak(view).setText(periodFormatter.print(period));
            }
        }
    }

    private class HoursAdapter extends AbstractArrayAdapter<Hours> {

        public HoursAdapter(int layoutResourceId) {
            // This constructor indicates we will provide our own View inflation
            super(getActivity(), layoutResourceId);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // The Layout ID to which the View will be (or is already) bound
            // Check to see if we need to create a view, or if it is recycled
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(getLayoutResourceId(), null);
                ViewBinder<Hours> hoursViewBinder = createViewBinder();
                // The View bone connected to the Binder bone...
                convertView.setTag(getLayoutResourceId(), hoursViewBinder);
            }
            Hours hours = getItem(position);
            // The Binder bone connected to the View bone...
            HoursViewBinder hoursViewBinder = (HoursViewBinder) convertView.getTag(getLayoutResourceId());
            hoursViewBinder.initView(convertView);
            hoursViewBinder.bind(hours, convertView);
            return convertView;
        }

        @Override
        protected ViewBinder<Hours> createViewBinder() {
            return new HoursViewBinder();
        }
    }

    private class ProjectViewBinder implements ViewBinder<Project> {

        @Override
        public void initView(View view) {
            getNameTextView(view).setText("");
            getDescriptionTextView(view).setText("");
        }

        @Override
        public void bind(Project object, View view) {
            getNameTextView(view).setText(object.getName());
            getDescriptionTextView(view).setText(object.getDescription());
        }

        private TextView getNameTextView(View view) {
            return (TextView) view.findViewById(R.id.textview_project_list_row_name);
        }

        private TextView getDescriptionTextView(View view) {
            return (TextView) view.findViewById(R.id.textview_project_list_row_description);
        }

    }

    // A   T   T   I   C
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_hours_list, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        // Handle New Job
//        if (id == R.id.menu_item_hours_new) {
//            startHoursActivity();
//
//            // Handled
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
}
