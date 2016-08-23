package com.studio.artaban.leclassico.activities.introduction;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.components.RevealFragment;
import com.studio.artaban.leclassico.connection.DataService;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.codes.Errors;
import com.studio.artaban.leclassico.data.codes.Preferences;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.codes.WebServices;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.helpers.Database;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.tools.SyncValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pascal on 20/08/16.
 * Progress fragment class
 */
public class ConnectFragment extends RevealFragment {

    public static final String TAG = "progress";

    public static final String ARGS_KEY_PSEUDO = "pseudo";
    public static final String ARGS_KEY_PASSWORD = "password";
    // Argument keys

    public static final byte STEP_ERROR = 0;
    private static final byte STEP_CHECK_INTERNET = 13;  // NB: [1;12]: reserved (tables ID)
    private static final byte STEP_LOGIN_OFFLINE = 14;
    private static final byte STEP_LOGIN_ONLINE = 15;
    public static final byte STEP_LOGIN_FAILED = 17;
    public static final byte STEP_INTERNET_NEEDED = 18;
    private static final byte STEP_SYNCHRONIZATION_PROGRESS = 21;
    private static final byte STEP_SYNCHRONIZATION_SUCCEEDED = 22;
    private static final byte STEP_SERVICE_BOUND = 23;
    // Step codes

    public interface OnConnectListener { ///////////////////////////////////////////////////////////

        void onError(byte error);
        boolean onServiceRequested();
        void onConnected(String pseudo, String token, long timeLag);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private byte mStep; // Connection step
    private int mProgress; // Synchronization progress status
    private boolean mOnline; // Online connection flag

    private void reset(boolean indeterminate) { // Reset progress UI components

        Logs.add(Logs.Type.V, "indeterminate: " + indeterminate);
        ((ImageView)mRootView.findViewById(R.id.checked_internet))
                .setImageDrawable(getResources().getDrawable(R.drawable.forward_purple));

        ImageView pin = (ImageView)mRootView.findViewById(R.id.checked_identification);
        pin.setImageDrawable(getResources().getDrawable(R.drawable.forward_purple));
        pin.setVisibility(View.INVISIBLE);
        pin = (ImageView)mRootView.findViewById(R.id.checked_synchro);
        pin.setImageDrawable(getResources().getDrawable(R.drawable.forward_purple));
        pin.setVisibility(View.INVISIBLE);
        pin = (ImageView)mRootView.findViewById(R.id.checked_notification);
        pin.setImageDrawable(getResources().getDrawable(R.drawable.forward_purple));
        pin.setVisibility(View.INVISIBLE);
        if (indeterminate) {

            ProgressBar progressBar = (ProgressBar)mRootView.findViewById(R.id.progress_view);
            progressBar.setIndeterminate(true);
            progressBar.invalidate();
        }
        ((TextView)mRootView.findViewById(R.id.progress_percentage)).setText(null);
    }
    public void update(byte step, int progress) { // Update UI connection progress

        Logs.add(Logs.Type.V, "step: " + step + ";progress: " + progress);
        switch (step) {
            case STEP_CHECK_INTERNET: {
                mProgress = 0;
                mStep = step;
                reset(true);
                break;
            }
            case STEP_LOGIN_ONLINE: {
                mProgress = 0;
                mStep = step;
                mOnline = true;
                reset(true);

                ((ImageView)mRootView.findViewById(R.id.checked_internet))
                        .setImageDrawable(getResources().getDrawable(R.drawable.checked_orange));
                mRootView.findViewById(R.id.checked_identification).setVisibility(View.VISIBLE);
                break;
            }
            case STEP_LOGIN_OFFLINE: {
                mProgress = 0;
                mStep = step;
                mOnline = false;
                reset(true);

                ((ImageView)mRootView.findViewById(R.id.checked_internet))
                        .setImageDrawable(getResources().getDrawable(R.drawable.cancel_orange));
                mRootView.findViewById(R.id.checked_identification).setVisibility(View.VISIBLE);
                break;
            }
            case STEP_SYNCHRONIZATION_PROGRESS: {
                mProgress = progress;
                mStep = step;
                reset(false);

                ((ImageView)mRootView.findViewById(R.id.checked_internet))
                        .setImageDrawable(getResources().getDrawable((mOnline) ?
                                R.drawable.checked_orange : R.drawable.cancel_orange));
                ImageView pin = (ImageView)mRootView.findViewById(R.id.checked_identification);
                pin.setImageDrawable(getResources().getDrawable(R.drawable.checked_orange));
                pin.findViewById(R.id.checked_identification).setVisibility(View.VISIBLE);
                mRootView.findViewById(R.id.checked_synchro).setVisibility(View.VISIBLE);

                ProgressBar progressBar = (ProgressBar)mRootView.findViewById(R.id.progress_view);
                progressBar.setIndeterminate(false);
                progressBar.invalidate();
                progressBar.setProgress(progress);
                ((TextView)mRootView.findViewById(R.id.progress_percentage))
                        .setText(String.format("%d%%", (byte) (progress * 100f / Tables.ID_LAST)));
                break;
            }
            case STEP_SYNCHRONIZATION_SUCCEEDED: {
                mProgress = 0;
                mStep = step;
                reset(true);

                ((ImageView)mRootView.findViewById(R.id.checked_internet))
                        .setImageDrawable(getResources().getDrawable((mOnline) ?
                                R.drawable.checked_orange : R.drawable.cancel_orange));
                ImageView pin = (ImageView)mRootView.findViewById(R.id.checked_identification);
                pin.setImageDrawable(getResources().getDrawable(R.drawable.checked_orange));
                pin.findViewById(R.id.checked_identification).setVisibility(View.VISIBLE);
                pin = (ImageView)mRootView.findViewById(R.id.checked_synchro);
                pin.setImageDrawable(getResources().getDrawable((mOnline) ?
                                R.drawable.checked_orange : R.drawable.cancel_orange));
                pin.setVisibility(View.VISIBLE);
                mRootView.findViewById(R.id.checked_notification).setVisibility(View.VISIBLE);
                break;
            }
            default: // Do not save data
                return;
        }
        // Save persistent data coz unable to use save instance state behavior (not working if in pause)
        SharedPreferences prefs = getContext().getSharedPreferences(Constants.APP_PREFERENCE, 0);
        prefs.edit().putInt(Preferences.CONNECTION_STEP, mStep)
                    .putInt(Preferences.CONNECTION_PROGRESS, mProgress)
                    .putBoolean(Preferences.CONNECTION_ONLINE, mOnline)
                    .apply();
    }

    public static final SyncValue<Boolean> stopped = new SyncValue<>(false);
    // NB: Add an own cancel connection task management using this declaration coz it seems that when
    //     the activity is re-created (when configuration change) and then a destroy operation on this
    //     fragment is requested, the 'AsyncTask.cancel(true)' method call has no effect!

    //////
    private OnConnectListener mListener; // Activity interface callback
    private ConnectTask mTask; // Connection asynchronous task

    private class ConnectTask extends AsyncTask<String, Byte, Boolean> {

        private static final int DELAY_WAIT_PROGRESS = 150; // Max delay to wait progress published (in ms)
        private static final int DELAY_INFORM_USER = 700; // Delay of displaying step (in ms)
        private static final int DELAY_WAIT_BOUND = 700; // Delay to wait progress published (in ms)
        // Delays

        private boolean isStopped() { // Own cancel connection task management
            synchronized (stopped) {

                if (stopped.get()) {
                    Logs.add(Logs.Type.I, "Connection task cancelled");
                    return true;
                }
                return false;
            }
        }
        private boolean publishWaitProgress(byte step, boolean delay) {
        // Publish progression and wait its display B4 marking a delay to let's the user to be informed

            Logs.add(Logs.Type.V, "step: " + step + ";delay: " + delay);
            if (isStopped()) return false;

            Byte stepToPublish = step;
            synchronized (stepToPublish) {

                publishProgress(stepToPublish);

                // Wait progress published
                try {
                    stepToPublish.wait(DELAY_WAIT_PROGRESS);
                } catch (InterruptedException e) {
                    Logs.add(Logs.Type.E, "Wait display interrupted");
                    return false;
                }
            }
            if (delay)
                return true; // No delay requested

            try {
                Thread.sleep(DELAY_INFORM_USER, 0); // Sleep to inform user of the step
            } catch (InterruptedException e) {
                Logs.add(Logs.Type.W, "User delay interrupted");
                return false;
            }
            return !isStopped();
        }

        //
        private String mPseudo; // User pseudo used to connect with
        private String mToken; // Token provided by the web service to identify user
        private long mTimeLag; // Time laps between remote server & current OS (in sec)

        private volatile boolean mServiceBound; // Service bound flag

        //////
        @Override
        protected Boolean doInBackground(String... params) {

            Logs.add(Logs.Type.V, "params[0]: " + params[0] + ";params[1]: " + params[1]);
            mPseudo = params[0];
            String password = params[1];

            try {
                ////// Check Internet connection
                if (!publishWaitProgress(STEP_CHECK_INTERNET, true)) return Boolean.FALSE;
                boolean online = Internet.isOnline(getWaitActivity());
                if (isStopped()) return Boolean.FALSE;

                ////// Identification
                if (!online) {

                    // No Internet connection so check existing DB to work offline
                    ContentResolver cr = getWaitActivity().getContentResolver();
                    Cursor result = cr.query(Uri.parse(DataProvider.CONTENT_URI + CamaradesTable.TABLE_NAME),
                            new String[]{"count(*)"}, null, null, null);
                    result.moveToFirst();
                    int membersCount = result.getInt(0);
                    result.close();

                    if (isStopped()) return Boolean.FALSE;
                    if (membersCount > 0) { // Found existing DB (try to work offline)

                        if (!publishWaitProgress(STEP_LOGIN_OFFLINE, true)) return Boolean.FALSE;
                        String pseudo = null;

                        // Offline identification
                        result = cr.query(Uri.parse(DataProvider.CONTENT_URI + CamaradesTable.TABLE_NAME),
                                new String[]{CamaradesTable.COLUMN_PSEUDO},
                                "UPPER(" + CamaradesTable.COLUMN_PSEUDO + ")=" +
                                        DatabaseUtils.sqlEscapeString(mPseudo.toUpperCase()) +
                                        " AND " + CamaradesTable.COLUMN_CODE_CONF + "=" +
                                        DatabaseUtils.sqlEscapeString(password),
                                null, null);
                        if (result.getCount() > 0) {
                            result.moveToFirst();
                            pseudo = result.getString(0);
                        }
                        result.close();

                        if (isStopped()) return Boolean.FALSE;
                        if (pseudo != null) ////// Login succeeded
                            mPseudo = pseudo; // Pseudo as defined in the DB

                        else { // Login failed
                            publishProgress(STEP_LOGIN_FAILED);
                            return Boolean.FALSE;
                        }

                    } else { // No local  DB to work offline
                        if (!publishWaitProgress(STEP_CHECK_INTERNET, true)) return Boolean.FALSE;
                        publishProgress(STEP_INTERNET_NEEDED);
                        return Boolean.FALSE;
                    }

                } else {

                    // Online identification
                    if (!publishWaitProgress(STEP_LOGIN_ONLINE, true)) return Boolean.FALSE;

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
                            if (isStopped()) return Boolean.FALSE;
                            boolean result = false; ////// Reply error
                            try {

                                JSONObject reply = new JSONObject(response);
                                if (!reply.has(WebServices.JSON_KEY_ERROR)) { // Check no web service error

                                    // Login succeeded
                                    JSONObject logged = reply.getJSONObject(WebServices.JSON_KEY_LOGGED);
                                    mPseudo = logged.getString(WebServices.JSON_KEY_PSEUDO);
                                    mToken = logged.getString(WebServices.JSON_KEY_TOKEN);
                                    mTimeLag = logged.getLong(WebServices.JSON_KEY_TIME_LAG);

                                    Logs.add(Logs.Type.I, "Logged with time lag: " + mTimeLag);
                                    result = true; ////// Reply succeeded

                                } else switch ((byte)reply.getInt(WebServices.JSON_KEY_ERROR)) {

                                    case Errors.WEBSERVICE_LOGIN_FAILED: {

                                        Logs.add(Logs.Type.W, "Login failed");
                                        mToken = null; // Login failed
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
                            return result; // Manage method reply (below)
                        }
                    });
                    if (isStopped()) return Boolean.FALSE;
                    switch (result) {
                        case WRONG_URL:
                        case CONNECTION_FAILED:
                        case REPLY_ERROR: {

                            Logs.add(Logs.Type.W, "Connection request failed");
                            publishProgress(STEP_ERROR);
                            return Boolean.FALSE;
                        }
                        default: { ////// Reply succeeded

                            // Check login result
                            if (mToken == null) { // Login failed
                                publishProgress(STEP_LOGIN_FAILED);
                                return Boolean.FALSE;
                            }
                            //else ////// Login succeeded
                            break;
                        }
                    }

                    ////// Synchronization
                    for (byte tableId = 1; tableId <= Tables.ID_LAST; ++tableId) {
                        if (isStopped()) return Boolean.FALSE;
                        publishProgress(tableId);

                        if (!Database.synchronize(tableId, getWaitActivity().getContentResolver(), mToken)) {

                            Logs.add(Logs.Type.E, "Synchronization #" + tableId + " error");
                            publishProgress(STEP_ERROR);
                            return Boolean.FALSE;
                        }
                    }
                }

                ////// Start service
                if (!publishWaitProgress(STEP_SYNCHRONIZATION_SUCCEEDED, true)) return Boolean.FALSE;
                DataService.start(getWaitActivity());

                ////// Wait activity service bound
                mServiceBound = false;
                while (!mServiceBound) {

                    if (!publishWaitProgress(STEP_SERVICE_BOUND, false)) return Boolean.FALSE;

                    // Check service request result
                    if (!mServiceBound) {
                        try {
                            Thread.sleep(DELAY_WAIT_BOUND, 0);
                        } catch (InterruptedException e) {

                            Logs.add(Logs.Type.W, "Wait service bound interrupted");
                            return Boolean.FALSE;
                        }
                    }
                }
                return Boolean.TRUE; ////// Succeeded

            } catch (Exception e) {
                Logs.add(Logs.Type.F, "Unexpected error: " + e.getMessage());












                return Boolean.FALSE;
            }
        }

        //////
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Logs.add(Logs.Type.V, null);
            synchronized (stopped) { // Own cancel connection task management
                stopped.set(false);
            }
        }

        @Override
        protected void onProgressUpdate(Byte... values) {

            Logs.add(Logs.Type.V, "values[0]: " + values[0]);
            switch (values[0]) {

                ////// Service requested
                case STEP_SERVICE_BOUND: {
                    if (mListener != null)
                        mServiceBound = mListener.onServiceRequested();
                    break;
                }
                ////// Error
                case STEP_ERROR:
                case STEP_LOGIN_FAILED:
                case STEP_INTERNET_NEEDED: {
                    if (mListener != null)
                        mListener.onError(values[0]);
                    break;
                }
                ////// Step progress
                case STEP_LOGIN_ONLINE:
                case STEP_SYNCHRONIZATION_SUCCEEDED:
                case STEP_LOGIN_OFFLINE:
                case STEP_CHECK_INTERNET: {

                    update(values[0], 0);
                    synchronized (values[0]) { // Notify progress publication done
                        values[0].notify();
                    }
                    break;
                }
                default: {
                    update(STEP_SYNCHRONIZATION_PROGRESS, values[0]);
                    break;
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            Logs.add(Logs.Type.V, "aBoolean: " + aBoolean);
            if ((aBoolean) && (mListener != null))
                mListener.onConnected(mPseudo, mToken, mTimeLag);
        }
    }

    private static final int DELAY_WAIT_ACTIVITY = 200; // Max delay to wait activity attached (in ms)
    private final Boolean mWaitActivity = Boolean.FALSE; // Used to wait activity attached

    private FragmentActivity getWaitActivity() { // Wait activity attached

    // NB: When activity is re-created (when configuration change) the fragment is detached and
    //     re-attached. During this laps of time the activity is not available and it is the reason
    //     why this method exists.

        Logs.add(Logs.Type.V, null);
        if (getActivity() == null) {

            Logs.add(Logs.Type.W, "Activity not attached yet");
            synchronized (mWaitActivity) {
                try {
                    mWaitActivity.wait(DELAY_WAIT_ACTIVITY);
                } catch (InterruptedException e) {
                    Logs.add(Logs.Type.W, "Wait activity attached interrupted");
                }
            }
        }
        return getActivity();
    }

    //////
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Logs.add(Logs.Type.V, null);
        if (context instanceof OnConnectListener)
            mListener = (OnConnectListener)context;
        else
            throw new RuntimeException(context.toString() + " must implement 'OnInteractionListener'");

        synchronized (mWaitActivity) { // Manage waiting activity attached
            mWaitActivity.notify();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
        setRetainInstance(true); // Needed to keep activity attached (even if it recreated)
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);

        mRootView = inflater.inflate(R.layout.layout_progress, container, false);
        ProgressBar progressBar = (ProgressBar)mRootView.findViewById(R.id.progress_view);
        progressBar.setMax(Tables.ID_LAST);
        progressBar.incrementProgressBy(0);

        // Start connection (if not already done)
        if (mTask == null) {

            Logs.add(Logs.Type.I, "Start connection task");
            mTask = new ConnectTask();
            mTask.execute(
                    getArguments().getString(ARGS_KEY_PSEUDO),
                    getArguments().getString(ARGS_KEY_PASSWORD));

        } else {

            // Restore data (persistent)
            SharedPreferences settings = getContext().getSharedPreferences(Constants.APP_PREFERENCE, 0);
            mStep = (byte)settings.getInt(Preferences.CONNECTION_STEP, 0);
            mProgress = settings.getInt(Preferences.CONNECTION_PROGRESS, 0);
            mOnline = settings.getBoolean(Preferences.CONNECTION_ONLINE, false);

            update(mStep, mProgress);
        }
        return mRootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Logs.add(Logs.Type.V, null);
        synchronized (stopped) { // Own cancel connection task management
            stopped.set(true);
        }
        mTask.cancel(true);
        mTask = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logs.add(Logs.Type.V, null);
        mListener = null;
    }
}
