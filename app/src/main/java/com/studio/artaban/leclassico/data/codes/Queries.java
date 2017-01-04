package com.studio.artaban.leclassico.data.codes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 07/09/16.
 * Query ID & limit (see QueryLoader class)
 */
public class Queries {

    public static Cursor get(SQLiteDatabase db, Uri uri, String[] projection, String selection,
                             String[] selectionArgs, String sortOrder) { // Get query result according URI

        Logs.add(Logs.Type.V, "uri: " + uri + ";selection: " + selection);
        switch (Uris.getId(uri)) {
            case Uris.ID_USER_MEMBERS: { // MAIN_MEMBERS_LIST



                /*
                membersData.putString(QueryLoader.DATA_KEY_SELECTION,
                        "SELECT " + CamaradesTable.TABLE_NAME + '.' + IDataTable.DataField.COLUMN_ID + ',' + // COLUMN_INDEX_ID
                                CamaradesTable.COLUMN_PSEUDO + ',' + // COLUMN_INDEX_PSEUDO
                                CamaradesTable.COLUMN_SEXE + ',' + // COLUMN_INDEX_SEX
                                CamaradesTable.COLUMN_PROFILE + ',' + // COLUMN_INDEX_PROFILE
                                CamaradesTable.COLUMN_PHONE + ',' + // COLUMN_INDEX_PHONE
                                CamaradesTable.COLUMN_EMAIL + ',' + // COLUMN_INDEX_EMAIL
                                CamaradesTable.COLUMN_VILLE + ',' + // COLUMN_INDEX_TOWN
                                CamaradesTable.COLUMN_NOM + ',' + // COLUMN_INDEX_NAME
                                CamaradesTable.COLUMN_ADRESSE + ',' + // COLUMN_INDEX_ADDRESS
                                CamaradesTable.COLUMN_ADMIN + ',' + // COLUMN_INDEX_ADMIN
                                AbonnementsTable.TABLE_NAME + '.' + IDataTable.DataField.COLUMN_ID + // COLUMN_INDEX_FOLLOWED
                                " FROM " + CamaradesTable.TABLE_NAME +
                                " LEFT JOIN " + AbonnementsTable.TABLE_NAME + " ON " +
                                AbonnementsTable.COLUMN_CAMARADE + '=' + CamaradesTable.COLUMN_PSEUDO + " AND " +
                                AbonnementsTable.COLUMN_PSEUDO + "='" + pseudo + '\'' +
                                " WHERE " + CamaradesTable.TABLE_NAME + '.' + IDataTable.DataField.COLUMN_ID + "<>" + pseudoId +
                                " ORDER BY " + CamaradesTable.COLUMN_PSEUDO + " ASC");
                                */




            }
            case Uris.ID_RAW_QUERY:
            default: { // Raw query
                return db.rawQuery(selection, selectionArgs);
            }
        }
    }

    ////// ID //////////////////////////////////////////////////////////////////////////////////////

    ////// Main
    public static final int MAIN_DATA_USER = Tables.ID_CAMARADES; // Table query
    public static final int MAIN_SHORTCUT_MAIL_COUNT = Tables.ID_MAX;
    public static final int MAIN_SHORTCUT_NOTIFY_COUNT = Tables.ID_MAX + 1;
    public static final int MAIN_SHORTCUT_LAST_FOLLOWED = Tables.ID_MAX + 2;
    public static final int MAIN_PUBLICATIONS_LIST = Tables.ID_MAX + 3;
    public static final int MAIN_BEST_PHOTOS = Tables.ID_MAX + 4;
    public static final int MAIN_MEMBERS_LIST = Tables.ID_MAX + 5;

    ////// Notifications
    public static final int NOTIFICATIONS_NEW_INFO = Tables.ID_MAX + 6;
    public static final int NOTIFICATIONS_MAIN_LIST = Tables.ID_MAX + 7;


    ////// Limit ///////////////////////////////////////////////////////////////////////////////////
    // Max entry count to display into a list view at start
    // NB: All limits must be < *Table.DEFAULT_LIMIT

    public static final short NOTIFICATIONS_LIST_LIMIT = 20;
    public static final short PUBLICATIONS_LIST_LIMIT = 5;
    public static final short COMMENTS_LIST_LIMIT = 4;


    ////// Older ///////////////////////////////////////////////////////////////////////////////////
    // Max entry count to display into a list after the last element (when old entries are requested)

    public static final short NOTIFICATIONS_OLD_LIMIT = 5;
    public static final short PUBLICATIONS_OLD_LIMIT = 3;
    public static final short COMMENTS_OLD_LIMIT = 4;
}
