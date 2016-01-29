package com.makotogo.mobile.framework;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
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

    protected final String getStringResource(int resourceId) {
        final String METHOD = "getStringResource(" + resourceId + "): ";
        String ret = "RESOURCE STRING NOT FOUND";
        //
        try {
            ret = getActivity().getResources().getString(resourceId);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, METHOD + e.getLocalizedMessage(), e);
        }
        return ret;
    }
}
