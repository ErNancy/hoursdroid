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

import android.os.Bundle;

import com.makotogo.mobile.hoursdroid.model.Hours;
import com.makotogo.mobile.hoursdroid.model.Job;
import com.makotogo.mobile.hoursdroid.model.Project;

import java.util.Date;

/**
 * Created by sperry on 1/19/16.
 */
public class FragmentFactory {

    public static final String FRAG_ARG_PREFIX = "fragment.argument.";
    public static final String FRAG_ARG_JOB = FRAG_ARG_PREFIX + Job.class.getName();
    public static final String FRAG_ARG_PROJECT = FRAG_ARG_PREFIX + Project.class.getName();
    public static final String FRAG_ARG_HOURS = FRAG_ARG_PREFIX + Hours.class.getName();
    public static final String FRAG_ARG_DATE = FRAG_ARG_PREFIX + Date.class.getName();
    public static final String FRAG_ARG_MINUTES = FRAG_ARG_PREFIX + "minutes";
    public static final String FRAG_ARG_MAX_MINUTES = FRAG_ARG_PREFIX + "max.minutes";
    public static final String FRAG_ARG_TITLE = FRAG_ARG_PREFIX + "title";
    public static final String FRAG_ARG_DATE_TYPE = FRAG_ARG_DATE + "Type";
    public static final String FRAG_ARG_FILTER_DIALOG_BEGIN_DATE = FRAG_ARG_PREFIX + "begin.date";
    public static final String FRAG_ARG_FILTER_DIALOG_END_DATE = FRAG_ARG_PREFIX + "end.date";
    public static final String FRAG_ARG_DATETIME_PICKER_CHOICE = FRAG_ARG_PREFIX + "date.picker.choice";

    /**
     * Creates the JoblistFragment.
     *
     * @return JobListFragment
     */
    public static JobListFragment createJobListFragment() {
        return new JobListFragment();
    }

    /**
     * Creates the JobDetailFragment.
     *
     * @param jobToBeEdited The Job object for which details are to be edited.
     * @return JobDetailFragment
     */
    public static JobDetailFragment createJobDetailFragment(Job jobToBeEdited) {
        JobDetailFragment ret = new JobDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(FRAG_ARG_JOB, jobToBeEdited);
        ret.setArguments(args);
        return ret;
    }

    /**
     * Creates the HoursListFragment.
     *
     * @param job The Job object for which any Hours records listed.
     * @return HoursListFragment
     */
    public static HoursListFragment createHoursListFragment(Job job) {
        HoursListFragment ret = new HoursListFragment();
        Bundle args = new Bundle();
        args.putSerializable(FRAG_ARG_JOB, job);
        ret.setArguments(args);
        return ret;
    }

    /**
     * Creates the HoursDetailFragment.
     *
     * @param hours The Hours object for which details are to be edited.
     * @return HoursDetailFragment
     */
    public static HoursDetailFragment createHoursDetailFragment(Hours hours) {
        HoursDetailFragment ret = new HoursDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(FRAG_ARG_HOURS, hours);
        ret.setArguments(args);
        return ret;
    }

    /**
     * Creates the DatePickerFragment. Allows the user to set the Date and/or the Time. Due
     * to space limitations on mobile devices, only one or the other of the DatePicker or
     * TimePicker can be displayed at one time. This method allows the caller to specify
     * which (DatePicker or TimePicker) is displayed initially.
     *
     * @param date          The Date to display on the Date Picker
     * @param dateType      The date type, used for display purposes
     * @param choice        The  choice to display to the user. One of:
     *                      <ol>
     *                      <li>{@link com.makotogo.mobile.hoursdroid.DateTimePickerFragment#DATE}</li>
     *                      <li>{@link com.makotogo.mobile.hoursdroid.DateTimePickerFragment#TIME}</li>
     *                      <li>{@link com.makotogo.mobile.hoursdroid.DateTimePickerFragment#BOTH}</li>
     *                      </ol>
     * @return DateTimePickerFragment
     */
    public static DateTimePickerFragment createDatePickerFragment(Date date, String dateType, String choice) {
        DateTimePickerFragment ret = new DateTimePickerFragment();
        Bundle args = new Bundle();
        args.putSerializable(FRAG_ARG_DATE, date);
        args.putSerializable(FRAG_ARG_DATE_TYPE, dateType);
        args.putSerializable(FRAG_ARG_DATETIME_PICKER_CHOICE, choice);
        ret.setArguments(args);
        return ret;
    }

    /**
     * Creates the ProjectListFragment
     *
     * @param job The Job to display Projects for.
     * @return ProjectListFragment
     */
    public static ProjectListFragment createProjectListFragment(Job job) {
        ProjectListFragment ret = new ProjectListFragment();
        Bundle args = new Bundle();
        args.putSerializable(FRAG_ARG_JOB, job);
        ret.setArguments(args);
        return ret;
    }

    /**
     * Creates the ProjectDetailFragment
     *
     * @param project The Project to be edited
     * @return ProjectDetailFragment
     */
    public static ProjectDetailFragment createProjectDetailFragment(Project project) {
        ProjectDetailFragment ret = new ProjectDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(FRAG_ARG_PROJECT, project);
        ret.setArguments(args);
        return ret;
    }

    /**
     * Creates the NumberPickerFragment
     *
     * @param numberOfMinutes The number of minutes to initialize the display
     * @param maxMinutes      The maximum number of minutes in the picker wheel
     * @param title           The title to display in the picker so that different types of numbers
     *                        can be chosen, and the dialog reused.
     * @return NumberPickerFragment
     */
    public static NumberPickerFragment createNumberPickerFragment(Integer numberOfMinutes, Integer maxMinutes, String title) {
        NumberPickerFragment ret = new NumberPickerFragment();
        Bundle args = new Bundle();
        args.putSerializable(FRAG_ARG_MINUTES, numberOfMinutes);
        args.putSerializable(FRAG_ARG_MAX_MINUTES, maxMinutes);
        args.putSerializable(FRAG_ARG_TITLE, title);
        ret.setArguments(args);
        return ret;
    }

    /**
     * Creates the BillingSummaryFragment
     *
     * @return BillingSummaryFragment
     */
    public static BillingSummaryFragment createReportingSummaryFragment() {
        BillingSummaryFragment ret = new BillingSummaryFragment();
        // No args
        return ret;
    }

    /**
     * Creates the FilterDialogFragment
     *
     * @param beginDate
     * @param endDate
     * @return FilterDialogFragment
     */
    public static FilterDialogFragment createFilterDialogFragment(Date beginDate, Date endDate) {
        FilterDialogFragment ret = new FilterDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(FRAG_ARG_FILTER_DIALOG_BEGIN_DATE, beginDate);
        args.putSerializable(FRAG_ARG_FILTER_DIALOG_END_DATE, endDate);
        ret.setArguments(args);
        return ret;
    }

    public static ApplicationOptionsFragment createApplicationOptionsFragment() {
        ApplicationOptionsFragment ret = new ApplicationOptionsFragment();
        // No arguments
        return ret;
    }

}
