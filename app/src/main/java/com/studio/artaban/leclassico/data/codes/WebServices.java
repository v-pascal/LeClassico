package com.studio.artaban.leclassico.data.codes;

/**
 * Created by pascal on 13/08/16.
 * Web service constants data
 */
public final class WebServices {

    ////// URL
    public static final String URL_CONNECTION = "/connexion.php"; // Connection web service
    public static final String URL_MEMBERS = "/camarades.php"; // Members web service
    public static final String URL_FOLLOWERS = "/abonnements.php"; // Followers web service
    public static final String URL_NOTIFICATIONS = "/notifications.php"; // Notifications web service
    public static final String URL_MESSAGERIE = "/messagerie.php"; // Mailbox web service
    public static final String URL_PUBLICATIONS = "/actualites.php"; // Publications web service
    public static final String URL_COMMENTS = "/commentaires.php"; // Comments web service


    ////// GET & POST data keys
    public static final String DATA_TOKEN = "Clf";
    public static final String DATA_DATE = "Date";
    public static final String DATA_LIMIT = "Count";

    // Connection
    public static final String CONNECTION_DATA_PSEUDO = "psd";
    public static final String CONNECTION_DATA_PASSWORD = "ccf";
    public static final String CONNECTION_DATA_DATETIME = "odt";


    ////// JSON keys
    public static final String JSON_KEY_ERROR = "Error"; // JSON object error field (containing the web service error codes)
    public static final String JSON_KEY_STATUS = "Status"; // JSON object DB status field

    // Connection
    public static final String JSON_KEY_LOGGED = "Logged";
    public static final String JSON_KEY_PSEUDO = "Pseudo";
    public static final String JSON_KEY_TOKEN = "Token";
    public static final String JSON_KEY_TIME_LAG = "TimeLag";


    ////// Status field IDs (eg. on 'Camarades' table the 'CAM_Status' field)
    public static final int STATUS_FIELD_NEW = 0;
    public static final int STATUS_FIELD_UPDATED = 1;
    public static final int STATUS_FIELD_DELETED = 2;

}
