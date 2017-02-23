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
 * Photos database table class
 */
public class PhotosTable extends DataTable {

    public static class Picture extends DataField { /////////////////////////////////// Photos entry

        public Picture(short count, long id) { super(count, id); }
        public Picture(Parcel parcel) {

            super(parcel);
            Logs.add(Logs.Type.V, "parcel: " + parcel);
        }
        public static final Parcelable.Creator<Picture> CREATOR = new Creator<Picture>() {

            @Override public Picture createFromParcel(Parcel source) { return new Picture(source); }
            @Override public Picture[] newArray(int size) { return new Picture[size]; }
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
    public static final String TABLE_NAME = "Photos";

    // Columns
    public static final String COLUMN_ALBUM = "PHT_Album";
    public static final String COLUMN_PSEUDO = "PHT_Pseudo";
    public static final String COLUMN_FICHIER = "PHT_Fichier";
    public static final String COLUMN_FICHIER_ID = "PHT_FichierID";
    public static final String COLUMN_BEST = "PHT_Best"; // Additional field (reserved to application)
    public static final String COLUMN_RANGE = "PHT_Range"; // Additional field (reserved to application)
    private static final String COLUMN_STATUS_DATE = "PHT_StatusDate";

    // Columns index
    private static final short COLUMN_INDEX_ALBUM = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_PSEUDO = 2;
    private static final short COLUMN_INDEX_FICHIER = 3;
    private static final short COLUMN_INDEX_FICHIER_ID = 4;
    private static final short COLUMN_INDEX_BEST = 5;
    private static final short COLUMN_INDEX_RANGE = 6;
    private static final short COLUMN_INDEX_STATUS_DATE = 7;
    private static final short COLUMN_INDEX_SYNCHRONIZED = 8;

    //
    private PhotosTable() { }
    public static PhotosTable newInstance() { return new PhotosTable(); }
    public static String getBestIds(ContentResolver resolver) { // Return best photo IDs

        Logs.add(Logs.Type.V, "resolver: " + resolver);
        Cursor cursor = resolver.query(Uri.parse(DataProvider.CONTENT_URI + TABLE_NAME),
                new String[]{COLUMN_FICHIER_ID}, COLUMN_BEST + "=1", null, COLUMN_FICHIER_ID + " DESC");

        StringBuilder ids = new StringBuilder();
        if (cursor.moveToFirst()) {
            do {
                ids.append((ids.length() > 0)?
                        WebServices.LIST_SEPARATOR + String.valueOf(cursor.getInt(0)):
                        String.valueOf(cursor.getInt(0)));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return (ids.length() > 0)? ids.toString():null;
    }

    @Override
    public void create(SQLiteDatabase db) {

        Logs.add(Logs.Type.V, "db: " + db);
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                DataField.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                COLUMN_ALBUM + " TEXT NOT NULL," +
                COLUMN_PSEUDO + " TEXT NOT NULL," +
                COLUMN_FICHIER + " TEXT NOT NULL," +
                COLUMN_FICHIER_ID + " INTEGER NOT NULL," +
                COLUMN_BEST + " INTEGER NOT NULL," +
                COLUMN_RANGE + " TEXT NOT NULL," +

                Constants.DATA_COLUMN_STATUS_DATE + " TEXT NOT NULL," +
                Constants.DATA_COLUMN_SYNCHRONIZED + " INTEGER NOT NULL" +

                ");");

        // Add indexes
        db.execSQL("CREATE INDEX " + TABLE_NAME + JSON_KEY_FICHIER_ID + " ON " +
                TABLE_NAME + '(' + COLUMN_FICHIER_ID + ')');
        db.execSQL("CREATE INDEX " + TABLE_NAME + JSON_KEY_BEST + " ON " +
                TABLE_NAME + '(' + COLUMN_BEST + ')');
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
    private static final String JSON_KEY_ALBUM = COLUMN_ALBUM.substring(4);
    private static final String JSON_KEY_PSEUDO = COLUMN_PSEUDO.substring(4);
    private static final String JSON_KEY_FICHIER = COLUMN_FICHIER.substring(4);
    private static final String JSON_KEY_FICHIER_ID = COLUMN_FICHIER_ID.substring(4);
    private static final String JSON_KEY_BEST = COLUMN_BEST.substring(4);
    private static final String JSON_KEY_RANGE = COLUMN_RANGE.substring(4);
    private static final String JSON_KEY_STATUS_DATE = COLUMN_STATUS_DATE.substring(4);

    //////
    private static Internet.DownloadResult sendSyncRequest(String url, @Nullable final ContentValues postData,
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

                        if (reply.isNull(TABLE_NAME)) {
                            if ((postData != null) && (postData.containsKey(WebServices.PHOTOS_DATA_IDS)))
                                resetBestFields(resolver, postData.getAsString(WebServices.PHOTOS_DATA_IDS));

                            return (operation == WebServices.OPERATION_SELECT);
                            // Already synchronized for selection but error for any other operation
                        }

                        Uri tableUri = Uri.parse(DataProvider.CONTENT_URI + TABLE_NAME);
                        JSONArray entries = reply.getJSONArray(TABLE_NAME);
                        for (int i = 0; i < entries.length(); ++i) {

                            JSONObject entry = (JSONObject) entries.get(i);

                            // Key fields
                            String fichier = entry.getString(JSON_KEY_FICHIER);
                            int fichierId = entry.getInt(JSON_KEY_FICHIER_ID);

                            // Data fields
                            ContentValues values = new ContentValues();
                            values.put(COLUMN_ALBUM, entry.getString(JSON_KEY_ALBUM));
                            values.put(COLUMN_PSEUDO, entry.getString(JSON_KEY_PSEUDO));
                            if (!entry.isNull(JSON_KEY_BEST)) { // Best
                                values.put(COLUMN_BEST, entry.getInt(JSON_KEY_BEST));
                                values.put(COLUMN_RANGE, entry.getString(JSON_KEY_RANGE));
                            }
                            values.put(Constants.DATA_COLUMN_STATUS_DATE, entry.getString(JSON_KEY_STATUS_DATE));
                            values.put(Constants.DATA_COLUMN_SYNCHRONIZED, Synchronized.DONE.getValue());

                            // Check if entry already exists
                            String selection = COLUMN_FICHIER + "='" + fichier + "' AND " +
                                    COLUMN_FICHIER_ID + '=' + fichierId;
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
                                values.put(COLUMN_FICHIER, fichier);
                                values.put(COLUMN_FICHIER_ID, fichierId);
                                if (!values.containsKey(COLUMN_BEST))
                                    values.put(COLUMN_BEST, 0);
                                if (!values.containsKey(COLUMN_RANGE))
                                    values.put(COLUMN_RANGE, Constants.UNDEFINED);
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
    private static String getMaxStatusDate(ContentResolver resolver, String pseudo) {
    // Return the newest photo status date of the connected user albums (if any)

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";pseudo: " + pseudo);
        Cursor cursor = resolver.query(Uris.getUri(Uris.ID_RAW_QUERY), null,
                "SELECT max(" + TABLE_NAME + '.' + Constants.DATA_COLUMN_STATUS_DATE + ") FROM " + TABLE_NAME +
                        " LEFT JOIN " + AlbumsTable.TABLE_NAME + " ON " +
                        AlbumsTable.COLUMN_NOM + '=' + COLUMN_ALBUM +
                        " WHERE " + AlbumsTable.COLUMN_PSEUDO + "='" + pseudo + "' AND " +
                        TABLE_NAME + '.' + Constants.DATA_COLUMN_SYNCHRONIZED + "<=" + Synchronized.DONE.getValue(),
                null, null);

        String statusDate = null;
        if (cursor.moveToFirst())
            statusDate = cursor.getString(0);
        cursor.close();
        return statusDate;
    }
    private static String resetBestFields(ContentResolver resolver, @Nullable String ids) {
    // Set or reset best fields

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";best: " + ids);
        Uri uri = Uri.parse(DataProvider.CONTENT_URI + TABLE_NAME);
        if (ids != null) { // Set or replace best flags

            ContentValues values = new ContentValues();
            values.put(COLUMN_BEST, 1);
            resolver.update(uri, values,
                    COLUMN_FICHIER_ID + " IN (" + ids.replace(WebServices.LIST_SEPARATOR, ',') + ')', null);
            return null;
        }
        // Reset best flags (return best photos)
        String best = getBestIds(resolver);
        if (best != null) {

            // Clear best flags
            ContentValues values = new ContentValues();
            values.put(COLUMN_BEST, 0);
            resolver.update(uri, values, null, null);
        }
        return best; // Best photo IDs
    }

    @Override
    public @Nullable SyncResult synchronize(ContentResolver resolver, byte operation,
                                            Bundle syncData, @Nullable ContentValues postData) {

        // Synchronize data from remote to local DB (return inserted, deleted or
        // updated entry count & NO_DATA if error)
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";operation: " + operation +
                ";syncData: " + syncData + ";postData: " + postData);

        syncData.putString(DATA_KEY_WEB_SERVICE, WebServices.URL_PHOTOS);
        syncData.putByte(DATA_KEY_OPERATION, operation);
        syncData.putString(DATA_KEY_TABLE_NAME, TABLE_NAME);
        syncData.remove(DATA_KEY_LIMIT); // No limit

        syncData.putString(DATA_KEY_FIELD_PSEUDO, COLUMN_PSEUDO);
        syncData.remove(DATA_KEY_FIELD_DATE); // No date field criteria for this table

        SyncResult syncResult = new SyncResult();
        if (operation == WebServices.OPERATION_SELECT) { ////// Select
            String best = resetBestFields(resolver, null);

            // Add post data for best photos request
            ContentValues data = new ContentValues();
            data.put(WebServices.PHOTOS_DATA_BEST, 1);
            if (best != null)
                data.put(WebServices.PHOTOS_DATA_IDS, best);

            //////
            if (sendSyncRequest(getSyncUrlRequest(resolver, syncData), data, resolver,
                    operation, syncResult) != Internet.DownloadResult.SUCCEEDED) {

                Logs.add(Logs.Type.E, "Table '" + TABLE_NAME + "' synchronization request error (B)");
                if (best != null)
                    resetBestFields(resolver, best);
                return null;
            }

            // Update request
            syncData.putString(DATA_KEY_STATUS_DATE, getMaxStatusDate(resolver, syncData.getString(DATA_KEY_PSEUDO)));
            String url = getSyncUrlRequest(resolver, syncData);

            syncData.remove(DATA_KEY_STATUS_DATE);

            //////
            if (sendSyncRequest(url, null, resolver, operation, syncResult) != Internet.DownloadResult.SUCCEEDED) {
                Logs.add(Logs.Type.E, "Table '" + TABLE_NAME + "' synchronization request error (B)");
                return null;
            }

        } else { ////// Insert, update or delete (no old request available for this table)

            if (sendSyncRequest(getSyncUrlRequest(resolver, syncData), postData, resolver,
                    operation, syncResult) != Internet.DownloadResult.SUCCEEDED) {

                Logs.add(Logs.Type.E, "Table '" + TABLE_NAME + "' synchronization request error (C)");
                resetSyncInProgress(resolver, syncData);
                return null;
            }
        }
        return syncResult;
    }
}
