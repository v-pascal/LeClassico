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
 * Camarades database table class
 */
public class CamaradesTable extends DataTable {

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
    public static final String COLUMN_CODE_CONF_UPD = "CAM_CodeConfUPD";
    public static final String COLUMN_NOM = "CAM_Nom";
    public static final String COLUMN_NOM_UPD = "CAM_NomUPD";
    public static final String COLUMN_PRENOM = "CAM_Prenom";
    public static final String COLUMN_PRENOM_UPD = "CAM_PrenomUPD";
    public static final String COLUMN_SEXE = "CAM_Sexe";
    public static final String COLUMN_SEXE_UPD = "CAM_SexeUPD";
    public static final String COLUMN_BORN_DATE = "CAM_BornDate";
    public static final String COLUMN_BORN_DATE_UPD = "CAM_BornDateUPD";
    public static final String COLUMN_ADRESSE = "CAM_Adresse";
    public static final String COLUMN_ADRESSE_UPD = "CAM_AdresseUPD";
    public static final String COLUMN_VILLE = "CAM_Ville";
    public static final String COLUMN_VILLE_UPD = "CAM_VilleUPD";
    public static final String COLUMN_POSTAL = "CAM_Postal";
    public static final String COLUMN_POSTAL_UPD = "CAM_PostalUPD";
    public static final String COLUMN_EMAIL = "CAM_Email";
    public static final String COLUMN_EMAIL_UPD = "CAM_EmailUPD";
    public static final String COLUMN_HOBBIES = "CAM_Hobbies";
    public static final String COLUMN_HOBBIES_UPD = "CAM_HobbiesUPD";
    public static final String COLUMN_A_PROPOS = "CAM_APropos";
    public static final String COLUMN_A_PROPOS_UPD = "CAM_AProposUPD";
    public static final String COLUMN_LOG_DATE = "CAM_LogDate";
    public static final String COLUMN_LOG_DATE_UPD = "CAM_LogDateUPD";
    public static final String COLUMN_ADMIN = "CAM_Admin";
    public static final String COLUMN_ADMIN_UPD = "CAM_AdminUPD";
    public static final String COLUMN_PROFILE = "CAM_Profile";
    public static final String COLUMN_PROFILE_UPD = "CAM_ProfileUPD";
    public static final String COLUMN_BANNER = "CAM_Banner";
    public static final String COLUMN_BANNER_UPD = "CAM_BannerUPD";
    public static final String COLUMN_LOCATED = "CAM_Located";
    public static final String COLUMN_LOCATED_UPD = "CAM_LocatedUPD";
    public static final String COLUMN_LATITUDE = "CAM_Latitude";
    public static final String COLUMN_LATITUDE_UPD = "CAM_LatitudeUPD";
    public static final String COLUMN_LONGITUDE = "CAM_Longitude";
    public static final String COLUMN_LONGITUDE_UPD = "CAM_LongitudeUPD";
    private static final String COLUMN_STATUS_DATE = "CAM_StatusDate";

    // Columns index
    private static final short COLUMN_INDEX_PSEUDO = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_CODE_CONF = 2;
    private static final short COLUMN_INDEX_CODE_CONF_UPD = 3;
    private static final short COLUMN_INDEX_NOM = 4;
    private static final short COLUMN_INDEX_NOM_UPD = 5;
    private static final short COLUMN_INDEX_PRENOM = 6;
    private static final short COLUMN_INDEX_PRENOM_UPD = 7;
    private static final short COLUMN_INDEX_SEXE = 8;
    private static final short COLUMN_INDEX_SEXE_UPD = 9;
    private static final short COLUMN_INDEX_BORN_DATE = 10;
    private static final short COLUMN_INDEX_BORN_DATE_UPD = 11;
    private static final short COLUMN_INDEX_ADRESSE = 12;
    private static final short COLUMN_INDEX_ADRESSE_UPD = 13;
    private static final short COLUMN_INDEX_VILLE = 14;
    private static final short COLUMN_INDEX_VILLE_UPD = 15;
    private static final short COLUMN_INDEX_POSTAL = 16;
    private static final short COLUMN_INDEX_POSTAL_UPD = 17;
    private static final short COLUMN_INDEX_EMAIL = 18;
    private static final short COLUMN_INDEX_EMAIL_UPD = 19;
    private static final short COLUMN_INDEX_HOBBIES = 20;
    private static final short COLUMN_INDEX_HOBBIES_UPD = 21;
    private static final short COLUMN_INDEX_A_PROPOS = 22;
    private static final short COLUMN_INDEX_A_PROPOS_UPD = 23;
    private static final short COLUMN_INDEX_LOG_DATE = 24;
    private static final short COLUMN_INDEX_LOG_DATE_UPD = 25;
    private static final short COLUMN_INDEX_ADMIN = 26;
    private static final short COLUMN_INDEX_ADMIN_UPD = 27;
    private static final short COLUMN_INDEX_PROFILE = 28;
    private static final short COLUMN_INDEX_PROFILE_UPD = 29;
    private static final short COLUMN_INDEX_BANNER = 30;
    private static final short COLUMN_INDEX_BANNER_UPD = 31;
    private static final short COLUMN_INDEX_LOCATED = 32;
    private static final short COLUMN_INDEX_LOCATED_UPD = 33;
    private static final short COLUMN_INDEX_LATITUDE = 34;
    private static final short COLUMN_INDEX_LATITUDE_UPD = 35;
    private static final short COLUMN_INDEX_LONGITUDE = 36;
    private static final short COLUMN_INDEX_LONGITUDE_UPD = 37;
    private static final short COLUMN_INDEX_STATUS_DATE = 38;
    private static final short COLUMN_INDEX_SYNCHRONIZED = 39;

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
                COLUMN_CODE_CONF_UPD + " TEXT NOT NULL," +
                COLUMN_NOM + " TEXT," +
                COLUMN_NOM_UPD + " TEXT NOT NULL," +
                COLUMN_PRENOM + " TEXT," +
                COLUMN_PRENOM_UPD + " TEXT NOT NULL," +
                COLUMN_SEXE + " INTEGER," +
                COLUMN_SEXE_UPD + " TEXT NOT NULL," +
                COLUMN_BORN_DATE + " TEXT," +
                COLUMN_BORN_DATE_UPD + " TEXT NOT NULL," +
                COLUMN_ADRESSE + " TEXT," +
                COLUMN_ADRESSE_UPD + " TEXT NOT NULL," +
                COLUMN_VILLE + " TEXT," +
                COLUMN_VILLE_UPD + " TEXT NOT NULL," +
                COLUMN_POSTAL + " TEXT," +
                COLUMN_POSTAL_UPD + " TEXT NOT NULL," +
                COLUMN_EMAIL + " TEXT," +
                COLUMN_EMAIL_UPD + " TEXT NOT NULL," +
                COLUMN_HOBBIES + " TEXT," +
                COLUMN_HOBBIES_UPD + " TEXT NOT NULL," +
                COLUMN_A_PROPOS + " TEXT," +
                COLUMN_A_PROPOS_UPD + " TEXT NOT NULL," +
                COLUMN_LOG_DATE + " TEXT," +
                COLUMN_LOG_DATE_UPD + " TEXT NOT NULL," +
                COLUMN_ADMIN + " INTEGER NOT NULL," +
                COLUMN_ADMIN_UPD + " TEXT NOT NULL," +
                COLUMN_PROFILE + " TEXT," +
                COLUMN_PROFILE_UPD + " TEXT NOT NULL," +
                COLUMN_BANNER + " TEXT," +
                COLUMN_BANNER_UPD + " TEXT NOT NULL," +
                COLUMN_LOCATED + " INTEGER NOT NULL," +
                COLUMN_LOCATED_UPD + " TEXT NOT NULL," +
                COLUMN_LATITUDE + " REAL," +
                COLUMN_LATITUDE_UPD + " TEXT NOT NULL," +
                COLUMN_LONGITUDE + " REAL," +
                COLUMN_LONGITUDE_UPD + " TEXT NOT NULL," +

                Constants.DATA_COLUMN_STATUS_DATE + " TEXT NOT NULL," +
                Constants.DATA_COLUMN_SYNCHRONIZED + " INTEGER NOT NULL" +

                ");");

        // Add indexes
        db.execSQL("CREATE INDEX " + TABLE_NAME + JSON_KEY_PSEUDO + " ON " +
                TABLE_NAME + "(" + COLUMN_PSEUDO + ")");
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
    private static final String JSON_KEY_CODE_CONF = COLUMN_CODE_CONF.substring(4);
    private static final String JSON_KEY_CODE_CONF_UPD = COLUMN_CODE_CONF_UPD.substring(4);
    private static final String JSON_KEY_NOM = COLUMN_NOM.substring(4);
    private static final String JSON_KEY_NOM_UPD = COLUMN_NOM_UPD.substring(4);
    private static final String JSON_KEY_PRENOM = COLUMN_PRENOM.substring(4);
    private static final String JSON_KEY_PRENOM_UPD = COLUMN_PRENOM_UPD.substring(4);
    private static final String JSON_KEY_SEXE = COLUMN_SEXE.substring(4);
    private static final String JSON_KEY_SEXE_UPD = COLUMN_SEXE_UPD.substring(4);
    private static final String JSON_KEY_BORN_DATE = COLUMN_BORN_DATE.substring(4);
    private static final String JSON_KEY_BORN_DATE_UPD = COLUMN_BORN_DATE_UPD.substring(4);
    private static final String JSON_KEY_ADRESSE = COLUMN_ADRESSE.substring(4);
    private static final String JSON_KEY_ADRESSE_UPD = COLUMN_ADRESSE_UPD.substring(4);
    private static final String JSON_KEY_VILLE = COLUMN_VILLE.substring(4);
    private static final String JSON_KEY_VILLE_UPD = COLUMN_VILLE_UPD.substring(4);
    private static final String JSON_KEY_POSTAL = COLUMN_POSTAL.substring(4);
    private static final String JSON_KEY_POSTAL_UPD = COLUMN_POSTAL_UPD.substring(4);
    private static final String JSON_KEY_EMAIL = COLUMN_EMAIL.substring(4);
    private static final String JSON_KEY_EMAIL_UPD = COLUMN_EMAIL_UPD.substring(4);
    private static final String JSON_KEY_HOBBIES = COLUMN_HOBBIES.substring(4);
    private static final String JSON_KEY_HOBBIES_UPD = COLUMN_HOBBIES_UPD.substring(4);
    private static final String JSON_KEY_A_PROPOS = COLUMN_A_PROPOS.substring(4);
    private static final String JSON_KEY_A_PROPOS_UPD = COLUMN_A_PROPOS_UPD.substring(4);
    private static final String JSON_KEY_LOG_DATE = COLUMN_LOG_DATE.substring(4);
    private static final String JSON_KEY_LOG_DATE_UPD = COLUMN_LOG_DATE_UPD.substring(4);
    private static final String JSON_KEY_ADMIN = COLUMN_ADMIN.substring(4);
    private static final String JSON_KEY_ADMIN_UPD = COLUMN_ADMIN_UPD.substring(4);
    private static final String JSON_KEY_PROFILE = COLUMN_PROFILE.substring(4);
    private static final String JSON_KEY_PROFILE_UPD = COLUMN_PROFILE_UPD.substring(4);
    private static final String JSON_KEY_BANNER = COLUMN_BANNER.substring(4);
    private static final String JSON_KEY_BANNER_UPD = COLUMN_BANNER_UPD.substring(4);
    private static final String JSON_KEY_LOCATED = COLUMN_LOCATED.substring(4);
    private static final String JSON_KEY_LOCATED_UPD = COLUMN_LOCATED_UPD.substring(4);
    private static final String JSON_KEY_LATITUDE = COLUMN_LATITUDE.substring(4);
    private static final String JSON_KEY_LATITUDE_UPD = COLUMN_LATITUDE_UPD.substring(4);
    private static final String JSON_KEY_LONGITUDE = COLUMN_LONGITUDE.substring(4);
    private static final String JSON_KEY_LONGITUDE_UPD = COLUMN_LONGITUDE_UPD.substring(4);
    private static final String JSON_KEY_STATUS_DATE = COLUMN_STATUS_DATE.substring(4);
    // TODO: Replace JSON keys by column indexes

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

        data.putString(DATA_KEY_WEB_SERVICE, WebServices.URL_MEMBERS);
        data.putString(DATA_KEY_TOKEN, token);
        data.putByte(DATA_KEY_OPERATION, operation);
        data.putString(DATA_KEY_PSEUDO, pseudo);
        data.putString(DATA_KEY_TABLE_NAME, TABLE_NAME);
        data.putString(DATA_KEY_FIELD_PSEUDO, COLUMN_PSEUDO);
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
                            return (operation == WebServices.OPERATION_SELECT);
                            // Already synchronized for selection but error for any other operation

                        Uri tableUri = Uri.parse(DataProvider.CONTENT_URI + TABLE_NAME);
                        JSONArray entries = reply.getJSONArray(TABLE_NAME);
                        for (int i = 0; i < entries.length(); ++i) {

                            JSONObject entry = (JSONObject) entries.get(i);

                            // Key field
                            String pseudo = entry.getString(JSON_KEY_PSEUDO);

                            // Data fields
                            ContentValues values = new ContentValues();
                            values.put(COLUMN_CODE_CONF, entry.getString(JSON_KEY_CODE_CONF));
                            values.put(COLUMN_CODE_CONF_UPD, entry.getString(JSON_KEY_CODE_CONF_UPD));
                            if (!entry.isNull(JSON_KEY_NOM))
                                values.put(COLUMN_NOM, entry.getString(JSON_KEY_NOM));
                            values.put(COLUMN_NOM_UPD, entry.getString(JSON_KEY_NOM_UPD));
                            if (!entry.isNull(JSON_KEY_PRENOM))
                                values.put(COLUMN_PRENOM, entry.getString(JSON_KEY_PRENOM));
                            values.put(COLUMN_PRENOM_UPD, entry.getString(JSON_KEY_PRENOM_UPD));
                            if (!entry.isNull(JSON_KEY_SEXE))
                                values.put(COLUMN_SEXE, entry.getInt(JSON_KEY_SEXE));
                            values.put(COLUMN_SEXE_UPD, entry.getString(JSON_KEY_SEXE_UPD));
                            if (!entry.isNull(JSON_KEY_BORN_DATE))
                                values.put(COLUMN_BORN_DATE, entry.getString(JSON_KEY_BORN_DATE));
                            values.put(COLUMN_BORN_DATE_UPD, entry.getString(JSON_KEY_BORN_DATE_UPD));
                            if (!entry.isNull(JSON_KEY_ADRESSE))
                                values.put(COLUMN_ADRESSE, entry.getString(JSON_KEY_ADRESSE));
                            values.put(COLUMN_ADRESSE_UPD, entry.getString(JSON_KEY_ADRESSE_UPD));
                            if (!entry.isNull(JSON_KEY_VILLE))
                                values.put(COLUMN_VILLE, entry.getString(JSON_KEY_VILLE));
                            values.put(COLUMN_VILLE_UPD, entry.getString(JSON_KEY_VILLE_UPD));
                            if (!entry.isNull(JSON_KEY_POSTAL))
                                values.put(COLUMN_POSTAL, entry.getString(JSON_KEY_POSTAL));
                            values.put(COLUMN_POSTAL_UPD, entry.getString(JSON_KEY_POSTAL_UPD));
                            if (!entry.isNull(JSON_KEY_EMAIL))
                                values.put(COLUMN_EMAIL, entry.getString(JSON_KEY_EMAIL));
                            values.put(COLUMN_EMAIL_UPD, entry.getString(JSON_KEY_EMAIL_UPD));
                            if (!entry.isNull(JSON_KEY_HOBBIES))
                                values.put(COLUMN_HOBBIES, entry.getString(JSON_KEY_HOBBIES));
                            values.put(COLUMN_HOBBIES_UPD, entry.getString(JSON_KEY_HOBBIES_UPD));
                            if (!entry.isNull(JSON_KEY_A_PROPOS))
                                values.put(COLUMN_A_PROPOS, entry.getString(JSON_KEY_A_PROPOS));
                            values.put(COLUMN_A_PROPOS_UPD, entry.getString(JSON_KEY_A_PROPOS_UPD));
                            if (!entry.isNull(JSON_KEY_LOG_DATE))
                                values.put(COLUMN_LOG_DATE, entry.getString(JSON_KEY_LOG_DATE));
                            values.put(COLUMN_LOG_DATE_UPD, entry.getString(JSON_KEY_LOG_DATE_UPD));
                            values.put(COLUMN_ADMIN, entry.getInt(JSON_KEY_ADMIN));
                            values.put(COLUMN_ADMIN_UPD, entry.getString(JSON_KEY_ADMIN_UPD));
                            if (!entry.isNull(JSON_KEY_PROFILE))
                                values.put(COLUMN_PROFILE, entry.getString(JSON_KEY_PROFILE));
                            values.put(COLUMN_PROFILE_UPD, entry.getString(JSON_KEY_PROFILE_UPD));
                            if (!entry.isNull(JSON_KEY_BANNER))
                                values.put(COLUMN_BANNER, entry.getString(JSON_KEY_BANNER));
                            values.put(COLUMN_BANNER_UPD, entry.getString(JSON_KEY_BANNER_UPD));
                            values.put(COLUMN_LOCATED, entry.getInt(JSON_KEY_LOCATED));
                            values.put(COLUMN_LOCATED_UPD, entry.getString(JSON_KEY_LOCATED_UPD));
                            if (!entry.isNull(JSON_KEY_LATITUDE))
                                values.put(COLUMN_LATITUDE, entry.getDouble(JSON_KEY_LATITUDE));
                            values.put(COLUMN_LATITUDE_UPD, entry.getString(JSON_KEY_LATITUDE_UPD));
                            if (!entry.isNull(JSON_KEY_LONGITUDE))
                                values.put(COLUMN_LONGITUDE, entry.getDouble(JSON_KEY_LONGITUDE));
                            values.put(COLUMN_LONGITUDE_UPD, entry.getString(JSON_KEY_LONGITUDE_UPD));

                            values.put(Constants.DATA_COLUMN_STATUS_DATE, entry.getString(JSON_KEY_STATUS_DATE));
                            values.put(Constants.DATA_COLUMN_SYNCHRONIZED, Synchronized.DONE.getValue());

                            // Check if entry already exists
                            String selection = COLUMN_PSEUDO + '=' + DatabaseUtils.sqlEscapeString(pseudo);
                            Cursor cursor = resolver.query(tableUri, null, selection, null, null);
                            if (cursor.moveToFirst()) { // DB entry exists

                                if (entry.getInt(WebServices.JSON_KEY_STATUS) == STATUS_FIELD_DELETED) {
                                    cursor.close();

                                    ////// Delete entry (definitively)
                                    values.put(Constants.DATA_COLUMN_SYNCHRONIZED,
                                            Synchronized.TO_DELETE.getValue());
                                    resolver.update(tableUri, values, selection, null);
                                    resolver.delete(tableUri,
                                            selection + " AND " + Constants.DATA_DELETE_SELECTION, null);

                                    ++syncResult.deleted;
                                }
                                else { ////// Update entry

                                    // Remove fields that must not be synchronized yet coz updated after
                                    // remote DB fields
                                    boolean removed = false;

                                    if (cursor.getString(COLUMN_INDEX_CODE_CONF_UPD)
                                            .compareTo(entry.getString(JSON_KEY_CODE_CONF_UPD)) > 0) {
                                        values.remove(COLUMN_CODE_CONF);
                                        values.remove(COLUMN_CODE_CONF_UPD);
                                        removed = true;
                                    }
                                    if (cursor.getString(COLUMN_INDEX_NOM_UPD)
                                            .compareTo(entry.getString(JSON_KEY_NOM_UPD)) > 0) {
                                        values.remove(COLUMN_NOM);
                                        values.remove(COLUMN_NOM_UPD);
                                        removed = true;
                                    }
                                    if (cursor.getString(COLUMN_INDEX_PRENOM_UPD)
                                            .compareTo(entry.getString(JSON_KEY_PRENOM_UPD)) > 0) {
                                        values.remove(COLUMN_PRENOM);
                                        values.remove(COLUMN_PRENOM_UPD);
                                        removed = true;
                                    }
                                    if (cursor.getString(COLUMN_INDEX_SEXE_UPD)
                                            .compareTo(entry.getString(JSON_KEY_SEXE_UPD)) > 0) {
                                        values.remove(COLUMN_SEXE);
                                        values.remove(COLUMN_SEXE_UPD);
                                        removed = true;
                                    }
                                    if (cursor.getString(COLUMN_INDEX_BORN_DATE_UPD)
                                            .compareTo(entry.getString(JSON_KEY_BORN_DATE_UPD)) > 0) {
                                        values.remove(COLUMN_BORN_DATE);
                                        values.remove(COLUMN_BORN_DATE_UPD);
                                        removed = true;
                                    }
                                    if (cursor.getString(COLUMN_INDEX_ADRESSE_UPD)
                                            .compareTo(entry.getString(JSON_KEY_ADRESSE_UPD)) > 0) {
                                        values.remove(COLUMN_ADRESSE);
                                        values.remove(COLUMN_ADRESSE_UPD);
                                        removed = true;
                                    }
                                    if (cursor.getString(COLUMN_INDEX_VILLE_UPD)
                                            .compareTo(entry.getString(JSON_KEY_VILLE_UPD)) > 0) {
                                        values.remove(COLUMN_VILLE);
                                        values.remove(COLUMN_VILLE_UPD);
                                        removed = true;
                                    }
                                    if (cursor.getString(COLUMN_INDEX_POSTAL_UPD)
                                            .compareTo(entry.getString(JSON_KEY_POSTAL_UPD)) > 0) {
                                        values.remove(COLUMN_POSTAL);
                                        values.remove(COLUMN_POSTAL_UPD);
                                        removed = true;
                                    }
                                    if (cursor.getString(COLUMN_INDEX_EMAIL_UPD)
                                            .compareTo(entry.getString(JSON_KEY_EMAIL_UPD)) > 0) {
                                        values.remove(COLUMN_EMAIL);
                                        values.remove(COLUMN_EMAIL_UPD);
                                        removed = true;
                                    }
                                    if (cursor.getString(COLUMN_INDEX_HOBBIES_UPD)
                                            .compareTo(entry.getString(JSON_KEY_HOBBIES_UPD)) > 0) {
                                        values.remove(COLUMN_HOBBIES);
                                        values.remove(COLUMN_HOBBIES_UPD);
                                        removed = true;
                                    }
                                    if (cursor.getString(COLUMN_INDEX_A_PROPOS_UPD)
                                            .compareTo(entry.getString(JSON_KEY_A_PROPOS_UPD)) > 0) {
                                        values.remove(COLUMN_A_PROPOS);
                                        values.remove(COLUMN_A_PROPOS_UPD);
                                        removed = true;
                                    }
                                    if (cursor.getString(COLUMN_INDEX_LOG_DATE_UPD)
                                            .compareTo(entry.getString(JSON_KEY_LOG_DATE_UPD)) > 0) {
                                        values.remove(COLUMN_LOG_DATE);
                                        values.remove(COLUMN_LOG_DATE_UPD);
                                        removed = true;
                                    }
                                    if (cursor.getString(COLUMN_INDEX_ADMIN_UPD)
                                            .compareTo(entry.getString(JSON_KEY_ADMIN_UPD)) > 0) {
                                        values.remove(COLUMN_ADMIN);
                                        values.remove(COLUMN_ADMIN_UPD);
                                        removed = true;
                                    }
                                    if (cursor.getString(COLUMN_INDEX_PROFILE_UPD)
                                            .compareTo(entry.getString(JSON_KEY_PROFILE_UPD)) > 0) {
                                        values.remove(COLUMN_PROFILE);
                                        values.remove(COLUMN_PROFILE_UPD);
                                        removed = true;
                                    }
                                    if (cursor.getString(COLUMN_INDEX_BANNER_UPD)
                                            .compareTo(entry.getString(JSON_KEY_BANNER_UPD)) > 0) {
                                        values.remove(COLUMN_BANNER);
                                        values.remove(COLUMN_BANNER_UPD);
                                        removed = true;
                                    }
                                    if (cursor.getString(COLUMN_INDEX_LOCATED_UPD)
                                            .compareTo(entry.getString(JSON_KEY_LOCATED_UPD)) > 0) {
                                        values.remove(COLUMN_LOCATED);
                                        values.remove(COLUMN_LOCATED_UPD);
                                        removed = true;
                                    }
                                    if (cursor.getString(COLUMN_INDEX_LATITUDE_UPD)
                                            .compareTo(entry.getString(JSON_KEY_LATITUDE_UPD)) > 0) {
                                        values.remove(COLUMN_LATITUDE);
                                        values.remove(COLUMN_LATITUDE_UPD);
                                        removed = true;
                                    }
                                    if (cursor.getString(COLUMN_INDEX_LONGITUDE_UPD)
                                            .compareTo(entry.getString(JSON_KEY_LONGITUDE_UPD)) > 0) {
                                        values.remove(COLUMN_LONGITUDE);
                                        values.remove(COLUMN_LONGITUDE_UPD);
                                        removed = true;
                                    }

                                    if (removed) { // Keep local synchronized & status date fields

                                        values.put(Constants.DATA_COLUMN_SYNCHRONIZED,
                                                cursor.getInt(COLUMN_INDEX_SYNCHRONIZED));
                                        values.put(Constants.DATA_COLUMN_STATUS_DATE,
                                                cursor.getString(COLUMN_INDEX_STATUS_DATE));
                                    }
                                    cursor.close();
                                    resolver.update(tableUri, values, selection, null);
                                    ++syncResult.updated;
                                }

                            } else {
                                cursor.close();

                                if (entry.getInt(WebServices.JSON_KEY_STATUS) != STATUS_FIELD_DELETED) {

                                    ////// Insert entry into DB
                                    values.put(COLUMN_PSEUDO, pseudo);
                                    resolver.insert(tableUri, values);

                                    ++syncResult.inserted;
                                }
                                //else // Do not add a deleted entry (created & removed when offline)
                            }
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
