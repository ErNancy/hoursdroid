package com.makotogo.mobile.framework;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by sperry on 2/6/16.
 */
public abstract class AbstractDialogFragment extends DialogFragment {

    protected abstract void processFragmentArguments();

    @Override
    public abstract Dialog onCreateDialog(Bundle savedInstanceState);

    protected abstract void saveInstanceState(Bundle outState);

    protected abstract void restoreInstanceState(Bundle savedInstanceState);

    @Override
    public void onSaveInstanceState(Bundle outState) {
        saveInstanceState(outState);
    }

}
