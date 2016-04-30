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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.makotogo.mobile.framework.AbstractDialogFragment;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by sperry on 1/20/16.
 */
public class DateTimePickerFragment extends AbstractDialogFragment {

    public static final String DIALOG_TAG = DateTimePickerFragment.class.getName();

    public static final String RESULT_DATE_TIME = "result." + DateTimePickerFragment.class.getName();
    public static final String TIME = "Time";
    public static final String DATE = "Date";

    private static final String TAG = DateTimePickerFragment.class.getSimpleName();

    private Date mDate;
    private String mDateType;
    private String mInitialChoice;

    /**
     * Maintain a reference to the DatePicker to work around a bug in Android 5.
     * The OnWhatever() listener does not work, so whenver we need to pull the values
     * in the Pickers, we have to reference them directly. We do not, however, need
     * to save these in the Bundle because they can always be recreated if necessary
     * (on device rotation, say).
     */
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Pull the date out of the Fragment Arguments
        processFragmentArguments();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_date_time, null);

        // Spinner to choose either Date or Time to edit
        final Spinner dateTimeSpinner = (Spinner) view.findViewById(R.id.spinner_date_time_choice);
        mDatePicker = (DatePicker) view.findViewById(R.id.date_picker);
        mTimePicker = (TimePicker) view.findViewById(R.id.time_picker);
        // Note: the OnDateChangedListener does not work in Android 5 when using the
        /// super cool material style of calendar, which is really slick.
        mDatePicker.init(year, month, day, null);

        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteOfHour = calendar.get(Calendar.MINUTE);
        //noinspection deprecation
        mTimePicker.setCurrentHour(hourOfDay);
        //noinspection deprecation
        mTimePicker.setCurrentMinute(minuteOfHour);

        configureDateTimeSpinner(dateTimeSpinner);
        // Now show the Dialog in all its glory!
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.please_choose)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String METHOD = "onClick(" + dialog + ", " + which + "): ";
                        if (getTargetFragment() == null) {
                            Log.e(TAG, "Target Fragment is null!");
                        } else {
                            //noinspection deprecation
                            mDate = computeDateFromComponents(
                                    mDatePicker.getYear(),
                                    mDatePicker.getMonth(),
                                    mDatePicker.getDayOfMonth(),
                                    mTimePicker.getCurrentHour(),
                                    mTimePicker.getCurrentMinute());
                            Intent intent = new Intent();
                            intent.putExtra(RESULT_DATE_TIME, mDate);
                            getTargetFragment().onActivityResult(
                                    getTargetRequestCode(),
                                    Activity.RESULT_OK,
                                    intent);
                            Log.d(TAG, METHOD + "Sending result back to the caller...:" +
                                    new LocalDateTime(mDate.getTime()).toString("MM/dd/yyyy hh:mm a"));
                        }
                    }
                })
                .create();
    }

    @Override
    protected void restoreInstanceState(Bundle savedInstanceState) {
        // Nothing to do
    }

    @Override
    protected void saveInstanceState(Bundle outState) {
        // Nothing to do
    }

    private void configureDateTimeSpinner(final Spinner dateTimeSpinner) {
        List<String> choices = new ArrayList<>();
        choices.add(computeChoice(DATE));
        choices.add(computeChoice(TIME));
        dateTimeSpinner.setAdapter(
                new ArrayAdapter<String>(
                        getActivity(),
                        android.R.layout.simple_list_item_1,
                        choices));
        dateTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choice = (String) dateTimeSpinner.getAdapter().getItem(position);
                if (choice.equalsIgnoreCase(computeChoice(DATE))) {
                    // Make the DatePicker visible
                    mDatePicker.setVisibility(View.VISIBLE);
                    mTimePicker.setVisibility(View.GONE);
                } else {
                    // Make the TimePicker visible
                    mTimePicker.setVisibility(View.VISIBLE);
                    mDatePicker.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Select initial choice.
        dateTimeSpinner.setSelection(choices.indexOf(computeChoice(mInitialChoice)));
    }

    private String computeChoice(String baseChoice) {
        return mDateType + " " + baseChoice;
    }

    @Override
    protected void processFragmentArguments() {
        mDate = (Date) getArguments().getSerializable(FragmentFactory.FRAG_ARG_DATE);
        // Sanity check
        if (mDate == null) {
            throw new RuntimeException("Fragment argument (" + FragmentFactory.FRAG_ARG_DATE + ") cannot be null!");
        }
        mDateType = (String) getArguments().getSerializable(FragmentFactory.FRAG_ARG_DATE_TYPE);
        // Sanity check
        if (mDateType == null || mDateType.isEmpty()) {
            throw new RuntimeException("Fragment argument (" + FragmentFactory.FRAG_ARG_DATE_TYPE + ") cannot be null!");
        }
        mInitialChoice = (String) getArguments().getSerializable(FragmentFactory.FRAG_ARG_DATETIME_PICKER_INITIAL_CHOICE);
        if (mInitialChoice == null || mInitialChoice.isEmpty()) {
            mInitialChoice = TIME;
        }
    }

    private Date computeDateFromComponents(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour) {
        Calendar changedDateCalendar = Calendar.getInstance();
        changedDateCalendar.set(Calendar.YEAR, year);
        changedDateCalendar.set(Calendar.MONTH, monthOfYear);
        changedDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        changedDateCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        changedDateCalendar.set(Calendar.MINUTE, minuteOfHour);
        changedDateCalendar.set(Calendar.SECOND, 0);
        changedDateCalendar.set(Calendar.MILLISECOND, 0);
        return changedDateCalendar.getTime();
    }
}
