package com.makotogo.mobile.hoursdroid;

import android.app.Fragment;

import com.makotogo.mobile.framework.AbstractSingleFragmentActivity;
import com.makotogo.mobile.hoursdroid.model.Project;

/**
 * Created by sperry on 1/31/16.
 */
public class ProjectDetailActivity extends AbstractSingleFragmentActivity {

    public static final String EXTRA_PROJECT = "extra." + Project.class.getName();

    private transient Project mProject;

    @Override
    protected Fragment createFragment() {
        mProject = (Project) getIntent().getSerializableExtra(EXTRA_PROJECT);
        // Sanity Check
        if (mProject == null) {
            // Complain. Loudly.
            throw new RuntimeException("Intent extra (" + EXTRA_PROJECT + " cannot be null!");
        }
        ProjectDetailFragment fragment = FragmentFactory.createProjectDetailFragment(mProject);
        return fragment;
    }

    @Override
    protected String getActionBarTitle() {
        return getStringResource(R.string.project_detail);
    }

    @Override
    protected String getActionBarSubTitle() {
        return null;
    }
}
