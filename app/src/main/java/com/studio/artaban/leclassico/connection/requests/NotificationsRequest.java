package com.studio.artaban.leclassico.connection.requests;

import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.services.DataService;
import com.studio.artaban.leclassico.connection.ServiceNotify;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.codes.WebServices;
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
        super(service, Tables.ID_NOTIFICATIONS, NotificationsTable.COLUMN_PSEUDO);
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
    public boolean request(Bundle data) { // Update data from remote to local DB

        Logs.add(Logs.Type.V, "data: " + data);
        if (!Internet.isConnected())
            return false; // Nothing to do (without connection)

        // Get login info
        Login.Reply dataLogin = new Login.Reply();
        mService.copyLoginData(dataLogin);

        if (data != null) { ////// Old data requested
            Logs.add(Logs.Type.I, "Old notifications requested");

            DataTable.SyncResult result = Database.getTable(NotificationsTable.TABLE_NAME)
                    .synchronize(mService.getContentResolver(), dataLogin.token.get(),
                            WebServices.OPERATION_SELECT_OLD, dataLogin.pseudo, data.getString(EXTRA_DATA_DATE),
                            Queries.NOTIFICATIONS_OLD_LIMIT, null);
            if (DataTable.SyncResult.hasChanged(result)) {

                Logs.add(Logs.Type.I, "Old notifications received");
                mService.getContentResolver().notifyChange((Uri) data.getParcelable(EXTRA_DATA_URI),
                        mSyncObserver); // Last parameter needed in case where new data URI is registered

                return true; // Old entries found
            }

        } else { ////// New or data updates requested

            // Get previous new notification count
            int prevNewNotify = DataTable.getNewNotification(mService.getContentResolver(), dataLogin.pseudo);

            // Synchronization (from remote to local DB)
            DataTable.SyncResult result = Database.getTable(NotificationsTable.TABLE_NAME)
                    .synchronize(mService.getContentResolver(), dataLogin.token.get(), WebServices.OPERATION_SELECT,
                            dataLogin.pseudo, null, null, null);
            if (DataTable.SyncResult.hasChanged(result)) {

                int newNotify = DataTable.getNewNotification(mService.getContentResolver(), dataLogin.pseudo);
                if (prevNewNotify != newNotify)
                    ServiceNotify.update(mService, newNotify);

                Logs.add(Logs.Type.I, "Remote table #" + mTableId + " has changed");
                notifyChange(); // Notify DB change to observer URI
            }
        }
        return false;
    }
}
