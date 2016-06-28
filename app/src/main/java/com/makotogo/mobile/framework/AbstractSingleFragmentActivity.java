/*
 *     Copyright 2016 Makoto Consulting Group, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package com.makotogo.mobile.framework;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.makotogo.mobile.hoursdroid.R;
import com.makotogo.mobile.hoursdroid.model.DataStore;

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

    @Override
    public void onPause() {
        final String METHOD = "onPause(): ";
        Log.d(TAG, METHOD + "Closing DataStore...");
        super.onPause();
        // Make sure and tidy this up or we could leak a DB connection
        DataStore.instance(this).close();
        Log.d(TAG, METHOD + "DONE.");
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
     * Helper method to retrieve string resources.
     *
     * @param resourceId
     * @return
     */
    protected final String getStringResource(int resourceId) {
        final String METHOD = "getStringResource(" + resourceId + "): ";
        String ret = "RESOURCE " + resourceId + " NOT FOUND";
        try {
            ret = getResources().getString(resourceId);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, METHOD + e.getLocalizedMessage(), e);
        }
        return ret;
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
