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
            throw new RuntimeException("Intent extra (" + EXTRA_JOB + " cannot be null!");
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
