package com.studio.artaban.leclassico.activities;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.notification.NotifyActivity;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.helpers.QueryLoader;
import com.studio.artaban.leclassico.services.DataService;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 28/10/16.
 * Logged activity with logout management
 */
public abstract class LoggedActivity extends AppCompatActivity implements QueryLoader.OnResultListener {

    public static final String EXTRA_DATA_ID = "id"; // WARNING: Data can be != than Login.EXTRA_DATA_PSEUDO_ID
    public static final String EXTRA_DATA_URI = "uri";
    // Extra data keys

    protected int mId; // ID (pseudo, publication, etc)

    protected static Uri mNotifyURI; // User notifications URI
    // NB: Not private coz used in 'NotifyActivity' +
    //     Static coz Login.EXTRA_DATA_PSEUDO_ID extras login data not always defined (called from link)

    private final QueryLoader mNewNotifyLoader = new QueryLoader(this, this); // New notification query loader
    private boolean mNotifyExists; // Existing notification flag
    private boolean mNotifyNew; // New notification flag (first unread exists)

    private final LoggedReceiver mLoggedReceiver = new LoggedReceiver(); // Activity broadcast receiver
    private class LoggedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Logs.add(Logs.Type.V, "context: " + context + ";intent: " + intent);
            if (intent.getAction().equals(DataService.REQUEST_LOGOUT)) ////// Logout requested
                finish(); // Finish activity
        }
    }

    //////
    protected void startNotificationActivity() { // Start notification activity

        Logs.add(Logs.Type.V, null);
        if (mNotifyExists) {

            ////// Start notification activity
            Intent notifyIntent = new Intent(this, NotifyActivity.class);
            Login.copyExtraData(getIntent(), notifyIntent);
            startActivity(notifyIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        } else
            Toast.makeText(this, R.string.no_notification, Toast.LENGTH_SHORT).show();
    }

    protected boolean onNotifyItemSelected(MenuItem item) { // Notification menu item clicked
        Logs.add(Logs.Type.V, "item: " + item);

        if (item.getItemId() == R.id.mnu_notification) {
            startNotificationActivity();
            return true;
        }
        return false;
    }
    protected boolean onNotifyLoadFinished(int id, Cursor cursor) { // Notification info updated
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        if (!cursor.moveToFirst())
            return false;

        if (id == Queries.NOTIFICATIONS_NEW_INFO) {
            Logs.add(Logs.Type.I, "New notification info (" + this + ')');

            mNotifyExists = true;
            boolean prevNewFlag = mNotifyNew;

            mNotifyNew = (cursor.getInt(0) == Constants.DATA_UNREAD);
            if (prevNewFlag != mNotifyNew)
                invalidateOptionsMenu();

            return true; // Proceeded
        }
        return false;
    }

    ////// LoggedActivity //////////////////////////////////////////////////////////////////////////

    protected abstract void onLoggedResume();

    ////// AppCompatActivity ///////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);

        // Set URI to observe notification DB changes (if not already done)
        if (mNotifyURI == null)
            mNotifyURI = Uris.getUri(Uris.ID_USER_NOTIFICATIONS,
                    String.valueOf(getIntent().getIntExtra(Login.EXTRA_DATA_PSEUDO_ID, Constants.NO_DATA)));
                    // NB: The first logged activity started is the 'MainActivity' which always
                    //     contains extras login data

        // Load notification info (using query loader)
        Bundle notifyData = new Bundle();
        notifyData.putParcelable(QueryLoader.DATA_KEY_URI, mNotifyURI);
        notifyData.putString(QueryLoader.DATA_KEY_SELECTION,
                "SELECT " + NotificationsTable.COLUMN_LU_FLAG + " FROM " + NotificationsTable.TABLE_NAME +
                        " WHERE " + NotificationsTable.COLUMN_PSEUDO + "='" +
                        getIntent().getStringExtra(Login.EXTRA_DATA_PSEUDO) +
                        "' ORDER BY " + NotificationsTable.COLUMN_DATE + " DESC");
        mNewNotifyLoader.init(this, Queries.NOTIFICATIONS_NEW_INFO, notifyData);

        // Register new user notifications service
        sendBroadcast(DataService.getIntent(true, Tables.ID_NOTIFICATIONS, mNotifyURI));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Logs.add(Logs.Type.V, "menu: " + menu);

        // Add notification menu item (if not already done)
        MenuItem notifyItem = menu.findItem(R.id.mnu_notification);
        if (notifyItem == null) {
            notifyItem = menu.add(0, R.id.mnu_notification, 0, R.string.notifications);
            notifyItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        Logs.add(Logs.Type.I, "New: " + mNotifyNew);
        notifyItem.setIcon(getDrawable((mNotifyNew) ?
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

        onLoggedResume(); // Call implemented resume (if any)

        if (!DataService.isRunning())
            finish();
        else
            getContentResolver().notifyChange(mNotifyURI, null); // Refresh notification info
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logs.add(Logs.Type.V, null);

        // Unregister new user notifications service
        sendBroadcast(DataService.getIntent(false, Tables.ID_NOTIFICATIONS, mNotifyURI));
    }
}
