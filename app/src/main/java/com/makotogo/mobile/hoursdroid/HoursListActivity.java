package com.makotogo.mobile.hoursdroid;

import android.app.Fragment;
import android.util.Log;

import com.makotogo.mobile.hoursdroid.model.Job;

/**
 * Created by sperry on 1/12/16.
 */
public class HoursListActivity extends AbstractSingleFragmentActivity {

    private static final String TAG = HoursListActivity.class.getSimpleName();

    public static final String EXTRA_JOB = "extra." + HoursListActivity.class.getSimpleName();

    private static final String STATE_JOB = "state." + Job.class.getSimpleName();

    /**
     * This is the Job to which the Hours List items belong.
     */
    private Job mJob;

    @Override
    protected Fragment createFragment() {
        final String METHOD = "createFragment(): ";
        Log.d(TAG, METHOD + "...");
        processActivityExtras();
        HoursListFragment fragment = HoursListFragment.newInstance(mJob);
        return fragment;
    }

    @Override
    protected void processActivityExtras() {
        mJob = (Job) getIntent().getSerializableExtra(EXTRA_JOB);
        if (mJob == null) {
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
        return "For Job " + ((mJob != null) ? mJob.getName() : "");
    }

}
