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
