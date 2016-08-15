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
import com.studio.artaban.leclassico.data.IDataTable;
import com.studio.artaban.leclassico.data.codes.WebServices;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by pascal on 28/07/16.
 * Camarades database table class
 */
public class CamaradesTable implements IDataTable {

    public static final int MALE = 2;
    public static final int FEMALE = 1;

    public static class Friend extends DataField { ///////////////////////////////// Camarades entry

        public Friend(short count, long id) { super(count, id); }
        public Friend(Parcel parcel) {

            super(parcel);
            Logs.add(Logs.Type.V, "parcel: " + parcel);
        }
        public static final Parcelable.Creator<Friend> CREATOR = new Creator<Friend>() {

            @Override public Friend createFromParcel(Parcel source) { return new Friend(source); }
            @Override public Friend[] newArray(int size) { return new Friend[size]; }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean synchronize(final ContentResolver resolver, String token) {
    // Synchronize data with remote DB

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";token: " + token);
        String url = Constants.APP_WEBSERVICES + WebServices.URL_MEMBERS + "?" +
                WebServices.DATA_TOKEN + "=" + token; // Add token to URL

        // Get last synchronization date
        Cursor cursor = resolver.query(Uri.parse(DataProvider.CONTENT_URI + TABLE_NAME),
                new String[]{ "max(" + COLUMN_STATUS_DATE + ")" }, null, null, null);
        cursor.moveToFirst();
        if (cursor.getString(0) != null) {
            Logs.add(Logs.Type.I, "Previous status date: " + cursor.getString(0));
            url += "&" + WebServices.DATA_DATE + "=" + cursor.getString(0).replace(' ', 'n');
        }
        cursor.close();

        // Send remote DB request
        Internet.DownloadResult result = Internet.downloadHttpRequest(url, null,
                new Internet.OnRequestListener() {

            @Override
            public boolean onReceiveReply(String response) {
                //Logs.add(Logs.Type.V, "response: " + response);
                try {

                    JSONObject reply = new JSONObject(response);
                    if (!reply.has(WebServices.JSON_KEY_ERROR)) { // Check no web service error

                        JSONArray members = reply.getJSONArray(JSON_KEY_MEMBERS);
                        for (int i = 0; i < members.length(); ++i) {

                            JSONObject member = (JSONObject) members.get(i);
                            Uri memberUri = Uri.parse(DataProvider.CONTENT_URI + TABLE_NAME);
                            String pseudo = member.getString(JSON_KEY_PSEUDO);

                            // Member data
                            ContentValues values = new ContentValues();
                            values.put(COLUMN_CODE_CONF, member.getString(JSON_KEY_CODE_CONF));
                            if (!member.isNull(JSON_KEY_NOM))
                                values.put(COLUMN_NOM, member.getString(JSON_KEY_NOM));
                            if (!member.isNull(JSON_KEY_PRENOM))
                                values.put(COLUMN_PRENOM, member.getString(JSON_KEY_PRENOM));
                            if (!member.isNull(JSON_KEY_SEXE))
                                values.put(COLUMN_SEXE, member.getInt(JSON_KEY_SEXE));
                            if (!member.isNull(JSON_KEY_BORN_DATE))
                                values.put(COLUMN_BORN_DATE, member.getString(JSON_KEY_BORN_DATE));
                            if (!member.isNull(JSON_KEY_ADRESSE))
                                values.put(COLUMN_ADRESSE, member.getString(JSON_KEY_ADRESSE));
                            if (!member.isNull(JSON_KEY_VILLE))
                                values.put(COLUMN_VILLE, member.getString(JSON_KEY_VILLE));
                            if (!member.isNull(JSON_KEY_POSTAL))
                                values.put(COLUMN_POSTAL, member.getString(JSON_KEY_POSTAL));
                            if (!member.isNull(JSON_KEY_EMAIL))
                                values.put(COLUMN_EMAIL, member.getString(JSON_KEY_EMAIL));
                            if (!member.isNull(JSON_KEY_HOBBIES))
                                values.put(COLUMN_HOBBIES, member.getString(JSON_KEY_HOBBIES));
                            if (!member.isNull(JSON_KEY_A_PROPOS))
                                values.put(COLUMN_A_PROPOS, member.getString(JSON_KEY_A_PROPOS));
                            if (!member.isNull(JSON_KEY_LOG_DATE))
                                values.put(COLUMN_LOG_DATE, member.getString(JSON_KEY_LOG_DATE));
                            values.put(COLUMN_ADMIN, member.getInt(JSON_KEY_ADMIN));
                            if (!member.isNull(JSON_KEY_PROFILE))
                                values.put(COLUMN_PROFILE, member.getString(JSON_KEY_PROFILE));
                            if (!member.isNull(JSON_KEY_BANNER))
                                values.put(COLUMN_BANNER, member.getString(JSON_KEY_BANNER));
                            values.put(COLUMN_LOCATED, member.getInt(JSON_KEY_LOCATED));
                            if (!member.isNull(JSON_KEY_LATITUDE))
                                values.put(COLUMN_LATITUDE, member.getDouble(JSON_KEY_LATITUDE));
                            if (!member.isNull(JSON_KEY_LONGITUDE))
                                values.put(COLUMN_LONGITUDE, member.getDouble(JSON_KEY_LONGITUDE));
                            values.put(COLUMN_STATUS_DATE, member.getString(JSON_KEY_STATUS_DATE));
                            values.put(Constants.DATA_COLUMN_SYNCHRONIZED,
                                    DataProvider.Synchronized.DONE.getValue());

                            // Check if member already exists
                            String selection = COLUMN_PSEUDO + "='" + pseudo + "'";
                            Cursor cursor = resolver.query(memberUri, new String[]{ "count(*)" },
                                    COLUMN_PSEUDO + "='" + pseudo + "'",
                                    null, null);
                            cursor.moveToFirst();
                            if (cursor.getInt(0) > 0) // Update DB entry
                                resolver.update(memberUri, values, selection, null);
                            else { // Insert entry into DB

                                values.put(COLUMN_PSEUDO, pseudo);
                                resolver.insert(memberUri, values);
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
    public static final String TABLE_NAME = "Camarades";

    // Columns
    public static final String COLUMN_PSEUDO = "CAM_Pseudo";
    public static final String COLUMN_CODE_CONF = "CAM_CodeConf";
    public static final String COLUMN_NOM = "CAM_Nom";
    public static final String COLUMN_PRENOM = "CAM_Prenom";
    public static final String COLUMN_SEXE = "CAM_Sexe";
    public static final String COLUMN_BORN_DATE = "CAM_BornDate";
    public static final String COLUMN_ADRESSE = "CAM_Adresse";
    public static final String COLUMN_VILLE = "CAM_Ville";
    public static final String COLUMN_POSTAL = "CAM_Postal";
    public static final String COLUMN_EMAIL = "CAM_Email";
    public static final String COLUMN_HOBBIES = "CAM_Hobbies";
    public static final String COLUMN_A_PROPOS = "CAM_APropos";
    public static final String COLUMN_LOG_DATE = "CAM_LogDate";
    public static final String COLUMN_ADMIN = "CAM_Admin";
    public static final String COLUMN_PROFILE = "CAM_Profile";
    public static final String COLUMN_BANNER = "CAM_Banner";
    public static final String COLUMN_LOCATED = "CAM_Located";
    public static final String COLUMN_LATITUDE = "CAM_Latitude";
    public static final String COLUMN_LONGITUDE = "CAM_Longitude";
    public static final String COLUMN_STATUS_DATE = "CAM_StatusDate";

    // Columns index
    private static final short COLUMN_INDEX_PSEUDO = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_CODE_CONF = 2;
    private static final short COLUMN_INDEX_NOM = 3;
    private static final short COLUMN_INDEX_PRENOM = 4;
    private static final short COLUMN_INDEX_SEXE = 5;
    private static final short COLUMN_INDEX_BORN_DATE = 6;
    private static final short COLUMN_INDEX_ADRESSE = 7;
    private static final short COLUMN_INDEX_VILLE = 8;
    private static final short COLUMN_INDEX_POSTAL = 9;
    private static final short COLUMN_INDEX_EMAIL = 10;
    private static final short COLUMN_INDEX_HOBBIES = 11;
    private static final short COLUMN_INDEX_A_PROPOS = 12;
    private static final short COLUMN_INDEX_LOG_DATE = 13;
    private static final short COLUMN_INDEX_ADMIN = 14;
    private static final short COLUMN_INDEX_PROFILE = 15;
    private static final short COLUMN_INDEX_BANNER = 16;
    private static final short COLUMN_INDEX_LOCATED = 17;
    private static final short COLUMN_INDEX_LATITUDE = 18;
    private static final short COLUMN_INDEX_LONGITUDE = 19;
    private static final short COLUMN_INDEX_STATUS_DATE = 20;

    private static final short COLUMN_INDEX_SYNCHRONIZED = 21;

    // JSON keys
    private static final String JSON_KEY_MEMBERS = "camarades";

    private static final String JSON_KEY_PSEUDO = COLUMN_PSEUDO.substring(4);
    private static final String JSON_KEY_CODE_CONF = COLUMN_CODE_CONF.substring(4);
    private static final String JSON_KEY_NOM = COLUMN_NOM.substring(4);
    private static final String JSON_KEY_PRENOM = COLUMN_PRENOM.substring(4);
    private static final String JSON_KEY_SEXE = COLUMN_SEXE.substring(4);
    private static final String JSON_KEY_BORN_DATE = COLUMN_BORN_DATE.substring(4);
    private static final String JSON_KEY_ADRESSE = COLUMN_ADRESSE.substring(4);
    private static final String JSON_KEY_VILLE = COLUMN_VILLE.substring(4);
    private static final String JSON_KEY_POSTAL = COLUMN_POSTAL.substring(4);
    private static final String JSON_KEY_EMAIL = COLUMN_EMAIL.substring(4);
    private static final String JSON_KEY_HOBBIES = COLUMN_HOBBIES.substring(4);
    private static final String JSON_KEY_A_PROPOS = COLUMN_A_PROPOS.substring(4);
    private static final String JSON_KEY_LOG_DATE = COLUMN_LOG_DATE.substring(4);
    private static final String JSON_KEY_ADMIN = COLUMN_ADMIN.substring(4);
    private static final String JSON_KEY_PROFILE = COLUMN_PROFILE.substring(4);
    private static final String JSON_KEY_BANNER = COLUMN_BANNER.substring(4);
    private static final String JSON_KEY_LOCATED = COLUMN_LOCATED.substring(4);
    private static final String JSON_KEY_LATITUDE = COLUMN_LATITUDE.substring(4);
    private static final String JSON_KEY_LONGITUDE = COLUMN_LONGITUDE.substring(4);
    private static final String JSON_KEY_STATUS_DATE = COLUMN_STATUS_DATE.substring(4);

    //
    private CamaradesTable() { }
    public static CamaradesTable newInstance() { return new CamaradesTable(); }

    @Override
    public void create(SQLiteDatabase db) {

        Logs.add(Logs.Type.V, "db: " + db);
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                DataField.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                COLUMN_PSEUDO + " TEXT NOT NULL," +
                COLUMN_CODE_CONF + " TEXT NOT NULL," +
                COLUMN_NOM + " TEXT," +
                COLUMN_PRENOM + " TEXT," +
                COLUMN_SEXE + " INTEGER," +
                COLUMN_BORN_DATE + " TEXT," +
                COLUMN_ADRESSE + " TEXT," +
                COLUMN_VILLE + " TEXT," +
                COLUMN_POSTAL + " TEXT," +
                COLUMN_EMAIL + " TEXT," +
                COLUMN_HOBBIES + " TEXT," +
                COLUMN_A_PROPOS + " TEXT," +
                COLUMN_LOG_DATE + " TEXT," +
                COLUMN_ADMIN + " INTEGER NOT NULL," +
                COLUMN_PROFILE + " TEXT," +
                COLUMN_BANNER + " TEXT," +
                COLUMN_LOCATED + " INTEGER NOT NULL," +
                COLUMN_LATITUDE + " REAL," +
                COLUMN_LONGITUDE + " REAL," +
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
