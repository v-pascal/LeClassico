package com.studio.artaban.leclassico.connection;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;

import java.util.Date;

public class DataService extends Service implements Internet.OnConnectivityListener {

    private static boolean isRunning; // Service running flag
    public static boolean isRunning() {
        return isRunning;
    }

    // Broadcast actions
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
    private Thread mThread; // Thread to manage updates from remote DB to local DB

    public void stop() {

        Logs.add(Logs.Type.V, null);
        isRunning = false;

        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
    }

    private String mPseudo;
    private String mPassword;
    // Login

    public void login(String pseudo, String password) {

        Logs.add(Logs.Type.V, "pseudo: " + pseudo + ";password: " + password);
        mPseudo = pseudo;
        mPassword = password;

        if (Internet.isConnected()) {









        }
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





        /*
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {

                Logs.add(Logs.Type.V, "Thread started");
                while (isRunning) {



                    try { Thread.sleep(10000, 0);
                    } catch (InterruptedException e) {
                        Logs.add(Logs.Type.W, "Sleep interrupted");
                        break;
                    }



                }
                Logs.add(Logs.Type.I, "Thread stopped");
            }

        });
        mThread.start();
        */







        return START_NOT_STICKY;
    }
}
