package com.studio.artaban.leclassico.data.codes;

/**
 * Created by pascal on 13/08/16.
 * Web service constants data
 */
public final class WebServices {

    ////// URL
    public static final String URL_CONNECTION = "/connexion.php"; // Connection web service
    public static final String URL_MEMBERS = "/camarades.php"; // Members web service
    public static final String URL_PUBLICATIONS = "/actualites.php"; // Publications web service
    public static final String URL_COMMENTS = "/commentaires.php"; // Comments web service


    ////// GET & POST data keys
    public static final String DATA_TOKEN = "Clf";
    public static final String DATA_DATE = "Date";

    // Connection
    public static final String CONNECTION_DATA_PSEUDO = "psd";
    public static final String CONNECTION_DATA_PASSWORD = "ccf";
    public static final String CONNECTION_DATA_DATETIME = "odt";


    ////// JSON keys
    public static final String JSON_KEY_ERROR = "error"; // JSON object error field (containing the web service error codes)

    // Connection
    public static final String JSON_KEY_LOGGED = "logged";
    public static final String JSON_KEY_PSEUDO = "pseudo";
    public static final String JSON_KEY_TOKEN = "token";
    public static final String JSON_KEY_TIME_LAG = "timeLag";

}
