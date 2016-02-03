package com.makotogo.mobile.hoursdroid;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import java.util.Date;
import java.util.List;

/**
 * Created by sperry on 1/13/16.
 */
public class HoursDetailFragment extends AbstractFragment {

    public static final int REQUEST_CODE_MANAGE_PROJECTS = 200;
    private static final String TAG = HoursDetailFragment.class.getSimpleName();
    private static final String DIALOG_TAG_DATE_PICKER = DateTimePickerFragment.class.getName();
    private static final int REQUEST_BEGIN_DATE_PICKER = 100;
    private static final int REQUEST_END_DATE_PICKER = 110;
    private static final String DATE_FORMAT_PATTERN = "M/d/yyyy h:mm a";

    private transient Hours mHours;

    @Override
    protected void processFragmentArguments() {
        mHours = (Hours) getArguments().getSerializable(FragmentFactory.FRAG_ARG_HOURS);
        if (mHours == null) {
            throw new RuntimeException("Fragment argument (Hours) cannot be null!");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_hours_detail, container, false);
        // Process Fragment arguments.
        processFragmentArguments();
        if (mHours == null) {
            throw new RuntimeException("Fragment argument (Hours) cannot be null!");
        }
        configureUI(ret);
        return ret;
    }

    @Override
    protected void configureUI(View view) {
        // Job Spinner
        configureJobSpinner(view);
        // Project Spinner
        configureProjectSpinner(view);
        // Begin Date
        configureBeginDate(view);
        // End Date
        configureEndDate(view);
        // Break Time
        configureBreakTime(view);
        // Total Time
        configureTotalTime(view);
        // Description
        configureDescription(view);
        // Save Button
        configureSaveButton(view);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final String METHOD = "onActivityResult(" + requestCode + ", " + resultCode + ", " + data + "): ";
        Log.d(TAG, METHOD + "Howdy");
        if (resultCode == Activity.RESULT_OK) {
            // Figure out which Result code we are dealing with. This method
            /// handles the results of all dialog fragments used to set the
            /// model data.
            switch (requestCode) {
                case REQUEST_BEGIN_DATE_PICKER:
                    Date beginDate = (Date) data.getSerializableExtra(DateTimePickerFragment.EXTRA_DATE_TIME);
                    LocalDateTime ldtBeginDate = new LocalDateTime(beginDate.getTime());
                    Log.d(TAG, METHOD + "Begin Date set to: " + ldtBeginDate.toString(DATE_FORMAT_PATTERN));
                    if (ldtBeginDate.isBefore(new LocalDateTime(mHours.getEnd().getTime()))) {
                        mHours.setBegin(beginDate);
                        updateUI();
                    } else {
                        String message = "End date must be after begin date";
                        Log.e(TAG, METHOD + message);
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    }
                    break;
                case REQUEST_END_DATE_PICKER:
                    Date endDate = (Date) data.getSerializableExtra(DateTimePickerFragment.EXTRA_DATE_TIME);
                    LocalDateTime ldtEndDate = new LocalDateTime(endDate.getTime());
                    Log.d(TAG, METHOD + "End Date set to: " + ldtEndDate.toString(DATE_FORMAT_PATTERN));
                    if (ldtEndDate.isAfter(new LocalDateTime(mHours.getBegin().getTime()))) {
                        mHours.setEnd(endDate);
                        updateUI();
                    } else {
                        String message = "End date must be after begin date";
                        Log.e(TAG, METHOD + message);
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    }
                    break;
                case REQUEST_CODE_MANAGE_PROJECTS:
                    Project project = (Project) data.getSerializableExtra(ProjectListActivity.RESULT_PROJECT);
                    Log.d(TAG, METHOD + "Project set to " + project);
                    mHours.setProject(project);
                    updateUI();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void updateUI() {
        if (mHours.getBegin() != null) {
            LocalDateTime beginDateTime = new LocalDateTime(mHours.getBegin().getTime());
            ((TextView) getView().findViewById(R.id.textview_hours_detail_begin_date))
                    .setText(beginDateTime.toString(DATE_FORMAT_PATTERN));
        }
        if (mHours.getEnd() != null) {
            LocalDateTime endDateTime = new LocalDateTime(mHours.getEnd().getTime());
            ((TextView) getView().findViewById(R.id.textview_hours_detail_end_date))
                    .setText(endDateTime.toString(DATE_FORMAT_PATTERN));
        }
        DataStore dataStore = DataStore.instance(getActivity());
        List<Project> projects = dataStore.getProjects(mHours.getJob());
        projects.add(Project.MANAGE_PROJECTS);
        AbstractArrayAdapter<Project> projectListAdapter = getProjectListAdapter();
        if (projectListAdapter != null) {
            projectListAdapter.clear();
            projectListAdapter.addAll(projects);
            projectListAdapter.notifyDataSetChanged();
            int selectedIndex = 0;
            // Figure out which selection corresponds to the active project
            for (int aa = 0; aa < projectListAdapter.getCount(); aa++) {
                if (projectListAdapter.getItem(aa).equals(mHours.getProject())) {
                    selectedIndex = aa;
                    break;
                }
            }
            // Select the active project
            getProjectSpinner().setSelection(selectedIndex);
        }
    }

    @Override
    protected boolean validate(View view) {
        // TODO: Add validation logic here
        // Begin Date must be before End Date
        // End Date must be after Before Date
        return true;
    }

    private void configureJobSpinner(View view) {
        final String METHOD = "configureProjectSpinner(Spinner): ";
        Log.d(TAG, METHOD + "...");
        Log.d(TAG, METHOD + "DONE.");
    }

    private void configureProjectSpinner(View view) {
        final Spinner projectSpinner = (Spinner) view.findViewById(R.id.spinner_hours_detail_project);
        projectSpinner.setAdapter(new AbstractArrayAdapter(getActivity(), R.layout.project_list_row) {
            @Override
            protected ViewBinder<Project> createViewBinder() {
                return new ProjectViewBinder();
            }
        });
        projectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Project project = (Project) projectSpinner.getAdapter().getItem(position);
                if (project == Project.MANAGE_PROJECTS) {
                    // Launch the Project List Screen
                    Intent intent = new Intent(getActivity(), ProjectListActivity.class);
                    intent.putExtra(ProjectListActivity.EXTRA_JOB, mHours.getJob());
                    //Toast.makeText(getActivity(), "Launching ProjectListActivity (eventually)...", Toast.LENGTH_LONG).show();
                    startActivityForResult(intent, REQUEST_CODE_MANAGE_PROJECTS);
                } else {
                    // Active project has changed. Update the UI.
                    mHours.setProject(project);
                    updateUI();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing to do
            }
        });
    }

    private Spinner getProjectSpinner() {
        View view = getView();
        if (view == null) {
            throw new RuntimeException("View has not yet been configured. Cannot invoke getProjectSpinner()!");
        }
        return (Spinner) view.findViewById(R.id.spinner_hours_detail_project);
    }

    private AbstractArrayAdapter<Project> getProjectListAdapter() {
        AbstractArrayAdapter<Project> ret = null;
        if (getProjectSpinner() == null) {
            throw new RuntimeException("Project Spinner has not been configured!");
        }
        ret = (AbstractArrayAdapter<Project>) getProjectSpinner().getAdapter();
        return ret;
    }

    private void configureBeginDate(View view) {
        TextView beginDateTextView = (TextView) view.findViewById(R.id.textview_hours_detail_begin_date);
        beginDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                // If there is already a Date displayed, use that.
                Date dateToUse = (mHours.getBegin() == null) ? new Date() : mHours.getBegin();
                DateTimePickerFragment datePickerFragment = FragmentFactory.createDatePickerFragment(dateToUse);
                datePickerFragment.setTargetFragment(HoursDetailFragment.this, REQUEST_BEGIN_DATE_PICKER);
                datePickerFragment.show(fragmentManager, DIALOG_TAG_DATE_PICKER);
            }
        });
        if (mHours.getBegin() != null) {
            LocalDateTime beginDateTime = new LocalDateTime(mHours.getBegin().getTime());
            beginDateTextView.setText(
                    beginDateTime.toString(DATE_FORMAT_PATTERN)
            );
        }
    }

    private void configureEndDate(View view) {
        TextView endDateTextView = (TextView) view.findViewById(R.id.textview_hours_detail_end_date);
        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                // If there is already a Date displayed, use that.
                Date dateToUse = (mHours.getEnd() == null) ? new Date() : mHours.getEnd();
                DateTimePickerFragment datePickerFragment = FragmentFactory.createDatePickerFragment(dateToUse);
                datePickerFragment.setTargetFragment(HoursDetailFragment.this, REQUEST_END_DATE_PICKER);
                datePickerFragment.show(fragmentManager, DIALOG_TAG_DATE_PICKER);
            }
        });
        if (mHours.getEnd() != null) {
            LocalDateTime endDateTime = new LocalDateTime(mHours.getEnd().getTime());
            endDateTextView.setText(endDateTime.toString(DATE_FORMAT_PATTERN));
        }
    }

    private void configureBreakTime(View view) {

    }

    private void configureTotalTime(View view) {

    }

    private void configureDescription(View view) {

    }

    private void configureSaveButton(View view) {

    }
}
