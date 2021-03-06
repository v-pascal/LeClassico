package com.studio.artaban.leclassico.helpers;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.codes.Tables;

/**
 * Created by pascal on 14/08/16.
 * Content resolver loader class
 */
public class QueryLoader {

    // Data keys
    public static final String DATA_KEY_PROJECTION = "projection"; // Query projection (table query reserved)
    public static final String DATA_KEY_SELECTION = "selection"; // Query criteria
    public static final String DATA_KEY_SELECTION_ARGS = "selectionArgs"; // Query criteria arguments
    public static final String DATA_KEY_SORT_ORDER = "sortOrder"; // Query order (table query reserved)

    public static final String DATA_KEY_URI = "uri";
    public static final String DATA_KEY_URI_FILTER = "uriFilter";

    //////
    public interface OnResultListener {

        void onLoadFinished(int id, Cursor cursor);
        void onLoaderReset();
    };
    private final Context mContext; // Activity context
    private final OnResultListener mListener; // Query loader result listener

    public QueryLoader(Context context, OnResultListener listener) {
        Logs.add(Logs.Type.V, "context: " + context + ";listener: " + listener);
        mContext = context;
        mListener = listener;
    }

    //
    private final LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Cursor>() {

        private int mLoaderId; // Query loader ID

        //////
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Logs.add(Logs.Type.V, "id: " + id + ";args: " + args);
            mLoaderId = id;

            Uri queryURI;
            if ((id > 0) && (id <= Tables.ID_LAST) && (!args.containsKey(DATA_KEY_URI))) // Table URI
                queryURI = Uri.parse(DataProvider.CONTENT_URI + Tables.getName((byte) id));
            else if (args.containsKey(DATA_KEY_URI))
                queryURI = args.getParcelable(DATA_KEY_URI);
            else
                throw new IllegalArgumentException("Missing URI");

            // Check to add row ID or query filter into the URI
            if (args.containsKey(DATA_KEY_URI_FILTER)) {
                if (args.get(DATA_KEY_URI_FILTER) instanceof Long)
                    queryURI = ContentUris.withAppendedId(queryURI, args.getLong(DATA_KEY_URI_FILTER));
                else if (args.get(DATA_KEY_URI_FILTER) instanceof String)
                    queryURI = Uri.withAppendedPath(queryURI, Uri.encode(args.getString(DATA_KEY_URI_FILTER)));
                else
                    throw new IllegalArgumentException("Unexpected URI filter type");
            }
            return new CursorLoader(mContext, queryURI,
                    args.getStringArray(DATA_KEY_PROJECTION),
                    args.getString(DATA_KEY_SELECTION, null),
                    args.getStringArray(DATA_KEY_SELECTION_ARGS),
                    args.getString(DATA_KEY_SORT_ORDER, null));
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Logs.add(Logs.Type.V, "data: " + data);

            if (mListener != null)
                mListener.onLoadFinished(mLoaderId, data);
            else
                Logs.add(Logs.Type.W, "No result listener (onLoadFinished)");
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            Logs.add(Logs.Type.V, null);
            try {
                if (mListener != null)
                    mListener.onLoaderReset();
                else
                    Logs.add(Logs.Type.W, "No result listener (onLoaderReset)");

            } catch (IllegalStateException e) {
                Logs.add(Logs.Type.E, "No activity");
            }
        }
    };

    //////
    public void init(FragmentActivity activity, int id, Bundle args) {
        // Initialize a new cursor loader (destroy previous one if any)

        Logs.add(Logs.Type.V, "activity: " + activity + ";id: " + id + ";args: " + args);
        activity.getSupportLoaderManager().destroyLoader(id);
        activity.getSupportLoaderManager().initLoader(id, args, mLoaderCallbacks);
    }
    public void restart(FragmentActivity activity, int id, Bundle args) {
        // Restart a loader with new query parameters

        Logs.add(Logs.Type.V, "activity: " + activity + ";id: " + id + ";args: " + args);
        activity.getSupportLoaderManager().restartLoader(id, args, mLoaderCallbacks);
    }
}
