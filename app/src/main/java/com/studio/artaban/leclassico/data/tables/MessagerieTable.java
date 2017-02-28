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
 * Messagerie database table class
 */
public class MessagerieTable extends DataTable {

    private static final short DEFAULT_LIMIT = 10; // Default remote DB query limit

    public static class Message extends DataField { /////////////////////////////// Messagerie entry

        public Message(short count, long id) { super(count, id); }
        public Message(Parcel parcel) {

            super(parcel);
            Logs.add(Logs.Type.V, "parcel: " + parcel);
        }
        public static final Parcelable.Creator<Message> CREATOR = new Creator<Message>() {

            @Override public Message createFromParcel(Parcel source) { return new Message(source); }
            @Override public Message[] newArray(int size) { return new Message[size]; }
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
    public static final String TABLE_NAME = "Messagerie";

    // Columns
    public static final String COLUMN_PSEUDO = "MSG_Pseudo";
    public static final String COLUMN_FROM = "MSG_From";
    public static final String COLUMN_MESSAGE = "MSG_Message";
    public static final String COLUMN_DATE = "MSG_Date";
    public static final String COLUMN_TIME = "MSG_Time";
    public static final String COLUMN_LU_FLAG = "MSG_LuFlag";
    public static final String COLUMN_READ_STK = "MSG_ReadStk";
    public static final String COLUMN_WRITE_STK = "MSG_WriteStk";
    public static final String COLUMN_OBJET = "MSG_Objet";
    private static final String COLUMN_STATUS_DATE = "MSG_StatusDate";

    // Columns index
    private static final short COLUMN_INDEX_PSEUDO = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_FROM = 2;
    private static final short COLUMN_INDEX_MESSAGE = 3;
    private static final short COLUMN_INDEX_DATE = 4;
    private static final short COLUMN_INDEX_TIME = 5;
    private static final short COLUMN_INDEX_LU_FLAG = 6;
    private static final short COLUMN_INDEX_READ_STK = 7;
    private static final short COLUMN_INDEX_WRITE_STK = 8;
    private static final short COLUMN_INDEX_OBJET = 9;
    private static final short COLUMN_INDEX_STATUS_DATE = 10;
    private static final short COLUMN_INDEX_SYNCHRONIZED = 11;

    //
    private MessagerieTable() { }
    public static MessagerieTable newInstance() { return new MessagerieTable(); }

    @Override
    public void create(SQLiteDatabase db) {

        Logs.add(Logs.Type.V, "db: " + db);
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                DataField.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                COLUMN_PSEUDO + " TEXT NOT NULL," +
                COLUMN_FROM + " TEXT NOT NULL," +
                COLUMN_MESSAGE + " TEXT NOT NULL," +
                COLUMN_DATE + " TEXT NOT NULL," +
                COLUMN_TIME + " TEXT NOT NULL," +
                COLUMN_LU_FLAG + " INTEGER NOT NULL," +
                COLUMN_READ_STK + " INTEGER NOT NULL," +
                COLUMN_WRITE_STK + " INTEGER NOT NULL," +
                COLUMN_OBJET + " TEXT," +

                Constants.DATA_COLUMN_STATUS_DATE + " TEXT NOT NULL," +
                Constants.DATA_COLUMN_SYNCHRONIZED + " INTEGER NOT NULL" +

                ");");

        // Add indexes
        db.execSQL("CREATE INDEX " + TABLE_NAME + JSON_KEY_FROM + " ON " +
                TABLE_NAME + '(' + COLUMN_FROM + ')');
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
        return inserted;
    }
    @Override
    public ContentValues syncUpdated(ContentResolver resolver, String pseudo) {
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";pseudo: " + pseudo);




        ContentValues updated = new ContentValues();
        return updated;
    }
    @Override
    public ContentValues syncDeleted(ContentResolver resolver, String pseudo) {
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";pseudo: " + pseudo);




        ContentValues deleted = new ContentValues();
        return deleted;
    }

    // JSON keys
    private static final String JSON_KEY_PSEUDO = COLUMN_PSEUDO.substring(4);
    private static final String JSON_KEY_FROM = COLUMN_FROM.substring(4);
    private static final String JSON_KEY_MESSAGE = COLUMN_MESSAGE.substring(4);
    private static final String JSON_KEY_DATE = COLUMN_DATE.substring(4);
    private static final String JSON_KEY_TIME = COLUMN_TIME.substring(4);
    private static final String JSON_KEY_LU_FLAG = COLUMN_LU_FLAG.substring(4);
    private static final String JSON_KEY_READ_STK = COLUMN_READ_STK.substring(4);
    private static final String JSON_KEY_WRITE_STK = COLUMN_WRITE_STK.substring(4);
    private static final String JSON_KEY_OBJET = COLUMN_OBJET.substring(4);
    private static final String JSON_KEY_STATUS_DATE = COLUMN_STATUS_DATE.substring(4);

    @Override
    public @Nullable SyncResult synchronize(final ContentResolver resolver, final byte operation,
                                            Bundle syncData, @Nullable ContentValues postData) {

        // Synchronize data from remote to local DB (return inserted, deleted or
        // updated entry count & NO_DATA if error)
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";operation: " + operation +
                ";syncData: " + syncData + ";postData: " + postData);

        final SyncResult syncResult = new SyncResult();

        syncData.putString(DATA_KEY_WEB_SERVICE, WebServices.URL_MESSAGERIE);
        syncData.putByte(DATA_KEY_OPERATION, operation);
        syncData.putString(DATA_KEY_TABLE_NAME, TABLE_NAME);
        if ((syncData.containsKey(DATA_KEY_LIMIT)) && (syncData.getShort(DATA_KEY_LIMIT) == 0))
            syncData.putShort(DATA_KEY_LIMIT, DEFAULT_LIMIT);

        syncData.putString(DATA_KEY_FIELD_PSEUDO, COLUMN_PSEUDO);
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
                                    String time = entry.getString(JSON_KEY_TIME);

                                    // Data fields
                                    ContentValues values = new ContentValues();
                                    values.put(COLUMN_FROM, entry.getString(JSON_KEY_FROM));
                                    values.put(COLUMN_MESSAGE, entry.getString(JSON_KEY_MESSAGE));
                                    values.put(COLUMN_LU_FLAG, entry.getInt(JSON_KEY_LU_FLAG));
                                    values.put(COLUMN_READ_STK, entry.getInt(JSON_KEY_READ_STK));
                                    values.put(COLUMN_WRITE_STK, entry.getInt(JSON_KEY_WRITE_STK));
                                    if (!entry.isNull(JSON_KEY_OBJET))
                                        values.put(COLUMN_OBJET, entry.getString(JSON_KEY_OBJET));

                                    values.put(Constants.DATA_COLUMN_STATUS_DATE, entry.getString(JSON_KEY_STATUS_DATE));
                                    values.put(Constants.DATA_COLUMN_SYNCHRONIZED, Synchronized.DONE.getValue());

                                    // Check if entry already exists
                                    String selection = COLUMN_PSEUDO + '=' + DatabaseUtils.sqlEscapeString(pseudo) +
                                            " AND " + COLUMN_DATE + "='" + date + '\'' +
                                            " AND " + COLUMN_TIME + "='" + time + '\'';
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
                                        values.put(COLUMN_TIME, time);
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
