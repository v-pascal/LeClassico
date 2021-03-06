package com.studio.artaban.leclassico.data.codes;

import android.content.UriMatcher;
import android.net.Uri;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.tables.ActualitesTable;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
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
    public static short getId(Uri uri) { // Return URI id
        Logs.add(Logs.Type.V, "uri: " + uri);

        int id = URI_MATCHER.match(uri);
        if (id != UriMatcher.NO_MATCH)
            return (short)id;

        return ID_RAW_QUERY; // Raw query
    }
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
            case ID_USER_LOCATION: // User/#/Location
                return Uri.parse(DataProvider.CONTENT_URI +
                        PATH_USER + arguments[0] + PATH_LOCATION);
            case ID_USER_PUBLICATIONS: // User/#/Actualites
                return Uri.parse(DataProvider.CONTENT_URI +
                        PATH_USER + arguments[0] + '/' + ActualitesTable.TABLE_NAME);
            case ID_USER_PROFILE: // User/#/Profile
                return Uri.parse(DataProvider.CONTENT_URI +
                        PATH_USER + arguments[0] + PATH_PROFILE);
            case ID_MAIN_SHORTCUT_NOTIFY: // User/#/Shortcut/Notifications
                return Uri.parse(DataProvider.CONTENT_URI +
                        PATH_USER + arguments[0] + PATH_SHORTCUT + NotificationsTable.TABLE_NAME);
            case ID_MAIN_SHORTCUT_MEMBER: // User/#/Shortcut/Camarades
                return Uri.parse(DataProvider.CONTENT_URI +
                        PATH_USER + arguments[0] + PATH_SHORTCUT + CamaradesTable.TABLE_NAME);
            case ID_USER_MEMBERS: // User/#/Camarades
                return Uri.parse(DataProvider.CONTENT_URI +
                        PATH_USER + arguments[0] + '/' + CamaradesTable.TABLE_NAME);
            case ID_MAIN_BEST_PHOTOS:
                return Uri.parse(DataProvider.CONTENT_URI +
                        PATH_BEST_PHOTOS); // BestPhoto
            case ID_MAIN_EVENTS:
                return Uri.parse(DataProvider.CONTENT_URI +
                        PATH_EVENTS); // Events
            case ID_PICK_MEMBER:
                return Uri.parse(DataProvider.CONTENT_URI + // Member & Member/*
                        PATH_MEMBER + ((arguments.length > 0)? '/' + Uri.encode(arguments[0]):""));

            default:
                throw new IllegalArgumentException("Unexpected URI id: " + id);
        }
    }

    ////// Path ////////////////////////////////////////////////////////////////////////////////////

    private static final String PATH_SQL = "SQL"; // SQL path for raw query (reserved)
    private static final String PATH_BEST_PHOTOS = "BestPhotos"; // Best photos URI path
    private static final String PATH_EVENTS = "Events"; // Events URI path
    private static final String PATH_MEMBER = "Member"; // Member URI path

    private static final String PATH_USER = "User/"; // User URI path following with member ID
    private static final String PATH_SHORTCUT = "/Shortcut/"; // Shortcut URI path
    private static final String PATH_LOCATION = "/Location"; // Location URI path
    private static final String PATH_PROFILE = "/Profile"; // Profile URI path

    ////// URI /////////////////////////////////////////////////////////////////////////////////////

    public static final short ID_RAW_QUERY = 0;            // SQL
    public static final short ID_USER_NOTIFICATIONS = 1;   // User/#/Notifications
    public static final short ID_USER_MAILBOX = 2;         // User/#/Messagerie
    public static final short ID_USER_LOCATION = 3;        // User/#/Location
    public static final short ID_USER_PUBLICATIONS = 4;    // User/#/Actualites
    public static final short ID_USER_PROFILE = 5;         // User/#/Profile
    public static final short ID_USER_MEMBERS = 6;         // User/#/Camarades
    public static final short ID_MAIN_SHORTCUT_NOTIFY = 7; // User/#/Shortcut/Notifications
    public static final short ID_MAIN_SHORTCUT_MEMBER = 8; // User/#/Shortcut/Camarades
    public static final short ID_MAIN_BEST_PHOTOS = 9;     // BestPhotos
    public static final short ID_MAIN_EVENTS = 10;         // Events
    public static final short ID_PICK_MEMBER = 11;         // Member

    ////// URI Matcher /////////////////////////////////////////////////////////////////////////////

    private static final UriMatcher URI_MATCHER;
    static { // Queries URI
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

        ////// ID_USER_MEMBERS
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, // User/#/Camarades
                PATH_USER + "#/" + CamaradesTable.TABLE_NAME, ID_USER_MEMBERS);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, // User/#/Camarades/*
                PATH_USER + "#/" + CamaradesTable.TABLE_NAME + DataProvider.FILTER_ROW, ID_USER_MEMBERS);

        ////// ID_MAIN_EVENTS
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, // Events
                PATH_EVENTS, ID_MAIN_EVENTS);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, // Events/*
                PATH_EVENTS + DataProvider.FILTER_ROW, ID_MAIN_EVENTS);

        ////// ID_USER_LOCATION
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, // User/*/Location
                PATH_USER + '*' + PATH_LOCATION, ID_USER_LOCATION);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, // User/*/Location/#
                PATH_USER + '*' + PATH_LOCATION + DataProvider.SINGLE_ROW, ID_USER_LOCATION);

        ////// ID_PICK_MEMBER
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, // Member
                PATH_MEMBER, ID_PICK_MEMBER);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, // Member/*
                PATH_MEMBER + DataProvider.FILTER_ROW, ID_PICK_MEMBER);
    }
}
