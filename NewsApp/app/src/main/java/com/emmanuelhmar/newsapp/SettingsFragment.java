package com.emmanuelhmar.newsapp;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements androidx.preference.Preference.OnPreferenceChangeListener {


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences,rootKey);

        androidx.preference.Preference orderBy = findPreference("order_by");
        bindPreferenceSummaryToValue(orderBy);

    }

    private void bindPreferenceSummaryToValue(androidx.preference.Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());

        String preferenceString = sharedPreferences.getString(preference.getKey(), "");
        onPreferenceChange(preference, preferenceString);

    }

    @Override
    public boolean onPreferenceChange(androidx.preference.Preference preference, Object newValue) {
        String stringValue = newValue.toString();

        if (preference instanceof androidx.preference.ListPreference) {
            ListPreference listPreference = (ListPreference) preference;

//            Get the index of the value chosen
            int prefIndex = listPreference.findIndexOfValue(stringValue);

            if (prefIndex >= 0) {
                CharSequence[] labels = listPreference.getEntries();
                preference.setSummary(labels[prefIndex]);
            } else {
                preference.setSummary(stringValue);
            }
        }

        return true;
    }
}
