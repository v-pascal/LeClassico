package com.studio.artaban.leclassico.connection;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.Notify;

/**
 * Created by pascal on 14/08/16.
 * Data service that manage synchronization with remote DB
 */
public class DataService extends Service implements Internet.OnConnectivityListener {

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
    public static boolean start(Context context) throws NullPointerException { // Start service

        Logs.add(Logs.Type.V, "context: " + context);
        if (isRunning) {
            Logs.add(Logs.Type.W, "Service already started");
            return false;
        }
        context.startService(new Intent(context, DataService.class));
        return true;
    }

    ////// Broadcast actions
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

    ////// OnConnectivityListener //////////////////////////////////////////////////////////////////
    @Override
    public void onConnection() {





        //if (mToken == null) // Working offline
        //  use mPseudo to get CAM_CodeConf then connect





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

    //
    private String mToken; // Token used to identify the user when requesting remote DB
    private long mTimeLag; // Time lag between remote DB & current OS (in milliseconds)
                           // NB: Will be updated at every remote requests coz OS date & time can changed

    private String mPseudo;
    // Login pseudo (used to get a token if working offline and an Internet connection is established)

    public void connected(String pseudo, String token, long timeLag) {

        Logs.add(Logs.Type.V, "pseudo: " + pseudo + ";token: " + token + ";timeLag: " + timeLag);
        mPseudo = pseudo;
        mToken = token;
        mTimeLag = timeLag;

        // Add notification
        Bundle notifyData = new Bundle();
        notifyData.putInt(Notify.DATA_KEY_ICON, R.drawable.notification);
        notifyData.putString(Notify.DATA_KEY_TITLE, Constants.APP_NAME);
        notifyData.putString(Notify.DATA_KEY_TEXT, getString(R.string.pseudo_connected, pseudo));

        Notify.update(this, Notify.Type.EVENT, null, notifyData);








    }

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
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Logs.add(Logs.Type.V, null);
        isRunning = false;

        Notify.cancel(this); // Remove notification







    }
}
