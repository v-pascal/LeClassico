package com.studio.artaban.leclassico.activities.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studio.artaban.leclassico.connection.DataService;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 17/11/16.
 * Preference activity with compatibility delegate (to display action bar)
 * + Logout management (see 'LoggedActivity' class)
 */
public class BasePreferenceActivity extends PreferenceActivity {

    private final LogoutReceiver mLogoutReceiver = new LogoutReceiver();
    private class LogoutReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Logs.add(Logs.Type.V, "context: " + context + ";intent: " + intent);
            if (intent.getAction().equals(DataService.REQUEST_LOGOUT)) ////// Logout requested
                finish(); // Finish activity
        }
    }

    //
    private AppCompatDelegate mDelegate;
    private AppCompatDelegate getDelegate() {
        if (mDelegate == null)
            mDelegate = AppCompatDelegate.create(this, null);

        return mDelegate;
    }

    //////
    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    ////// PreferenceActivity //////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
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
    protected void onResume() {
        super.onResume();
        Logs.add(Logs.Type.V, null);
        registerReceiver(mLogoutReceiver, new IntentFilter(DataService.REQUEST_LOGOUT));
        if (!DataService.isRunning())
            finish();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Logs.add(Logs.Type.V, null);
        unregisterReceiver(mLogoutReceiver);
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
