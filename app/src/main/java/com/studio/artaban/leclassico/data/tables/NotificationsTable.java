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
 * Created by pascal on 08/08/16.
 * Notifications database table class
 */
public class NotificationsTable implements IDataTable {

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
    public static final String TABLE_NAME = "Notifications";

    // Columns
    public static final String COLUMN_PSEUDO = "NOT_Pseudo";
    public static final String COLUMN_DATE = "NOT_Date";
    public static final String COLUMN_OBJECT_TYPE = "NOT_ObjType";
    public static final String COLUMN_OBJECT_ID = "NOT_ObjID";
    public static final String COLUMN_OBJECT_DATE = "NOT_ObjDate";
    public static final String COLUMN_OBJECT_FROM = "NOT_ObjFrom";
    public static final String COLUMN_LU_FLAG = "NOT_LuFlag";

    // Columns index
    private static final short COLUMN_INDEX_PSEUDO = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_DATE = 2;
    private static final short COLUMN_INDEX_OBJECT_TYPE = 3;
    private static final short COLUMN_INDEX_OBJECT_ID = 4;
    private static final short COLUMN_INDEX_OBJECT_DATE = 5;
    private static final short COLUMN_INDEX_OBJECT_FROM = 6;
    private static final short COLUMN_INDEX_LU_FLAG = 7;

    private static final short COLUMN_INDEX_SYNCHRONIZED = 8;

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
