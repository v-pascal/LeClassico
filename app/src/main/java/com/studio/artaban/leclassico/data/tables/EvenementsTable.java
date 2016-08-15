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
 * Evenements database table class
 */
public class EvenementsTable implements IDataTable {

    public static class Event extends DataField { ///////////////////////////////// Evenements entry

        public Event(short count, long id) { super(count, id); }
        public Event(Parcel parcel) {

            super(parcel);
            Logs.add(Logs.Type.V, "parcel: " + parcel);
        }
        public static final Parcelable.Creator<Event> CREATOR = new Creator<Event>() {

            @Override public Event createFromParcel(Parcel source) { return new Event(source); }
            @Override public Event[] newArray(int size) { return new Event[size]; }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean synchronize(ContentResolver contentResolver, long timeLag) {
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
    public static final String TABLE_NAME = "Evenements";

    // Columns
    public static final String COLUMN_EVENT_ID = "EVE_EventID";
    public static final String COLUMN_PSEUDO = "EVE_Pseudo";
    public static final String COLUMN_NOM = "EVE_Nom";
    public static final String COLUMN_LIEU = "EVE_Lieu";
    public static final String COLUMN_DATE = "EVE_Date";
    public static final String COLUMN_FLYER = "EVE_Flyer";
    public static final String COLUMN_REMARK = "EVE_Remark";

    // Columns index
    private static final short COLUMN_INDEX_EVENT_ID = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_PSEUDO = 2;
    private static final short COLUMN_INDEX_NOM = 3;
    private static final short COLUMN_INDEX_LIEU = 4;
    private static final short COLUMN_INDEX_DATE = 5;
    private static final short COLUMN_INDEX_FLYER = 6;
    private static final short COLUMN_INDEX_REMARK = 7;

    private static final short COLUMN_INDEX_SYNCHRONIZED = 8;

    //
    private EvenementsTable() { }
    public static EvenementsTable newInstance() { return new EvenementsTable(); }

    @Override
    public void create(SQLiteDatabase db) {

        Logs.add(Logs.Type.V, "db: " + db);
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                DataField.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                COLUMN_EVENT_ID + " INTEGER NOT NULL," +
                COLUMN_PSEUDO + " TEXT NOT NULL," +
                COLUMN_NOM + " TEXT NOT NULL," +
                COLUMN_LIEU + " TEXT NOT NULL," +
                COLUMN_DATE + " TEXT NOT NULL," +
                COLUMN_FLYER + " TEXT," +
                COLUMN_REMARK + " TEXT," +

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
