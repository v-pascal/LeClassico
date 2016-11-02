package com.studio.artaban.leclassico.connection.requests;

import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.connection.DataService;
import com.studio.artaban.leclassico.connection.ServiceNotify;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.helpers.Database;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.helpers.Logs;

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
    public long getDelay() {
        return 60000; // 1 minute
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




            //mService.getContentResolver()
            //         .notifyChange(Uri.parse(intent.getStringExtra(EXTRA_DATA_URI), null);





        } else { ////// New or data updates requested

            // Get login info
            Login.Reply dataLogin = new Login.Reply();
            mService.copyLoginData(dataLogin);

            // Get previous new notification count
            int prevNewNotify = DataTable.getNewNotification(mService.getContentResolver(), dataLogin.pseudo);

            // Synchronization (from remote to local DB)
            Database.SyncResult result = Database.synchronize(mTableId, mService.getContentResolver(),
                    dataLogin.token.get(), dataLogin.pseudo, Queries.LIMIT_MAIN_NOTIFY, null);
            if (Database.SyncResult.hasChanged(result)) {

                int newNotify = DataTable.getNewNotification(mService.getContentResolver(), dataLogin.pseudo);
                if (prevNewNotify != newNotify)
                    ServiceNotify.update(mService, newNotify);

                // Notify DB change to observer URI
                notifyChange();
            }
        }
    }

    @Override
    public void synchronize() { // Update data from local to remote DB
        Logs.add(Logs.Type.V, null);

        // Update synchronization fields (to inform user synchronization in progress)
        setSyncInProgress(NotificationsTable.TABLE_NAME);

        // Synchronization (from local to remote DB)







        //mToSynchronize = false;







    }
}
