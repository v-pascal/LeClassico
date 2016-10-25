package com.studio.artaban.leclassico.helpers;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.codes.Tables;

/**
 * Created by pascal on 14/08/16.
 * Content resolver loader class
 */
public class QueryLoader {

    // Data keys
    public static final String DATA_KEY_PROJECTION = "projection";
    public static final String DATA_KEY_SELECTION = "selection";
    public static final String DATA_KEY_SELECTION_ARGS = "selectionArgs";
    public static final String DATA_KEY_SORT_ORDER = "sortOrder";

    public static final String DATA_KEY_URI = "uri";
    public static final String DATA_KEY_ROW_ID = "rowId";

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
    private final LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        private int mLoaderId; // Query loader ID

        //////
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Logs.add(Logs.Type.V, "id: " + id + ";args: " + args);
            mLoaderId = id;

            Uri queryURI;
            if ((id > 0) && (id <= Tables.ID_LAST) && (!args.containsKey(DATA_KEY_URI))) // Table query
                queryURI = Uri.parse(DataProvider.CONTENT_URI + Tables.getName((byte) id));
            else if (args.containsKey(DATA_KEY_URI)) // Particular query (table or raw query)
                queryURI = args.getParcelable(DATA_KEY_URI);
            else
                throw new IllegalArgumentException("Missing URI (not a table query)");

            // Check to add row or query ID into the URI
            if (args.containsKey(DATA_KEY_ROW_ID))
                queryURI = ContentUris.withAppendedId(queryURI, args.getLong(DATA_KEY_ROW_ID));

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
            if (mListener != null)
                mListener.onLoaderReset();
            else
                Logs.add(Logs.Type.W, "No result listener (onLoaderReset)");
        }
    };

    //////
    public void init(Activity activity, int id, Bundle args) {
        // Initialize a new cursor loader (destroy previous one if any)

        Logs.add(Logs.Type.V, "activity: " + activity + ";id: " + id + ";args: " + args);
        activity.getLoaderManager().destroyLoader(id);
        activity.getLoaderManager().initLoader(id, args, mLoaderCallbacks);
    }
    public void restart(Activity activity, int id, Bundle args) {
        // Restart a loader with new query parameters

        Logs.add(Logs.Type.V, "activity: " + activity + ";id: " + id + ";args: " + args);
        activity.getLoaderManager().restartLoader(id, args, mLoaderCallbacks);
    }
}
