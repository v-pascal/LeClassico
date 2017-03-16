package com.studio.artaban.leclassico.connection.requests;

import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.services.DataService;
import com.studio.artaban.leclassico.connection.Login;
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
        super(service, Tables.ID_MESSAGERIE, MessagerieTable.COLUMN_PSEUDO, true);
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

        DataTable.SyncResult result;
        if (data != null) { ////// Old data requested

            Logs.add(Logs.Type.I, "Old mails requested");





            return Result.NO_MORE; // No more old entries
        }
        ////// Data updates requested (inserted, deleted or updated)






        return Result.NOT_FOUND; // Unused
    }
}
