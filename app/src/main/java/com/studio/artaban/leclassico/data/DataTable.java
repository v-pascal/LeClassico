package com.studio.artaban.leclassico.data;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.data.codes.WebServices;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 24/08/16.
 * Abstract DB table class
 * Containing shared method used by DB tables (such as method needed for synchronization)
 */
public abstract class DataTable implements IDataTable {

    public static final String SQL_QUERY_URI = "SQL"; // Reserved SQL query URI (for complex queries)

    // URL synchronization request data keys
    public static final String DATA_KEY_WEB_SERVICE = "webService";
    public static final String DATA_KEY_TOKEN = "token";
    public static final String DATA_KEY_PSEUDO = "pseudo";
    public static final String DATA_KEY_TABLE_NAME = "tableName";
    public static final String DATA_KEY_FIELD_STATUS_DATE = "statusDate";
    public static final String DATA_KEY_FIELD_PSEUDO = "pseudoField";

    //////
    protected static String getUrlSynchroRequest(ContentResolver resolver, Bundle data) {

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";data: " + data);
        String url = Constants.APP_WEBSERVICES + data.getString(DATA_KEY_WEB_SERVICE) + "?" +
                WebServices.DATA_TOKEN + "=" + data.getString(DATA_KEY_TOKEN); // Add token to URL

        // Get last synchronization date
        Cursor cursor = resolver.query(Uri.parse(DataProvider.CONTENT_URI + data.getString(DATA_KEY_TABLE_NAME)),
                new String[]{ "max(" + data.getString(DATA_KEY_FIELD_STATUS_DATE) + ")" },
                data.getString(DATA_KEY_FIELD_PSEUDO) + "='" + data.getString(DATA_KEY_PSEUDO) + "'",
                null, null);
        cursor.moveToFirst();
        if (cursor.getString(0) != null) {
            Logs.add(Logs.Type.I, "Previous status date: " + cursor.getString(0));
            url += "&" + WebServices.DATA_DATE + "=" + cursor.getString(0).replace(' ', 'n');
        }
        cursor.close();
        return url;
    }
}
