package com.studio.artaban.leclassico.connection;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import com.studio.artaban.leclassico.data.DataObserver;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;

import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Created by pascal on 16/10/16.
 * Remote & local DB exchange request class
 */
public abstract class DataRequest extends TimerTask implements DataObserver.OnContentListener {

    // Extra data keys (common)
    public static final String EXTRA_DATA_URI = "uri";

    //////
    protected DataService mService; // Data service
    protected String mTable; // DB table name
    protected byte mTableId; // DB table ID

    public DataRequest(DataService service, String table, byte tableId) {

        Logs.add(Logs.Type.V, "service: " + service + ";table: " + table);
        HandlerThread observerThread = new HandlerThread("requestDataObserverThread");
        observerThread.start();

        mSyncObserver = new DataObserver(new Handler(observerThread.getLooper()), this);
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
                // Return flag to schedule current request timer task (if not already done)

            } else
                Logs.add(Logs.Type.W, "Data request already registered: " + uri);
        }
        return false;
    }
    public void unregister(Intent intent) { // Unregister URI observer
        Logs.add(Logs.Type.V, "intent: " + intent);

        Uri uri = intent.getParcelableExtra(EXTRA_DATA_URI);
        synchronized (mRegister) {
            if (mRegister.remove(uri)) {

                unregister(uri);
                if (mRegister.isEmpty())
                    cancel();

            } else
                Logs.add(Logs.Type.W, "Failed to unregister data request: " + uri);
        }
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

    public abstract void register(Bundle data);
    public abstract void unregister(Uri uri);

    public abstract void request(Bundle data); // Update data from remote to local DB
    public abstract void synchronize(); // Update data from local to remote DB
}
