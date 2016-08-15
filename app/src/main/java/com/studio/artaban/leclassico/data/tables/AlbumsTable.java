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
 * Albums database table class
 */
public class AlbumsTable implements IDataTable {

    public static class Photo extends DataField { ///////////////////////////////////// Albums entry

        public Photo(short count, long id) { super(count, id); }
        public Photo(Parcel parcel) {

            super(parcel);
            Logs.add(Logs.Type.V, "parcel: " + parcel);
        }
        public static final Parcelable.Creator<Photo> CREATOR = new Creator<Photo>() {

            @Override public Photo createFromParcel(Parcel source) { return new Photo(source); }
            @Override public Photo[] newArray(int size) { return new Photo[size]; }
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
    public static final String TABLE_NAME = "Albums";

    // Columns
    public static final String COLUMN_NOM = "ALB_Nom";
    public static final String COLUMN_PSEUDO = "ALB_Pseudo";
    public static final String COLUMN_SHARED = "ALB_Shared";
    public static final String COLUMN_EVENT_ID = "ALB_EventID";
    public static final String COLUMN_REMARK = "ALB_Remark";
    public static final String COLUMN_DATE = "ALB_Date";
    public static final String COLUMN_STATUS_DATE = "ALB_StatusDate";

    // Columns index
    private static final short COLUMN_INDEX_NOM = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_PSEUDO = 2;
    private static final short COLUMN_INDEX_SHARED = 3;
    private static final short COLUMN_INDEX_EVENT_ID = 4;
    private static final short COLUMN_INDEX_REMARK = 5;
    private static final short COLUMN_INDEX_DATE = 6;
    private static final short COLUMN_INDEX_STATUS_DATE = 7;

    private static final short COLUMN_INDEX_SYNCHRONIZED = 8;

    //
    private AlbumsTable() { }
    public static AlbumsTable newInstance() { return new AlbumsTable(); }

    @Override
    public void create(SQLiteDatabase db) {

        Logs.add(Logs.Type.V, "db: " + db);
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                DataField.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                COLUMN_NOM + " TEXT NOT NULL," +
                COLUMN_PSEUDO + " TEXT NOT NULL," +
                COLUMN_SHARED + " INTEGER NOT NULL," +
                COLUMN_EVENT_ID + " INTEGER NOT NULL," +
                COLUMN_REMARK + " TEXT," +
                COLUMN_DATE + " TEXT," +
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
