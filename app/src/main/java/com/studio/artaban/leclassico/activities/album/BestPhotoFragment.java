package com.studio.artaban.leclassico.activities.album;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.studio.artaban.leclassico.LeClassicoApp;
import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.animations.RequestAnimation;
import com.studio.artaban.leclassico.components.RecyclerAdapter;
import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.connection.requests.CommentairesRequest;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.IDataTable;
import com.studio.artaban.leclassico.data.codes.Preferences;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.data.tables.CommentairesTable;
import com.studio.artaban.leclassico.data.tables.PhotosTable;
import com.studio.artaban.leclassico.helpers.Glider;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;
import com.studio.artaban.leclassico.helpers.Storage;
import com.studio.artaban.leclassico.services.DataService;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pascal on 19/11/16.
 * Fragment to display a photo with comments
 */
public class BestPhotoFragment extends Fragment implements
        QueryLoader.OnResultListener, RecyclerAdapter.DataView.OnCriteriaListener, View.OnClickListener {

    private RecyclerView mComList; // Recycler view containing comments list
    private Uri mComUri; // Comments observer URI

    private void registerDataService() { // Register best photo comments data service
        Logs.add(Logs.Type.V, null);

        Intent intentService = DataService.getIntent(true, Tables.ID_COMMENTAIRES, mComUri);
        intentService.putExtra(CommentairesRequest.EXTRA_DATA_OBJECT_IDS, String.valueOf(mBestId));
        intentService.putExtra(CommentairesRequest.EXTRA_DATA_OBJECT_TYPE, CommentairesTable.TYPE_PHOTO);
        getContext().sendBroadcast(intentService);
    }

    //////
    private final ComRecyclerViewAdapter mComAdapter = new ComRecyclerViewAdapter();
    private class ComRecyclerViewAdapter extends RecyclerAdapter {

        private RequestAnimation mRequestAnim; // Request old management (animation + event)
        public ComRecyclerViewAdapter() {
            super(R.layout.layout_best_comment_item, R.layout.layout_old_request_best, false, COLUMN_INDEX_COMMENT_ID);
        }

        ////// View.OnClickListener ////////////////////////////////////////////////////////////////
        @Override
        public void onClick(View sender) {
            Logs.add(Logs.Type.V, "sender: " + sender);
            switch (sender.getId()) {

                case R.id.image_remove: { // Remove comment
                    int commentId = (int) sender.getTag(R.id.tag_comment_id);
                    Logs.add(Logs.Type.I, "Remove comment #" + commentId);





                    break;
                }
                case R.id.layout_more: { // Old data requested
                    Logs.add(Logs.Type.I, "Old photo comments requested");

                    ////// Request old photo comments to remote DB
                    mQueryLimit += Queries.COMMENTS_OLD_LIMIT;
                    mComAdapter.setRequesting(RequestFlag.IN_PROGRESS);

                    Intent request = new Intent(DataService.REQUEST_OLD_DATA);
                    request.putExtra(DataRequest.EXTRA_DATA_DATE, mQueryDate);
                    request.putExtra(CommentairesRequest.EXTRA_DATA_OBJECT_IDS, String.valueOf(mBestId));
                    request.putExtra(CommentairesRequest.EXTRA_DATA_OBJECT_TYPE, CommentairesTable.TYPE_PHOTO);
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
                    mRequestAnim = new RequestAnimation(getContext(), false);

                mRequestAnim.display(getResources(), this, holder.requestView);
                return;
            }
            position = getAscPosition(position);

            ////// Comment
            String pseudo = mDataSource.getString(position, COLUMN_INDEX_COMMENT_PSEUDO);
            TextView comment = (TextView)holder.rootView.findViewById(R.id.text_comment);
            comment.setText(pseudo + ": " + mDataSource.getString(position, COLUMN_INDEX_COMMENT_TEXT));

            final int item = position;
            Linkify.addLinks(comment, Pattern.compile('^' + pseudo + ':', 0), Constants.DATA_CONTENT_SCHEME,
                    null, new Linkify.TransformFilter() {
                        @Override
                        public String transformUrl(Matcher match, String url) {

                            // content://com.studio.artaban.provider.leclassico/User/#/Profile
                            return Uris.getUri(Uris.ID_USER_PROFILE, String.valueOf(mDataSource.getInt(item,
                                    COLUMN_INDEX_COMMENT_PSEUDO_ID))).toString();
                        }
                    });

            // Check remove command visibility (only available if connected user == comment owner)
            ImageView remove = (ImageView) holder.rootView.findViewById(R.id.image_remove);
            if (mDataSource.getInt(position, COLUMN_INDEX_COMMENT_PSEUDO_ID) !=
                    getActivity().getIntent().getIntExtra(Login.EXTRA_DATA_PSEUDO_ID, Constants.NO_DATA)) {

                remove.setVisibility(View.GONE);
                return; // No need to add remove event
            }
            ////// Events
            remove.setVisibility(View.VISIBLE);
            remove.setTag(R.id.tag_comment_id, mDataSource.getInt(position, COLUMN_INDEX_COMMENT_ID));
            remove.setOnClickListener(this);
        }
    }
    private View mRootView; // Fragment root view

    private int mBestId; // Best photo ID displayed
    private String mBestFile; // Best photo file displayed

    ////// View.OnClickListener ////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View sender) { // Display best photo in full screen
        Logs.add(Logs.Type.V, "sender: " + sender);

        ////// Start full screen photo activity
        Intent fullScreen = new Intent(getContext(), FullPhotoActivity.class);
        fullScreen.putExtra(FullPhotoActivity.EXTRA_DATA_TITLE, mBestFile);
        fullScreen.putExtra(FullPhotoActivity.EXTRA_DATA_NAME, mBestFile);
        Login.copyExtraData(getActivity().getIntent(), fullScreen);

        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation(getActivity(), sender, mBestFile);
        startActivity(fullScreen, options.toBundle());
    }

    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        if (!cursor.moveToFirst())
            return;

        if (id == Queries.MAIN_BEST_PHOTOS) {
            mComCursor = cursor;

            // Get best photo IDs list
            ArrayList<Integer> ids = new ArrayList<>();
            do {
                if (!ids.contains(cursor.getInt(COLUMN_INDEX_PHOTO_ID)))
                    ids.add(cursor.getInt(COLUMN_INDEX_PHOTO_ID));

            } while (cursor.moveToNext());
            cursor.moveToFirst();

            // Select best photo to display (random with online criteria)
            if ((Internet.isConnected()) || (mBestId == Constants.NO_DATA))
                mBestId = ids.get(LeClassicoApp.getRandom().nextInt(ids.size()));

            do {
                if (cursor.getInt(COLUMN_INDEX_PHOTO_ID) == mBestId) {

                    mBestFile = cursor.getString(COLUMN_INDEX_PHOTO_FICHIER);
                    ((TextView)mRootView.findViewById(R.id.text_title)).setText(mBestFile);
                    ((TextView)mRootView.findViewById(R.id.text_info_album))
                            .setText(cursor.getString(COLUMN_INDEX_PHOTO_ALBUM));
                    ((TextView)mRootView.findViewById(R.id.text_info_provider))
                            .setText(cursor.getString(COLUMN_INDEX_PHOTO_PSEUDO));
                    ((TextView)mRootView.findViewById(R.id.text_info_range))
                            .setText(cursor.getString(COLUMN_INDEX_PHOTO_RANGE));

                    Glider.with(getContext()).placeholder(R.drawable.no_photo)
                            .load(Storage.FOLDER_PHOTOS + File.separator + mBestFile,
                                    Constants.APP_URL_PHOTOS + '/' + mBestFile)
                            .into((ImageView) mRootView.findViewById(R.id.image_photo), new Glider.OnLoadListener() {

                                @Override
                                public void onLoadFailed(ImageView imageView) {
                                    Logs.add(Logs.Type.V, "imageView: " + imageView);
                                    try {
                                        imageView.setOnClickListener(null);
                                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.no_photo));
                                        // NB: Done here instead of using placeholder coz the image size setting (width
                                        //     & height size defined in layouts hierarchy) causes to display a wrong
                                        //     image scale when succeeded (equal default 'no_photo' image size).

                                    } catch (IllegalStateException e) {
                                        Logs.add(Logs.Type.E, "Activity not attached");
                                    }
                                }

                                @Override
                                public boolean onSetResource(Bitmap resource, ImageView imageView) {
                                    Logs.add(Logs.Type.V, "resource: " + resource + ";imageView: " + imageView);

                                    // Store persistent data (if not already done)
                                    SharedPreferences prefs =
                                            getContext().getSharedPreferences(Constants.APP_PREFERENCE, 0);
                                    if (!prefs.contains(Preferences.MAIN_BEST_PHOTO))
                                        prefs.edit().putInt(Preferences.MAIN_BEST_PHOTO, mBestId).apply();

                                    imageView.setTransitionName(mBestFile);
                                    imageView.setOnClickListener(BestPhotoFragment.this);
                                    return false;
                                }
                            });
                    break;
                }

            } while (cursor.moveToNext());
            cursor.moveToFirst();

            // Register best photo comments data service
            getContext().sendBroadcast(DataService.getIntent(false, Tables.ID_COMMENTAIRES, mComUri));
            registerDataService();
            // NB: Remove previous best photo comments registration B4 (if any)

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

    private short mQueryCount; // DB query result count
    private short mQueryLimit = Queries.COMMENTS_LIST_LIMIT; // DB query limit
    private String mQueryDate; // Old query date displayed (visible)

    // Query column indexes
    private static final int COLUMN_INDEX_COMMENT_ID = 0;
    private static final int COLUMN_INDEX_COMMENT_PSEUDO = 1;
    private static final int COLUMN_INDEX_COMMENT_TEXT = 2;
    private static final int COLUMN_INDEX_COMMENT_DATE = 3;
    private static final int COLUMN_INDEX_COMMENT_PSEUDO_ID = 4;
    private static final int COLUMN_INDEX_PHOTO_ID = 5;
    private static final int COLUMN_INDEX_PHOTO_ALBUM = 6;
    private static final int COLUMN_INDEX_PHOTO_PSEUDO = 7;
    private static final int COLUMN_INDEX_PHOTO_FICHIER = 8;
    private static final int COLUMN_INDEX_PHOTO_RANGE = 9;

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

                boolean localOld = false;
                DataRequest.Result result =
                        (DataRequest.Result) intent.getSerializableExtra(DataService.EXTRA_DATA_REQUEST_RESULT);
                if (result != DataRequest.Result.FOUND) {
                    if (mQueryCount > (mQueryLimit - Queries.COMMENTS_OLD_LIMIT)) {
                        refresh(); // No more old remote DB photo comments but existing in local DB
                        localOld = true;
                    }
                    else
                        mQueryLimit -= Queries.COMMENTS_OLD_LIMIT;
                }
                //else // Not found or No more remote DB entries
                // NB: When old entries are found DB update will notify cursor (no need to refresh)

                mComAdapter.setRequesting(((result == DataRequest.Result.NO_MORE) && (!localOld))?
                        RecyclerAdapter.RequestFlag.HIDDEN : RecyclerAdapter.RequestFlag.DISPLAYED);
            }
        }
    }

    ////// OnCriteriaListener //////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCheckEntry(Cursor cursor) { // Return if current comment concerned the best photo
        return ((!cursor.isNull(COLUMN_INDEX_COMMENT_ID)) &&
                (cursor.getInt(COLUMN_INDEX_PHOTO_ID) == mBestId));
    }

    private void refresh() { // Display/Refresh photo comment list
        Logs.add(Logs.Type.V, null);
        mComCursor.moveToFirst();

        // Update current display data
        String lastCom = null;
        short count = 0;
        do {
            if (onCheckEntry(mComCursor)) {
                if (lastCom == null)
                    lastCom = mComCursor.getString(COLUMN_INDEX_COMMENT_DATE);
                ++count;
            }

        } while (mComCursor.moveToNext());
        mComCursor.moveToFirst();

        if ((mComLast != null) && (lastCom != null) && (mComLast.compareTo(lastCom) != 0))
            mQueryLimit += count - mQueryCount; // New entries case (from remote DB)

        mQueryCount = count;
        mComLast = lastCom;

        // Get last visible photo comment date
        int limit = mQueryLimit;
        do {
            if (onCheckEntry(mComCursor)) {
                mQueryDate = mComCursor.getString(COLUMN_INDEX_COMMENT_DATE);
                if (--limit == 0)
                    break; // Only visible item are concerned
            }

        } while (mComCursor.moveToNext());
        mComCursor.moveToFirst();

        //////
        if (mComAdapter.isInitialized()) { // Check if not the initial query
            Logs.add(Logs.Type.I, "Query update");

            ////// Update comments list
            mComAdapter.getDataSource().swap(mComAdapter, mComCursor, mQueryLimit, this, null);

        } else {
            Logs.add(Logs.Type.I, "Initial query");

            ////// Fill comments list
            mComAdapter.getDataSource().fill(mComCursor, mQueryLimit, this);
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

        // Restore data
        SharedPreferences settings = getContext().getSharedPreferences(Constants.APP_PREFERENCE, 0);
        mBestId = settings.getInt(Preferences.MAIN_BEST_PHOTO, Constants.NO_DATA);

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
                        CommentairesTable.COLUMN_PSEUDO + ',' + // COLUMN_INDEX_COMMENT_PSEUDO
                        CommentairesTable.COLUMN_TEXT + ',' + // COLUMN_INDEX_COMMENT_TEXT
                        CommentairesTable.COLUMN_DATE + ',' + // COLUMN_INDEX_COMMENT_DATE
                        CamaradesTable.TABLE_NAME + '.' + IDataTable.DataField.COLUMN_ID + ',' + // COLUMN_INDEX_COMMENT_PSEUDO_ID
                        PhotosTable.COLUMN_FICHIER_ID + ',' + // COLUMN_INDEX_PHOTO_ID
                        PhotosTable.COLUMN_ALBUM + ',' + // COLUMN_INDEX_PHOTO_ALBUM
                        PhotosTable.COLUMN_PSEUDO + ',' + // COLUMN_INDEX_PHOTO_PSEUDO
                        PhotosTable.COLUMN_FICHIER + ',' + // COLUMN_INDEX_PHOTO_FICHIER
                        PhotosTable.COLUMN_RANGE + // COLUMN_INDEX_PHOTO_RANGE
                        " FROM " + PhotosTable.TABLE_NAME +
                        " LEFT JOIN " + CommentairesTable.TABLE_NAME + " ON " +
                        CommentairesTable.COLUMN_OBJ_TYPE + "='" + CommentairesTable.TYPE_PHOTO + "' AND " +
                        CommentairesTable.COLUMN_OBJ_ID + '=' + PhotosTable.COLUMN_FICHIER_ID + " AND " +
                        DataTable.getNotDeletedCriteria(CommentairesTable.TABLE_NAME) +
                        " LEFT JOIN " + CamaradesTable.TABLE_NAME + " ON " +
                        CamaradesTable.COLUMN_PSEUDO + '=' + CommentairesTable.COLUMN_PSEUDO +
                        " WHERE " +
                        PhotosTable.COLUMN_BEST + "=1" +
                        " ORDER BY " + CommentairesTable.COLUMN_DATE + " DESC");
        mListLoader.init(getActivity(), Queries.MAIN_BEST_PHOTOS, bestData);

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Logs.add(Logs.Type.V, null);

        // Register old request receiver & comments data service
        getContext().registerReceiver(mOldReceiver, new IntentFilter(DataService.REQUEST_OLD_DATA));
        if (mBestId != Constants.NO_DATA)
            registerDataService();
    }

    @Override
    public void onPause() {
        super.onPause();
        Logs.add(Logs.Type.V, null);

        // Unregister old request receiver & comments data service
        getContext().unregisterReceiver(mOldReceiver);
        getContext().sendBroadcast(DataService.getIntent(false, Tables.ID_COMMENTAIRES, mComUri));
    }
}
