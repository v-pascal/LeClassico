package com.studio.artaban.leclassico.connection.requests;

import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.services.DataService;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.tables.EvenementsTable;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 20/10/16.
 * Evenements table exchange request (web service consumption)
 */
public class EvenementsRequest extends DataRequest {

    public EvenementsRequest(DataService service) {
        super(service, Tables.ID_EVENEMENTS, EvenementsTable.COLUMN_PSEUDO);
    }

    ////// DataRequest /////////////////////////////////////////////////////////////////////////////
    @Override
    public long getDelay() {
        return DEFAULT_DELAY;
    }
    @Override
    public void register(Bundle data) {

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

        DataTable.SyncResult result;
        if (data != null) { ////// Old data requested

            Logs.add(Logs.Type.I, "Old events requested");





            return Result.NO_MORE; // No more old entries
        }
        ////// New or data updates requested






        return Result.NOT_FOUND; // Unused
    }
}
