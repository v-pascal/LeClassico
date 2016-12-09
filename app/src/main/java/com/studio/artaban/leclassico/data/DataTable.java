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

        int id = Constants.NO_DATA;
        if ((result.moveToFirst()) && (result.getCount() == 1))
            id = result.getInt(0);
        result.close();

        return id;
    }

    ////// Notifications

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

    ////// Synchronization

    protected static final int STATUS_FIELD_NEW = 0;
    protected static final int STATUS_FIELD_UPDATED = 1;
    protected static final int STATUS_FIELD_DELETED = 2;
    // Status field IDs (remote DB)

    public enum Synchronized { // Synchronized field values (local DB)

        DONE((byte)0x00), // Synchronized
        TO_INSERT((byte)0x01), TO_UPDATE((byte)0x02), TO_DELETE((byte)0x03), // Operations
        IN_PROGRESS((byte)0x10); // In progress status mask (only combined with an operation)

        //
        private final byte id;
        Synchronized(byte id) { this.id = id; }
        public byte getValue() { return this.id; }
    }

    ////// DataTable ///////////////////////////////////////////////////////////////////////////////

    protected static final String IS_NULL = " is null";

    // URL synchronization request data keys
    public static final String DATA_KEY_TOKEN = "token";
    public static final String DATA_KEY_LIMIT = "limit";
    public static final String DATA_KEY_PSEUDO = "pseudo";
    public static final String DATA_KEY_DATE = "date";
    public static final String DATA_KEY_STATUS_DATE = "statusDate";

    protected static final String DATA_KEY_WEB_SERVICE = "webService";
    protected static final String DATA_KEY_OPERATION = "operation";
    protected static final String DATA_KEY_TABLE_NAME = "tableName";
    protected static final String DATA_KEY_FIELD_PSEUDO = "pseudoField";
    protected static final String DATA_KEY_FIELD_DATE = "dateField";

    //////
    protected static String getMaxStatusDate(ContentResolver resolver, Bundle data) {
    // Return max status date of table specified by data for a select operation (URL)

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";data: " + data);
        Cursor cursor = resolver.query(Uri.parse(DataProvider.CONTENT_URI + data.getString(DATA_KEY_TABLE_NAME)),
                new String[]{ "max(" + Constants.DATA_COLUMN_STATUS_DATE + ')' },
                ((data.containsKey(DATA_KEY_FIELD_PSEUDO))? // Check pseudo criteria need
                        data.getString(DATA_KEY_FIELD_PSEUDO) + "='" + data.getString(DATA_KEY_PSEUDO) + "' AND ":"") +
                        Constants.DATA_COLUMN_SYNCHRONIZED + '=' + Synchronized.DONE.getValue(), // Unchanged
                null, null);

        String maxDate = null;
        if (cursor.moveToFirst())
            maxDate = cursor.getString(0);
        cursor.close();

        return maxDate;
    }
    protected static String getMinDate(ContentResolver resolver, Bundle data) {
    // Return oldest entry date of table specified by data for a select operation (old entries request)

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";data: " + data);
        Cursor cursor = resolver.query(Uri.parse(DataProvider.CONTENT_URI + data.getString(DATA_KEY_TABLE_NAME)),
                new String[]{ "min(" + data.getString(DATA_KEY_FIELD_DATE) + ')' },
                ((data.containsKey(DATA_KEY_FIELD_PSEUDO))? // Check pseudo criteria need
                        data.getString(DATA_KEY_FIELD_PSEUDO) + "='" + data.getString(DATA_KEY_PSEUDO) + "' AND ":"") +
                        Constants.DATA_COLUMN_SYNCHRONIZED + '=' + Synchronized.DONE.getValue(), // Unchanged
                null, null);

        String minDate = null;
        if (cursor.moveToFirst())
            minDate = cursor.getString(0);
        cursor.close();

        return minDate;
    }

    protected static String getSyncUrlRequest(ContentResolver resolver, Bundle data) {
    // Return URL of synchronization request according table data

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";data: " + data);
        byte operation = data.getByte(DATA_KEY_OPERATION);
        String url = Constants.APP_WEBSERVICES + data.getString(DATA_KEY_WEB_SERVICE) + '?' +
                WebServices.DATA_TOKEN + '=' + data.getString(DATA_KEY_TOKEN) + '&' + // Add token...
                WebServices.DATA_OPERATION + '=' + operation; // ...& operation (always)

        // Get & add last synchronization date & date criteria (if needed)
        switch (operation) {
            case WebServices.OPERATION_SELECT: { ////// Selection

                String statusDate = (data.containsKey(DATA_KEY_STATUS_DATE))?
                        data.getString(DATA_KEY_STATUS_DATE) : getMaxStatusDate(resolver, data);
                if (statusDate == null)
                    break; // No entry (no date criteria)

                Logs.add(Logs.Type.I, "Previous status date: " + statusDate);
                url += '&' + WebServices.DATA_STATUS_DATE + '=' +
                        statusDate.replace(' ', WebServices.LIST_SEPARATOR);
                //break;
            }
            case WebServices.OPERATION_SELECT_OLD: { ////// Old selection

                // Add old date criteria
                String date = null;
                if (data.containsKey(DATA_KEY_DATE))
                    date = data.getString(DATA_KEY_DATE);
                else if (data.containsKey(DATA_KEY_FIELD_DATE)) // If existing
                    date = getMinDate(resolver, data);

                if (date == null)
                    break;

                Logs.add(Logs.Type.I, "Previous date: " + date);
                url += '&' + WebServices.DATA_DATE + '=' + date.replace(' ', WebServices.LIST_SEPARATOR);
                break;
            }
        }
        if (data.containsKey(DATA_KEY_LIMIT)) // Add result limitation (if needed)
            url += '&' + WebServices.DATA_LIMIT + '=' + data.getShort(DATA_KEY_LIMIT);

        return url;
    }
    protected void resetSyncInProgress(ContentResolver resolver, Bundle data) {
    // Remove all in progress synchronization status (replaced by its operation)

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";data: " + data);
        byte sync;
        switch (data.getByte(DATA_KEY_OPERATION)) {
            case WebServices.OPERATION_UPDATE: sync = Synchronized.TO_UPDATE.getValue(); break;
            case WebServices.OPERATION_INSERT: sync = Synchronized.TO_INSERT.getValue(); break;
            case WebServices.OPERATION_DELETE: sync = Synchronized.TO_DELETE.getValue(); break;
            default:
                throw new IllegalArgumentException("Unexpected DB operation");
        }
        ContentValues values = new ContentValues();
        values.put(Constants.DATA_COLUMN_SYNCHRONIZED, sync);
        resolver.update(Uri.parse(DataProvider.CONTENT_URI + data.getString(DATA_KEY_TABLE_NAME)), values,
                data.getString(DATA_KEY_FIELD_PSEUDO) + "='" + data.getString(DATA_KEY_PSEUDO) + "' AND " +
                        Constants.DATA_COLUMN_SYNCHRONIZED + '=' + String.valueOf(sync |
                        DataTable.Synchronized.IN_PROGRESS.getValue()),
                null);
    }

    //////
    public abstract ContentValues syncInserted(ContentResolver resolver, String pseudo);
    public abstract ContentValues syncUpdated(ContentResolver resolver, String pseudo);
    public abstract ContentValues syncDeleted(ContentResolver resolver, String pseudo);
    // Local to remote DB synchronization methods

    public static class SyncResult { // Remote to local DB synchronization result

        public static boolean hasChanged(SyncResult result) {
            return ((result != null) && ((result.inserted > 0) || (result.updated > 0) || (result.deleted > 0)));
        }
        public int inserted; // Inserted row count
        public int updated; // Updated row count
        public int deleted; // deleted row count
    }
    public abstract SyncResult synchronize(ContentResolver resolver, byte operation, Bundle syncData,
                                           @Nullable ContentValues postData);
}
