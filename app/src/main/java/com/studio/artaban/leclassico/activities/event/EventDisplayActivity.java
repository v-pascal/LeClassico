package com.studio.artaban.leclassico.activities.event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.LoggedActivity;
import com.studio.artaban.leclassico.activities.album.FullPhotoActivity;
import com.studio.artaban.leclassico.activities.profile.ProfileActivity;
import com.studio.artaban.leclassico.animations.RequestAnimation;
import com.studio.artaban.leclassico.components.RecyclerAdapter;
import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;
import com.studio.artaban.leclassico.helpers.Storage;
import com.studio.artaban.leclassico.services.DataService;
import com.studio.artaban.leclassico.tools.SyncValue;
import com.studio.artaban.leclassico.tools.Tools;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pascal on 05/02/17.
 * Event display activity
 */
public class EventDisplayActivity extends LoggedActivity implements
        RecyclerAdapter.DataView.OnCriteriaListener {

    // Extra data keys (see 'LoggedActivity' & 'Login' extra data keys)

    public void onEntry(View sender) { // Click event to set user present flag
        Logs.add(Logs.Type.V, "sender: " + sender);







    }

    public static SpannableStringBuilder getHourly(Context context, String from, String to) {
    // Return formatted string with from & to date info (date & time)

        //Logs.add(Logs.Type.V, "context: " + context + ";from: " + from + ";to: " + to);
        SpannableStringBuilder hourly = new SpannableStringBuilder();
        DateFormat selected = new SimpleDateFormat(Constants.FORMAT_DATE_TIME);

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        try {
            Date startDate = selected.parse(from);
            hourly.append(context.getString(R.string.from, dateFormat.format(startDate)));
            hourly.append(' ');
            hourly.append(timeFormat.format(startDate));

        } catch (ParseException e) {
            Logs.add(Logs.Type.E, "Wrong start date & time format: " + from);
            hourly.append(context.getString(R.string.from, from));
        }
        hourly.append(' ');
        hourly.setSpan(new StyleSpan(Typeface.BOLD), 0,
                context.getResources().getInteger(R.integer.from_pos), 0);
        int endPos = hourly.length();
        try {
            Date endDate = selected.parse(to);
            hourly.append(context.getString(R.string.to, dateFormat.format(endDate)));
            hourly.append(' ');
            hourly.append(timeFormat.format(endDate));

        } catch (ParseException e) {
            Logs.add(Logs.Type.E, "Wrong end date & time format: " + to);
            hourly.append(context.getString(R.string.to, to));
        }
        hourly.setSpan(new StyleSpan(Typeface.BOLD), endPos,
                endPos + context.getResources().getInteger(R.integer.to_pos), 0);
        return hourly;
    }

    //////
    private final PresentsRecyclerAdapter mAdapter = new PresentsRecyclerAdapter();
    private class PresentsRecyclerAdapter extends RecyclerAdapter {

        private RequestAnimation mRequestAnim; // Request more management (animation + event)
        public PresentsRecyclerAdapter() {
            super(R.layout.layout_presents_member_item, R.layout.layout_more_request_presents,
                    true, COLUMN_INDEX_ENTRY_PSEUDO_ID);
        }

        ////// View.OnClickListener ////////////////////////////////////////////////////////////////
        @Override
        public void onClick(View sender) {
            Logs.add(Logs.Type.V, "sender: " + sender);
            switch (sender.getId()) {

                case R.id.layout_pseudo:
                case R.id.image_pseudo: { // Display profile
                    int pseudoId = (int) sender.getTag(R.id.tag_pseudo_id);
                    Logs.add(Logs.Type.V, "Display profile #" + pseudoId);

                    ////// Start profile activity
                    Intent profile = new Intent(EventDisplayActivity.this, ProfileActivity.class);
                    profile.putExtra(LoggedActivity.EXTRA_DATA_ID, pseudoId);
                    startActivity(profile);
                    break;
                }
                case R.id.layout_more: { // More data requested
                    Logs.add(Logs.Type.I, "More present members requested");

                    ////// Request more present members to remote DB
                    mQueryLimit += Queries.PRESENTS_MORE_LIMIT;
                    mAdapter.setRequesting(RequestFlag.IN_PROGRESS);



                    /*
                    Intent request = new Intent(DataService.REQUEST_OLD_DATA);
                    request.putExtra(DataRequest.EXTRA_DATA_DATE, mQueryDate);
                    getActivity().sendBroadcast(DataService.getIntent(request,
                            Tables.ID_COMMENTAIRES, mComUri));
                            */
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
                    mRequestAnim = new RequestAnimation(EventDisplayActivity.this, false);

                mRequestAnim.display(getResources(), this, holder.requestView);
                return;
            }
            ////// Presents member
            ((TextView)holder.rootView.findViewById(R.id.text_pseudo))
                    .setText(mDataSource.getString(position, COLUMN_INDEX_ENTRY_PSEUDO));
            if (!mDataSource.isNull(position, COLUMN_INDEX_ENTRY_PHONE))
                ((TextView) holder.rootView.findViewById(R.id.text_info))
                        .setText(mDataSource.getString(position, COLUMN_INDEX_ENTRY_PHONE));
            else if (!mDataSource.isNull(position, COLUMN_INDEX_ENTRY_EMAIL))
                ((TextView) holder.rootView.findViewById(R.id.text_info))
                        .setText(mDataSource.getString(position, COLUMN_INDEX_ENTRY_EMAIL));
            else if (!mDataSource.isNull(position, COLUMN_INDEX_ENTRY_NAME))
                ((TextView) holder.rootView.findViewById(R.id.text_info))
                        .setText(mDataSource.getString(position, COLUMN_INDEX_ENTRY_NAME));
            else if (!mDataSource.isNull(position, COLUMN_INDEX_ENTRY_TOWN))
                ((TextView) holder.rootView.findViewById(R.id.text_info))
                        .setText(mDataSource.getString(position, COLUMN_INDEX_ENTRY_TOWN));
            else if (!mDataSource.isNull(position, COLUMN_INDEX_ENTRY_ADDRESS))
                ((TextView) holder.rootView.findViewById(R.id.text_info))
                        .setText(mDataSource.getString(position, COLUMN_INDEX_ENTRY_ADDRESS));
            else
                ((TextView) holder.rootView.findViewById(R.id.text_info))
                        .setText(getString((mDataSource.getInt(position, COLUMN_INDEX_ENTRY_ADMIN) == 1) ?
                                R.string.admin_privilege : R.string.no_admin_privilege));

            // Set pseudo icon
            boolean female = (!mDataSource.isNull(position, COLUMN_INDEX_ENTRY_SEX)) &&
                    (mDataSource.getInt(position, COLUMN_INDEX_ENTRY_SEX) == CamaradesTable.GENDER_FEMALE);
            String profile = (!mDataSource.isNull(position, COLUMN_INDEX_ENTRY_PROFILE)) ?
                    mDataSource.getString(position, COLUMN_INDEX_ENTRY_PROFILE) : null;
            ImageView icon = (ImageView) holder.rootView.findViewById(R.id.image_pseudo);
            Tools.setProfile(EventDisplayActivity.this, icon, female, profile, R.dimen.user_item_height, true);

            ////// Events
            int pseudoId = mDataSource.getInt(position, COLUMN_INDEX_ENTRY_PSEUDO_ID);
            View layout = holder.rootView.findViewById(R.id.layout_pseudo);

            layout.setTag(R.id.tag_pseudo_id, pseudoId);
            layout.setOnClickListener(this);
            icon.setTag(R.id.tag_pseudo_id, pseudoId);
            icon.setOnClickListener(this);
        }
    }
    private RecyclerView mPresentsList; // Recycler view containing present members list

    ////// OnCriteriaListener //////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCheckEntry(Cursor cursor) {
        return !cursor.isNull(COLUMN_INDEX_ENTRY_PSEUDO);
    }

    ////// LoggedActivity //////////////////////////////////////////////////////////////////////////
    @Override
    protected void onLoggedResume() {
        Logs.add(Logs.Type.V, null);

        // Register more request receiver
        registerReceiver(mMoreReceiver, new IntentFilter(DataService.REQUEST_OLD_DATA));
    }

    //////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        if ((!cursor.moveToFirst()) || (onNotifyLoadFinished(id, cursor)))
            return;

        if (id == Queries.EVENTS_DISPLAY) {
            mCursor = cursor;

            // Set event info
            ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar))
                    .setTitle(cursor.getString(COLUMN_INDEX_NOM));
            if (!cursor.isNull(COLUMN_INDEX_FLYER)) {
                Bitmap bmpFlyer = BitmapFactory.decodeFile(Storage.get() + Storage.FOLDER_FLYERS +
                            File.separator + cursor.getString(COLUMN_INDEX_FLYER));
                if (bmpFlyer != null)
                    ((ImageView) findViewById(R.id.image_flyer)).setImageBitmap(bmpFlyer);
                else
                    Logs.add(Logs.Type.E, "Failed to open flyer: " + cursor.getString(COLUMN_INDEX_FLYER));
            }
            ((TextView) findViewById(R.id.text_location)).setText(cursor.getString(COLUMN_INDEX_LIEU));
            ((TextView) findViewById(R.id.text_hourly))
                    .setText(getHourly(this, cursor.getString(COLUMN_INDEX_DATE), cursor.getString(COLUMN_INDEX_DATE_END)));
            if (!cursor.isNull(COLUMN_INDEX_REMARK))
                ((TextView) findViewById(R.id.text_info)).setText(cursor.getString(COLUMN_INDEX_REMARK));

            // Set synchronization
            Tools.setSyncView(EventDisplayActivity.this, (TextView) findViewById(R.id.text_sync_date),
                    (ImageView) findViewById(R.id.image_sync), cursor.getString(COLUMN_INDEX_STATUS_DATE),
                    (byte) cursor.getInt(COLUMN_INDEX_SYNC));

            // Set event author link
            final SyncValue<Integer> pseudoId = new SyncValue<>(Constants.NO_DATA);
            do {
                if (cursor.isNull(COLUMN_INDEX_ENTRY_PSEUDO)) {
                    pseudoId.set(cursor.getInt(COLUMN_INDEX_ENTRY_PSEUDO_ID));
                    break; // Author info found (ID)
                }

            } while (cursor.moveToNext());
            cursor.moveToFirst();

            TextView author = (TextView) findViewById(R.id.text_author);
            author.setText(cursor.getString(COLUMN_INDEX_PSEUDO));
            if (pseudoId.get() != Constants.NO_DATA) {

                author.setLinkTextColor(Color.BLUE); // NB: Needed coz unexpected link color displayed
                Linkify.addLinks(author, Pattern.compile('^' + cursor.getString(COLUMN_INDEX_PSEUDO) + '$', 0),
                        Constants.DATA_CONTENT_SCHEME, null, new Linkify.TransformFilter() {
                            @Override
                            public String transformUrl(Matcher match, String url) {
                                Logs.add(Logs.Type.V, "match: " + match + ";url: " + url);

                                // content://com.studio.artaban.provider.leclassico/User/#/Profile
                                return Uris.getUri(Uris.ID_USER_PROFILE, String.valueOf(pseudoId.get())).toString();
                            }
                        });

            } else { // Author member not found in members table (no link available)
                Logs.add(Logs.Type.E, "Invalid data result");
                author.setTextColor(Color.BLUE);
            }
            refresh(); // Display presents list
        }
    }

    @Override
    public void onLoaderReset() {
        Logs.add(Logs.Type.V, null);
        mCursor = null;
    }

    //////
    private final QueryLoader mEventLoader = new QueryLoader(this, this); // Event query loader
    private Uri mEventUri; // Event query URI

    private short mQueryCount = Constants.NO_DATA; // DB query result count
    private short mQueryLimit = Queries.PRESENTS_LIST_LIMIT; // DB query limit

    // Query column indexes
    private static final int COLUMN_INDEX_PSEUDO = 0;
    private static final int COLUMN_INDEX_NOM = 1;
    private static final int COLUMN_INDEX_DATE = 2;
    private static final int COLUMN_INDEX_DATE_END = 3;
    private static final int COLUMN_INDEX_LIEU = 4;
    private static final int COLUMN_INDEX_FLYER = 5;
    private static final int COLUMN_INDEX_REMARK = 6;
    private static final int COLUMN_INDEX_STATUS_DATE = 7;
    private static final int COLUMN_INDEX_SYNC = 8;
    private static final int COLUMN_INDEX_ENTRY_PSEUDO = 9;
    private static final int COLUMN_INDEX_ENTRY_PSEUDO_ID = 10;
    private static final int COLUMN_INDEX_ENTRY_SEX = 11;
    private static final int COLUMN_INDEX_ENTRY_PROFILE = 12;
    private static final int COLUMN_INDEX_ENTRY_PHONE = 13;
    private static final int COLUMN_INDEX_ENTRY_EMAIL = 14;
    private static final int COLUMN_INDEX_ENTRY_TOWN = 15;
    private static final int COLUMN_INDEX_ENTRY_NAME = 16;
    private static final int COLUMN_INDEX_ENTRY_ADDRESS = 17;
    private static final int COLUMN_INDEX_ENTRY_ADMIN = 18;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final MoreReceiver mMoreReceiver = new MoreReceiver(); // More data broadcast receiver
    private class MoreReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Logs.add(Logs.Type.V, "context: " + context + ";intent: " + intent);
            if ((intent.getAction().equals(DataService.REQUEST_OLD_DATA)) && // More request
                    (intent.getBooleanExtra(DataService.EXTRA_DATA_RECEIVED, false)) && // Received
                    (((Uri) intent.getParcelableExtra(DataRequest.EXTRA_DATA_URI)) // Event URI
                            .compareTo(mEventUri) == 0)) {

                switch ((DataRequest.Result)intent.getSerializableExtra(DataService.EXTRA_DATA_REQUEST_RESULT)) {
                    case NOT_FOUND: { // More entries not found
                        if (mQueryCount > (mQueryLimit - Queries.PRESENTS_MORE_LIMIT))
                            refresh(); // No more old remote DB presents member but existing in local DB
                        else
                            mQueryLimit -= Queries.PRESENTS_MORE_LIMIT;
                        //break;
                    }
                    case FOUND: { // More entries found
                        mAdapter.setRequesting(RecyclerAdapter.RequestFlag.DISPLAYED);
                        // DB table update will notify cursor (no need to call refresh)
                        break;
                    }
                    case NO_MORE: { // No more entries into remote DB
                        mAdapter.setRequesting(RecyclerAdapter.RequestFlag.HIDDEN);
                        break;
                    }
                }
            }
        }
    }

    private void refresh() { // Display/Refresh presents list
        Logs.add(Logs.Type.V, null);
        mCursor.moveToFirst();







        /*
        // Update current display data
        String lastPub = mComCursor.getString(COLUMN_INDEX_DATE);
        short count = (short) mComCursor.getCount();
        if ((mQueryCount != Constants.NO_DATA) && (mComLast.compareTo(lastPub) != 0))
            mQueryLimit += count - mQueryCount; // New entries case (from remote DB)

        mQueryCount = count;
        mComLast = lastPub;

        // Get last visible publication date
        int limit = mQueryLimit;
        do {
            mQueryDate = mComCursor.getString(COLUMN_INDEX_DATE);
            if (--limit == 0)
                break; // Only visible item are concerned

        } while (mComCursor.moveToNext());
        mComCursor.moveToFirst();
        */




        //////
        if (mAdapter.isInitialized()) { // Check if not the initial query
            Logs.add(Logs.Type.I, "Query update");

            ////// Update presents list
            mAdapter.getDataSource().swap(mAdapter, mCursor, mQueryLimit, this, null);

        } else {
            Logs.add(Logs.Type.I, "Initial query");

            ////// Fill presents list
            mAdapter.getDataSource().fill(mCursor, mQueryLimit, this);
            mPresentsList.scrollToPosition(0);
        }








    }

    ////// AppCompatActivity ///////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
        setContentView(R.layout.activity_event_display);

        // Set toolbar & default title
        Toolbar toolbar =   (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar)).setTitle(getString(R.string.title_activity_event));

        // Get event ID
        mId = getIntent().getIntExtra(EXTRA_DATA_ID, Constants.NO_DATA);
        Logs.add(Logs.Type.I, "Event #" + mId);

        // Set URI to observe DB changes
        mEventUri = Uris.getUri(Uris.ID_MAIN_EVENTS);

        // Load event info
        Bundle eventData = new Bundle();
        eventData.putParcelable(QueryLoader.DATA_KEY_URI, mEventUri);
        eventData.putLong(QueryLoader.DATA_KEY_URI_FILTER, mId);
        mEventLoader.init(this, Queries.EVENTS_DISPLAY, eventData);

        // Set members list
        LinearLayoutManager linearManager = new LinearLayoutManager(this);
        linearManager.setOrientation(LinearLayoutManager.VERTICAL);

        mPresentsList = (RecyclerView) findViewById(R.id.list_members);
        mPresentsList.setLayoutManager(linearManager);
        mPresentsList.setItemAnimator(new DefaultItemAnimator());
        mPresentsList.setAdapter(mAdapter);

        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPresentsList.getLayoutParams();
            params.height = screenSize.y - Tools.getStatusBarHeight(getResources()) - Tools.getActionBarHeight(this) -
                    (getResources().getDimensionPixelSize(R.dimen.event_info_height) << 1) -
                    (getResources().getDimensionPixelSize(R.dimen.event_info_margin) * 3);
            mPresentsList.setLayoutParams(params);

        } else {





        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Logs.add(Logs.Type.V, "menu: " + menu);
        getMenuInflater().inflate(R.menu.menu_event_display, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Logs.add(Logs.Type.V, "menu: " + menu);
        if ((mCursor != null) && (mCursor.isNull(COLUMN_INDEX_FLYER)))
            menu.findItem(R.id.mnu_notification).setVisible(false);
            // NB: No flyer to display in full screen mode

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logs.add(Logs.Type.V, "item: " + item);
        if (onNotifyItemSelected(item))
            return true; // Display notifications

        switch (item.getItemId()) {
            case android.R.id.home: { // Back to previous activity

                supportFinishAfterTransition();
                return true;
            }
            case R.id.mnu_full_screen: { // Display flyer in full screen
                if (mCursor == null)
                    break;

                ////// Start full screen photo activity
                Intent fullScreen = new Intent(this, FullPhotoActivity.class);
                fullScreen.putExtra(FullPhotoActivity.EXTRA_DATA_TITLE, mCursor.getString(COLUMN_INDEX_NOM));
                fullScreen.putExtra(FullPhotoActivity.EXTRA_DATA_NAME, mCursor.getString(COLUMN_INDEX_FLYER));
                fullScreen.putExtra(FullPhotoActivity.EXTRA_DATA_FLYER, true);
                Login.copyExtraData(getIntent(), fullScreen);

                startActivity(fullScreen);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logs.add(Logs.Type.V, null);

        // Unregister old request receiver
        unregisterReceiver(mMoreReceiver);
    }
}
