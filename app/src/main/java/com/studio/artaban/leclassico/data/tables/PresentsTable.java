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
 * Created by pascal on 28/07/16.
 * Presents database table class
 */
public class PresentsTable extends DataTable {

    public static class Reveler extends DataField { ///////////////////////////////// Presents entry

        public Reveler(short count, long id) { super(count, id); }
        public Reveler(Parcel parcel) {

            super(parcel);
            Logs.add(Logs.Type.V, "parcel: " + parcel);
        }
        public static final Parcelable.Creator<Reveler> CREATOR = new Creator<Reveler>() {

            @Override public Reveler createFromParcel(Parcel source) { return new Reveler(source); }
            @Override public Reveler[] newArray(int size) { return new Reveler[size]; }
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
    public static final String TABLE_NAME = "Presents";

    // Columns
    public static final String COLUMN_EVENT_ID = "PRE_EventID";
    public static final String COLUMN_PSEUDO = "PRE_Pseudo";
    private static final String COLUMN_STATUS_DATE = "PRE_StatusDate";

    // Columns index
    private static final short COLUMN_INDEX_EVENT_ID = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_PSEUDO = 2;
    private static final short COLUMN_INDEX_STATUS_DATE = 3;
    private static final short COLUMN_INDEX_SYNCHRONIZED = 4;

    //
    private PresentsTable() { }
    public static PresentsTable newInstance() { return new PresentsTable(); }

    @Override
    public void create(SQLiteDatabase db) {

        Logs.add(Logs.Type.V, "db: " + db);
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                DataField.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                COLUMN_EVENT_ID + " INTEGER NOT NULL," +
                COLUMN_PSEUDO + " TEXT NOT NULL," +

                Constants.DATA_COLUMN_STATUS_DATE + " TEXT NOT NULL," +
                Constants.DATA_COLUMN_SYNCHRONIZED + " INTEGER NOT NULL" +

                ");");

        // Add indexes
        db.execSQL("CREATE INDEX " + TABLE_NAME + JSON_KEY_EVENT_ID + " ON " +
                TABLE_NAME + '(' + COLUMN_EVENT_ID + ')');
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
        Cursor cursor = resolver.query(Uri.parse(DataProvider.CONTENT_URI + TABLE_NAME), null,
                COLUMN_PSEUDO + '=' + DatabaseUtils.sqlEscapeString(pseudo) + " AND (" +
                        Constants.DATA_COLUMN_SYNCHRONIZED + '=' + Synchronized.TO_INSERT.getValue() + " OR " +
                        Constants.DATA_COLUMN_SYNCHRONIZED + '=' + (Synchronized.TO_INSERT.getValue() |
                        Synchronized.IN_PROGRESS.getValue()) + ')', null, null);
        if (cursor.moveToFirst()) {
            try {

                JSONArray keysArray = new JSONArray();
                JSONArray statusArray = new JSONArray();
                do {

                    // Keys
                    JSONObject key = new JSONObject();
                    key.put(JSON_KEY_EVENT_ID, cursor.getInt(COLUMN_INDEX_EVENT_ID));
                    key.put(JSON_KEY_PSEUDO, cursor.getString(COLUMN_INDEX_PSEUDO));

                    // Status
                    JSONObject state = new JSONObject();
                    state.put(JSON_KEY_STATUS_DATE, cursor.getString(COLUMN_INDEX_STATUS_DATE));
                    // TODO: Implement local and remote time lag here ?!?!

                    //////
                    keysArray.put(key);
                    statusArray.put(state);

                } while (cursor.moveToNext());

                //////
                //Logs.add(Logs.Type.I, "Keys: " + keysArray.toString());
                //Logs.add(Logs.Type.I, "Status: " + statusArray.toString());

                inserted.put(WebServices.DATA_KEYS, keysArray.toString());
                inserted.put(WebServices.DATA_STATUS, statusArray.toString());

            } catch (JSONException e) {
                Logs.add(Logs.Type.F, "Unexpected error: " + e.getMessage());
            }
        }
        cursor.close();
        return inserted;
    }
    @Override
    public ContentValues syncUpdated(ContentResolver resolver, String pseudo) {
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";pseudo: " + pseudo);
        ContentValues updated = new ContentValues(); // Not needed for presents table
        return updated;
    }
    @Override
    public ContentValues syncDeleted(ContentResolver resolver, String pseudo) {
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";pseudo: " + pseudo);

        ContentValues deleted = new ContentValues();
        Cursor cursor = resolver.query(Uri.parse(DataProvider.CONTENT_URI + TABLE_NAME), null,
                COLUMN_PSEUDO + '=' + DatabaseUtils.sqlEscapeString(pseudo) + " AND (" +
                        Constants.DATA_COLUMN_SYNCHRONIZED + '=' + Synchronized.TO_DELETE.getValue() + " OR " +
                        Constants.DATA_COLUMN_SYNCHRONIZED + '=' + (Synchronized.TO_DELETE.getValue() |
                        Synchronized.IN_PROGRESS.getValue()) + ')', null, null);
        if (cursor.moveToFirst()) {
            try {

                JSONArray keysArray = new JSONArray();
                JSONArray statusArray = new JSONArray();
                do {

                    // Keys
                    JSONObject key = new JSONObject();
                    key.put(JSON_KEY_EVENT_ID, cursor.getInt(COLUMN_INDEX_EVENT_ID));
                    key.put(JSON_KEY_PSEUDO, cursor.getString(COLUMN_INDEX_PSEUDO));

                    // Status
                    JSONObject state = new JSONObject();
                    state.put(JSON_KEY_STATUS_DATE, cursor.getString(COLUMN_INDEX_STATUS_DATE));
                    // TODO: Implement local and remote time lag here ?!?!

                    //////
                    keysArray.put(key);
                    statusArray.put(state);

                } while (cursor.moveToNext());

                //////
                //Logs.add(Logs.Type.I, "Keys: " + keysArray.toString());
                //Logs.add(Logs.Type.I, "Status: " + statusArray.toString());

                deleted.put(WebServices.DATA_KEYS, keysArray.toString());
                deleted.put(WebServices.DATA_STATUS, statusArray.toString());

            } catch (JSONException e) {
                Logs.add(Logs.Type.F, "Unexpected error: " + e.getMessage());
            }
        }
        cursor.close();
        return deleted;
    }

    // JSON keys
    private static final String JSON_KEY_EVENT_ID = COLUMN_EVENT_ID.substring(4);
    private static final String JSON_KEY_PSEUDO = COLUMN_PSEUDO.substring(4);
    private static final String JSON_KEY_STATUS_DATE = COLUMN_STATUS_DATE.substring(4);

    @Override
    public @Nullable SyncResult synchronize(final ContentResolver resolver, final byte operation,
                                            Bundle syncData, @Nullable ContentValues postData) {

        // Synchronize data from remote to local DB (return inserted, deleted or
        // updated entry count & NO_DATA if error)
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";operation: " + operation +
                ";syncData: " + syncData + ";postData: " + postData);

        final SyncResult syncResult = new SyncResult();

        syncData.putString(DATA_KEY_WEB_SERVICE, WebServices.URL_PRESENTS);
        syncData.putByte(DATA_KEY_OPERATION, operation);
        syncData.putString(DATA_KEY_TABLE_NAME, TABLE_NAME);

        syncData.remove(DATA_KEY_FIELD_PSEUDO); // No pseudo field criteria for this table
        syncData.remove(DATA_KEY_FIELD_DATE); // No date field criteria for this table
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
                                    return (operation == WebServices.OPERATION_SELECT);
                                    // Already synchronized for selection but error for any other operation

                                Uri tableUri = Uri.parse(DataProvider.CONTENT_URI + TABLE_NAME);
                                JSONArray entries = reply.getJSONArray(TABLE_NAME);
                                for (int i = 0; i < entries.length(); ++i) {

                                    JSONObject entry = (JSONObject) entries.get(i);

                                    // Key fields
                                    int eventId = entry.getInt(JSON_KEY_EVENT_ID);
                                    String pseudo = entry.getString(JSON_KEY_PSEUDO);

                                    ContentValues values = new ContentValues();
                                    values.put(Constants.DATA_COLUMN_STATUS_DATE, entry.getString(JSON_KEY_STATUS_DATE));
                                    values.put(Constants.DATA_COLUMN_SYNCHRONIZED, Synchronized.DONE.getValue());

                                    // Check if entry already exists
                                    String selection = COLUMN_EVENT_ID + '=' + eventId + " AND " +
                                            COLUMN_PSEUDO + "='" + pseudo + '\'';
                                    Cursor cursor = resolver.query(tableUri, null, selection, null, null);
                                    if (cursor.moveToFirst()) { // DB entry exists

                                        if (entry.getInt(WebServices.JSON_KEY_STATUS) == STATUS_FIELD_DELETED) {
                                            // NB: Web site deletion priority (no status date comparison)

                                            ////// Delete entry (not definitively to keep last status date)
                                            values.put(Constants.DATA_COLUMN_SYNCHRONIZED,
                                                    Synchronized.DELETED.getValue());
                                            resolver.update(tableUri, values, selection, null);

                                            ++syncResult.deleted;

                                        } else { ////// Update status fields (for new entry)

                                            resolver.update(tableUri, values, selection, null);
                                            ++syncResult.updated; // Same as inserted (re-inserted)
                                        }

                                    } else if (entry.getInt(WebServices.JSON_KEY_STATUS) != STATUS_FIELD_DELETED) {

                                        ////// Insert entry into DB
                                        values.put(COLUMN_EVENT_ID, eventId);
                                        values.put(COLUMN_PSEUDO, pseudo);
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
            if (operation != WebServices.OPERATION_SELECT)
                resetSyncInProgress(resolver, syncData);
            return null;
        }
        return syncResult;
    }
}
