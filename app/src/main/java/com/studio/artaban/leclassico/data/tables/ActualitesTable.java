package com.studio.artaban.leclassico.data.tables;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
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
 * Actualites database table class
 */
public class ActualitesTable extends DataTable {

    public static final short TYPE_TEXT = 0;
    public static final short TYPE_LINK = 1;
    public static final short TYPE_IMAGE = 2;
    // Publication types

    private static final short DEFAULT_LIMIT = 7; // Default remote DB query limit

    public static class Publication extends DataField { /////////////////////////// Actualites entry

        public Publication(short count, long id) { super(count, id); }
        public Publication(Parcel parcel) {

            super(parcel);
            Logs.add(Logs.Type.V, "parcel: " + parcel);
        }
        public static final Parcelable.Creator<Publication> CREATOR = new Creator<Publication>() {

            @Override public Publication createFromParcel(Parcel source) { return new Publication(source); }
            @Override public Publication[] newArray(int size) { return new Publication[size]; }
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
    public static final String TABLE_NAME = "Actualites";

    // Columns
    public static final String COLUMN_ACTU_ID = "ACT_ActuID";
    public static final String COLUMN_PSEUDO = "ACT_Pseudo";
    public static final String COLUMN_DATE = "ACT_Date";
    public static final String COLUMN_CAMARADE = "ACT_Camarade";
    public static final String COLUMN_TEXT = "ACT_Text";
    public static final String COLUMN_LINK = "ACT_Link";
    public static final String COLUMN_FICHIER = "ACT_Fichier";
    private static final String COLUMN_STATUS_DATE = "ACT_StatusDate";

    // Columns index
    private static final short COLUMN_INDEX_ACTU_ID = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_PSEUDO = 2;
    private static final short COLUMN_INDEX_CAMARADE = 3;
    private static final short COLUMN_INDEX_TEXT = 4;
    private static final short COLUMN_INDEX_LINK = 5;
    private static final short COLUMN_INDEX_FICHIER = 6;
    private static final short COLUMN_INDEX_STATUS_DATE = 7;
    private static final short COLUMN_INDEX_SYNCHRONIZED = 8;

    //
    private ActualitesTable() { }
    public static ActualitesTable newInstance() { return new ActualitesTable(); }

    @Override
    public void create(SQLiteDatabase db) {

        Logs.add(Logs.Type.V, "db: " + db);
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                DataField.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                COLUMN_ACTU_ID + " INTEGER NOT NULL," +
                COLUMN_PSEUDO + " TEXT NOT NULL," +
                COLUMN_DATE + " TEXT NOT NULL," +
                COLUMN_CAMARADE + " TEXT," +
                COLUMN_TEXT + " TEXT," +
                COLUMN_LINK + " TEXT," +
                COLUMN_FICHIER + " TEXT," +

                Constants.DATA_COLUMN_STATUS_DATE + " TEXT NOT NULL," +
                Constants.DATA_COLUMN_SYNCHRONIZED + " INTEGER NOT NULL" +

                ");");

        // Add indexes
        db.execSQL("CREATE INDEX " + TABLE_NAME + JSON_KEY_ACTU_ID + " ON " +
                TABLE_NAME + '(' + COLUMN_ACTU_ID + ')');
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
    private static final String JSON_KEY_ACTU_ID = COLUMN_ACTU_ID.substring(4);
    private static final String JSON_KEY_PSEUDO = COLUMN_PSEUDO.substring(4);
    private static final String JSON_KEY_DATE = COLUMN_DATE.substring(4);
    private static final String JSON_KEY_CAMARADE = COLUMN_CAMARADE.substring(4);
    private static final String JSON_KEY_TEXT = COLUMN_TEXT.substring(4);
    private static final String JSON_KEY_LINK = COLUMN_LINK.substring(4);
    private static final String JSON_KEY_FICHIER = COLUMN_FICHIER.substring(4);
    private static final String JSON_KEY_STATUS_DATE = COLUMN_STATUS_DATE.substring(4);

    @Override
    public SyncResult synchronize(final ContentResolver resolver, String token, final byte operation,
                                  @Nullable String pseudo, @Nullable Short limit,
                                  @Nullable ContentValues postData) {

        // Synchronize data from remote to local DB (return inserted, deleted or
        // updated entry count & NO_DATA if error)
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";token: " + token + ";operation: " + operation +
                ";pseudo: " + pseudo + ";limit: " + limit + ";postData: " + postData);

        final SyncResult syncResult = new SyncResult();
        Bundle data = new Bundle();

        data.putString(DATA_KEY_WEB_SERVICE, WebServices.URL_PUBLICATIONS);
        data.putString(DATA_KEY_TOKEN, token);
        data.putByte(DATA_KEY_OPERATION, operation);
        if (limit != null)
            data.putShort(DATA_KEY_LIMIT, (limit != 0) ? limit : DEFAULT_LIMIT);
        data.putString(DATA_KEY_PSEUDO, pseudo);
        data.putString(DATA_KEY_TABLE_NAME, TABLE_NAME);
        //data.putString(DATA_KEY_FIELD_PSEUDO, COLUMN_PSEUDO);
        String url = getSyncUrlRequest(resolver, data);

        data.putString(DATA_KEY_FIELD_PSEUDO, COLUMN_PSEUDO);
        // NB: Do not use pseudo criteria to get max status date but add it to reset sync fields!

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
                            return (operation == WebServices.OPERATION_SELECT);
                            // Already synchronized for selection but error for any other operation

                        Uri tableUri = Uri.parse(DataProvider.CONTENT_URI + TABLE_NAME);
                        JSONArray entries = reply.getJSONArray(TABLE_NAME);
                        for (int i = 0; i < entries.length(); ++i) {

                            JSONObject entry = (JSONObject) entries.get(i);

                            // Key fields
                            int actuId = entry.getInt(JSON_KEY_ACTU_ID);

                            // Data fields
                            ContentValues values = new ContentValues();
                            values.put(COLUMN_PSEUDO, entry.getString(JSON_KEY_PSEUDO));
                            values.put(COLUMN_DATE, entry.getString(JSON_KEY_DATE));
                            if (!entry.isNull(JSON_KEY_CAMARADE))
                                values.put(COLUMN_CAMARADE, entry.getString(JSON_KEY_CAMARADE));
                            if (!entry.isNull(JSON_KEY_TEXT))
                                values.put(COLUMN_TEXT, entry.getString(JSON_KEY_TEXT));
                            if (!entry.isNull(JSON_KEY_LINK))
                                values.put(COLUMN_LINK, entry.getString(JSON_KEY_LINK));
                            if (!entry.isNull(JSON_KEY_FICHIER))
                                values.put(COLUMN_FICHIER, entry.getString(JSON_KEY_FICHIER));
                            values.put(Constants.DATA_COLUMN_STATUS_DATE, entry.getString(JSON_KEY_STATUS_DATE));
                            values.put(Constants.DATA_COLUMN_SYNCHRONIZED, Synchronized.DONE.getValue());

                            // Check if entry already exists
                            String selection = COLUMN_ACTU_ID + '=' + actuId;
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
                                values.put(COLUMN_ACTU_ID, actuId);
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
                resetSyncInProgress(resolver, data);
            return null;
        }
        return syncResult;
    }
}
