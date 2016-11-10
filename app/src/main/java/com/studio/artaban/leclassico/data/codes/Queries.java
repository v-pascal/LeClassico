package com.studio.artaban.leclassico.data.codes;

/**
 * Created by pascal on 07/09/16.
 * Query ID & limit (see QueryLoader class)
 */
public class Queries {

    ////// ID //////////////////////////////////////////////////////////////////////////////////////

    ////// Main
    public static final int MAIN_DATA_NEW_NOTIFY = Tables.ID_MAX;
    public static final int MAIN_DATA_USER = Tables.ID_CAMARADES; // Table query
    public static final int MAIN_SHORTCUT_NOTIFY_COUNT = Tables.ID_MAX + 2;
    public static final int MAIN_SHORTCUT_LAST_FOLLOWED = Tables.ID_MAX + 3;
    public static final int MAIN_SHORTCUT_MAIL_COUNT = Tables.ID_MAX + 4;
    public static final int MAIN_NOTIFY_LIST = Tables.ID_MAX + 5;

    ////// Limit ///////////////////////////////////////////////////////////////////////////////////
    // Max entry count to display into a list view at start

    public static final short LIMIT_MAIN_NOTIFY = 10; //20;

    ////// Older ///////////////////////////////////////////////////////////////////////////////////
    // Max entry count to display into a list after the last element (when older entries is requested)

    public static final short OLDER_MAIN_NOTIFY = 5;

}
