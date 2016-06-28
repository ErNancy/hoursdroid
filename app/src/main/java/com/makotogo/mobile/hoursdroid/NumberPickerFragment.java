/*
 *     Copyright 2016 Makoto Consulting Group, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package com.makotogo.mobile.hoursdroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.makotogo.mobile.framework.AbstractDialogFragment;

/**
 * Created by sperry on 2/3/16.
 */
public class NumberPickerFragment extends AbstractDialogFragment {

    public static final String DIALOG_TAG = NumberPickerFragment.class.getName();

    public static final String RESULT_MINUTES = "result.minutes";
    private static final String TAG = NumberPickerFragment.class.getSimpleName();

    // From Fragment Arguments. No need to save to Bundle
    private String mTitle;
    private Integer mMaxMinutes;
    private Integer mMinutes;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        processFragmentArguments();

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_number_picker, null);

        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.numberpicker_minutes);
        numberPicker.setMaxValue(mMaxMinutes);
        numberPicker.setMinValue(0);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setValue(mMinutes);

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
                            intent.putExtra(RESULT_MINUTES, mMinutes);
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

    @Override
    protected void processFragmentArguments() {
        mMinutes = (Integer) getArguments().getSerializable(FragmentFactory.FRAG_ARG_MINUTES);
        mTitle = (String) getArguments().getSerializable(FragmentFactory.FRAG_ARG_TITLE);
        mMaxMinutes = (Integer) getArguments().getSerializable(FragmentFactory.FRAG_ARG_MAX_MINUTES);
    }

    @Override
    protected void restoreInstanceState(Bundle savedInstanceState) {
        // Nothing to do
    }

    @Override
    protected void saveInstanceState(Bundle outState) {
        // Nothing to do
    }
}
