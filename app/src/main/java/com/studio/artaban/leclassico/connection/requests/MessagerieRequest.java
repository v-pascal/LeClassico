package com.studio.artaban.leclassico.connection.requests;

import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.connection.DataService;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.tables.MessagerieTable;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 20/10/16.
 * Messageries table exchange request (web service consumption)
 */
public class MessagerieRequest extends DataRequest {

    public MessagerieRequest(DataService service) {
        super(service, MessagerieTable.TABLE_NAME, Tables.ID_MESSAGERIE);
    }

    ////// DataRequest /////////////////////////////////////////////////////////////////////////////
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
