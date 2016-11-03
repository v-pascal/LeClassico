package com.studio.artaban.leclassico.data.codes;

/**
 * Created by pascal on 11/08/16.
 * Error codes
 */
public final class Errors {

    /////////////////////////////////////////////////////////////////////////////////// Web services

    // Depending on web site files:
    // * WebServices/constants.php
    // * Librairies/publication.js

    public static final byte WEBSERVICE_SERVER_UNAVAILABLE = 1;

    // Connection
    public static final byte WEBSERVICE_LOGIN_FAILED = 2;
    public static final byte WEBSERVICE_TOKEN_EXPIRED = 3;
    public static final byte WEBSERVICE_INVALID_LOGIN = 4;
    public static final byte WEBSERVICE_SYSTEM_DATE = 5;

    public static final byte WEBSERVICE_INVALID_TOKEN = 6;
    public static final byte WEBSERVICE_INVALID_USER = 7;
    public static final byte WEBSERVICE_INVALID_OPERATION = 8;

    public static final byte WEBSERVICE_MISSING_KEYS = 9;
    public static final byte WEBSERVICE_MISSING_STATUS = 10;
    public static final byte WEBSERVICE_MISSING_UPDATES = 11;
    public static final byte WEBSERVICE_INVALID_KEYS = 12;
    public static final byte WEBSERVICE_INVALID_STATUS = 13;
    public static final byte WEBSERVICE_INVALID_UPDATES = 14;

    public static final byte WEBSERVICE_QUERY_INSERT = 15;
    public static final byte WEBSERVICE_QUERY_UPDATE = 16;
    public static final byte WEBSERVICE_QUERY_DELETE = 17;

    // Publications
    public static final byte WEBSERVICE_INVALID_PUBLICATION_ID = 18;
    public static final byte WEBSERVICE_REQUEST_PUBLICATION_DELETE = 19;
    public static final byte WEBSERVICE_REQUEST_PUBLICATION_INSERT = 20;

    // Comments
    public static final byte WEBSERVICE_REQUEST_COMMENT_DELETE = 21;
    public static final byte WEBSERVICE_REQUEST_COMMENT_INSERT = 22;
}
