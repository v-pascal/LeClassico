package com.studio.artaban.leclassico.connection;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.studio.artaban.leclassico.data.Constants;

public class DataService extends Service {

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

    //////
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {





        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");



        /*
        IntentFilter filter = new IntentFilter();
        filter.addAction(DataService.NEW_PUBLICATIONS);
        filter.addAction(DataService.NEW_COMMENTS);
        registerReceiver(receiver, filter);
        */



        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
