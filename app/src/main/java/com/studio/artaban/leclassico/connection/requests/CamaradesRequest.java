package com.studio.artaban.leclassico.connection.requests;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.activities.settings.PrefsUserFragment;
import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.codes.WebServices;
import com.studio.artaban.leclassico.helpers.Database;
import com.studio.artaban.leclassico.services.DataService;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;

import java.util.HashMap;

/**
 * Created by pascal on 20/10/16.
 * Camarades table exchange request (web service consumption)
 */
public class CamaradesRequest extends DataRequest {

    public static final String EXTRA_DATA_PSEUDO = "pseudo";
    // Extra data keys (see 'DataRequest' extra data keys)

    public static final char PSEUDO_SEPARATOR = '&'; // Pseudo separator in the post data request
    // NB: The '&' is not available in the member pseudo definition

    public CamaradesRequest(DataService service) {
        super(service, Tables.ID_CAMARADES, CamaradesTable.COLUMN_PSEUDO, false);
    }
    private final HashMap<Uri, String> mIdsPseudo = new HashMap<>(); // Member IDs for each registered URI

    ////// DataRequest /////////////////////////////////////////////////////////////////////////////
    @Override
    public long getDelay() {
        return DEFAULT_DELAY;
    }
    @Override
    public void register(Uri uri, Bundle data) {
        Logs.add(Logs.Type.V, "uri: " + uri + ";data: " + data);
        if ((data == null) || (!data.containsKey(EXTRA_DATA_PSEUDO)))
            throw new IllegalArgumentException("Missing member ID in register data (pseudo)");

        mIdsPseudo.put(uri, data.getString(EXTRA_DATA_PSEUDO));
    }
    @Override
    public void unregister(Uri uri) {
        Logs.add(Logs.Type.V, "uri: " + uri);
        mIdsPseudo.remove(uri);
    }

    @Override
    public Result request(Bundle data) { // Update data from remote to local DB

        Logs.add(Logs.Type.V, "data: " + data);
        if (!Internet.isConnected())
            return Result.NOT_FOUND; // Nothing to do (without connection)

        if (data != null) { ////// More data requested

            Logs.add(Logs.Type.I, "More members requested");
            throw new IllegalArgumentException("More members request not implemented");
            // NB: Currently all members are transferred from remote to local DB
        }
        ////// Data updates requested (inserted, deleted or updated)

        // Get login info
        Login.Reply dataLogin = new Login.Reply();
        mService.copyLoginData(dataLogin);

        Bundle syncData = new Bundle();
        syncData.putString(DataTable.DATA_KEY_TOKEN, dataLogin.token.get());
        syncData.putString(DataTable.DATA_KEY_PSEUDO, dataLogin.pseudo);

        // Get pseudos to request
        StringBuilder pseudos = new StringBuilder();
        for (HashMap.Entry<Uri, String> pseudoIds : mIdsPseudo.entrySet()) {
            if (pseudos.length() != 0)
                pseudos.append(PSEUDO_SEPARATOR);

            pseudos.append(pseudoIds.getValue());
        }
        ContentValues postData = new ContentValues();
        postData.put(WebServices.MEMBERS_DATA_PSEUDO, pseudos.toString());

        // Get status date criteria (according members to request)
        Cursor status = mService.getContentResolver().query(Uri.parse(DataProvider.CONTENT_URI +
                CamaradesTable.TABLE_NAME), new String[]{"max(" + Constants.DATA_COLUMN_STATUS_DATE + ')'},
                CamaradesTable.COLUMN_PSEUDO + " IN ('" +
                        pseudos.toString().replace(String.valueOf(PSEUDO_SEPARATOR), "','") + "') AND " +
                        Constants.DATA_COLUMN_SYNCHRONIZED + '=' + DataTable.Synchronized.DONE.getValue(),
                null ,null);
        status.moveToFirst();
        syncData.putString(DataTable.DATA_KEY_STATUS_DATE, status.getString(0));
        status.close();

        // Synchronization (from remote to local DB)
        DataTable.SyncResult result;
        synchronized (Database.getTable(CamaradesTable.TABLE_NAME)) {

            result = Database.getTable(CamaradesTable.TABLE_NAME)
                    .synchronize(mService.getContentResolver(), WebServices.OPERATION_SELECT,
                            syncData, postData);
        }
        if (DataTable.SyncResult.hasChanged(result)) {
            Logs.add(Logs.Type.I, "Remote table #" + mTableId + " has changed");

            // Set user preferences info (in case of update)
            if (result.updated > 0) {
                Cursor cursor = mService.getContentResolver().query(Uri.parse(DataProvider.CONTENT_URI +
                        CamaradesTable.TABLE_NAME), null, DataTable.DataField.COLUMN_ID + '=' +
                        dataLogin.pseudoId, null, null);
                PrefsUserFragment.getData(cursor);
                cursor.close();
            }
            notifyChange(); // Notify DB change to observer URI
        }
        return Result.NOT_FOUND; // Unused
    }
}
