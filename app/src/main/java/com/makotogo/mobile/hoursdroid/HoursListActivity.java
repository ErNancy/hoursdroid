package com.makotogo.mobile.hoursdroid;

import android.app.Fragment;
import android.util.Log;

import com.makotogo.mobile.framework.AbstractSingleFragmentActivity;
import com.makotogo.mobile.hoursdroid.model.Project;

/**
 * Created by sperry on 1/12/16.
 */
public class HoursListActivity extends AbstractSingleFragmentActivity {

    public static final String EXTRA_PROJECT = "extra." + Project.class.getSimpleName();
    private static final String TAG = HoursListActivity.class.getSimpleName();
    /**
     * This is the Project to which the Hours List items belong.
     */
    private Project mProject;

    @Override
    protected Fragment createFragment() {
        final String METHOD = "createFragment(): ";
        Log.d(TAG, METHOD + "...");
        processActivityExtras();
        HoursListFragment fragment = FragmentFactory.createHoursListFragment(mProject);
        return fragment;
    }

    @Override
    protected void processActivityExtras() {
        mProject = (Project) getIntent().getSerializableExtra(EXTRA_PROJECT);
        // Sanity check
        if (mProject == null) {
            // Complain. Loudly.
            throw new RuntimeException("Cannot create this Activity (" + TAG + ") with null Extra (" + EXTRA_PROJECT + ") object!");
        }
    }

    @Override
    protected String getActionBarTitle() {
        return "Hours";
    }

    @Override
    protected String getActionBarSubTitle() {
        // Use the Job name as the subtitle
        return "For Job " + ((mProject != null) ? mProject.getJob().getName() : "(PROJECT MISSING - CONFIG ERROR?)");
    }

}
