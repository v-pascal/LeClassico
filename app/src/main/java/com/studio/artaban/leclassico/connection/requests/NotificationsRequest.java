package com.studio.artaban.leclassico.connection.requests;

import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.connection.DataService;
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

    ////// TimerTask ///////////////////////////////////////////////////////////////////////////////
    @Override
    public void run() {
        Logs.add(Logs.Type.V, null);







        // Notify change to observer URI
        //for (Uri observerUri : mRegister)
        //    mService.getContentResolver().notifyChange(observerUri, null);






    }
}
