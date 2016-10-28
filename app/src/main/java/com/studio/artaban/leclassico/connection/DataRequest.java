package com.studio.artaban.leclassico.connection;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.data.DataObserver;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;

import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Created by pascal on 16/10/16.
 * Remote & local DB exchange request class
 */
public abstract class DataRequest implements DataObserver.OnContentListener {

    public static final String EXTRA_DATA_URI = "uri";
    // Extra data keys (common)

    protected static final long DEFAULT_DELAY = 30000; // DB request task delay (in ms)

    //////
    private TimerTask mTask; // Request timer task
    private boolean mCancelled; // Request timer task cancelled flag

    protected DataService mService; // Data service
    protected String mTable; // DB table name
    protected byte mTableId; // DB table ID

    public DataRequest(DataService service, String table, byte tableId) {
        Logs.add(Logs.Type.V, "service: " + service + ";table: " + table);

        mSyncObserver = new DataObserver("requestDataObserverThread", this);
        mService = service;
        mTable = table;
        mTableId = tableId;
    }

    //
    public boolean register(Intent intent) { // Register URI observer (if not already done)
        Logs.add(Logs.Type.V, "intent: " + intent);

        Uri uri = intent.getParcelableExtra(EXTRA_DATA_URI);
        synchronized (mRegister) {
            if (!mRegister.contains(uri)) {
                boolean stopped = mRegister.isEmpty();

                mRegister.add(uri);
                register(intent.getExtras());
                mSyncObserver.register(mService.getContentResolver(), uri);

                return ((stopped) && (Internet.isConnected()));
                // Returns flag to schedule current request timer task

            } else
                Logs.add(Logs.Type.W, "Data request already registered: " + uri);
        }
        return false;
    }
    public boolean unregister(Intent intent) { // Unregister URI observer
        Logs.add(Logs.Type.V, "intent: " + intent);

        Uri uri = intent.getParcelableExtra(EXTRA_DATA_URI);
        synchronized (mRegister) {
            if (mRegister.remove(uri)) {

                unregister(uri);
                if (mRegister.isEmpty()) {

                    cancel();
                    mSyncObserver.unregister(mService.getContentResolver());
                    return true;
                    // Returns timer task cancel flag
                }

            } else
                Logs.add(Logs.Type.D, "Failed to unregister data request: " + uri);
                // NB: Often called twice in 'onPause' & 'onDestroy' methods (in user kill app case)
        }
        return false;
    }

    public TimerTask getTask() { // Return request timer task (create it if NULL or cancelled)

        Logs.add(Logs.Type.V, null);
        if (mRegister.isEmpty())
            return null;

        if ((mTask == null) || (mCancelled))
            mTask = new TimerTask() {
                @Override
                public void run() {
                    synchronized (mRegister) {
                        request(null);
                    }
                }
            };

        mCancelled = false;
        return mTask;
    }
    public void cancel() { // Cancel request task

        Logs.add(Logs.Type.V, null);
        if (mTask != null) // Can be cancelled without having started (offline)
            mTask.cancel();

        mCancelled = true;
    }

    ////// OnContentListener ///////////////////////////////////////////////////////////////////////
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Logs.add(Logs.Type.V, "selfChange: " + selfChange + ";uri: " + uri);
        if (Internet.isConnected())
            synchronize();
        else
            mToSynchronize = true;
    }

    protected boolean mToSynchronize; // Flag to synchronize data (from local to remote DB)
    public boolean toSynchronize() {
        return mToSynchronize;
    }
    private final DataObserver mSyncObserver; // Data update observer

    ////// DataRequest /////////////////////////////////////////////////////////////////////////////
    protected final ArrayList<Uri> mRegister = new ArrayList<>(); // Register URI list

    public abstract long getDelay();
    public abstract void register(Bundle data);
    public abstract void unregister(Uri uri);

    public abstract void request(Bundle data); // Update data from remote to local DB
    public abstract void synchronize(); // Update data from local to remote DB
}
