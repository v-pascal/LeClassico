package com.studio.artaban.leclassico.data.codes;

import android.content.UriMatcher;
import android.net.Uri;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;

/**
 * Created by pascal on 13/10/16.
 * URI list (used to manage particular content observer)
 */
public class Uris {

    public static String getTable(Uri uri) {
        switch (URI_MATCHER.match(uri)) {

            case Tables.ID_NOTIFICATIONS: return NotificationsTable.TABLE_NAME;
        }
        return null;
    }

    ////// Path ////////////////////////////////////////////////////////////////////////////////////

    private static final String PATH_PSEUDO = "/Pseudo";

    ////// URI /////////////////////////////////////////////////////////////////////////////////////

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {

        ////// Main
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, NotificationsTable.TABLE_NAME + PATH_PSEUDO, Tables.ID_NOTIFICATIONS);

        ////// Reserved URI
        // * DataTable.SQL_QUERY_URI: for multiple table queries
    }
}
