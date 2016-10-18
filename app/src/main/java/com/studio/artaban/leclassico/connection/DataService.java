package com.studio.artaban.leclassico.connection;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.connection.requests.NotificationsRequest;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.codes.WebServices;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.Notify;
import com.studio.artaban.leclassico.tools.Tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by pascal on 14/08/16.
 * Data service that manage synchronization with remote DB
 */
public class DataService extends Service implements Internet.OnConnectivityListener {

    private static final String EXTRA_DATA_PSEUDO = "pseudo";
    private static final String EXTRA_DATA_TOKEN = "token";
    private static final String EXTRA_DATA_TIME_LAG = "timeLag";

    public static final String EXTRA_DATA_TABLE_ID = "tableId";
    // Extra data keys

    private static boolean isRunning; // Service running flag
    public static boolean isRunning() {
        return isRunning;
    }

    public static boolean stop(Context context) { // Stop service

        Logs.add(Logs.Type.V, "context: " + context);
        if (!isRunning) {
            Logs.add(Logs.Type.W, "Service already stopped");
            return false;
        }
        context.stopService(new Intent(context, DataService.class));
        return true;
    }
    public static boolean start(Context context, String pseudo, String token, long timeLag)
            throws NullPointerException { // Start service

        Logs.add(Logs.Type.V, "context: " + context);
        if (isRunning) {
            Logs.add(Logs.Type.W, "Service already started");
            return false;
        }
        Intent intent = new Intent(context, DataService.class);
        intent.putExtra(EXTRA_DATA_PSEUDO, pseudo);
        intent.putExtra(EXTRA_DATA_TOKEN, token);
        intent.putExtra(EXTRA_DATA_TIME_LAG, timeLag);
        context.startService(intent);
        return true;
    }

    ////// Broadcast actions
    public static final String REGISTER_NEW_DATA = "com." + Constants.APP_URI_COMPANY + "." +
            Constants.APP_URI + ".action.REGISTER_NEW_DATA";
    public static final String UNREGISTER_NEW_DATA = "com." + Constants.APP_URI_COMPANY + "." +
            Constants.APP_URI + ".action.UNREGISTER_NEW_DATA";
    public static final String REQUEST_OLD_DATA = "com." + Constants.APP_URI_COMPANY + "." +
            Constants.APP_URI + ".action.REQUEST_OLD_DATA";

    //
    public static Intent getIntent(@Nullable Boolean newRegister, byte tableId, Uri uri) {
    // Return intent for data request according the 'newRegister' parameter:
        // true -> Register new data request
        // false -> Unregister new data request
        // null -> Old data request

        Logs.add(Logs.Type.V, "newRegister: " + newRegister + ";tableId: " + tableId + ";uri: " + uri);
        Intent dataIntent = new Intent((newRegister != null)?
                ((newRegister)? REGISTER_NEW_DATA:UNREGISTER_NEW_DATA):REQUEST_OLD_DATA);

        dataIntent.putExtra(DataService.EXTRA_DATA_TABLE_ID, tableId);
        dataIntent.putExtra(DataRequest.EXTRA_DATA_URI, uri);
        return dataIntent;
    }
    private final DataReceiver mDataReceiver = new DataReceiver(); // Data broadcast receiver
    private final ArrayList<DataRequest> mDataRequests = new ArrayList<>(); // Data request task list
    private final Timer mRequestTimer = new Timer(); // Timer to manage data request tasks

    private class DataReceiver extends BroadcastReceiver { /////////////////////////////////////////

        private static final long DELAY_REQUEST = 30000; // DB request task delay (in ms)

        @Override
        public void onReceive(Context context, Intent intent) {

            Logs.add(Logs.Type.V, "context: " + context + ";intent: " + intent);
            int tableIdx = intent.getByteExtra(EXTRA_DATA_TABLE_ID, (byte)0) - 1;
            if (tableIdx == Constants.NO_DATA)
                throw new IllegalArgumentException("Unexpected request table ID");

            if (mDataRequests.get(tableIdx) == null)
                throw new RuntimeException("Table request not implemented");

            //////
            if (intent.getAction().equals(REGISTER_NEW_DATA)) { // Register new data request
                if ((mDataRequests.get(tableIdx).register(intent)) && (Internet.isConnected()))
                    mRequestTimer.schedule(mDataRequests.get(tableIdx), DELAY_REQUEST, DELAY_REQUEST);
            }
            else if(intent.getAction().equals(UNREGISTER_NEW_DATA)) { // Unregister new data request

                mDataRequests.get(tableIdx).unregister(intent);
                mRequestTimer.purge(); // Purge cancelled request task (if any)

            } else if (intent.getAction().equals(REQUEST_OLD_DATA)) // Request old data
                mDataRequests.get(tableIdx).request(intent.getExtras());
        }
    }

    ////// OnConnectivityListener //////////////////////////////////////////////////////////////////
    @Override
    public void onConnection() {
        Logs.add(Logs.Type.V, null);

        // Restart/Start connection supervisor
        stopConnectionSupervisor();
        startConnectionSupervisor(true);

        // Restart/Start data requests
        stopDataRequests(false);
        startDataRequests();
    }
    @Override
    public void onDisconnection() {
        Logs.add(Logs.Type.V, null);

        stopConnectionSupervisor(); // Stop connection supervisor
        stopDataRequests(false); // Stop data requests
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateToken() { // Update token to keep connection active (or get it if null)

        Logs.add(Logs.Type.V, null);
        if (!Internet.isConnected())
            return;

        synchronized (mToken) {

            Date now = new Date();
            DateFormat dateFormat = new SimpleDateFormat(Constants.FORMAT_DATE_TIME);
            ContentValues data = new ContentValues();
            data.put(WebServices.CONNECTION_DATA_DATETIME, dateFormat.format(now));

            final Tools.LoginReply replyRes = new Tools.LoginReply();
            Internet.OnRequestListener receiveListener = new Internet.OnRequestListener() {
                @Override
                public boolean onReceiveReply(String response) {

                    Logs.add(Logs.Type.V, "response: " + response);
                    return Tools.receiveLogin(response, replyRes); // Manage method reply (below use)
                }
            };
            if (mToken != null) {
                Logs.add(Logs.Type.I, "Update token (" + mToken + ")");

                ////// Update token
                Internet.DownloadResult result = Internet.downloadHttpRequest(Constants.APP_WEBSERVICES +
                                WebServices.URL_CONNECTION + '?' + WebServices.DATA_TOKEN + '=' + mToken,
                        data, receiveListener);
                switch (result) {

                    case WRONG_URL:
                    case CONNECTION_FAILED:
                    case REPLY_ERROR: {

                        Logs.add(Logs.Type.E, "Token request failed");
                        return; // Nothing to do...
                    }
                    default: { ////// Reply succeeded (check it)
                        mToken = replyRes.token;

                        // Check update result
                        if (mToken != null) {
                            Logs.add(Logs.Type.I, "Token updated (" + mToken + ")");

                            mPseudo = replyRes.pseudo;
                            mTimeLag = replyRes.timeLag;
                            return; ////// OK
                        }
                        Logs.add(Logs.Type.W, "Token expired/invalid");
                        break; // Try to get a new one (below)
                    }
                }
            }
            Logs.add(Logs.Type.I, "Get new token");
            Cursor cursor = getContentResolver().query(Uri.parse(DataProvider.CONTENT_URI + CamaradesTable.TABLE_NAME),
                    new String[]{CamaradesTable.COLUMN_CODE_CONF}, CamaradesTable.COLUMN_PSEUDO + "='" +
                            mPseudo + '\'', null, null);
            cursor.moveToFirst();
            String password = cursor.getString(0);
            cursor.close();

            data.put(WebServices.CONNECTION_DATA_PSEUDO, mPseudo);
            data.put(WebServices.CONNECTION_DATA_PASSWORD, password);

            ////// Get new token
            Internet.DownloadResult result = Internet.downloadHttpRequest(Constants.APP_WEBSERVICES +
                    WebServices.URL_CONNECTION, data, receiveListener);
            switch (result) {

                case WRONG_URL:
                case CONNECTION_FAILED:
                case REPLY_ERROR: {

                    Logs.add(Logs.Type.W, "Login request failed");
                    return;
                }
                default: { ////// Reply succeeded (check it)
                    mToken = replyRes.token;

                    // Check login result
                    if (mToken != null) {
                        Logs.add(Logs.Type.I, "Token created");

                        mPseudo = replyRes.pseudo;
                        mTimeLag = replyRes.timeLag;
                        ////// OK

                    } else {
                        Logs.add(Logs.Type.F, "Login failed");

                        stopSelf();
                        // NB: This will close the application (at service binding). The user has been
                        //     connected offline using local DB but not exist in remote DB. Let's the
                        //     user to reconnect by entering new login.
                    }
                    break;
                }
            }
        }
    }

    //
    private void startConnectionSupervisor(boolean now) {

        Logs.add(Logs.Type.V, "now: " + now);
        mTokenTimer.schedule(new TimerTask() {
                                 @Override
                                 public void run() {

                                     Logs.add(Logs.Type.V, null);
                                     updateToken();
                                 }
                             }, (now) ? 0 : Constants.SERVICE_DELAY_TOKEN_UPDATE,
                Constants.SERVICE_DELAY_TOKEN_UPDATE);
    }
    private void startDataRequests() { // Schedule data requests
        Logs.add(Logs.Type.V, null);

        for (DataRequest request : mDataRequests) {
            if (request != null)
                mRequestTimer.schedule(request, DataReceiver.DELAY_REQUEST, DataReceiver.DELAY_REQUEST);
        }
    }

    private void stopConnectionSupervisor() {
        Logs.add(Logs.Type.V, null);
        mTokenTimer.cancel();
        mTokenTimer.purge();
    }
    private void stopDataRequests(boolean clear) { // Cancel & clear data requests (clear if required)

        Logs.add(Logs.Type.V, "clear: " + clear);
        for (TimerTask request : mDataRequests) {
            if (request != null)
                request.cancel();
        }
        if (clear)
            mDataRequests.clear();

        mRequestTimer.cancel();
        mRequestTimer.purge();
    }

    //////
    public class DataBinder extends Binder {
        public DataService getService() {
            return DataService.this;
        }
    };
    private final Binder mBinder = new DataBinder();

    //
    private String mToken; // Token used to identify the user when requesting remote DB
    public String getToken() {
        synchronized(mToken) {
            return mToken;
        }
    }
    private final Timer mTokenTimer = new Timer(); // Timer used to update token (connection supervisor)
    private long mTimeLag; // Time lag between remote DB & current OS (in ms)
                           // NB: Will be updated at every remote requests coz OS date & time can changed

    private String mPseudo;
    // Login pseudo (used to get a token if working offline and an Internet connection is established)

    ////// Service /////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate() {
        super.onCreate();
        Logs.add(Logs.Type.V, null);
        isRunning = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Logs.add(Logs.Type.V, "intent: " + intent + ";flags: " + flags + ";startId: " + startId);
        mPseudo = intent.getStringExtra(EXTRA_DATA_PSEUDO);
        mToken = intent.getStringExtra(EXTRA_DATA_TOKEN);
        mTimeLag = intent.getLongExtra(EXTRA_DATA_TIME_LAG, 0);

        // Add notification
        Bundle notifyData = new Bundle();
        notifyData.putInt(Notify.DATA_KEY_ICON, R.drawable.notification);
        notifyData.putString(Notify.DATA_KEY_TITLE, getString(R.string.app_name));
        notifyData.putString(Notify.DATA_KEY_TEXT, getString(R.string.pseudo_connected, mPseudo));

        Notify.update(this, Notify.Type.EVENT, null, notifyData);

        // Start connection supervisor (if connected)
        if (Internet.isConnected())
            startConnectionSupervisor(false);

        // Set broadcast receiver
        for (byte tableId = 1; tableId <= Tables.ID_LAST; ++tableId) {
            switch (tableId) {

                case Tables.ID_NOTIFICATIONS: {
                    mDataRequests.add(new NotificationsRequest(this, NotificationsTable.TABLE_NAME));
                    break;
                }
                case Tables.ID_CAMARADES:
                case Tables.ID_ABONNEMENTS:
                case Tables.ID_ACTUALITES:
                case Tables.ID_ALBUMS:
                case Tables.ID_COMMENTAIRES:
                case Tables.ID_EVENEMENTS:
                case Tables.ID_MESSAGERIE:
                case Tables.ID_MUSIC:
                case Tables.ID_PHOTOS:
                case Tables.ID_PRESENTS:
                case Tables.ID_VOTES: {
                    mDataRequests.add(null);
                    break; // Not implemented yet
                }
            }
        }
        registerReceiver(mDataReceiver, new IntentFilter(REGISTER_NEW_DATA));
        registerReceiver(mDataReceiver, new IntentFilter(UNREGISTER_NEW_DATA));
        registerReceiver(mDataReceiver, new IntentFilter(REQUEST_OLD_DATA));

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Logs.add(Logs.Type.V, null);
        isRunning = false;

        Notify.cancel(this); // Remove notification
        stopConnectionSupervisor(); // Cancel token update

        // Remove broadcast receiver
        unregisterReceiver(mDataReceiver);
        stopDataRequests(true);
    }
}
