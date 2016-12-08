package com.studio.artaban.leclassico.data.tables;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.helpers.Logs;

import java.util.List;

/**
 * Created by pascal on 28/07/16.
 * Votes database table class
 */
public class VotesTable extends DataTable {

    public static class Grade extends DataField { ////////////////////////////////////// Votes entry

        public Grade(short count, long id) { super(count, id); }
        public Grade(Parcel parcel) {

            super(parcel);
            Logs.add(Logs.Type.V, "parcel: " + parcel);
        }
        public static final Parcelable.Creator<Grade> CREATOR = new Creator<Grade>() {

            @Override public Grade createFromParcel(Parcel source) { return new Grade(source); }
            @Override public Grade[] newArray(int size) { return new Grade[size]; }
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
    public static final String TABLE_NAME = "Votes";

    // Columns
    public static final String COLUMN_PSEUDO = "VOT_Pseudo";
    public static final String COLUMN_FICHIER = "VOT_Fichier";
    public static final String COLUMN_NOTE = "VOT_Note";
    public static final String COLUMN_TOTAL = "VOT_Total";
    public static final String COLUMN_DATE = "VOT_Date";
    public static final String COLUMN_TYPE = "VOT_Type";

    // Columns index
    private static final short COLUMN_INDEX_PSEUDO = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_FICHIER = 2;
    private static final short COLUMN_INDEX_NOTE = 3;
    private static final short COLUMN_INDEX_TOTAL = 4;
    private static final short COLUMN_INDEX_DATE = 5;
    private static final short COLUMN_INDEX_TYPE = 6;
    private static final short COLUMN_INDEX_SYNCHRONIZED = 7;

    //
    private VotesTable() { }
    public static VotesTable newInstance() { return new VotesTable(); }

    @Override
    public void create(SQLiteDatabase db) {

        Logs.add(Logs.Type.V, "db: " + db);
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                DataField.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                COLUMN_PSEUDO + " TEXT NOT NULL," +
                COLUMN_FICHIER + " TEXT NOT NULL," +
                COLUMN_NOTE + " INTEGER NOT NULL," +
                COLUMN_TOTAL + " INTEGER NOT NULL," +
                COLUMN_DATE + " TEXT NOT NULL," +
                COLUMN_TYPE + " INTEGER NOT NULL," +

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

    @Override
    public SyncResult synchronize(final ContentResolver resolver, final byte operation, Bundle syncData,
                                  @Nullable ContentValues postData) {

        // Synchronize data from remote to local DB (return inserted, deleted or
        // updated entry count & NO_DATA if error)
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";operation: " + operation +
                ";syncData: " + syncData + ";postData: " + postData);







        final SyncResult syncResult = new SyncResult();
        return syncResult;
    }
}
