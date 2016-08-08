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
        void onProgressUpdate();
        void onPostExecute();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private String mPseudo;
    private String mPassword;
    // Login data

    public void display(Activity activity, String pseudo, String password) {

        Logs.add(Logs.Type.V, null);
        mPseudo = pseudo;
        mPassword = password;

        mProgressTask = new ConnectionProgressTask();
        mProgressTask.execute(activity);
    }
    public void cancel() {

        Logs.add(Logs.Type.V, null);
        if (mProgressTask != null)
            mProgressTask.cancel(true);
    }

    //
    private ConnectionProgressTask mProgressTask;
    private class ConnectionProgressTask extends AsyncTask<Activity, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            Logs.add(Logs.Type.V, null);




        }

        @Override
        protected Integer doInBackground(Activity... params) {
            Logs.add(Logs.Type.V, null);




            /*
            // Check Internet connection
            boolean connected = Internet.isOnline(params[0]);
            try {

                if (!connected) {

                    // No Internet connection so check existing DB to work offline
                    ContentResolver cr = params[0].getContentResolver();
                    Cursor result = cr.query(Uri.parse(DataProvider.CONTENT_URI + CamaradesTable.TABLE_NAME),
                            new String[]{ "count(*)" }, null, null, null);
                    int membersCount = 0;
                    if (result.getCount() > 0) {
                        result.moveToFirst();
                        membersCount = result.getInt(0);
                    }
                    result.close();

                    if (membersCount > 0) { // Found existing DB (try to work offline)

                        WaitUiThread.run(params[0], new WaitUiThread.TaskToRun() {
                            @Override
                            public void proceed() {
                                mProgressDialog.setMessage(getString(R.string.offline_identification));
                            }
                        });

                        try { Thread.sleep(1000, 0); // Sleep to inform user offline identification
                        } catch (InterruptedException e) {
                            Logs.add(Logs.Type.E, "Sleep interrupted");
                        }
                        String pseudo = null;

                        // Offline identification
                        result = cr.query(Uri.parse(DataProvider.CONTENT_URI + CamaradesTable.TABLE_NAME),
                            new String[]{ CamaradesTable.COLUMN_PSEUDO },
                            "UPPER(" + CamaradesTable.COLUMN_PSEUDO  + ")='" +
                                    mPseudo.toUpperCase() +
                                    "' AND " +
                                    CamaradesTable.COLUMN_CODE_CONF + "='" +
                                    mPassword + "'",
                            null, null);
                        if (result.getCount() > 0) {
                            result.moveToFirst();
                            pseudo = result.getString(0);
                        }
                        result.close();

                        if (pseudo != null) { // Login succeeded

                            mDataService.get().login(pseudo, mPassword);
                            startMainActivity(); ////// Start main activity

                        } else // Login failed
                            displayError(false, R.string.login_failed);

                    } else
                        displayError(false, R.string.no_internet);

                } else {

                    // Identification
                    WaitUiThread.run(params[0], new WaitUiThread.TaskToRun() {
                        @Override
                        public void proceed() {
                            mProgressDialog.setMessage(getString(R.string.identification));
                        }
                    });
                    if (!mDataService.get().login(mPseudo, mPassword))
                        displayError(false, R.string.no_internet);
                }

            } catch (NullPointerException e) {

                Logs.add(Logs.Type.E, "Unexpected service disconnection");
                displayError(true, R.string.service_unavailable);
            }
            */









            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            Logs.add(Logs.Type.V, "integer: " + integer);



        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Logs.add(Logs.Type.V, null);




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
