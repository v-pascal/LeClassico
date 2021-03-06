package com.studio.artaban.leclassico.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.connection.ServiceNotify;
import com.studio.artaban.leclassico.connection.requests.AbonnementsRequest;
import com.studio.artaban.leclassico.connection.requests.ActualitesRequest;
import com.studio.artaban.leclassico.connection.requests.AlbumsRequest;
import com.studio.artaban.leclassico.connection.requests.CamaradesRequest;
import com.studio.artaban.leclassico.connection.requests.CommentairesRequest;
import com.studio.artaban.leclassico.connection.requests.EvenementsRequest;
import com.studio.artaban.leclassico.connection.requests.LocationsRequest;
import com.studio.artaban.leclassico.connection.requests.MessagerieRequest;
import com.studio.artaban.leclassico.connection.requests.MusicRequest;
import com.studio.artaban.leclassico.connection.requests.NotificationsRequest;
import com.studio.artaban.leclassico.connection.requests.PhotosRequest;
import com.studio.artaban.leclassico.connection.requests.PresentsRequest;
import com.studio.artaban.leclassico.connection.requests.VotesRequest;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.codes.WebServices;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.data.tables.LocationsTable;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;

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
public class DataService extends Service implements Internet.OnConnectivityListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String EXTRA_DATA_RECEIVED = "received";
    public static final String EXTRA_DATA_REQUEST_RESULT = "requestResult";

    private static final String EXTRA_DATA_TOKEN = "token";
    private static final String EXTRA_DATA_TIME_LAG = "timeLag";
    private static final String EXTRA_DATA_TABLE_ID = "tableId";

    private static final String EXTRA_DATA_ENABLE = "enable";
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
    public static boolean start(Context context, String pseudo, int pseudoId, String token, long timeLag)
            throws NullPointerException { // Start service

        Logs.add(Logs.Type.V, "context: " + context + ";pseudo: " + pseudo + ";pseudoId: " + pseudoId +
                ";timeLag: " + timeLag);
        if (isRunning) {
            Logs.add(Logs.Type.W, "Service already started");
            return false;
        }
        Intent intent = new Intent(context, DataService.class);
        intent.putExtra(Login.EXTRA_DATA_PSEUDO, pseudo);
        intent.putExtra(Login.EXTRA_DATA_PSEUDO_ID, pseudoId);
        intent.putExtra(EXTRA_DATA_TOKEN, token);
        intent.putExtra(EXTRA_DATA_TIME_LAG, timeLag);
        context.startService(intent);
        return true;
    }

    ////// Broadcast actions
    public static final String REQUEST_LOGOUT = "com." + Constants.APP_URI_COMPANY + '.' +
            Constants.APP_URI + ".action.REQUEST_LOGOUT";

    public static final String REQUEST_OLD_DATA = "com." + Constants.APP_URI_COMPANY + '.' +
            Constants.APP_URI + ".action.REQUEST_OLD_DATA";
    private static final String REGISTER_NEW_DATA = "com." + Constants.APP_URI_COMPANY + '.' +
            Constants.APP_URI + ".action.REGISTER_NEW_DATA";
    private static final String UNREGISTER_NEW_DATA = "com." + Constants.APP_URI_COMPANY + '.' +
            Constants.APP_URI + ".action.UNREGISTER_NEW_DATA";

    private static final String REQUEST_LOCATION = "com." + Constants.APP_URI_COMPANY + '.' +
            Constants.APP_URI + ".action.REQUEST_LOCATION";

    //
    public static Intent getIntent(Intent action, byte tableId, Uri uri) {
    // Return action intent with table & URI specification

        Logs.add(Logs.Type.V, "action: " + action + ";tableId: " + tableId + ";uri: " + uri);
        action.putExtra(EXTRA_DATA_TABLE_ID, tableId);
        action.putExtra(DataRequest.EXTRA_DATA_URI, uri);
        return action;
    }
    public static Intent getIntent(boolean register, byte tableId, Uri uri) {
    // Return intent for new data request according the 'register' parameter (register/unregister)

        Logs.add(Logs.Type.V, "register: " + register + ";tableId: " + tableId + ";uri: " + uri);
        return getIntent(new Intent((register)? REGISTER_NEW_DATA:UNREGISTER_NEW_DATA), tableId, uri);
    }
    public static Intent getIntent(boolean locate) { // Return intent to set location request
        Logs.add(Logs.Type.V, "locate: " + locate);

        Intent intent = new Intent(REQUEST_LOCATION);
        intent.putExtra(EXTRA_DATA_ENABLE, locate);
        return intent;
    }
    private final DataReceiver mDataReceiver = new DataReceiver(); // Data broadcast receiver
    private final ArrayList<DataRequest> mDataRequests = new ArrayList<>(); // Data request task list
    private Timer mRequestTimer; // Timer to manage data request tasks

    private class DataReceiver extends BroadcastReceiver { /////////////////////////////////////////
        @Override
        public void onReceive(Context context, Intent intent) {

            Logs.add(Logs.Type.V, "context: " + context + ";intent: " + intent);
            if (intent.getAction().equals(REQUEST_LOGOUT)) // Logout
                stopSelf();

            else if (intent.getAction().equals(REQUEST_LOCATION)) { ////// Location
                if (intent.getBooleanExtra(EXTRA_DATA_ENABLE, false)) { // Enable

                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    try {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApi,
                                locationRequest, DataService.this);
                    } catch (SecurityException e) {
                        Logs.add(Logs.Type.F, "Location permission not granted");
                    }

                } else { // Disable
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApi, DataService.this);

                    mLatitude = null;
                    mLongitude = null;
                }

            } else {

                int tableIdx = intent.getByteExtra(EXTRA_DATA_TABLE_ID, (byte)0) - 1;
                if (tableIdx == Constants.NO_DATA)
                    throw new IllegalArgumentException("Unexpected request table ID");

                //////
                if (intent.getAction().equals(REGISTER_NEW_DATA)) { ////// Register new data
                    if (mDataRequests.get(tableIdx).register(intent)) {

                        Logs.add(Logs.Type.I, "Schedule table ID #" + (tableIdx + 1) + " request");
                        DataRequest request = mDataRequests.get(tableIdx);
                        mRequestTimer.schedule(request.getTask(), 0, request.getDelay());
                    }
                }
                else if(intent.getAction().equals(UNREGISTER_NEW_DATA)) { ////// Unregister new data
                    if (mDataRequests.get(tableIdx).unregister(intent)) {

                        Logs.add(Logs.Type.I, "Purge table ID #" + (tableIdx + 1) + " request");
                        mRequestTimer.purge(); // Purge cancelled request task
                    }

                } else if ((intent.getAction().equals(REQUEST_OLD_DATA)) &&
                        (!intent.getBooleanExtra(EXTRA_DATA_RECEIVED, false)))
                    mRequestLooper.sendMessage(tableIdx, intent.getExtras()); ////// Request old data
            }
        }
    }

    //////
    private LooperThread mRequestLooper; // Old requests processing thread loop
    class LooperThread extends Thread {

        private Handler mHandler;
        public void sendMessage(int tableIdx, Bundle data) { // Send old request message
            Logs.add(Logs.Type.V, "tableIdx: " + tableIdx + ";data: " + data);

            Message request = new Message();
            request.what = tableIdx;
            request.obj = data;
            mHandler.sendMessage(request);
        }
        public void quit() { // Stop looper thread
            Logs.add(Logs.Type.V, null);
            mHandler.getLooper().quit();
        }

        @Override
        public void run() {

            Looper.prepare();
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Logs.add(Logs.Type.V, "msg: " + msg);

                    // Make a pause (let's old request animation work)
                    try {
                        long delay = getResources().getInteger(R.integer.duration_request_anim) << 1;
                        Thread.sleep(delay, 0); // NB: Double delay to be sure animation displayed

                    } catch (InterruptedException e) {
                        Logs.add(Logs.Type.E, "Olb request delay interrupted");
                    }
                    // Request old entries
                    DataRequest.Result result = mDataRequests.get(msg.what).request((Bundle)msg.obj);

                    // Send old request finished info (received)
                    Intent received = new Intent(REQUEST_OLD_DATA);
                    received.putExtra(EXTRA_DATA_TABLE_ID, (byte)msg.what);
                    received.putExtra(EXTRA_DATA_RECEIVED, true);
                    received.putExtra(EXTRA_DATA_REQUEST_RESULT, result);
                    received.putExtra(DataRequest.EXTRA_DATA_URI,
                            ((Bundle)msg.obj).getParcelable(DataRequest.EXTRA_DATA_URI));

                    sendBroadcast(received);
                }
            };
            Looper.loop();
            Logs.add(Logs.Type.I, "Old request thread stopped");
        }
    }

    ////// ConnectionCallbacks /////////////////////////////////////////////////////////////////////
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Logs.add(Logs.Type.V, "bundle: " + bundle);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Logs.add(Logs.Type.V, "cause: " + cause);
        mGoogleApi.connect();
    }
    private GoogleApiClient mGoogleApi; // Google API client

    ////// OnConnectionFailedListener //////////////////////////////////////////////////////////////
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Logs.add(Logs.Type.E, "connectionResult: " + connectionResult);
    }

    ////// LocationListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLocationChanged(Location location) {

        Logs.add(Logs.Type.V, "location: " + location);
        if ((mLatitude == null) || (Math.abs(location.getLatitude() - mLatitude) > LOCATION_PRECISION) ||
            (mLongitude == null) || (Math.abs(location.getLongitude() - mLongitude) > LOCATION_PRECISION)) {

            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();

            // Update local DB
            Uri uri = Uri.parse(DataProvider.CONTENT_URI + LocationsTable.TABLE_NAME);
            String selection = LocationsTable.COLUMN_PSEUDO + "='" + mDataLogin.pseudo + '\'';
            int id = DataTable.getEntryId(getContentResolver(), LocationsTable.TABLE_NAME, selection);

            ContentValues values = new ContentValues();
            values.put(LocationsTable.COLUMN_LATITUDE, mLatitude);
            values.put(LocationsTable.COLUMN_LONGITUDE, mLongitude);
            if (id == Constants.NO_DATA) {

                values.put(LocationsTable.COLUMN_PSEUDO, mDataLogin.pseudo);
                DataTable.addSyncFields(values, DataTable.Synchronized.TO_INSERT.getValue());
                getContentResolver().insert(uri, values);

            } else
                getContentResolver().update(uri, values, selection, null);

            // Update remote DB
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Logs.add(Logs.Type.V, null);
                    mDataRequests.get(Tables.ID_LOCATIONS - 1).synchronize();
                }
            }).start();
        }
    }
    private static final double LOCATION_PRECISION = 0.0001; // 10 meters

    private Double mLatitude;
    private Double mLongitude;
    // User location coordinates

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

            final Login.Reply replyRes = new Login.Reply();
            Internet.OnRequestListener receiveListener = new Internet.OnRequestListener() {
                @Override
                public boolean onReceiveReply(String response) {

                    Logs.add(Logs.Type.V, "response: " + response);
                    return Login.receive(response, replyRes); // Manage method reply (below use)
                }
            };
            if (mDataLogin.token.get() != null) {
                Logs.add(Logs.Type.I, "Update token (" + mDataLogin.token.get() + ')');

                ////// Update token
                Internet.DownloadResult result = Internet.downloadHttpRequest(Constants.APP_WEBSERVICES +
                                WebServices.URL_CONNECTION + '?' + WebServices.DATA_TOKEN + '=' + mDataLogin.token.get(),
                        data, null, receiveListener);
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
                            Logs.add(Logs.Type.I, "Token updated (" + mDataLogin.token.get() + ')');

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
                    WebServices.URL_CONNECTION, data, null, receiveListener);
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

        for (final DataRequest request : mDataRequests) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    request.synchronize(); // Synchronize from local to remote DB
                }
            }).start();

            TimerTask task = request.getTask();
            if (task != null)
                mRequestTimer.schedule(task, 0, request.getDelay());
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
    private final Login.Reply mDataLogin = new Login.Reply(); // Login info (token, pseudo & time lag)
    public void copyLoginData(Login.Reply into) {
        synchronized (mDataLogin.token) {
            mDataLogin.copy(into);
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
        mDataLogin.pseudo = intent.getStringExtra(Login.EXTRA_DATA_PSEUDO);
        mDataLogin.pseudoId = intent.getIntExtra(Login.EXTRA_DATA_PSEUDO_ID, Constants.NO_DATA);
        mDataLogin.token.set(intent.getStringExtra(EXTRA_DATA_TOKEN));
        mDataLogin.timeLag = intent.getLongExtra(EXTRA_DATA_TIME_LAG, 0);

        // Create service notification
        ServiceNotify.create(this, mDataLogin.pseudo);

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
                case Tables.ID_LOCATIONS: mDataRequests.add(new LocationsRequest(this)); break;
            }
        }
        mRequestTimer = new Timer();
        mRequestLooper = new LooperThread();
        mRequestLooper.start();

        // Register broadcast
        registerReceiver(mDataReceiver, new IntentFilter(REQUEST_LOGOUT));
        registerReceiver(mDataReceiver, new IntentFilter(REGISTER_NEW_DATA));
        registerReceiver(mDataReceiver, new IntentFilter(UNREGISTER_NEW_DATA));
        registerReceiver(mDataReceiver, new IntentFilter(REQUEST_OLD_DATA));
        registerReceiver(mDataReceiver, new IntentFilter(REQUEST_LOCATION));

        // Start google API service
        mGoogleApi = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApi.connect();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Logs.add(Logs.Type.V, null);
        Internet.removeConnectivityListener(this);
        isRunning = false;

        ServiceNotify.remove(this); // Remove notification
        stopConnectionSupervisor(); // Cancel token update

        // Remove broadcast receiver & request management
        unregisterReceiver(mDataReceiver);
        stopDataRequests(true);

        // Stop google API service
        mGoogleApi.disconnect();

        mRequestLooper.quit();
    }
}
