package com.studio.artaban.leclassico.data.codes;

/**
 * Created by pascal on 13/08/16.
 * Web service constants data
 */
public final class WebServices {

    public static final char LIST_SEPARATOR = 'n';

    ////// URL /////////////////////////////////////////////////////////////////////////////////////

    public static final String URL_CONNECTION = "/connexion.php"; // Connection web service
    public static final String URL_MEMBERS = "/camarades.php"; // Members web service
    public static final String URL_FOLLOWERS = "/abonnements.php"; // Followers web service
    public static final String URL_NOTIFICATIONS = "/notifications.php"; // Notifications web service
    public static final String URL_MESSAGERIE = "/messagerie.php"; // Mailbox web service
    public static final String URL_PUBLICATIONS = "/actualites.php"; // Publications web service
    public static final String URL_COMMENTS = "/commentaires.php"; // Comments web service
    public static final String URL_PHOTOS = "/photos.php"; // Photos web service
    public static final String URL_EVENTS = "/evenements.php"; // Events web service


    ////// DB Operations ///////////////////////////////////////////////////////////////////////////

    public static final byte OPERATION_SELECT = 1;
    public static final byte OPERATION_SELECT_OLD = 2;
    public static final byte OPERATION_UPDATE = 3;
    public static final byte OPERATION_INSERT = 4;
    public static final byte OPERATION_DELETE = 5;


    ////// GET & POST //////////////////////////////////////////////////////////////////////////////

    public static final String DATA_TOKEN = "Clf";
    public static final String DATA_OPERATION = "Ope";
    public static final String DATA_STATUS_DATE = "StatusDate";
    public static final String DATA_LIMIT = "Count";

    public static final String DATA_DATE = "Date";

    public static final String DATA_KEYS = "Keys";
    public static final String DATA_STATUS = "Status";
    public static final String DATA_UPDATES = "Updates";

    // Connection
    public static final String CONNECTION_DATA_PSEUDO = "psd";
    public static final String CONNECTION_DATA_PASSWORD = "ccf";
    public static final String CONNECTION_DATA_DATETIME = "odt";

    // Comments
    public static final String COMMENTS_DATA_TYPE = "Type";
    public static final String COMMENTS_DATA_IDS = "Ids";

    // Votes
    public static final String VOTES_DATA_TYPE = "Type";

    // Photos
    public static final String PHOTOS_DATA_BEST = "Best";
    public static final String PHOTOS_DATA_IDS = "Ids";


    ////// JSON ////////////////////////////////////////////////////////////////////////////////////

    public static final String JSON_KEY_ERROR = "Error"; // JSON object error field (containing the web service error codes)
    public static final String JSON_KEY_STATUS = "Status"; // JSON object DB status field

    // Connection
    public static final String JSON_KEY_LOGGED = "Logged";
    public static final String JSON_KEY_PSEUDO = "Pseudo";
    public static final String JSON_KEY_TOKEN = "Token";
    public static final String JSON_KEY_TIME_LAG = "TimeLag";
}
