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
