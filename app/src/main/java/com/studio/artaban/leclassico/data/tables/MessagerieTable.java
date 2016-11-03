package com.studio.artaban.leclassico.data.tables;

import android.content.ContentResolver;
import android.content.ContentValues;
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
                TABLE_NAME + "(" + COLUMN_FROM + ")");
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
    public ContentValues syncInserted(ContentResolver resolver, String token, String pseudo) {
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";token: " + token + ";pseudo: " + pseudo);




        ContentValues inserted = new ContentValues();
        return inserted;
    }
    @Override
    public ContentValues syncUpdated(ContentResolver resolver, String token, String pseudo) {
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";token: " + token + ";pseudo: " + pseudo);




        ContentValues updated = new ContentValues();
        return updated;
    }
    @Override
    public ContentValues syncDeleted(ContentResolver resolver, String token, String pseudo) {
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";token: " + token + ";pseudo: " + pseudo);




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
    public SyncResult synchronize(final ContentResolver resolver, String token, byte operation,
                                  @Nullable String pseudo, @Nullable Short limit,
                                  @Nullable ContentValues postData) {

        // Synchronize data from remote to local DB (return inserted, deleted or
        // updated entry count & NO_DATA if error)
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";token: " + token + ";operation: " + operation +
                ";pseudo: " + pseudo + ";limit: " + limit + ";postData: " + postData);

        final SyncResult syncResult = new SyncResult();
        Bundle data = new Bundle();

        data.putString(DataTable.DATA_KEY_WEB_SERVICE, WebServices.URL_MESSAGERIE);
        data.putString(DataTable.DATA_KEY_TOKEN, token);
        data.putByte(DataTable.DATA_KEY_OPERATION, operation);
        data.putString(DataTable.DATA_KEY_PSEUDO, pseudo);
        data.putString(DataTable.DATA_KEY_TABLE_NAME, TABLE_NAME);
        data.putString(DataTable.DATA_KEY_FIELD_PSEUDO, COLUMN_PSEUDO);
        String url = getSyncUrlRequest(resolver, data);

        // Send remote DB request
        Internet.DownloadResult result = Internet.downloadHttpRequest(url, postData,
                new Internet.OnRequestListener() {

                    @Override
                    public boolean onReceiveReply(String response) {
                        //Logs.add(Logs.Type.V, "response: " + response);
                        try {

                            JSONObject reply = new JSONObject(response);
                            if (!reply.has(WebServices.JSON_KEY_ERROR)) { // Check no web service error

                                if (reply.isNull(TABLE_NAME))
                                    return true; // Already synchronized

                                Uri tableUri = Uri.parse(DataProvider.CONTENT_URI + TABLE_NAME);
                                JSONArray entries = reply.getJSONArray(TABLE_NAME);
                                for (int i = 0; i < entries.length(); ++i) {

                                    JSONObject entry = (JSONObject) entries.get(i);
                                    String pseudo = entry.getString(JSON_KEY_PSEUDO);
                                    String date = entry.getString(JSON_KEY_DATE);
                                    String time = entry.getString(JSON_KEY_TIME);

                                    // Entry fields
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
                                    if (DataTable.getEntryCount(resolver, TABLE_NAME, selection) > 0) { // DB entry exists

                                        if (entry.getInt(WebServices.JSON_KEY_STATUS) == STATUS_FIELD_DELETED) {

                                            // Delete entry (definitively)
                                            values.put(Constants.DATA_COLUMN_SYNCHRONIZED,
                                                    Synchronized.TO_DELETE.getValue());
                                            resolver.update(tableUri, values, selection, null);
                                            resolver.delete(tableUri,
                                                    selection + " AND " + Constants.DATA_DELETE_SELECTION, null);

                                            ++syncResult.deleted;

                                        } else { // Update entry

                                            resolver.update(tableUri, values, selection, null);
                                            ++syncResult.updated;
                                        }

                                    } else if (entry.getInt(WebServices.JSON_KEY_STATUS) != STATUS_FIELD_DELETED) {

                                        // Insert entry into DB
                                        values.put(COLUMN_PSEUDO, pseudo);
                                        values.put(COLUMN_DATE, date);
                                        values.put(COLUMN_TIME, time);
                                        resolver.insert(tableUri, values);

                                        ++syncResult.inserted;
                                    }
                                    //else // Do not add a deleted entry
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
                resetSyncInProgress(resolver, data);
            return null;
        }
        return syncResult;
    }
}
