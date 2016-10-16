package com.studio.artaban.leclassico.data.codes;

import android.content.UriMatcher;
import android.net.Uri;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 13/10/16.
 * Observer URI list (used to manage specific content observer)
 */
public class Uris {

    private static final String SINGLE_ROW = "#/";

    //////
    public static Uri getUri(int id, String... arguments) {
        // Return URI formatted as expected (according ID parameter)

        Logs.add(Logs.Type.V, "id: " + id + ";arguments: " + arguments);
        switch (id) {

            case URI_ID_USER_NOTIFICATIONS: // User/#/Notifications
                return Uri.parse(DataProvider.CONTENT_URI +
                        PATH_USER + arguments[0] + '/' + NotificationsTable.TABLE_NAME);

            default:
                throw new IllegalArgumentException("Unexpected URI id: " + id);
        }
    }
    public static int getUriTableId(Uri uri) { // Return table ID associated with the URI
        return URI_MATCHER.match(uri);
    }

    ////// Path ////////////////////////////////////////////////////////////////////////////////////

    private static final String PATH_USER = "User/"; // User URI path following with member ID

    ////// URI /////////////////////////////////////////////////////////////////////////////////////

    private static final int URI_ID_USER_NOTIFICATIONS = 0; // User/#/Notifications

    //////
    public static class USER_NOTIFICATIONS {

        public static final int URI_ID = URI_ID_USER_NOTIFICATIONS;
        private static final int TABLE_ID = Tables.ID_NOTIFICATIONS;
        private static final String MATCHER = PATH_USER + SINGLE_ROW + NotificationsTable.TABLE_NAME;
    }

    //
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, USER_NOTIFICATIONS.MATCHER, USER_NOTIFICATIONS.TABLE_ID);
    }
}
