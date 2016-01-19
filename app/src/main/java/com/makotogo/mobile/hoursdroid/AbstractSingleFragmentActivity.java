package com.makotogo.mobile.hoursdroid;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public abstract class AbstractSingleFragmentActivity extends AppCompatActivity {

    private static final String TAG = AbstractSingleFragmentActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String METHOD = "onCreate(" + savedInstanceState + "): ";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        if (savedInstanceState == null) {
            Log.d(TAG, METHOD + "Creating fresh instance of this Activity...");
            FragmentManager fragmentManager = getFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
            if (fragment == null) {
                Log.d(TAG, METHOD + "Creating Fragment for the first time...");
                fragment = createFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit();
            }
        } else {
            processActivityExtras();
        }
        // For the record, I do not like this. Because of some mystery appcompat-v7
        /// dependency, I am forced to use getSupportActionBar() or the framework
        /// NPEs on startup... sheesh.
        getSupportActionBar().setTitle(getActionBarTitle());
        // Set the subtitle.
        String actionBarSubtitle = getActionBarSubTitle();
        if (actionBarSubtitle != null && !actionBarSubtitle.equals("")) {
            getSupportActionBar().setSubtitle(actionBarSubtitle);
        }
    }

    /**
     * Process any Extras for this Activity. By default, there are none,
     * so if that is not the case, override this method and process the
     * extras.
     */
    protected void processActivityExtras() {
        // By default, there are no extras.
    }

    /**
     * Creates the Fragment hosted by this Activity subclass.
     * Must be implemented by subclasses.
     *
     * @return Fragment - the fragment to be hosted by this activity
     * subclass.
     */
    protected abstract Fragment createFragment();

    /**
     * Returns the action bar title to be used by the subclass.
     *
     * @return String - the action bar title.
     */
    protected abstract String getActionBarTitle();

    /**
     * Returns the action bar subtitle to be used by the subclass.
     * If null or empty string, then no subtitle will be set.
     *
     * @return String - the action bar subtitle, or null (or empty
     * string) if no subtitle is to be used.
     */
    protected abstract String getActionBarSubTitle();
}
