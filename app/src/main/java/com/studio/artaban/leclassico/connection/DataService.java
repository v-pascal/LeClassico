package com.studio.artaban.leclassico.connection;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;

import com.studio.artaban.leclassico.ConnectionFragment;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Errors;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.codes.WebServices;
import com.studio.artaban.leclassico.helpers.Database;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataService extends Service implements Internet.OnConnectivityListener {

    private static boolean isRunning; // Service running flag
    public static boolean isRunning() {
        return isRunning;
    }
    public static void stop(Context context) { // Stop service
        Logs.add(Logs.Type.V, "context: " + context);

        isRunning = false;
        context.stopService(new Intent(context, DataService.class));
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

    private String mToken; // Token used to identify the user when requesting remote DB
    private long mTimeLag; // Time lag between remote DB & current OS (in milliseconds)
                           // NB: Will be updated at every remote requests coz OS date & time can changed

    private String mPseudo;
    // Login pseudo (used to get a token if working offline and an Internet connection is established)

    public static final byte LOGIN_STEP_ERROR = 0;
    public static final byte LOGIN_STEP_CHECK_INTERNET = 13; // NB: [1;12]: reserved (tables ID)
    public static final byte LOGIN_STEP_OFFLINE_IDENTIFICATION = 14;
    public static final byte LOGIN_STEP_ONLINE_IDENTIFICATION = 15;
    public static final byte LOGIN_STEP_IN_PROGRESS = 16;
    public static final byte LOGIN_STEP_FAILED = 17;
    public static final byte LOGIN_STEP_INTERNET_NEEDED = 18;
    public static final byte LOGIN_STEP_SYNCHRONIZATION = 19;
    public static final byte LOGIN_STEP_SUCCEEDED = 20;
    // Login step codes

    public void login(final ConnectionFragment.ServiceHandler handler, String pseudo, final String password) {

        Logs.add(Logs.Type.V, "handler: " + handler + ";pseudo: " + pseudo);// + ";password: " + password);
        mPseudo = pseudo;

        if (!isRunning)
            throw new IllegalArgumentException("Service stopped");

        new Thread(new Runnable() {
            @Override
            public void run() {

                ////// Login
                Date now = new Date();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                ContentValues data = new ContentValues();
                data.put(WebServices.CONNECTION_DATA_PSEUDO, mPseudo);
                data.put(WebServices.CONNECTION_DATA_PASSWORD, password);
                data.put(WebServices.CONNECTION_DATA_DATETIME, dateFormat.format(now));

                Internet.DownloadResult result = Internet.downloadHttpRequest(Constants.APP_WEBSERVICES +
                        WebServices.URL_CONNECTION, data, new Internet.OnRequestListener() {
                    @Override
                    public boolean onReceiveReply(String response) {

                        Logs.add(Logs.Type.V, "response: " + response);
                        if (handler.isCancelled()) return false;
                        boolean result = false;
                        try {

                            JSONObject reply = new JSONObject(response);
                            if (!reply.has(WebServices.JSON_KEY_ERROR)) { // Check no web service error

                                JSONObject logged = reply.getJSONObject(WebServices.JSON_KEY_LOGGED);
                                mPseudo = logged.getString(WebServices.JSON_KEY_PSEUDO);
                                mToken = logged.getString(WebServices.JSON_KEY_TOKEN);
                                mTimeLag = logged.getLong(WebServices.JSON_KEY_TIME_LAG);

                                Logs.add(Logs.Type.I, "Logged with time lag: " + mTimeLag);
                                result = true; // Continue

                            } else switch ((byte)reply.getInt(WebServices.JSON_KEY_ERROR)) {

                                // Error
                                case Errors.WEBSERVICE_LOGIN_FAILED:
                                    Logs.add(Logs.Type.W, "Login failed");
                                    handler.sendEmptyMessage(LOGIN_STEP_FAILED);
                                    break;

                                case Errors.WEBSERVICE_SERVER_UNAVAILABLE:
                                case Errors.WEBSERVICE_INVALID_LOGIN:
                                case Errors.WEBSERVICE_SYSTEM_DATE:
                                    Logs.add(Logs.Type.E, "Connection error: #" +
                                            reply.getInt(WebServices.JSON_KEY_ERROR));
                                    handler.sendEmptyMessage(LOGIN_STEP_ERROR);
                                    break;
                            }

                        } catch (JSONException e) {
                            Logs.add(Logs.Type.F, "Unexpected connection reply: " + e.getMessage());
                            handler.sendEmptyMessage(LOGIN_STEP_ERROR);
                        }
                        return result; // Manage method reply (below)
                    }
                });
                if (handler.isCancelled()) return;
                switch (result) {
                    case WRONG_URL:
                    case CONNECTION_FAILED:
                    case REPLY_ERROR: {

                        Logs.add(Logs.Type.W, "Connection request failed");
                        if (result != Internet.DownloadResult.REPLY_ERROR)
                            handler.sendEmptyMessage(LOGIN_STEP_ERROR);
                        return;
                    }
                    default: // SUCCEEDED
                        break;
                }

                ////// Synchronization
                Message msg = handler.obtainMessage();
                msg.obj = mPseudo;
                msg.what = LOGIN_STEP_SYNCHRONIZATION;
                handler.sendMessage(msg);

                for (byte tableId = 1; tableId <= Tables.ID_LAST; ++tableId) {
                    if (!Database.synchronize(tableId, getContentResolver(), mToken)) {

                        Logs.add(Logs.Type.E, "Synchronization #" + tableId + " error");
                        handler.sendEmptyMessage(LOGIN_STEP_ERROR);
                        return; // Exit on error
                    }
                    if (handler.isCancelled()) return;
                    handler.sendEmptyMessage(tableId);







                    try {
                        Thread.sleep(1000, 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }








                }

                // Login & Synchronization finished successfully
                handler.sendEmptyMessage(LOGIN_STEP_SUCCEEDED);
            }

        }).start();
    }
    public void logout() {

        Logs.add(Logs.Type.V, null);







        mPseudo = null;
        mToken = null;







    }

    ////// Service /////////////////////////////////////////////////////////////////////////////////
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
