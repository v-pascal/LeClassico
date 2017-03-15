package com.studio.artaban.leclassico.activities.settings;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.connection.requests.CamaradesRequest;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataObserver;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.codes.Preferences;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.helpers.Database;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.services.DataService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
public class PrefsUserFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener, DataObserver.OnContentListener {

    public static void getData(Cursor user) { // Store connected user info into preferences (from DB)
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
        Preferences.setString(Preferences.SETTINGS_USER_EMAIL, (!user.isNull(CamaradesTable.COLUMN_INDEX_EMAIL)) ?
                user.getString(CamaradesTable.COLUMN_INDEX_EMAIL) : null);
        Preferences.setString(Preferences.SETTINGS_USER_HOBBIES, (!user.isNull(CamaradesTable.COLUMN_INDEX_HOBBIES))?
                user.getString(CamaradesTable.COLUMN_INDEX_HOBBIES):null);
        Preferences.setString(Preferences.SETTINGS_USER_ABOUT, (!user.isNull(CamaradesTable.COLUMN_INDEX_A_PROPOS)) ?
                user.getString(CamaradesTable.COLUMN_INDEX_A_PROPOS) : null);
    }

    //////
    private Uri mUserUri; // User member URI to observe DB
    private DataObserver mUserObserver; // User data update observer

    private void updateUserInfo(final String key, final String value) { // Update DB user info
        Logs.add(Logs.Type.V, "key: " + key + ";value: " + value);

        new Thread(new Runnable() { // Background process
            @Override
            public void run() {
                Logs.add(Logs.Type.V, null);

                String field = (key.equals(Preferences.SETTINGS_USER_PASSWORD))? CamaradesTable.COLUMN_CODE_CONF:
                        ((key.equals(Preferences.SETTINGS_USER_NAME))? CamaradesTable.COLUMN_NOM:
                        ((key.equals(Preferences.SETTINGS_USER_SURNAME))? CamaradesTable.COLUMN_PRENOM:
                        ((key.equals(Preferences.SETTINGS_USER_GENDER))? CamaradesTable.COLUMN_SEXE:
                        ((key.equals(Preferences.SETTINGS_USER_BIRTHDAY))? CamaradesTable.COLUMN_BORN_DATE:
                        ((key.equals(Preferences.SETTINGS_USER_ADDRESS))? CamaradesTable.COLUMN_ADRESSE:
                        ((key.equals(Preferences.SETTINGS_USER_TOWN))? CamaradesTable.COLUMN_VILLE:
                        ((key.equals(Preferences.SETTINGS_USER_POSTAL_CODE))? CamaradesTable.COLUMN_POSTAL:
                        ((key.equals(Preferences.SETTINGS_USER_PHONE))? CamaradesTable.COLUMN_PHONE:
                        ((key.equals(Preferences.SETTINGS_USER_EMAIL))? CamaradesTable.COLUMN_EMAIL:
                        ((key.equals(Preferences.SETTINGS_USER_HOBBIES))? CamaradesTable.COLUMN_HOBBIES:
                        CamaradesTable.COLUMN_A_PROPOS))))))))));
                String updateField = (key.equals(Preferences.SETTINGS_USER_PASSWORD))? CamaradesTable.COLUMN_CODE_CONF_UPD:
                        ((key.equals(Preferences.SETTINGS_USER_NAME))? CamaradesTable.COLUMN_NOM_UPD:
                        ((key.equals(Preferences.SETTINGS_USER_SURNAME))? CamaradesTable.COLUMN_PRENOM_UPD:
                        ((key.equals(Preferences.SETTINGS_USER_GENDER))? CamaradesTable.COLUMN_SEXE_UPD:
                        ((key.equals(Preferences.SETTINGS_USER_BIRTHDAY))? CamaradesTable.COLUMN_BORN_DATE_UPD:
                        ((key.equals(Preferences.SETTINGS_USER_ADDRESS))? CamaradesTable.COLUMN_ADRESSE_UPD:
                        ((key.equals(Preferences.SETTINGS_USER_TOWN))? CamaradesTable.COLUMN_VILLE_UPD:
                        ((key.equals(Preferences.SETTINGS_USER_POSTAL_CODE))? CamaradesTable.COLUMN_POSTAL_UPD:
                        ((key.equals(Preferences.SETTINGS_USER_PHONE))? CamaradesTable.COLUMN_PHONE_UPD:
                        ((key.equals(Preferences.SETTINGS_USER_EMAIL))? CamaradesTable.COLUMN_EMAIL_UPD:
                        ((key.equals(Preferences.SETTINGS_USER_HOBBIES))? CamaradesTable.COLUMN_HOBBIES_UPD:
                        CamaradesTable.COLUMN_A_PROPOS_UPD))))))))));
                Uri uri = Uri.parse(DataProvider.CONTENT_URI + CamaradesTable.TABLE_NAME);
                String where = DataTable.DataField.COLUMN_ID + '=' +
                        Preferences.getInt(Preferences.SETTINGS_LOGIN_PSEUDO_ID);

                synchronized (Database.getTable(CamaradesTable.TABLE_NAME)) {
                    ContentValues values = new ContentValues();

                    if (field.equals(CamaradesTable.COLUMN_SEXE))
                        values.put(field, Integer.valueOf(value));
                    else
                        values.put(field, value);
                    Date now = new Date();
                    DateFormat dateFormat = new SimpleDateFormat(Constants.FORMAT_DATE_TIME);
                    values.put(updateField, dateFormat.format(now));

                    Cursor status = getActivity().getContentResolver().query(uri,
                            new String[]{Constants.DATA_COLUMN_STATUS_DATE}, where, null, null);
                    status.moveToFirst();
                    values.put(Constants.DATA_COLUMN_STATUS_DATE, status.getString(0));
                    // NB: Needed to keep current status date entry (allow to find fields to update)

                    getActivity().getContentResolver().update(uri, values, where, null);
                    getActivity().getContentResolver().notifyChange(mUserUri, mUserObserver); // Notify changes
                }
            }
        }).start();
    }
    private void displayData() { // Display preferences data into summaries
        Logs.add(Logs.Type.V, null);

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

    ////// OnContentListener ///////////////////////////////////////////////////////////////////////
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Logs.add(Logs.Type.V, "selfChange: " + selfChange + ";uri: " + uri);

        Cursor cursor = getActivity().getContentResolver().query(Uri.parse(DataProvider.CONTENT_URI +
                CamaradesTable.TABLE_NAME), null, DataTable.DataField.COLUMN_ID + '=' +
                Preferences.getInt(Preferences.SETTINGS_LOGIN_PSEUDO_ID), null, null);
        getData(cursor);
        displayData();
    }

    ////// OnPreferenceChangeListener //////////////////////////////////////////////////////////////
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Logs.add(Logs.Type.V, "preference: " + preference + ";newValue: " + newValue);

        String value = ((String)newValue).trim();
        if (preference.getKey().equals(Preferences.SETTINGS_USER_GENDER))
            preference.setSummary(getResources()
                    .getStringArray(R.array.pref_gender)[Integer.valueOf(value) - 1]);
        else {
            if (!value.isEmpty())
                preference.setSummary(value);
            else {
                preference.getEditor().remove(preference.getKey()).apply();
                preference.setSummary(getString(R.string.undefined));
            }
        }
        updateUserInfo(preference.getKey(), value);
        return true;
    }

    ////// PreferenceFragment //////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
        addPreferencesFromResource(R.xml.settings_fragment_user);

        // Set URI & observer (to check DB changes)
        mUserUri = Uris.getUri(Uris.ID_USER_MEMBERS,
                String.valueOf(Preferences.getInt(Preferences.SETTINGS_LOGIN_PSEUDO_ID)));

        mUserObserver = new DataObserver("prefsUserDataObserverThread", this);
        mUserObserver.register(getActivity().getContentResolver(), mUserUri);

        // Initialize preferences
        displayData();
    }

    @Override
    public void onResume() {
        super.onResume();
        Logs.add(Logs.Type.V, null);

        // Register data service
        Intent intent = DataService.getIntent(true, Tables.ID_CAMARADES, mUserUri);
        intent.putExtra(CamaradesRequest.EXTRA_DATA_PSEUDO,
                Preferences.getString(Preferences.SETTINGS_LOGIN_PSEUDO));
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        Logs.add(Logs.Type.V, null);

        // Unregister data service
        getActivity().sendBroadcast(DataService.getIntent(false, Tables.ID_CAMARADES, mUserUri));
    }
}
