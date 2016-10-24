package com.studio.artaban.leclassico.data.codes;

import android.content.UriMatcher;
import android.net.Uri;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.tables.MessagerieTable;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 13/10/16.
 * Query & Observer URI list (used to manage specific content observer)
 */
public class Uris {

    public static Uri getUri(int id, String... arguments) {
        // Return URI formatted as expected (according ID parameter)

        Logs.add(Logs.Type.V, "id: " + id + ";arguments: " + arguments);
        switch (id) {

            case ID_USER_NOTIFICATIONS: // User/#/Notifications
                return Uri.parse(DataProvider.CONTENT_URI +
                        PATH_USER + arguments[0] + '/' + NotificationsTable.TABLE_NAME);
            case ID_MAIN_SHORTCUT: // User/#/Shortcut
                return Uri.parse(DataProvider.CONTENT_URI +
                        PATH_USER + arguments[0] + PATH_SHORTCUT);

            default:
                throw new IllegalArgumentException("Unexpected URI id: " + id);
        }
    }
    public static String getUriTable(Uri uri) {
        // Return table name on which the URI is associated (or NULL if raw query)

        Logs.add(Logs.Type.V, "uri: " + uri);
        switch (URI_MATCHER_SINGLE.match(uri)) {

            ////// Main
            case ID_USER_NOTIFICATIONS:
            case ID_MAIN_SHORTCUT:

                return Queries.getTable(Integer.valueOf(uri.getLastPathSegment()));
        }
        switch (URI_MATCHER.match(uri)) {

            ////// Main
            case ID_MAIN_SHORTCUT:
                return MessagerieTable.TABLE_NAME; // See 'HomeFragment.mMailLoader' member

            default:
                throw new RuntimeException("Unexpected URI: " + uri);
        }
    }

    ////// Path ////////////////////////////////////////////////////////////////////////////////////

    private static final String PATH_USER = "User/"; // User URI path following with member ID
    private static final String PATH_SHORTCUT = "/Shortcut"; // Shortcut URI path

    ////// URI /////////////////////////////////////////////////////////////////////////////////////
    // All URI can be append with a query ID (to manage several query ID with an unique URI)

    public static final int ID_USER_NOTIFICATIONS = 0; // User/#/Notifications
    public static final int ID_MAIN_SHORTCUT = 1;      // User/#/Shortcut

    //
    private static final UriMatcher URI_MATCHER_SINGLE = new UriMatcher(UriMatcher.NO_MATCH);
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {

        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI,
                PATH_USER + "#/" + NotificationsTable.TABLE_NAME + DataProvider.SINGLE_ROW,
                ID_USER_NOTIFICATIONS); // User/#/Notifications/#
        URI_MATCHER_SINGLE.addURI(Constants.DATA_CONTENT_URI,
                PATH_USER + '#' + PATH_SHORTCUT + DataProvider.SINGLE_ROW,
                ID_MAIN_SHORTCUT); // User/#/Shortcut/#

        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, PATH_USER + "#/" + NotificationsTable.TABLE_NAME,
                ID_USER_NOTIFICATIONS); // User/#/Notifications
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, PATH_USER + '#' + PATH_SHORTCUT,
                ID_MAIN_SHORTCUT); // User/#/Shortcut
    }
}
