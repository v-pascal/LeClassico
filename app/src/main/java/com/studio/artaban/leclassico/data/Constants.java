package com.studio.artaban.leclassico.data;

/**
 * Created by pascal on 15/07/16.
 * Application constants class
 */
public final class Constants {

    public static final int NO_DATA = -1; // No value (integer)
    public static final String DATABASE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.000"; // SQLite date format


    ////// Application
    public static final String APP_NAME = "LeClassico"; // Application name
    public static final String APP_WEBSITE = "http://www.leclassico.fr/"; // Social network application web site
    public static final String APP_URI_COMPANY = "studio.artaban"; // Application company URI
    public static final String APP_URI = "leclassico"; // Application URI

    public static final String APP_WEBSERVICES = APP_WEBSITE + "WebServices"; // LeClassico web services URL
    public static final String APP_URL_PROFILES = APP_WEBSITE + "Profiles"; // LeClassico profile URL

    // Preference keys
    public static final String APP_PREFERENCE = APP_NAME + "Preferences"; // Application preference


    ////// Images
    public static final int IMAGE_JPEG_QUALITY = 80; // Default stored JPEG file quality
    public static final int IMAGE_PNG_QUALITY = 80; // Default stored PNG file quality

    // File extensions
    public static final String IMAGE_JPEG_EXTENSION = ".JPG";
    public static final String IMAGE_PNG_EXTENSION = ".PNG";


    ////// Introduction
    public static final int INTRO_PAGE_COUNT = 5; // Introduction page count

    public static final int INTRO_BACKGROUND_IMAGE_HEIGHT = 507; // Fixed background image height (in pixel)
    public static final int INTRO_CONTAINER_IMAGE_HEIGHT = 192; // Fixed container image height (in pixel)
    public static final int INTRO_BALL_IMAGE_HEIGHT = 222; // Fixed ball image height (in pixel)
    public static final int INTRO_LIGHT_IMAGE_HEIGHT = 115; // Fixed light image height (in pixel)
    public static final int INTRO_DISK_TRAY_IMAGE_HEIGHT = 191; // Fixed disk tray image height (in pixel)
    public static final int INTRO_SOUND_SPEAKER_IMAGE_HEIGHT = 90; // Fixed sound speaker image height (in pixel)
    public static final int INTRO_SMILEY_IMAGE_HEIGHT = 86; // Fixed smiley image height (in pixel)

    public static final int INTRO_LINK_IMAGE_HEIGHT = 100; // Fixed link publication image height (in pixel)
    public static final int INTRO_FRIEND_IMAGE_HEIGHT = 100; // Fixed link publication image height (in pixel)
    public static final int INTRO_PHOTO_IMAGE_HEIGHT = 288; // Fixed link publication image height (in pixel)

    public static final int INTRO_GIRLS_IMAGE_HEIGHT = 200; // Fixed girls image height (in pixel)
    public static final int INTRO_COUPLE_IMAGE_HEIGHT = 263; // Fixed couple image height (in pixel)
    public static final int INTRO_INDOOR_IMAGE_HEIGHT = 120; // Fixed indoor party image height (in pixel)
    public static final int INTRO_OUTDOOR_IMAGE_HEIGHT = 166; // Fixed outdoor party image height (in pixel)
    public static final int INTRO_DJ_IMAGE_HEIGHT = 150; // Fixed DJ image height (in pixel)

    public static final int INTRO_EVENTS_IMAGE_HEIGHT = 337; // Fixed events image height (in pixel)
    public static final int INTRO_CALENDAR_IMAGE_HEIGHT = 188; // Fixed calendar image height (in pixel)
    public static final int INTRO_FLYER_IMAGE_HEIGHT = 420; // Fixed flyer image height (in pixel)

    public static final int INTRO_MARKER_IMAGE_HEIGHT = 100; // Fixed location marker image height (in pixel)


    ////// Database
    public static final String DATA_CONTENT_URI = "com." + APP_URI_COMPANY + ".provider." + APP_URI; // DB content provider URI
    public static final String DATA_COLUMN_SYNCHRONIZED = "Synchronized"; // Synchronized field

    public static final String DATA_DELETE_SELECTION = DATA_COLUMN_SYNCHRONIZED + "=" + DataProvider.Synchronized.TO_DELETE;
    // Selection criteria to delete records from DB definitively


    ////// Main
    public static final int MAIN_SECTION_HOME = 0;
    public static final int MAIN_SECTION_PUBLICATIONS = 1;
    public static final int MAIN_SECTION_EVENTS = 2;
    public static final int MAIN_SECTION_MEMBERS = 3;
    public static final int MAIN_SECTION_NOTIFICATIONS = 4;
    // Section indexes

    public static final int MAIN_PAGE_COUNT = MAIN_SECTION_NOTIFICATIONS + 1; // Main page count

}
