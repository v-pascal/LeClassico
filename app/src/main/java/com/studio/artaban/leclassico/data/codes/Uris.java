package com.studio.artaban.leclassico.data.codes;

import android.net.Uri;

import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 13/10/16.
 * Observer URI list (used to manage specific content observer)
 */
public class Uris {

    public static Uri getUri(int id, String... arguments) {
        // Return URI formatted as expected (according ID parameter)

        Logs.add(Logs.Type.V, "id: " + id + ";arguments: " + arguments);
        switch (id) {

            case ID_USER_NOTIFICATIONS: // User/#/Notifications
                return Uri.parse(DataProvider.CONTENT_URI +
                        PATH_USER + arguments[0] + '/' + NotificationsTable.TABLE_NAME);

            default:
                throw new IllegalArgumentException("Unexpected URI id: " + id);
        }
    }

    ////// Path ////////////////////////////////////////////////////////////////////////////////////

    private static final String PATH_USER = "User/"; // User URI path following with member ID

    ////// URI /////////////////////////////////////////////////////////////////////////////////////

    public static final int ID_USER_NOTIFICATIONS = 0; // User/#/Notifications
}
