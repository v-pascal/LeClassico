package com.studio.artaban.leclassico.activities.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.codes.Preferences;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 08/03/17.
 * User preferences:
 * _ Password
 * _ Name
 * _ Surname
 * _ Gender
 * _ Birthday
 * _ Address
 * _ Town
 * _ Postal code
 * _ Email
 * _ Hobbies
 * _ About
 */
public class PrefsUserFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    ////// OnPreferenceChangeListener //////////////////////////////////////////////////////////////
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Logs.add(Logs.Type.V, "preference: " + preference + ";newValue: " + newValue);






        return false;
    }

    ////// PreferenceFragment //////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
        addPreferencesFromResource(R.xml.settings_fragment_user);

        findPreference(Preferences.SETTINGS_USER_NAME).setOnPreferenceChangeListener(this);
        findPreference(Preferences.SETTINGS_USER_SURNAME).setOnPreferenceChangeListener(this);
        findPreference(Preferences.SETTINGS_USER_GENDER).setOnPreferenceChangeListener(this);
        findPreference(Preferences.SETTINGS_USER_BIRTHDAY).setOnPreferenceChangeListener(this);
        findPreference(Preferences.SETTINGS_USER_ADDRESS).setOnPreferenceChangeListener(this);
        findPreference(Preferences.SETTINGS_USER_TOWN).setOnPreferenceChangeListener(this);
        findPreference(Preferences.SETTINGS_USER_POSTAL_CODE).setOnPreferenceChangeListener(this);
        findPreference(Preferences.SETTINGS_USER_EMAIL).setOnPreferenceChangeListener(this);
        findPreference(Preferences.SETTINGS_USER_HOBBIES).setOnPreferenceChangeListener(this);
        findPreference(Preferences.SETTINGS_USER_ABOUT).setOnPreferenceChangeListener(this);
    }
}
