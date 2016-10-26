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
import com.studio.artaban.leclassico.connection.requests.AbonnementsRequest;
import com.studio.artaban.leclassico.connection.requests.ActualitesRequest;
import com.studio.artaban.leclassico.connection.requests.AlbumsRequest;
import com.studio.artaban.leclassico.connection.requests.CamaradesRequest;
import com.studio.artaban.leclassico.connection.requests.CommentairesRequest;
import com.studio.artaban.leclassico.connection.requests.EvenementsRequest;
import com.studio.artaban.leclassico.connection.requests.MessagerieRequest;
import com.studio.artaban.leclassico.connection.requests.MusicRequest;
import com.studio.artaban.leclassico.connection.requests.NotificationsRequest;
import com.studio.artaban.leclassico.connection.requests.PhotosRequest;
import com.studio.artaban.leclassico.connection.requests.PresentsRequest;
import com.studio.artaban.leclassico.connection.requests.VotesRequest;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.codes.WebServices;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
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
    public static final String REGISTER_NEW_DATA = "com." + Constants.APP_URI_COMPANY + '.' +
            Constants.APP_URI + ".action.REGISTER_NEW_DATA";
    public static final String UNREGISTER_NEW_DATA = "com." + Constants.APP_URI_COMPANY + '.' +
            Constants.APP_URI + ".action.UNREGISTER_NEW_DATA";
    public static final String REQUEST_OLD_DATA = "com." + Constants.APP_URI_COMPANY + '.' +
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
    private Timer mRequestTimer; // Timer to manage data request tasks

    private class DataReceiver extends BroadcastReceiver { /////////////////////////////////////////

        private static final long DELAY_REQUEST = 30000; // DB request task delay (in ms)

        @Override
        public void onReceive(Context context, Intent intent) {

            Logs.add(Logs.Type.V, "context: " + context + ";intent: " + intent);
            int tableIdx = intent.getByteExtra(EXTRA_DATA_TABLE_ID, (byte)0) - 1;
            if (tableIdx == Constants.NO_DATA)
                throw new IllegalArgumentException("Unexpected request table ID");

            //////
            if (intent.getAction().equals(REGISTER_NEW_DATA)) { // Register new data request
                if (mDataRequests.get(tableIdx).register(intent)) {

                    Logs.add(Logs.Type.I, "Schedule table ID #" + (tableIdx + 1) + " request");
                    mRequestTimer.schedule(mDataRequests.get(tableIdx).getTask(), 0, DELAY_REQUEST);
                }
            }
            else if(intent.getAction().equals(UNREGISTER_NEW_DATA)) { // Unregister new data request
                if (mDataRequests.get(tableIdx).unregister(intent)) {

                    Logs.add(Logs.Type.I, "Purge table ID #" + (tableIdx + 1) + " request");
                    mRequestTimer.purge(); // Purge cancelled request task
                }

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

        synchronized (mDataLogin.token) {

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
            if (mDataLogin.token.get() != null) {
                Logs.add(Logs.Type.I, "Update token (" + mDataLogin.token.get() + ")");

                ////// Update token
                Internet.DownloadResult result = Internet.downloadHttpRequest(Constants.APP_WEBSERVICES +
                                WebServices.URL_CONNECTION + '?' + WebServices.DATA_TOKEN + '=' + mDataLogin.token.get(),
                        data, receiveListener);
                switch (result) {

                    case WRONG_URL:
                    case CONNECTION_FAILED:
                    case REPLY_ERROR: {

                        Logs.add(Logs.Type.E, "Token request failed");
                        return; // Nothing to do...
                    }
                    default: { ////// Reply succeeded (check it)
                        mDataLogin.token.set(replyRes.token.get());

                        // Check update result
                        if (mDataLogin.token.get() != null) {
                            Logs.add(Logs.Type.I, "Token updated (" + mDataLogin.token.get() + ")");

                            mDataLogin.pseudo = replyRes.pseudo;
                            mDataLogin.timeLag = replyRes.timeLag;
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
                            mDataLogin.pseudo + '\'', null, null);
            cursor.moveToFirst();
            String password = cursor.getString(0);
            cursor.close();

            data.put(WebServices.CONNECTION_DATA_PSEUDO, mDataLogin.pseudo);
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
                    mDataLogin.token.set(replyRes.token.get());

                    // Check login result
                    if (mDataLogin.token.get() != null) {
                        Logs.add(Logs.Type.I, "Token created");

                        mDataLogin.pseudo = replyRes.pseudo;
                        mDataLogin.timeLag = replyRes.timeLag;
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

        mTokenTimer = new Timer();
        mTokenTimer.schedule(new TimerTask() {
                                 @Override
                                 public void run() {

                                     Logs.add(Logs.Type.V, null);
                                     updateToken();
                                 }

                             }, (now) ? 0 : Constants.SERVICE_DELAY_TOKEN_UPDATE,
                Constants.SERVICE_DELAY_TOKEN_UPDATE);
    }
    private void stopConnectionSupervisor() {

        Logs.add(Logs.Type.V, null);
        if (mTokenTimer != null) // Can be cancelled without having started (offline)
            mTokenTimer.cancel();
    }

    private void startDataRequests() { // Schedule data requests
        Logs.add(Logs.Type.V, null);

        for (DataRequest request : mDataRequests) {
            if (request.toSynchronize())
                request.synchronize(); // Synchronize from local to remote DB

            TimerTask task = request.getTask();
            if (task != null)
                mRequestTimer.schedule(task, 0, DataReceiver.DELAY_REQUEST);
        }
    }
    private void stopDataRequests(boolean clear) { // Cancel data requests (and clear if requested)

        Logs.add(Logs.Type.V, "clear: " + clear);
        for (DataRequest request : mDataRequests)
            request.cancel();

        if (clear) {
            mDataRequests.clear();
            mRequestTimer.cancel();

        } else
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
    private final Tools.LoginReply mDataLogin = new Tools.LoginReply(); // Login info (token, pseudo & time lag)
    public Tools.LoginReply getLoginData() {
        synchronized (mDataLogin.token) {
            return mDataLogin;
        }
    }
    private Timer mTokenTimer; // Timer used to update token (connection supervisor)

    ////// Service /////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate() {
        super.onCreate();

        Logs.add(Logs.Type.V, null);
        isRunning = true;
        Internet.addConnectivityListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Logs.add(Logs.Type.V, "intent: " + intent + ";flags: " + flags + ";startId: " + startId);
        mDataLogin.pseudo = intent.getStringExtra(EXTRA_DATA_PSEUDO);
        mDataLogin.token.set(intent.getStringExtra(EXTRA_DATA_TOKEN));
        mDataLogin.timeLag = intent.getLongExtra(EXTRA_DATA_TIME_LAG, 0);

        // Add notification
        Bundle notifyData = new Bundle();
        notifyData.putInt(Notify.DATA_KEY_ICON, R.drawable.notification);
        notifyData.putString(Notify.DATA_KEY_TITLE, getString(R.string.app_name));
        notifyData.putString(Notify.DATA_KEY_TEXT, getString(R.string.pseudo_connected, mDataLogin.pseudo));

        Notify.update(this, Notify.Type.EVENT, null, notifyData);

        // Start connection supervisor (if connected)
        if (Internet.isConnected())
            startConnectionSupervisor(false);

        // Set broadcast receiver & request management
        for (byte tableId = 1; tableId <= Tables.ID_LAST; ++tableId) {
            switch (tableId) {

                case Tables.ID_NOTIFICATIONS: mDataRequests.add(new NotificationsRequest(this)); break;
                case Tables.ID_CAMARADES: mDataRequests.add(new CamaradesRequest(this)); break;
                case Tables.ID_ABONNEMENTS: mDataRequests.add(new AbonnementsRequest(this)); break;
                case Tables.ID_ACTUALITES: mDataRequests.add(new ActualitesRequest(this)); break;
                case Tables.ID_ALBUMS: mDataRequests.add(new AlbumsRequest(this)); break;
                case Tables.ID_COMMENTAIRES: mDataRequests.add(new CommentairesRequest(this)); break;
                case Tables.ID_EVENEMENTS: mDataRequests.add(new EvenementsRequest(this)); break;
                case Tables.ID_MESSAGERIE: mDataRequests.add(new MessagerieRequest(this)); break;
                case Tables.ID_MUSIC: mDataRequests.add(new MusicRequest(this)); break;
                case Tables.ID_PHOTOS: mDataRequests.add(new PhotosRequest(this)); break;
                case Tables.ID_PRESENTS: mDataRequests.add(new PresentsRequest(this)); break;
                case Tables.ID_VOTES: mDataRequests.add(new VotesRequest(this)); break;
            }
        }
        mRequestTimer = new Timer();

        registerReceiver(mDataReceiver, new IntentFilter(REGISTER_NEW_DATA));
        registerReceiver(mDataReceiver, new IntentFilter(UNREGISTER_NEW_DATA));
        registerReceiver(mDataReceiver, new IntentFilter(REQUEST_OLD_DATA));

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Logs.add(Logs.Type.V, null);
        Internet.removeConnectivityListener(this);
        isRunning = false;

        Notify.cancel(this); // Remove notification
        stopConnectionSupervisor(); // Cancel token update

        // Remove broadcast receiver & request management
        unregisterReceiver(mDataReceiver);
        stopDataRequests(true);
    }
}
