package com.studio.artaban.leclassico.activities.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.connection.requests.CamaradesRequest;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataObserver;
import com.studio.artaban.leclassico.data.codes.Preferences;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.services.DataService;

import java.util.List;

/**
 * Created by pascal on 16/11/16.
 * User settings activity
 */
public class SettingsActivity extends BasePreferenceActivity implements DataObserver.OnContentListener {

    public static class PrefsNotifyFragment extends PreferenceFragment { ///////////////////////////

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
            addPreferencesFromResource(R.xml.settings_fragment_notify);
        }
    }
    protected Uri mUserUri; // URI to observe user DB info (e.i location share flag)
    protected DataObserver mObserver; // DB update observer (observe remote DB updates)

    ////// OnContentListener ///////////////////////////////////////////////////////////////////////
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Logs.add(Logs.Type.V, "selfChange: " + selfChange + ";uri: " + uri);
        invalidateHeaders(); // Refresh headers
    }

    ////// PreferenceActivity //////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);

        if (getIntent().hasExtra(Login.EXTRA_DATA_PSEUDO))
            Preferences.setString(Preferences.SETTINGS_LOGIN_PSEUDO,
                    getIntent().getStringExtra(Login.EXTRA_DATA_PSEUDO));
        if (getIntent().hasExtra(Login.EXTRA_DATA_PSEUDO_ID))
            Preferences.setInt(Preferences.SETTINGS_LOGIN_PSEUDO_ID,
                    getIntent().getIntExtra(Login.EXTRA_DATA_PSEUDO_ID, Constants.NO_DATA));
        // NB: Needed coz intent is replaced by new one at preference header selection (without extras)

        // Enable to display back arrow (toolbar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set observer & URI to check DB changes (location share flag)
        mObserver = new DataObserver(new Handler(Looper.getMainLooper()), this);
        mUserUri = Uris.getUri(Uris.ID_USER_MEMBERS,
                String.valueOf(Preferences.getInt(Preferences.SETTINGS_LOGIN_PSEUDO_ID)));
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        Logs.add(Logs.Type.V, "target: " + target);
        loadHeadersFromResource(R.xml.settings_headers, target);

        // Set location flag (summary & icon)
        if (Preferences.getString(Preferences.SETTINGS_LOCATION_DEVICE_ID) != null) {
            target.get(2).summaryRes = R.string.enabled;
            target.get(2).iconRes = R.drawable.ic_location_on_black_24dp;
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Logs.add(Logs.Type.V, "featureId: " + featureId + ";item: " + item);
        if (item.getItemId() == android.R.id.home) {

            finish();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logs.add(Logs.Type.V, null);

        invalidateHeaders(); // Refresh headers
        // NB: Needed when back from location screen with changes

        // Register DB observer
        mObserver.register(getContentResolver(), mUserUri);

        // Register data service
        Intent intent = DataService.getIntent(true, Tables.ID_CAMARADES, mUserUri);
        intent.putExtra(CamaradesRequest.EXTRA_DATA_PSEUDO,
                Preferences.getString(Preferences.SETTINGS_LOGIN_PSEUDO));
        sendBroadcast(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logs.add(Logs.Type.V, null);

        // Unregister data service
        sendBroadcast(DataService.getIntent(false, Tables.ID_CAMARADES, mUserUri));

        // Unregister DB observer
        mObserver.unregister(getContentResolver());
    }
}
