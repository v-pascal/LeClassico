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
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.data.codes.WebServices;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by pascal on 28/07/16.
 * Commentaires database table class
 */
public class CommentairesTable extends DataTable {

    public static char TYPE_PUBLICATION = 'A';
    public static char TYPE_PHOTO = 'P';
    // Types

    private static final short DEFAULT_LIMIT = 5; // Default remote DB query limit

    public static class Comment extends DataField { ///////////////////////////// Commentaires entry

        public Comment(short count, long id) { super(count, id); }
        public Comment(Parcel parcel) {

            super(parcel);
            Logs.add(Logs.Type.V, "parcel: " + parcel);
        }
        public static final Parcelable.Creator<Comment> CREATOR = new Creator<Comment>() {

            @Override public Comment createFromParcel(Parcel source) { return new Comment(source); }
            @Override public Comment[] newArray(int size) { return new Comment[size]; }
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
    public static final String TABLE_NAME = "Commentaires";

    // Columns
    public static final String COLUMN_OBJ_TYPE = "COM_ObjType";
    public static final String COLUMN_OBJ_ID = "COM_ObjID";
    public static final String COLUMN_PSEUDO = "COM_Pseudo";
    public static final String COLUMN_DATE = "COM_Date";
    public static final String COLUMN_TEXT = "COM_Text";
    private static final String COLUMN_STATUS_DATE = "COM_StatusDate";

    // Columns index
    private static final short COLUMN_INDEX_OBJ_TYPE = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_OBJ_ID = 2;
    private static final short COLUMN_INDEX_PSEUDO = 3;
    private static final short COLUMN_INDEX_DATE = 4;
    private static final short COLUMN_INDEX_TEXT = 5;
    private static final short COLUMN_INDEX_STATUS_DATE = 6;
    private static final short COLUMN_INDEX_SYNCHRONIZED = 7;

    //
    private CommentairesTable() { }
    public static CommentairesTable newInstance() { return new CommentairesTable(); }

    @Override
    public void create(SQLiteDatabase db) {

        Logs.add(Logs.Type.V, "db: " + db);
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                DataField.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                COLUMN_OBJ_TYPE + " TEXT NOT NULL," +
                COLUMN_OBJ_ID + " INTEGER NOT NULL," +
                COLUMN_PSEUDO + " TEXT NOT NULL," +
                COLUMN_DATE + " TEXT NOT NULL," +
                COLUMN_TEXT + " TEXT NOT NULL," +

                Constants.DATA_COLUMN_STATUS_DATE + " TEXT NOT NULL," +
                Constants.DATA_COLUMN_SYNCHRONIZED + " INTEGER NOT NULL" +

                ");");

        // Add indexes
        db.execSQL("CREATE INDEX " + TABLE_NAME + JSON_KEY_PSEUDO + " ON " +
                TABLE_NAME + '(' + COLUMN_PSEUDO + ')');
        db.execSQL("CREATE INDEX " + TABLE_NAME + JSON_KEY_OBJ_ID + " ON " +
                TABLE_NAME + '(' + COLUMN_OBJ_ID + ')');
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
    private static final String JSON_KEY_OBJ_TYPE = COLUMN_OBJ_TYPE.substring(4);
    private static final String JSON_KEY_OBJ_ID = COLUMN_OBJ_ID.substring(4);
    private static final String JSON_KEY_PSEUDO = COLUMN_PSEUDO.substring(4);
    private static final String JSON_KEY_DATE = COLUMN_DATE.substring(4);
    private static final String JSON_KEY_TEXT = COLUMN_TEXT.substring(4);
    private static final String JSON_KEY_STATUS_DATE = COLUMN_STATUS_DATE.substring(4);

    //////
    private static String getMaxStatusDate(ContentResolver resolver, char type, String ids) {
    // Return newest status date of entries specified by criteria (passed in parameters)

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";type: " + type + ";ids: " + ids);
        Cursor cursor = resolver.query(Uri.parse(DataProvider.CONTENT_URI + TABLE_NAME),
                new String[]{ "max(" + Constants.DATA_COLUMN_STATUS_DATE + ')' },
                Constants.DATA_COLUMN_SYNCHRONIZED + '=' + Synchronized.DONE.getValue() + " AND " +
                        COLUMN_OBJ_TYPE + "='" + type + "' AND " +
                        COLUMN_OBJ_ID + " IN (" + ids.replace(WebServices.LIST_SEPARATOR, ',') + ')',
                null, null);

        String statusDate = null;
        if (cursor.moveToFirst())
            statusDate = cursor.getString(0);
        cursor.close();
        return statusDate;
    }
    private static String getMinDate(ContentResolver resolver, char type, String ids) {
    // Return oldest date of entries specified by criteria (passed in parameters)

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";type: " + type + ";ids: " + ids);
        Cursor cursor = resolver.query(Uris.getUri(Uris.ID_RAW_QUERY), null,
                "SELECT min(" + COLUMN_DATE + ")," + COLUMN_OBJ_ID + " FROM " + TABLE_NAME + " WHERE " +
                        Constants.DATA_COLUMN_SYNCHRONIZED + '=' + Synchronized.DONE.getValue() + " AND " +
                        COLUMN_OBJ_TYPE + "='" + type + "' AND " +
                        COLUMN_OBJ_ID + " IN (" + ids.replace(WebServices.LIST_SEPARATOR, ',') + ')' +
                        " GROUP BY " + COLUMN_OBJ_ID + " ORDER BY " + COLUMN_DATE + " DESC LIMIT 1",
                null, null);

        // NB: Do not get min date directly as done in the common function coz should return the newest
        //     date of the last comments. This need is due to the limitation added to the comments
        //     synchronization query (LIMIT statement).
        String date = null;
        if (cursor.moveToFirst())
            date = cursor.getString(0); // The most recent of the oldest date (see comments above)
        cursor.close();
        return date;
    }

    private static Internet.DownloadResult sendSyncRequest(String url, @Nullable ContentValues postData,
                                                           final ContentResolver resolver, final byte operation,
                                                           final SyncResult syncResult) {
        // Send remote DB request
        return Internet.downloadHttpRequest(url, postData, new Internet.OnRequestListener() {

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
                            char type = entry.getString(JSON_KEY_OBJ_TYPE).charAt(0);
                            int id = entry.getInt(JSON_KEY_OBJ_ID);
                            String pseudo = entry.getString(JSON_KEY_PSEUDO);
                            String date = entry.getString(JSON_KEY_DATE);

                            // Data fields
                            ContentValues values = new ContentValues();
                            values.put(COLUMN_TEXT, entry.getString(JSON_KEY_TEXT));
                            values.put(Constants.DATA_COLUMN_STATUS_DATE, entry.getString(JSON_KEY_STATUS_DATE));
                            values.put(Constants.DATA_COLUMN_SYNCHRONIZED, Synchronized.DONE.getValue());

                            // Check if entry already exists
                            String selection = COLUMN_OBJ_TYPE + "='" + type + "' AND " +
                                    COLUMN_OBJ_ID + '=' + id + " AND " +
                                    COLUMN_PSEUDO + "='" + pseudo + "' AND " +
                                    COLUMN_DATE + "='" + date + '\'';
                            Cursor cursor = resolver.query(tableUri, new String[]{Constants.DATA_COLUMN_STATUS_DATE},
                                    selection, null, null);
                            if (cursor.moveToFirst()) { // DB entry exists

                                if (entry.getInt(WebServices.JSON_KEY_STATUS) == STATUS_FIELD_DELETED) {
                                    // NB: Web site deletion priority (no status date comparison)

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
                                    resolver.update(tableUri, values, selection, null);
                                    ++syncResult.updated;
                                }
                                //else // Nothing to do here (let's synchronize from local to remote DB)

                            } else if (entry.getInt(WebServices.JSON_KEY_STATUS) != STATUS_FIELD_DELETED) {

                                ////// Insert entry into DB
                                values.put(COLUMN_OBJ_TYPE, String.valueOf(type));
                                values.put(COLUMN_OBJ_ID, id);
                                values.put(COLUMN_PSEUDO, pseudo);
                                values.put(COLUMN_DATE, date);
                                resolver.insert(tableUri, values);

                                ++syncResult.inserted;
                            }
                            //else // Do not add a deleted entry (created & removed when offline)
                            cursor.close();
                        }

                    } else {
                        Logs.add(Logs.Type.E, "Synchronization error: #" + reply.getInt(WebServices.JSON_KEY_ERROR));
                        return false;
                    }

                } catch (JSONException e) {
                    Logs.add(Logs.Type.F, "Unexpected connection reply: " + e.getMessage());
                    return false;
                }
                return true;
            }
        });
    }

    @Override
    public SyncResult synchronize(final ContentResolver resolver, byte operation, Bundle syncData,
                                  @Nullable ContentValues postData) {

        // Synchronize data from remote to local DB (return inserted, deleted or
        // updated entry count & NO_DATA if error)
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";operation: " + operation +
                ";syncData: " + syncData + ";postData: " + postData);

        syncData.putString(DATA_KEY_WEB_SERVICE, WebServices.URL_COMMENTS);
        syncData.putByte(DATA_KEY_OPERATION, operation);
        syncData.putString(DATA_KEY_TABLE_NAME, TABLE_NAME);
        if ((syncData.containsKey(DATA_KEY_LIMIT)) && (syncData.getShort(DATA_KEY_LIMIT) == 0))
            syncData.putShort(DATA_KEY_LIMIT, DEFAULT_LIMIT);

        syncData.remove(DATA_KEY_FIELD_PSEUDO); // No pseudo field criteria for this table
        syncData.putString(DATA_KEY_FIELD_DATE, COLUMN_DATE);

        SyncResult syncResult = new SyncResult();
        if (operation == WebServices.OPERATION_SELECT) { ////// All comments

            // Get publication IDs list
            String ids = ActualitesTable.getIds(resolver, syncData.getString(DATA_KEY_PSEUDO));
            //if (ids == null)
            //  Cannot be NULL coz there is at least one publication: the Webmaster presentation!

            // Get request URL accordingly
            syncData.putString(DATA_KEY_STATUS_DATE, getMaxStatusDate(resolver, TYPE_PUBLICATION, ids));
            syncData.putString(DATA_KEY_DATE, getMinDate(resolver, TYPE_PUBLICATION, ids));
            String url = getSyncUrlRequest(resolver, syncData);

            // Add IDs & type to post data
            ContentValues data = new ContentValues();
            data.put(WebServices.COMMENTS_DATA_IDS, ids);
            data.put(WebServices.COMMENTS_DATA_TYPE, String.valueOf(TYPE_PUBLICATION));

            ////// Get publications comments
            if (sendSyncRequest(url, data, resolver, WebServices.OPERATION_SELECT,
                    syncResult) != Internet.DownloadResult.SUCCEEDED) {
                Logs.add(Logs.Type.E, "Table '" + TABLE_NAME + "' synchronization request error (A)");
                return null;
            }

            // Get photos IDs list (best only)
            ids = PhotosTable.getBestIds(resolver);
            if (ids == null)
                return syncResult; // No photo found

            // Get request URL accordingly
            syncData.putString(DATA_KEY_STATUS_DATE, getMaxStatusDate(resolver, TYPE_PHOTO, ids));
            syncData.putString(DATA_KEY_DATE, getMinDate(resolver, TYPE_PHOTO, ids));
            url = getSyncUrlRequest(resolver, syncData);

            syncData.remove(DATA_KEY_STATUS_DATE);
            syncData.remove(DATA_KEY_DATE);

            // Replace IDs & type from post data
            data.put(WebServices.COMMENTS_DATA_IDS, ids);
            data.put(WebServices.COMMENTS_DATA_TYPE, String.valueOf(TYPE_PHOTO));

            ////// Get photos comments
            if (sendSyncRequest(url, data, resolver, WebServices.OPERATION_SELECT,
                    syncResult) != Internet.DownloadResult.SUCCEEDED) {
                Logs.add(Logs.Type.E, "Table '" + TABLE_NAME + "' synchronization request error (P)");
                return null;
            }

        } else { ////// Specific comments

            if (postData == null)
                throw new IllegalArgumentException("Missing IDs & Type info into data");

            //////
            if (sendSyncRequest(getSyncUrlRequest(resolver, syncData), postData, resolver,
                    operation, syncResult) != Internet.DownloadResult.SUCCEEDED) {

                Logs.add(Logs.Type.E, "Table '" + TABLE_NAME + "' synchronization request error");
                if (operation != WebServices.OPERATION_SELECT_OLD) {
                    syncData.putString(DATA_KEY_FIELD_PSEUDO, COLUMN_PSEUDO);
                    // NB: Needed to reset sync fields!

                    resetSyncInProgress(resolver, syncData);
                }
                return null;
            }
        }
        return syncResult;
    }
}
