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
 * Commentaires database table class
 */
public class CommentairesTable extends DataTable {

    public static class Comment extends DataField { ///////////////////////////// Commentaires entry

        public Comment(short count, long id) { super(count, id); }
        public Comment(Parcel parcel) {

            super(parcel);
            Logs.add(Logs.Type.V, "parcel: " + parcel);
        }
        public static final Parcelable.Creator<Comment> CREATOR = new Creator<Comment>() {

            @Override public Comment createFromParcel(Parcel source) { return new Comment(source); }
            @Override public Comment[] newArray(int size) { return new Comment[size]; }
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
    public static final String TABLE_NAME = "Commentaires";

    // Columns
    public static final String COLUMN_OBJ_TYPE = "COM_ObjType";
    public static final String COLUMN_OBJ_ID = "COM_ObjID";
    public static final String COLUMN_PSEUDO = "COM_Pseudo";
    public static final String COLUMN_DATE = "COM_Date";
    public static final String COLUMN_TEXT = "COM_Text";

    // Columns index
    private static final short COLUMN_INDEX_OBJ_TYPE = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_OBJ_ID = 2;
    private static final short COLUMN_INDEX_PSEUDO = 3;
    private static final short COLUMN_INDEX_DATE = 4;
    private static final short COLUMN_INDEX_TEXT = 5;

    private static final short COLUMN_INDEX_SYNCHRONIZED = 6;

    // JSON keys
    private static final String JSON_KEY_PSEUDO = COLUMN_PSEUDO.substring(4);

    //
    private CommentairesTable() { }
    public static CommentairesTable newInstance() { return new CommentairesTable(); }

    @Override
    public void create(SQLiteDatabase db) {

        Logs.add(Logs.Type.V, "db: " + db);
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                DataField.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                COLUMN_OBJ_TYPE + " TEXT NOT NULL," +
                COLUMN_OBJ_ID + " INTEGER NOT NULL," +
                COLUMN_PSEUDO + " TEXT NOT NULL," +
                COLUMN_DATE + " TEXT NOT NULL," +
                COLUMN_TEXT + " TEXT NOT NULL," +

                Constants.DATA_COLUMN_SYNCHRONIZED + " INTEGER NOT NULL" +

                ");");

        // Add indexes
        db.execSQL("CREATE INDEX " + TABLE_NAME + JSON_KEY_PSEUDO + " ON " +
                TABLE_NAME + "(" + COLUMN_PSEUDO + ")");
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
