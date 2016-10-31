package com.studio.artaban.leclassico.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.studio.artaban.leclassico.data.codes.WebServices;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.helpers.Database;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 24/08/16.
 * Abstract DB table class
 * Containing DB shared & tools methods used by tables (such as method needed for synchronization)
 */
public abstract class DataTable implements IDataTable {

    public static int getEntryCount(ContentResolver resolver, String table, String selection) {
    // Get entry count on specific table and according selection criteria

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";table: " + table + ";selection: " + selection);
        Cursor result = resolver.query(Uri.parse(DataProvider.CONTENT_URI + table),
                new String[]{"count(*)"}, selection, null, null);
        result.moveToFirst();
        int count = result.getInt(0);
        result.close();

        return count;
    }

    public static int getEntryId(ContentResolver resolver, String table, String selection) {
    // Get entry Id on specific table and according selection criteria
    // NB: Returns NO_DATA if more than one record is found with selection criteria

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";table: " + table + ";selection: " + selection);
        Cursor result = resolver.query(Uri.parse(DataProvider.CONTENT_URI + table),
                new String[]{IDataTable.DataField.COLUMN_ID}, selection, null, null);
        result.moveToFirst();
        int id = result.getInt(0);
        if (result.getCount() > 1)
            id = Constants.NO_DATA;
        result.close();

        return id;
    }

    public static int getNewNotification(ContentResolver resolver, String pseudo) {
    // Return new notification count for current user (pseudo)

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";pseudo: " + pseudo);
        Cursor newNotify = resolver.query(Uri.parse(DataProvider.CONTENT_URI + NotificationsTable.TABLE_NAME),
                new String[]{NotificationsTable.COLUMN_LU_FLAG},
                NotificationsTable.COLUMN_PSEUDO + '=' + DatabaseUtils.sqlEscapeString(pseudo),
                null, NotificationsTable.COLUMN_DATE + " DESC");

        int newCount = 0;
        if (newNotify.moveToFirst()) {
            do {
                if (newNotify.getInt(0) != Constants.DATA_UNREAD)
                    break;
                ++newCount;

            } while (newNotify.moveToNext());
        }
        return newCount;
    }

    public static String getMaxStatusDate(ContentResolver resolver, Bundle data) {
    // Return max status date found into table (specified by data)

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";data: " + data);
        Cursor cursor = resolver.query(Uri.parse(DataProvider.CONTENT_URI + data.getString(DATA_KEY_TABLE_NAME)),
                new String[]{ "max(" + Constants.DATA_COLUMN_STATUS_DATE + ")" },
                data.getString(DATA_KEY_FIELD_PSEUDO) + "='" + data.getString(DATA_KEY_PSEUDO) + '\'',
                null, null);
        cursor.moveToFirst();
        String maxDate = cursor.getString(0);
        cursor.close();

        return maxDate;
    }

    ////// DataTable ///////////////////////////////////////////////////////////////////////////////

    protected static final String IS_NULL = " is null";

    // URL synchronization request data keys
    public static final String DATA_KEY_WEB_SERVICE = "webService";
    public static final String DATA_KEY_TOKEN = "token";
    public static final String DATA_KEY_PSEUDO = "pseudo";
    public static final String DATA_KEY_TABLE_NAME = "tableName";
    public static final String DATA_KEY_FIELD_PSEUDO = "pseudoField";
    public static final String DATA_KEY_LIMIT = "limit";

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

    //////
    public abstract Database.SyncResult synchronize(ContentResolver resolver, String token, String pseudo,
                                                    @Nullable Short limit, @Nullable ContentValues postData);
}
