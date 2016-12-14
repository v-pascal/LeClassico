package com.studio.artaban.leclassico.data.codes;

import android.net.Uri;

import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.tables.ActualitesTable;
import com.studio.artaban.leclassico.data.tables.MessagerieTable;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 13/10/16.
 * Query, observer & activity link URI list (used to manage specific content observer)
 */
public class Uris {

    private static final String MIMETYPE_IMAGE_JPEG = "image/jpeg";
    private static final String MIMETYPE_IMAGE_PNG = "image/png";
    private static final String MIMETYPE_IMAGE_GIF = "image/gif";

    public static String getMimeType(Uri uri) { // Return MIME Type according URI
        Logs.add(Logs.Type.V, "uri: " + uri);

        String uriFile = uri.getLastPathSegment();
        if ((uriFile.toUpperCase().substring(uriFile.length() - 4).compareTo(".JPG") == 0) ||
                (uriFile.toUpperCase().substring(uriFile.length() - 5).compareTo(".JPEG") == 0))
            return MIMETYPE_IMAGE_JPEG;
        if (uriFile.toUpperCase().substring(uriFile.length() - 4).compareTo(".PNG") == 0)
            return MIMETYPE_IMAGE_PNG;
        if (uriFile.toUpperCase().substring(uriFile.length() - 4).compareTo(".GIF") == 0)
            return MIMETYPE_IMAGE_GIF;

        // Default image MIME Type (image only)
        return "image/*";
    }

    //////
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
            case ID_USER_MAILBOX: // User/#/Messagerie
                return Uri.parse(DataProvider.CONTENT_URI +
                        PATH_USER + arguments[0] + '/' + MessagerieTable.TABLE_NAME);
            case ID_USER_LOCATIONS: // User/#/Locations
                return Uri.parse(DataProvider.CONTENT_URI +
                        PATH_USER + arguments[0] + PATH_LOCATIONS);
            case ID_USER_PUBLICATIONS: // User/#/Actualites
                return Uri.parse(DataProvider.CONTENT_URI +
                        PATH_USER + arguments[0] + '/' + ActualitesTable.TABLE_NAME);
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
    private static final String PATH_LOCATIONS = "/Locations"; // Locations URI path

    ////// URI /////////////////////////////////////////////////////////////////////////////////////

    public static final short ID_RAW_QUERY = 0;          // SQL
    public static final short ID_USER_NOTIFICATIONS = 1; // User/#/Notifications
    public static final short ID_USER_MAILBOX = 2;       // User/#/Messagerie
    public static final short ID_USER_LOCATIONS = 3;       // User/#/Locations
    public static final short ID_USER_PUBLICATIONS = 4;       // User/#/Actualites
    public static final short ID_MAIN_SHORTCUT = 5;      // User/#/Notifications/Shortcut
}
