package com.studio.artaban.leclassico.connection;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataObserver;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.codes.WebServices;
import com.studio.artaban.leclassico.helpers.Database;
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

    protected final DataService mService; // Data service
    private final byte mTableId; // DB table ID
    private final String mFieldPseudo; // DB pseudo column name

    public DataRequest(DataService service, byte tableId, String pseudoField) {
        Logs.add(Logs.Type.V, "service: " + service + ";tableId: " + tableId + ";pseudoField: " + pseudoField);

        mSyncObserver = new DataObserver("requestDataObserverThread", this);
        mFieldPseudo = pseudoField;
        mService = service;
        mTableId = tableId;
    }

    //
    protected void notifyChange() { // Notify change on registered URI
        Logs.add(Logs.Type.V, null);
        for (Uri observerUri : mRegister)
            mService.getContentResolver().notifyChange(observerUri, mSyncObserver);
    }
    private void setSyncInProgress(String table, String selection, byte operation) {
    // Set synchronization fields into in progress status according its operation

        Logs.add(Logs.Type.V, "table: " + table + ";selection: " + selection + ";operation: " + operation);

        ContentValues values = new ContentValues();
        values.put(Constants.DATA_COLUMN_SYNCHRONIZED, operation | DataTable.Synchronized.IN_PROGRESS.getValue());
        int result = mService.getContentResolver().update(Uri.parse(DataProvider.CONTENT_URI + table),
                values, selection + " AND " + Constants.DATA_COLUMN_SYNCHRONIZED + '=' + operation,
                null);

        // Notify registered URIs (if needed)
        if (result > 0)
            notifyChange();
    }

    //////
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
    }

    protected final DataObserver mSyncObserver; // Data update observer

    ////// DataRequest /////////////////////////////////////////////////////////////////////////////
    protected final ArrayList<Uri> mRegister = new ArrayList<>(); // Register URI list

    public abstract long getDelay();
    public abstract void register(Bundle data);
    public abstract void unregister(Uri uri);

    public abstract void request(Bundle data); // Update data from remote to local DB
    public void synchronize() { // Update data from local to remote DB
        Logs.add(Logs.Type.V, null);

        // Get login info
        Login.Reply dataLogin = new Login.Reply();
        mService.copyLoginData(dataLogin);

        synchronized (mRegister) {
            String tableName = Tables.getName(mTableId);
            DataTable table = Database.getTable(tableName);

            ////// Synchronize inserted rows
            setSyncInProgress(tableName, mFieldPseudo + "='" + dataLogin.pseudo + '\'',
                    DataTable.Synchronized.TO_INSERT.getValue());

            ContentValues operationData =
                    table.syncInserted(mService.getContentResolver(), dataLogin.pseudo);
            if ((operationData.size() > 0) &&
                    (table.synchronize(mService.getContentResolver(), dataLogin.token.get(), WebServices.OPERATION_INSERT,
                    dataLogin.pseudo, null, operationData) == null)) {

                Logs.add(Logs.Type.E, "Synchronization #" + mTableId + " error (inserted)");
                return;
            }

            ////// Synchronize updated rows
            setSyncInProgress(tableName, mFieldPseudo + "='" + dataLogin.pseudo + '\'',
                    DataTable.Synchronized.TO_UPDATE.getValue());

            operationData = table.syncUpdated(mService.getContentResolver(), dataLogin.pseudo);
            if ((operationData.size() > 0) &&
                    (table.synchronize(mService.getContentResolver(), dataLogin.token.get(), WebServices.OPERATION_UPDATE,
                    dataLogin.pseudo, null, operationData) == null)) {

                Logs.add(Logs.Type.E, "Synchronization #" + mTableId + " error (updated)");
                return;
            }

            ////// Synchronize deleted rows
            setSyncInProgress(tableName, mFieldPseudo + "='" + dataLogin.pseudo + '\'',
                    DataTable.Synchronized.TO_DELETE.getValue());

            operationData = table.syncInserted(mService.getContentResolver(), dataLogin.pseudo);
            if ((operationData.size() > 0) &&
                    (table.synchronize(mService.getContentResolver(), dataLogin.token.get(), WebServices.OPERATION_DELETE,
                    dataLogin.pseudo, null, operationData) == null)) {

                Logs.add(Logs.Type.E, "Synchronization #" + mTableId + " error (deleted)");
            }
        }
    }
}
