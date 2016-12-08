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
 * Abonnements database table class
 */
public class AbonnementsTable extends DataTable {

    public static class Followed extends DataField { ///////////////////////////// Abonnements entry

        public Followed(short count, long id) { super(count, id); }
        public Followed(Parcel parcel) {

            super(parcel);
            Logs.add(Logs.Type.V, "parcel: " + parcel);
        }
        public static final Parcelable.Creator<Followed> CREATOR = new Creator<Followed>() {

            @Override public Followed createFromParcel(Parcel source) { return new Followed(source); }
            @Override public Followed[] newArray(int size) { return new Followed[size]; }
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
    public static final String TABLE_NAME = "Abonnements";

    // Columns
    public static final String COLUMN_PSEUDO = "ABO_Pseudo";
    public static final String COLUMN_CAMARADE = "ABO_Camarade";
    private static final String COLUMN_STATUS_DATE = "ABO_StatusDate";

    // Columns index
    private static final short COLUMN_INDEX_PSEUDO = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_CAMARADE = 2;
    private static final short COLUMN_INDEX_STATUS_DATE = 3;
    private static final short COLUMN_INDEX_SYNCHRONIZED = 4;

    //
    private AbonnementsTable() { }
    public static AbonnementsTable newInstance() { return new AbonnementsTable(); }

    @Override
    public void create(SQLiteDatabase db) {

        Logs.add(Logs.Type.V, "db: " + db);
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                DataField.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                COLUMN_PSEUDO + " TEXT NOT NULL," +
                COLUMN_CAMARADE + " TEXT NOT NULL," +

                Constants.DATA_COLUMN_STATUS_DATE + " TEXT NOT NULL," +
                Constants.DATA_COLUMN_SYNCHRONIZED + " INTEGER NOT NULL" +

                ");");

        // Add indexes
        db.execSQL("CREATE INDEX " + TABLE_NAME + JSON_KEY_CAMARADE + " ON " +
                TABLE_NAME + '(' + COLUMN_CAMARADE + ')');
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
    private static final String JSON_KEY_CAMARADE = COLUMN_CAMARADE.substring(4);
    private static final String JSON_KEY_STATUS_DATE = COLUMN_STATUS_DATE.substring(4);

    @Override
    public SyncResult synchronize(final ContentResolver resolver, final byte operation, Bundle syncData,
                                  @Nullable ContentValues postData) {

        // Synchronize data from remote to local DB (return inserted, deleted or
        // updated entry count & NO_DATA if error)
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";operation: " + operation +
                ";syncData: " + syncData + ";postData: " + postData);

        final SyncResult syncResult = new SyncResult();

        syncData.putString(DATA_KEY_WEB_SERVICE, WebServices.URL_FOLLOWERS);
        syncData.putByte(DATA_KEY_OPERATION, operation);
        syncData.putString(DATA_KEY_TABLE_NAME, TABLE_NAME);
        syncData.putString(DATA_KEY_FIELD_PSEUDO, COLUMN_PSEUDO);
        String url = getSyncUrlRequest(resolver, syncData);

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
                            return ((operation == WebServices.OPERATION_SELECT) ||
                                    (operation == WebServices.OPERATION_SELECT_OLD));
                            // Already synchronized for selection but error for any other operation

                        Uri tableUri = Uri.parse(DataProvider.CONTENT_URI + TABLE_NAME);
                        JSONArray entries = reply.getJSONArray(TABLE_NAME);
                        for (int i = 0; i < entries.length(); ++i) {

                            JSONObject entry = (JSONObject) entries.get(i);

                            // Key fields
                            String pseudo = entry.getString(JSON_KEY_PSEUDO);
                            String camarade = entry.getString(JSON_KEY_CAMARADE);

                            // Data fields
                            ContentValues values = new ContentValues();
                            values.put(Constants.DATA_COLUMN_STATUS_DATE, entry.getString(JSON_KEY_STATUS_DATE));
                            values.put(Constants.DATA_COLUMN_SYNCHRONIZED, Synchronized.DONE.getValue());

                            // Check if entry already exists
                            String selection = COLUMN_PSEUDO + '=' + DatabaseUtils.sqlEscapeString(pseudo) +
                                    " AND " + COLUMN_CAMARADE + '=' + DatabaseUtils.sqlEscapeString(camarade);
                            Cursor cursor = resolver.query(tableUri, new String[]{Constants.DATA_COLUMN_STATUS_DATE},
                                    selection, null, null);
                            if (cursor.moveToFirst()) { // DB entry exists

                                if (entry.getInt(WebServices.JSON_KEY_STATUS) == STATUS_FIELD_DELETED) {
                                    // NB: Web site deletion priority (no status date comparison)
                                    cursor.close();

                                    ////// Delete entry (definitively)
                                    values.put(Constants.DATA_COLUMN_SYNCHRONIZED,
                                            Synchronized.TO_DELETE.getValue());
                                    resolver.update(tableUri, values, selection, null);
                                    resolver.delete(tableUri,
                                            selection + " AND " + Constants.DATA_DELETE_SELECTION, null);

                                    ++syncResult.deleted;

                                } else if (cursor.getString(0)
                                            .compareTo(entry.getString(JSON_KEY_STATUS_DATE)) < 0) {

                                    ////// Update entry
                                    cursor.close();
                                    resolver.update(tableUri, values, selection, null);
                                    ++syncResult.updated;

                                } else // Nothing to do here (let's synchronize from local to remote DB)
                                    cursor.close();

                            } else if (entry.getInt(WebServices.JSON_KEY_STATUS) != STATUS_FIELD_DELETED) {

                                ////// Insert entry into DB
                                values.put(COLUMN_PSEUDO, pseudo);
                                values.put(COLUMN_CAMARADE, camarade);
                                resolver.insert(tableUri, values);

                                ++syncResult.inserted;
                            }
                            //else // Do not add a deleted entry (created & removed when offline)
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
