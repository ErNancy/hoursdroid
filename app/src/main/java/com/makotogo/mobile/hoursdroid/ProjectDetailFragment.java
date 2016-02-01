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
import android.widget.EditText;
import android.widget.Toast;

import com.makotogo.mobile.framework.AbstractFragment;
import com.makotogo.mobile.hoursdroid.model.DataStore;
import com.makotogo.mobile.hoursdroid.model.Project;

/**
 * Created by sperry on 1/31/16.
 */
public class ProjectDetailFragment extends AbstractFragment {

    private static final String TAG = ProjectDetailFragment.class.getSimpleName();

    private transient Project mProject;

    @Override
    protected void processFragmentArguments() {
        mProject = (Project) getArguments().getSerializable(FragmentFactory.FRAG_ARG_PROJECT);
        // Sanity Check
        if (mProject == null) {
            // Complain. Loudly.
            throw new RuntimeException("Fragment argument (" + FragmentFactory.FRAG_ARG_PROJECT + ") cannot be null!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_project_detail, container, false);
        // Process Fragment arguments.
        processFragmentArguments();
        // Configure the UI
        configureUI(view);
        return view;
    }

    @Override
    protected void configureUI(View view) {
        // Project Name
        configureProjectNameEditText(view);
        // Project Description
        configureProjectDescriptionEditText(view);
        // Now for the buttons!
        // Save
        configureSaveButton(view);
    }

    @Override
    protected void updateUI() {
        // Nothing to do...
    }

    @Override
    protected void saveInstanceState(Bundle outState) {
        // Nothing to do
    }

    @Override
    protected void restoreInstanceState(Bundle savedInstanceState) {
        // Nothing to do
    }

    @Override
    protected boolean validate(View view) {
        boolean ret = true;
        EditText projectName = (EditText) view.findViewById(R.id.edittext_project_detail_project_name);
        if (projectName.getText() == null || projectName.getText().toString().isEmpty()) {
            projectName.setError("Project Name is a required field.");
            ret = false;
        }
        EditText jobDescription = (EditText) view.findViewById(R.id.edittext_project_detail_project_description);
        if (jobDescription.getText() == null || jobDescription.getText().toString().isEmpty()) {
            jobDescription.setError("Project Description is a required field.");
            ret = false;
        }
        return ret;
    }

    private void configureProjectDescriptionEditText(View ret) {
        EditText projectDescriptionEditText = (EditText) ret.findViewById(R.id.edittext_project_detail_project_description);
        projectDescriptionEditText.setText(mProject.getDescription());
        projectDescriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing to do
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mProject.setDescription(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing to do
            }
        });
    }

    private void configureProjectNameEditText(View ret) {
        EditText jobNameEditText = (EditText) ret.findViewById(R.id.edittext_project_detail_project_name);
        jobNameEditText.setText(mProject.getName());
        jobNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing to do
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mProject.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing to do
            }
        });
    }

    private void configureSaveButton(View view) {
        final String METHOD = "configureSaveButton(View): ";
        Button doneButton = (Button) view.findViewById(R.id.button_project_detail_save);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String METHOD = "onClick(View): ";
                // If valid, save to the DB
                if (validate(ProjectDetailFragment.this.getView())) {
                    DataStore dataStore = DataStore.instance(getActivity());
                    if (mProject.getId() == null) {
                        // Save
                        dataStore.create(mProject);
                    } else {
                        // Update
                        dataStore.update(mProject);
                    }
                    Toast.makeText(getActivity(), "Your changes have been saved.", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                } else {
                    String message = "Cannot save your changes because there are errors. Please correct the errors and try again.";
                    Log.w(TAG, METHOD + message);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
                }
            }
        });
    }
}
