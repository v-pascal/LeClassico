package com.studio.artaban.leclassico.helpers;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.tables.AbonnementsTable;
import com.studio.artaban.leclassico.data.tables.ActualitesTable;
import com.studio.artaban.leclassico.data.tables.AlbumsTable;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.data.tables.CommentairesTable;
import com.studio.artaban.leclassico.data.tables.EvenementsTable;
import com.studio.artaban.leclassico.data.tables.MessagerieTable;
import com.studio.artaban.leclassico.data.tables.MusicTable;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.data.tables.PhotosTable;
import com.studio.artaban.leclassico.data.tables.PresentsTable;
import com.studio.artaban.leclassico.data.tables.VotesTable;

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

    public static final String DATA_KEY_URI_SINGLE = "single";
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
            String contentUri = DataProvider.CONTENT_URI;
            mLoaderId = id;

            switch ((byte)id) {

                case Tables.ID_CAMARADES: contentUri += CamaradesTable.TABLE_NAME; break;
                case Tables.ID_ABONNEMENTS: contentUri += AbonnementsTable.TABLE_NAME; break;
                case Tables.ID_ACTUALITES: contentUri += ActualitesTable.TABLE_NAME; break;
                case Tables.ID_ALBUMS: contentUri += AlbumsTable.TABLE_NAME; break;
                case Tables.ID_COMMENTAIRES: contentUri += CommentairesTable.TABLE_NAME; break;
                case Tables.ID_EVENEMENTS: contentUri += EvenementsTable.TABLE_NAME; break;
                case Tables.ID_MESSAGERIE: contentUri += MessagerieTable.TABLE_NAME; break;
                case Tables.ID_MUSIC: contentUri += MusicTable.TABLE_NAME; break;
                case Tables.ID_PHOTOS: contentUri += PhotosTable.TABLE_NAME; break;
                case Tables.ID_PRESENTS: contentUri += PresentsTable.TABLE_NAME; break;
                case Tables.ID_VOTES: contentUri += VotesTable.TABLE_NAME; break;
                case Tables.ID_NOTIFICATIONS: contentUri += NotificationsTable.TABLE_NAME; break;
                default:
                    throw new IllegalArgumentException("Unexpected loader ID");
            }

            // Check single row query
            if (args.getBoolean(DATA_KEY_URI_SINGLE, false))
                contentUri += '/' + args.getLong(DATA_KEY_ROW_ID, Constants.NO_DATA);

            return new CursorLoader(mContext, Uri.parse(contentUri),
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
    // Initialize or resend loader with same query parameters

        Logs.add(Logs.Type.V, "activity: " + activity + ";id: " + id + ";args: " + args);
        activity.getLoaderManager().initLoader(id, args, mLoaderCallbacks);
    }
    public void restart(Activity activity, int id, Bundle args) {
    // Restart a loader with new query parameters
    // NB: Do not send several request with same ID otherwise only one 'onLoadFinished' will be called

        Logs.add(Logs.Type.V, "activity: " + activity + ";id: " + id + ";args: " + args);
        activity.getLoaderManager().restartLoader(id, args, mLoaderCallbacks);
    }
}
