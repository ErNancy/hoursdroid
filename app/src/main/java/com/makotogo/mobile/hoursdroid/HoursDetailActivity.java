package com.makotogo.mobile.hoursdroid;

import android.app.Fragment;

import com.makotogo.mobile.hoursdroid.model.Hours;

/**
 * Created by sperry on 1/13/16.
 */
public class HoursDetailActivity extends AbstractSingleFragmentActivity {

    public static final String EXTRA_HOURS = "extra." + HoursDetailActivity.class.getSimpleName();

    private Hours mHours;

    @Override
    protected Fragment createFragment() {
        mHours = (Hours) getIntent().getSerializableExtra(EXTRA_HOURS);
        HoursDetailFragment fragment = HoursDetailFragment.newInstance(mHours);
        return fragment;
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
