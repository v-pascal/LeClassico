package com.studio.artaban.leclassico.data.codes;

import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 07/09/16.
 * Query ID & limit (see QueryLoader class)
 */
public class Queries {

    ////// ID //////////////////////////////////////////////////////////////////////////////////////

    private static final int MAX_TABLE_ID = 255;

    ////// Main
    public static final int MAIN_NEW_NOTIFY_FLAG = MAX_TABLE_ID;
    public static final int MAIN_SHORTCUT_NOTIFY_COUNT = MAX_TABLE_ID + 1;
    public static final int MAIN_SHORTCUT_LAST_FOLLOWED = MAX_TABLE_ID + 2;
    public static final int MAIN_NOTIFICATIONS = MAX_TABLE_ID + 3;
    public static final int MAIN_NOTIFICATION_MAX = MAX_TABLE_ID + 4;

    //////
    public static String getTable(int id) { // Return table name associated with the query ID
        Logs.add(Logs.Type.V, "id: " + id);

        switch (id) { // Switch on particular query ID

            ////// Particular table query //////////////////////////////////////////////////////////

            // Main
            case MAIN_NEW_NOTIFY_FLAG:
            case MAIN_SHORTCUT_NOTIFY_COUNT:
            case MAIN_NOTIFICATION_MAX:
                return NotificationsTable.TABLE_NAME;

            ////// Raw query ///////////////////////////////////////////////////////////////////////

            // Main
            case MAIN_NOTIFICATIONS:
            case MAIN_SHORTCUT_LAST_FOLLOWED:
                return null; // Raw query (complex queries)

            default: //////
                throw new IllegalArgumentException("Unexpected query loader ID");
        }
    }

    ////// Limit ///////////////////////////////////////////////////////////////////////////////////
    // Max entry count to display into a list view at start

    public static final short LIMIT_MAIN_NOTIFICATIONS = 20;

    ////// Older ///////////////////////////////////////////////////////////////////////////////////
    // Max entry count to display into a list after the last element (when older entries is requested)

    public static final short OLDER_MAIN_NOTIFICATIONS = 5;

}
