package com.studio.artaban.leclassico.activities.event;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.LoggedActivity;
import com.studio.artaban.leclassico.activities.album.FullPhotoActivity;
import com.studio.artaban.leclassico.activities.profile.ProfileActivity;
import com.studio.artaban.leclassico.animations.InOutScreen;
import com.studio.artaban.leclassico.animations.RequestAnimation;
import com.studio.artaban.leclassico.components.RecyclerAdapter;
import com.studio.artaban.leclassico.connection.DataRequest;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.data.tables.PresentsTable;
import com.studio.artaban.leclassico.helpers.Database;
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

    private void updateUserPresence() { // Update DB user presence to the event
        Logs.add(Logs.Type.V, null);
        String pseudo = getIntent().getStringExtra(Login.EXTRA_DATA_PSEUDO);
        String where = PresentsTable.COLUMN_EVENT_ID + '=' + mEventId + " AND " +
                PresentsTable.COLUMN_PSEUDO + "='" + pseudo + '\'';

        Uri uri = Uri.parse(DataProvider.CONTENT_URI + PresentsTable.TABLE_NAME);
        synchronized (Database.getTable(PresentsTable.TABLE_NAME)) {

            // Check existing DB entry
            byte sync = Constants.NO_DATA;
            Cursor existingEntry = getContentResolver().query(uri,
                    new String[]{Constants.DATA_COLUMN_SYNCHRONIZED}, where, null, null);
            if (existingEntry.moveToFirst())
                sync = (byte) existingEntry.getInt(0);
            existingEntry.close();

            if (!mPresent) { ////// Mark user as present
                ContentValues values = new ContentValues();
                values.put(PresentsTable.COLUMN_EVENT_ID, mEventId);
                values.put(PresentsTable.COLUMN_PSEUDO, pseudo);

                DataTable.addSyncFields(values, DataTable.Synchronized.TO_INSERT.getValue());
                // NB: Always marked as "to insert" coz update not available for this table

                if (sync == Constants.NO_DATA) // Insert entry
                    getContentResolver().insert(uri, values);
                else // Update entry (previously marked as to delete)
                    getContentResolver().update(uri, values, where, null);

            } else ////// Mark user as not present
                getContentResolver().delete(uri, where, null); // Mark DB entry as "to delete"
        }
        mPresent = !mPresent;

        // Notify change on cursor URI
        getContentResolver().notifyChange(ContentUris.withAppendedId(mEventUri, mId), null);
    }

    private static final int DURATION_FAB_ANIMATION = 250; // in ms
    public void onEntry(View sender) { // Click event to update user present flag
        Logs.add(Logs.Type.V, "sender: " + sender);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final boolean present = mPresent;
        fab.setClickable(false);

        // Animate FAB
        InOutScreen.with(this)
                .setLocation(InOutScreen.Location.RIGHT)
                .setDuration(DURATION_FAB_ANIMATION)
                .addListener(new InOutScreen.OnInOutListener() {

                    @Override
                    public void onAnimationEnd() {
                        Logs.add(Logs.Type.V, null);
                        fab.setImageDrawable(getDrawable((present) ?
                                R.drawable.ic_person_add_white_36dp : R.drawable.ic_person_remove_white_36dp));

                        // Update DB
                        updateUserPresence();

                        InOutScreen.with(EventDisplayActivity.this)
                                .setLocation(InOutScreen.Location.RIGHT)
                                .setDuration(DURATION_FAB_ANIMATION)
                                .addListener(new InOutScreen.OnInOutListener() {

                                    @Override
                                    public void onAnimationEnd() {
                                        Logs.add(Logs.Type.V, null);
                                        fab.setClickable(true);
                                    }
                                })
                                .in(fab);
                    }
                })
                .out(fab);
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

                    Intent request = new Intent(DataService.REQUEST_OLD_DATA);
                    request.putExtra(DataRequest.EXTRA_DATA_DATE, mQueryDate);
                    EventDisplayActivity.this.sendBroadcast(DataService.getIntent(request,
                            Tables.ID_PRESENTS, mEventUri));
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
    private ScrollView mLayoutList; // Members list parent scroll view

    ////// OnCriteriaListener //////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCheckEntry(Cursor cursor) {
        return !cursor.isNull(COLUMN_INDEX_ENTRY_PSEUDO);
    }

    ////// LoggedActivity //////////////////////////////////////////////////////////////////////////
    @Override
    protected void onLoggedResume() {
        Logs.add(Logs.Type.V, null);

        // Register data service & more request receiver
        sendBroadcast(DataService.getIntent(true, Tables.ID_EVENEMENTS, mEventUri));
        sendBroadcast(DataService.getIntent(true, Tables.ID_PRESENTS, mEventUri));
        registerReceiver(mMoreReceiver, new IntentFilter(DataService.REQUEST_OLD_DATA));
    }

    //////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        if (onNotifyLoadFinished(id, cursor))
            return;
        if (!cursor.moveToFirst()) { // No more event to display (event removed)

            new AlertDialog.Builder(this)
                    .setIcon(getDrawable(R.drawable.warning_red))
                    .setTitle(getString(R.string.warning))
                    .setMessage(getString(R.string.event_removed))
                    .setCancelable(true)
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Logs.add(Logs.Type.V, "dialog: " + dialog);

                            setResult(Activity.RESULT_CANCELED);
                            finish(); // Stop activity
                        }
                    })
                    .show();

            return; // Activity stopped
        }
        if (id == Queries.EVENTS_DISPLAY) {
            mCursor = cursor;
            mEventId = cursor.getInt(COLUMN_INDEX_EVENT_ID);

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
            ((TextView) findViewById(R.id.text_info)).setText((!cursor.isNull(COLUMN_INDEX_REMARK))?
                    cursor.getString(COLUMN_INDEX_REMARK):getString(R.string.none));

            // Set start & end date
            DateFormat schedule = new SimpleDateFormat(Constants.FORMAT_DATE_TIME);
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
            DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
            try {
                Date fromDate = schedule.parse(cursor.getString(COLUMN_INDEX_DATE));
                ((TextView) findViewById(R.id.text_from))
                        .setText(dateFormat.format(fromDate) + ' ' + timeFormat.format(fromDate));

                Date toDate = schedule.parse(cursor.getString(COLUMN_INDEX_DATE_END));
                ((TextView) findViewById(R.id.text_to))
                        .setText(dateFormat.format(toDate) + ' ' + timeFormat.format(toDate));

            } catch (ParseException e) {
                Logs.add(Logs.Type.E, "Wrong start or end date format");
                ((TextView) findViewById(R.id.text_from)).setText(cursor.getString(COLUMN_INDEX_DATE));
                ((TextView) findViewById(R.id.text_to)).setText(cursor.getString(COLUMN_INDEX_DATE_END));
            }
            // Change FAB image according if connected user is marked as present...
            if (!mAdapter.isInitialized()) { // ...only at initialization (one call only)
                String pseudo = getIntent().getStringExtra(Login.EXTRA_DATA_PSEUDO);
                do {
                    if ((!cursor.isNull(COLUMN_INDEX_ENTRY_PSEUDO)) &&
                            (cursor.getString(COLUMN_INDEX_ENTRY_PSEUDO).compareTo(pseudo) == 0)) { // Present
                        ((FloatingActionButton) findViewById(R.id.fab))
                                .setImageDrawable(getDrawable(R.drawable.ic_person_remove_white_36dp));
                        mPresent = true;
                        break;
                    }

                } while (cursor.moveToNext());
                cursor.moveToFirst();

                // Set FAB ready to work
                findViewById(R.id.fab).setClickable(true);
            }
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
    private int mEventId; // Event ID (!= _id -> != mId)

    private short mQueryCount; // DB query result count
    private short mQueryLimit = Queries.PRESENTS_LIST_LIMIT; // DB query limit
    private String mQueryDate; // More query date displayed (visible)

    private String mPresentsLast; // Last present status date received (newest date)
    private boolean mPresent; // Event user present flag

    // Query column indexes
    private static final int COLUMN_INDEX_EVENT_ID = 0;
    private static final int COLUMN_INDEX_PSEUDO = 1;
    private static final int COLUMN_INDEX_NOM = 2;
    private static final int COLUMN_INDEX_DATE = 3;
    private static final int COLUMN_INDEX_DATE_END = 4;
    private static final int COLUMN_INDEX_LIEU = 5;
    private static final int COLUMN_INDEX_FLYER = 6;
    private static final int COLUMN_INDEX_REMARK = 7;
    private static final int COLUMN_INDEX_STATUS_DATE = 8;
    private static final int COLUMN_INDEX_SYNC = 9;
    private static final int COLUMN_INDEX_ENTRY_PSEUDO = 10;
    private static final int COLUMN_INDEX_ENTRY_DATE = 11; // Presents status date (used to sort entries)
    private static final int COLUMN_INDEX_ENTRY_PSEUDO_ID = 12;
    private static final int COLUMN_INDEX_ENTRY_SEX = 13;
    private static final int COLUMN_INDEX_ENTRY_PROFILE = 14;
    private static final int COLUMN_INDEX_ENTRY_PHONE = 15;
    private static final int COLUMN_INDEX_ENTRY_EMAIL = 16;
    private static final int COLUMN_INDEX_ENTRY_TOWN = 17;
    private static final int COLUMN_INDEX_ENTRY_NAME = 18;
    private static final int COLUMN_INDEX_ENTRY_ADDRESS = 19;
    private static final int COLUMN_INDEX_ENTRY_ADMIN = 20;

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

        // Update current display data
        if ((mCursor.getCount() != 1) || (!mCursor.isNull(COLUMN_INDEX_ENTRY_PSEUDO))) {
            String lastMember = mCursor.getString(COLUMN_INDEX_ENTRY_DATE);

            short count = (short) mCursor.getCount();
            mCursor.moveToLast(); // Using sort
            if (mCursor.isNull(COLUMN_INDEX_ENTRY_PSEUDO))
                --count; // Remove event info entry from member presence count
            mCursor.moveToFirst();

            if ((mPresentsLast != null) && (mPresentsLast.compareTo(lastMember) < 0) && (count > mQueryCount))
                mQueryLimit += count - mQueryCount; // New entries case

            mQueryCount = count;
            mPresentsLast = lastMember;
            Logs.add(Logs.Type.I, "Count: " + count + " - Last: " + lastMember);

            // Get last visible present member date (status date)
            int limit = mQueryLimit;
            do {
                if (mCursor.isNull(COLUMN_INDEX_ENTRY_PSEUDO))
                    break; // No more member present in result
                mQueryDate = mCursor.getString(COLUMN_INDEX_ENTRY_DATE);
                if (--limit == 0)
                    break; // Only visible item are concerned

            } while (mCursor.moveToNext());
            mCursor.moveToFirst();

        } else { // No member presents at event
            mPresentsLast = null;
            mQueryLimit = Queries.PRESENTS_LIST_LIMIT;
            mQueryCount = 0;
        }
        //////
        if (mAdapter.isInitialized()) { // Check if not the initial query
            Logs.add(Logs.Type.I, "Query update");

            ////// Update presents list
            mAdapter.getDataSource().swap(mAdapter, mCursor, mQueryLimit, this, null);

        } else {
            Logs.add(Logs.Type.I, "Initial query");

            ////// Fill presents list
            mAdapter.getDataSource().fill(mCursor, mQueryLimit, this);
        }
        mLayoutList.scrollTo(0, 0);
        mPresentsList.scrollToPosition(0);
        // NB: Always called even when updating coz needed to fix bug that occurs where only one
        //     entry is available but not appear
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
        mPresentsList.setHasFixedSize(true);
        mPresentsList.setNestedScrollingEnabled(false);

        mLayoutList = (ScrollView) findViewById(R.id.layout_members);

        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mLayoutList.getLayoutParams();
            params.height = screenSize.y - Tools.getStatusBarHeight(getResources()) -
                    Tools.getActionBarHeight(this) -
                    (getResources().getDimensionPixelSize(R.dimen.event_info_height) << 1) -
                    (getResources().getDimensionPixelSize(R.dimen.event_info_margin) << 1);
            mLayoutList.setLayoutParams(params);

        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLayoutList.getLayoutParams();
            params.height = screenSize.y - Tools.getStatusBarHeight(getResources()) - Tools.getActionBarHeight(this) -
                    (getResources().getDimensionPixelSize(R.dimen.event_info_height) * 5) -
                    getResources().getDimensionPixelSize(R.dimen.sync_height) -
                    (getResources().getDimensionPixelSize(R.dimen.event_info_margin) * 5);

            int navigationY = Tools.getNavigationBarHeight(getResources());
            if (navigationY > 0) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                params.height += navigationY;

                ScrollView.LayoutParams paramsList = (ScrollView.LayoutParams) mPresentsList.getLayoutParams();
                paramsList.setMargins(0, 0, 0, navigationY);
                mPresentsList.setLayoutParams(paramsList);
            }
            mLayoutList.setLayoutParams(params);
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
            menu.findItem(R.id.mnu_full_screen).setVisible(false);
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

        // Unregister data service & more request receiver
        sendBroadcast(DataService.getIntent(false, Tables.ID_EVENEMENTS, mEventUri));
        sendBroadcast(DataService.getIntent(false, Tables.ID_PRESENTS, mEventUri));
        unregisterReceiver(mMoreReceiver);
    }
}
