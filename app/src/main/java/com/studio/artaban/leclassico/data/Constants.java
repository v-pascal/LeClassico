package com.studio.artaban.leclassico.data;

/**
 * Created by pascal on 15/07/16.
 * Application constants class
 */
public final class Constants {

    public static final int NO_DATA = -1; // No value (integer)


    ////// Application
    public static final String APP_NAME = "LeClassico"; // Application name
    public static final String APP_WEBSITE = "http://leclassico.fr"; // Social network application web site
    public static final String APP_COMPANY = "studio.artaban"; // Application company URI

    // Preferences
    public static final String APP_PREFERENCE = APP_NAME + "Preferences";
    public static final String APP_PREFERENCE_INTRO_DONE = "introDone";


    ////// Introduction
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
    public static final String DATA_CONTENT_URI = "com.studio.artaban.provider.leclassico"; // DB content provider URI
    public static final String DATA_COLUMN_SYNCHRONIZED = "Synchronized"; // Synchronized field

    // Database table IDs
    public static final int DATA_CAMARADES_TABLE_ID = 1;
    public static final int DATA_ABONNEMENTS_TABLE_ID = 2;
    public static final int DATA_ACTUALITES_TABLE_ID = 3;
    public static final int DATA_ALBUMS_TABLE_ID = 4;
    public static final int DATA_COMMENTAIRES_TABLE_ID = 5;
    public static final int DATA_EVENEMENTS_TABLE_ID = 6;
    public static final int DATA_MESSAGERIE_TABLE_ID = 7;
    public static final int DATA_MUSIC_TABLE_ID = 8;
    public static final int DATA_PHOTOS_TABLE_ID = 9;
    public static final int DATA_PRESENTS_TABLE_ID = 10;
    public static final int DATA_VOTES_TABLE_ID = 11;

}
