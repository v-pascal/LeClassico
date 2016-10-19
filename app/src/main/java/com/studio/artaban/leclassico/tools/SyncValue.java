package com.studio.artaban.leclassico.tools;

/**
 * Created by pascal on 23/08/16.
 * Synchronization value class
 * _ Used to manage synchronization properly and without resource need
 * _ Used to manage primitive type to be able to update its value when defined as final
 */
public final class SyncValue<Type> {

    private Type mValue; // Value
    public SyncValue(Type value) {
        mValue = value;
    }

    ////// Accessors
    public Type get() {
        return mValue;
    }
    public void set(Type value) {
        mValue = value;
    }
}
