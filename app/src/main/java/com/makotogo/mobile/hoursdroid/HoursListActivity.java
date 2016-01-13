package com.makotogo.mobile.hoursdroid;

import android.app.Fragment;

import com.makotogo.mobile.hoursdroid.model.Job;

/**
 * Created by sperry on 1/12/16.
 */
public class HoursListActivity extends AbstractSingleFragmentActivity {

    public static final String EXTRA_JOB = "extra." + HoursListActivity.class.getSimpleName();

    /**
     * This is the Job to which the Hours List items belong.
     */
    private Job mJob;

    @Override
    protected Fragment createFragment() {
        mJob = (Job) getIntent().getSerializableExtra(EXTRA_JOB);
        HoursListFragment fragment = HoursListFragment.newInstance(mJob);
        return fragment;
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
