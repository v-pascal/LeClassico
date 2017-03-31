package com.studio.artaban.leclassico.activities.settings;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.connection.requests.CamaradesRequest;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.codes.Preferences;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.helpers.Database;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.services.DataService;
import com.studio.artaban.leclassico.tools.Tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pascal on 20/03/17.
 * Location preferences:
 * _ Enable/Disable location share
 */
public class PrefsLocationFragment extends BasePreferenceFragment {

    private void updateData(String deviceId, String device) {
        Logs.add(Logs.Type.V, "deviceId: " + deviceId + ";device: " + device);

        Preferences.setString(Preferences.SETTINGS_LOCATION_DEVICE_ID, deviceId);
        Preferences.setString(Preferences.SETTINGS_LOCATION_DEVICE, device);
        updateData(null, (deviceId == null)? Boolean.FALSE : Boolean.TRUE);
    }

    ////// OnPreferenceChangeListener //////////////////////////////////////////////////////////////
    @Override
    public boolean onPreferenceChange(final Preference preference, Object newValue) {
        Logs.add(Logs.Type.V, "preference: " + preference + ";newValue: " + newValue);

        String deviceId = Preferences.getString(Preferences.SETTINGS_LOCATION_DEVICE_ID);
        String currentId = Tools.getDeviceId(getActivity());
        Boolean locate = (Boolean)newValue;
        if (!locate) {
            if ((deviceId == null) || (currentId == null) || (deviceId.equals(currentId))) {
                preference.setSummary(getString(R.string.disabled));
                updateData(null, null);
                return true;
            }
            // Confirm disabling location share defined on another device
            new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.question_red)
                    .setTitle(R.string.confirm)
                    .setMessage(getString(R.string.confirm_disable_location,
                            Preferences.getString(Preferences.SETTINGS_LOCATION_DEVICE)))
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Logs.add(Logs.Type.V, "dialog: " + dialog + ";which: " + which);

                            preference.setSummary(getString(R.string.disabled));
                            ((SwitchPreference)preference).setChecked(false);
                            preference.getEditor().apply();
                            updateData(null, null);
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .create()
                    .show();

        } else {
            String device = Tools.getDeviceName();
            preference.setSummary(getString(R.string.current_device, device));
            updateData(currentId, device);
            return true;
        }
        return false;
    }

    ////// BasePreferenceFragment //////////////////////////////////////////////////////////////////
    @Override
    protected void displayData(@Nullable Bundle result) {
        Logs.add(Logs.Type.V, "result: " + result);

        SwitchPreference preference = (SwitchPreference)findPreference(Preferences.SETTINGS_LOCATION);
        String value = Preferences.getString(Preferences.SETTINGS_LOCATION_DEVICE_ID);
        if (value == null) {
            preference.setChecked(false);
            preference.setSummary(getString(R.string.disabled));

        } else {
            String summary, deviceId = Preferences.getString(Preferences.SETTINGS_LOCATION_DEVICE_ID);
            String currentId = Tools.getDeviceId(getActivity());

            if ((currentId == null) || (deviceId == null))
                summary = getString(R.string.enabled);
            else
                summary = getString((currentId.equals(deviceId))?
                        R.string.current_device : R.string.other_device,
                        Preferences.getString(Preferences.SETTINGS_LOCATION_DEVICE));

            preference.setChecked(true);
            preference.setSummary(summary);
        }
        preference.setOnPreferenceChangeListener(this);
    }

    @Override
    protected Bundle onDataChanged(ContentResolver resolver) {
        Logs.add(Logs.Type.V, "resolver: " + resolver);

        Cursor cursor = resolver.query(Uri.parse(DataProvider.CONTENT_URI + CamaradesTable.TABLE_NAME),
                new String[]{
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
    protected void onUpdateData(String key, Object newValue) {
        Logs.add(Logs.Type.V, "key: " + key + ";newValue: " + newValue);

        Uri uri = Uri.parse(DataProvider.CONTENT_URI + CamaradesTable.TABLE_NAME);
        String where = DataTable.DataField.COLUMN_ID + '=' +
                Preferences.getInt(Preferences.SETTINGS_LOGIN_PSEUDO_ID);

        synchronized (Database.getTable(CamaradesTable.TABLE_NAME)) {
            ContentValues values = new ContentValues();

            Date now = new Date();
            DateFormat dateFormat = new SimpleDateFormat(Constants.FORMAT_DATE_TIME);
            String currentDate = dateFormat.format(now);
            values.put(CamaradesTable.COLUMN_DEVICE_UPD, currentDate);
            values.put(CamaradesTable.COLUMN_DEVICE_ID_UPD, currentDate);

            Cursor status = getActivity().getContentResolver().query(uri,
                    new String[]{Constants.DATA_COLUMN_STATUS_DATE}, where, null, null);
            status.moveToFirst();
            values.put(Constants.DATA_COLUMN_STATUS_DATE, status.getString(0));
            status.close();
            // NB: Needed to keep current status date entry (allow to find fields to update)

            if ((Boolean)newValue) {
                try {
                    values.put(CamaradesTable.COLUMN_DEVICE, Tools.getDeviceName());
                    values.put(CamaradesTable.COLUMN_DEVICE_ID, Tools.getDeviceId(getActivity()));

                } catch (Exception e) {
                    Logs.add(Logs.Type.E, "Unable to get device Name & ID");
                    values.put(CamaradesTable.COLUMN_DEVICE, "UNKNOWN");
                    values.put(CamaradesTable.COLUMN_DEVICE_ID, "NO-DEVICE-ID");
                }

            } else {
                values.putNull(CamaradesTable.COLUMN_DEVICE);
                values.putNull(CamaradesTable.COLUMN_DEVICE_ID);

                values.put(CamaradesTable.COLUMN_LATITUDE_UPD, currentDate);
                values.put(CamaradesTable.COLUMN_LONGITUDE_UPD, currentDate);
                values.putNull(CamaradesTable.COLUMN_LATITUDE);
                values.putNull(CamaradesTable.COLUMN_LONGITUDE);
            }
            getActivity().getContentResolver().update(uri, values, where, null);
            getActivity().getContentResolver().notifyChange(mUri, mObserver); // Notify changes
        }
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
        displayData(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        Logs.add(Logs.Type.V, null);

        // Register data service
        Intent intent = DataService.getIntent(true, Tables.ID_CAMARADES, mUri);
        intent.putExtra(CamaradesRequest.EXTRA_DATA_PSEUDO,
                Preferences.getString(Preferences.SETTINGS_LOGIN_PSEUDO));
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        Logs.add(Logs.Type.V, null);

        // Unregister data service
        getActivity().sendBroadcast(DataService.getIntent(false, Tables.ID_CAMARADES, mUri));
    }
}
