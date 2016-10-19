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

    }
    @Override
    public void unregister(Uri uri) {

    }

    @Override
    public void request(Bundle data) {

        Logs.add(Logs.Type.V, "data: " + data);
        if (!Internet.isConnected())
            return; // Nothing to do (without connection)

        if (data != null) { ////// Old data requested
            Logs.add(Logs.Type.I, "Old notifications requested");



            //mService.getContentResolver()
            //         .notifyChange(Uri.parse(intent.getStringExtra(EXTRA_DATA_URI), null);




        } else { ////// New or data updates requested




            // boolean changed;
            // DataRequest.EXTRA_DATA_DATE can be null!

            // Notify change to URI observer
            //for (Uri observerUri : mRegister)
            //    mService.getContentResolver().notifyChange(observerUri, null);



        }
    }

    @Override
    public void synchronize() {
        Logs.add(Logs.Type.V, null);





        //mToSynchronize = false;





    }

    ////// TimerTask ///////////////////////////////////////////////////////////////////////////////
    @Override
    public void run() {
        Logs.add(Logs.Type.V, null);
        request(null);
    }
}
