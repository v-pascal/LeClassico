package com.studio.artaban.leclassico.activities.settings;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
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

    public interface OnSetResult {
        void onSet(boolean newShare);
    }
    public static void set(Activity activity, final boolean share, @NonNull final OnSetResult result,
                           @Nullable final Uri uri, @Nullable final ContentObserver observer) {

        Logs.add(Logs.Type.V, "activity: " + activity + ";share: " + share + ";result: " + result +
                ";uri: " + uri + ";observer: " + observer);
        final String currentId = Tools.getDeviceId(activity);
        final ContentResolver resolver = activity.getContentResolver();
        if (!share) {

            String deviceId = Preferences.getString(Preferences.SETTINGS_LOCATION_DEVICE_ID);
            if ((deviceId == null) || (currentId == null) || (deviceId.equals(currentId))) {
                Preferences.setString(Preferences.SETTINGS_LOCATION_DEVICE_ID, null);
                Preferences.setString(Preferences.SETTINGS_LOCATION_DEVICE, null);

                updateData(resolver, share, currentId, uri, observer);
                result.onSet(share);

            } else // Confirm disabling location share defined on another device
                new AlertDialog.Builder(activity)
                        .setIcon(R.drawable.question_red)
                        .setTitle(R.string.confirm)
                        .setMessage(activity.getString(R.string.confirm_disable_location,
                                Preferences.getString(Preferences.SETTINGS_LOCATION_DEVICE)))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Logs.add(Logs.Type.V, "dialog: " + dialog + ";which: " + which);
                                Preferences.setString(Preferences.SETTINGS_LOCATION_DEVICE_ID, null);
                                Preferences.setString(Preferences.SETTINGS_LOCATION_DEVICE, null);

                                updateData(resolver, share, currentId, uri, observer);
                                result.onSet(share);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Logs.add(Logs.Type.V, "dialog: " + dialog + ";which: " + which);
                                result.onSet(!share); // No change
                            }
                        })
                        .create()
                        .show();

        } else {
            String device = Tools.getDeviceName();
            Preferences.setString(Preferences.SETTINGS_LOCATION_DEVICE_ID, currentId);
            Preferences.setString(Preferences.SETTINGS_LOCATION_DEVICE, device);

            updateData(resolver, share, currentId, uri, observer);
            result.onSet(share);
        }
    }
    private static void updateData(final ContentResolver resolver, final boolean share, final String deviceId,
                                   @Nullable final Uri uri, @Nullable final ContentObserver observer) {

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";share: " + share + ";deviceId: " + deviceId);
        new Thread(new Runnable() { // Background process
            @Override
            public void run() {
                Logs.add(Logs.Type.V, null);

                Uri memberUri = Uri.parse(DataProvider.CONTENT_URI + CamaradesTable.TABLE_NAME);
                String where = DataTable.DataField.COLUMN_ID + '=' +
                        Preferences.getInt(Preferences.SETTINGS_LOGIN_PSEUDO_ID);

                synchronized (Database.getTable(CamaradesTable.TABLE_NAME)) {
                    ContentValues values = new ContentValues();

                    Date now = new Date();
                    DateFormat dateFormat = new SimpleDateFormat(Constants.FORMAT_DATE_TIME);
                    String currentDate = dateFormat.format(now);
                    values.put(CamaradesTable.COLUMN_DEVICE_UPD, currentDate);
                    values.put(CamaradesTable.COLUMN_DEVICE_ID_UPD, currentDate);

                    Cursor status = resolver.query(memberUri,
                            new String[]{Constants.DATA_COLUMN_STATUS_DATE}, where, null, null);
                    status.moveToFirst();
                    values.put(Constants.DATA_COLUMN_STATUS_DATE, status.getString(0));
                    status.close();
                    // NB: Needed to keep current status date entry (allow to find fields to update)

                    if (share) {
                        try {
                            values.put(CamaradesTable.COLUMN_DEVICE, Tools.getDeviceName());
                            values.put(CamaradesTable.COLUMN_DEVICE_ID, deviceId);

                        } catch (Exception e) {
                            Logs.add(Logs.Type.E, "Unable to get device Name & ID");
                            values.put(CamaradesTable.COLUMN_DEVICE, "UNKNOWN");
                            values.put(CamaradesTable.COLUMN_DEVICE_ID, "NO-DEVICE-ID");
                        }

                    } else {
                        values.putNull(CamaradesTable.COLUMN_DEVICE);
                        values.putNull(CamaradesTable.COLUMN_DEVICE_ID);
                    }
                    resolver.update(memberUri, values, where, null);

                    if (uri != null) // Notify changes (if requested)
                        resolver.notifyChange(uri, observer);
                }
            }
        }).start();
    }

    ////// OnPreferenceChangeListener //////////////////////////////////////////////////////////////
    @Override
    public boolean onPreferenceChange(final Preference preference, Object newValue) {
        Logs.add(Logs.Type.V, "preference: " + preference + ";newValue: " + newValue);

        final Boolean share = (Boolean)newValue;
        final ContentResolver resolver = getActivity().getContentResolver();
        set(getActivity(), share, new OnSetResult() {

            @Override
            public void onSet(boolean newShare) {
                Logs.add(Logs.Type.V, "newShare: " + newShare);
                if (share != newShare)
                    return; // No change

                if (!newShare)
                    preference.setSummary(getString(R.string.disabled));
                else {
                    String device = Tools.getDeviceName();
                    preference.setSummary(getString(R.string.current_device, device));
                }
                ((SwitchPreference)preference).setChecked(newShare);
                preference.getEditor().apply();

                // Update location service
                getActivity().sendBroadcast(DataService.getIntent(newShare));
            }

        }, mUri, mObserver);
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
        return null;
    }
    @Override
    protected void onUpdateData(String key, Object newValue) {

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
