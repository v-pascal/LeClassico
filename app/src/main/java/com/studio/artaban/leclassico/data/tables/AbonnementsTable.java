package com.studio.artaban.leclassico.data.tables;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

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

    public boolean synchronize(final ContentResolver resolver, String token) {
    // Synchronize data with remote DB

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";token: " + token);
        String url = getUrlSynchroRequest(resolver, WebServices.URL_FOLLOWERS, token,
                TABLE_NAME, COLUMN_STATUS_DATE);

        // Send remote DB request
        Internet.DownloadResult result = Internet.downloadHttpRequest(url, null,
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
                            String camarade = entry.getString(JSON_KEY_CAMARADE);

                            // Entry fields
                            ContentValues values = new ContentValues();
                            values.put(COLUMN_STATUS_DATE, entry.getString(JSON_KEY_STATUS_DATE));
                            values.put(Constants.DATA_COLUMN_SYNCHRONIZED,
                                    DataProvider.Synchronized.DONE.getValue());

                            // Check if entry already exists
                            String selection = COLUMN_PSEUDO + "='" + pseudo + "' AND " +
                                    COLUMN_CAMARADE + "='" + camarade + "'";
                            Cursor cursor = resolver.query(tableUri, new String[]{ "count(*)" },
                                    selection, null, null);
                            cursor.moveToFirst();
                            if (cursor.getInt(0) > 0) { // DB entry exists

                                if (entry.getInt(WebServices.JSON_KEY_STATUS) == WebServices.STATUS_FIELD_DELETED) {

                                    // Delete entry (definitively)
                                    values.put(Constants.DATA_COLUMN_SYNCHRONIZED,
                                            DataProvider.Synchronized.TO_DELETE.getValue());
                                    resolver.update(tableUri, values, selection, null);
                                    resolver.delete(tableUri, selection, null);
                                }
                                else // Update entry
                                    resolver.update(tableUri, values, selection, null);
                            }
                            else { // Insert entry into DB

                                values.put(COLUMN_PSEUDO, pseudo);
                                values.put(COLUMN_CAMARADE, camarade);
                                resolver.insert(tableUri, values);
                            }
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
            return false;
        }
        return true;
    }

    //
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
    public static final String COLUMN_STATUS_DATE = "ABO_StatusDate";

    // Columns index
    private static final short COLUMN_INDEX_PSEUDO = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_CAMARADE = 2;
    private static final short COLUMN_INDEX_STATUS_DATE = 3;

    private static final short COLUMN_INDEX_SYNCHRONIZED = 4;

    // JSON keys
    private static final String JSON_KEY_PSEUDO = COLUMN_PSEUDO.substring(4);
    private static final String JSON_KEY_CAMARADE = COLUMN_CAMARADE.substring(4);
    private static final String JSON_KEY_STATUS_DATE = COLUMN_STATUS_DATE.substring(4);

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
                COLUMN_STATUS_DATE + " TEXT NOT NULL," +

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
}
