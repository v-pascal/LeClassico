package com.studio.artaban.leclassico.activities.settings;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.telephony.TelephonyManager;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.codes.Preferences;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.tools.Tools;

/**
 * Created by pascal on 20/03/17.
 * Location preferences:
 * _ Enable/Disable location share
 */
public class PrefsLocationFragment extends BasePreferenceFragment {

    private void displayData() { // Display location preference
        Logs.add(Logs.Type.V, null);

        SwitchPreference preference = (SwitchPreference)findPreference(Preferences.SETTINGS_LOCATION);
        String value = Preferences.getString(Preferences.SETTINGS_LOCATION_DEVICE_ID);
        if (value == null) {
            preference.setChecked(false);
            preference.setSummary(getString(R.string.disabled));

        } else {
            String device, deviceId = Preferences.getString(Preferences.SETTINGS_LOCATION_DEVICE_ID);
            String currentId = ((TelephonyManager)getActivity()
                    .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

            if ((currentId == null) || (deviceId == null))
                device = getString(R.string.enabled);
            else
                device = getString((currentId.equals(deviceId))?
                        R.string.current_device : R.string.other_device, currentId);
            preference.setChecked(true);
            preference.setSummary(device);
        }
        preference.setOnPreferenceChangeListener(this);
    }

    ////// OnPreferenceChangeListener //////////////////////////////////////////////////////////////
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Logs.add(Logs.Type.V, "preference: " + preference + ";newValue: " + newValue);
        String deviceId = Preferences.getString(Preferences.SETTINGS_LOCATION_DEVICE_ID);
        String currentId = ((TelephonyManager)getActivity()
                .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

        Boolean locate = (Boolean)newValue;
        if (!locate) {
            if ((deviceId == null) || (currentId == null) || (deviceId.equals(currentId))) {
                preference.setSummary(getString(R.string.disabled));
                //updateData(preference.getKey(), newValue);
                return true;
            }
            // Confirm disabling location share of another device






        } else {





        }
        return false;
    }

    ////// BasePreferenceFragment ///////////////////////////////////////////////////////////////////////
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Logs.add(Logs.Type.V, "selfChange: " + selfChange + ";uri: " + uri);

        final ContentResolver resolver = getActivity().getContentResolver();
        Tools.startProcess(getActivity(), new Tools.OnProcessListener() {
            @Override
            public Bundle onBackgroundTask() {
                Logs.add(Logs.Type.V, null);

                Cursor cursor = resolver.query(Uri.parse(DataProvider.CONTENT_URI +
                        CamaradesTable.TABLE_NAME), new String[]{
                                CamaradesTable.COLUMN_DEVICE_ID,
                                CamaradesTable.COLUMN_DEVICE
                        },
                        DataTable.DataField.COLUMN_ID + '=' + Preferences.getInt(Preferences.SETTINGS_LOGIN_PSEUDO_ID),
                        null, null);
                cursor.moveToFirst();
                Preferences.setString(Preferences.SETTINGS_LOCATION_DEVICE_ID,
                        (!cursor.isNull(0)) ? cursor.getString(0) : null);
                Preferences.setString(Preferences.SETTINGS_LOCATION_DEVICE,
                        (!cursor.isNull(1)) ? cursor.getString(1) : null);
                cursor.close();
                return null;
            }

            @Override
            public void onMainNextTask(Bundle backResult) {
                Logs.add(Logs.Type.V, "backResult: " + backResult);
                displayData();
            }
        });
    }

    ////// PreferenceFragment //////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
        addPreferencesFromResource(R.xml.settings_fragment_location);

        // Set URI to check DB changes
        mUri = Uris.getUri(Uris.ID_USER_MEMBERS,
                String.valueOf(Preferences.getInt(Preferences.SETTINGS_LOGIN_PSEUDO_ID)));

        // Initialize location preference
        displayData();
    }
}
