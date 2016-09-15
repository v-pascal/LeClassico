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
 * Actualites database table class
 */
public class ActualitesTable implements IDataTable {

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

    public boolean synchronize(ContentResolver resolver, String token, String pseudo) {
    // Synchronize data with remote DB







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
    public static final String TABLE_NAME = "Actualites";

    // Columns
    public static final String COLUMN_ACTU_ID = "ACT_ActuID";
    public static final String COLUMN_PSEUDO = "ACT_Pseudo";
    public static final String COLUMN_DATE = "ACT_Date";
    public static final String COLUMN_CAMARADE = "ACT_Camarade";
    public static final String COLUMN_TEXT = "ACT_Text";
    public static final String COLUMN_LINK = "ACT_Link";
    public static final String COLUMN_FICHIER = "ACT_Fichier";

    // Columns index
    private static final short COLUMN_INDEX_ACTU_ID = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_PSEUDO = 2;
    private static final short COLUMN_INDEX_CAMARADE = 3;
    private static final short COLUMN_INDEX_TEXT = 4;
    private static final short COLUMN_INDEX_LINK = 5;
    private static final short COLUMN_INDEX_FICHIER = 6;

    private static final short COLUMN_INDEX_SYNCHRONIZED = 7;

    // JSON keys
    private static final String JSON_KEY_ACTU_ID = COLUMN_ACTU_ID.substring(4);

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

                Constants.DATA_COLUMN_SYNCHRONIZED + " INTEGER NOT NULL" +

                ");");

        // Add indexes
        db.execSQL("CREATE INDEX " + TABLE_NAME + JSON_KEY_ACTU_ID + " ON " +
                TABLE_NAME + "(" + COLUMN_ACTU_ID + ")");
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
