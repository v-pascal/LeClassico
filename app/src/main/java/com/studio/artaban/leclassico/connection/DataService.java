package com.studio.artaban.leclassico.connection;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;

public class DataService extends Service implements Internet.OnConnectivityListener {

    private static boolean isRunning; // Service running flag
    public static boolean isRunning() {
        return isRunning;
    }

    ////// Broadcast actions
    public static final String STATUS_CONNECTION = "com." + Constants.APP_URI_COMPANY + "." +
            Constants.APP_URI + ".action.STATUS_CONNECTION";
    public static final String STATUS_SYNCHRONIZATION = "com." + Constants.APP_URI_COMPANY + "." +
            Constants.APP_URI + ".action.STATUS_SYNCHRONIZATION";

    public static final String NEW_NOTIFICATIONS = "com." + Constants.APP_URI_COMPANY + "." +
            Constants.APP_URI + ".action.NEW_NOTIFICATIONS";

    public static final String NEW_PUBLICATIONS = "com." + Constants.APP_URI_COMPANY + "." +
            Constants.APP_URI + ".action.NEW_PUBLICATIONS";
    public static final String NEW_COMMENTS = "com." + Constants.APP_URI_COMPANY + "." +
            Constants.APP_URI + ".action.NEW_COMMENTS";
    public static final String NEW_MESSAGES = "com." + Constants.APP_URI_COMPANY + "." +
            Constants.APP_URI + ".action.NEW_MESSAGES";
    public static final String NEW_LOCATIONS = "com." + Constants.APP_URI_COMPANY + "." +
            Constants.APP_URI + ".action.NEW_LOCATIONS";
    public static final String NEW_EVENTS = "com." + Constants.APP_URI_COMPANY + "." +
            Constants.APP_URI + ".action.NEW_EVENTS";

    ////// Broadcast keys & data
    public static final byte STATE_ERROR = 0;

    // action.STATUS_CONNECTION
    public static final String CONNECTION_STATE = "connectionState";
    public static final byte CONNECTION_STATE_CONNECTED = 1;
    public static final byte CONNECTION_STATE_LOGIN_FAILED = 2;
    public static final byte CONNECTION_STATE_EXPIRED = 3;

    private static final String CONNECTION_DATA_PSEUDO = "pseudo";
    private static final String CONNECTION_DATA_PASSWORD = "password";

    // action.STATUS_SYNCHRONIZATION
    public static final String SYNCHRONIZATION_STATE = "synchronizationState";
    public static final byte SYNCHRONIZATION_STATE_DONE = 1;

    //////
    @Override
    public void onConnection() {








    }
    @Override
    public void onDisconnection() {







    }

    //////
    public class DataBinder extends Binder {
        public DataService getService() {
            return DataService.this;
        }
    };
    private final Binder mBinder = new DataBinder();

    private String mToken; // Token used to identify the user when requesting remote DB
    private long mTimeLag; // Time lag between remote DB & current OS (in milliseconds)

    //
    public void stop() {

        Logs.add(Logs.Type.V, null);
        isRunning = false;

        logout();
    }

    private String mPseudo;
    private String mPassword;
    // Login

    public boolean login(String pseudo, String password) {

        Logs.add(Logs.Type.V, "pseudo: " + pseudo + ";password: " + password);
        mPseudo = pseudo;
        mPassword = password;

        if (!Internet.isConnected())
            return false;

        ContentValues data = new ContentValues();
        data.put(CONNECTION_DATA_PSEUDO, pseudo);
        data.put(CONNECTION_DATA_PASSWORD, password);

        Internet.DownloadResult result = Internet.downloadHttpRequest(Constants.APP_WEBSERVICES +
                Constants.WEBSERVICE_CONNECTION, data, new Internet.OnRequestListener() {
            @Override
            public void onReceiveReply(String response) {








                //mToken








            }
        });
        if (result != Internet.DownloadResult.SUCCEEDED) { // == CONNECTION_FAILED
            Logs.add(Logs.Type.E, "Connection request error");

            ////// STATUS_CONNECTION
            Intent intent = new Intent(STATUS_CONNECTION);
            intent.putExtra(CONNECTION_STATE, STATE_ERROR);
            sendBroadcast(intent);
        }
        return true;
    }
    public boolean synchronize() {

        Logs.add(Logs.Type.V, null);
        if ((!Internet.isConnected()) || (mToken == null))
            return false;










        return true;
    }
    public void logout() {

        Logs.add(Logs.Type.V, null);
        mPseudo = null;
        mPassword = null;








        mToken = null;







    }

    //////
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Logs.add(Logs.Type.V, "intent: " + intent + ";flags: " + flags + ";startId: " + startId);
        isRunning = true;
        return START_NOT_STICKY;
    }
}
