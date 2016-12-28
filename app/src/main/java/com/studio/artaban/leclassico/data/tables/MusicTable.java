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
 * Music database table class
 */
public class MusicTable extends DataTable {

    public static class Sound extends DataField { ////////////////////////////////////// Music entry

        public Sound(short count, long id) { super(count, id); }
        public Sound(Parcel parcel) {

            super(parcel);
            Logs.add(Logs.Type.V, "parcel: " + parcel);
        }
        public static final Parcelable.Creator<Sound> CREATOR = new Creator<Sound>() {

            @Override public Sound createFromParcel(Parcel source) { return new Sound(source); }
            @Override public Sound[] newArray(int size) { return new Sound[size]; }
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
    public static final String TABLE_NAME = "Music";

    // Columns
    public static final String COLUMN_FICHIER = "MSG_Fichier";
    public static final String COLUMN_PSEUDO = "MSC_Pseudo";
    public static final String COLUMN_ARTISTE = "MSC_Artiste";
    public static final String COLUMN_ARTISTE_UPD = "MSC_ArtisteUPD";
    public static final String COLUMN_ALBUM = "MSC_Album";
    public static final String COLUMN_ALBUM_UPD = "MSC_AlbumUPD";
    public static final String COLUMN_MORCEAU = "MSC_Morceau";
    public static final String COLUMN_MORCEAU_UPD = "MSC_MorceauUPD";
    public static final String COLUMN_SOURCE = "MSC_Source";
    private static final String COLUMN_STATUS_DATE = "MSC_StatusDate";

    // Columns index
    private static final short COLUMN_INDEX_FICHIER = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_PSEUDO = 2;
    private static final short COLUMN_INDEX_ARTISTE = 3;
    private static final short COLUMN_INDEX_ARTISTE_UPD = 4;
    private static final short COLUMN_INDEX_ALBUM = 5;
    private static final short COLUMN_INDEX_ALBUM_UPD = 6;
    private static final short COLUMN_INDEX_MORCEAU = 7;
    private static final short COLUMN_INDEX_MORCEAU_UPD = 8;
    private static final short COLUMN_INDEX_SOURCE = 9;
    private static final short COLUMN_INDEX_STATUS_DATE = 10;
    private static final short COLUMN_INDEX_SYNCHRONIZED = 11;

    //
    private MusicTable() { }
    public static MusicTable newInstance() { return new MusicTable(); }

    @Override
    public void create(SQLiteDatabase db) {

        Logs.add(Logs.Type.V, "db: " + db);
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                DataField.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                COLUMN_FICHIER + " TEXT NOT NULL," +
                COLUMN_PSEUDO + " TEXT NOT NULL," +
                COLUMN_ARTISTE + " TEXT NOT NULL," +
                COLUMN_ARTISTE_UPD + " TEXT NOT NULL," +
                COLUMN_ALBUM + " TEXT NOT NULL," +
                COLUMN_ALBUM_UPD + " TEXT NOT NULL," +
                COLUMN_MORCEAU + " TEXT NOT NULL," +
                COLUMN_MORCEAU_UPD + " TEXT NOT NULL," +
                COLUMN_SOURCE + " TEXT NOT NULL," +

                Constants.DATA_COLUMN_STATUS_DATE + " TEXT NOT NULL," +
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
    public @Nullable SyncResult synchronize(ContentResolver resolver, byte operation,
                                            Bundle syncData, @Nullable ContentValues postData) {

        // Synchronize data from remote to local DB (return inserted, deleted or
        // updated entry count & NO_DATA if error)
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";operation: " + operation +
                ";syncData: " + syncData + ";postData: " + postData);







        final SyncResult syncResult = new SyncResult();
        return syncResult;
    }
}
