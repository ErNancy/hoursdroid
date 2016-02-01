package com.makotogo.mobile.hoursdroid;

import android.app.Fragment;

import com.makotogo.mobile.framework.AbstractSingleFragmentActivity;

/**
 * Created by sperry on 12/30/15.
 */
public class JobListActivity extends AbstractSingleFragmentActivity {

    private static final String TAG = JobListActivity.class.getSimpleName();

    @Override
    protected Fragment createFragment() {
        return FragmentFactory.createJobListFragment();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.app_name);
    }

    @Override
    protected String getActionBarSubTitle() {
        // No subtitle for this activity
        return null;
    }

}
