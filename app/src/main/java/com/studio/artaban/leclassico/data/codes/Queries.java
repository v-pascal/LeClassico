package com.studio.artaban.leclassico.data.codes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.IDataTable;
import com.studio.artaban.leclassico.data.tables.AbonnementsTable;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.data.tables.EvenementsTable;
import com.studio.artaban.leclassico.data.tables.PresentsTable;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 07/09/16.
 * Queries, IDs & limits (see QueryLoader class)
 */
public class Queries {

    public static Cursor get(SQLiteDatabase db, Uri uri, String[] projection, String selection,
                             String[] selectionArgs, String sortOrder) { // Return query result according URI

        Logs.add(Logs.Type.V, "uri: " + uri + ";selection: " + selection);
        switch (Uris.getId(uri)) {
            case Uris.ID_USER_MEMBERS: { // MAIN_MEMBERS_LIST

                Logs.add(Logs.Type.I, "Members list query");
                int pseudoId = Integer.valueOf(uri.getPathSegments().get(1)); // User/#/Camarades/Shortcut
                Cursor cursor = db.query(CamaradesTable.TABLE_NAME, new String[]{CamaradesTable.COLUMN_PSEUDO},
                        IDataTable.DataField.COLUMN_ID + '=' + pseudoId, null, null, null, null);

                // Get user pseudo
                String pseudo;
                if (cursor.moveToFirst())
                    pseudo = cursor.getString(0);
                else
                    throw new IllegalArgumentException("Wrong user ID");
                cursor.close();

                // Get filter criteria (if any)
                String filterSelection = "";
                if ((uri.getPathSegments().size() > 3) && (!uri.getPathSegments().get(3).isEmpty()))
                    filterSelection = " AND upper(" + CamaradesTable.COLUMN_PSEUDO + ") LIKE '" +
                            Uri.decode(uri.getPathSegments().get(3)).toUpperCase() + "%'";

                return db.rawQuery("SELECT " + CamaradesTable.TABLE_NAME + '.' + IDataTable.DataField.COLUMN_ID + ',' +
                        CamaradesTable.COLUMN_PSEUDO + ',' +
                        CamaradesTable.COLUMN_SEXE + ',' +
                        CamaradesTable.COLUMN_PROFILE + ',' +
                        CamaradesTable.COLUMN_PHONE + ',' +
                        CamaradesTable.COLUMN_EMAIL + ',' +
                        CamaradesTable.COLUMN_VILLE + ',' +
                        CamaradesTable.COLUMN_NOM + ',' +
                        CamaradesTable.COLUMN_ADRESSE + ',' +
                        CamaradesTable.COLUMN_ADMIN + ',' +
                        AbonnementsTable.TABLE_NAME + '.' + IDataTable.DataField.COLUMN_ID +
                        " FROM " + CamaradesTable.TABLE_NAME +
                        " LEFT JOIN " + AbonnementsTable.TABLE_NAME + " ON " +
                        AbonnementsTable.COLUMN_CAMARADE + '=' + CamaradesTable.COLUMN_PSEUDO + " AND " +
                        AbonnementsTable.COLUMN_PSEUDO + "='" + pseudo + '\'' +
                        " WHERE " + CamaradesTable.TABLE_NAME + '.' + IDataTable.DataField.COLUMN_ID + "<>" + pseudoId +
                        filterSelection +
                        " ORDER BY " + CamaradesTable.COLUMN_PSEUDO + " ASC", null);
            }
            case Uris.ID_MAIN_EVENTS: { // MAIN_EVENTS_LIST

                Logs.add(Logs.Type.I, "Events list query");

                // TODO: Get date filter criteria to return events that follow N month B4 and after it

                String fields = EvenementsTable.TABLE_NAME + '.' + IDataTable.DataField.COLUMN_ID + ',' +
                        EvenementsTable.COLUMN_NOM + ',' +
                        EvenementsTable.COLUMN_DATE + ',' +
                        EvenementsTable.COLUMN_DATE_END + ',' +
                        EvenementsTable.COLUMN_LIEU + ',' +
                        EvenementsTable.COLUMN_REMARK;

                return db.rawQuery("SELECT " + fields + ',' +
                        "count(" + PresentsTable.COLUMN_EVENT_ID + ')' +
                        " FROM " + EvenementsTable.TABLE_NAME +
                        " LEFT JOIN " + PresentsTable.TABLE_NAME + " ON " +
                        PresentsTable.COLUMN_EVENT_ID + '=' + EvenementsTable.COLUMN_EVENT_ID + " AND " +
                        PresentsTable.TABLE_NAME + '.' + Constants.DATA_COLUMN_SYNCHRONIZED + "<>" +
                        DataTable.Synchronized.TO_DELETE.getValue() + " AND " +
                        PresentsTable.TABLE_NAME + '.' + Constants.DATA_COLUMN_SYNCHRONIZED + "<>" +
                        (DataTable.Synchronized.TO_DELETE.getValue() | DataTable.Synchronized.IN_PROGRESS.getValue()) +
                        " WHERE " +
                        EvenementsTable.TABLE_NAME + '.' + Constants.DATA_COLUMN_SYNCHRONIZED + "<>" +
                        DataTable.Synchronized.TO_DELETE.getValue() + " AND " +
                        EvenementsTable.TABLE_NAME + '.' + Constants.DATA_COLUMN_SYNCHRONIZED + "<>" +
                        (DataTable.Synchronized.TO_DELETE.getValue() | DataTable.Synchronized.IN_PROGRESS.getValue()) +
                        " GROUP BY " + fields +
                        " ORDER BY " + EvenementsTable.COLUMN_DATE + " DESC", null);
            }
            case Uris.ID_RAW_QUERY:
            default: { // Raw query (for multiple table selection)
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
    public static final int MAIN_EVENTS_LIST = Tables.ID_MAX + 6;

    ////// Notifications
    public static final int NOTIFICATIONS_NEW_INFO = Tables.ID_MAX + 7;
    public static final int NOTIFICATIONS_MAIN_LIST = Tables.ID_MAX + 8;


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
