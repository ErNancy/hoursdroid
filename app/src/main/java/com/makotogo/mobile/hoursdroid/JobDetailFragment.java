package com.makotogo.mobile.hoursdroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.makotogo.mobile.framework.AbstractFragment;
import com.makotogo.mobile.hoursdroid.model.DataStore;
import com.makotogo.mobile.hoursdroid.model.Job;
import com.makotogo.mobile.hoursdroid.util.ApplicationOptions;

/**
 * Created by sperry on 1/5/16.
 */
public class JobDetailFragment extends AbstractFragment {
    private static final String TAG = JobDetailFragment.class.getSimpleName();

    /**
     * The Job that is edited by this Fragment
     */
    private Job mJob;

    @Override
    protected void processFragmentArguments() {
        Bundle arguments = getArguments();
        mJob = (Job) arguments.getSerializable(FragmentFactory.FRAG_ARG_JOB);
        if (mJob == null) {
            throw new RuntimeException("Fragment argument (" + FragmentFactory.FRAG_ARG_JOB + ") cannot be null!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_job_detail, container, false);
        // Process Fragment arguments.
        processFragmentArguments();
        // Configure the UI
        configureUI(view);
        return view;
    }

    @Override
    public void saveInstanceState(Bundle outState) {
        // Nothing to do...
    }

    @Override
    public void restoreInstanceState(Bundle savedInstanceState) {
        // Nothing to do...
    }

    @Override
    protected void configureUI(View view) {
        // Job Name
        configureJobNameEditText(view);
        // Job Description
        configureJobDescriptionEditText(view);
        // Active Flag
        configureActiveFlagCheckBox(view);
        // Now for the buttons!
        // Save
        configureSaveButton(view);
        // Cancel
        //createCancelButton(ret);
    }

    @Override
    protected void updateUI() {
        // Nothing to do...
    }

    @Override
    protected boolean validate(View view) {
        boolean ret = true;
        // Validation logic here
        EditText jobName = (EditText) view.findViewById(R.id.edittext_job_detail_job_name);
        if (jobName.getText() == null || jobName.getText().toString().isEmpty()) {
            jobName.setError("Job Name is a required field.");
            ret = false;
        }
        EditText jobDescription = (EditText) view.findViewById(R.id.edittext_job_detail_job_description);
        if (jobDescription.getText() == null || jobDescription.getText().toString().isEmpty()) {
            jobDescription.setError("Job Description is a required field.");
            ret = false;
        }
        CheckBox activeCheckBox = (CheckBox) view.findViewById(R.id.checkBox_job_detail_job_active);
        if (!activeCheckBox.isChecked() && DataStore.instance(getActivity()).hasActiveHours(mJob)) {
            String message = "Cannot deactivate: Job has active Hours.";
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            activeCheckBox.setError(message);
            ret = false;
        }
        return ret;
    }

    private void configureSaveButton(View ret) {
        Button saveButton = (Button) ret.findViewById(R.id.button_job_detail_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String METHOD = "onClick(" + v + "): ";
                if (validate(JobDetailFragment.this.getView())) {
                    // Save this object using the DataStore
                    DataStore dataStore = DataStore.instance(getActivity());
                    if (mJob.getId() == null) {
                        Job newlyCreatedJobObject = dataStore.create(mJob);
                        if (newlyCreatedJobObject == null) {
                            Toast.makeText(getActivity(), "Cannot save this job (job name must be unique). Please try again.", Toast.LENGTH_LONG).show();
                        } else if (ApplicationOptions.instance(getActivity()).showNotifications(true)) {
                            Toast.makeText(getActivity(), "Your changes have been saved.", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                    } else {
                        int numRowsUpdated = dataStore.update(mJob);
                        if (numRowsUpdated == 0) {
                            Toast.makeText(getActivity(), "Cannot save your changes (job name must be unique). Please try again.", Toast.LENGTH_LONG).show();
                        } else if (ApplicationOptions.instance(getActivity()).showNotifications(true)) {
                            Toast.makeText(getActivity(), "Your changes have been saved.", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                    }
                } else {
                    // Validation errors
                    String message = "Cannot save your changes because there are errors. Please correct the errors and try again.";
                    Log.w(TAG, METHOD + message);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
                }
            }
        });
    }

    private void configureActiveFlagCheckBox(View ret) {
        CheckBox activeCheckBox = (CheckBox) ret.findViewById(R.id.checkBox_job_detail_job_active);
        activeCheckBox.setChecked(mJob.isActive());
        activeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update the model
                mJob.setActive(isChecked);
            }
        });
    }

    private void configureJobDescriptionEditText(View ret) {
        EditText jobDescriptionEditText = (EditText) ret.findViewById(R.id.edittext_job_detail_job_description);
        jobDescriptionEditText.setText(mJob.getDescription());
        jobDescriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing to do
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mJob.setDescription(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing to do
            }
        });
    }

    private void configureJobNameEditText(View ret) {
        EditText jobNameEditText = (EditText) ret.findViewById(R.id.edittext_job_detail_job_name);
        jobNameEditText.setText(mJob.getName());
        jobNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing to do
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mJob.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing to do
            }
        });
    }

}
