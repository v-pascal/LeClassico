package com.studio.artaban.leclassico.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.IDataTable;
import com.studio.artaban.leclassico.data.tables.AbonnementsTable;
import com.studio.artaban.leclassico.data.tables.ActualitesTable;
import com.studio.artaban.leclassico.data.tables.AlbumsTable;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.data.tables.CommentairesTable;
import com.studio.artaban.leclassico.data.tables.EvenementsTable;
import com.studio.artaban.leclassico.data.tables.MessagerieTable;
import com.studio.artaban.leclassico.data.tables.MusicTable;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.data.tables.PhotosTable;
import com.studio.artaban.leclassico.data.tables.PresentsTable;
import com.studio.artaban.leclassico.data.tables.VotesTable;
import com.studio.artaban.leclassico.data.tables.persistent.LinksTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pascal on 12/05/16.
 * Database helper
 */
public class Database extends SQLiteOpenHelper {

    private static final String NAME = Constants.APP_NAME + ".db"; // Database name
    public static final int VERSION = 2;
    // Database versions:
    // #1: Following application version 1.0
    // #2: Following application version 1.1

    public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss.000"; // SQLite date & time format

    protected SQLiteDatabase mDatabase;

    protected static Map<String, DataTable> mTableMap = new HashMap<>();
    static {
        mTableMap.put(CamaradesTable.TABLE_NAME, CamaradesTable.newInstance());
        mTableMap.put(AbonnementsTable.TABLE_NAME, AbonnementsTable.newInstance());
        mTableMap.put(ActualitesTable.TABLE_NAME, ActualitesTable.newInstance());
        mTableMap.put(AlbumsTable.TABLE_NAME, AlbumsTable.newInstance());
        mTableMap.put(CommentairesTable.TABLE_NAME, CommentairesTable.newInstance());
        mTableMap.put(EvenementsTable.TABLE_NAME, EvenementsTable.newInstance());
        mTableMap.put(MessagerieTable.TABLE_NAME, MessagerieTable.newInstance());
        mTableMap.put(MusicTable.TABLE_NAME, MusicTable.newInstance());
        mTableMap.put(PhotosTable.TABLE_NAME, PhotosTable.newInstance());
        mTableMap.put(PresentsTable.TABLE_NAME, PresentsTable.newInstance());
        mTableMap.put(VotesTable.TABLE_NAME, VotesTable.newInstance());
        mTableMap.put(NotificationsTable.TABLE_NAME, NotificationsTable.newInstance());

        // Persistent tables
        mTableMap.put(LinksTable.TABLE_NAME, LinksTable.newInstance());
    }

    //////
    protected boolean isReady() {

        Logs.add(Logs.Type.V, null);
        if ((mDatabase == null) || (!mDatabase.isOpen())) {
            Logs.add(Logs.Type.W, "Database not opened");
            return false;
        }
        return true;
    }
    private boolean isWritable() {

        Logs.add(Logs.Type.V, null);
        if (!isReady())
            return false;

        if (mDatabase.isReadOnly()) {
            Logs.add(Logs.Type.W, "Read only database opened");
            return false;
        }
        return true;
    }
    private boolean isTableExist(String name) {

        Logs.add(Logs.Type.V, "name: " + name);
        if (mTableMap.containsKey(name))
            return true;

        Logs.add(Logs.Type.W, "Data table '" + name + "' not defined");
        return false;
    }

    //
    public static DataTable getTable(String name) { return mTableMap.get(name); }
    public int insert(String table, Object[] data) {

        Logs.add(Logs.Type.V, "table: " + table + ", data: " + ((data != null)? data.length:"null"));
        return (isWritable() && isTableExist(table))?
                mTableMap.get(table).insert(mDatabase, data):Constants.NO_DATA;
    }
    public boolean update(String table, Object data) {

        Logs.add(Logs.Type.V, "table: " + table + ", data: " + data);
        return (isWritable() && isTableExist(table) && mTableMap.get(table).update(mDatabase, data));
    }
    public int delete(String table, long[] keys) {

        Logs.add(Logs.Type.V, "table: " + table + ", keys: " + ((keys != null)? keys.length:"null"));
        return (isWritable() && isTableExist(table))?
                mTableMap.get(table).delete(mDatabase, keys):Constants.NO_DATA;
    }

    public int getEntryCount(String table) {

        Logs.add(Logs.Type.V, "table: " + table);
        return (isReady() && isTableExist(table))?
                mTableMap.get(table).getEntryCount(mDatabase):Constants.NO_DATA;
    }
    public <T> List<T> getAllEntries(String table) {

        Logs.add(Logs.Type.V, "table: " + table);
        if (!isReady() || !isTableExist(table))
            return null;

        return mTableMap.get(table).getAllEntries(mDatabase);
    }

    //////
    public Database(Context context) { super(context, NAME, null, VERSION); }
    public void open(boolean write) {

        Logs.add(Logs.Type.V, "write: " + write);
        mDatabase = (write)? getWritableDatabase():getReadableDatabase();
    }

    //////
    @Override public void onCreate(SQLiteDatabase db) {

        Logs.add(Logs.Type.V, "db: " + db);
        for (IDataTable table: mTableMap.values())
            table.create(db);
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Logs.add(Logs.Type.V, "db: " + db);
        Logs.add(Logs.Type.W, "Upgrading DB from " + oldVersion + " to " + newVersion);
        for (IDataTable table: mTableMap.values())
            table.upgrade(db, oldVersion, newVersion);
    }
}
