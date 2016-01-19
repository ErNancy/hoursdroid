package com.makotogo.mobile;

import android.app.Fragment;

/**
 * Created by sperry on 1/18/16.
 */
public abstract class AbstractFragment extends Fragment {

    private static final String TAG = AbstractFragment.class.getSimpleName();

    protected abstract void processFragmentArguments();

}
