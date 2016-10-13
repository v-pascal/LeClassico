package com.studio.artaban.leclassico.data.codes;

import android.content.UriMatcher;
import android.net.Uri;

import com.studio.artaban.leclassico.activities.main.MainActivity;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;

/**
 * Created by pascal on 13/10/16.
 * URI list (used to manage particular content observer)
 */
public class Uris {

    private static final String SINGLE_ROW = "#/";

    //
    public static int getUriTableId(Uri uri) { // Return table ID associated with the URI
        return URI_MATCHER.match(uri);
    }
    public static String getUriTable(Uri uri) { // Return table name associated with the URI
        switch (getUriTableId(uri)) {

            ////// Main
            case ID_MAIN_NOTIFY:
            case ID_MAIN_NOTIFY_SINGLE:
                return NotificationsTable.TABLE_NAME;
        }
        return null;
    }

    ////// Path ////////////////////////////////////////////////////////////////////////////////////

    public static final String PATH_PSEUDO = "Pseudo/"; // Pseudo URI path (following with pseudo Id)

    // Main
    private static final String PATH_MAIN_NOTIFY = PATH_PSEUDO + SINGLE_ROW + NotificationsTable.TABLE_NAME;
    private static final String PATH_MAIN_NOTIFY_SINGLE = PATH_MAIN_NOTIFY + DataProvider.SINGLE_ROW;

    ////// ID //////////////////////////////////////////////////////////////////////////////////////

    // Main
    public static final int ID_MAIN_NOTIFY = 0;
    public static final int ID_MAIN_NOTIFY_SINGLE = 1;

    ////// URI /////////////////////////////////////////////////////////////////////////////////////

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {

        ////// Main
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, PATH_MAIN_NOTIFY, ID_MAIN_NOTIFY);
        URI_MATCHER.addURI(Constants.DATA_CONTENT_URI, PATH_MAIN_NOTIFY_SINGLE, ID_MAIN_NOTIFY_SINGLE);

        ////// Reserved URI
        // See DataProvider class
    }
}
