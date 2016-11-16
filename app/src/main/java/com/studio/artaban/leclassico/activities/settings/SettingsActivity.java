package com.studio.artaban.leclassico.activities.settings;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.helpers.Logs;

import java.util.List;

/**
 * Created by pascal on 16/11/16.
 * User settings activity
 */
public class SettingsActivity extends PreferenceActivity {





    public static class PrefsNotifyFragment extends PreferenceFragment { ///////////////////////////

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);



            // Make sure default values are applied.  In a real app, you would
            // want this in a shared function that is used to retrieve the
            // SharedPreferences wherever they are needed.

            //PreferenceManager.setDefaultValues(getActivity(),
            //        R.xml.advanced_preferences, false);




            // Set title

            //((Toolbar) getToolbarParent(getActivity()).findViewById(R.id.toolbar))
            //        .setTitle(getResources().getString(R.string.title_activity_notify));
            //((SettingsActivity)getActivity()).toolbar.setTitle("Ollala");





            //
            addPreferencesFromResource(R.xml.settings_fragment_notify);
        }
    }







    //////
    private AppCompatDelegate mDelegate; // Application compatibility delegate (display action bar)
    private AppCompatDelegate getDelegate() {
        if (mDelegate == null)
            mDelegate = AppCompatDelegate.create(this, null);

        return mDelegate;
    }

    ////// PreferenceActivity //////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        // Enable to display back arrow (toolbar)
        getDelegate().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    //////
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }
    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }
    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }
    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }
    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }
    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }
}
