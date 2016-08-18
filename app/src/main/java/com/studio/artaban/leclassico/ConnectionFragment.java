package com.studio.artaban.leclassico;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.studio.artaban.leclassico.connection.DataService;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.tools.WaitUiThread;

/**
 * Created by pascal on 08/08/16.
 * Fragment to display a progress dialog during connection & synchronization
 */
public class ConnectionFragment extends Fragment {

    public static final String TAG = "connectionProgress";

    //
    private OnProgressListener mListener;
    public interface OnProgressListener { //////////////////////////////////////////////////////////

        void onPreExecute();
        void onProgressUpdate(byte step);
        void onPostExecute(boolean result, boolean online, String pseudo);

        boolean onLoginRequested(ServiceHandler handler, String pseudo, String password);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private String mPseudo;
    private String mPassword;
    // Login data

    public void display(final Activity activity, String pseudo, String password) {

        Logs.add(Logs.Type.V, "activity: " + activity + ";pseudo: " + pseudo);// + ";password: " + password);
        mPseudo = pseudo;
        mPassword = password;

        mThread = new Thread(mConnectionRunnable);
        mThread.start();
    }
    public void cancel() {

        Logs.add(Logs.Type.V, null);
        if (mThread != null) {
            if (mConnectionRunnable.getServiceHandler() != null)
                mConnectionRunnable.getServiceHandler().cancel();
            mThread.interrupt();
            mThread = null;
        }
    }
    public boolean isDisplayed() {
        return ((mThread != null) && (!mThread.isInterrupted()));
    }

    //////
    private Thread mThread; // Connection thread

    private final ConnectionRunnable mConnectionRunnable = new ConnectionRunnable();
    private class ConnectionRunnable implements Runnable {

        private static final long INFORM_USER_DELAY = 700; // Delay of displaying step

        //
        private ServiceHandler mHandler; // Data service exchange handler
        public ServiceHandler getServiceHandler() {
            return mHandler;
        }
        private void cancel() {
        // Cancel thread from current runnable implementation (in case where activity is detached)
        // NB: Should not happen! Fragment is a retain instance

            Logs.add(Logs.Type.V, null);
            if (mHandler != null)
                mHandler.cancel();
            Thread.currentThread().interrupt();
        }

        private void publishProgress(final Byte step) {

            Logs.add(Logs.Type.V, "step: " + step);
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // Check online login service requested
                        if (step == DataService.LOGIN_STEP_IN_PROGRESS) {

                            if (mHandler.isCancelled()) return;
                            if (!mListener.onLoginRequested(mHandler, mPseudo, mPassword))
                                mHandler.sendEmptyMessage(Constants.NO_DATA);
                            return;
                        }
                        mListener.onProgressUpdate(step);

                        switch (step) {
                            case DataService.LOGIN_STEP_CHECK_INTERNET:
                            case DataService.LOGIN_STEP_OFFLINE_IDENTIFICATION:
                            case DataService.LOGIN_STEP_ONLINE_IDENTIFICATION: {

                                synchronized (step) { // Notify progress publication done
                                    step.notify();
                                }
                                break;
                            }
                        }
                    }
                });

            } catch (NullPointerException e) {
                Logs.add(Logs.Type.E, "No more attached activity");
                cancel();
            }
        }
        public void publishProgress(byte step, String pseudo) {
        // Allow to publish progression from the service handler

            Logs.add(Logs.Type.V, "step: " + step + ";pseudo: " + pseudo);
            if (step == DataService.LOGIN_STEP_SYNCHRONIZATION)
                mPseudo = pseudo; // Pseudo as defined in the remote DB

            publishProgress(step);
        }
        private void publishWaitProgress(byte step) {
        // Publish progression and wait its display B4 marking a delay to let's the user to be informed

            Logs.add(Logs.Type.V, "step: " + step);
            Byte stepToPublish = step;
            synchronized (stepToPublish) {

                publishProgress(stepToPublish);

                // Wait progress published
                try { stepToPublish.wait();
                } catch (InterruptedException e) {
                    Logs.add(Logs.Type.E, "Wait display interrupted");
                }
            }
            try { Thread.sleep(INFORM_USER_DELAY, 0); // Sleep to inform user of the step
            } catch (InterruptedException e) {
                Logs.add(Logs.Type.W, "User delay interrupted");
            }
        }

        //////
        @Override
        public void run() {

            Logs.add(Logs.Type.V, null);
            onPreExecute();
            try {

                // Check Internet connection
                boolean online = Internet.isOnline(getActivity());
                if (Thread.currentThread().isInterrupted()) return;
                if (!online) {

                    // No Internet connection so check existing DB to work offline
                    ContentResolver cr = getActivity().getContentResolver();
                    Cursor result = cr.query(Uri.parse(DataProvider.CONTENT_URI + CamaradesTable.TABLE_NAME),
                            new String[]{"count(*)"}, null, null, null);
                    result.moveToFirst();
                    int membersCount = result.getInt(0);
                    result.close();

                    if (Thread.currentThread().isInterrupted()) return;
                    if (membersCount > 0) { // Found existing DB (try to work offline)

                        publishWaitProgress(DataService.LOGIN_STEP_OFFLINE_IDENTIFICATION);
                        if (Thread.currentThread().isInterrupted()) return;
                        String pseudo = null;

                        // Offline identification
                        result = cr.query(Uri.parse(DataProvider.CONTENT_URI + CamaradesTable.TABLE_NAME),
                                new String[]{CamaradesTable.COLUMN_PSEUDO},
                                "UPPER(" + CamaradesTable.COLUMN_PSEUDO + ")='" + mPseudo.toUpperCase() +
                                        "' AND " + CamaradesTable.COLUMN_CODE_CONF + "='" + mPassword + "'",
                                null, null);
                        if (result.getCount() > 0) {
                            result.moveToFirst();
                            pseudo = result.getString(0);
                        }
                        result.close();

                        if (Thread.currentThread().isInterrupted()) return;
                        if (pseudo != null) { // Login succeeded

                            mPseudo = pseudo; // Pseudo as defined in the DB
                            onPostExecute(true, false, mPseudo); // Succeeded (offline)
                            return;

                        } else // Login failed
                            publishProgress(DataService.LOGIN_STEP_FAILED);

                    } else {
                        publishWaitProgress(DataService.LOGIN_STEP_CHECK_INTERNET);
                        publishProgress(DataService.LOGIN_STEP_INTERNET_NEEDED);
                    }
                    onPostExecute(false, false, null);
                    return;
                }

                // Online identification
                publishWaitProgress(DataService.LOGIN_STEP_ONLINE_IDENTIFICATION);
                if (Thread.currentThread().isInterrupted()) return;

                Looper.prepare();
                mHandler = new ServiceHandler(this);
                publishProgress(DataService.LOGIN_STEP_IN_PROGRESS);
                Looper.loop();

                //
                if (Thread.currentThread().isInterrupted()) return;
                if (mHandler.getResult() != DataService.LOGIN_STEP_SUCCEEDED) {

                    publishProgress(mHandler.getResult());
                    onPostExecute(false, true, null);
                    return;
                }
                onPostExecute(true, true, mPseudo); // Succeeded (online)

            } catch (NullPointerException e) {
                Logs.add(Logs.Type.E, "No more attached activity");
                cancel();
            }
        }

        //////
        private void onPreExecute() {
            try {
                WaitUiThread.run(getActivity(), new WaitUiThread.TaskToRun() {
                    @Override
                    public void proceed(Bundle res) {
                        mListener.onPreExecute();
                    }
                });

            } catch (NullPointerException e) {
                Logs.add(Logs.Type.E, "No more attached activity");
                cancel();
            }
        }
        private void onPostExecute(final boolean result, final boolean online, final String pseudo) {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onPostExecute(result, online, pseudo);
                    }
                });

            } catch (NullPointerException e) {
                Logs.add(Logs.Type.E, "No more attached activity");
                cancel();
            }
        }
    };

    public static class ServiceHandler extends Handler {
    // Handler used to manage exchange between the data service & the asynchronous connection task

        private byte mLoginResult; // Login & synchronization result
        public byte getResult() {
            return mLoginResult;
        }
        private boolean mCancelled; // Cancel request flag
        public boolean isCancelled() { // Used by the data service to known if needed to cancel
            return mCancelled;
        }

        public void cancel() { // Cancel thread requested
            Logs.add(Logs.Type.V, null);
            mCancelled = true;
        }

        //
        private ConnectionRunnable mRunnable;
        public ServiceHandler(ConnectionRunnable runnable) {
            super();
            mRunnable = runnable;
        }

        //////
        @Override
        public void handleMessage(Message msg) {

            Logs.add(Logs.Type.V, "msg: " + msg);
            if (mCancelled) {

                mLoginResult = DataService.LOGIN_STEP_ERROR;
                getLooper().quit();
                return; // Prevents any message process if cancelled
            }
            switch (msg.what) {
                case Constants.NO_DATA: { // Unexpected error (service not bound)

                    Logs.add(Logs.Type.E, "Service not bound");
                    mLoginResult = (byte)Constants.NO_DATA;
                    getLooper().quit();
                    break;
                }
                case DataService.LOGIN_STEP_SYNCHRONIZATION: {

                    mRunnable.publishProgress(DataService.LOGIN_STEP_SYNCHRONIZATION, (String)msg.obj);
                    break;
                }
                case DataService.LOGIN_STEP_ERROR:
                case DataService.LOGIN_STEP_FAILED:
                case DataService.LOGIN_STEP_SUCCEEDED: {

                    mLoginResult = (byte)msg.what;
                    getLooper().quit();
                    break;
                }
                default: { // Tables DB synchronization

                    mRunnable.publishProgress((byte)msg.what, null);
                    break;
                }
            }
        }
    };

    ////// Fragment ////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logs.add(Logs.Type.V, null);

        if (context instanceof OnProgressListener)
            mListener = (OnProgressListener)context;
        else
            throw new RuntimeException(context.toString() + " must implement 'OnInteractionListener'");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
        setRetainInstance(true); // Retain fragment instance
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logs.add(Logs.Type.V, null);
        mListener = null;
    }
}
