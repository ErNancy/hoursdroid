package com.makotogo.mobile.hoursdroid;

import android.app.Fragment;

import com.makotogo.mobile.hoursdroid.model.Job;

/**
 * Created by sperry on 1/5/16.
 */
public class JobDetailActivity extends AbstractSingleFragmentActivity {

    public static final String EXTRA_JOB = "extra." + JobDetailActivity.class.getSimpleName();

    @Override
    protected Fragment createFragment() {
        Job job = (Job) getIntent().getSerializableExtra(EXTRA_JOB);
        JobDetailFragment fragment = JobDetailFragment.newInstance(job);
        return fragment;
    }

    @Override
    protected String getActionBarTitle() {
        return "Hours - Job Details";
    }
}
