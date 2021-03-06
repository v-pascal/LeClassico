package com.studio.artaban.leclassico.data.tables;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.codes.WebServices;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by pascal on 08/08/16.
 * Notifications database table class
 */
public class NotificationsTable extends DataTable {

    public static final char TYPE_SHARED = 'S'; // Photo added into the user shared album
    public static final char TYPE_WALL = 'W'; // Publication added onto the user wall
    public static final char TYPE_MAIL = 'M'; // Mail received
    public static final char TYPE_PUB_COMMENT = 'A'; // User publication commented
    public static final char TYPE_PIC_COMMENT = 'P'; // User photo commented
    // Notification types

    private static final short DEFAULT_LIMIT = 25; // Default remote DB query limit

    public static class Pin extends DataField { //////////////////////////////// Notifications entry

        public Pin(short count, long id) { super(count, id); }
        public Pin(Parcel parcel) {

            super(parcel);
            Logs.add(Logs.Type.V, "parcel: " + parcel);
        }
        public static final Parcelable.Creator<Pin> CREATOR = new Creator<Pin>() {

            @Override public Pin createFromParcel(Parcel source) { return new Pin(source); }
            @Override public Pin[] newArray(int size) { return new Pin[size]; }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int insert(SQLiteDatabase db, Object[] data) {
        return 0;
    }
    @Override
    public boolean update(SQLiteDatabase db, Object data) {
        return false;
    }
    @Override
    public int delete(SQLiteDatabase db, long[] keys) {
        return 0;
    }
    @Override
    public int getEntryCount(SQLiteDatabase db) {
        return 0;
    }
    @Override
    public <T> List<T> getAllEntries(SQLiteDatabase db) {
        return null;
    }

    //////
    public static final String TABLE_NAME = "Notifications";

    // Columns
    public static final String COLUMN_PSEUDO = "NOT_Pseudo";
    public static final String COLUMN_DATE = "NOT_Date";
    public static final String COLUMN_OBJECT_TYPE = "NOT_ObjType";
    public static final String COLUMN_OBJECT_ID = "NOT_ObjID";
    public static final String COLUMN_OBJECT_DATE = "NOT_ObjDate";
    public static final String COLUMN_OBJECT_FROM = "NOT_ObjFrom";
    public static final String COLUMN_LU_FLAG = "NOT_LuFlag";
    private static final String COLUMN_STATUS_DATE = "NOT_StatusDate";

    // Columns index
    private static final short COLUMN_INDEX_PSEUDO = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_DATE = 2;
    private static final short COLUMN_INDEX_OBJECT_TYPE = 3;
    private static final short COLUMN_INDEX_OBJECT_ID = 4;
    private static final short COLUMN_INDEX_OBJECT_DATE = 5;
    private static final short COLUMN_INDEX_OBJECT_FROM = 6;
    private static final short COLUMN_INDEX_LU_FLAG = 7;
    private static final short COLUMN_INDEX_STATUS_DATE = 8;
    private static final short COLUMN_INDEX_SYNCHRONIZED = 9;

    //
    private NotificationsTable() { }
    public static NotificationsTable newInstance() { return new NotificationsTable(); }

    @Override
    public void create(SQLiteDatabase db) {

        Logs.add(Logs.Type.V, "db: " + db);
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                DataField.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                COLUMN_PSEUDO + " TEXT NOT NULL," +
                COLUMN_DATE + " TEXT NOT NULL," +
                COLUMN_OBJECT_TYPE + " TEXT NOT NULL," +
                COLUMN_OBJECT_ID + " INTEGER," +
                COLUMN_OBJECT_DATE + " TEXT," +
                COLUMN_OBJECT_FROM + " TEXT NOT NULL," +
                COLUMN_LU_FLAG + " INTEGER NOT NULL," +

                Constants.DATA_COLUMN_STATUS_DATE + " TEXT NOT NULL," +
                Constants.DATA_COLUMN_SYNCHRONIZED + " INTEGER NOT NULL" +

                ");");

        // Add indexes
        db.execSQL("CREATE INDEX " + TABLE_NAME + JSON_KEY_PSEUDO + " ON " +
                TABLE_NAME + '(' + COLUMN_PSEUDO + ')');
    }
    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Logs.add(Logs.Type.V, "db: " + db);
        Logs.add(Logs.Type.W, "Upgrade '" + TABLE_NAME + "' table from " + oldVersion + " to " +
                newVersion + " version: old data will be destroyed!");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        create(db);
    }

    ////// DataTable ///////////////////////////////////////////////////////////////////////////////
    @Override
    public ContentValues syncInserted(ContentResolver resolver, String pseudo) {
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";pseudo: " + pseudo);
        ContentValues inserted = new ContentValues();
        return inserted; // Not needed for notifications table
    }
    @Override
    public ContentValues syncUpdated(ContentResolver resolver, String pseudo) {
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";pseudo: " + pseudo);

        ContentValues updated = new ContentValues(); // Empty (size == 0)
        Cursor cursor = resolver.query(Uri.parse(DataProvider.CONTENT_URI + TABLE_NAME), null,
                COLUMN_PSEUDO + '=' + DatabaseUtils.sqlEscapeString(pseudo) + " AND (" +
                        Constants.DATA_COLUMN_SYNCHRONIZED + '=' + Synchronized.TO_UPDATE.getValue() + " OR " +
                        Constants.DATA_COLUMN_SYNCHRONIZED + '=' + (Synchronized.TO_UPDATE.getValue() |
                        Synchronized.IN_PROGRESS.getValue()) + ')', null, null);
        if (cursor.moveToFirst()) {
            try {

                JSONArray keysArray = new JSONArray();
                JSONArray statusArray = new JSONArray();
                JSONArray updatesArray = new JSONArray();
                do {

                    // Keys
                    JSONObject key = new JSONObject();
                    key.put(JSON_KEY_PSEUDO, cursor.getString(COLUMN_INDEX_PSEUDO));
                    key.put(JSON_KEY_DATE, cursor.getString(COLUMN_INDEX_DATE));
                    key.put(JSON_KEY_OBJECT_TYPE, cursor.getString(COLUMN_INDEX_OBJECT_TYPE));
                    key.put(JSON_KEY_OBJECT_ID, (!cursor.isNull(COLUMN_INDEX_OBJECT_ID)) ?
                            cursor.getInt(COLUMN_INDEX_OBJECT_ID) : JSONObject.NULL);
                    key.put(JSON_KEY_OBJECT_DATE, (!cursor.isNull(COLUMN_INDEX_OBJECT_DATE)) ?
                            cursor.getString(COLUMN_INDEX_OBJECT_DATE) : JSONObject.NULL);
                    key.put(JSON_KEY_OBJECT_FROM, cursor.getString(COLUMN_INDEX_OBJECT_FROM));

                    // Status
                    JSONObject state = new JSONObject();
                    state.put(JSON_KEY_STATUS_DATE, cursor.getString(COLUMN_INDEX_STATUS_DATE));
                    // TODO: Implement local and remote time lag here ?!?!

                    // Updates
                    JSONObject update = new JSONObject();
                    update.put(JSON_KEY_LU_FLAG, cursor.getInt(COLUMN_INDEX_LU_FLAG));

                    //////
                    keysArray.put(key);
                    statusArray.put(state);
                    updatesArray.put(update);

                } while (cursor.moveToNext());

                //////
                //Logs.add(Logs.Type.I, "Keys: " + keysArray.toString());
                //Logs.add(Logs.Type.I, "Status: " + statusArray.toString());
                //Logs.add(Logs.Type.I, "Updates: " + updatesArray.toString());

                updated.put(WebServices.DATA_KEYS, keysArray.toString());
                updated.put(WebServices.DATA_STATUS, statusArray.toString());
                updated.put(WebServices.DATA_UPDATES, updatesArray.toString());

            } catch (JSONException e) {
                Logs.add(Logs.Type.F, "Unexpected error: " + e.getMessage());
            }
        }
        cursor.close();
        return updated;
    }
    @Override
    public ContentValues syncDeleted(ContentResolver resolver, String pseudo) {
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";pseudo: " + pseudo);
        ContentValues deleted = new ContentValues();
        return deleted; // Not needed for notifications table
    }

    // JSON keys
    private static final String JSON_KEY_PSEUDO = COLUMN_PSEUDO.substring(4);
    private static final String JSON_KEY_DATE = COLUMN_DATE.substring(4);
    private static final String JSON_KEY_OBJECT_TYPE = COLUMN_OBJECT_TYPE.substring(4);
    private static final String JSON_KEY_OBJECT_ID = COLUMN_OBJECT_ID.substring(4);
    private static final String JSON_KEY_OBJECT_DATE = COLUMN_OBJECT_DATE.substring(4);
    private static final String JSON_KEY_OBJECT_FROM = COLUMN_OBJECT_FROM.substring(4);
    private static final String JSON_KEY_LU_FLAG = COLUMN_LU_FLAG.substring(4);
    private static final String JSON_KEY_STATUS_DATE = COLUMN_STATUS_DATE.substring(4);

    @Override
    public @Nullable SyncResult synchronize(final ContentResolver resolver, final byte operation,
                                            Bundle syncData, @Nullable ContentValues postData) {

        // Synchronize data from remote to local DB (return inserted, deleted or
        // updated entry count & NO_DATA if error)
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";operation: " + operation +
                ";syncData: " + syncData + ";postData: " + postData);

        final SyncResult syncResult = new SyncResult();

        syncData.putString(DATA_KEY_WEB_SERVICE, WebServices.URL_NOTIFICATIONS);
        syncData.putByte(DATA_KEY_OPERATION, operation);
        syncData.putString(DATA_KEY_TABLE_NAME, TABLE_NAME);
        if ((syncData.containsKey(DATA_KEY_LIMIT)) && (syncData.getShort(DATA_KEY_LIMIT) == 0))
            syncData.putShort(DATA_KEY_LIMIT, DEFAULT_LIMIT);

        syncData.putString(DATA_KEY_FIELD_PSEUDO, COLUMN_PSEUDO);
        syncData.putString(DATA_KEY_FIELD_DATE, COLUMN_DATE);
        String url = getSyncUrlRequest(resolver, syncData);

        // Send remote DB request
        Internet.DownloadResult result = Internet.downloadHttpRequest(url, postData, null,
                new Internet.OnRequestListener() {

                    @Override
                    public boolean onReceiveReply(String response) {
                        //Logs.add(Logs.Type.V, "response: " + response);
                        try {

                            JSONObject reply = new JSONObject(response);
                            if (!reply.has(WebServices.JSON_KEY_ERROR)) { // Check no web service error

                                if (reply.isNull(TABLE_NAME))
                                    return ((operation == WebServices.OPERATION_SELECT) ||
                                            (operation == WebServices.OPERATION_SELECT_OLD));
                                    // Already synchronized for selection but error for any other operation

                                Uri tableUri = Uri.parse(DataProvider.CONTENT_URI + TABLE_NAME);
                                JSONArray entries = reply.getJSONArray(TABLE_NAME);
                                for (int i = 0; i < entries.length(); ++i) {

                                    JSONObject entry = (JSONObject) entries.get(i);

                                    // Key fields
                                    String pseudo = entry.getString(JSON_KEY_PSEUDO);
                                    String date = entry.getString(JSON_KEY_DATE);
                                    String objType = entry.getString(JSON_KEY_OBJECT_TYPE);
                                    int objID = Constants.NO_DATA;
                                    if (!entry.isNull(JSON_KEY_OBJECT_ID))
                                        objID = entry.getInt(JSON_KEY_OBJECT_ID);
                                    String objDate = null;
                                    if (!entry.isNull(JSON_KEY_OBJECT_DATE))
                                        objDate = entry.getString(JSON_KEY_OBJECT_DATE);
                                    String objFrom = entry.getString(JSON_KEY_OBJECT_FROM);

                                    // Data fields
                                    ContentValues values = new ContentValues();
                                    values.put(COLUMN_LU_FLAG, entry.getInt(JSON_KEY_LU_FLAG));

                                    values.put(Constants.DATA_COLUMN_STATUS_DATE, entry.getString(JSON_KEY_STATUS_DATE));
                                    values.put(Constants.DATA_COLUMN_SYNCHRONIZED, Synchronized.DONE.getValue());

                                    // Check if entry already exists
                                    String selection = COLUMN_PSEUDO + '=' + DatabaseUtils.sqlEscapeString(pseudo) +
                                            " AND " + COLUMN_DATE + "='" + date + '\'' +
                                            " AND " + COLUMN_OBJECT_TYPE + "='" + objType + '\'' +
                                            " AND " + COLUMN_OBJECT_ID +
                                            ((objID != Constants.NO_DATA)? "=" + objID:IS_NULL) +
                                            " AND " + COLUMN_OBJECT_FROM + '=' + DatabaseUtils.sqlEscapeString(objFrom) +
                                            " AND " + COLUMN_OBJECT_DATE +
                                            ((objDate != null)? "='" + objDate + '\'':IS_NULL);
                                    Cursor cursor = resolver.query(tableUri, new String[]{Constants.DATA_COLUMN_STATUS_DATE},
                                            selection, null, null);
                                    if (cursor.moveToFirst()) { // DB entry exists

                                        if (entry.getInt(WebServices.JSON_KEY_STATUS) == STATUS_FIELD_DELETED) {
                                            // NB: Web site deletion priority (no status date comparison)

                                            ////// Delete entry (not definitively to keep last status date)
                                            values.put(Constants.DATA_COLUMN_SYNCHRONIZED,
                                                    Synchronized.DELETED.getValue());
                                            resolver.update(tableUri, values, selection, null);

                                            ++syncResult.deleted;

                                        } else if (cursor.getString(0)
                                                .compareTo(entry.getString(JSON_KEY_STATUS_DATE)) < 0) {

                                            ////// Update entry
                                            resolver.update(tableUri, values, selection, null);
                                            ++syncResult.updated;

                                        }
                                        //else // Nothing to do here (let's synchronize from local to remote DB)

                                    } else if (entry.getInt(WebServices.JSON_KEY_STATUS) != STATUS_FIELD_DELETED) {

                                        ////// Insert entry into DB
                                        values.put(COLUMN_PSEUDO, pseudo);
                                        values.put(COLUMN_DATE, date);
                                        values.put(COLUMN_OBJECT_TYPE, objType);
                                        if (objID != Constants.NO_DATA)
                                            values.put(COLUMN_OBJECT_ID, objID);
                                        else
                                            values.putNull(COLUMN_OBJECT_ID);
                                        values.put(COLUMN_OBJECT_FROM, objFrom);
                                        values.put(COLUMN_OBJECT_DATE, objDate);
                                        resolver.insert(tableUri, values);

                                        ++syncResult.inserted;
                                    }
                                    //else // Do not add a deleted entry (created & removed when offline)
                                    cursor.close();
                                }

                            } else {
                                Logs.add(Logs.Type.E, "Synchronization error: #" +
                                        reply.getInt(WebServices.JSON_KEY_ERROR));
                                return false;
                            }

                        } catch (JSONException e) {
                            Logs.add(Logs.Type.F, "Unexpected connection reply: " + e.getMessage());
                            return false;
                        }
                        return true;
                    }
                });
        if (result != Internet.DownloadResult.SUCCEEDED) {

            Logs.add(Logs.Type.E, "Table '" + TABLE_NAME + "' synchronization request error");
            if ((operation != WebServices.OPERATION_SELECT) && (operation != WebServices.OPERATION_SELECT_OLD))
                resetSyncInProgress(resolver, syncData);
            return null;
        }
        return syncResult;
    }
}
