package com.studio.artaban.leclassico.connection.requests;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.WebServices;
import com.studio.artaban.leclassico.helpers.Database;
import com.studio.artaban.leclassico.services.DataService;
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

    public static final String EXTRA_DATA_OBJECT_IDS = "objectIDs";
    public static final String EXTRA_DATA_OBJECT_TYPE = "objectType";
    // Extra data keys

    public CommentairesRequest(DataService service) {
        super(service, Tables.ID_COMMENTAIRES, CommentairesTable.COLUMN_PSEUDO);
    }
    private Bundle mData; // Register data

    ////// DataRequest /////////////////////////////////////////////////////////////////////////////
    @Override
    public long getDelay() {
        return DEFAULT_DELAY;
    }
    @Override
    public void register(Bundle data) {
        Logs.add(Logs.Type.V, "data: " + data);
        if (data != null)
            mData = (Bundle) data.clone();
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

        Bundle syncData = new Bundle();
        syncData.putString(DataTable.DATA_KEY_TOKEN, dataLogin.token.get());
        syncData.putString(DataTable.DATA_KEY_PSEUDO, dataLogin.pseudo);

        DataTable.SyncResult result;
        if (data != null) { ////// Old data requested

            Logs.add(Logs.Type.I, "Old comments requested");
            syncData.putShort(DataTable.DATA_KEY_LIMIT, Queries.COMMENTS_OLD_LIMIT);
            syncData.putString(DataTable.DATA_KEY_DATE, data.getString(EXTRA_DATA_DATE));

            ContentValues postData = new ContentValues();
            postData.put(WebServices.COMMENTS_DATA_IDS, data.getString(EXTRA_DATA_OBJECT_IDS));
            postData.put(WebServices.COMMENTS_DATA_TYPE, String.valueOf(data.getChar(EXTRA_DATA_OBJECT_TYPE)));

            synchronized (Database.getTable(CommentairesTable.TABLE_NAME)) {
                result = Database.getTable(CommentairesTable.TABLE_NAME)
                        .synchronize(mService.getContentResolver(), WebServices.OPERATION_SELECT_OLD,
                                syncData, postData);
            }
            if (result == null) {
                Logs.add(Logs.Type.E, "Failed to get old comments");
                return Result.NOT_FOUND;
            }
            if (DataTable.SyncResult.hasChanged(result)) {

                Logs.add(Logs.Type.I, "Old comments received");
                mService.getContentResolver().notifyChange((Uri) data.getParcelable(EXTRA_DATA_URI),
                        mSyncObserver); // Last parameter needed in case where new data URI is registered

                return Result.FOUND; // Old entries found
            }
            return Result.NO_MORE; // No more old entries
        }
        ////// Data updates requested (inserted, deleted or updated)
        if ((mData == null) ||
                (!mData.containsKey(EXTRA_DATA_OBJECT_IDS)) || (!mData.containsKey(EXTRA_DATA_OBJECT_TYPE)))
            throw new IllegalStateException("Missing IDs & Type in register data");

        ContentValues postData = new ContentValues();
        postData.put(WebServices.COMMENTS_DATA_IDS, mData.getString(EXTRA_DATA_OBJECT_IDS));
        postData.put(WebServices.COMMENTS_DATA_TYPE, String.valueOf(mData.getChar(EXTRA_DATA_OBJECT_TYPE)));



        /*
        if NEW

        StringBuilder ids = new StringBuilder();
        for (Integer id : syncData.getIntegerArrayList(DATA_KEY_IDS))
            ids.append((ids.length() == 0)? id.toString():WebServices.LIST_SEPARATOR + id.toString());
        postData.put(WebServices.DATA_IDS, ids.toString()); // Add IDs to post data
        postData.put(WebServices.DATA_TYPE, ?); // Add type to post data
        ////// ALSO ADD CODE ABOVE TO OLD REQUEST


        syncData.putString(DATA_KEY_STATUS_DATE, getMaxStatusDate(resolver, ids));
        syncData.putString(DATA_KEY_DATE, getMinDate(resolver, ids));
        */





        return Result.NOT_FOUND; // Unused
    }
}
