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

    public static final int GENDER_MALE = 2;
    public static final int GENDER_FEMALE = 1;
    // Genders

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
    public static final String COLUMN_PHONE = "CAM_Phone";
    public static final String COLUMN_PHONE_UPD = "CAM_PhoneUPD";
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
    public static final String COLUMN_DEVICE = "CAM_Device";
    public static final String COLUMN_DEVICE_UPD = "CAM_DeviceUPD";
    public static final String COLUMN_DEVICE_ID = "CAM_DevId";
    public static final String COLUMN_DEVICE_ID_UPD = "CAM_DevIdUPD";
    public static final String COLUMN_LATITUDE = "CAM_Latitude";
    public static final String COLUMN_LATITUDE_UPD = "CAM_LatitudeUPD";
    public static final String COLUMN_LONGITUDE = "CAM_Longitude";
    public static final String COLUMN_LONGITUDE_UPD = "CAM_LongitudeUPD";
    private static final String COLUMN_STATUS_DATE = "CAM_StatusDate";

    // Columns index
    public static final short COLUMN_INDEX_PSEUDO = 1; // DataField.COLUMN_INDEX_ID + 1
    public static final short COLUMN_INDEX_CODE_CONF = 2;
    private static final short COLUMN_INDEX_CODE_CONF_UPD = 3;
    public static final short COLUMN_INDEX_NOM = 4;
    private static final short COLUMN_INDEX_NOM_UPD = 5;
    public static final short COLUMN_INDEX_PRENOM = 6;
    private static final short COLUMN_INDEX_PRENOM_UPD = 7;
    public static final short COLUMN_INDEX_SEXE = 8;
    private static final short COLUMN_INDEX_SEXE_UPD = 9;
    public static final short COLUMN_INDEX_BORN_DATE = 10;
    private static final short COLUMN_INDEX_BORN_DATE_UPD = 11;
    public static final short COLUMN_INDEX_ADRESSE = 12;
    private static final short COLUMN_INDEX_ADRESSE_UPD = 13;
    public static final short COLUMN_INDEX_VILLE = 14;
    private static final short COLUMN_INDEX_VILLE_UPD = 15;
    public static final short COLUMN_INDEX_POSTAL = 16;
    private static final short COLUMN_INDEX_POSTAL_UPD = 17;
    public static final short COLUMN_INDEX_PHONE = 18;
    private static final short COLUMN_INDEX_PHONE_UPD = 19;
    public static final short COLUMN_INDEX_EMAIL = 20;
    private static final short COLUMN_INDEX_EMAIL_UPD = 21;
    public static final short COLUMN_INDEX_HOBBIES = 22;
    private static final short COLUMN_INDEX_HOBBIES_UPD = 23;
    public static final short COLUMN_INDEX_A_PROPOS = 24;
    private static final short COLUMN_INDEX_A_PROPOS_UPD = 25;
    private static final short COLUMN_INDEX_LOG_DATE = 26;
    private static final short COLUMN_INDEX_LOG_DATE_UPD = 27;
    private static final short COLUMN_INDEX_ADMIN = 28;
    private static final short COLUMN_INDEX_ADMIN_UPD = 29;
    private static final short COLUMN_INDEX_PROFILE = 30;
    private static final short COLUMN_INDEX_PROFILE_UPD = 31;
    private static final short COLUMN_INDEX_BANNER = 32;
    private static final short COLUMN_INDEX_BANNER_UPD = 33;
    public static final short COLUMN_INDEX_DEVICE = 34;
    private static final short COLUMN_INDEX_DEVICE_UPD = 35;
    public static final short COLUMN_INDEX_DEVICE_ID = 36;
    private static final short COLUMN_INDEX_DEVICE_ID_UPD = 37;
    private static final short COLUMN_INDEX_LATITUDE = 38;
    private static final short COLUMN_INDEX_LATITUDE_UPD = 39;
    private static final short COLUMN_INDEX_LONGITUDE = 40;
    private static final short COLUMN_INDEX_LONGITUDE_UPD = 41;
    private static final short COLUMN_INDEX_STATUS_DATE = 42;
    private static final short COLUMN_INDEX_SYNCHRONIZED = 43;

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
                COLUMN_PHONE + " TEXT," +
                COLUMN_PHONE_UPD + " TEXT NOT NULL," +
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
                COLUMN_DEVICE + " TEXT," +
                COLUMN_DEVICE_UPD + " TEXT NOT NULL," +
                COLUMN_DEVICE_ID + " TEXT," +
                COLUMN_DEVICE_ID_UPD + " TEXT NOT NULL," +
                COLUMN_LATITUDE + " REAL," +
                COLUMN_LATITUDE_UPD + " TEXT NOT NULL," +
                COLUMN_LONGITUDE + " REAL," +
                COLUMN_LONGITUDE_UPD + " TEXT NOT NULL," +

                Constants.DATA_COLUMN_STATUS_DATE + " TEXT NOT NULL," +
                Constants.DATA_COLUMN_SYNCHRONIZED + " INTEGER NOT NULL" +

                ");");

        // Add indexes
        db.execSQL("CREATE INDEX " + TABLE_NAME + JSON_KEY_PSEUDO + " ON " +
                TABLE_NAME + '(' + COLUMN_PSEUDO + ')');
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
        Cursor cursor = resolver.query(Uri.parse(DataProvider.CONTENT_URI + TABLE_NAME), null, '(' +
                Constants.DATA_COLUMN_STATUS_DATE + '<' + COLUMN_CODE_CONF_UPD + " OR " +
                Constants.DATA_COLUMN_STATUS_DATE + '<' + COLUMN_NOM_UPD + " OR " +
                Constants.DATA_COLUMN_STATUS_DATE + '<' + COLUMN_PRENOM_UPD + " OR " +
                Constants.DATA_COLUMN_STATUS_DATE + '<' + COLUMN_SEXE_UPD + " OR " +
                Constants.DATA_COLUMN_STATUS_DATE + '<' + COLUMN_BORN_DATE_UPD + " OR " +
                Constants.DATA_COLUMN_STATUS_DATE + '<' + COLUMN_ADRESSE_UPD + " OR " +
                Constants.DATA_COLUMN_STATUS_DATE + '<' + COLUMN_VILLE_UPD + " OR " +
                Constants.DATA_COLUMN_STATUS_DATE + '<' + COLUMN_POSTAL_UPD + " OR " +
                Constants.DATA_COLUMN_STATUS_DATE + '<' + COLUMN_PHONE_UPD + " OR " +
                Constants.DATA_COLUMN_STATUS_DATE + '<' + COLUMN_EMAIL_UPD + " OR " +
                Constants.DATA_COLUMN_STATUS_DATE + '<' + COLUMN_HOBBIES_UPD + " OR " +
                Constants.DATA_COLUMN_STATUS_DATE + '<' + COLUMN_A_PROPOS_UPD + " OR " +
                Constants.DATA_COLUMN_STATUS_DATE + '<' + COLUMN_LOG_DATE_UPD + " OR " +
                Constants.DATA_COLUMN_STATUS_DATE + '<' + COLUMN_ADMIN_UPD + " OR " +
                Constants.DATA_COLUMN_STATUS_DATE + '<' + COLUMN_PROFILE_UPD + " OR " +
                Constants.DATA_COLUMN_STATUS_DATE + '<' + COLUMN_BANNER_UPD + " OR " +
                Constants.DATA_COLUMN_STATUS_DATE + '<' + COLUMN_DEVICE_UPD + " OR " +
                Constants.DATA_COLUMN_STATUS_DATE + '<' + COLUMN_DEVICE_ID_UPD + " OR " +
                Constants.DATA_COLUMN_STATUS_DATE + '<' + COLUMN_LATITUDE_UPD + " OR " +
                Constants.DATA_COLUMN_STATUS_DATE + '<' + COLUMN_LONGITUDE_UPD + ") AND (" +
                        Constants.DATA_COLUMN_SYNCHRONIZED + '=' + Synchronized.TO_UPDATE.getValue() + " OR " +
                        Constants.DATA_COLUMN_SYNCHRONIZED + '=' + (Synchronized.TO_UPDATE.getValue() |
                        Synchronized.IN_PROGRESS.getValue()) + ')', null, null);
        if (cursor.moveToFirst()) {
            try {

                JSONArray keysArray = new JSONArray();
                JSONArray updatesArray = new JSONArray();
                JSONArray statusArray = new JSONArray();
                do {

                    // Keys
                    JSONObject key = new JSONObject();
                    key.put(JSON_KEY_PSEUDO, cursor.getString(COLUMN_INDEX_PSEUDO));

                    // Updates & Status
                    JSONObject update = new JSONObject();
                    JSONObject status = new JSONObject();

                    if (cursor.getString(COLUMN_INDEX_STATUS_DATE).compareTo(cursor.getString(COLUMN_INDEX_CODE_CONF_UPD)) < 0) {
                        update.put(JSON_KEY_CODE_CONF, cursor.getString(COLUMN_INDEX_CODE_CONF));
                        status.put(JSON_KEY_CODE_CONF_UPD, cursor.getString(COLUMN_INDEX_CODE_CONF_UPD));
                    }
                    if (cursor.getString(COLUMN_INDEX_STATUS_DATE).compareTo(cursor.getString(COLUMN_INDEX_NOM_UPD)) < 0) {
                        update.put(JSON_KEY_NOM, (!cursor.isNull(COLUMN_INDEX_NOM))? cursor.getString(COLUMN_INDEX_NOM):null);
                        status.put(JSON_KEY_NOM_UPD, cursor.getString(COLUMN_INDEX_NOM_UPD));
                    }
                    if (cursor.getString(COLUMN_INDEX_STATUS_DATE).compareTo(cursor.getString(COLUMN_INDEX_PRENOM_UPD)) < 0) {
                        update.put(JSON_KEY_PRENOM, (!cursor.isNull(COLUMN_INDEX_PRENOM))? cursor.getString(COLUMN_INDEX_PRENOM):null);
                        status.put(JSON_KEY_PRENOM_UPD, cursor.getString(COLUMN_INDEX_PRENOM_UPD));
                    }
                    if (cursor.getString(COLUMN_INDEX_STATUS_DATE).compareTo(cursor.getString(COLUMN_INDEX_SEXE_UPD)) < 0) {
                        update.put(JSON_KEY_SEXE, cursor.getInt(COLUMN_INDEX_SEXE));
                        status.put(JSON_KEY_SEXE_UPD, cursor.getString(COLUMN_INDEX_SEXE_UPD));
                    }
                    if (cursor.getString(COLUMN_INDEX_STATUS_DATE).compareTo(cursor.getString(COLUMN_INDEX_BORN_DATE_UPD)) < 0) {
                        update.put(JSON_KEY_BORN_DATE, (!cursor.isNull(COLUMN_INDEX_BORN_DATE))? cursor.getString(COLUMN_INDEX_BORN_DATE):null);
                        status.put(JSON_KEY_BORN_DATE_UPD, cursor.getString(COLUMN_INDEX_BORN_DATE_UPD));
                    }
                    if (cursor.getString(COLUMN_INDEX_STATUS_DATE).compareTo(cursor.getString(COLUMN_INDEX_ADRESSE_UPD)) < 0) {
                        update.put(JSON_KEY_ADRESSE, (!cursor.isNull(COLUMN_INDEX_ADRESSE))? cursor.getString(COLUMN_INDEX_ADRESSE):null);
                        status.put(JSON_KEY_ADRESSE_UPD, cursor.getString(COLUMN_INDEX_ADRESSE_UPD));
                    }
                    if (cursor.getString(COLUMN_INDEX_STATUS_DATE).compareTo(cursor.getString(COLUMN_INDEX_VILLE_UPD)) < 0) {
                        update.put(JSON_KEY_VILLE, (!cursor.isNull(COLUMN_INDEX_VILLE))? cursor.getString(COLUMN_INDEX_VILLE):null);
                        status.put(JSON_KEY_VILLE_UPD, cursor.getString(COLUMN_INDEX_VILLE_UPD));
                    }
                    if (cursor.getString(COLUMN_INDEX_STATUS_DATE).compareTo(cursor.getString(COLUMN_INDEX_POSTAL_UPD)) < 0) {
                        update.put(JSON_KEY_POSTAL, (!cursor.isNull(COLUMN_INDEX_POSTAL))? cursor.getString(COLUMN_INDEX_POSTAL):null);
                        status.put(JSON_KEY_POSTAL_UPD, cursor.getString(COLUMN_INDEX_POSTAL_UPD));
                    }
                    if (cursor.getString(COLUMN_INDEX_STATUS_DATE).compareTo(cursor.getString(COLUMN_INDEX_PHONE_UPD)) < 0) {
                        update.put(JSON_KEY_PHONE, (!cursor.isNull(COLUMN_INDEX_PHONE))? cursor.getString(COLUMN_INDEX_PHONE):null);
                        status.put(JSON_KEY_PHONE_UPD, cursor.getString(COLUMN_INDEX_PHONE_UPD));
                    }
                    if (cursor.getString(COLUMN_INDEX_STATUS_DATE).compareTo(cursor.getString(COLUMN_INDEX_EMAIL_UPD)) < 0) {
                        update.put(JSON_KEY_EMAIL, (!cursor.isNull(COLUMN_INDEX_EMAIL))? cursor.getString(COLUMN_INDEX_EMAIL):null);
                        status.put(JSON_KEY_EMAIL_UPD, cursor.getString(COLUMN_INDEX_EMAIL_UPD));
                    }
                    if (cursor.getString(COLUMN_INDEX_STATUS_DATE).compareTo(cursor.getString(COLUMN_INDEX_HOBBIES_UPD)) < 0) {
                        update.put(JSON_KEY_HOBBIES, (!cursor.isNull(COLUMN_INDEX_HOBBIES))? cursor.getString(COLUMN_INDEX_HOBBIES):null);
                        status.put(JSON_KEY_HOBBIES_UPD, cursor.getString(COLUMN_INDEX_HOBBIES_UPD));
                    }
                    if (cursor.getString(COLUMN_INDEX_STATUS_DATE).compareTo(cursor.getString(COLUMN_INDEX_A_PROPOS_UPD)) < 0) {
                        update.put(JSON_KEY_A_PROPOS, (!cursor.isNull(COLUMN_INDEX_A_PROPOS))? cursor.getString(COLUMN_INDEX_A_PROPOS):null);
                        status.put(JSON_KEY_A_PROPOS_UPD, cursor.getString(COLUMN_INDEX_A_PROPOS_UPD));
                    }
                    if (cursor.getString(COLUMN_INDEX_STATUS_DATE).compareTo(cursor.getString(COLUMN_INDEX_LOG_DATE_UPD)) < 0) {
                        update.put(JSON_KEY_LOG_DATE, cursor.getString(COLUMN_INDEX_LOG_DATE));
                        status.put(JSON_KEY_LOG_DATE_UPD, cursor.getString(COLUMN_INDEX_LOG_DATE_UPD));
                    }
                    if (cursor.getString(COLUMN_INDEX_STATUS_DATE).compareTo(cursor.getString(COLUMN_INDEX_ADMIN_UPD)) < 0) {
                        update.put(JSON_KEY_ADMIN, cursor.getInt(COLUMN_INDEX_ADMIN));
                        status.put(JSON_KEY_ADMIN_UPD, cursor.getString(COLUMN_INDEX_ADMIN_UPD));
                    }
                    if (cursor.getString(COLUMN_INDEX_STATUS_DATE).compareTo(cursor.getString(COLUMN_INDEX_PROFILE_UPD)) < 0) {
                        update.put(JSON_KEY_PROFILE, (!cursor.isNull(COLUMN_INDEX_PROFILE))? cursor.getString(COLUMN_INDEX_PROFILE):null);
                        status.put(JSON_KEY_PROFILE_UPD, cursor.getString(COLUMN_INDEX_PROFILE_UPD));
                    }
                    if (cursor.getString(COLUMN_INDEX_STATUS_DATE).compareTo(cursor.getString(COLUMN_INDEX_BANNER_UPD)) < 0) {
                        update.put(JSON_KEY_BANNER, (!cursor.isNull(COLUMN_INDEX_BANNER))? cursor.getString(COLUMN_INDEX_BANNER):null);
                        status.put(JSON_KEY_BANNER_UPD, cursor.getString(COLUMN_INDEX_BANNER_UPD));
                    }
                    if (cursor.getString(COLUMN_INDEX_STATUS_DATE).compareTo(cursor.getString(COLUMN_INDEX_DEVICE_UPD)) < 0) {
                        update.put(JSON_KEY_DEVICE, (!cursor.isNull(COLUMN_INDEX_DEVICE))? cursor.getString(COLUMN_INDEX_DEVICE):null);
                        status.put(JSON_KEY_DEVICE_UPD, cursor.getString(COLUMN_INDEX_DEVICE_UPD));
                    }
                    if (cursor.getString(COLUMN_INDEX_STATUS_DATE).compareTo(cursor.getString(COLUMN_INDEX_DEVICE_ID_UPD)) < 0) {
                        update.put(JSON_KEY_DEVICE_ID, (!cursor.isNull(COLUMN_INDEX_DEVICE_ID))? cursor.getString(COLUMN_INDEX_DEVICE_ID):null);
                        status.put(JSON_KEY_DEVICE_ID_UPD, cursor.getString(COLUMN_INDEX_DEVICE_ID_UPD));
                    }
                    if (cursor.getString(COLUMN_INDEX_STATUS_DATE).compareTo(cursor.getString(COLUMN_INDEX_LATITUDE_UPD)) < 0) {
                        update.put(JSON_KEY_LATITUDE, cursor.getDouble(COLUMN_INDEX_LATITUDE));
                        status.put(JSON_KEY_LATITUDE_UPD, cursor.getString(COLUMN_INDEX_LATITUDE_UPD));
                    }
                    if (cursor.getString(COLUMN_INDEX_STATUS_DATE).compareTo(cursor.getString(COLUMN_INDEX_LONGITUDE_UPD)) < 0) {
                        update.put(JSON_KEY_LONGITUDE, cursor.getDouble(COLUMN_INDEX_LONGITUDE));
                        status.put(JSON_KEY_LONGITUDE_UPD, cursor.getString(COLUMN_INDEX_LONGITUDE_UPD));
                    }

                    //////
                    keysArray.put(key);
                    updatesArray.put(update);
                    statusArray.put(status);

                } while (cursor.moveToNext());

                //////
                //Logs.add(Logs.Type.I, "Keys: " + keysArray.toString());
                //Logs.add(Logs.Type.I, "Updates: " + updatesArray.toString());
                //Logs.add(Logs.Type.I, "Status: " + statusArray.toString());

                updated.put(WebServices.DATA_KEYS, keysArray.toString());
                updated.put(WebServices.DATA_UPDATES, updatesArray.toString());
                updated.put(WebServices.DATA_STATUS, statusArray.toString());

            } catch (JSONException e) {
                Logs.add(Logs.Type.F, "Unexpected error: " + e.getMessage());
            }
        }
        cursor.close();
        return updated;
    }
    @Override
    public ContentValues syncDeleted(ContentResolver resolver, String pseudo) {
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";pseudo: " + pseudo);
        ContentValues deleted = new ContentValues();
        return deleted; // Not defined for this table
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
    private static final String JSON_KEY_PHONE = COLUMN_PHONE.substring(4);
    private static final String JSON_KEY_PHONE_UPD = COLUMN_PHONE_UPD.substring(4);
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
    private static final String JSON_KEY_DEVICE = COLUMN_DEVICE.substring(4);
    private static final String JSON_KEY_DEVICE_UPD = COLUMN_DEVICE_UPD.substring(4);
    private static final String JSON_KEY_DEVICE_ID = COLUMN_DEVICE_ID.substring(4);
    private static final String JSON_KEY_DEVICE_ID_UPD = COLUMN_DEVICE_ID_UPD.substring(4);
    private static final String JSON_KEY_LATITUDE = COLUMN_LATITUDE.substring(4);
    private static final String JSON_KEY_LATITUDE_UPD = COLUMN_LATITUDE_UPD.substring(4);
    private static final String JSON_KEY_LONGITUDE = COLUMN_LONGITUDE.substring(4);
    private static final String JSON_KEY_LONGITUDE_UPD = COLUMN_LONGITUDE_UPD.substring(4);
    private static final String JSON_KEY_STATUS_DATE = COLUMN_STATUS_DATE.substring(4);
    // TODO: Replace JSON keys by column indexes

    @Override
    public @Nullable SyncResult synchronize(final ContentResolver resolver, final byte operation,
                                            Bundle syncData, @Nullable ContentValues postData) {

        // Synchronize data from remote to local DB (return inserted, deleted or
        // updated entry count & NO_DATA if error)
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";operation: " + operation +
                ";syncData: " + syncData + ";postData: " + postData);

        final SyncResult syncResult = new SyncResult();

        syncData.putString(DATA_KEY_WEB_SERVICE, WebServices.URL_MEMBERS);
        syncData.putByte(DATA_KEY_OPERATION, operation);
        syncData.putString(DATA_KEY_TABLE_NAME, TABLE_NAME);

        syncData.remove(DATA_KEY_FIELD_PSEUDO); // No pseudo field criteria for this table
        syncData.remove(DATA_KEY_FIELD_DATE); // No date field criteria for this table
        String url = getSyncUrlRequest(resolver, syncData);

        // Send remote DB request
        Internet.DownloadResult result = Internet.downloadHttpRequest(url, postData, null,
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
                            if (!entry.isNull(JSON_KEY_CODE_CONF)) {
                                values.put(COLUMN_CODE_CONF, entry.getString(JSON_KEY_CODE_CONF));
                                values.put(COLUMN_CODE_CONF_UPD, entry.getString(JSON_KEY_CODE_CONF_UPD));
                            } else {
                                values.put(COLUMN_CODE_CONF, Constants.UNDEFINED);
                                values.put(COLUMN_CODE_CONF_UPD, Constants.UNDEFINED);
                            }
                            if (!entry.isNull(JSON_KEY_NOM))
                                values.put(COLUMN_NOM, entry.getString(JSON_KEY_NOM));
                            else
                                values.putNull(COLUMN_NOM);
                            values.put(COLUMN_NOM_UPD, entry.getString(JSON_KEY_NOM_UPD));
                            if (!entry.isNull(JSON_KEY_PRENOM))
                                values.put(COLUMN_PRENOM, entry.getString(JSON_KEY_PRENOM));
                            else
                                values.putNull(COLUMN_PRENOM);
                            values.put(COLUMN_PRENOM_UPD, entry.getString(JSON_KEY_PRENOM_UPD));
                            if (!entry.isNull(JSON_KEY_SEXE))
                                values.put(COLUMN_SEXE, entry.getInt(JSON_KEY_SEXE));
                            //else // Cannot be set to null
                            values.put(COLUMN_SEXE_UPD, entry.getString(JSON_KEY_SEXE_UPD));
                            if (!entry.isNull(JSON_KEY_BORN_DATE))
                                values.put(COLUMN_BORN_DATE, entry.getString(JSON_KEY_BORN_DATE));
                            else
                                values.putNull(COLUMN_BORN_DATE);
                            values.put(COLUMN_BORN_DATE_UPD, entry.getString(JSON_KEY_BORN_DATE_UPD));
                            if (!entry.isNull(JSON_KEY_ADRESSE))
                                values.put(COLUMN_ADRESSE, entry.getString(JSON_KEY_ADRESSE));
                            else
                                values.putNull(COLUMN_ADRESSE);
                            values.put(COLUMN_ADRESSE_UPD, entry.getString(JSON_KEY_ADRESSE_UPD));
                            if (!entry.isNull(JSON_KEY_VILLE))
                                values.put(COLUMN_VILLE, entry.getString(JSON_KEY_VILLE));
                            else
                                values.putNull(COLUMN_VILLE);
                            values.put(COLUMN_VILLE_UPD, entry.getString(JSON_KEY_VILLE_UPD));
                            if (!entry.isNull(JSON_KEY_POSTAL))
                                values.put(COLUMN_POSTAL, entry.getString(JSON_KEY_POSTAL));
                            else
                                values.putNull(COLUMN_POSTAL);
                            values.put(COLUMN_POSTAL_UPD, entry.getString(JSON_KEY_POSTAL_UPD));
                            if (!entry.isNull(JSON_KEY_PHONE))
                                values.put(COLUMN_PHONE, entry.getString(JSON_KEY_PHONE));
                            else
                                values.putNull(COLUMN_PHONE);
                            values.put(COLUMN_PHONE_UPD, entry.getString(JSON_KEY_PHONE_UPD));
                            if (!entry.isNull(JSON_KEY_EMAIL))
                                values.put(COLUMN_EMAIL, entry.getString(JSON_KEY_EMAIL));
                            else
                                values.putNull(COLUMN_EMAIL);
                            values.put(COLUMN_EMAIL_UPD, entry.getString(JSON_KEY_EMAIL_UPD));
                            if (!entry.isNull(JSON_KEY_HOBBIES))
                                values.put(COLUMN_HOBBIES, entry.getString(JSON_KEY_HOBBIES));
                            else
                                values.putNull(COLUMN_HOBBIES);
                            values.put(COLUMN_HOBBIES_UPD, entry.getString(JSON_KEY_HOBBIES_UPD));
                            if (!entry.isNull(JSON_KEY_A_PROPOS))
                                values.put(COLUMN_A_PROPOS, entry.getString(JSON_KEY_A_PROPOS));
                            else
                                values.putNull(COLUMN_A_PROPOS);
                            values.put(COLUMN_A_PROPOS_UPD, entry.getString(JSON_KEY_A_PROPOS_UPD));
                            if (!entry.isNull(JSON_KEY_LOG_DATE))
                                values.put(COLUMN_LOG_DATE, entry.getString(JSON_KEY_LOG_DATE));
                            //else // Cannot be set to null
                            values.put(COLUMN_LOG_DATE_UPD, entry.getString(JSON_KEY_LOG_DATE_UPD));
                            values.put(COLUMN_ADMIN, entry.getInt(JSON_KEY_ADMIN));
                            values.put(COLUMN_ADMIN_UPD, entry.getString(JSON_KEY_ADMIN_UPD));
                            if (!entry.isNull(JSON_KEY_PROFILE))
                                values.put(COLUMN_PROFILE, entry.getString(JSON_KEY_PROFILE));
                            else
                                values.putNull(COLUMN_PROFILE);
                            values.put(COLUMN_PROFILE_UPD, entry.getString(JSON_KEY_PROFILE_UPD));
                            if (!entry.isNull(JSON_KEY_BANNER))
                                values.put(COLUMN_BANNER, entry.getString(JSON_KEY_BANNER));
                            else
                                values.putNull(COLUMN_BANNER);
                            values.put(COLUMN_BANNER_UPD, entry.getString(JSON_KEY_BANNER_UPD));
                            if (!entry.isNull(JSON_KEY_DEVICE))
                                values.put(COLUMN_DEVICE, entry.getString(JSON_KEY_DEVICE));
                            else
                                values.putNull(COLUMN_DEVICE);
                            values.put(COLUMN_DEVICE_UPD, entry.getString(JSON_KEY_DEVICE_UPD));
                            if (!entry.isNull(JSON_KEY_DEVICE_ID))
                                values.put(COLUMN_DEVICE_ID, entry.getString(JSON_KEY_DEVICE_ID));
                            else
                                values.putNull(COLUMN_DEVICE_ID);
                            values.put(COLUMN_DEVICE_ID_UPD, entry.getString(JSON_KEY_DEVICE_ID_UPD));
                            if (!entry.isNull(JSON_KEY_LATITUDE))
                                values.put(COLUMN_LATITUDE, entry.getDouble(JSON_KEY_LATITUDE));
                            else
                                values.putNull(COLUMN_LATITUDE);
                            values.put(COLUMN_LATITUDE_UPD, entry.getString(JSON_KEY_LATITUDE_UPD));
                            if (!entry.isNull(JSON_KEY_LONGITUDE))
                                values.put(COLUMN_LONGITUDE, entry.getDouble(JSON_KEY_LONGITUDE));
                            else
                                values.putNull(COLUMN_LONGITUDE);
                            values.put(COLUMN_LONGITUDE_UPD, entry.getString(JSON_KEY_LONGITUDE_UPD));

                            values.put(Constants.DATA_COLUMN_STATUS_DATE, entry.getString(JSON_KEY_STATUS_DATE));
                            values.put(Constants.DATA_COLUMN_SYNCHRONIZED, Synchronized.DONE.getValue());

                            // Check if entry already exists
                            String selection = COLUMN_PSEUDO + '=' + DatabaseUtils.sqlEscapeString(pseudo);
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

                                    if ((entry.has(JSON_KEY_CODE_CONF_UPD)) && // Connected user only
                                            (cursor.getString(COLUMN_INDEX_CODE_CONF_UPD)
                                                    .compareTo(entry.getString(JSON_KEY_CODE_CONF_UPD)) > 0)) {
                                        values.remove(COLUMN_CODE_CONF);
                                        values.remove(COLUMN_CODE_CONF_UPD);
                                        removed = true;
                                    }
                                    // NB: See to do comments into 'ConnectFragment' class

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
                                    if (cursor.getString(COLUMN_INDEX_PHONE_UPD)
                                            .compareTo(entry.getString(JSON_KEY_PHONE_UPD)) > 0) {
                                        values.remove(COLUMN_PHONE);
                                        values.remove(COLUMN_PHONE_UPD);
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
                                    if (cursor.getString(COLUMN_INDEX_DEVICE_UPD)
                                            .compareTo(entry.getString(JSON_KEY_DEVICE_UPD)) > 0) {
                                        values.remove(COLUMN_DEVICE);
                                        values.remove(COLUMN_DEVICE_UPD);
                                        removed = true;
                                    }
                                    if (cursor.getString(COLUMN_INDEX_DEVICE_ID_UPD)
                                            .compareTo(entry.getString(JSON_KEY_DEVICE_ID_UPD)) > 0) {
                                        values.remove(COLUMN_DEVICE_ID);
                                        values.remove(COLUMN_DEVICE_ID_UPD);
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
                                    resolver.update(tableUri, values, selection, null);
                                    ++syncResult.updated;
                                }

                            } else if (entry.getInt(WebServices.JSON_KEY_STATUS) != STATUS_FIELD_DELETED) {

                                ////// Insert entry into DB
                                values.put(COLUMN_PSEUDO, pseudo);
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
            if (operation != WebServices.OPERATION_SELECT) {

                syncData.putString(DATA_KEY_FIELD_PSEUDO, COLUMN_PSEUDO);
                resetSyncInProgress(resolver, syncData);
            }
            return null;
        }
        return syncResult;
    }
}
