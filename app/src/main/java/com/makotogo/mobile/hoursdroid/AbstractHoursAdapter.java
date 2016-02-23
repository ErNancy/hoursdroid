package com.makotogo.mobile.hoursdroid;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.makotogo.mobile.framework.AbstractArrayAdapter;
import com.makotogo.mobile.framework.ViewBinder;
import com.makotogo.mobile.hoursdroid.model.Hours;

/**
 * Created by sperry on 2/22/16.
 */
public abstract class AbstractHoursAdapter extends AbstractArrayAdapter<Hours> {

    public AbstractHoursAdapter(Context context, int layoutResourceId) {
        // This constructor indicates we will provide our own View inflation
        super(context, layoutResourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // The Layout ID to which the View will be (or is already) bound
        // Check to see if we need to create a view, or if it is recycled
        if (convertView == null) {
            convertView = getLayoutInflater().inflate(getLayoutResourceId(), null);
            ViewBinder<Hours> hoursViewBinder = createViewBinder();
            // The View bone connected to the Binder bone...
            convertView.setTag(getLayoutResourceId(), hoursViewBinder);
        }
        Hours hours = getItem(position);
        // The Binder bone connected to the View bone...
        ViewBinder<Hours> hoursViewBinder = (ViewBinder<Hours>) convertView.getTag(getLayoutResourceId());
        hoursViewBinder.initView(convertView);
        hoursViewBinder.bind(hours, convertView);
        return convertView;
    }

}
