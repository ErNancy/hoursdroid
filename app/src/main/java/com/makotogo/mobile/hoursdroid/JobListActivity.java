package com.makotogo.mobile.hoursdroid;

import android.app.Fragment;
import android.util.Log;

import com.makotogo.mobile.hoursdroid.model.DataStore;

/**
 * Created by sperry on 12/30/15.
 */
public class JobListActivity extends AbstractSingleFragmentActivity {

    private static final String TAG = JobListActivity.class.getSimpleName();

    @Override
    protected Fragment createFragment() {
        return new JobListFragment();
    }

    @Override
    protected String getActionBarTitle() {
        return "Jobs"; //TODO: Externalize in strings.xml
    }

    @Override
    protected String getActionBarSubTitle() {
        // No subtitle for this activity
        return null;
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()...");
        super.onPause();
        // Make sure and tidy this up or we could leak a DB connection
        DataStore.instance(this).close();
        Log.d(TAG, "Done.");
    }
}
