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
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by sperry on 1/20/16.
 */
public class DateTimePickerFragment extends DialogFragment {

    private static final String TAG = DateTimePickerFragment.class.getSimpleName();

    public static final String EXTRA_DATE_TIME = "extra." + DateTimePickerFragment.class.getName();

    private Date mDate;

    /**
     * Maintain a reference to the DatePicker to work around a bug in Android 5
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

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date_time, null);

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

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.date_picker_title)
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
                            intent.putExtra(EXTRA_DATE_TIME, mDate);
                            getTargetFragment().onActivityResult(
                                    getTargetRequestCode(),
                                    Activity.RESULT_OK,
                                    intent);
                            Log.d(TAG, METHOD + "Sending result back to the caller...");
                        }
                    }
                })
                .create();
    }

    private void processFragmentArguments() {
        mDate = (Date) getArguments().getSerializable(FragmentFactory.FRAG_ARG_DATE);
    }

    private Date computeDateFromComponents(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour) {
        Calendar changedDateCalendar = Calendar.getInstance();
        changedDateCalendar.set(Calendar.YEAR, year);
        changedDateCalendar.set(Calendar.MONTH, monthOfYear);
        changedDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return changedDateCalendar.getTime();
    }
}
