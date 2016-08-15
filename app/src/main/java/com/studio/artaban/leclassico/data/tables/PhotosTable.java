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
 * Photos database table class
 */
public class PhotosTable implements IDataTable {

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

    public boolean synchronize(ContentResolver resolver, String token) {
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
    public static final String TABLE_NAME = "Photos";

    // Columns
    public static final String COLUMN_ALBUM = "PHT_Album";
    public static final String COLUMN_PSEUDO = "PHT_Pseudo";
    public static final String COLUMN_FICHIER = "PHT_Fichier";
    public static final String COLUMN_FICHIER_ID = "PHT_FichierID";
    public static final String COLUMN_STATUS_DATE = "PHT_StatusDate";

    // Columns index
    private static final short COLUMN_INDEX_ALBUM = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_PSEUDO = 2;
    private static final short COLUMN_INDEX_FICHIER = 3;
    private static final short COLUMN_INDEX_FICHIER_ID = 4;
    private static final short COLUMN_INDEX_STATUS_DATE = 5;

    private static final short COLUMN_INDEX_SYNCHRONIZED = 6;

    //
    private PhotosTable() { }
    public static PhotosTable newInstance() { return new PhotosTable(); }

    @Override
    public void create(SQLiteDatabase db) {

        Logs.add(Logs.Type.V, "db: " + db);
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                DataField.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                COLUMN_ALBUM + " TEXT NOT NULL," +
                COLUMN_PSEUDO + " TEXT NOT NULL," +
                COLUMN_FICHIER + " TEXT NOT NULL," +
                COLUMN_FICHIER_ID + " INTEGER NOT NULL," +
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
