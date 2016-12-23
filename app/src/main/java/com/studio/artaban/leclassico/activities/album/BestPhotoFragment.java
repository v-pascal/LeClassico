package com.studio.artaban.leclassico.activities.album;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.animations.RequestAnimation;
import com.studio.artaban.leclassico.components.RecyclerAdapter;
import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.IDataTable;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.data.tables.CommentairesTable;
import com.studio.artaban.leclassico.data.tables.PhotosTable;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;
import com.studio.artaban.leclassico.services.DataService;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by pascal on 19/11/16.
 * Fragment to display a photo with comments
 */
public class BestPhotoFragment extends Fragment implements QueryLoader.OnResultListener {






    private View mRootView; // Fragment root view

    private RecyclerView mComList; // Recycler view containing comments list
    private Uri mComUri; // Comments observer URI

    //////
    private final ComRecyclerViewAdapter mComAdapter = new ComRecyclerViewAdapter();
    private class ComRecyclerViewAdapter extends RecyclerAdapter {

        private RequestAnimation mRequestAnim; // Request old management (animation + event)
        public ComRecyclerViewAdapter() {
            super(R.layout.layout_comment_item, R.layout.layout_old_request, COLUMN_INDEX_COMMENT_ID);
        }

        ////// View.OnClickListener ////////////////////////////////////////////////////////////////
        @Override
        public void onClick(View sender) {
            Logs.add(Logs.Type.V, "sender: " + sender);
            switch (sender.getId()) {

                case R.id.image_pseudo: {
                    int pseudoId = (int) sender.getTag(R.id.tag_pseudo_id);
                    Logs.add(Logs.Type.I, "Pseudo #" + pseudoId + " selected");





                    break;
                }
                case R.id.layout_more: {
                    Logs.add(Logs.Type.I, "Old photo comments requested");

                    ////// Request old photo comments to remote DB
                    mQueryLimit += Queries.COMMENTS_OLD_LIMIT;
                    mComAdapter.setRequesting(true);

                    Intent request = new Intent(DataService.REQUEST_OLD_DATA);
                    request.putExtra(DataRequest.EXTRA_DATA_DATE, mQueryDate);
                    getActivity().sendBroadcast(DataService.getIntent(request,
                            Tables.ID_COMMENTAIRES, mComUri));
                    break;
                }
            }
        }

        ////// RecyclerAdapter /////////////////////////////////////////////////////////////////////
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Logs.add(Logs.Type.V, "holder: " + holder + ";position: " + position);

            if (isRequestHolder(holder, position)) { ////// Request

                Logs.add(Logs.Type.I, "Request item (" + isRequesting() + ')');
                if (mRequestAnim == null)
                    mRequestAnim = new RequestAnimation(getContext());

                mRequestAnim.display(getResources(), this, holder.requestView);
                return;
            }
            ////// Comment







            // COLUMN_INDEX_COMMENT_PSEUDO
            ((TextView)holder.rootView.findViewById(R.id.test))
                    .setText(mDataSource.getString(position, COLUMN_INDEX_COMMENT_TEXT));







        }
    }

    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        if (!cursor.moveToFirst())
            return;

        if (id == Queries.MAIN_BEST_PHOTOS) {
            mComCursor = cursor;



            // COLUMN_INDEX_COMMENT_OBJ_ID

            // COLUMN_INDEX_PHOTO_FICHIER
            // COLUMN_INDEX_PHOTO_ALBUM

            ((TextView)mRootView.findViewById(R.id.text_title)).setText("LC0180.jpg");
            ((TextView)mRootView.findViewById(R.id.text_info_album)).setText("Tillate.com");
            ((TextView)mRootView.findViewById(R.id.text_info_provider)).setText("Pascal");
            ((TextView)mRootView.findViewById(R.id.text_info_range)).setText("1/258");
            // image_photo



            refresh(); // Display/Refresh photo comments
        }
    }

    @Override
    public void onLoaderReset() {
        Logs.add(Logs.Type.V, null);
        mComCursor = null;
    }

    //////
    private QueryLoader mListLoader; // Photo comment list query loader
    private Cursor mComCursor; // Photo comments cursor
    private String mComLast; // Last photo comment date received (newest date)

    private short mQueryCount = Constants.NO_DATA; // DB query result count
    private short mQueryLimit = Queries.COMMENTS_LIST_LIMIT; // DB query limit
    private String mQueryDate; // Old query date displayed (visible)

    // Query column indexes
    private static final int COLUMN_INDEX_COMMENT_ID = 0;
    private static final int COLUMN_INDEX_COMMENT_OBJ_ID = 1;
    private static final int COLUMN_INDEX_COMMENT_PSEUDO = 2;
    private static final int COLUMN_INDEX_COMMENT_TEXT = 3;
    private static final int COLUMN_INDEX_COMMENT_DATE = 4;
    private static final int COLUMN_INDEX_PHOTO_ALBUM = 5;
    private static final int COLUMN_INDEX_PHOTO_PSEUDO = 6;
    private static final int COLUMN_INDEX_PHOTO_FICHIER = 7;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final OldReceiver mOldReceiver = new OldReceiver(); // Old data broadcast receiver
    private class OldReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Logs.add(Logs.Type.V, "context: " + context + ";intent: " + intent);
            if ((intent.getAction().equals(DataService.REQUEST_OLD_DATA)) && // Old request
                    (intent.getBooleanExtra(DataService.EXTRA_DATA_RECEIVED, false)) && // Received
                    (((Uri) intent.getParcelableExtra(DataRequest.EXTRA_DATA_URI))
                            .compareTo(mComUri) == 0)) { // Comment URI

                mComAdapter.setRequesting(false);
                if (!intent.getBooleanExtra(DataService.EXTRA_DATA_OLD_FOUND, false)) {
                    if (mQueryCount > (mQueryLimit - Queries.COMMENTS_OLD_LIMIT))
                        refresh(); // No more old remote DB photo comments but existing in local DB
                    else
                        mQueryLimit -= Queries.COMMENTS_OLD_LIMIT;
                }
                //else // DB table update will notify cursor (no need to call refresh)
            }
        }
    }

    private void refresh() { // Display/Refresh photo comment list
        Logs.add(Logs.Type.V, null);
        mComCursor.moveToFirst();

        // Update current display data
        String lastPub = mComCursor.getString(COLUMN_INDEX_COMMENT_DATE);
        short count = (short) mComCursor.getCount();
        if ((mQueryCount != Constants.NO_DATA) && (mComLast.compareTo(lastPub) != 0))
            mQueryLimit += count - mQueryCount; // New entries case (from remote DB)

        mQueryCount = count;
        mComLast = lastPub;

        // Get last visible photo comment date
        int limit = mQueryLimit;
        do {
            mQueryDate = mComCursor.getString(COLUMN_INDEX_COMMENT_DATE);
            if (--limit == 0)
                break; // Only visible item are concerned

        } while (mComCursor.moveToNext());
        mComCursor.moveToFirst();

        //////
        if (mComAdapter.isInitialized()) { // Check if not the initial query
            Logs.add(Logs.Type.I, "Query update");

            ////// Update comments list
            mComAdapter.getDataSource().swap(mComAdapter, mComCursor, mQueryLimit, null);

        } else {
            Logs.add(Logs.Type.I, "Initial query");

            ////// Fill comments list
            mComAdapter.getDataSource().fill(mComCursor, mQueryLimit);
            mComList.scrollToPosition(0);
        }
    }

    ////// Fragment ////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logs.add(Logs.Type.V, "context: " + context);
        mListLoader = new QueryLoader(context, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);

        // Set backgrounds
        mRootView = inflater.inflate(R.layout.fragment_best, container, false);
        try {
            float radius = getResources().getDimensionPixelSize(R.dimen.best_background_radius);
            GradientDrawable background = (GradientDrawable) Drawable.createFromXml(getResources(),
                    getResources().getXml(R.xml.photo_background));
            background.setCornerRadii(new float[]{
                    radius, radius, radius, radius, 0, 0, 0, 0
            });
            mRootView.findViewById(R.id.layout_header).setBackground(background);
            background = (GradientDrawable) Drawable.createFromXml(getResources(),
                    getResources().getXml(R.xml.photo_background));
            background.setCornerRadii(new float[]{
                    0, 0, radius, radius, 0, 0, 0, 0
            });
            mRootView.findViewById(R.id.view_middle).setBackground(background);
            background = (GradientDrawable) Drawable.createFromXml(getResources(),
                    getResources().getXml(R.xml.photo_background));
            background.setCornerRadii(new float[]{
                    0, 0, 0, 0, radius, radius, radius, radius
            });
            mRootView.findViewById(R.id.view_bottom).setBackground(background);

        } catch (XmlPullParserException e) {
            Logs.add(Logs.Type.E, "Failed to add background (parser): " + e.getMessage());
        } catch (IOException e) {
            Logs.add(Logs.Type.E, "Failed to add background (IO): " + e.getMessage());
        }

        // Set URI to observe DB changes
        mComUri = Uris.getUri(Uris.ID_MAIN_BEST_PHOTOS);

        // Set comments list
        LinearLayoutManager linearManager = new LinearLayoutManager(getContext());
        linearManager.setOrientation(LinearLayoutManager.VERTICAL);

        mComList = (RecyclerView) mRootView.findViewById(R.id.recycler_comments);
        mComList.setLayoutManager(linearManager);
        mComList.setItemAnimator(new DefaultItemAnimator());
        mComList.setAdapter(mComAdapter);

        // Initialize comments list (set query loaders)
        Bundle bestData = new Bundle();
        bestData.putParcelable(QueryLoader.DATA_KEY_URI, mComUri);
        bestData.putString(QueryLoader.DATA_KEY_SELECTION,



                "SELECT " +
                        CommentairesTable.TABLE_NAME + '.' + IDataTable.DataField.COLUMN_ID + ',' + // COLUMN_INDEX_COMMENT_ID
                        CommentairesTable.COLUMN_OBJ_ID + ',' + // COLUMN_INDEX_COMMENT_OBJ_ID
                        CommentairesTable.COLUMN_PSEUDO + ',' + // COLUMN_INDEX_COMMENT_PSEUDO
                        CommentairesTable.COLUMN_TEXT + ',' + // COLUMN_INDEX_COMMENT_TEXT
                        CommentairesTable.COLUMN_DATE + ',' + // COLUMN_INDEX_COMMENT_DATE
                        PhotosTable.COLUMN_ALBUM + ',' + // COLUMN_INDEX_PHOTO_ALBUM
                        PhotosTable.COLUMN_PSEUDO + ',' + // COLUMN_INDEX_PHOTO_PSEUDO
                        PhotosTable.COLUMN_FICHIER + // COLUMN_INDEX_PHOTO_FICHIER
                        " FROM " + CommentairesTable.TABLE_NAME +
                        " LEFT JOIN " + PhotosTable.TABLE_NAME + " ON " +
                        CommentairesTable.COLUMN_OBJ_ID + '=' + PhotosTable.COLUMN_FICHIER_ID +
                        " WHERE " +
                        PhotosTable.COLUMN_BEST + "=1 AND " +
                        CommentairesTable.COLUMN_OBJ_TYPE + '=' + CommentairesTable.TYPE_PHOTO + " AND " +
                        CommentairesTable.TABLE_NAME + '.' + Constants.DATA_COLUMN_SYNCHRONIZED + "<>" +
                        DataTable.Synchronized.TO_DELETE.getValue() + " AND " +
                        CommentairesTable.TABLE_NAME + '.' + Constants.DATA_COLUMN_SYNCHRONIZED + "<>" +
                        (DataTable.Synchronized.TO_DELETE.getValue() | DataTable.Synchronized.IN_PROGRESS.getValue()) +
                        " ORDER BY " + CommentairesTable.COLUMN_DATE + " DESC");
        mListLoader.init(getActivity(), Queries.MAIN_BEST_PHOTOS, bestData);

        return mRootView;
    }
}
