package com.studio.artaban.leclassico.data;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
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
    public void register(ContentResolver resolver, Cursor cursor, String table, int columnId) {

        Logs.add(Logs.Type.V, "resolver: " + resolver + ";cursor: " + cursor);
        if (cursor.moveToFirst()) {
            do {
                resolver.notifyChange(
                        Uri.parse(DataProvider.CONTENT_URI + table + '/' + cursor.getInt(columnId)), this);

            } while (cursor.moveToNext());
        }
        cursor.moveToFirst();
    }
    public void unregister(ContentResolver resolver) {
        Logs.add(Logs.Type.V, "resolver: " + resolver);
        resolver.unregisterContentObserver(this);
    }

    //
    public DataObserver(Handler handler, OnContentListener listener) {
        super(handler);
        mListener = listener;
    }

    ////// ContentObserver /////////////////////////////////////////////////////////////////////////
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        mListener.onChange(selfChange, uri);
    }
}
