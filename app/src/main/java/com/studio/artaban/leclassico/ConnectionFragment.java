package com.studio.artaban.leclassico;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 08/08/16.
 * Fragment to display a progress dialog during connection & synchronization
 */
public class ConnectionFragment extends Fragment {

    public static final int STEP_CHECK_INTERNET = 0;
    public static final int STEP_OFFLINE_IDENTIFICATION = 1;
    public static final int STEP_ONLINE_IDENTIFICATION = 2;
    public static final int STEP_LOGIN = 3;
    public static final int STEP_LOGIN_FAILED = 4;
    public static final int STEP_NO_INTERNET = 5;
    // Connection steps

    public static final String TAG = "connectionProgress";

    //
    private OnProgressListener mListener;
    public interface OnProgressListener { //////////////////////////////////////////////////////////

        void onPreExecute();
        boolean onProgressUpdate(int step, String pseudo, String password);
        void onPostExecute(boolean result, String pseudo);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private String mPseudo;
    private String mPassword;
    // Login data

    public void display(Activity activity, String pseudo, String password) {

        Logs.add(Logs.Type.V, "activity: " + activity + ";pseudo: " + pseudo);// + ";password: " + password);
        mPseudo = pseudo;
        mPassword = password;

        mProgressTask = new ConnectionProgressTask();
        mProgressTask.execute(activity);
    }
    public void cancel() {

        Logs.add(Logs.Type.V, null);
        if (mProgressTask != null) {
            mProgressTask.cancel(true);
            mProgressTask = null;
        }
    }
    public boolean isDisplayed() {
        return ((mProgressTask != null) && (mProgressTask.getStatus() != AsyncTask.Status.FINISHED));
    }

    //
    private ConnectionProgressTask mProgressTask;
    private class ConnectionProgressTask extends AsyncTask<Activity, Integer, Boolean> {

        private static final long INFORM_USER_DELAY = 1000; // Delay of displaying step

        private void publishWaitProgress(int step) {
        // Publish progression and wait its displayed B4 marking a delay to let's the user to be informed

            Logs.add(Logs.Type.V, "step: " + step);

            Integer stepToPublish = step;
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
                Logs.add(Logs.Type.E, "User delay interrupted");
            }
        }

        //////
        @Override
        protected void onPreExecute() {
            Logs.add(Logs.Type.V, null);
            mListener.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Activity... params) {
            Logs.add(Logs.Type.V, null);

            // Check Internet connection
            if (!Internet.isOnline(params[0])) {

                // No Internet connection so check existing DB to work offline
                ContentResolver cr = params[0].getContentResolver();
                Cursor result = cr.query(Uri.parse(DataProvider.CONTENT_URI + CamaradesTable.TABLE_NAME),
                        new String[]{ "count(*)" }, null, null, null);
                result.moveToFirst();
                int membersCount = result.getInt(0);
                result.close();

                if (membersCount > 0) { // Found existing DB (try to work offline)

                    publishWaitProgress(STEP_OFFLINE_IDENTIFICATION);
                    String pseudo = null;

                    // Offline identification
                    result = cr.query(Uri.parse(DataProvider.CONTENT_URI + CamaradesTable.TABLE_NAME),
                        new String[]{ CamaradesTable.COLUMN_PSEUDO },
                        "UPPER(" + CamaradesTable.COLUMN_PSEUDO  + ")='" + mPseudo.toUpperCase() +
                                "' AND " + CamaradesTable.COLUMN_CODE_CONF + "='" + mPassword + "'",
                        null, null);
                    if (result.getCount() > 0) {
                        result.moveToFirst();
                        pseudo = result.getString(0);
                    }
                    result.close();

                    if (pseudo != null) { // Login succeeded

                        mPseudo = pseudo; // Pseudo as defined in the DB
                        return Boolean.TRUE;

                    } else // Login failed
                        publishProgress(STEP_LOGIN_FAILED);

                } else {
                    publishWaitProgress(STEP_CHECK_INTERNET);
                    publishProgress(STEP_NO_INTERNET);
                }

            } else {

                // Identification
                publishWaitProgress(STEP_ONLINE_IDENTIFICATION);
                publishProgress(STEP_LOGIN);
            }
            return Boolean.FALSE;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Logs.add(Logs.Type.V, "result: " + result);
            mListener.onPostExecute(result.booleanValue(), mPseudo);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            Logs.add(Logs.Type.V, null);
            if (!mListener.onProgressUpdate(values[0].intValue(), mPseudo, mPassword))
                cancel(true);

            switch (values[0].intValue()) {
                case STEP_CHECK_INTERNET:
                case STEP_OFFLINE_IDENTIFICATION:
                case STEP_ONLINE_IDENTIFICATION: {

                    synchronized (values[0]) { // Notify progress publication done
                        values[0].notify();
                    }
                    break;
                }
            }
        }
    };

    //////
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
