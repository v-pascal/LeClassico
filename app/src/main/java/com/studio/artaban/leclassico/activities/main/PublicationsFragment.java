package com.studio.artaban.leclassico.activities.main;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.animations.RecyclerItemAnimator;
import com.studio.artaban.leclassico.components.RecyclerAdapter;
import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.connection.DataService;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;
import com.studio.artaban.leclassico.tools.Tools;

/**
 * Created by pascal on 05/09/16.
 * Publications fragment class (MainActivity)
 */
public class PublicationsFragment extends MainFragment implements QueryLoader.OnResultListener {

    private RecyclerView mPubList; // Recycler view containing publication list
    private PubRecyclerViewAdapter mPubAdapter; // Recycler view adapter (with cursor management)
    private Uri mPubUri; // Publications observer URI

    //////
    private class PubRecyclerViewAdapter extends RecyclerAdapter implements View.OnClickListener {
        public PubRecyclerViewAdapter() {
            super(R.layout.layout_publication_item, R.layout.layout_old_request, COLUMN_INDEX_PUB_ID);
        }

        ////// View.OnClickListener ////////////////////////////////////////////////////////////////
        @Override
        public void onClick(View sender) {
            Logs.add(Logs.Type.V, "sender: " + sender);
            switch (sender.getId()) {

                case R.id.image_pseudo: {
                    int position = (int)sender.getTag(R.id.tag_position);
                    Logs.add(Logs.Type.I, "Pseudo #" + position + " selected");





                    break;
                }
                case R.id.layout_more: {
                    Logs.add(Logs.Type.I, "Old notifications requested");
                    mQueryLimit += Queries.PUBLICATIONS_OLD_LIMIT;

                    int requestCount = mQueryLimit - mQueryCount;
                    if (requestCount <= 0)
                        refresh(); // Refresh publication list (with new limitation)

                    else {
                        Logs.add(Logs.Type.I, "Request old notifications to remote DB");
                        mPubAdapter.setRequesting(true);

                        ////// Request old publications to remote DB
                        getActivity().sendBroadcast(DataService
                                .getIntent(new Intent(DataService.REQUEST_OLD_DATA),
                                        Tables.ID_ACTUALITES, mPubUri));
                    }
                    break;
                }
            }
        }

        // Requesting old entries animations
        private AnimatorSet mRequestAnim1;
        private AnimatorSet mRequestAnim2;
        private AnimatorSet mRequestAnim3;

        ////// RecyclerAdapter /////////////////////////////////////////////////////////////////////
        @Override
        public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
            Logs.add(Logs.Type.V, "holder: " + holder + ";position: " + position);
            if (isRequestHolder(holder, position)) { ////// Request

                View request = holder.requestView.findViewById(R.id.layout_more);
                if (!isRequesting()) {
                    request.setBackground(getResources().getDrawable(R.drawable.select_more_background));
                    request.setOnClickListener(this);

                    // Stop requesting old publications animation
                    if (mRequestAnim1 != null) mRequestAnim1.cancel();
                    if (mRequestAnim2 != null) mRequestAnim2.cancel();
                    if (mRequestAnim3 != null) mRequestAnim3.cancel();

                } else {
                    request.setBackground(null);

                    // Start requesting old publications animation
                    mRequestAnim1 = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(),
                            R.animator.request_old);
                    mRequestAnim1.setTarget(holder.requestView.findViewById(R.id.image_1));
                    mRequestAnim1.start();

                    long delay = getResources().getInteger(R.integer.duration_request_anim) / 3;
                    mRequestAnim2 = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(),
                            R.animator.request_old);
                    mRequestAnim2.setTarget(holder.requestView.findViewById(R.id.image_2));
                    mRequestAnim2.setStartDelay(delay);
                    mRequestAnim2.start();

                    mRequestAnim3 = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(),
                            R.animator.request_old);
                    mRequestAnim3.setTarget(holder.requestView.findViewById(R.id.image_3));
                    mRequestAnim3.setStartDelay(delay << 1);
                    mRequestAnim3.start();
                }
                return;
            }
            ////// Publication












            // Set from pseudo icon
            boolean female = (!mDataSource.isNull(position, COLUMN_INDEX_SEX)) &&
                    (mDataSource.getInt(position, COLUMN_INDEX_SEX) == CamaradesTable.FEMALE);
            String profile = (!mDataSource.isNull(position, COLUMN_INDEX_PROFILE)) ?
                    mDataSource.getString(position, COLUMN_INDEX_PROFILE) : null;
            Tools.setProfile(getActivity(), (ImageView) holder.rootView.findViewById(R.id.image_pseudo),
                    female, profile, R.dimen.shortcut_height, true);







            /*
            // Set notify synchronization
            Tools.setSyncView(getActivity(), (TextView) holder.rootView.findViewById(R.id.text_sync_date),
                    (ImageView) holder.rootView.findViewById(R.id.image_sync),
                    mDataSource.getString(position, COLUMN_INDEX_STATUS_DATE),
                    (byte) mDataSource.getInt(position, COLUMN_INDEX_SYNC));
                    */










            // Events
            View imagePseudo = holder.rootView.findViewById(R.id.image_pseudo);

            imagePseudo.setTag(R.id.tag_position, position);
            imagePseudo.setOnClickListener(this);

            ////// Animate item appearance
            animateAppearance(holder, new AppearanceAnimatorMaker() {
                @Override
                public void onAnimate(View item) {
                    //Logs.add(Logs.Type.V, "item: " + item);






                }

                @Override
                public void onCancel(View item) {
                    Logs.add(Logs.Type.V, "item: " + item);






                }
            });
        }
    }

    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, final Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        if (!cursor.moveToFirst())
            return;

        if (id == Queries.MAIN_PUBLICATIONS_LIST) {
            mPubCursor = cursor;
            refresh(); // Display publications
        }
    }

    @Override
    public void onLoaderReset() {
        Logs.add(Logs.Type.V, null);
        mPubCursor = null;
    }

    //////
    private QueryLoader mListLoader; // Publication list query loader
    private Cursor mPubCursor; // Publications cursor
    private String mPubLast; // Last publication date received

    private short mQueryCount = Constants.NO_DATA; // DB query result count
    private short mQueryLimit = Queries.PUBLICATIONS_LIST_LIMIT; // DB query limit

    // Query column indexes
    private static final int COLUMN_INDEX_STATUS_DATE = 1;
    private static final int COLUMN_INDEX_SYNC = 2;
    private static final int COLUMN_INDEX_DATE = 4;
    private static final int COLUMN_INDEX_SEX = 7;
    private static final int COLUMN_INDEX_PROFILE = 8;
    private static final int COLUMN_INDEX_PUB_ID = 15;











    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final OldReceiver mOldReceiver = new OldReceiver(); // Old data broadcast receiver
    private class OldReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Logs.add(Logs.Type.V, "context: " + context + ";intent: " + intent);
            if ((intent.getAction().equals(DataService.REQUEST_OLD_DATA)) && // Old request
                    (intent.getBooleanExtra(DataService.EXTRA_DATA_RECEIVED, false)) && // Received
                    (((Uri) intent.getParcelableExtra(DataRequest.EXTRA_DATA_URI)) // Publication URI
                            .compareTo(mPubUri) == 0)) {

                mPubAdapter.setRequesting(false);
                if (!intent.getBooleanExtra(DataService.EXTRA_DATA_OLD_FOUND, false)) {
                    if (mQueryCount > (mQueryLimit - Queries.PUBLICATIONS_OLD_LIMIT)) {

                        // Refresh list if no more old remote DB publications but existing in local DB
                        mQueryLimit = mQueryCount;
                        refresh();

                    } else // Remove expected old publications count from limitation
                        mQueryLimit -= Queries.NOTIFICATIONS_OLD_LIMIT;
                }
            }
        }
    }

    private void refresh() { // Display/Refresh publication list
        Logs.add(Logs.Type.V, null);
        mPubCursor.moveToFirst();

        // Get last publication date received
        String lastPub;
        do {
            lastPub = mPubCursor.getString(COLUMN_INDEX_DATE);
        } while (mPubCursor.moveToNext());
        mPubCursor.moveToFirst();

        // Update current display data
        short count = (short) mPubCursor.getCount();
        if ((mQueryCount != Constants.NO_DATA) && (mPubLast.compareTo(lastPub) == 0))
            mQueryLimit += count - mQueryCount; // New entries case (from remote DB)

        mQueryCount = count;
        mPubLast = lastPub;

        //////
        if (mPubAdapter != null) { // Check if not the initial query

            ////// Update publication list
            Logs.add(Logs.Type.I, "Query update");

            if (mPubList.getAdapter() == null)
                mPubList.setAdapter(mPubAdapter);
            RecyclerAdapter.SwapResult swapResult = mPubAdapter.getDataSource().swap(mPubAdapter, mPubCursor,
                    mQueryLimit, null);

            // Check if only change publication(s) on synchronization fields
            boolean syncFieldsOnly = swapResult.countChanged > 0;
            for (int i = 0; i < swapResult.columnChanged.size(); ++i) {
                if ((swapResult.columnChanged.get(i) != COLUMN_INDEX_SYNC) &&
                        (swapResult.columnChanged.get(i) != COLUMN_INDEX_STATUS_DATE)) {

                    syncFieldsOnly = false;
                    break;
                }
            }
            if (syncFieldsOnly) // Disable change animations (only sync fields have changed)
                ((RecyclerItemAnimator)mPubList.getItemAnimator())
                        .setDisableChangeAnimations(swapResult.countChanged);

        } else {
            Logs.add(Logs.Type.I, "Initial query");

            ////// Fill publication list
            mPubAdapter = new PubRecyclerViewAdapter();
            mPubAdapter.getDataSource().fill(mPubCursor, mQueryLimit);
            mPubList.setAdapter(mPubAdapter);
            mPubList.scrollToPosition(0);
        }
    }

    ////// MainFragment ////////////////////////////////////////////////////////////////////////////
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logs.add(Logs.Type.V, "context: " + context);
        mListLoader = new QueryLoader(context, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_publications, container, false);
        rootView.setTag(Constants.MAIN_SECTION_PUBLICATIONS);

        // Set shortcut data (default)
        SpannableStringBuilder data = new SpannableStringBuilder(getString(R.string.no_publication));
        mListener.onGetShortcut(Constants.MAIN_SECTION_PUBLICATIONS, false).setMessage(data);
        data = new SpannableStringBuilder(getString(R.string.no_publication_info));
        mListener.onGetShortcut(Constants.MAIN_SECTION_PUBLICATIONS, false).setInfo(data);

        // Set URI to observe DB changes
        mPubUri = Uris.getUri(Uris.ID_USER_PUBLICATIONS, String.valueOf(getActivity().getIntent()
                .getIntExtra(Login.EXTRA_DATA_PSEUDO_ID, Constants.NO_DATA)));

        // Set recycler view
        final RecyclerItemAnimator itemAnimator = new RecyclerItemAnimator();
        itemAnimator.setAnimationMaker(new RecyclerItemAnimator.ItemAnimatorMaker() {
            @Override
            public void onCancel(RecyclerItemAnimator.AnimType type, View item) {
                Logs.add(Logs.Type.V, "type: " + type + ";item: " + item);
                switch (type) {

                    case ADD: {



                        break;
                    }
                    case REMOVE: {



                        break;
                    }
                }
            }

            @Override
            public void onPrepare(RecyclerItemAnimator.AnimInfo info) {
                Logs.add(Logs.Type.V, "info: " + info);
                switch (RecyclerItemAnimator.getAnimType(info)) {

                    case ADD: {



                        break;
                    }
                    case REMOVE: {



                        break;
                    }
                }
            }

            @Override
            public ViewPropertyAnimatorCompat onAnimate(RecyclerItemAnimator.AnimInfo info, boolean changeNew) {
                Logs.add(Logs.Type.V, "info: " + info + ";changeNew: " + changeNew);

                View itemView = info.mHolder.itemView;
                switch (RecyclerItemAnimator.getAnimType(info)) {

                    case ADD: {



                        break;
                    }
                    case REMOVE: {




                        break;
                    }
                }
                return ViewCompat.animate(itemView);
            }
        });
        LinearLayoutManager linearManager = new LinearLayoutManager(getContext());
        linearManager.setOrientation(LinearLayoutManager.VERTICAL);

        mPubList = (RecyclerView) rootView.findViewById(R.id.list_publication);
        mPubList.setLayoutManager(linearManager);
        mPubList.setItemAnimator(itemAnimator);

        // Initialize notification list (set query loaders)
        Bundle pubData = new Bundle();
        pubData.putParcelable(QueryLoader.DATA_KEY_URI, mPubUri);
        /*pubData.putString(QueryLoader.DATA_KEY_SELECTION,
                "SELECT " +
                        NotificationsTable.COLUMN_OBJECT_TYPE + ',' + // COLUMN_INDEX_OBJECT_TYPE
                        NotificationsTable.TABLE_NAME + '.' + Constants.DATA_COLUMN_STATUS_DATE + ',' + // COLUMN_INDEX_STATUS_DATE
                        NotificationsTable.TABLE_NAME + '.' + Constants.DATA_COLUMN_SYNCHRONIZED + ',' + // COLUMN_INDEX_SYNC
                        NotificationsTable.TABLE_NAME + '.' + IDataTable.DataField.COLUMN_ID + ',' + // COLUMN_INDEX_NOTIFY_ID
                        NotificationsTable.COLUMN_DATE + ',' + // COLUMN_INDEX_DATE
                        NotificationsTable.COLUMN_LU_FLAG + ',' + // COLUMN_INDEX_LU_FLAG
                        CamaradesTable.COLUMN_PSEUDO + ',' + // COLUMN_INDEX_PSEUDO
                        CamaradesTable.COLUMN_SEXE + ',' + // COLUMN_INDEX_SEX
                        CamaradesTable.COLUMN_PROFILE + ',' + // COLUMN_INDEX_PROFILE
                        CamaradesTable.TABLE_NAME + '.' + IDataTable.DataField.COLUMN_ID + ',' + // COLUMN_INDEX_MEMBER_ID
                        PhotosTable.COLUMN_ALBUM + ',' + // COLUMN_INDEX_ALBUM
                        PhotosTable.TABLE_NAME + '.' + IDataTable.DataField.COLUMN_ID + ',' + // COLUMN_INDEX_PHOTO_ID
                        ActualitesTable.COLUMN_TEXT + ',' + // COLUMN_INDEX_PUB_TEXT
                        ActualitesTable.COLUMN_LINK + ',' + // COLUMN_INDEX_LINK
                        ActualitesTable.COLUMN_FICHIER + ',' + // COLUMN_INDEX_FICHIER
                        ActualitesTable.TABLE_NAME + '.' + IDataTable.DataField.COLUMN_ID + ',' + // COLUMN_INDEX_PUB_ID
                        MessagerieTable.COLUMN_MESSAGE + ',' + // COLUMN_INDEX_MSG_TEXT
                        MessagerieTable.TABLE_NAME + '.' + IDataTable.DataField.COLUMN_ID + ',' + // COLUMN_INDEX_MSG_ID
                        CommentairesTable.COLUMN_TEXT + ',' + // COLUMN_INDEX_COM_TEXT
                        CommentairesTable.TABLE_NAME + '.' + IDataTable.DataField.COLUMN_ID + // COLUMN_INDEX_COMMENT_ID

                        " FROM " + NotificationsTable.TABLE_NAME +
                        " LEFT JOIN " + CamaradesTable.TABLE_NAME + " ON " +
                        NotificationsTable.COLUMN_OBJECT_FROM + '=' + CamaradesTable.COLUMN_PSEUDO +
                        " LEFT JOIN " + PhotosTable.TABLE_NAME + " ON " +
                        NotificationsTable.COLUMN_OBJECT_ID + '=' + PhotosTable.COLUMN_FICHIER_ID + " AND " +
                        NotificationsTable.COLUMN_OBJECT_TYPE + "='" + NotificationsTable.TYPE_SHARED + "' AND " +
                        NotificationsTable.COLUMN_OBJECT_DATE + " IS NULL" +
                        " LEFT JOIN " + ActualitesTable.TABLE_NAME + " ON " +
                        NotificationsTable.COLUMN_OBJECT_ID + '=' + ActualitesTable.COLUMN_ACTU_ID + " AND " +
                        NotificationsTable.COLUMN_OBJECT_TYPE + "='" + NotificationsTable.TYPE_WALL + '\'' +
                        " LEFT JOIN " + MessagerieTable.TABLE_NAME + " ON " +
                        NotificationsTable.COLUMN_OBJECT_FROM + '=' + MessagerieTable.COLUMN_FROM + " AND " +
                        NotificationsTable.COLUMN_OBJECT_DATE + '=' +
                        MessagerieTable.COLUMN_DATE + "||' '||" + MessagerieTable.COLUMN_TIME + " AND " +
                        NotificationsTable.COLUMN_OBJECT_TYPE + "='" + NotificationsTable.TYPE_MAIL + "' AND " +
                        NotificationsTable.COLUMN_OBJECT_ID + " IS NULL" +
                        " LEFT JOIN " + CommentairesTable.TABLE_NAME + " ON " +
                        NotificationsTable.COLUMN_OBJECT_ID + '=' + CommentairesTable.COLUMN_OBJ_ID + " AND " +
                        NotificationsTable.COLUMN_OBJECT_TYPE + '=' + CommentairesTable.COLUMN_OBJ_TYPE + " AND " +
                        NotificationsTable.COLUMN_OBJECT_DATE + '=' + CommentairesTable.COLUMN_DATE + " AND " +
                        NotificationsTable.COLUMN_OBJECT_FROM + '=' + CommentairesTable.COLUMN_PSEUDO +
                        " WHERE " +
                        NotificationsTable.COLUMN_PSEUDO + "='" + getIntent().getStringExtra(Login.EXTRA_DATA_PSEUDO) +
                        "' ORDER BY " + NotificationsTable.COLUMN_DATE + " DESC");*/

        mListLoader.init(getActivity(), Queries.MAIN_PUBLICATIONS_LIST, pubData);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Logs.add(Logs.Type.V, null);

        // Register data service & old request receiver
        getContext().sendBroadcast(DataService.getIntent(true, Tables.ID_ACTUALITES, mPubUri));
        getContext().registerReceiver(mOldReceiver, new IntentFilter(DataService.REQUEST_OLD_DATA));
    }
    @Override
    public void onPause() {
        super.onPause();
        Logs.add(Logs.Type.V, null);

        // Unregister data service & old request receiver
        getContext().sendBroadcast(DataService.getIntent(false, Tables.ID_ACTUALITES, mPubUri));
        getContext().unregisterReceiver(mOldReceiver);
    }
}
