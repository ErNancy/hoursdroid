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
import com.makotogo.mobile.hoursdroid.model.Job;
import com.makotogo.mobile.hoursdroid.model.Project;

/**
 * Created by sperry on 1/30/16.
 */
public class ProjectListActivity extends AbstractSingleFragmentActivity {

    public static final String EXTRA_JOB = "extra." + Job.class.getName();
    public static final String RESULT_PROJECT = "result." + Project.class.getName();
    private static final String TAG = ProjectListActivity.class.getSimpleName();
    /**
     * This is just for convenience. This object is not stored as part of the Activity
     * state, since it will be retained in the Extras.
     */
    private transient Job mJob;

    @Override
    protected Fragment createFragment() {
        final String METHOD = "createFragment(): ";
        Log.d(TAG, METHOD + "...");
        processActivityExtras();
        ProjectListFragment fragment = FragmentFactory.createProjectListFragment(mJob);
        return fragment;
    }

    @Override
    protected String getActionBarTitle() {
        return getStringResource(R.string.projects);
    }

    @Override
    protected String getActionBarSubTitle() {
        // Use the Job name as the subtitle
        return "For Job " + ((mJob != null) ? mJob.getName() : "(JOB MISSING - CONFIG ERROR?)");
    }

    @Override
    protected void processActivityExtras() {
        mJob = (Job) getIntent().getSerializableExtra(EXTRA_JOB);
        // Sanity check
        if (mJob == null) {
            // Complain. Loudly.
            throw new RuntimeException("Cannot create this Activity (" + TAG + ") with empty Extra (" + EXTRA_JOB + ")!");
        }
    }
}
