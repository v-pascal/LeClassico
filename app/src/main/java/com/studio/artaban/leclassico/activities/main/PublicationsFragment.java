package com.studio.artaban.leclassico.activities.main;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.LoggedActivity;
import com.studio.artaban.leclassico.activities.album.FullPhotoActivity;
import com.studio.artaban.leclassico.activities.profile.ProfileActivity;
import com.studio.artaban.leclassico.activities.publication.PublicationActivity;
import com.studio.artaban.leclassico.animations.RequestAnimation;
import com.studio.artaban.leclassico.components.RecyclerAdapter;
import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.data.tables.persistent.LinksTable;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.services.DataService;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.IDataTable;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.data.tables.AbonnementsTable;
import com.studio.artaban.leclassico.data.tables.ActualitesTable;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.helpers.Glider;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;
import com.studio.artaban.leclassico.helpers.Storage;
import com.studio.artaban.leclassico.services.LinkService;
import com.studio.artaban.leclassico.tools.Tools;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by pascal on 05/09/16.
 * Publications fragment class (MainActivity)
 */
public class PublicationsFragment extends MainFragment implements
        QueryLoader.OnResultListener, Internet.OnConnectivityListener {

    private static final String DATA_KEY_LINK_REQUESTS = "linkRequests";
    // Data keys

    private SpannableStringBuilder getInfo(String pseudo, short type, @ColorRes int color) {
    // Return publication info text: [User (with color)] has published [publication type]

        //Logs.add(Logs.Type.V, "pseudo: " + pseudo + ";type: " + type + ";color: " + color);
        int pseudoPos = getResources().getInteger(R.integer.publication_info_pseudo_pos);

        SpannableStringBuilder info = new SpannableStringBuilder(getString(R.string.publication_info,
                pseudo, getResources().getStringArray(R.array.publication_types)[type]));
        info.setSpan(new ForegroundColorSpan(getResources().getColor(color)),
                pseudoPos, pseudoPos + pseudo.length(), 0);
        return info;
    }

    //
    private static class ShortcutData {

        int pseudoId; // ID of the user that has published the last post
        boolean female; // Last published user gender
        String profile; // Last published user profile (icon)
        String date; // Date of the last publication

        SpannableStringBuilder message; // Last published type
        SpannableStringBuilder info; // Last published comment text
    }
    private ShortcutData getShortcutData() { // Return shortcut data according publication cursor
        Logs.add(Logs.Type.V, null);
        ShortcutData result = new ShortcutData();

        result.pseudoId = mPubCursor.getInt(COLUMN_INDEX_MEMBER_ID);
        result.female = (!mPubCursor.isNull(COLUMN_INDEX_SEX)) &&
                (mPubCursor.getInt(COLUMN_INDEX_SEX) == CamaradesTable.GENDER_FEMALE);
        result.profile = (!mPubCursor.isNull(COLUMN_INDEX_PROFILE)) ?
                mPubCursor.getString(COLUMN_INDEX_PROFILE) : null;
        result.date = mPubCursor.getString(COLUMN_INDEX_DATE);

        short type = Tools.getPubType(DataTable.getDataType(mPubCursor), Constants.NO_DATA,
                COLUMN_INDEX_LINK, COLUMN_INDEX_FICHIER);
        result.message = getInfo(mPubCursor.getString(COLUMN_INDEX_PSEUDO), type, R.color.yellow);
        result.info = new SpannableStringBuilder((!mPubCursor.isNull(COLUMN_INDEX_TEXT))?
                mPubCursor.getString(COLUMN_INDEX_TEXT) : getString(R.string.no_publication_text));
        return result;
    }

    private void fillShortcut(ShortcutData data, boolean newPub) { // Fill shortcut info (with data)
        Logs.add(Logs.Type.V, "data: " + data + "newPub: " + newPub);

        ShortcutFragment shortcut = mListener.onGetShortcut(Constants.MAIN_SECTION_PUBLICATIONS, newPub);
        shortcut.setIcon(data.pseudoId, data.female, data.profile);
        shortcut.setMessage(data.message);
        shortcut.setInfo(data.info);
        shortcut.setDate(false, data.date);
    }
    private RecyclerView mPubList; // Recycler view containing publication list
    private Uri mPubUri; // Publications observer URI
    // Recycler view adapter (with cursor management)
    private final PubRecyclerViewAdapter mPubAdapter = new PubRecyclerViewAdapter();

    //////
    private class PubRecyclerViewAdapter extends RecyclerAdapter {

        private RequestAnimation mRequestAnim; // Request old management (animation + event)
        public PubRecyclerViewAdapter() {
            super(R.layout.layout_publication_item, R.layout.layout_old_request, true, COLUMN_INDEX_PUB_ID);
        }

        private void displayURL(RecyclerAdapter.ViewHolder holder, int position) {
        // Display URL text view on the link image

            Logs.add(Logs.Type.V, "holder: " + holder + ";position: " + position);
            TextView url = (TextView) holder.rootView.findViewById(R.id.text_url);

            url.setText(mDataSource.getString(position, COLUMN_INDEX_LINK));
            url.setVisibility(View.VISIBLE);
        }

        ////// View.OnClickListener ////////////////////////////////////////////////////////////////
        @Override
        public void onClick(View sender) {
            Logs.add(Logs.Type.V, "sender: " + sender);
            switch (sender.getId()) {

                case R.id.image_pseudo: { // Display publication owner profile
                    int pseudoId = (int)sender.getTag(R.id.tag_pseudo_id);
                    Logs.add(Logs.Type.I, "Pseudo #" + pseudoId + " selected");

                    ////// Start profile activity
                    Intent profile = new Intent(getActivity(), ProfileActivity.class);
                    profile.putExtra(LoggedActivity.EXTRA_DATA_ID, pseudoId);
                    startActivity(profile);
                    break;
                }
                case R.id.pub_card:
                case R.id.image_display: { // Display publication activity (with comments)
                    int commentId = (int)sender.getTag(R.id.tag_comment_id);
                    Logs.add(Logs.Type.I, "Display #" + commentId + " selected");

                    ////// Start publication activity
                    Intent pub = new Intent(getContext(), PublicationActivity.class);
                    pub.putExtra(LoggedActivity.EXTRA_DATA_ID, commentId);
                    pub.putExtra(LoggedActivity.EXTRA_DATA_URI, mPubUri);

                    startActivity(pub);
                    break;
                }
                case R.id.layout_published: {
                    int position = (int)sender.getTag(R.id.tag_position);
                    Logs.add(Logs.Type.I, "Link/Image #" + position + " selected");

                    if (mDataSource.isNull(position, COLUMN_INDEX_LINK)) { // Image clicked

                        String name = mDataSource.getString(position, COLUMN_INDEX_FICHIER);
                        String title = (!mDataSource.isNull(position, COLUMN_INDEX_TEXT))?
                                mDataSource.getString(position, COLUMN_INDEX_TEXT):
                                mDataSource.getString(position, COLUMN_INDEX_FICHIER);

                        ////// Start full screen photo activity
                        Intent fullScreen = new Intent(getContext(), FullPhotoActivity.class);
                        fullScreen.putExtra(FullPhotoActivity.EXTRA_DATA_TITLE, title);
                        fullScreen.putExtra(FullPhotoActivity.EXTRA_DATA_NAME, name);
                        Login.copyExtraData(getActivity().getIntent(), fullScreen);

                        ActivityOptions options = ActivityOptions
                                .makeSceneTransitionAnimation(getActivity(), sender, name);
                        startActivity(fullScreen, options.toBundle());

                    } else // Link clicked
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(mDataSource.getString(position, COLUMN_INDEX_LINK))));
                    break;
                }
                case R.id.layout_more: { // Old data requested
                    Logs.add(Logs.Type.I, "Old publications requested");

                    ////// Request old publications to remote DB
                    mQueryLimit += Queries.PUBLICATIONS_OLD_LIMIT;
                    mPubAdapter.setRequesting(RequestFlag.IN_PROGRESS);

                    Intent request = new Intent(DataService.REQUEST_OLD_DATA);
                    request.putExtra(DataRequest.EXTRA_DATA_DATE, mQueryDate);
                    getActivity().sendBroadcast(DataService.getIntent(request, Tables.ID_ACTUALITES, mPubUri));
                    break;
                }
            }
        }

        ////// RecyclerAdapter /////////////////////////////////////////////////////////////////////
        @Override
        public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, final int position) {
            Logs.add(Logs.Type.V, "holder: " + holder + ";position: " + position);

            if (isRequestHolder(holder, position)) { ////// Request

                Logs.add(Logs.Type.I, "Request item (" + isRequesting() + ')');
                if (mRequestAnim == null)
                    mRequestAnim = new RequestAnimation(getContext(), true);

                mRequestAnim.display(getResources(), this, holder.requestView);
                return;
            }
            ////// Publication

            // Set pseudo icon
            boolean female = (!mDataSource.isNull(position, COLUMN_INDEX_SEX)) &&
                    (mDataSource.getInt(position, COLUMN_INDEX_SEX) == CamaradesTable.GENDER_FEMALE);
            String profile = (!mDataSource.isNull(position, COLUMN_INDEX_PROFILE)) ?
                    mDataSource.getString(position, COLUMN_INDEX_PROFILE) : null;
            Tools.setProfile(getActivity(), (ImageView) holder.rootView.findViewById(R.id.image_pseudo),
                    female, profile, R.dimen.user_item_height, true);

            // Display/Hide removal publication button
            holder.rootView.findViewById(R.id.image_remove)
                    .setVisibility((mDataSource.getInt(position, COLUMN_INDEX_MEMBER_ID) == getActivity()
                            .getIntent().getIntExtra(Login.EXTRA_DATA_PSEUDO_ID, Constants.NO_DATA)) ?
                            View.VISIBLE : View.GONE);

            // Set publication header (info, date & type icon)
            short type = Tools.getPubType(mDataSource, position, COLUMN_INDEX_LINK, COLUMN_INDEX_FICHIER);
            SpannableStringBuilder info = getInfo(mDataSource.getString(position, COLUMN_INDEX_PSEUDO),
                    type, R.color.colorPrimaryLocation);

            ((TextView)holder.rootView.findViewById(R.id.text_info)).setText(info, TextView.BufferType.SPANNABLE);
            ((ImageView)holder.rootView.findViewById(R.id.image_type))
                    .setImageDrawable(getResources().getDrawable(Tools.getPubIcon(type)));

            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.FORMAT_DATE_TIME);
            String date = mDataSource.getString(position, COLUMN_INDEX_DATE);
            try {
                Date pubDate = dateFormat.parse(date);
                DateFormat userDate = android.text.format.DateFormat.getMediumDateFormat(getContext());
                DateFormat userTime = android.text.format.DateFormat.getTimeFormat(getContext());

                ((TextView)holder.rootView.findViewById(R.id.text_date))
                        .setText(getString(R.string.publication_date, userDate.format(pubDate),
                                userTime.format(pubDate)));

            } catch (ParseException e) {
                Logs.add(Logs.Type.E, "Wrong status date format: " + date);
            }

            // Set message text (if any)
            TextView message = (TextView) holder.rootView.findViewById(R.id.text_message);
            if (mDataSource.isNull(position, COLUMN_INDEX_TEXT))
                message.setVisibility(View.GONE);
            else {

                message.setVisibility(View.VISIBLE);
                message.setText(mDataSource.getString(position, COLUMN_INDEX_TEXT));
            }

            // Set image or link (if any)
            if (type != ActualitesTable.TYPE_TEXT) {

                final View layoutPub = holder.rootView.findViewById(R.id.layout_published);
                layoutPub.setVisibility(View.VISIBLE);

                holder.rootView.findViewById(R.id.text_url).setVisibility(View.GONE);
                holder.rootView.findViewById(R.id.image_loading).setVisibility(View.GONE);
                holder.rootView.findViewById(R.id.text_link_title).setVisibility(View.GONE);
                holder.rootView.findViewById(R.id.text_link_description).setVisibility(View.GONE);
                holder.rootView.findViewById(R.id.text_link_info).setVisibility(View.GONE);

                ImageView image = (ImageView)holder.rootView.findViewById(R.id.image_published);
                ((RelativeLayout.LayoutParams)image.getLayoutParams()).setMargins(0, 0, 0, 0);

                if (type == ActualitesTable.TYPE_IMAGE) { ////// Image

                    String photo = mDataSource.getString(position, COLUMN_INDEX_FICHIER);
                    image.setTransitionName(photo);
                    layoutPub.setOnClickListener(null);

                    Glider.with(getContext()).placeholder(R.drawable.no_photo)
                            .load(Storage.FOLDER_PHOTOS + File.separator + photo,
                                    Constants.APP_URL_PHOTOS + '/' + photo)
                            .into(image, new Glider.OnLoadListener() {
                                @Override
                                public void onLoadFailed(ImageView imageView) {

                                }

                                @Override
                                public boolean onSetResource(Bitmap resource, ImageView imageView) {

                                    layoutPub.setTag(R.id.tag_position, position);
                                    layoutPub.setOnClickListener(PubRecyclerViewAdapter.this);
                                    return false;
                                }
                            });

                } else { ////// Link

                    image.setImageDrawable(getResources().getDrawable(R.drawable.earth));
                    layoutPub.setTag(R.id.tag_position, position);
                    layoutPub.setOnClickListener(this);

                    if (!mDataSource.isNull(position, COLUMN_INDEX_LINK_ID)) {
                        // Link data already downloaded

                        if (!mDataSource.isNull(position, COLUMN_INDEX_LINK_IMAGE)) {
                            Bitmap bmp = BitmapFactory.decodeFile(Storage.get() + Storage.FOLDER_LINKS +
                                    File.separator + mDataSource.getInt(position, COLUMN_INDEX_LINK_ID) +
                                    File.separator + mDataSource.getString(position, COLUMN_INDEX_LINK_IMAGE));

                            if (bmp != null) {
                                image.setImageBitmap(bmp);

                                int margin = getResources().getDimensionPixelSize(R.dimen.pub_image_margin);
                                ((RelativeLayout.LayoutParams)image.getLayoutParams())
                                        .setMargins(margin, margin, margin, 0);

                            } else {
                                Logs.add(Logs.Type.W, "Failed to decode link image");
                                displayURL(holder, position);
                            }

                        } else
                            displayURL(holder, position);

                        if (!mDataSource.isNull(position, COLUMN_INDEX_LINK_TITLE)) {
                            TextView title = (TextView) holder.rootView.findViewById(R.id.text_link_title);

                            title.setText(mDataSource.getString(position, COLUMN_INDEX_LINK_TITLE));
                            title.setVisibility(View.VISIBLE);
                        }
                        if (!mDataSource.isNull(position, COLUMN_INDEX_LINK_DESC)) {
                            TextView desc = (TextView) holder.rootView.findViewById(R.id.text_link_description);

                            desc.setText(mDataSource.getString(position, COLUMN_INDEX_LINK_DESC));
                            desc.setVisibility(View.VISIBLE);
                        }
                        if (!mDataSource.isNull(position, COLUMN_INDEX_LINK_INFO)) {
                            TextView owner = (TextView) holder.rootView.findViewById(R.id.text_link_info);

                            owner.setText(mDataSource.getString(position, COLUMN_INDEX_LINK_INFO));
                            owner.setVisibility(View.VISIBLE);
                        }

                        // Remove link request from list if exists (download finished)
                        int request = mLinkRequests.indexOf(mDataSource.getInt(position, COLUMN_INDEX_PUB_ID));
                        if (request != Constants.NO_DATA)
                            mLinkRequests.remove(request);

                    } else { // Download or wait link data downloaded
                        displayURL(holder, position);

                        if (mLinkRequests.contains(mDataSource.getInt(position, COLUMN_INDEX_PUB_ID))) {
                            // Wait downloading link data (progress animation)

                            ImageView loading = (ImageView) holder.rootView.findViewById(R.id.image_loading);
                            loading.setVisibility(View.VISIBLE);
                            loading.startAnimation(Tools.getProgressAnimation());
                        }
                        //else // Nothing to do (let's connectivity listener do the job)
                    }
                }

            } else ////// Text
                holder.rootView.findViewById(R.id.layout_published).setVisibility(View.GONE);

            // Set comments count
            ((TextView)holder.rootView.findViewById(R.id.text_comments_count))
                    .setText(getString(R.string.publication_comments_count,
                            mDataSource.getInt(position, COLUMN_INDEX_COMMENTS)));

            // Set synchronization
            Tools.setSyncView(getActivity(), (TextView) holder.rootView.findViewById(R.id.text_sync_date),
                    (ImageView) holder.rootView.findViewById(R.id.image_sync),
                    mDataSource.getString(position, COLUMN_INDEX_STATUS_DATE),
                    (byte) mDataSource.getInt(position, COLUMN_INDEX_SYNC));

            ////// Events
            View imagePseudo = holder.rootView.findViewById(R.id.image_pseudo);
            View cardView = holder.rootView.findViewById(R.id.pub_card);
            View imageDisplay = holder.rootView.findViewById(R.id.image_display);

            imagePseudo.setTag(R.id.tag_pseudo_id, mDataSource.getInt(position, COLUMN_INDEX_MEMBER_ID));
            imagePseudo.setOnClickListener(this);
            cardView.setTag(R.id.tag_comment_id, mDataSource.getInt(position, COLUMN_INDEX_PUB_ID));
            cardView.setOnClickListener(this);
            imageDisplay.setTag(R.id.tag_comment_id, mDataSource.getInt(position, COLUMN_INDEX_PUB_ID));
            imageDisplay.setOnClickListener(this);
        }
    }

    ////// OnConnectivityListener //////////////////////////////////////////////////////////////////
    @Override
    public void onConnection() {
        Logs.add(Logs.Type.V, null);
        refresh();
    }

    @Override
    public void onDisconnection() {

    }

    //////
    private ArrayList<Integer> mLinkRequests; // To keep download data link requests

    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
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
    private String mPubLast; // Last publication date received (newest date)

    private short mQueryCount; // DB query result count
    private short mQueryLimit = Queries.PUBLICATIONS_LIST_LIMIT; // DB query limit
    private String mQueryDate; // Old query date displayed (visible)

    // Query column indexes
    private static final int COLUMN_INDEX_ACTU_ID = 0;
    private static final int COLUMN_INDEX_CAMARADE = 1;
    private static final int COLUMN_INDEX_DATE = 2;
    private static final int COLUMN_INDEX_TEXT = 3;
    private static final int COLUMN_INDEX_LINK = 4;
    private static final int COLUMN_INDEX_FICHIER = 5;
    private static final int COLUMN_INDEX_COMMENTS = 6;
    private static final int COLUMN_INDEX_STATUS_DATE = 7;
    private static final int COLUMN_INDEX_SYNC = 8;
    private static final int COLUMN_INDEX_PUB_ID = 9;
    private static final int COLUMN_INDEX_LINK_ID = 10;
    private static final int COLUMN_INDEX_LINK_IMAGE = 11;
    private static final int COLUMN_INDEX_LINK_TITLE = 12;
    private static final int COLUMN_INDEX_LINK_DESC = 13;
    private static final int COLUMN_INDEX_LINK_INFO = 14;
    private static final int COLUMN_INDEX_PSEUDO = 15;
    private static final int COLUMN_INDEX_SEX = 16;
    private static final int COLUMN_INDEX_PROFILE = 17;
    private static final int COLUMN_INDEX_MEMBER_ID = 18;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final OldReceiver mOldReceiver = new OldReceiver(); // Old data broadcast receiver
    private class OldReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Logs.add(Logs.Type.V, "context: " + context + ";intent: " + intent);
            if ((intent.getAction().equals(DataService.REQUEST_OLD_DATA)) && // Old request
                    (intent.getBooleanExtra(DataService.EXTRA_DATA_RECEIVED, false)) && // Received
                    (((Uri) intent.getParcelableExtra(DataRequest.EXTRA_DATA_URI))
                            .compareTo(mPubUri) == 0)) { // Publication URI

                boolean localOld = false;
                DataRequest.Result result =
                        (DataRequest.Result) intent.getSerializableExtra(DataService.EXTRA_DATA_REQUEST_RESULT);
                if (result != DataRequest.Result.FOUND) {
                    if (mQueryCount > (mQueryLimit - Queries.PUBLICATIONS_OLD_LIMIT)) {
                        refresh(); // No more old remote DB publications but existing in local DB
                        localOld = true;
                    }
                    else
                        mQueryLimit -= Queries.PUBLICATIONS_OLD_LIMIT;
                }
                //else // Not found or No more remote DB entries
                // NB: When old entries are found DB update will notify cursor (no need to refresh)

                mPubAdapter.setRequesting(((result == DataRequest.Result.NO_MORE) && (!localOld))?
                        RecyclerAdapter.RequestFlag.HIDDEN : RecyclerAdapter.RequestFlag.DISPLAYED);
            }
        }
    }
    private boolean mVisible; // Fragment visibility flag (displayed)
    private boolean mToRefresh; // Refresh list request flag (done when visible)

    private void refresh() { // Display/Refresh publication list
        Logs.add(Logs.Type.V, null);
        mPubCursor.moveToFirst();

        // Get shortcut data (according new cursor)
        final ShortcutData shortcutData = getShortcutData();

        // Update current display data
        String lastPub = mPubCursor.getString(COLUMN_INDEX_DATE);
        short count = (short) mPubCursor.getCount();
        boolean fillShortcut = true;

        if ((mPubLast != null) && (mPubLast.compareTo(lastPub) != 0)) {
            fillShortcut = false;

            mQueryLimit += count - mQueryCount; // New or removed entries case (from remote DB)
            if (mQueryLimit <= 0)
                mQueryLimit = count;

            if (mVisible) { // Update shortcut...
                fillShortcut(shortcutData, true); // ...with animation
                mListener.onAnimateShortcut(Constants.MAIN_SECTION_PUBLICATIONS,
                        new OnFragmentListener.OnAnimationListener() {

                            @Override
                            public void onCopyNewToPrevious(ShortcutFragment previous) {
                                Logs.add(Logs.Type.V, "previous: " + previous);

                                previous.setIcon(shortcutData.pseudoId, shortcutData.female, shortcutData.profile);
                                previous.setMessage(shortcutData.message);
                                previous.setInfo(shortcutData.info);
                            }
                        });
            } else // ...without animation
                fillShortcut(shortcutData, false);
        }
        mQueryCount = count;
        mPubLast = lastPub;

        // Get last visible publication date
        int limit = mQueryLimit;
        do {
            mQueryDate = mPubCursor.getString(COLUMN_INDEX_DATE);
            if (--limit == 0)
                break; // Only visible item are concerned

        } while (mPubCursor.moveToNext());
        mPubCursor.moveToFirst();

        // Send link data requests (if needed)
        if (Internet.isConnected()) {
            do {
                if ((!mPubCursor.isNull(COLUMN_INDEX_LINK)) && (mPubCursor.isNull(COLUMN_INDEX_LINK_ID)) &&
                        (!mLinkRequests.contains(mPubCursor.getInt(COLUMN_INDEX_PUB_ID)))) {

                    // Download data link (send request to link service)
                    Logs.add(Logs.Type.I, "Download '" + mPubCursor.getString(COLUMN_INDEX_LINK) + "' link data");

                    ////// Start link service
                    Intent linkIntent = new Intent(getContext(), LinkService.class);
                    linkIntent.setData(Uri.parse(mPubCursor.getString(COLUMN_INDEX_LINK)));
                    linkIntent.putExtra(LinkService.EXTRA_DATA_URI, mPubUri);
                    getContext().startService(linkIntent); // Start link data download service

                    mLinkRequests.add(mPubCursor.getInt(COLUMN_INDEX_PUB_ID));
                }

            } while (mPubCursor.moveToNext());
            mPubCursor.moveToFirst();
        }
        if (fillShortcut) // Fill shortcut info (if not already done)
            fillShortcut(shortcutData, false);

        //////
        mToRefresh = !mVisible;
        if ((mToRefresh) && (mPubAdapter.isInitialized()))
            return; // Do not refresh/update list now (not visible yet)
            // NB: Let's refresh when visible (see 'setMenuVisibility' method)

        if (mPubAdapter.isInitialized()) { // Check if not the initial query
            Logs.add(Logs.Type.I, "Query update");

            ////// Update publication list
            mPubAdapter.getDataSource().swap(mPubAdapter, mPubCursor, mQueryLimit, null, null);

        } else {
            Logs.add(Logs.Type.I, "Initial query");

            ////// Fill publication list
            mPubAdapter.getDataSource().fill(mPubCursor, mQueryLimit, null);
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

        // Restore data
        if (savedInstanceState != null)
            mLinkRequests = savedInstanceState.getIntegerArrayList(DATA_KEY_LINK_REQUESTS);
        if (mLinkRequests == null)
            mLinkRequests = new ArrayList<>();

        // Set shortcut data (default)
        SpannableStringBuilder data = new SpannableStringBuilder(getString(R.string.no_publication));
        mListener.onGetShortcut(Constants.MAIN_SECTION_PUBLICATIONS, false).setMessage(data);
        data = new SpannableStringBuilder(getString(R.string.no_publication_info));
        mListener.onGetShortcut(Constants.MAIN_SECTION_PUBLICATIONS, false).setInfo(data);

        // Set URI to observe DB changes
        mPubUri = Uris.getUri(Uris.ID_USER_PUBLICATIONS, String.valueOf(getActivity().getIntent()
                .getIntExtra(Login.EXTRA_DATA_PSEUDO_ID, Constants.NO_DATA)));

        // Set recycler view
        LinearLayoutManager linearManager = new LinearLayoutManager(getContext());
        linearManager.setOrientation(LinearLayoutManager.VERTICAL);

        mPubList = (RecyclerView) rootView.findViewById(R.id.list_publication);
        mPubList.setLayoutManager(linearManager);
        mPubList.setItemAnimator(new DefaultItemAnimator());
        mPubList.setAdapter(mPubAdapter);

        // Initialize publication list (set query loaders)
        Bundle pubData = new Bundle();
        pubData.putParcelable(QueryLoader.DATA_KEY_URI, mPubUri);
        pubData.putString(QueryLoader.DATA_KEY_SELECTION,
                "SELECT " +
                        ActualitesTable.COLUMN_ACTU_ID + ',' + // COLUMN_INDEX_ACTU_ID
                        ActualitesTable.COLUMN_CAMARADE + ',' + // COLUMN_INDEX_CAMARADE
                        ActualitesTable.COLUMN_DATE + ',' + // COLUMN_INDEX_DATE
                        ActualitesTable.COLUMN_TEXT + ',' + // COLUMN_INDEX_TEXT
                        ActualitesTable.COLUMN_LINK + ',' + // COLUMN_INDEX_LINK
                        ActualitesTable.COLUMN_FICHIER + ',' + // COLUMN_INDEX_FICHIER
                        ActualitesTable.COLUMN_COMMENTS + ',' + // COLUMN_INDEX_COMMENTS
                        ActualitesTable.TABLE_NAME + '.' + Constants.DATA_COLUMN_STATUS_DATE + ',' + // COLUMN_INDEX_STATUS_DATE
                        ActualitesTable.TABLE_NAME + '.' + Constants.DATA_COLUMN_SYNCHRONIZED + ',' + // COLUMN_INDEX_SYNC
                        ActualitesTable.TABLE_NAME + '.' + IDataTable.DataField.COLUMN_ID + ',' + // COLUMN_INDEX_PUB_ID
                        LinksTable.TABLE_NAME + '.' + IDataTable.DataField.COLUMN_ID + ',' + // COLUMN_INDEX_LINK_ID
                        LinksTable.COLUMN_IMAGE + ',' + // COLUMN_INDEX_LINK_IMAGE
                        LinksTable.COLUMN_TITLE + ',' + // COLUMN_INDEX_LINK_TITLE
                        LinksTable.COLUMN_DESCRIPTION + ',' + // COLUMN_INDEX_LINK_DESC
                        LinksTable.COLUMN_INFO + ',' + // COLUMN_INDEX_LINK_INFO
                        CamaradesTable.COLUMN_PSEUDO + ',' + // COLUMN_INDEX_PSEUDO
                        CamaradesTable.COLUMN_SEXE + ',' + // COLUMN_INDEX_SEX
                        CamaradesTable.COLUMN_PROFILE + ',' + // COLUMN_INDEX_PROFILE
                        CamaradesTable.TABLE_NAME + '.' + IDataTable.DataField.COLUMN_ID + // COLUMN_INDEX_MEMBER_ID
                        " FROM " + ActualitesTable.TABLE_NAME +
                        " LEFT JOIN " + CamaradesTable.TABLE_NAME + " ON " +
                        CamaradesTable.COLUMN_PSEUDO + '=' + ActualitesTable.COLUMN_PSEUDO +
                        " LEFT JOIN " + LinksTable.TABLE_NAME + " ON " +
                        LinksTable.COLUMN_URL + '=' + ActualitesTable.COLUMN_LINK +
                        " INNER JOIN " + AbonnementsTable.TABLE_NAME + " ON " +
                        AbonnementsTable.COLUMN_CAMARADE + '=' + ActualitesTable.COLUMN_PSEUDO + " AND " +
                        DataTable.getNotDeletedCriteria(AbonnementsTable.TABLE_NAME) + " AND " +
                        AbonnementsTable.COLUMN_PSEUDO + "='" + getActivity().getIntent().getStringExtra(Login.EXTRA_DATA_PSEUDO) + '\'' +
                        " WHERE " +
                        DataTable.getNotDeletedCriteria(ActualitesTable.TABLE_NAME) +
                        " ORDER BY " + ActualitesTable.COLUMN_DATE + " DESC");

        // TODO: Same as explained in the 'ActualitesTable.getIds' method (about the query above)
        mListLoader.init(getActivity(), Queries.MAIN_PUBLICATIONS_LIST, pubData);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Logs.add(Logs.Type.V, null);

        // Register data service & old request receiver
        getContext().sendBroadcast(DataService.getIntent(true, Tables.ID_ACTUALITES, mPubUri));
        getContext().sendBroadcast(DataService.getIntent(true, Tables.ID_ABONNEMENTS, mPubUri));
        getContext().registerReceiver(mOldReceiver, new IntentFilter(DataService.REQUEST_OLD_DATA));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putIntegerArrayList(DATA_KEY_LINK_REQUESTS, mLinkRequests);

        Logs.add(Logs.Type.V, "outState: " + outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        Logs.add(Logs.Type.V, "menuVisible: " + menuVisible);

        mVisible = menuVisible;
        if ((mVisible) && (mToRefresh))
            refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        Logs.add(Logs.Type.V, null);

        // Unregister data service & old request receiver
        getContext().sendBroadcast(DataService.getIntent(false, Tables.ID_ACTUALITES, mPubUri));
        getContext().sendBroadcast(DataService.getIntent(false, Tables.ID_ABONNEMENTS, mPubUri));
        getContext().unregisterReceiver(mOldReceiver);
    }
}
