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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makotogo.mobile.hoursdroid.util.ApplicationOptions;

/**
 * Created by sperry on 3/26/16.
 */
public class ApplicationOptionsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = ApplicationOptionsFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ret = super.onCreateView(inflater, container, savedInstanceState);
        // Show Inactive Jobs?
        CheckBoxPreference checkboxPreference = (CheckBoxPreference) findPreference(ApplicationOptions.PREFS_KEY_SHOW_INACTIVE_JOBS);
        checkboxPreference.setSummary(
                toNiceBooleanSummary(
                        ApplicationOptions.instance(getActivity()).showInactiveJobs()
                )
        );

        // Show Inactive Jobs?
        checkboxPreference = (CheckBoxPreference) findPreference(ApplicationOptions.PREFS_KEY_SHOW_NOTIFICATIONS);
        checkboxPreference.setSummary(
                toNiceBooleanSummary(
                        ApplicationOptions.instance(getActivity()).showNotifications()
                )
        );
        //
        // Show Billed Hours Records?
        checkboxPreference = (CheckBoxPreference) findPreference(ApplicationOptions.PREFS_KEY_SHOW_BILLED_HOURS_RECORDS);
        checkboxPreference.setSummary(
                toNiceBooleanSummary(
                        ApplicationOptions.instance(getActivity()).showBilledHoursRecords()
                )
        );
        // Rounding
        ListPreference listPreference = (ListPreference) findPreference(ApplicationOptions.PREFS_KEY_ROUNDING);
        listPreference.setSummary(toNiceIntegerSummary(
                ApplicationOptions.PREFS_KEY_ROUNDING,
                ApplicationOptions.instance(getActivity()).getRounding()));
        return ret;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        String summary = generateSummaryForKey(sharedPreferences, key);
        preference.setSummary(summary);
        ApplicationOptions.release();// Forces re-read
    }

    private String generateSummaryForKey(SharedPreferences sharedPreferences, String key) {
        String ret = "UNKNOWN";
        if (isBooleanTypeKey(key)) {
            ret = toNiceBooleanSummary(sharedPreferences, key);
        } else if (isStringTypeKey(key)) {
            ret = toNiceStringSummary(sharedPreferences, key);
        }
        return ret;
    }
    protected boolean isBooleanTypeKey(String key) {
        return
                key.equals(ApplicationOptions.PREFS_KEY_SHOW_INACTIVE_JOBS) ||
                        key.equals(ApplicationOptions.PREFS_KEY_SHOW_NOTIFICATIONS) ||
                        key.equals(ApplicationOptions.PREFS_KEY_SHOW_BILLED_HOURS_RECORDS)
                ;
    }

    protected boolean isStringTypeKey(String key) {
        return
                key.equals(ApplicationOptions.PREFS_KEY_ROUNDING)
                ;
    }

    /**
     * Takes the specified value and turns it into a nice form of a Boolean.
     * Eventually.
     *
     * @param sharedPreferences
     * @param key
     * @return - String - the transformed value
     */
    private String toNiceBooleanSummary(SharedPreferences sharedPreferences, String key) {
        String ret;
        Boolean value = sharedPreferences.getBoolean(key, false);
        ret = toNiceBooleanSummary(value);
        return ret;
    }

    /**
     * Takes the specified value and transforms it into a Nice Boolean.
     *
     * @param value
     * @return
     */
    private String toNiceBooleanSummary(Boolean value) {
        return (value == true)
                ? "Yes"
                : "No"
                ;
    }

    /**
     * Takes the specified value and transforms it according to the type of values
     * it may take on, which are based on the specified key.
     *
     * @param sharedPreferences
     * @param key               The Key (from ApplicationOptions)
     * @return String - the transformed/decorated value. Returns UNRECOGNIZED KEY
     * if the key is not recognized.
     */
    private String toNiceStringSummary(SharedPreferences sharedPreferences, String key) {
        String ret = "UNRECOGNIZED KEY";
        if (key.equals(ApplicationOptions.PREFS_KEY_ROUNDING)) {
            String value = sharedPreferences.getString(key, "?");
            ret = value + " minutes";
        }
        return ret;
    }

    private String toNiceIntegerSummary(String key, int value) {
        String ret = "UNKNOWN";
        if (key.equals(ApplicationOptions.PREFS_KEY_ROUNDING)) {
            ret = Integer.toString(value) + " minutes";
        }
        return ret;
    }

}
