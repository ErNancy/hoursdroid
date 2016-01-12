package com.makotogo.mobile.hoursdroid;

import android.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
        return "Hours - Jobs"; //TODO: Externalize in strings.xml
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
