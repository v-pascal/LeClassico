package com.studio.artaban.leclassico.connection.requests;

import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.connection.DataService;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.helpers.Database;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.tools.Tools;

/**
 * Created by pascal on 17/10/16.
 * Notifications table exchange request (web service consumption)
 */
public class NotificationsRequest extends DataRequest {

    public NotificationsRequest(DataService service) {
        super(service, NotificationsTable.TABLE_NAME, Tables.ID_NOTIFICATIONS);
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




            //mService.getContentResolver()
            //         .notifyChange(Uri.parse(intent.getStringExtra(EXTRA_DATA_URI), null);




        } else { ////// New or data updates requested

            Tools.LoginReply dataLogin = mService.getLoginData();
            int entryUpdated = Database.synchronize(mTableId, mService.getContentResolver(),
                    dataLogin.token.get(), dataLogin.pseudo, Queries.LIMIT_MAIN_NOTIFICATIONS, null);

            if (entryUpdated > 0) { // Check to notify DB change to observer URI
                for (Uri observerUri : mRegister)
                    mService.getContentResolver().notifyChange(observerUri, null);
            }
        }
    }

    @Override
    public void synchronize() { // Update data from local to remote DB
        Logs.add(Logs.Type.V, null);









        //synchronized (mRegister) {
        //    for (Uri observerUri : mRegister)
        //        mService.getContentResolver().notifyChange(observerUri, null);
        //}

        //mToSynchronize = false;




    }
}
