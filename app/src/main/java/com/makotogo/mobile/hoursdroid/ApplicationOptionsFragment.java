package com.makotogo.mobile.hoursdroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
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
        return ret;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (isBooleanTypeKey(key)) {
            String summary = toNiceBooleanSummary(sharedPreferences.getBoolean(key, false));
            preference.setSummary(summary);
        }
    }

    private String toNiceBooleanSummary(Boolean value) {
        return (value == true)
                ? "Yes"
                : "No"
                ;
    }

    protected boolean isStringTypeKey(String key) {
        return
                false // There are no String keys at the moment
                ;
    }

    protected boolean isIntegerTypeKey(String key) {
        return
                false // There are no Integer keys at the moment
                ;
    }

    protected boolean isBooleanTypeKey(String key) {
        return
                key.equals(ApplicationOptions.PREFS_KEY_SHOW_INACTIVE_JOBS) ||
                        key.equals(ApplicationOptions.PREFS_KEY_SHOW_NOTIFICATIONS)
                ;
    }

    protected boolean isRingtoneTypeKey(String key) {
        return
                false // There are no ringtone keys at the moment
                ;
    }

}
