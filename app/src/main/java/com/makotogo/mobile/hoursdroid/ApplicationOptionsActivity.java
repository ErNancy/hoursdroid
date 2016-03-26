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
