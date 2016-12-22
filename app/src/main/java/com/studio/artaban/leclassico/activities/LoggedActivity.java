package com.studio.artaban.leclassico.activities;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.notification.NotifyActivity;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.connection.ServiceNotify;
import com.studio.artaban.leclassico.services.DataService;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 28/10/16.
 * Logged activity with logout management
 */
public abstract class LoggedActivity extends AppCompatActivity {

    public static String EXTRA_DATA_ID = "id";
    public static String EXTRA_DATA_URI = "uri";
    // Extra data key

    protected int mId; // ID (pseudo, publication, etc)

    //
    private final LoggedReceiver mLoggedReceiver = new LoggedReceiver();
    private class LoggedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Logs.add(Logs.Type.V, "context: " + context + ";intent: " + intent);
            if (intent.getAction().equals(DataService.REQUEST_LOGOUT)) ////// Logout requested
                finish(); // Finish activity
            else if (intent.getAction().equals(ServiceNotify.NOTIFICATION_DATA_CHANGE))
                invalidateOptionsMenu(); ////// Notification changed
        }
    }

    //////
    protected void startNotificationActivity() { // Start notification activity
        Logs.add(Logs.Type.V, null);

        if (ServiceNotify.Existing) {

            ////// Start notification activity
            Intent notifyIntent = new Intent(this, NotifyActivity.class);
            Login.copyExtraData(getIntent(), notifyIntent);
            startActivity(notifyIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        } else
            Toast.makeText(this, R.string.no_notification, Toast.LENGTH_SHORT).show();
    }
    protected boolean onNotifyItemSelected(MenuItem item) {
        Logs.add(Logs.Type.V, "item: " + item);

        if (item.getItemId() == R.id.mnu_notification) {
            startNotificationActivity();
            return true;
        }
        return false;
    }

    ////// LoggedActivity //////////////////////////////////////////////////////////////////////////

    protected abstract void onLoggedResume();

    ////// AppCompatActivity ///////////////////////////////////////////////////////////////////////
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Logs.add(Logs.Type.V, "menu: " + menu);

        // Add notification menu item (if not already done)
        MenuItem notifyItem = menu.findItem(R.id.mnu_notification);
        if (notifyItem == null) {
            notifyItem = menu.add(0, R.id.mnu_notification, 0, R.string.notifications);
            notifyItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        notifyItem.setIcon(getDrawable((ServiceNotify.Unread) ?
                R.drawable.ic_notifications_info_24dp : R.drawable.ic_notifications_white_24dp));
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logs.add(Logs.Type.V, null);
        unregisterReceiver(mLoggedReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logs.add(Logs.Type.V, null);
        registerReceiver(mLoggedReceiver, new IntentFilter(DataService.REQUEST_LOGOUT));
        registerReceiver(mLoggedReceiver, new IntentFilter(ServiceNotify.NOTIFICATION_DATA_CHANGE));

        onLoggedResume(); // Call implemented resume (if any)

        if (!DataService.isRunning())
            finish();
        else
            invalidateOptionsMenu(); // Refresh notification info
    }
}
