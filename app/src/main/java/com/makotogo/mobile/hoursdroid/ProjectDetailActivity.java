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
import com.makotogo.mobile.hoursdroid.model.Project;

/**
 * Created by sperry on 1/31/16.
 */
public class ProjectDetailActivity extends AbstractSingleFragmentActivity {

    public static final String EXTRA_PROJECT = "extra." + Project.class.getName();

    public static final String RESULT_PROJECT = "result." + Project.class.getName();

    private transient Project mProject;

    @Override
    protected Fragment createFragment() {
        mProject = (Project) getIntent().getSerializableExtra(EXTRA_PROJECT);
        // Sanity Check
        if (mProject == null) {
            // Complain. Loudly.
            throw new RuntimeException("Intent extra (" + EXTRA_PROJECT + " cannot be null!");
        }
        ProjectDetailFragment fragment = FragmentFactory.createProjectDetailFragment(mProject);
        return fragment;
    }

    @Override
    protected String getActionBarTitle() {
        return getStringResource(R.string.project_detail);
    }

    @Override
    protected String getActionBarSubTitle() {
        return null;
    }
}
