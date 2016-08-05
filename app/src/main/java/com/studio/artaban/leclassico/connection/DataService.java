package com.studio.artaban.leclassico.connection;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

public class DataService extends Service {

    private static boolean isRunning; // Service running flag
    public static boolean isRunning() {
        return isRunning;
    }

    // Broadcast actions
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
    public static final String NEW_NOTIFICATIONS = "com." + Constants.APP_URI_COMPANY + "." +
            Constants.APP_URI + ".action.NEW_NOTIFICATIONS";

    //
    public class DataBinder extends Binder {
        public DataService getService() {
            return DataService.this;
        }
    };
    private final Binder mBinder = new DataBinder();

    //
    private Thread mThread;

    public void stop() {

        Logs.add(Logs.Type.V, null);
        isRunning = false;
        mThread.interrupt();
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
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {

                Logs.add(Logs.Type.V, "Thread started");
                isRunning = true;

                while (isRunning) {









                    try { Thread.sleep(10000, 0);
                    } catch (InterruptedException e) {
                        Logs.add(Logs.Type.E, "Sleep interrupted");
                    }









                }
                Logs.add(Logs.Type.I, "Thread stopped");
            }

        });
        mThread.start();
        return START_NOT_STICKY;
    }
}
