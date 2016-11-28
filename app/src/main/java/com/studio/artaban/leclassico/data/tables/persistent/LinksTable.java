package com.studio.artaban.leclassico.data.tables.persistent;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

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

        public Link(short count, long id) { super(count, id); }
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
    public static final String COLUMN_DATE = "LNK_Date";
    public static final String COLUMN_STATUS = "LNK_Status";
    public static final String COLUMN_IMAGE = "LNK_Image";
    public static final String COLUMN_TITLE = "LNK_Title";
    public static final String COLUMN_DESCRIPTION = "LNK_Description";
    public static final String COLUMN_INFO = "LNK_Info";

    // Columns index
    private static final short COLUMN_INDEX_URL = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_DATE = 2;
    private static final short COLUMN_INDEX_STATUS = 3;
    private static final short COLUMN_INDEX_IMAGE = 4;
    private static final short COLUMN_INDEX_TITLE = 5;
    private static final short COLUMN_INDEX_DESCRIPTION = 6;
    private static final short COLUMN_INDEX_INFO = 7;

    //
    private LinksTable() { }
    public static LinksTable newInstance() { return new LinksTable(); }

    @Override
    public void create(SQLiteDatabase db) {

        Logs.add(Logs.Type.V, "db: " + db);
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                DataField.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                COLUMN_URL + " TEXT NOT NULL," +
                COLUMN_DATE + " TEXT NOT NULL," +
                COLUMN_STATUS + " INTEGER NOT NULL," +
                COLUMN_IMAGE + " TEXT," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_INFO + " TEXT" +

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
    public SyncResult synchronize(ContentResolver resolver, String token, byte operation,
                                  @Nullable String pseudo, @Nullable Short limit,
                                  @Nullable ContentValues postData) {
        return null;
    }
}
