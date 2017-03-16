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
import com.studio.artaban.leclassico.data.tables.AbonnementsTable;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 20/10/16.
 * Abonnements table exchange request (web service consumption)
 */
public class AbonnementsRequest extends DataRequest {

    public AbonnementsRequest(DataService service) {
        super(service, Tables.ID_ABONNEMENTS, AbonnementsTable.COLUMN_PSEUDO, false);
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

        if (data != null) { ////// Old data requested

            Logs.add(Logs.Type.I, "Old followers requested");
            throw new IllegalArgumentException("Old followers request not allowed for this table");
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
        synchronized (Database.getTable(AbonnementsTable.TABLE_NAME)) {
            result = Database.getTable(AbonnementsTable.TABLE_NAME)
                    .synchronize(mService.getContentResolver(), WebServices.OPERATION_SELECT, syncData, null);
        }
        if (DataTable.SyncResult.hasChanged(result)) {
            Logs.add(Logs.Type.I, "Remote table #" + mTableId + " has changed");
            notifyChange(); // Notify DB change to observer URI
        }
        return Result.NOT_FOUND; // Unused
    }
}
