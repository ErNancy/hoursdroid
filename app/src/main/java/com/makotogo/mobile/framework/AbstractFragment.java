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
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sperry on 1/18/16.
 */
public abstract class AbstractFragment extends Fragment {

    private static final String TAG = AbstractFragment.class.getSimpleName();

    /**
     * Called by the Framework as part of the View creation process.
     */
    protected abstract void processFragmentArguments();

    /**
     * Called by the Framework as part of the View creation process.
     *
     * @param layoutInflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    protected abstract View configureUI(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * Called by the Framework when the UI needs updating.
     */
    protected abstract void updateUI();

    /**
     * Called by the Framework when the Fragment should save its state (on rotation
     * for example).
     *
     * @param outState
     */
    protected abstract void saveInstanceState(Bundle outState);

    /**
     * Called by the Fragment when its state needs to be restored. This method is dictated
     * by the Framework so this is done consistently across the app.
     *
     * @param savedInstanceState
     */
    protected abstract void restoreInstanceState(Bundle savedInstanceState);

    /**
     * Called by the Fragment when its fields need to be validated (will not apply to all
     * fragments). This method is dictated by the Framework so this is done consistently
     * across the app.
     *
     * @param view
     * @return
     */
    protected abstract boolean validate(View view);

    /**
     * Called by the ART to create the View. Implemented here so that this is done
     * for all Fragments in a consistent manner. Two things need to happen:
     * <p/>
     * 1. The Fragment's arguments (if any) need to be processed.
     * 2. The View itself needs to be configured.
     * 3. Restore the instance state if necessary.
     * <p/>
     * Okay, so that is actually three things. You can count. Congratulations.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Process Fragment arguments.
        processFragmentArguments();
        // Configure the View
        View view = configureUI(inflater, container, savedInstanceState);
        // If the Bundle is present, this Fragment is being recreated, so let's do that
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
        // Return the view
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        updateSharedPreferences();
    }

    protected void updateSharedPreferences() {
        // Nothing to do.
    }

    protected void readSharedPreferences() {
        // Nothing to do.
    }

    @Override
    public void onResume() {
        super.onResume();
        readSharedPreferences();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveInstanceState(outState);
    }

    protected final String getStringResource(int resourceId) {
        final String METHOD = "getStringResource(" + resourceId + "): ";
        String ret = "RESOURCE STRING NOT FOUND";
        //
        try {
            ret = getActivity().getResources().getString(resourceId);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, METHOD + e.getLocalizedMessage(), e);
        }
        return ret;
    }
}
