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

import android.view.View;
import android.widget.TextView;

import com.makotogo.mobile.framework.ViewBinder;
import com.makotogo.mobile.hoursdroid.model.Project;

/**
 * Created by sperry on 1/30/16.
 */
class ProjectViewBinder implements ViewBinder<Project> {

    @Override
    public void initView(View view) {
        getNameTextView(view).setText("");
        getDescriptionTextView(view).setText("");
    }

    @Override
    public void bind(Project object, View view) {
        getNameTextView(view).setText(object.getName());
        getDescriptionTextView(view).setText(object.getDescription());
    }

    // TODO: Add clock icon to active project if there is one

    private TextView getNameTextView(View view) {
        return (TextView) view.findViewById(R.id.textview_project_list_row_name);
    }

    private TextView getDescriptionTextView(View view) {
        return (TextView) view.findViewById(R.id.textview_project_list_row_description);
    }

}
