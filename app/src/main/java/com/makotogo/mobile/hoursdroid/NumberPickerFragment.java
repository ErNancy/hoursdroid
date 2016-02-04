package com.makotogo.mobile.hoursdroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by sperry on 2/3/16.
 */
public class NumberPickerFragment extends DialogFragment {

    public static final String EXTRA_MINUTES = "extra.minutes";
    private static final String TAG = NumberPickerFragment.class.getSimpleName();
    private transient String mTitle;
    private transient Integer mMaxMinutes;
    private Integer mMinutes;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        processFragmentArguments();

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_number_picker, null);

        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.numberpicker_minutes);
        numberPicker.setMaxValue(mMaxMinutes);
        numberPicker.setMinValue(0);
        numberPicker.setWrapSelectorWheel(true);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(mTitle)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String METHOD = "onClick(" + dialog + ", " + which + "): ";
                        if (getTargetFragment() == null) {
                            Log.e(TAG, "Target Fragment is null!");
                        } else {
                            mMinutes = numberPicker.getValue();
                            //noinspection deprecation
                            Intent intent = new Intent();
                            intent.putExtra(EXTRA_MINUTES, mMinutes);
                            getTargetFragment().onActivityResult(
                                    getTargetRequestCode(),
                                    Activity.RESULT_OK,
                                    intent);
                            Log.d(TAG, METHOD + "Sending result (" + mMinutes + ") back to the caller...:");
                        }
                    }
                })
                .create()
                ;
    }

    private void processFragmentArguments() {
        mMinutes = (Integer) getArguments().getSerializable(FragmentFactory.FRAG_ARG_MINUTES);
        mTitle = (String) getArguments().getSerializable(FragmentFactory.FRAG_ARG_TITLE);
        mMaxMinutes = (Integer) getArguments().getSerializable(FragmentFactory.FRAG_ARG_MAX_MINUTES);
    }

}
