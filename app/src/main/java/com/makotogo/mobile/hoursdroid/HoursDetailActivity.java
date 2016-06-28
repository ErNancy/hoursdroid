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

package com.makotogo.mobile.hoursdroid;

import android.app.Fragment;
import android.util.Log;

import com.makotogo.mobile.framework.AbstractSingleFragmentActivity;
import com.makotogo.mobile.hoursdroid.model.Hours;

/**
 * Created by sperry on 1/13/16.
 */
public class HoursDetailActivity extends AbstractSingleFragmentActivity {

    private static final String TAG = HoursDetailActivity.class.getSimpleName();

    public static final String EXTRA_HOURS = "extra." + HoursDetailActivity.class.getSimpleName();

    private Hours mHours;

    @Override
    protected Fragment createFragment() {
        final String METHOD = "createFragment(): ";
        Log.d(TAG, METHOD + "...");
        processActivityExtras();
        HoursDetailFragment fragment = FragmentFactory.createHoursDetailFragment(mHours);
        return fragment;
    }

    @Override
    protected void processActivityExtras() {
        final String METHOD = "processActivityExtras(): ";
        mHours = (Hours) getIntent().getSerializableExtra(EXTRA_HOURS);
        Log.d(TAG, METHOD + "Hours extra => " + mHours);
        if (mHours == null) {
            throw new RuntimeException("Hours extra cannot be null!");
        }
    }

    @Override
    protected String getActionBarTitle() {
        return "Hours Detail";
    }

    @Override
    protected String getActionBarSubTitle() {
        return "For Job " + mHours.getJob().getName();
    }
}
