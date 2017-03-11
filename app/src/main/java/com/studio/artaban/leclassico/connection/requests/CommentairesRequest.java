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

import java.util.HashMap;

/**
 * Created by pascal on 20/10/16.
 * Commentaires table exchange request (web service consumption)
 */
public class CommentairesRequest extends DataRequest {

    public static final String EXTRA_DATA_OBJECT_IDS = "objectIDs";
    public static final String EXTRA_DATA_OBJECT_TYPE = "objectType";
    // Extra data keys (see 'DataRequest' extra data keys)

    public CommentairesRequest(DataService service) {
        super(service, Tables.ID_COMMENTAIRES, CommentairesTable.COLUMN_PSEUDO);
    }
    private final HashMap<Uri, String> mIdsPub = new HashMap<>(); // Publication IDs for each registered URI
    private final HashMap<Uri, String> mIdsPhoto = new HashMap<>(); // Photo IDs for each registered URI

    ////// DataRequest /////////////////////////////////////////////////////////////////////////////
    @Override
    public long getDelay() {
        return DEFAULT_DELAY;
    }
    @Override
    public void register(Uri uri, Bundle data) {
        Logs.add(Logs.Type.V, "uri: " + uri + ";data: " + data);
        if ((data == null) ||
                (!data.containsKey(EXTRA_DATA_OBJECT_TYPE)) || (!data.containsKey(EXTRA_DATA_OBJECT_IDS)))
            throw new IllegalArgumentException("Missing IDs & Type in register data");

        switch (data.getChar(EXTRA_DATA_OBJECT_TYPE)) {
            case CommentairesTable.TYPE_PUBLICATION: { // Publication comments
                mIdsPub.put(uri, data.getString(EXTRA_DATA_OBJECT_IDS));
                break;
            }
            case CommentairesTable.TYPE_PHOTO: { // Photo comments
                mIdsPhoto.put(uri, data.getString(EXTRA_DATA_OBJECT_IDS));
                break;
            }
        }
    }
    @Override
    public void unregister(Uri uri) {
        Logs.add(Logs.Type.V, "uri: " + uri);

        mIdsPub.remove(uri);
        mIdsPhoto.remove(uri);
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

        // Publication comments
        StringBuilder ids = new StringBuilder();
        for (HashMap.Entry<Uri, String> uriIds : mIdsPub.entrySet()) {
            if (ids.length() != 0)
                ids.append(WebServices.LIST_SEPARATOR);

            ids.append(uriIds.getValue());
        }
        if (ids.length() != 0) {
            ContentValues postData = new ContentValues();
            postData.put(WebServices.COMMENTS_DATA_IDS, ids.toString());
            postData.put(WebServices.COMMENTS_DATA_TYPE, String.valueOf(CommentairesTable.TYPE_PUBLICATION));

            // Get max status date criteria (according selected IDs & publication type)
            syncData.putString(DataTable.DATA_KEY_STATUS_DATE,
                    CommentairesTable.getMaxStatusDate(mService.getContentResolver(),
                            CommentairesTable.TYPE_PUBLICATION, ids.toString()));

            // Publication comments synchronization (from remote to local DB)
            synchronized (Database.getTable(CommentairesTable.TABLE_NAME)) {
                result = Database.getTable(CommentairesTable.TABLE_NAME)
                        .synchronize(mService.getContentResolver(), WebServices.OPERATION_SELECT,
                                syncData, postData);
            }
            if (DataTable.SyncResult.hasChanged(result)) {

                Logs.add(Logs.Type.I, "Remote table #" + mTableId + " has changed");
                notifyChange(); // Notify DB change to observer URI
            }
        }

        // Photo comments
        ids = new StringBuilder();
        for (HashMap.Entry<Uri, String> uriIds : mIdsPhoto.entrySet()) {
            if (ids.length() != 0)
                ids.append(WebServices.LIST_SEPARATOR);

            ids.append(uriIds.getValue());
        }
        if (ids.length() != 0) {
            ContentValues postData = new ContentValues();
            postData.put(WebServices.COMMENTS_DATA_IDS, ids.toString());
            postData.put(WebServices.COMMENTS_DATA_TYPE, String.valueOf(CommentairesTable.TYPE_PHOTO));

            // Get max status date criteria (according selected IDs & photo type)
            syncData.putString(DataTable.DATA_KEY_STATUS_DATE,
                    CommentairesTable.getMaxStatusDate(mService.getContentResolver(),
                            CommentairesTable.TYPE_PHOTO, ids.toString()));

            // Photo comments synchronization (from remote to local DB)
            synchronized (Database.getTable(CommentairesTable.TABLE_NAME)) {
                result = Database.getTable(CommentairesTable.TABLE_NAME)
                        .synchronize(mService.getContentResolver(), WebServices.OPERATION_SELECT,
                                syncData, postData);
            }
            if (DataTable.SyncResult.hasChanged(result)) {

                Logs.add(Logs.Type.I, "Remote table #" + mTableId + " has changed");
                notifyChange(); // Notify DB change to observer URI
            }
        }
        return Result.NOT_FOUND; // Unused
    }
}
