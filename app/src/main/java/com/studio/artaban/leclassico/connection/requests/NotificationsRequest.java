package com.studio.artaban.leclassico.connection.requests;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.connection.DataService;
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
        super(service);
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

        // Get login info
        Login.Reply dataLogin = new Login.Reply();
        mService.copyLoginData(dataLogin);

        if (data != null) { ////// Old data requested
            Logs.add(Logs.Type.I, "Old notifications requested");




            //dataLogin.token

            //mService.getContentResolver()
            //         .notifyChange(Uri.parse(intent.getStringExtra(EXTRA_DATA_URI), null);





        } else { ////// New or data updates requested

            // Get previous new notification count
            int prevNewNotify = DataTable.getNewNotification(mService.getContentResolver(), dataLogin.pseudo);

            // Synchronization (from remote to local DB)
            DataTable.SyncResult result = Database.getTable(NotificationsTable.TABLE_NAME)
                    .synchronize(mService.getContentResolver(), dataLogin.token.get(), WebServices.OPERATION_SELECT,
                            dataLogin.pseudo, Queries.LIMIT_MAIN_NOTIFY, null);
            if (DataTable.SyncResult.hasChanged(result)) {

                int newNotify = DataTable.getNewNotification(mService.getContentResolver(), dataLogin.pseudo);
                if (prevNewNotify != newNotify)
                    ServiceNotify.update(mService, newNotify);

                notifyChange(); // Notify DB change to observer URI
            }
        }
    }

    @Override
    public void synchronize() { // Update data from local to remote DB
        Logs.add(Logs.Type.V, null);

        // Get login info
        Login.Reply dataLogin = new Login.Reply();
        mService.copyLoginData(dataLogin);

        synchronized (mRegister) {
            DataTable table = Database.getTable(NotificationsTable.TABLE_NAME);

            // Synchronize inserted rows
            setSyncInProgress(NotificationsTable.TABLE_NAME,
                    NotificationsTable.COLUMN_PSEUDO + '=' + dataLogin.pseudo + '\'',
                    DataTable.Synchronized.TO_INSERT.getValue());

            ContentValues operationData =
                    table.syncInserted(mService.getContentResolver(), dataLogin.token.get(), dataLogin.pseudo);
            if ((operationData.size() > 0) &&
                    (table.synchronize(mService.getContentResolver(), dataLogin.token.get(), WebServices.OPERATION_INSERT,
                    dataLogin.pseudo, null, operationData) == null)) {

                Logs.add(Logs.Type.E, "Synchronization #" + Tables.ID_NOTIFICATIONS + " error (inserted)");
                return;
            }

            // Synchronize updated rows
            setSyncInProgress(NotificationsTable.TABLE_NAME,
                    NotificationsTable.COLUMN_PSEUDO + '=' + dataLogin.pseudo + '\'',
                    DataTable.Synchronized.TO_UPDATE.getValue());

            operationData = table.syncUpdated(mService.getContentResolver(), dataLogin.token.get(), dataLogin.pseudo);
            if ((operationData.size() > 0) &&
                    (table.synchronize(mService.getContentResolver(), dataLogin.token.get(), WebServices.OPERATION_UPDATE,
                    dataLogin.pseudo, null, operationData) == null)) {

                Logs.add(Logs.Type.E, "Synchronization #" + Tables.ID_NOTIFICATIONS + " error (updated)");
                return;
            }

            // Synchronize deleted rows
            setSyncInProgress(NotificationsTable.TABLE_NAME,
                    NotificationsTable.COLUMN_PSEUDO + '=' + dataLogin.pseudo + '\'',
                    DataTable.Synchronized.TO_DELETE.getValue());

            operationData = table.syncInserted(mService.getContentResolver(), dataLogin.token.get(), dataLogin.pseudo);
            if ((operationData.size() > 0) &&
                    (table.synchronize(mService.getContentResolver(), dataLogin.token.get(), WebServices.OPERATION_DELETE,
                    dataLogin.pseudo, null, operationData) == null)) {

                Logs.add(Logs.Type.E, "Synchronization #" + Tables.ID_NOTIFICATIONS + " error (deleted)");
            }
        }
    }
}
