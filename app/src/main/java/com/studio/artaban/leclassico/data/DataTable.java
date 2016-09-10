package com.studio.artaban.leclassico.data;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.studio.artaban.leclassico.data.codes.WebServices;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 24/08/16.
 * Abstract DB table class
 * Containing shared method used by DB tables (such as method needed for synchronization)
 */
public abstract class DataTable implements IDataTable {

    public static final String SQL_QUERY_URI = "SQL"; // Reserved SQL query URI (for complex queries)

    //////
    protected static String getUrlSynchroRequest(ContentResolver resolver, String serviceUrl,
                                                 String token, String tableName, String statusDate) {

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";serviceUrl: " + serviceUrl +
                ";token: " + token + ";tableName: " + tableName + ";statusDate: " + statusDate);
        String url = Constants.APP_WEBSERVICES + serviceUrl + "?" +
                WebServices.DATA_TOKEN + "=" + token; // Add token to URL

        // Get last synchronization date
        Cursor cursor = resolver.query(Uri.parse(DataProvider.CONTENT_URI + tableName),
                new String[]{ "max(" + statusDate + ")" }, null, null, null);
        cursor.moveToFirst();
        if (cursor.getString(0) != null) {
            Logs.add(Logs.Type.I, "Previous status date: " + cursor.getString(0));
            url += "&" + WebServices.DATA_DATE + "=" + cursor.getString(0).replace(' ', 'n');
        }
        cursor.close();
        return url;
    }
}
