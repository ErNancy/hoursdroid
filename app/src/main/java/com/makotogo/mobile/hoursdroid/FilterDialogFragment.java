package com.makotogo.mobile.hoursdroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.makotogo.mobile.framework.AbstractDialogFragment;
import com.makotogo.mobile.hoursdroid.util.ApplicationOptions;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sperry on 2/9/16.
 */
public class FilterDialogFragment extends AbstractDialogFragment {

    public static final String TAG = FilterDialogFragment.class.getSimpleName();

    public static final String DIALOG_TAG = FilterDialogFragment.class.getName();
    public static final String RESULT_BEGIN_DATE = "result.begin." + Date.class.getName();
    public static final String RESULT_END_DATE = "result.end." + Date.class.getName();
    private static final int BEGIN_DATE = 100;
    private static final int END_DATE = 200;
    private static final String STATE_BEGIN_DATE = "state.begin." + Date.class.getName();
    private static final String STATE_END_DATE = "state.end." + Date.class.getName();
    private Date mBeginDate;
    private Date mEndDate;

    /**
     * This view. Used to retrieve controls on the Dialog.
     */
    private View mView;

    @Override
    protected void processFragmentArguments() {
        mBeginDate = (Date) getArguments().getSerializable(FragmentFactory.FRAG_ARG_FILTER_DIALOG_BEGIN_DATE);
        // Sanity check
        if (mBeginDate == null) {
            mBeginDate = new Date();
        }
        mEndDate = (Date) getArguments().getSerializable(FragmentFactory.FRAG_ARG_FILTER_DIALOG_END_DATE);
        // Sanity check
        if (mEndDate == null) {
            mEndDate = new Date();
        }
        // Sanity check
        if (getTargetFragment() == null) {
            throw new RuntimeException("Target Fragment is null. Cannot continue!");
        }
    }

    /**
     * Handle device rotation and other config changes
     *
     * @param outState
     */
    @Override
    protected void saveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_BEGIN_DATE, mBeginDate);
        outState.putSerializable(STATE_END_DATE, mEndDate);
    }

    /**
     * Handle device rotation and other config changes
     *
     * @param savedInstanceState
     */
    @Override
    protected void restoreInstanceState(Bundle savedInstanceState) {
        mBeginDate = (Date) savedInstanceState.getSerializable(STATE_BEGIN_DATE);
        mEndDate = (Date) savedInstanceState.get(STATE_END_DATE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String METHOD = "onCreateDialog(savedInstanceState: " + (savedInstanceState != null) + "): ";
        Log.d(TAG, METHOD + "BEGIN");
        // Process Fragment arguments
        processFragmentArguments();
        // Restore instance state if necessary
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
        // Create the views that are part of the dialog
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_dialog, null);
        // ListView - contains the filter choices
        ListView choicesListView = (ListView) mView.findViewById(R.id.listview_filter_dialog_choices);
        configureChoicesListView(choicesListView);
        Log.d(TAG, METHOD + "END");
        return new AlertDialog.Builder(getActivity())
                .setView(mView)
                .setTitle(R.string.filter_settings)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Put the results into an Intent
                        Intent intent = new Intent();
                        intent.putExtra(RESULT_BEGIN_DATE, mBeginDate);
                        intent.putExtra(RESULT_END_DATE, mEndDate);
                        getTargetFragment().onActivityResult(
                                getTargetRequestCode(),
                                Activity.RESULT_OK,
                                intent);
                    }
                })
                .create()
                ;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final String METHOD = "onActivityResult(" + requestCode + "," + resultCode + "," + data + "): ";
        Log.d(TAG, METHOD + "BEGIN");
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case BEGIN_DATE:
                    mBeginDate = (Date) data.getSerializableExtra(DateTimePickerFragment.RESULT_DATE_TIME);
                    break;
                case END_DATE:
                    mEndDate = (Date) data.getSerializableExtra(DateTimePickerFragment.RESULT_DATE_TIME);
                    break;
                default:
                    // Do nothing
            }
            updateUI();
        }
        Log.d(TAG, METHOD + "END");
    }

    private void updateUI() {
        //
        List<String> choices = new ArrayList<String>();
        choices.add("Begin Date: " + formatDate(mBeginDate));
        choices.add("End Date: " + formatDate(mEndDate));
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) getListView().getAdapter();
        adapter.clear();
        adapter.addAll(choices);
        adapter.notifyDataSetChanged();
    }

    private ListView getListView() {
        ListView ret = null;
        if (mView != null) {
            ret = (ListView) mView.findViewById(R.id.listview_filter_dialog_choices);
        }
        if (ret == null) {
            throw new RuntimeException("ListView reference cannot be null!");
        }
        return ret;
    }

    private void configureChoicesListView(ListView listView) {
        // Configure the choices the user sees
        List<String> choices = new ArrayList<String>();
        choices.add("Begin Date: " + formatDate(mBeginDate));
        choices.add("End Date: " + formatDate(mEndDate));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, choices);
        listView.setAdapter(arrayAdapter);
        // Add onClickListener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Item (choice) was clicked. Configure that choice by firing off
                /// the DateTimePickerFragment
                FragmentManager fragmentManager = getFragmentManager();
                DateTimePickerFragment dateTimePickerFragment;
                if (position == 0) {
                    // Begin Date
                    dateTimePickerFragment = FragmentFactory.createDatePickerFragment(mBeginDate, "Begin");
                    dateTimePickerFragment.setTargetFragment(FilterDialogFragment.this, BEGIN_DATE);
                } else {
                    // End Date
                    dateTimePickerFragment = FragmentFactory.createDatePickerFragment(mEndDate, "End");
                    dateTimePickerFragment.setTargetFragment(FilterDialogFragment.this, END_DATE);
                }
                dateTimePickerFragment.show(fragmentManager, DateTimePickerFragment.DIALOG_TAG);
            }
        });
    }

    /**
     * Formats the specified date according to the format string from the
     * ApplicationOptions object.
     * <p/>
     * TODO: Move to generic class?
     *
     * @param date
     * @return
     */
    private String formatDate(Date date) {
        ApplicationOptions applicationOptions = ApplicationOptions.instance(getActivity());
        String dateFormatString = applicationOptions.getDateFormatString();
        // Sanity check
        if (dateFormatString == null || dateFormatString.equals("")) {
            throw new RuntimeException("Date Format String cannot be null or empty string.");
        }
        LocalDateTime localDateTime = new LocalDateTime(date.getTime());
        return localDateTime.toString(dateFormatString);
    }

}
