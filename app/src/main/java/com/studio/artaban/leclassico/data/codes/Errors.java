package com.studio.artaban.leclassico.data.codes;

/**
 * Created by pascal on 11/08/16.
 * Error codes
 */
public final class Errors {

    ////// Web services
    public static final byte WEBSERVICE_SERVER_UNAVAILABLE = 1;

    // Connection
    public static final byte WEBSERVICE_LOGIN_FAILED = 2;
    public static final byte WEBSERVICE_TOKEN_EXPIRED = 3;
    public static final byte WEBSERVICE_INVALID_LOGIN = 4;
    public static final byte WEBSERVICE_SYSTEM_DATE = 5;

    public static final byte WEBSERVICE_INVALID_TOKEN = 6;
    public static final byte WEBSERVICE_INVALID_USER = 7;
    public static final byte WEBSERVICE_INVALID_OPERATION = 8;

    // Publications
    public static final byte WEBSERVICE_INVALID_PUBLICATION_ID = 9;
    public static final byte WEBSERVICE_REQUEST_PUBLICATION_DELETE = 10;
    public static final byte WEBSERVICE_REQUEST_PUBLICATION_INSERT = 11;

    // Comments
    public static final byte WEBSERVICE_REQUEST_COMMENT_DELETE = 12;
    public static final byte WEBSERVICE_REQUEST_COMMENT_INSERT = 13;
}
