package com.studio.artaban.leclassico.activities.main;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.tables.ActualitesTable;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.data.tables.CommentairesTable;
import com.studio.artaban.leclassico.data.tables.MessagerieTable;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.data.tables.PhotosTable;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;

/**
 * Created by pascal on 05/09/16.
 * Notifications fragment class (MainActivity)
 */
public class NotificationsFragment extends MainFragment implements QueryLoader.OnResultListener {

    private RecyclerView mNotifyList; // Recycler view containing notification list
    private Cursor mNotifyData; // Cursor containing notification data

    private class NotifyRecyclerViewAdapter extends RecyclerView.Adapter<NotifyRecyclerViewAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {





            return null;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {






        }

        @Override
        public int getItemCount() {
            return mNotifyData.getCount();
        }

        //////
        public class ViewHolder extends RecyclerView.ViewHolder {

            /*
            public final ImageView mThumbnailView; // Thumbnail image
            public final TextView mTitleView; // Title (duration)
            public final TextView mDateView; // Date & time
            */

            public ViewHolder(View view) {
                super(view);

                /*
                mThumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
                mTitleView = (TextView) view.findViewById(R.id.title);
                mDateView = (TextView) view.findViewById(R.id.date);
                */
            }
        }
    }

    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {

        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        if (id == Queries.MAIN_NOTIFICATIONS) {

            mNotifyData = cursor;
            mNotifyList.setAdapter(new NotifyRecyclerViewAdapter());
        }
    }

    @Override
    public void onLoaderReset() {

    }

    //////
    private QueryLoader mNotifyLoader; // User notification query loader

    // Query column indexes
    private static final int COLUMN_INDEX_OBJECT_TYPE = 0;
    private static final int COLUMN_INDEX_OBJECT_ID = 1;
    private static final int COLUMN_INDEX_OBJECT_DATE = 2;
    private static final int COLUMN_INDEX_OBJECT_FROM = 3;
    private static final int COLUMN_INDEX_DATE = 4;
    private static final int COLUMN_INDEX_PSEUDO = 5;
    private static final int COLUMN_INDEX_SEX = 6;
    private static final int COLUMN_INDEX_PROFILE = 7;
    private static final int COLUMN_INDEX_ALBUM = 8;
    private static final int COLUMN_INDEX_PUB_TEXT = 9;
    private static final int COLUMN_INDEX_MSG_TEXT = 10;
    private static final int COLUMN_INDEX_MSG_DATE = 11;
    private static final int COLUMN_INDEX_MSG_TIME = 12;
    private static final int COLUMN_INDEX_COM_TEXT = 13;

    ////// MainFragment ////////////////////////////////////////////////////////////////////////////
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logs.add(Logs.Type.V, "context: " + context);
        mNotifyLoader = new QueryLoader(context, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);
        View rootView = inflater.inflate(R.layout.layout_notifications, container, false);
        rootView.setTag(Constants.MAIN_SECTION_NOTIFICATIONS);
        mNotifyList = (RecyclerView)rootView.findViewById(R.id.list_notification);

        // Load notification data (using query loader)
        Bundle queryData = new Bundle();
        queryData.putString(QueryLoader.DATA_KEY_SELECTION,
                "SELECT " +
                        NotificationsTable.COLUMN_OBJECT_TYPE + "," + // COLUMN_INDEX_OBJECT_TYPE
                        NotificationsTable.COLUMN_OBJECT_ID + "," + // COLUMN_INDEX_OBJECT_ID
                        NotificationsTable.COLUMN_OBJECT_DATE + "," + // COLUMN_INDEX_OBJECT_DATE
                        NotificationsTable.COLUMN_OBJECT_FROM + "," + // COLUMN_INDEX_OBJECT_FROM
                        NotificationsTable.COLUMN_DATE + "," + // COLUMN_INDEX_DATE
                        CamaradesTable.COLUMN_PSEUDO + "," + // COLUMN_INDEX_PSEUDO
                        CamaradesTable.COLUMN_SEXE + "," + // COLUMN_INDEX_SEX
                        CamaradesTable.COLUMN_PROFILE + "," + // COLUMN_INDEX_PROFILE
                        PhotosTable.COLUMN_ALBUM + "," + // COLUMN_INDEX_ALBUM
                        ActualitesTable.COLUMN_TEXT + "," + // COLUMN_INDEX_PUB_TEXT
                        MessagerieTable.COLUMN_MESSAGE + "," + // COLUMN_INDEX_MSG_TEXT
                        MessagerieTable.COLUMN_DATE + "," + // COLUMN_INDEX_MSG_DATE
                        MessagerieTable.COLUMN_TIME + "," + // COLUMN_INDEX_MSG_TIME
                        CommentairesTable.COLUMN_TEXT + // COLUMN_INDEX_COM_TEXT

                        " FROM " + NotificationsTable.TABLE_NAME +
                        " LEFT JOIN " + CamaradesTable.TABLE_NAME + " ON " +
                        NotificationsTable.COLUMN_OBJECT_FROM + "=" + CamaradesTable.COLUMN_PSEUDO +
                        " LEFT JOIN " + PhotosTable.TABLE_NAME + " ON " +
                        NotificationsTable.COLUMN_OBJECT_ID + "=" + PhotosTable.COLUMN_FICHIER_ID + " AND " +
                        NotificationsTable.COLUMN_OBJECT_TYPE + "='" + NotificationsTable.TYPE_SHARED + "' AND " +
                        NotificationsTable.COLUMN_OBJECT_DATE + " IS NULL" +
                        " LEFT JOIN " + ActualitesTable.TABLE_NAME + " ON " +
                        NotificationsTable.COLUMN_OBJECT_ID + "=" + ActualitesTable.COLUMN_ACTU_ID + " AND " +
                        NotificationsTable.COLUMN_OBJECT_TYPE + "='" + NotificationsTable.TYPE_WALL + "'" +
                        " LEFT JOIN " + MessagerieTable.TABLE_NAME + " ON " +
                        NotificationsTable.COLUMN_OBJECT_DATE + "=" +
                        MessagerieTable.COLUMN_DATE + "||' '||" + MessagerieTable.COLUMN_TIME + " AND " +
                        NotificationsTable.COLUMN_OBJECT_TYPE + "='" + NotificationsTable.TYPE_MAIL + "' AND " +
                        NotificationsTable.COLUMN_OBJECT_ID + " IS NULL" +
                        " LEFT JOIN " + CommentairesTable.TABLE_NAME + " ON " +
                        NotificationsTable.COLUMN_OBJECT_ID + "=" + CommentairesTable.COLUMN_OBJ_ID + " AND " +
                        NotificationsTable.COLUMN_OBJECT_TYPE + "=" + CommentairesTable.COLUMN_OBJ_TYPE + " AND " +
                        NotificationsTable.COLUMN_OBJECT_DATE + "=" + CommentairesTable.COLUMN_DATE + " AND " +
                        NotificationsTable.COLUMN_OBJECT_FROM + "=" + CommentairesTable.COLUMN_PSEUDO +
                        " WHERE " + NotificationsTable.COLUMN_PSEUDO + "='" +
                        getActivity().getIntent().getStringExtra(MainActivity.EXTRA_DATA_KEY_PSEUDO) +
                        "' ORDER BY " + NotificationsTable.COLUMN_DATE + " DESC" +
                        " LIMIT " + Constants.MAIN_QUERY_LIMIT_NOTIFICATIONS);
        mNotifyLoader.restart(getActivity(), Queries.MAIN_NOTIFICATIONS, queryData);

        // Set shortcut data (default)
        SpannableStringBuilder data = new SpannableStringBuilder(getString(R.string.no_notification));
        mListener.onSetMessage(Constants.MAIN_SECTION_NOTIFICATIONS, data);
        data = new SpannableStringBuilder(getString(R.string.no_notification_info));
        mListener.onSetInfo(Constants.MAIN_SECTION_NOTIFICATIONS, data);

        return rootView;
    }
}
