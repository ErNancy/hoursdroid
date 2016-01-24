package com.makotogo.mobile.framework;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

/**
 * Created by sperry on 1/18/16.
 */
public abstract class AbstractFragment extends Fragment {

    private static final String TAG = AbstractFragment.class.getSimpleName();

    protected abstract void processFragmentArguments();

    protected abstract void configureUI(View view);

    protected abstract void updateUI();

    protected abstract void saveInstanceState(Bundle outState);

    protected abstract void restoreInstanceState(Bundle savedInstanceState);

    protected abstract boolean validate(View view);

    @Override
    public void onResume() {
        final String METHOD = "onResume(): ";
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveInstanceState(outState);
    }
}
