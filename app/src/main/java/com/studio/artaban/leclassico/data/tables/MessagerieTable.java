package com.studio.artaban.leclassico.data.tables;

import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.IDataTable;
import com.studio.artaban.leclassico.helpers.Logs;

import java.util.List;

/**
 * Created by pascal on 28/07/16.
 * Messagerie database table class
 */
public class MessagerieTable implements IDataTable {

    public static class Message extends DataField { /////////////////////////////// Messagerie entry

        public Message(short count, long id) { super(count, id); }
        public Message(Parcel parcel) {

            super(parcel);
            Logs.add(Logs.Type.V, "parcel: " + parcel);
        }
        public static final Parcelable.Creator<Message> CREATOR = new Creator<Message>() {

            @Override public Message createFromParcel(Parcel source) { return new Message(source); }
            @Override public Message[] newArray(int size) { return new Message[size]; }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

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
    public static final String TABLE_NAME = "Messagerie";

    // Columns
    public static final String COLUMN_PSEUDO = "MSG_Pseudo";
    public static final String COLUMN_FROM = "MSG_From";
    public static final String COLUMN_MESSAGE = "MSG_Message";
    public static final String COLUMN_DATE = "MSG_Date";
    public static final String COLUMN_TIME = "MSG_Time";
    public static final String COLUMN_LU_FLAG = "MSG_LuFlag";
    public static final String COLUMN_READ_STK = "MSG_ReadStk";
    public static final String COLUMN_WRITE_STK = "MSG_WriteStk";
    public static final String COLUMN_OBJET = "MSG_Objet";

    // Columns index
    private static final short COLUMN_INDEX_PSEUDO = 1; // DataField.COLUMN_INDEX_ID + 1
    private static final short COLUMN_INDEX_FROM = 2;
    private static final short COLUMN_INDEX_MESSAGE = 3;
    private static final short COLUMN_INDEX_DATE = 4;
    private static final short COLUMN_INDEX_TIME = 5;
    private static final short COLUMN_INDEX_LU_FLAG = 6;
    private static final short COLUMN_INDEX_READ_STK = 7;
    private static final short COLUMN_INDEX_WRITE_STK = 8;
    private static final short COLUMN_INDEX_OBJET = 9;

    private static final short COLUMN_INDEX_SYNCHRONIZED = 10;

    //
    private MessagerieTable() { }
    public static MessagerieTable newInstance() { return new MessagerieTable(); }

    @Override
    public void create(SQLiteDatabase db) {

        Logs.add(Logs.Type.V, "db: " + db);
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                DataField.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                COLUMN_PSEUDO + " TEXT NOT NULL," +
                COLUMN_FROM + " TEXT NOT NULL," +
                COLUMN_MESSAGE + " TEXT NOT NULL," +
                COLUMN_DATE + " TEXT NOT NULL," +
                COLUMN_TIME + " TEXT NOT NULL," +
                COLUMN_LU_FLAG + " INTEGER NOT NULL," +
                COLUMN_READ_STK + " INTEGER NOT NULL," +
                COLUMN_WRITE_STK + " INTEGER NOT NULL," +
                COLUMN_OBJET + " TEXT," +

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
