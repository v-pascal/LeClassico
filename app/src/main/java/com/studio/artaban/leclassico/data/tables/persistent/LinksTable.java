package com.studio.artaban.leclassico.data.tables.persistent;

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
 * Created by pascal on 28/11/16.
 * Table containing publication link data
 */
public class LinksTable extends DataTable {

    public static byte STATUS_DONE = 0; // Download link data succeeded
    public static byte STATUS_FAILED = 1; // Download link data failed
    public static byte STATUS_IMAGE_FAILED = 2; // Download link image failed
    // Status

    public static class Link extends DataField { /////////////////////////////////////// Links entry

        private static final short FIELD_COUNT = 8;

        public String url;
        public byte status;
        public String image;
        public String title;
        public String description;
        public String info;

        public Link(long id) { super(FIELD_COUNT, id); }
        public Link(Parcel parcel) {

            super(parcel);
            Logs.add(Logs.Type.V, "parcel: " + parcel);
        }
        public static final Parcelable.Creator<Link> CREATOR = new Creator<Link>() {

            @Override public Link createFromParcel(Parcel source) { return new Link(source); }
            @Override public Link[] newArray(int size) { return new Link[size]; }
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
    public static final String TABLE_NAME = "Links";

    // Columns
    public static final String COLUMN_URL = "LNK_URL";
    public static final String COLUMN_STATUS = "LNK_Status";
    public static final String COLUMN_IMAGE = "LNK_Image";
    public static final String COLUMN_TITLE = "LNK_Title";
    public static final String COLUMN_DESCRIPTION = "LNK_Description";
    public static final String COLUMN_INFO = "LNK_Info";

    // Columns index
    private static final short COLUMN_INDEX_URL = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_STATUS = 2;
    private static final short COLUMN_INDEX_IMAGE = 3;
    private static final short COLUMN_INDEX_TITLE = 4;
    private static final short COLUMN_INDEX_DESCRIPTION = 5;
    private static final short COLUMN_INDEX_INFO = 6;
    private static final short COLUMN_INDEX_STATUS_DATE = 7;
    private static final short COLUMN_INDEX_SYNCHRONIZED = 8;

    //
    private LinksTable() { }
    public static LinksTable newInstance() { return new LinksTable(); }

    @Override
    public void create(SQLiteDatabase db) {

        Logs.add(Logs.Type.V, "db: " + db);
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                DataField.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                COLUMN_URL + " TEXT NOT NULL," +
                COLUMN_STATUS + " INTEGER NOT NULL," +
                COLUMN_IMAGE + " TEXT," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_INFO + " TEXT," +

                Constants.DATA_COLUMN_STATUS_DATE + " TEXT NOT NULL," +
                Constants.DATA_COLUMN_SYNCHRONIZED + " INTEGER NOT NULL" +
                // NB: Needed by the data provider

                ");");

        // Add indexes
        db.execSQL("CREATE INDEX " + TABLE_NAME + COLUMN_URL.substring(4) + " ON " +
                TABLE_NAME + '(' + COLUMN_URL + ')');
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
        return null;
    }
    @Override
    public ContentValues syncUpdated(ContentResolver resolver, String pseudo) {
        return null;
    }
    @Override
    public ContentValues syncDeleted(ContentResolver resolver, String pseudo) {
        return null;
    }
    @Override
    public SyncResult synchronize(ContentResolver resolver, byte operation, Bundle syncData,
                                  @Nullable ContentValues postData) {
        return null;
    }
}
