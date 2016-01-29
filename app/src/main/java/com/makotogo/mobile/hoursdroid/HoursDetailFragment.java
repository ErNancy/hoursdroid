package com.makotogo.mobile.hoursdroid;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makotogo.mobile.framework.AbstractFragment;
import com.makotogo.mobile.hoursdroid.model.Hours;

import java.util.Date;

/**
 * Created by sperry on 1/13/16.
 */
public class HoursDetailFragment extends AbstractFragment {

    private static final String TAG = HoursDetailFragment.class.getSimpleName();

    private static final String DIALOG_TAG_DATE_PICKER = DateTimePickerFragment.class.getName();
    private static final int DIALOG_ID_BEGIN_DATE_PICKER = 100;

    private Hours mHours;

    @Override
    protected void processFragmentArguments() {
        mHours = (Hours) getArguments().getSerializable(FragmentFactory.FRAG_ARG_HOURS);
        if (mHours == null) {
            throw new RuntimeException("Fragment argument (Hours) cannot be null!");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_hours_detail, container, false);
        // Process Fragment arguments.
        processFragmentArguments();
        if (mHours == null) {
            throw new RuntimeException("Fragment argument (Hours) cannot be null!");
        }
        return ret;
    }

    @Override
    protected void configureUI(View view) {
        // Job Spinner
        // Project Spinner
        // Begin Date
        createBeginDate(view);
        // End Date
        createEndDate(view);
    }

    @Override
    public void saveInstanceState(Bundle outState) {
        // Nothing to do
    }

    @Override
    public void restoreInstanceState(Bundle savedInstanceState) {
        // Nothing to do
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final String METHOD = "onActivityResult(" + requestCode + ", " + resultCode + ", " + data + "): ";
        Log.d(TAG, METHOD + "Howdy");
        if (resultCode == Activity.RESULT_OK) {
            // Figure out which Result code we are dealing with. This method
            /// handles the results of all dialog fragments used to set the
            /// model data.
            switch (requestCode) {
                case DIALOG_ID_BEGIN_DATE_PICKER:
                    Date newDate = (Date) data.getSerializableExtra(DateTimePickerFragment.EXTRA_DATE_TIME);
                    Log.d(TAG, METHOD + "Date set to: " + DateUtils.formatDateTime(getActivity(), newDate.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
                    mHours.setBegin(newDate);
                    updateUI();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void updateUI() {
        if (mHours.getBegin() != null) {
            ((TextView) getView().findViewById(R.id.textview_hours_detail_begin_date))
                    .setText(DateUtils.formatDateTime(
                            getActivity(),
                            mHours.getBegin().getTime(),
                            DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
        }
    }

    @Override
    protected boolean validate(View view) {
        // TODO: Add validation logic here
        return true;
    }

    private void createBeginDate(View view) {
        TextView beginDateTextView = (TextView) view.findViewById(R.id.textview_hours_detail_begin_date);
        beginDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                // If there is already a Date displayed, use that.
                Date dateToUse = (mHours.getBegin() == null) ? new Date() : mHours.getBegin();
                DateTimePickerFragment datePickerFragment = FragmentFactory.createDatePickerFragment(dateToUse);
                datePickerFragment.setTargetFragment(HoursDetailFragment.this, DIALOG_ID_BEGIN_DATE_PICKER);
                datePickerFragment.show(fragmentManager, DIALOG_TAG_DATE_PICKER);
            }
        });
        if (mHours.getBegin() != null) {
            beginDateTextView.setText(
                    DateUtils.formatDateTime(
                            getActivity(),
                            mHours.getBegin().getTime(),
                            DateUtils.FORMAT_SHOW_DATE)
            );
        }
    }

    private void createEndDate(View view) {
        TextView endDateTextView = (TextView) view.findViewById(R.id.textview_hours_detail_end_date);
        // TODO: Add OnClick listener so when View is pressed, the TimePicker fragment displays
    }
}
