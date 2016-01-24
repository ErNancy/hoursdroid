package com.makotogo.mobile.hoursdroid;

import android.app.Fragment;

import com.makotogo.mobile.framework.AbstractSingleFragmentActivity;
import com.makotogo.mobile.hoursdroid.model.Job;

/**
 * Created by sperry on 1/5/16.
 */
public class JobDetailActivity extends AbstractSingleFragmentActivity {

    public static final String EXTRA_JOB = "extra." + JobDetailActivity.class.getSimpleName();

    /**
     * Not totally sure that Intent data is preserved if the Activity is recreated
     * after being stop()ped or destroy()ed. So let's save it off just in case...
     */
    private Job mJob;

    @Override
    protected Fragment createFragment() {
        mJob = (Job) getIntent().getSerializableExtra(EXTRA_JOB);
        if (mJob == null) {
            throw new RuntimeException("Job object from Intent cannot be null!");
        }
        JobDetailFragment fragment = FragmentFactory.createJobDetailFragment(mJob);
        return fragment;
    }

    @Override
    protected String getActionBarTitle() {
        return "Job Details";
    }

    @Override
    protected String getActionBarSubTitle() {
        return null;
    }
}
