package com.studio.artaban.leclassico.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

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
    public static final String DATA_KEY_LIMIT = "limit";

    public static String getMaxStatusDate(ContentResolver resolver, Bundle data) {
    // Return max status date found into table (specified by data)

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";data: " + data);
        Cursor cursor = resolver.query(Uri.parse(DataProvider.CONTENT_URI + data.getString(DATA_KEY_TABLE_NAME)),
                new String[]{ "max(" + data.getString(DATA_KEY_FIELD_STATUS_DATE) + ")" },
                data.getString(DATA_KEY_FIELD_PSEUDO) + "='" + data.getString(DATA_KEY_PSEUDO) + '\'',
                null, null);
        cursor.moveToFirst();
        String maxDate = cursor.getString(0);
        cursor.close();

        return maxDate;
    }

    //////
    protected static String getUrlSynchroRequest(ContentResolver resolver, Bundle data) {
        // Return URL of synchronization request according table data

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";data: " + data);
        String url = Constants.APP_WEBSERVICES + data.getString(DATA_KEY_WEB_SERVICE) + '?' +
                WebServices.DATA_TOKEN + '=' + data.getString(DATA_KEY_TOKEN); // Add token to URL

        // Get last synchronization date
        String statusDate = getMaxStatusDate(resolver, data);
        if (statusDate != null) {

            Logs.add(Logs.Type.I, "Previous status date: " + statusDate);
            url += '&' + WebServices.DATA_DATE + '=' + statusDate.replace(' ', 'n');
        }
        if (data.containsKey(DATA_KEY_LIMIT))
            url += '&' + WebServices.DATA_LIMIT + '=' + data.getShort(DATA_KEY_LIMIT);

        return url;
    }

    ////// DataTable ///////////////////////////////////////////////////////////////////////////////

    public abstract int synchronize(ContentResolver resolver, String token, String pseudo,
                                    @Nullable Short limit, @Nullable ContentValues postData);
}
