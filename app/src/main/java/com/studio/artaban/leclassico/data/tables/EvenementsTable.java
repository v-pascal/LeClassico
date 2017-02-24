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
 * Evenements database table class
 */
public class EvenementsTable extends DataTable {

    public static class Event extends DataField { ///////////////////////////////// Evenements entry

        public Event(short count, long id) { super(count, id); }
        public Event(Parcel parcel) {

            super(parcel);
            Logs.add(Logs.Type.V, "parcel: " + parcel);
        }
        public static final Parcelable.Creator<Event> CREATOR = new Creator<Event>() {

            @Override public Event createFromParcel(Parcel source) { return new Event(source); }
            @Override public Event[] newArray(int size) { return new Event[size]; }
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
    public static final String TABLE_NAME = "Evenements";

    // Columns
    public static final String COLUMN_EVENT_ID = "EVE_EventID";
    public static final String COLUMN_PSEUDO = "EVE_Pseudo";
    public static final String COLUMN_NOM = "EVE_Nom";
    public static final String COLUMN_NOM_UPD = "EVE_NomUPD";
    public static final String COLUMN_LIEU = "EVE_Lieu";
    public static final String COLUMN_LIEU_UPD = "EVE_LieuUPD";
    public static final String COLUMN_DATE = "EVE_Date";
    public static final String COLUMN_DATE_UPD = "EVE_DateUPD";
    public static final String COLUMN_DATE_END = "EVE_DateEnd";
    public static final String COLUMN_DATE_END_UPD = "EVE_DateEndUPD";
    public static final String COLUMN_FLYER = "EVE_Flyer";
    public static final String COLUMN_FLYER_UPD = "EVE_FlyerUPD";
    public static final String COLUMN_REMARK = "EVE_Remark";
    public static final String COLUMN_REMARK_UPD = "EVE_RemarkUPD";
    private static final String COLUMN_STATUS_DATE = "EVE_StatusDate";

    // Columns index
    private static final short COLUMN_INDEX_EVENT_ID = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_PSEUDO = 2;
    private static final short COLUMN_INDEX_NOM = 3;
    private static final short COLUMN_INDEX_NOM_UPD = 4;
    private static final short COLUMN_INDEX_LIEU = 5;
    private static final short COLUMN_INDEX_LIEU_UPD = 6;
    private static final short COLUMN_INDEX_DATE = 7;
    private static final short COLUMN_INDEX_DATE_UPD = 8;
    private static final short COLUMN_INDEX_DATE_END = 9;
    private static final short COLUMN_INDEX_DATE_END_UPD = 10;
    private static final short COLUMN_INDEX_FLYER = 11;
    private static final short COLUMN_INDEX_FLYER_UPD = 12;
    private static final short COLUMN_INDEX_REMARK = 13;
    private static final short COLUMN_INDEX_REMARK_UPD = 14;
    private static final short COLUMN_INDEX_STATUS_DATE = 15;
    private static final short COLUMN_INDEX_SYNCHRONIZED = 16;

    //
    private EvenementsTable() { }
    public static EvenementsTable newInstance() { return new EvenementsTable(); }

    @Override
    public void create(SQLiteDatabase db) {

        Logs.add(Logs.Type.V, "db: " + db);
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                DataField.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                COLUMN_EVENT_ID + " INTEGER NOT NULL," +
                COLUMN_PSEUDO + " TEXT NOT NULL," +
                COLUMN_NOM + " TEXT NOT NULL," +
                COLUMN_NOM_UPD + " TEXT NOT NULL," +
                COLUMN_LIEU + " TEXT NOT NULL," +
                COLUMN_LIEU_UPD + " TEXT NOT NULL," +
                COLUMN_DATE + " TEXT NOT NULL," +
                COLUMN_DATE_UPD + " TEXT NOT NULL," +
                COLUMN_DATE_END + " TEXT NOT NULL," +
                COLUMN_DATE_END_UPD + " TEXT NOT NULL," +
                COLUMN_FLYER + " TEXT," +
                COLUMN_FLYER_UPD + " TEXT NOT NULL," +
                COLUMN_REMARK + " TEXT," +
                COLUMN_REMARK_UPD + " TEXT NOT NULL," +

                Constants.DATA_COLUMN_STATUS_DATE + " TEXT NOT NULL," +
                Constants.DATA_COLUMN_SYNCHRONIZED + " INTEGER NOT NULL" +

                ");");
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
    private static final String JSON_KEY_EVENT_ID = COLUMN_EVENT_ID.substring(4);
    private static final String JSON_KEY_PSEUDO = COLUMN_PSEUDO.substring(4);
    private static final String JSON_KEY_NOM = COLUMN_NOM.substring(4);
    private static final String JSON_KEY_NOM_UPD = COLUMN_NOM_UPD.substring(4);
    private static final String JSON_KEY_LIEU = COLUMN_LIEU.substring(4);
    private static final String JSON_KEY_LIEU_UPD = COLUMN_LIEU_UPD.substring(4);
    private static final String JSON_KEY_DATE = COLUMN_DATE.substring(4);
    private static final String JSON_KEY_DATE_UPD = COLUMN_DATE_UPD.substring(4);
    private static final String JSON_KEY_DATE_END = COLUMN_DATE_END.substring(4);
    private static final String JSON_KEY_DATE_END_UPD = COLUMN_DATE_END_UPD.substring(4);
    private static final String JSON_KEY_FLYER = COLUMN_FLYER.substring(4);
    private static final String JSON_KEY_FLYER_UPD = COLUMN_FLYER_UPD.substring(4);
    private static final String JSON_KEY_REMARK = COLUMN_REMARK.substring(4);
    private static final String JSON_KEY_REMARK_UPD = COLUMN_REMARK_UPD.substring(4);
    private static final String JSON_KEY_STATUS_DATE = COLUMN_STATUS_DATE.substring(4);

    @Override
    public @Nullable SyncResult synchronize(final ContentResolver resolver, final byte operation,
                                            Bundle syncData, @Nullable ContentValues postData) {

        // Synchronize data from remote to local DB (return inserted, deleted or
        // updated entry count & NO_DATA if error)
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";operation: " + operation +
                ";syncData: " + syncData + ";postData: " + postData);

        final SyncResult syncResult = new SyncResult();

        syncData.putString(DATA_KEY_WEB_SERVICE, WebServices.URL_EVENTS);
        syncData.putByte(DATA_KEY_OPERATION, operation);
        syncData.putString(DATA_KEY_TABLE_NAME, TABLE_NAME);

        syncData.remove(DATA_KEY_FIELD_PSEUDO); // No pseudo field criteria for this table
        syncData.remove(DATA_KEY_FIELD_DATE); // No date field criteria for this table
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
                                    return (operation == WebServices.OPERATION_SELECT);
                                    // Already synchronized for selection but error for any other operation

                                Uri tableUri = Uri.parse(DataProvider.CONTENT_URI + TABLE_NAME);
                                JSONArray entries = reply.getJSONArray(TABLE_NAME);
                                for (int i = 0; i < entries.length(); ++i) {

                                    JSONObject entry = (JSONObject) entries.get(i);

                                    // Key fields
                                    int eventId = entry.getInt(JSON_KEY_EVENT_ID);

                                    // Data fields
                                    ContentValues values = new ContentValues();
                                    values.put(COLUMN_PSEUDO, entry.getString(JSON_KEY_PSEUDO));
                                    if (!entry.isNull(JSON_KEY_NOM))
                                        values.put(COLUMN_NOM, entry.getString(JSON_KEY_NOM));
                                    values.put(COLUMN_NOM_UPD, entry.getString(JSON_KEY_NOM_UPD));
                                    if (!entry.isNull(JSON_KEY_LIEU))
                                        values.put(COLUMN_LIEU, entry.getString(JSON_KEY_LIEU));
                                    values.put(COLUMN_LIEU_UPD, entry.getString(JSON_KEY_LIEU_UPD));
                                    if (!entry.isNull(JSON_KEY_DATE))
                                        values.put(COLUMN_DATE, entry.getString(JSON_KEY_DATE));
                                    values.put(COLUMN_DATE_UPD, entry.getString(JSON_KEY_DATE_UPD));
                                    if (!entry.isNull(JSON_KEY_DATE_END))
                                        values.put(COLUMN_DATE_END, entry.getString(JSON_KEY_DATE_END));
                                    values.put(COLUMN_DATE_END_UPD, entry.getString(JSON_KEY_DATE_END_UPD));
                                    if (!entry.isNull(JSON_KEY_FLYER))
                                        values.put(COLUMN_FLYER, entry.getString(JSON_KEY_FLYER));
                                    values.put(COLUMN_FLYER_UPD, entry.getString(JSON_KEY_FLYER_UPD));
                                    if (!entry.isNull(JSON_KEY_REMARK))
                                        values.put(COLUMN_REMARK, entry.getString(JSON_KEY_REMARK));
                                    values.put(COLUMN_REMARK_UPD, entry.getString(JSON_KEY_REMARK_UPD));

                                    values.put(Constants.DATA_COLUMN_STATUS_DATE, entry.getString(JSON_KEY_STATUS_DATE));
                                    values.put(Constants.DATA_COLUMN_SYNCHRONIZED, Synchronized.DONE.getValue());

                                    // Check if entry already exists
                                    String selection = COLUMN_EVENT_ID + '=' + eventId;
                                    Cursor cursor = resolver.query(tableUri, null, selection, null, null);
                                    if (cursor.moveToFirst()) { // DB entry exists

                                        if (entry.getInt(WebServices.JSON_KEY_STATUS) == STATUS_FIELD_DELETED) {
                                            // NB: Web site deletion priority (no status date comparison)

                                            ////// Delete entry (not definitively to keep last status date)
                                            values.put(Constants.DATA_COLUMN_SYNCHRONIZED,
                                                    Synchronized.DELETED.getValue());
                                            resolver.update(tableUri, values, selection, null);

                                            ++syncResult.deleted;

                                        } else { ////// Update entry

                                            // Remove fields that must not be synchronized yet coz updated after
                                            // remote DB fields
                                            boolean removed = false;

                                            if (cursor.getString(COLUMN_INDEX_NOM_UPD)
                                                    .compareTo(entry.getString(JSON_KEY_NOM_UPD)) > 0) {
                                                values.remove(COLUMN_NOM);
                                                values.remove(COLUMN_NOM_UPD);
                                                removed = true;
                                            }
                                            if (cursor.getString(COLUMN_INDEX_LIEU_UPD)
                                                    .compareTo(entry.getString(JSON_KEY_LIEU_UPD)) > 0) {
                                                values.remove(COLUMN_LIEU);
                                                values.remove(COLUMN_LIEU_UPD);
                                                removed = true;
                                            }
                                            if (cursor.getString(COLUMN_INDEX_DATE_UPD)
                                                    .compareTo(entry.getString(JSON_KEY_DATE_UPD)) > 0) {
                                                values.remove(COLUMN_DATE);
                                                values.remove(COLUMN_DATE_UPD);
                                                removed = true;
                                            }
                                            if (cursor.getString(COLUMN_INDEX_DATE_END_UPD)
                                                    .compareTo(entry.getString(JSON_KEY_DATE_END_UPD)) > 0) {
                                                values.remove(COLUMN_DATE_END);
                                                values.remove(COLUMN_DATE_END_UPD);
                                                removed = true;
                                            }
                                            if (cursor.getString(COLUMN_INDEX_FLYER_UPD)
                                                    .compareTo(entry.getString(JSON_KEY_FLYER_UPD)) > 0) {
                                                values.remove(COLUMN_FLYER);
                                                values.remove(COLUMN_FLYER_UPD);
                                                removed = true;
                                            }
                                            if (cursor.getString(COLUMN_INDEX_REMARK_UPD)
                                                    .compareTo(entry.getString(JSON_KEY_REMARK_UPD)) > 0) {
                                                values.remove(COLUMN_REMARK);
                                                values.remove(COLUMN_REMARK_UPD);
                                                removed = true;
                                            }

                                            if (removed) { // Keep local synchronized & status date fields

                                                values.put(Constants.DATA_COLUMN_SYNCHRONIZED,
                                                        cursor.getInt(COLUMN_INDEX_SYNCHRONIZED));
                                                values.put(Constants.DATA_COLUMN_STATUS_DATE,
                                                        cursor.getString(COLUMN_INDEX_STATUS_DATE));
                                            }
                                            resolver.update(tableUri, values, selection, null);
                                            ++syncResult.updated;
                                        }

                                    } else if (entry.getInt(WebServices.JSON_KEY_STATUS) != STATUS_FIELD_DELETED) {

                                        ////// Insert entry into DB
                                        values.put(COLUMN_EVENT_ID, eventId);
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
