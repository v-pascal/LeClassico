package com.studio.artaban.leclassico.data.codes;

/**
 * Created by pascal on 07/09/16.
 * Query ID & limit (see QueryLoader class)
 */
public class Queries {

    ////// ID //////////////////////////////////////////////////////////////////////////////////////

    ////// Main
    public static final int MAIN_DATA_USER = Tables.ID_CAMARADES; // Table query
    public static final int MAIN_SHORTCUT_LAST_FOLLOWED = Tables.ID_MAX;
    public static final int MAIN_SHORTCUT_MAIL_COUNT = Tables.ID_MAX + 1;
    public static final int MAIN_SHORTCUT_NOTIFY_COUNT = Tables.ID_MAX + 2;
    public static final int MAIN_PUBLICATIONS_LIST = Tables.ID_MAX + 3;
    public static final int MAIN_BEST_PHOTOS = Tables.ID_MAX + 4;

    ////// Notifications
    public static final int NOTIFICATIONS_NEW_INFO = Tables.ID_MAX + 5;
    public static final int NOTIFICATIONS_MAIN_LIST = Tables.ID_MAX + 6;


    ////// Limit ///////////////////////////////////////////////////////////////////////////////////
    // Max entry count to display into a list view at start
    // NB: All limits must be < *Table.DEFAULT_LIMIT

    public static final short NOTIFICATIONS_LIST_LIMIT = 20;
    public static final short PUBLICATIONS_LIST_LIMIT = 5;
    public static final short COMMENTS_LIST_LIMIT = 4;


    ////// Older ///////////////////////////////////////////////////////////////////////////////////
    // Max entry count to display into a list after the last element (when old entries are requested)

    public static final short NOTIFICATIONS_OLD_LIMIT = 5;
    public static final short PUBLICATIONS_OLD_LIMIT = 3;
    public static final short COMMENTS_OLD_LIMIT = 4;
}
