package com.studio.artaban.leclassico.activities.settings;

import android.database.Cursor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.codes.Preferences;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
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

    public static void getData(Cursor user) { // Fill connected user info in preferences data
        Logs.add(Logs.Type.V, "user: " + user);

        Preferences.setString(Preferences.SETTINGS_USER_PASSWORD, user.getString(CamaradesTable.COLUMN_INDEX_CODE_CONF));
        Preferences.setString(Preferences.SETTINGS_USER_NAME, (!user.isNull(CamaradesTable.COLUMN_INDEX_NOM)) ?
                user.getString(CamaradesTable.COLUMN_INDEX_NOM):null);
        Preferences.setString(Preferences.SETTINGS_USER_SURNAME, (!user.isNull(CamaradesTable.COLUMN_INDEX_PRENOM))?
                user.getString(CamaradesTable.COLUMN_INDEX_PRENOM):null);
        Preferences.setString(Preferences.SETTINGS_USER_GENDER, (!user.isNull(CamaradesTable.COLUMN_INDEX_SEXE))?
                String.valueOf(user.getInt(CamaradesTable.COLUMN_INDEX_SEXE)):null);
        Preferences.setString(Preferences.SETTINGS_USER_BIRTHDAY, (!user.isNull(CamaradesTable.COLUMN_INDEX_BORN_DATE))?
                user.getString(CamaradesTable.COLUMN_INDEX_BORN_DATE):null);
        Preferences.setString(Preferences.SETTINGS_USER_ADDRESS, (!user.isNull(CamaradesTable.COLUMN_INDEX_ADRESSE))?
                user.getString(CamaradesTable.COLUMN_INDEX_ADRESSE):null);
        Preferences.setString(Preferences.SETTINGS_USER_TOWN, (!user.isNull(CamaradesTable.COLUMN_INDEX_VILLE))?
                user.getString(CamaradesTable.COLUMN_INDEX_VILLE):null);
        Preferences.setString(Preferences.SETTINGS_USER_POSTAL_CODE, (!user.isNull(CamaradesTable.COLUMN_INDEX_POSTAL))?
                user.getString(CamaradesTable.COLUMN_INDEX_POSTAL):null);
        Preferences.setString(Preferences.SETTINGS_USER_PHONE, (!user.isNull(CamaradesTable.COLUMN_INDEX_PHONE))?
                user.getString(CamaradesTable.COLUMN_INDEX_PHONE):null);
        Preferences.setString(Preferences.SETTINGS_USER_EMAIL, (!user.isNull(CamaradesTable.COLUMN_INDEX_EMAIL))?
                user.getString(CamaradesTable.COLUMN_INDEX_EMAIL):null);
        Preferences.setString(Preferences.SETTINGS_USER_HOBBIES, (!user.isNull(CamaradesTable.COLUMN_INDEX_HOBBIES))?
                user.getString(CamaradesTable.COLUMN_INDEX_HOBBIES):null);
        Preferences.setString(Preferences.SETTINGS_USER_ABOUT, (!user.isNull(CamaradesTable.COLUMN_INDEX_A_PROPOS))?
                user.getString(CamaradesTable.COLUMN_INDEX_A_PROPOS):null);
    }

    ////// OnPreferenceChangeListener //////////////////////////////////////////////////////////////
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Logs.add(Logs.Type.V, "preference: " + preference + ";newValue: " + newValue);

        String value = ((String)newValue).trim();
        if (preference.getKey().equals(Preferences.SETTINGS_USER_GENDER)) {
            preference.setSummary(getResources()
                    .getStringArray(R.array.pref_gender)[Integer.valueOf(value) - 1]);





        } else {
            if (!value.isEmpty()) {
                preference.setSummary(value);




            } else {
                preference.getEditor().remove(preference.getKey()).apply();
                preference.setSummary(getString(R.string.undefined));




            }
        }
        return false;
    }

    ////// PreferenceFragment //////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
        addPreferencesFromResource(R.xml.settings_fragment_user);

        Preference preference = findPreference(Preferences.SETTINGS_USER_NAME);
        String value = Preferences.getString(Preferences.SETTINGS_USER_NAME);
        if (value != null)
            preference.setSummary(value);
        preference.setOnPreferenceChangeListener(this);

        preference = findPreference(Preferences.SETTINGS_USER_SURNAME);
        value = Preferences.getString(Preferences.SETTINGS_USER_SURNAME);
        if (value != null)
            preference.setSummary(value);
        preference.setOnPreferenceChangeListener(this);

        preference = findPreference(Preferences.SETTINGS_USER_GENDER);
        value = Preferences.getString(Preferences.SETTINGS_USER_GENDER);
        if (value != null)
            preference.setSummary(getResources()
                    .getStringArray(R.array.pref_gender)[Integer.valueOf(value) - 1]);
        preference.setOnPreferenceChangeListener(this);

        preference = findPreference(Preferences.SETTINGS_USER_BIRTHDAY);
        value = Preferences.getString(Preferences.SETTINGS_USER_BIRTHDAY);
        if (value != null)
            preference.setSummary(value);
        preference.setOnPreferenceChangeListener(this);

        preference = findPreference(Preferences.SETTINGS_USER_ADDRESS);
        value = Preferences.getString(Preferences.SETTINGS_USER_ADDRESS);
        if (value != null)
            preference.setSummary(value);
        preference.setOnPreferenceChangeListener(this);

        preference = findPreference(Preferences.SETTINGS_USER_TOWN);
        value = Preferences.getString(Preferences.SETTINGS_USER_TOWN);
        if (value != null)
            preference.setSummary(value);
        preference.setOnPreferenceChangeListener(this);

        preference = findPreference(Preferences.SETTINGS_USER_POSTAL_CODE);
        value = Preferences.getString(Preferences.SETTINGS_USER_POSTAL_CODE);
        if (value != null)
            preference.setSummary(value);
        preference.setOnPreferenceChangeListener(this);

        preference = findPreference(Preferences.SETTINGS_USER_PHONE);
        value = Preferences.getString(Preferences.SETTINGS_USER_PHONE);
        if (value != null)
            preference.setSummary(value);
        preference.setOnPreferenceChangeListener(this);

        preference = findPreference(Preferences.SETTINGS_USER_EMAIL);
        value = Preferences.getString(Preferences.SETTINGS_USER_EMAIL);
        if (value != null)
            preference.setSummary(value);
        preference.setOnPreferenceChangeListener(this);

        preference = findPreference(Preferences.SETTINGS_USER_HOBBIES);
        value = Preferences.getString(Preferences.SETTINGS_USER_HOBBIES);
        if (value != null)
            preference.setSummary(value);
        preference.setOnPreferenceChangeListener(this);

        preference = findPreference(Preferences.SETTINGS_USER_ABOUT);
        value = Preferences.getString(Preferences.SETTINGS_USER_ABOUT);
        if (value != null)
            preference.setSummary(value);
        preference.setOnPreferenceChangeListener(this);
    }
}
