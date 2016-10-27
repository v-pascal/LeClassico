package com.studio.artaban.leclassico.data.tables;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.helpers.Database;
import com.studio.artaban.leclassico.helpers.Logs;

import java.util.List;

/**
 * Created by pascal on 28/07/16.
 * Evenements database table class
 */
public class EvenementsTable extends DataTable {

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

    @Override
    public Database.SyncResult synchronize(final ContentResolver resolver, String token, String pseudo,
                                           @Nullable Short limit, @Nullable ContentValues postData) {

        // Synchronize data from remote to local DB (return inserted, deleted or
        // updated entry count & NO_DATA if error)
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";token: " + token + ";pseudo: " + pseudo +
                ";limit: " + limit + ";postData: " + postData);











        return null;
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
    public static final String COLUMN_NOM_UPD = "EVE_NomUPD";
    public static final String COLUMN_LIEU = "EVE_Lieu";
    public static final String COLUMN_LIEU_UPD = "EVE_LieuUPD";
    public static final String COLUMN_DATE = "EVE_Date";
    public static final String COLUMN_DATE_UPD = "EVE_DateUPD";
    public static final String COLUMN_FLYER = "EVE_Flyer";
    public static final String COLUMN_FLYER_UPD = "EVE_FlyerUPD";
    public static final String COLUMN_REMARK = "EVE_Remark";
    public static final String COLUMN_REMARK_UPD = "EVE_RemarkUPD";
    public static final String COLUMN_STATUS_DATE = "EVE_StatusDate";

    // Columns index
    private static final short COLUMN_INDEX_EVENT_ID = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_PSEUDO = 2;
    private static final short COLUMN_INDEX_NOM = 3;
    private static final short COLUMN_INDEX_NOM_UPD = 4;
    private static final short COLUMN_INDEX_LIEU = 5;
    private static final short COLUMN_INDEX_LIEU_UPD = 6;
    private static final short COLUMN_INDEX_DATE = 7;
    private static final short COLUMN_INDEX_DATE_UPD = 8;
    private static final short COLUMN_INDEX_FLYER = 9;
    private static final short COLUMN_INDEX_FLYER_UPD = 10;
    private static final short COLUMN_INDEX_REMARK = 11;
    private static final short COLUMN_INDEX_REMARK_UPD = 12;
    private static final short COLUMN_INDEX_STATUS_DATE = 13;

    private static final short COLUMN_INDEX_SYNCHRONIZED = 14;

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
                COLUMN_NOM_UPD + " TEXT NOT NULL," +
                COLUMN_LIEU + " TEXT NOT NULL," +
                COLUMN_LIEU_UPD + " TEXT NOT NULL," +
                COLUMN_DATE + " TEXT NOT NULL," +
                COLUMN_DATE_UPD + " TEXT NOT NULL," +
                COLUMN_FLYER + " TEXT," +
                COLUMN_FLYER_UPD + " TEXT NOT NULL," +
                COLUMN_REMARK + " TEXT," +
                COLUMN_REMARK_UPD + " TEXT NOT NULL," +
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
