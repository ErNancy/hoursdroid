package com.makotogo.mobile.hoursdroid;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.makotogo.mobile.framework.AbstractArrayAdapter;
import com.makotogo.mobile.framework.AbstractFragment;
import com.makotogo.mobile.framework.ViewBinder;
import com.makotogo.mobile.hoursdroid.model.DataStore;
import com.makotogo.mobile.hoursdroid.model.Hours;
import com.makotogo.mobile.hoursdroid.model.Project;
import com.makotogo.mobile.hoursdroid.util.ApplicationOptions;
import com.makotogo.mobile.hoursdroid.util.RoundingUtils;

import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Date;
import java.util.List;

/**
 * Created by sperry on 1/13/16.
 */
public class HoursDetailFragment extends AbstractFragment {

    public static final int REQUEST_CODE_MANAGE_PROJECTS = 200;
    // Logging
    private static final String TAG = HoursDetailFragment.class.getSimpleName();
    // Request IDs that will be used to identify which dialog is coming back with
    /// a result for us.
    private static final int REQUEST_BEGIN_DATE_PICKER = 100;
    private static final int REQUEST_END_DATE_PICKER = 110;
    private static final int REQUEST_BREAK = 120;
    // Don'tcha hate repeating yourself??
    private static final String DATE_FORMAT_PATTERN = "M/d/yyyy h:mm a";

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
     * The Hours object we are editing. It can easily be reconstituted from fragment
     * arguments Bundle, so we don't need to save it, but having it here as a class
     * variable is oh-so convenient.
     */
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

    /**
     * Called by the Framework as part of the View creation process.
     *
     * @param layoutInflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    protected View configureUI(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        final String METHOD = "configureUI(...): ";
        View view = layoutInflater.inflate(R.layout.fragment_hours_detail, container, false);
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
        // Billed
        configureBilled(view);
        // Description
        configureDescription(view);
        // Save Button
        configureSaveButton(view);

        return view;
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
        if (resultCode == Activity.RESULT_OK) {
            // Figure out which Result code we are dealing with. This method
            /// handles the results of all dialog fragments used to set the
            /// model data.
            switch (requestCode) {
                case REQUEST_BEGIN_DATE_PICKER:
                    Date beginDate = (Date) data.getSerializableExtra(DateTimePickerFragment.RESULT_DATE_TIME);
                    LocalDateTime ldtBeginDate = new LocalDateTime(beginDate.getTime());
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
                    Date endDate = (Date) data.getSerializableExtra(DateTimePickerFragment.RESULT_DATE_TIME);
                    LocalDateTime ldtEndDate = new LocalDateTime(endDate.getTime());
                    if (ldtEndDate.isAfter(new LocalDateTime(mHours.getBegin().getTime()))) {
                        mHours.setEnd(endDate);
                        updateUI();
                    } else {
                        String message = "End date must be after begin date";
                        Log.e(TAG, METHOD + message);
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    }
                    break;
                case REQUEST_BREAK:
                    Integer breakTimeInMinutes = (Integer) data.getSerializableExtra(NumberPickerFragment.RESULT_MINUTES);
                    mHours.setBreak(renderBreakForStorage(breakTimeInMinutes));
                    updateUI();
                    break;
                case REQUEST_CODE_MANAGE_PROJECTS:
                    Project project = (Project) data.getSerializableExtra(ProjectListActivity.RESULT_PROJECT);
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
        updateBegin();
        updateEnd();
        updateBreak();
        updateTotal();
        updateBilled();
        updateProjectSpinner();
    }

    private void updateProjectSpinner() {
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

    private void updateBegin() {
        if (mHours.getBegin() != null) {
            LocalDateTime beginDateTime = new LocalDateTime(mHours.getBegin().getTime());
            ((TextView) getView().findViewById(R.id.textview_hours_detail_begin_date))
                    .setText(beginDateTime.toString(DATE_FORMAT_PATTERN));
        }
    }

    private void updateEnd() {
        if (mHours.getEnd() != null) {
            LocalDateTime endDateTime = new LocalDateTime(mHours.getEnd().getTime());
            ((TextView) getView().findViewById(R.id.textview_hours_detail_end_date))
                    .setText(endDateTime.toString(DATE_FORMAT_PATTERN));
        }
    }

    private void updateBreak() {
        long breakMillis = 0L;
        if (mHours.getBreak() != null) {
            breakMillis = mHours.getBreak();
        }
        ((TextView) getView().findViewById(R.id.textview_hours_detail_break))
                .setText(renderTimePeriodForDisplay(breakMillis));
    }

    private void updateTotal() {
        long totalMillis = 0L;
        if (mHours.getEnd() != null) {
            long beginMillis = (mHours.getBegin() == null) ? 0L : mHours.getBegin().getTime();
            long endMillis = (mHours.getEnd() == null) ? 0L : mHours.getEnd().getTime();
            long breakMillis = (mHours.getBreak() == null) ? 0L : mHours.getBreak();
            totalMillis = endMillis - beginMillis - breakMillis;
            totalMillis = RoundingUtils.applyRoundingIfNecessary(getActivity(), totalMillis);
        }
        Log.d(TAG, "Updating millis with value => " + totalMillis);
        ((TextView) getView().findViewById(R.id.textview_hours_detail_total))
                .setText(renderTimePeriodForDisplay(totalMillis));
    }

    private void updateBilled() {
        // Nothing to do
    }

    @Override
    protected boolean validate(View view) {
        return true;
    }

    private void configureProjectSpinner(View view) {
        final Spinner projectSpinner = (Spinner) view.findViewById(R.id.spinner_hours_detail_project);
        projectSpinner.setEnabled(isThisHoursRecordNotActive());
        if (isThisHoursRecordActive()) {
            projectSpinner.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.no_border, null));
        } else {
            projectSpinner.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_border, null));
        }
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
        AbstractArrayAdapter<Project> ret;
        if (getProjectSpinner() == null) {
            throw new RuntimeException("Project Spinner has not been configured!");
        }
        ret = (AbstractArrayAdapter<Project>) getProjectSpinner().getAdapter();
        return ret;
    }

    private void configureBeginDate(View view) {
        TextView beginDateTextView = (TextView) view.findViewById(R.id.textview_hours_detail_begin_date);
        beginDateTextView.setEnabled(isThisHoursRecordNotActive());
        if (isThisHoursRecordActive()) {
            beginDateTextView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.no_border, null));
        } else {
            beginDateTextView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_border, null));
        }
        beginDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                // If there is already a Date displayed, use that.
                Date dateToUse = (mHours.getBegin() == null) ? new Date() : mHours.getBegin();
                DateTimePickerFragment datePickerFragment = FragmentFactory.createDatePickerFragment(dateToUse, "Begin", DateTimePickerFragment.BOTH);
                datePickerFragment.setTargetFragment(HoursDetailFragment.this, REQUEST_BEGIN_DATE_PICKER);
                datePickerFragment.show(fragmentManager, DateTimePickerFragment.DIALOG_TAG);
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
        endDateTextView.setEnabled(isThisHoursRecordNotActive());
        if (isThisHoursRecordActive()) {
            endDateTextView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.no_border, null));
        } else {
            endDateTextView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_border, null));
        }
        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                // If there is already a Date displayed, use that.
                Date dateToUse = (mHours.getEnd() == null) ? new Date() : mHours.getEnd();
                DateTimePickerFragment datePickerFragment = FragmentFactory.createDatePickerFragment(dateToUse, "End", DateTimePickerFragment.BOTH);
                datePickerFragment.setTargetFragment(HoursDetailFragment.this, REQUEST_END_DATE_PICKER);
                datePickerFragment.show(fragmentManager, DateTimePickerFragment.DIALOG_TAG);
            }
        });
        if (mHours.getEnd() != null) {
            LocalDateTime endDateTime = new LocalDateTime(mHours.getEnd().getTime());
            endDateTextView.setText(endDateTime.toString(DATE_FORMAT_PATTERN));
        }
    }

    private void configureBreakTime(View view) {
        TextView breakTimeTextView = (TextView) view.findViewById(R.id.textview_hours_detail_break);
        breakTimeTextView.setEnabled(isThisHoursRecordNotActive());
        if (isThisHoursRecordActive()) {
            breakTimeTextView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.no_border, null));
        } else {
            breakTimeTextView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_border, null));
        }
        breakTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                Integer minutes = mHours.getBreak().intValue();
                // Max minutes can at most be number of minutes diff between end and begin
                Integer maxMinutes = (int) (mHours.getEnd().getTime() - mHours.getBegin().getTime()) / 60000;
                NumberPickerFragment numberPickerFragment = FragmentFactory.createNumberPickerFragment(minutes, maxMinutes, "Break Time");
                numberPickerFragment.setTargetFragment(HoursDetailFragment.this, REQUEST_BREAK);
                numberPickerFragment.show(fragmentManager, NumberPickerFragment.DIALOG_TAG);
            }
        });
        breakTimeTextView.setText(renderTimePeriodForDisplay(mHours.getBreak()));
    }

    private void configureTotalTime(View view) {
        TextView totalTimeTextView = (TextView) view.findViewById(R.id.textview_hours_detail_total);
        if (isThisHoursRecordNotActive()) {
            long elapsedTime = mHours.getEnd().getTime() - mHours.getBegin().getTime() - mHours.getBreak();
            elapsedTime = RoundingUtils.applyRoundingIfNecessary(getActivity(), elapsedTime);
            Period period = new Period(elapsedTime);
            totalTimeTextView.setText(sPeriodFormatter.print(period));
        }
    }

    private void configureBilled(View view) {
        CheckBox billedCheckBox = (CheckBox) view.findViewById(R.id.checkbox_hours_detail_billed);
        billedCheckBox.setEnabled(isThisHoursRecordNotActive());
        billedCheckBox.setChecked(mHours.isBilled());
        if (isThisHoursRecordActive()) {
            billedCheckBox.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.no_border, null));
        } else {
            billedCheckBox.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_border, null));
        }
        billedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mHours.setBilled(isChecked);
            }
        });
    }

    private void configureDescription(View view) {
        EditText descriptionEditText = (EditText) view.findViewById(R.id.edittext_hours_detail_description);
        descriptionEditText.setText(mHours.getDescription());
        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing to do
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mHours.setDescription(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing to do
            }
        });
    }

    private void configureSaveButton(View view) {
        Button saveButton = (Button) view.findViewById(R.id.button_hours_detail_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String METHOD = "configureSaveButton(View)->onClick(View): ";
                // Validate and save the Hours record
                if (validate(HoursDetailFragment.this.getView())) {
                    DataStore dataStore = DataStore.instance(getActivity());
                    if (dataStore.update(mHours) > 0) {
                        if (ApplicationOptions.instance(getActivity()).showNotifications()) {
                            Toast.makeText(getActivity(), "Your changes have been saved.", Toast.LENGTH_SHORT).show();
                        }
                        getActivity().finish();
                    }
                } else {
                    Log.w(TAG, METHOD + "Validation errors prevented saving the Hours record.");
                }
            }
        });
    }

    private String renderTimePeriodForDisplay(long breakTimeInMillis) {
        Period period = new Period(breakTimeInMillis);
        return sPeriodFormatter.print(period);
    }

    private Long renderBreakForStorage(long breakTimeInMinutes) {
        Long breakTimeInMillis = breakTimeInMinutes * 60000L;
        return breakTimeInMillis;
    }

    private boolean isThisHoursRecordNotActive() {
        return !isThisHoursRecordActive();
    }

    private boolean isThisHoursRecordActive() {
        // If the End time is null, then this record is active.
        return mHours.getEnd() == null;
    }

}
