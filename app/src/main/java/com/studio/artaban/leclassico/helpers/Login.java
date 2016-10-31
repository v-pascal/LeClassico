package com.studio.artaban.leclassico.helpers;

import android.content.Intent;
import android.net.Uri;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Errors;
import com.studio.artaban.leclassico.data.codes.WebServices;
import com.studio.artaban.leclassico.tools.SyncValue;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pascal on 31/10/16.
 * Login tools class
 */
public final class Login {

    public static final String EXTRA_DATA_PSEUDO = "pseudo";
    public static final String EXTRA_DATA_PSEUDO_ID = "pseudoId";
    public static final String EXTRA_DATA_NOTIFY_URI = "notifyURI";
    // Extra data keys

    public static Uri notificationURI; // User notifications URI

    public static void copyExtraData(Intent from, Intent to) {
    // Put login extras data from an intent to another one (from previous activity to next activity)

        Logs.add(Logs.Type.V, "from: " + from + ";to: " + to);
        to.putExtra(EXTRA_DATA_PSEUDO, from.getStringExtra(EXTRA_DATA_PSEUDO));
        to.putExtra(EXTRA_DATA_PSEUDO_ID, from.getIntExtra(EXTRA_DATA_PSEUDO_ID, Constants.NO_DATA));
        to.putExtra(EXTRA_DATA_NOTIFY_URI, notificationURI); // Always add notification URI (new notification)
    }

    public static class Reply {

        public String pseudo; // Pseudo of the login
        public int pseudoId = Constants.NO_DATA; // Member ID (in local DB)
        public final SyncValue<String> token = new SyncValue<>(null); // Login token
        public long timeLag; // Time lag between remote & local DB
    }
    public static boolean receive(String response, Reply loginRes) { // Receive login reply

        Logs.add(Logs.Type.V, "response: " + response + ";loginRes: " + loginRes);
        boolean result = false; ////// Reply error
        try {

            JSONObject reply = new JSONObject(response);
            if (!reply.has(WebServices.JSON_KEY_ERROR)) { // Check no web service error

                // Login succeeded
                JSONObject logged = reply.getJSONObject(WebServices.JSON_KEY_LOGGED);
                loginRes.pseudo = logged.getString(WebServices.JSON_KEY_PSEUDO);
                loginRes.token.set(logged.getString(WebServices.JSON_KEY_TOKEN));
                loginRes.timeLag = logged.getLong(WebServices.JSON_KEY_TIME_LAG);

                Logs.add(Logs.Type.I, "Logged with time lag: " + loginRes.timeLag);
                result = true; ////// Reply succeeded

            } else switch ((byte)reply.getInt(WebServices.JSON_KEY_ERROR)) {

                case Errors.WEBSERVICE_TOKEN_EXPIRED:
                case Errors.WEBSERVICE_LOGIN_FAILED: {

                    Logs.add(Logs.Type.W, "Invalid login/token");
                    loginRes.token.set(null); // Login failed or token expired (invalid)
                    result = true; ////// Reply succeeded
                    break;
                }
                // Error
                case Errors.WEBSERVICE_SERVER_UNAVAILABLE:
                case Errors.WEBSERVICE_INVALID_LOGIN:
                case Errors.WEBSERVICE_SYSTEM_DATE: {

                    Logs.add(Logs.Type.E, "Connection error: #" +
                            reply.getInt(WebServices.JSON_KEY_ERROR));
                    break;
                }
            }

        } catch (JSONException e) {
            Logs.add(Logs.Type.F, "Unexpected connection reply: " + e.getMessage());
        }
        return result;
    }
}
