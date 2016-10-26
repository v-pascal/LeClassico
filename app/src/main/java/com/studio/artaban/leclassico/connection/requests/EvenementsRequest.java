package com.studio.artaban.leclassico.connection.requests;

import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.connection.DataService;
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
        super(service, EvenementsTable.TABLE_NAME, Tables.ID_EVENEMENTS);
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
    public void request(Bundle data) { // Update data from remote to local DB

        Logs.add(Logs.Type.V, "data: " + data);
        if (!Internet.isConnected())
            return; // Nothing to do (without connection)

        if (data != null) { ////// Old data requested
            Logs.add(Logs.Type.I, "Old notifications requested");






        } else { ////// New or data updates requested






        }
    }

    @Override
    public void synchronize() { // Update data from local to remote DB
        Logs.add(Logs.Type.V, null);





    }
}
