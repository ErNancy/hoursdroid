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

    public static final String FRAG_ARG_DATE_TYPE = FRAG_ARG_DATE + "Type";
    private static final String FRAG_ARG_PREFIX = "fragment.argument.";
    public static final String FRAG_ARG_JOB = FRAG_ARG_PREFIX + Job.class.getName();
    public static final String FRAG_ARG_PROJECT = FRAG_ARG_PREFIX + Project.class.getName();
    public static final String FRAG_ARG_HOURS = FRAG_ARG_PREFIX + Hours.class.getName();
    public static final String FRAG_ARG_DATE = FRAG_ARG_PREFIX + Date.class.getName();
    public static final String FRAG_ARG_MINUTES = FRAG_ARG_PREFIX + "minutes";
    public static final String FRAG_ARG_MAX_MINUTES = FRAG_ARG_PREFIX + "max.minutes";
    public static final String FRAG_ARG_TITLE = FRAG_ARG_PREFIX + "title";

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
     * Creates the DatePickerFragment.
     *
     * @param date The Date to display on the Date Picker
     * @return DateTimePickerFragment
     */
    public static DateTimePickerFragment createDatePickerFragment(Date date, String dateType) {
        DateTimePickerFragment ret = new DateTimePickerFragment();
        Bundle args = new Bundle();
        args.putSerializable(FRAG_ARG_DATE, date);
        args.putSerializable(FRAG_ARG_DATE_TYPE, dateType);
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

    public static ProjectDetailFragment createProjectDetailFragment(Project project) {
        ProjectDetailFragment ret = new ProjectDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(FRAG_ARG_PROJECT, project);
        ret.setArguments(args);
        return ret;
    }

    public static NumberPickerFragment createNumberPickerFragment(Integer numberOfMinutes, Integer maxMinutes, String title) {
        NumberPickerFragment ret = new NumberPickerFragment();
        Bundle args = new Bundle();
        args.putSerializable(FRAG_ARG_MINUTES, numberOfMinutes);
        args.putSerializable(FRAG_ARG_MAX_MINUTES, maxMinutes);
        args.putSerializable(FRAG_ARG_TITLE, title);
        ret.setArguments(args);
        return ret;
    }

}
