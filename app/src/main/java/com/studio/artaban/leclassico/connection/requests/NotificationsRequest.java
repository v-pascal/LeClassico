package com.studio.artaban.leclassico.connection.requests;

import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.connection.DataService;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 17/10/16.
 * Notification DB exchange request
 */
public class NotificationsRequest extends DataRequest {

    public NotificationsRequest(DataService service, String table) {
        super(service, table);
    }

    ////// DataRequest /////////////////////////////////////////////////////////////////////////////
    @Override
    public void register(Bundle data) {
        Logs.add(Logs.Type.V, "data: " + data);




    }
    @Override
    public void unregister(Uri uri) {
        Logs.add(Logs.Type.V, "uri: " + uri);




    }

    @Override
    public void request(Bundle data) {

        Logs.add(Logs.Type.V, "data: " + data);
        if (!Internet.isConnected())
            return; // Nothing to do (without connection)







        //if (data != null) { ////// OLD
        //  Old entries requested
        //  mService.getContentResolver()
        //         .notifyChange(Uri.parse(intent.getStringExtra(EXTRA_DATA_URI), null);
        //} else { ////// NEW

        //DataRequest.EXTRA_DATA_DATE can be null!

        // Notify change to observer URI
        //for (Uri observerUri : mRegister)
        //    mService.getContentResolver().notifyChange(observerUri, null);

        // }






    }

    ////// TimerTask ///////////////////////////////////////////////////////////////////////////////
    @Override
    public void run() {

        Logs.add(Logs.Type.V, null);
        request(null);
    }
}
