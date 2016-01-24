package com.makotogo.mobile.hoursdroid;

import android.app.Fragment;
import android.util.Log;

import com.makotogo.mobile.framework.AbstractSingleFragmentActivity;
import com.makotogo.mobile.hoursdroid.model.Project;

/**
 * Created by sperry on 1/12/16.
 */
public class HoursListActivity extends AbstractSingleFragmentActivity {

    private static final String TAG = HoursListActivity.class.getSimpleName();

    public static final String EXTRA_PROJECT = "extra." + Project.class.getSimpleName();

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
        if (mProject == null) {
            throw new RuntimeException("Cannot create HoursListFragment with null Job object!");
        }
    }

    @Override
    protected String getActionBarTitle() {
        return "Hours";
    }

    @Override
    protected String getActionBarSubTitle() {
        // Use the Job name as the subtitle
        return "For Job " + ((mProject != null) ? mProject.getJob().getName() : "");
    }

}
