package com.studio.artaban.leclassico.connection.requests;

import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.codes.WebServices;
import com.studio.artaban.leclassico.helpers.Database;
import com.studio.artaban.leclassico.services.DataService;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.tables.PresentsTable;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 20/10/16.
 * Presents table exchange request (web service consumption)
 */
public class PresentsRequest extends DataRequest {

    public PresentsRequest(DataService service) {
        super(service, Tables.ID_PRESENTS, PresentsTable.COLUMN_PSEUDO, true);
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

        if (data != null) { ////// More data requested

            Logs.add(Logs.Type.I, "More present members info requested");
            return Result.NOT_FOUND; // No more entries (always synchronized)
        }
        ////// Data updates requested (inserted, deleted or updated)

        // Get login info
        Login.Reply dataLogin = new Login.Reply();
        mService.copyLoginData(dataLogin);

        Bundle syncData = new Bundle();
        syncData.putString(DataTable.DATA_KEY_TOKEN, dataLogin.token.get());
        syncData.putString(DataTable.DATA_KEY_PSEUDO, dataLogin.pseudo);

        // Synchronization (from remote to local DB)
        DataTable.SyncResult result;
        synchronized (Database.getTable(PresentsTable.TABLE_NAME)) {
            result = Database.getTable(PresentsTable.TABLE_NAME)
                    .synchronize(mService.getContentResolver(), WebServices.OPERATION_SELECT, syncData, null);
        }
        if (DataTable.SyncResult.hasChanged(result)) {
            Logs.add(Logs.Type.I, "Remote table #" + mTableId + " has changed");
            notifyChange(); // Notify DB change to observer URI
        }
        return Result.NOT_FOUND; // Unused
    }
}
