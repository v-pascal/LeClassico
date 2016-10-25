package com.studio.artaban.leclassico.data.codes;

import android.net.Uri;

import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 13/10/16.
 * Query & Observer URI list (used to manage specific content observer)
 */
public class Uris {

    public static Uri getUri(short id, String... arguments) {
        // Return URI formatted as expected (according ID parameter)

        Logs.add(Logs.Type.V, "id: " + id + ";arguments: " + arguments);
        switch (id) {

            case ID_RAW_QUERY:
                return Uri.parse(DataProvider.CONTENT_URI +
                        PATH_SQL); // SQL
                // WARNING: Notification not allowed on this URI (reserved)

            case ID_USER_NOTIFICATIONS: // User/#/Notifications
                return Uri.parse(DataProvider.CONTENT_URI +
                        PATH_USER + arguments[0] + '/' + NotificationsTable.TABLE_NAME);
            case ID_MAIN_SHORTCUT: // User/#/Notifications/Shortcut
                return Uri.parse(DataProvider.CONTENT_URI +
                        PATH_USER + arguments[0] + '/' + NotificationsTable.TABLE_NAME + PATH_SHORTCUT);

            default:
                throw new IllegalArgumentException("Unexpected URI id: " + id);
        }
    }

    ////// Path ////////////////////////////////////////////////////////////////////////////////////

    private static final String PATH_SQL = "SQL"; // SQL path for raw query (reserved)

    private static final String PATH_USER = "User/"; // User URI path following with member ID
    private static final String PATH_SHORTCUT = "/Shortcut"; // Shortcut URI path

    ////// URI /////////////////////////////////////////////////////////////////////////////////////

    public static final short ID_RAW_QUERY = 0;          // SQL
    public static final short ID_USER_NOTIFICATIONS = 1; // User/#/Notifications
    public static final short ID_MAIN_SHORTCUT = 2;      // User/#/Notifications/Shortcut
}
