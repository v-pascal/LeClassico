package com.studio.artaban.leclassico.data.codes;

import android.app.Activity;

/**
 * Created by pascal on 11/08/16.
 * Activities request & result codes
 */
public final class Requests {

    private static final int REQUEST_EVENT_DISPLAY_2_MAIN = 1; // Event display to main activity request

    //////
    public static final class EVENT_DISPLAY_2_MAIN {
        public static final int CODE = REQUEST_EVENT_DISPLAY_2_MAIN;

        // Results
        public static final int RESULT_ID = Activity.RESULT_FIRST_USER;

        // Data keys
        public static final String DATA_KEY_ID = "id";
    };
}
