package com.studio.artaban.leclassico.data.codes;

import android.app.Activity;

/**
 * Created by pascal on 11/08/16.
 * Activities request & result codes
 */
public final class Requests {

    private static final int REQUEST_PICK_MEMBER = 1; // Pick member activity request

    //////
    public static final class PICK_MEMBER {
        public static final int CODE = REQUEST_PICK_MEMBER;

        // Results
        //public static final int RESULT_ID = Activity.RESULT_FIRST_USER;
        // NB: Not needed coz only RESULT_OK will be used

        // Extra data keys
        public static final String EXTRA_DATA_ID = "id";
    };
}
