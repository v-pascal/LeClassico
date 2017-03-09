package com.studio.artaban.leclassico.data.codes;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

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

    ////// Notifications (see '/xml/settings_fragment_user.xml')
    public static final String SETTINGS_USER_PASSWORD = "pref_user_password";
    public static final String SETTINGS_USER_NAME = "pref_user_name";
    public static final String SETTINGS_USER_SURNAME = "pref_user_surname";
    public static final String SETTINGS_USER_GENDER = "pref_user_gender";
    public static final String SETTINGS_USER_BIRTHDAY = "pref_user_birthday";
    public static final String SETTINGS_USER_ADDRESS = "pref_user_address";
    public static final String SETTINGS_USER_TOWN = "pref_user_town";
    public static final String SETTINGS_USER_POSTAL_CODE = "pref_user_postal_code";
    public static final String SETTINGS_USER_PHONE = "pref_user_phone";
    public static final String SETTINGS_USER_EMAIL = "pref_user_email";
    public static final String SETTINGS_USER_HOBBIES = "pref_user_hobbies";
    public static final String SETTINGS_USER_ABOUT = "pref_user_about";

    //////
    private static final SharedPreferences AppPrefs =
            PreferenceManager.getDefaultSharedPreferences(LeClassicoApp.getInstance());

    public static boolean getBoolean(String key) {
        return AppPrefs.getBoolean(key, false);
    }
    public static int getInt(String key) {
        return AppPrefs.getInt(key, Constants.NO_DATA);
    }
    public static String getString(String key) {
        return AppPrefs.getString(key, null);
    }

    //
    public static void setString(String key, @Nullable String value) {
        if (value == null)
            AppPrefs.edit().remove(key).apply();
        else
            AppPrefs.edit().putString(key, value).apply();
    }
}
