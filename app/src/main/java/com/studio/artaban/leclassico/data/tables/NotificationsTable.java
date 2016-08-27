package com.studio.artaban.leclassico.data.tables;

import android.content.ContentResolver;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.IDataTable;
import com.studio.artaban.leclassico.data.codes.WebServices;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by pascal on 08/08/16.
 * Notifications database table class
 */
public class NotificationsTable extends DataTable {

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

    public boolean synchronize(final ContentResolver resolver, String token) {
    // Synchronize data with remote DB

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";token: " + token);
        String url = getUrlSynchroRequest(resolver, WebServices.URL_NOTIFICATIONS, token,
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
    public static final String TABLE_NAME = "Notifications";

    // Columns
    public static final String COLUMN_PSEUDO = "NOT_Pseudo";
    public static final String COLUMN_DATE = "NOT_Date";
    public static final String COLUMN_OBJECT_TYPE = "NOT_ObjType";
    public static final String COLUMN_OBJECT_ID = "NOT_ObjID";
    public static final String COLUMN_OBJECT_DATE = "NOT_ObjDate";
    public static final String COLUMN_OBJECT_FROM = "NOT_ObjFrom";
    public static final String COLUMN_LU_FLAG = "NOT_LuFlag";
    public static final String COLUMN_STATUS_DATE = "NOT_StatusDate";

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
                COLUMN_OBJECT_TYPE + " TEXT," +
                COLUMN_OBJECT_ID + " INTEGER," +
                COLUMN_OBJECT_DATE + " TEXT," +
                COLUMN_OBJECT_FROM + " TEXT," +
                COLUMN_LU_FLAG + " INTEGER NOT NULL," +
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
