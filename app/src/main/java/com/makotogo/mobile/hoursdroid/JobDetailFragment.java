package com.makotogo.mobile.hoursdroid;

import android.app.Fragment;
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

import com.makotogo.mobile.hoursdroid.model.DataStore;
import com.makotogo.mobile.hoursdroid.model.Job;
import com.makotogo.mobile.hoursdroid.util.SystemOptions;

/**
 * Created by sperry on 1/5/16.
 */
public class JobDetailFragment extends Fragment {
    private static final String TAG = JobDetailFragment.class.getSimpleName();
    /**
     * The key to be used when storing a Job object as a Fragment argument
     */
    public static final String ARG_JOB_DETAIL = "arg." + JobDetailFragment.class.getSimpleName();

    /**
     * The Job that is edited by this Fragment
     */
    private Job mJob;

    /**
     * Create a new instance of this fragment.
     *
     * @param jobToBeEdited The Job object that is to be edited.
     * @return JobDetailFragment - the newly created fragment. Complete with
     * Fragment argument(s).
     */
    public static JobDetailFragment newInstance(Job jobToBeEdited) {
        JobDetailFragment ret = new JobDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_JOB_DETAIL, jobToBeEdited);
        ret.setArguments(args);
        return ret;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the view
        View ret = inflater.inflate(R.layout.fragment_job_detail, container, false);
        // Process Fragment arguments.
        // 1 - Job object to be edited
        Bundle arguments = getArguments();
        mJob = (Job) arguments.getSerializable(ARG_JOB_DETAIL);
        if (mJob != null) {
            // Job Name
            createJobNameEditText(ret);
            // Job Description
            createJobDescriptionEditText(ret);
            // Active Flag
            createActiveFlagCheckBox(ret);
            // Now for the buttons!
            // Save
            createSaveButton(ret);
            // Cancel
            //createCancelButton(ret);
        } else {
            Log.e(TAG, "Job detail object cannot be null!");
        }
        return ret;
    }

//    private void createCancelButton(View ret) {
//        Button cancelButton = (Button)ret.findViewById(R.id.button_job_detail_cancel);
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Hasta la vista, baby.
//                getActivity().finish();
//            }
//        });
//    }

    private void createSaveButton(View ret) {
        Button saveButton = (Button) ret.findViewById(R.id.button_job_detail_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save this object using the DataStore
                DataStore dataStore = DataStore.instance(getActivity());
                if (mJob.getId() == null) {
                    dataStore.addJob(mJob);
                } else {
                    dataStore.updateJob(mJob);
                }
                if (SystemOptions.instance(getActivity()).showNotifications()) {
                    Toast.makeText(getActivity(), "Your changes have been saved.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createActiveFlagCheckBox(View ret) {
        CheckBox activeCheckBox = (CheckBox) ret.findViewById(R.id.checkBox_job_active);
        activeCheckBox.setChecked(mJob.isActive());
        activeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update the model
                mJob.setActive(isChecked);
            }
        });
    }

    private void createJobDescriptionEditText(View ret) {
        EditText jobDescriptionEditText = (EditText) ret.findViewById(R.id.edit_job_description);
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

    private void createJobNameEditText(View ret) {
        EditText jobNameEditText = (EditText) ret.findViewById(R.id.edit_job_name);
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
