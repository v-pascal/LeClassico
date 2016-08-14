package com.studio.artaban.leclassico.connection;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

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
    public static final byte CONNECTION_STATE_LOGIN_FAILED = 2; // Constants.WEBSERVICE_ERROR_LOGIN_FAILED
    public static final byte CONNECTION_STATE_EXPIRED = 3; // Constants.WEBSERVICE_ERROR_TOKEN_EXPIRED

    // action.STATUS_SYNCHRONIZATION
    public static final String SYNCHRONIZATION_STATE = "synchronizationState"; // State == processing table Id
    public static final byte SYNCHRONIZATION_STATE_DONE = Tables.ID_LAST + 1;

    public static final String SYNCHRONIZATION_PSEUDO = "synchronizationPseudo"; // User pseudo data (from DB)

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
    //
    public void stop() {

        Logs.add(Logs.Type.V, null);
        logout();

        isRunning = false;
    }

    private String mPseudo;
    // Login pseudo (used to get a token if working offline and an Internet connection is established)

    public boolean login(final String pseudo, final String password) {

        Logs.add(Logs.Type.V, "pseudo: " + pseudo);// + ";password: " + password);
        if (!isRunning) {
            Logs.add(Logs.Type.E, "Login failed: service stopped");
            return false;
        }

        mPseudo = pseudo;
        if ((password == null) || (!Internet.isConnected()))
            return false; // Working offline, or online but connection lost

        new Thread(new Runnable() {
            @Override
            public void run() {









                Date now = new Date();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                ContentValues data = new ContentValues();
                data.put(WebServices.CONNECTION_DATA_PSEUDO, pseudo);
                data.put(WebServices.CONNECTION_DATA_PASSWORD, password);
                data.put(WebServices.CONNECTION_DATA_DATETIME, dateFormat.format(now));

                Internet.DownloadResult result = Internet.downloadHttpRequest(Constants.APP_WEBSERVICES +
                        Constants.WEBSERVICE_CONNECTION, data, new Internet.OnRequestListener() {
                    @Override
                    public void onReceiveReply(String response) {

                        byte result = STATE_ERROR;
                        try {
                            JSONObject reply = new JSONObject(response);
                            if (!reply.has(WebServices.JSON_KEY_ERROR)) { // Check no web service error

                                JSONObject logged = reply.getJSONObject(WebServices.JSON_KEY_LOGGED);
                                mPseudo = logged.getString(WebServices.JSON_KEY_PSEUDO);
                                mToken = logged.getString(WebServices.JSON_KEY_TOKEN);
                                mTimeLag = logged.getLong(WebServices.JSON_KEY_TIME_LAG);

                                Logs.add(Logs.Type.I, "Logged with time lag: " + mTimeLag);
                                result = CONNECTION_STATE_CONNECTED;

                            } else switch ((byte)reply.getInt(WebServices.JSON_KEY_ERROR)) {

                                // Error
                                case Errors.WEBSERVICE_LOGIN_FAILED:
                                    Logs.add(Logs.Type.W, "Login failed");
                                    result = CONNECTION_STATE_LOGIN_FAILED;
                                    break;

                                case Errors.WEBSERVICE_SERVER_UNAVAILABLE:
                                case Errors.WEBSERVICE_INVALID_LOGIN:
                                case Errors.WEBSERVICE_SYSTEM_DATE:
                                    Logs.add(Logs.Type.E, "Connection error: #" +
                                            reply.getInt(WebServices.JSON_KEY_ERROR));
                                    break;
                            }

                        } catch (JSONException e) {
                            Logs.add(Logs.Type.F, "Unexpected connection reply: " + e.getMessage());
                        }
                        Intent intent = new Intent(STATUS_CONNECTION);
                        intent.putExtra(CONNECTION_STATE, result);
                        sendBroadcast(intent); ////// STATUS_CONNECTION
                    }
                });
                if (result != Internet.DownloadResult.SUCCEEDED) {
                    Logs.add(Logs.Type.E, "Connection request error");

                    Intent intent = new Intent(STATUS_CONNECTION);
                    intent.putExtra(CONNECTION_STATE, STATE_ERROR);
                    sendBroadcast(intent); ////// STATUS_CONNECTION
                }











            }

        }).start();
        return true;
    }
    public boolean synchronize() {

        Logs.add(Logs.Type.V, null);
        if (!isRunning) {
            Logs.add(Logs.Type.E, "Synchronization failed: service stopped");
            return false;
        }
        if ((!Internet.isConnected()) || (mToken == null))
            return false;

        new Thread(new Runnable() {
            @Override
            public void run() {

                for (byte tableId = 1; tableId < Tables.ID_LAST; ++tableId) {

                    Intent intent = new Intent(STATUS_SYNCHRONIZATION);
                    if (!Database.synchronize(tableId, getContentResolver())) {

                        Logs.add(Logs.Type.E, "Synchronization #" + tableId + " error");
                        intent.putExtra(SYNCHRONIZATION_STATE, STATE_ERROR);
                        sendBroadcast(intent); ////// STATUS_SYNCHRONIZATION

                        return; // Exit on error
                    }
                    intent.putExtra(SYNCHRONIZATION_STATE, tableId);
                    sendBroadcast(intent); ////// STATUS_SYNCHRONIZATION
                }

                // Synchronization finished successfully
                Intent intent = new Intent(STATUS_SYNCHRONIZATION);
                intent.putExtra(SYNCHRONIZATION_STATE, SYNCHRONIZATION_STATE_DONE);
                intent.putExtra(SYNCHRONIZATION_PSEUDO, mPseudo); // From remote DB
                sendBroadcast(intent); ////// STATUS_SYNCHRONIZATION
            }

        }).start();
        return true;
    }
    public void logout() {

        Logs.add(Logs.Type.V, null);
        if (!isRunning) {
            Logs.add(Logs.Type.E, "Logout failed: service stopped");
            return;
        }







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
