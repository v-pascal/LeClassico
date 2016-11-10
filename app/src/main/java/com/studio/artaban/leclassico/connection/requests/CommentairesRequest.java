package com.studio.artaban.leclassico.connection.requests;

import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.connection.DataService;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.tables.CommentairesTable;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 20/10/16.
 * Commentaires table exchange request (web service consumption)
 */
public class CommentairesRequest extends DataRequest {

    public CommentairesRequest(DataService service) {
        super(service, Tables.ID_COMMENTAIRES, CommentairesTable.COLUMN_PSEUDO);
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

        // Get login info
        Login.Reply dataLogin = new Login.Reply();
        mService.copyLoginData(dataLogin);

        if (data != null) { ////// Old data requested
            Logs.add(Logs.Type.I, "Old comments requested");






        } else { ////// New or data updates requested






        }
    }
}
