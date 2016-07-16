package com.studio.artaban.leclassico;

import android.app.Application;

/**
 * Created by pascal on 15/07/16.
 * Application class
 */
public class LeClassicoApplication extends Application {

    private static LeClassicoApplication ourInstance;
    public static LeClassicoApplication getInstance() { return ourInstance; }

    //////
    @Override
    public void onCreate() {
        super.onCreate();
        ourInstance = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
