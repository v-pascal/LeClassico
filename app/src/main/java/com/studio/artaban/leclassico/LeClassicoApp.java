package com.studio.artaban.leclassico;

import android.app.Application;

import java.util.Random;

/**
 * Created by pascal on 15/07/16.
 * Application class
 */
public class LeClassicoApp extends Application {

    private static LeClassicoApp ourInstance;
    public static LeClassicoApp getInstance() {
        return ourInstance;
    }

    //
    private final Random mRandom = new Random();
    public static Random getRandom() {
        return ourInstance.mRandom;
    }

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
