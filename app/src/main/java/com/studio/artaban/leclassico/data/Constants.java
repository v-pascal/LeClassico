package com.studio.artaban.leclassico.data;

/**
 * Created by pascal on 15/07/16.
 * Application constants class
 */
public final class Constants {

    public static final int NO_DATA = -1; // No value (integer)
    public static final String DATABASE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.000"; // SQLite date format


    ////// Application
    public static final String APP_NAME = "LeClassico"; // Application name
    public static final String APP_WEBSITE = "http://www.leclassico.fr"; // Social network application web site
    public static final String APP_URI_COMPANY = "studio.artaban"; // Application company URI
    public static final String APP_URI = "leclassico"; // Application URI

    // Preferences
    public static final String APP_PREFERENCE = APP_NAME + "Preferences";
    public static final String APP_PREFERENCE_INTRO_DONE = "introDone";

    public static final String APP_WEBSERVICES = APP_WEBSITE + "/WebServices"; // LeClassico web services URL


    ////// Web services
    public static final String WEBSERVICE_CONNECTION = "/connexion.php"; // Connection web service
    public static final String WEBSERVICE_PUBLICATIONS = "/actualites.php"; // Publications web service
    public static final String WEBSERVICE_COMMENTS = "/commentaires.php"; // Comments web service

    public static final byte WEBSERVICE_ERROR_SERVER_UNAVAILABLE = 1;
    public static final byte WEBSERVICE_ERROR_LOGIN_FAILED = 2;
    public static final byte WEBSERVICE_ERROR_TOKEN_EXPIRED = 3;
    public static final byte WEBSERVICE_ERROR_INVALID_LOGIN = 4;
    public static final byte WEBSERVICE_ERROR_SYSTEM_DATE = 5;
    public static final byte WEBSERVICE_ERROR_INVALID_TOKEN = 6;
    public static final byte WEBSERVICE_ERROR_INVALID_USER = 7;
    public static final byte WEBSERVICE_ERROR_INVALID_PUBLICATION_ID = 8;
    public static final byte WEBSERVICE_ERROR_REQUEST_PUBLICATION_DELETE = 9;
    public static final byte WEBSERVICE_ERROR_REQUEST_PUBLICATION_INSERT = 10;
    public static final byte WEBSERVICE_ERROR_REQUEST_COMMENT_DELETE = 11;
    public static final byte WEBSERVICE_ERROR_REQUEST_COMMENT_INSERT = 12;
    // Error codes

    public static final String WEBSERVICE_JSON_ERROR = "error";
    // JSON object error field (containing the web service error codes above)

    public static final String WEBSERVICE_JSON_LOGGED = "logged";
    public static final String WEBSERVICE_JSON_PSEUDO = "pseudo";
    public static final String WEBSERVICE_JSON_TOKEN = "token";
    public static final String WEBSERVICE_JSON_TIME_LAG = "timeLag";
    // JSON connection object fields


    ////// Introduction
    public static final int INTRO_PAGE_COUNT = 5; // Introduction page count

    public static final int INTRO_BACKGROUND_IMAGE_HEIGHT = 507; // Fixed background image height (in pixel)
    public static final int INTRO_CONTAINER_IMAGE_HEIGHT = 192; // Fixed container image height (in pixel)
    public static final int INTRO_BALL_IMAGE_HEIGHT = 222; // Fixed ball image height (in pixel)
    public static final int INTRO_LIGHT_IMAGE_HEIGHT = 115; // Fixed light image height (in pixel)
    public static final int INTRO_DISK_TRAY_IMAGE_HEIGHT = 191; // Fixed disk tray image height (in pixel)
    public static final int INTRO_SOUND_SPEAKER_IMAGE_HEIGHT = 90; // Fixed sound speaker image height (in pixel)
    public static final int INTRO_SMILEY_IMAGE_HEIGHT = 86; // Fixed smiley image height (in pixel)

    public static final int INTRO_LINK_IMAGE_HEIGHT = 100; // Fixed link publication image height (in pixel)
    public static final int INTRO_FRIEND_IMAGE_HEIGHT = 100; // Fixed link publication image height (in pixel)
    public static final int INTRO_PHOTO_IMAGE_HEIGHT = 288; // Fixed link publication image height (in pixel)

    public static final int INTRO_GIRLS_IMAGE_HEIGHT = 200; // Fixed girls image height (in pixel)
    public static final int INTRO_COUPLE_IMAGE_HEIGHT = 263; // Fixed couple image height (in pixel)
    public static final int INTRO_INDOOR_IMAGE_HEIGHT = 120; // Fixed indoor party image height (in pixel)
    public static final int INTRO_OUTDOOR_IMAGE_HEIGHT = 166; // Fixed outdoor party image height (in pixel)
    public static final int INTRO_DJ_IMAGE_HEIGHT = 150; // Fixed DJ image height (in pixel)

    public static final int INTRO_EVENTS_IMAGE_HEIGHT = 337; // Fixed events image height (in pixel)
    public static final int INTRO_CALENDAR_IMAGE_HEIGHT = 188; // Fixed calendar image height (in pixel)
    public static final int INTRO_FLYER_IMAGE_HEIGHT = 420; // Fixed flyer image height (in pixel)

    public static final int INTRO_MARKER_IMAGE_HEIGHT = 100; // Fixed location marker image height (in pixel)


    ////// Database
    public static final String DATA_CONTENT_URI = "com." + APP_URI_COMPANY + ".provider." + APP_URI; // DB content provider URI
    public static final String DATA_COLUMN_SYNCHRONIZED = "Synchronized"; // Synchronized field

    public static final String DATA_DELETE_SELECTION = DATA_COLUMN_SYNCHRONIZED + "=" + DataProvider.Synchronized.TO_DELETE;
    // Selection criteria to delete records from DB definitively

    // Database table IDs
    public static final byte DATA_TABLE_ID_CAMARADES = 1; // Always start with 1 (0 is reserved)
    public static final byte DATA_TABLE_ID_ABONNEMENTS = 2;
    public static final byte DATA_TABLE_ID_ACTUALITES = 3;
    public static final byte DATA_TABLE_ID_ALBUMS = 4;
    public static final byte DATA_TABLE_ID_COMMENTAIRES = 5;
    public static final byte DATA_TABLE_ID_EVENEMENTS = 6;
    public static final byte DATA_TABLE_ID_MESSAGERIE = 7;
    public static final byte DATA_TABLE_ID_MUSIC = 8;
    public static final byte DATA_TABLE_ID_PHOTOS = 9;
    public static final byte DATA_TABLE_ID_PRESENTS = 10;
    public static final byte DATA_TABLE_ID_VOTES = 11;
    public static final byte DATA_TABLE_ID_NOTIFICATIONS = 12;

    public static final byte DATA_LAST_TABLE_ID = DATA_TABLE_ID_NOTIFICATIONS;

}
