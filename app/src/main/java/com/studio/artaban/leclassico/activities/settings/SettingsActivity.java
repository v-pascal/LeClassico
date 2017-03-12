package com.studio.artaban.leclassico.activities.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Preferences;
import com.studio.artaban.leclassico.helpers.Logs;

import java.util.List;

/**
 * Created by pascal on 16/11/16.
 * User settings activity
 */
public class SettingsActivity extends BasePreferenceActivity {

    public static class PrefsNotifyFragment extends PreferenceFragment { ///////////////////////////

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
            addPreferencesFromResource(R.xml.settings_fragment_notify);
        }
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
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        Logs.add(Logs.Type.V, "target: " + target);
        loadHeadersFromResource(R.xml.settings_headers, target);
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
}
