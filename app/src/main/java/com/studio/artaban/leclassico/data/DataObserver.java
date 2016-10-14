package com.studio.artaban.leclassico.data;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 27/09/16.
 * Data content observer class
 */
public class DataObserver extends ContentObserver {

    public interface OnContentListener {
        void onChange(boolean selfChange, Uri uri);
    }
    private OnContentListener mListener; // Registered content listener

    //////
    public void register(ContentResolver resolver, String path) {
        Logs.add(Logs.Type.V, "resolver: " + resolver + ";path: " + path);
        resolver.registerContentObserver(Uri.parse(DataProvider.CONTENT_URI + path), true, this);
    }
    public void unregister(ContentResolver resolver) {
        Logs.add(Logs.Type.V, "resolver: " + resolver);
        resolver.unregisterContentObserver(this);
    }

    //
    public DataObserver(Handler handler, OnContentListener listener) {
        super(handler);
        Logs.add(Logs.Type.V, "handler: " + handler + ";listener: " + listener);
        mListener = listener;
    }

    ////// ContentObserver /////////////////////////////////////////////////////////////////////////
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        //Logs.add(Logs.Type.V, "selfChange: " + selfChange + ";listener: " + uri);
        mListener.onChange(selfChange, uri);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }
}
