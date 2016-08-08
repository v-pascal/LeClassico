package com.studio.artaban.leclassico.data.tables;

import android.content.ContentResolver;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.IDataTable;
import com.studio.artaban.leclassico.helpers.Logs;

import java.util.List;

/**
 * Created by pascal on 28/07/16.
 * Camarades database table class
 */
public class CamaradesTable implements IDataTable {

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

    public boolean synchronize(ContentResolver contentResolver) { // Synchronize data with remote DB







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
