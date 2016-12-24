package com.studio.artaban.leclassico.data.codes;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.studio.artaban.leclassico.LeClassicoApp;
import com.studio.artaban.leclassico.data.Constants;

/**
 * Created by pascal on 23/08/16.
 * Application preference keys + User settings
 */
public final class Preferences {

    ////// Application states //////////////////////////////////////////////////////////////////////

    public static final String INTRODUCTION_DONE = "introductionDone"; // Introduction done flag
    public static final String INTRODUCTION_ERROR_DISPLAY = "errorDisplay"; // Error dialog display flag

    public static final String CONNECTION_STEP = "connectionStep"; // Connection step
    public static final String CONNECTION_PROGRESS = "connectionProgress"; // Synchronization progress status
    public static final String CONNECTION_ONLINE = "connectionOnline"; // Online connection flag

    public static final String MAIN_BEST_PHOTO = "mainBestPhoto"; // Best photo ID displayed


    ////// User settings ///////////////////////////////////////////////////////////////////////////

    ////// Notifications (see '/xml/settings_fragment_notify.xml')
    public static final String SETTINGS_NOTIFY_SOUND = "pref_notify_sound";
    public static final String SETTINGS_NOTIFY_VIBRATE = "pref_notify_vibrate";
    public static final String SETTINGS_NOTIFY_LIGHT = "pref_notify_light";

    //////
    public static boolean getBoolean(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LeClassicoApp.getInstance());
        return prefs.getBoolean(key, false);
    }
    public static int getInt(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LeClassicoApp.getInstance());
        return prefs.getInt(key, Constants.NO_DATA);
    }
}
