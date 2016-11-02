package com.studio.artaban.leclassico.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;

import com.studio.artaban.leclassico.connection.DataService;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 28/10/16.
 * Logged activity with logout management
 */
public abstract class LoggedActivity extends AppCompatActivity {

    private final LogoutReceiver mLogoutReceiver = new LogoutReceiver();
    private class LogoutReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Logs.add(Logs.Type.V, "context: " + context + ";intent: " + intent);
            if (intent.getAction().equals(DataService.REQUEST_LOGOUT)) ////// Logout requested
                finish(); // Finish activity
        }
    }

    ////// LoggedActivity //////////////////////////////////////////////////////////////////////////

    protected abstract void onLoggedResume();

    ////// AppCompatActivity ///////////////////////////////////////////////////////////////////////
    @Override
    protected void onPause() {
        super.onPause();
        Logs.add(Logs.Type.V, null);
        unregisterReceiver(mLogoutReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logs.add(Logs.Type.V, null);
        registerReceiver(mLogoutReceiver, new IntentFilter(DataService.REQUEST_LOGOUT));

        onLoggedResume(); // Call implemented resume (if any)

        if (!DataService.isRunning())
            finish();
    }
}
