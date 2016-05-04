package com.makotogo.mobile.hoursdroid;

import android.app.Fragment;

import com.makotogo.mobile.framework.AbstractSingleFragmentActivity;

/**
 * Created by sperry on 2/6/16.
 */
public class BillingSummaryActivity extends AbstractSingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return FragmentFactory.createReportingSummaryFragment();
    }

    @Override
    protected String getActionBarTitle() {
        return getStringResource(R.string.reporting_summary);
    }

    @Override
    protected String getActionBarSubTitle() {
        return null;
    }
}
