package com.studio.artaban.leclassico.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.studio.artaban.leclassico.connection.DataService;
import com.studio.artaban.leclassico.connection.ServiceHandler;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.tools.WaitUiThread;

/**
 * Created by pascal on 08/08/16.
 * Fragment to display a progression on UI during connection & synchronization
 * NB: The difficulty is to request the data service from this thread in order to log & synchronize
 *     data, using the activity service bound (which is not always bound due to the orientation
 *     change and pause operation)
 */
public class ConnectionTask extends Fragment {

    public static final String TAG = "connectionTask";

    //
    private OnProgressListener mListener;
    public interface OnProgressListener { //////////////////////////////////////////////////////////

        void onPreExecute();
        void onProgressUpdate(byte step);
        void onPostExecute(boolean result, boolean online, String pseudo);

        boolean onLoginRequested(ServiceHandler handler, String pseudo, String password);
        boolean onSynchronizationRequested(ServiceHandler handler);
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
    private class ConnectionRunnable implements Runnable, ServiceHandler.OnPublishListener {

        private static final int DELAY_INFORM_USER = 700; // Delay of displaying step (in ms)
        private static final int DELAY_WAIT_PROGRESS = 150; // Max delay to wait progress published (in ms)
        private static final int DELAY_WAIT_SERVICE = 300; // Max delay to wait service bound (in ms)
        // Delays

        private ServiceHandler mHandler; // Data service exchange handler
        public ServiceHandler getServiceHandler() {
            return mHandler;
        }

        private void cancel() {
        // Cancel thread from current runnable implementation (in case where activity is detached)

            Logs.add(Logs.Type.V, null);
            if (mHandler != null)
                mHandler.cancel();

            Thread.currentThread().interrupt();
        }

        //
        private static final String DATA_KEY_REQUEST_RESULT = "serviceResult";

        private static final byte REQUEST_RESULT_CANCELLED = 0;
        private static final byte REQUEST_RESULT_NOT_BOUND = 1;
        private static final byte REQUEST_RESULT_SUCCEEDED = 2;

        private Bundle requestStepService(final byte requestStep) {

            Logs.add(Logs.Type.V, "requestStep: " + requestStep);
            return WaitUiThread.run(getWaitActivity(), new WaitUiThread.TaskToRun() {
                @Override
                public void proceed(Bundle result) {

                    Logs.add(Logs.Type.V, "result: " + result);
                    result.putByte(DATA_KEY_REQUEST_RESULT, REQUEST_RESULT_CANCELLED);

                    if (mHandler.isCancelled()) return;

                    boolean requestDone;
                    if (requestStep == DataService.LOGIN_STEP_IN_PROGRESS) // Login
                        requestDone = mListener.onLoginRequested(mHandler, mPseudo, mPassword);
                    else // Synchronization
                        requestDone = mListener.onSynchronizationRequested(mHandler);

                    //////
                    result.putByte(DATA_KEY_REQUEST_RESULT,
                            (!requestDone)? REQUEST_RESULT_NOT_BOUND : REQUEST_RESULT_SUCCEEDED);
                }

            });
        }

        private void publishProgress(final Byte step) {

            Logs.add(Logs.Type.V, "step: " + step);
            boolean requestStep = false;

            // Check if service operation is requested
            if ((step == DataService.LOGIN_STEP_IN_PROGRESS) || (step == DataService.LOGIN_STEP_SUCCEEDED))
                requestStep = true;

            try {
                if (requestStep) {
                    Bundle requestRes = requestStepService(step);
                    if (requestRes.getByte(DATA_KEY_REQUEST_RESULT) == REQUEST_RESULT_NOT_BOUND) {

                        Logs.add(Logs.Type.W, "Service not bound yet");
                        try { // Wait service bound
                            Thread.sleep(DELAY_WAIT_SERVICE, 0);
                        } catch (InterruptedException e) {
                            Logs.add(Logs.Type.W, "Wait service delay interrupted");
                        }

                        requestRes = requestStepService(step);
                        if (requestRes.getByte(DATA_KEY_REQUEST_RESULT) == REQUEST_RESULT_NOT_BOUND)
                            mHandler.sendEmptyMessage(Constants.NO_DATA);
                    }

                } else { // Not a service request step
                    getWaitActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Logs.add(Logs.Type.V, null);
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
                }

            } catch (NullPointerException e) {
                Logs.add(Logs.Type.E, "Attached activity missing (pub)");

                // Check if needed to cancel the process due to this issue
                if (requestStep)
                    cancel();
            }
        }
        private void publishWaitProgress(byte step) {
        // Publish progression and wait its display B4 marking a delay to let's the user to be informed

            Logs.add(Logs.Type.V, "step: " + step);
            Byte stepToPublish = step;
            synchronized (stepToPublish) {

                publishProgress(stepToPublish);

                // Wait progress published
                try { stepToPublish.wait(DELAY_WAIT_PROGRESS);
                } catch (InterruptedException e) {
                    Logs.add(Logs.Type.E, "Wait display interrupted");
                }
            }
            try { Thread.sleep(DELAY_INFORM_USER, 0); // Sleep to inform user of the step
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
                boolean online = Internet.isOnline(getWaitActivity());
                if (Thread.currentThread().isInterrupted()) return;
                if (!online) {

                    // No Internet connection so check existing DB to work offline
                    ContentResolver cr = getWaitActivity().getContentResolver();
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
                if (mHandler.getResult() != DataService.SYNCHRONIZATION_STEP_SUCCEEDED) {

                    publishProgress(mHandler.getResult());
                    onPostExecute(false, true, null);
                    return;
                }
                onPostExecute(true, true, mPseudo); // Succeeded (online)

            } catch (NullPointerException e) {
                Logs.add(Logs.Type.E, "Attached activity missing (run)");
                cancel();
            }
        }

        //////
        private void onPreExecute() {
            try {
                WaitUiThread.run(getWaitActivity(), new WaitUiThread.TaskToRun() {
                    @Override
                    public void proceed(Bundle res) {
                        mListener.onPreExecute();
                    }
                });

            } catch (NullPointerException e) {
                Logs.add(Logs.Type.E, "Attached activity missing (pre)");
                //cancel(); // Not needed coz will always check this in the IntroActivity class
            }
        }
        private void onPostExecute(final boolean result, final boolean online, final String pseudo) {
            try {
                getWaitActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onPostExecute(result, online, pseudo);
                    }
                });

            } catch (NullPointerException e) {
                Logs.add(Logs.Type.E, "Attached activity missing (post)");
                cancel();
            }
        }

        ////// OnPublishListener ///////////////////////////////////////////////////////////////////
        @Override
        public void publishProgress(byte step, Object pseudo) {
        // Allow to publish progression from the service handler

            Logs.add(Logs.Type.V, "step: " + step + ";pseudo: " + pseudo);
            if (step == DataService.LOGIN_STEP_SUCCEEDED)
                mPseudo = (String)pseudo; // Pseudo as defined in the remote DB

            publishProgress(step);
        }
    };

    //
    private static final int DELAY_WAIT_ACTIVITY = 300; // Max delay to wait activity attached (in ms)
    private final Boolean mWaitActivity = Boolean.FALSE; // Used to wait activity attached

    private FragmentActivity getWaitActivity() { // Wait activity attached
    // NB: When activity is recreated during an orientation change the fragment is detached and
    //     reattached. During this laps of time the activity is not available and it is the reason
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

    ////// Fragment ////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Logs.add(Logs.Type.V, null);
        synchronized (mWaitActivity) { // Manage waiting activity attached
            mWaitActivity.notify();
        }
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
