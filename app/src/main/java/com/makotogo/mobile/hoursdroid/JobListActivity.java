package com.makotogo.mobile.hoursdroid;

import android.app.Fragment;
import android.util.Log;

import com.makotogo.mobile.framework.AbstractSingleFragmentActivity;
import com.makotogo.mobile.hoursdroid.model.DataStore;

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
        return getString(R.string.jobs);
    }

    @Override
    protected String getActionBarSubTitle() {
        // No subtitle for this activity
        return null;
    }

    @Override
    public void onPause() {
        final String METHOD = "onPause(): ";
        Log.d(TAG, METHOD + "Closing DataStore...");
        super.onPause();
        // Make sure and tidy this up or we could leak a DB connection
        DataStore.instance(this).close();
    }
}
