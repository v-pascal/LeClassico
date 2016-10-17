package com.studio.artaban.leclassico.connection;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.helpers.Logs;

import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Created by pascal on 16/10/16.
 * Remote & local DB exchange request class
 */
public abstract class DataRequest extends TimerTask {

    // Extra data keys
    public static final String EXTRA_DATA_URI = "uri";

    //////
    protected DataService mService; // Data service
    protected String mTable; // DB table name

    public DataRequest(DataService service, String table) {
        Logs.add(Logs.Type.V, "service: " + service + ";table: " + table);

        mService = service;
        mTable = table;
    }

    //
    public boolean register(Intent intent) { // Register URI observer (if not already done)
        Logs.add(Logs.Type.V, "intent: " + intent);

        Uri uri = Uri.parse(intent.getStringExtra(EXTRA_DATA_URI));
        synchronized (mRegister) {
            if (!mRegister.contains(uri)) {
                boolean stopped = mRegister.isEmpty();

                mRegister.add(uri);
                register(intent.getExtras());
                return stopped; // Schedule request timer (if not already the case)
            }
        }
        return false;
    }
    public void unregister(Intent intent) { // Unregister URI observer
        Logs.add(Logs.Type.V, "intent: " + intent);

        Uri uri = Uri.parse(intent.getStringExtra(EXTRA_DATA_URI));
        synchronized (mRegister) {
            if (mRegister.remove(uri)) {

                unregister(uri);
                if (mRegister.isEmpty())
                    cancel();
            }
        }
    }

    //////
    protected final ArrayList<Uri> mRegister = new ArrayList<>(); // Register URI list

    public abstract void register(Bundle data);
    public abstract void unregister(Uri uri);

    public abstract void request(Bundle data);
}
