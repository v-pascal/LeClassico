package com.studio.artaban.leclassico.connection.requests;

import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.WebServices;
import com.studio.artaban.leclassico.helpers.Database;
import com.studio.artaban.leclassico.services.DataService;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.tables.ActualitesTable;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 20/10/16.
 * Actualites table exchange request (web service consumption)
 */
public class ActualitesRequest extends DataRequest {

    public ActualitesRequest(DataService service) {
        super(service, Tables.ID_ACTUALITES, ActualitesTable.COLUMN_PSEUDO);
    }

    ////// DataRequest /////////////////////////////////////////////////////////////////////////////
    @Override
    public long getDelay() {
        return DEFAULT_DELAY;
    }
    @Override
    public void register(Uri uri, Bundle data) {

    }
    @Override
    public void unregister(Uri uri) {

    }

    @Override
    public Result request(Bundle data) { // Update data from remote to local DB

        Logs.add(Logs.Type.V, "data: " + data);
        if (!Internet.isConnected())
            return Result.NOT_FOUND; // Nothing to do (without connection)

        // Get login info
        Login.Reply dataLogin = new Login.Reply();
        mService.copyLoginData(dataLogin);

        Bundle syncData = new Bundle();
        syncData.putString(DataTable.DATA_KEY_TOKEN, dataLogin.token.get());
        syncData.putString(DataTable.DATA_KEY_PSEUDO, dataLogin.pseudo);

        DataTable.SyncResult result;
        if (data != null) { ////// Old data requested

            Logs.add(Logs.Type.I, "Old publications requested");
            syncData.putShort(DataTable.DATA_KEY_LIMIT, Queries.PUBLICATIONS_OLD_LIMIT);
            syncData.putString(DataTable.DATA_KEY_DATE, data.getString(EXTRA_DATA_DATE));

            synchronized (Database.getTable(ActualitesTable.TABLE_NAME)) {
                result = Database.getTable(ActualitesTable.TABLE_NAME)
                        .synchronize(mService.getContentResolver(), WebServices.OPERATION_SELECT_OLD,
                                syncData, null);
            }
            if (result == null) {

                Logs.add(Logs.Type.E, "Failed to get old publications");
                return Result.NOT_FOUND;
            }
            if (DataTable.SyncResult.hasChanged(result)) {

                Logs.add(Logs.Type.I, "Old publications received");
                mService.getContentResolver().notifyChange((Uri) data.getParcelable(EXTRA_DATA_URI),
                        mSyncObserver); // Last parameter needed in case where new data URI is registered

                return Result.FOUND; // Old entries found
            }
            return Result.NO_MORE; // No more old entries
        }
        ////// Data updates requested (inserted, deleted or updated)

        // Synchronization (from remote to local DB)
        synchronized (Database.getTable(ActualitesTable.TABLE_NAME)) {
            result = Database.getTable(ActualitesTable.TABLE_NAME)
                    .synchronize(mService.getContentResolver(), WebServices.OPERATION_SELECT, syncData, null);
        }
        if (DataTable.SyncResult.hasChanged(result)) {

            Logs.add(Logs.Type.I, "Remote table #" + mTableId + " has changed");
            notifyChange(); // Notify DB change to observer URI
        }
        return Result.NOT_FOUND; // Unused
    }
}
