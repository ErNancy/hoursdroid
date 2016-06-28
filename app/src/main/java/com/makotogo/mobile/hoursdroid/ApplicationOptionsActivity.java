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

/**
 * Created by sperry on 3/26/16.
 */
public class ApplicationOptionsActivity extends AbstractSingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return FragmentFactory.createApplicationOptionsFragment();
    }

    @Override
    protected String getActionBarTitle() {
        return "Application Options";
    }

    @Override
    protected String getActionBarSubTitle() {
        return null;
    }
}
