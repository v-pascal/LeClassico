package com.studio.artaban.leclassico.activities.notification;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.LoggedActivity;
import com.studio.artaban.leclassico.animations.RecyclerItemAnimator;
import com.studio.artaban.leclassico.components.RecyclerAdapter;
import com.studio.artaban.leclassico.connection.ServiceNotify;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.IDataTable;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.tables.ActualitesTable;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.data.tables.CommentairesTable;
import com.studio.artaban.leclassico.data.tables.MessagerieTable;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.data.tables.PhotosTable;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;
import com.studio.artaban.leclassico.tools.Tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by pascal on 29/10/16.
 * Notification activity class
 */
public class NotifyActivity extends LoggedActivity implements QueryLoader.OnResultListener {

    public void onRead(View sender) { // Mark unread notification(s) as read

        Logs.add(Logs.Type.V, "sender: " + sender);
        Tools.startProcess(this, new Tools.OnProcessListener() {
            @Override
            public Bundle onBackgroundTask() {
                Logs.add(Logs.Type.V, null);

                ContentValues values = new ContentValues();
                values.put(NotificationsTable.COLUMN_LU_FLAG, Constants.DATA_READ);
                getContentResolver().update(Uri.parse(DataProvider.CONTENT_URI + NotificationsTable.TABLE_NAME),
                        values, NotificationsTable.COLUMN_PSEUDO + "='" +
                                getIntent().getStringExtra(Login.EXTRA_DATA_PSEUDO) +
                                "' AND " + NotificationsTable.COLUMN_LU_FLAG + '=' + Constants.DATA_UNREAD,
                        null);
                return null;
            }

            @Override
            public void onMainNextTask(Bundle backResult) {
                Logs.add(Logs.Type.V, "backResult: " + backResult);

                // Notify service & notifications URI to refresh notification list
                ServiceNotify.update(NotifyActivity.this, 0);
                getContentResolver().notifyChange(ContentUris.withAppendedId((Uri) getIntent()
                        .getParcelableExtra(Login.EXTRA_DATA_NOTIFY_URI), 0), null);
                        // NB: The 0 appended to URI above permits to avoid multiple 'onChange' method call
            }
        });
    }

    //////
    private void setNewNotifyInfo(int newNotify) { // Display new notification info
        Logs.add(Logs.Type.V, "newNotify: " + newNotify);
        SpannableStringBuilder info =
                new SpannableStringBuilder(getString(R.string.service_new_notify, newNotify));

        info.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, String.valueOf(newNotify).length(), 0);
        ((TextView)findViewById(R.id.text_new_notify)).setText(info, TextView.BufferType.SPANNABLE);
    }
    private SpannableStringBuilder getNotifyMessage(char type, short wallType, String pseudo, int color) {
        // Return notification message formatted according its type

        Logs.add(Logs.Type.V, "type: " + type + ";wallType: " + wallType + ";pseudo: " + pseudo +
                ";color: " + color);
        SpannableStringBuilder message = null;
        switch (type) {
            case NotificationsTable.TYPE_SHARED: { ////// Photo added (into shared album)

                message = new SpannableStringBuilder(getString(R.string.notify_shared_message, pseudo));
                int pseudoPos = getResources().getInteger(R.integer.notify_shared_message_pseudo_pos);
                message.setSpan(new ForegroundColorSpan(getResources().getColor(color)),
                        pseudoPos, pseudoPos + pseudo.length(), 0);
                break;
            }
            case NotificationsTable.TYPE_WALL: { ////// Wall publication

                message = new SpannableStringBuilder(getString(R.string.notify_wall_message, pseudo,
                        getResources().getStringArray(R.array.notify_wall_types)[wallType]));
                int pseudoPos = getResources().getInteger(R.integer.notify_wall_message_pseudo_pos);
                message.setSpan(new ForegroundColorSpan(getResources().getColor(color)),
                        pseudoPos, pseudoPos + pseudo.length(), 0);
                break;
            }
            case NotificationsTable.TYPE_MAIL: { ////// Mail received

                message = new SpannableStringBuilder(getString(R.string.notify_mail_message, pseudo));
                int pseudoPos = getResources().getInteger(R.integer.notify_mail_pseudo_pos);
                message.setSpan(new ForegroundColorSpan(getResources().getColor(color)),
                        pseudoPos, pseudoPos + pseudo.length(), 0);
                break;
            }
            case NotificationsTable.TYPE_PUB_COMMENT:
            case NotificationsTable.TYPE_PIC_COMMENT: { ////// Comment added (onto publication or photo)

                int typeIdx = (type == NotificationsTable.TYPE_PUB_COMMENT) ? 0 : 1;
                message = new SpannableStringBuilder(getString(R.string.notify_comment_message, pseudo,
                        getResources().getStringArray(R.array.notify_comment_types)[typeIdx]));
                int pseudoPos = getResources().getInteger(R.integer.notify_comment_pseudo_pos);
                message.setSpan(new ForegroundColorSpan(getResources().getColor(color)),
                        pseudoPos, pseudoPos + pseudo.length(), 0);
                break;
            }
        }
        return message;
    }

    private int mFabTransY = Constants.NO_DATA; // Vertical floating action button translation
    private void hideFab(final ViewPropertyAnimatorCompat animation) {
        // Hide floating action button (with translation)

        Logs.add(Logs.Type.V,  "animation: " + animation);
        animation.setDuration(RecyclerItemAnimator.DEFAULT_DURATION)
                .translationY(mFabTransY)
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {

                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        animation.setListener(null);
                    }

                    @Override
                    public void onAnimationCancel(View view) {
                        ViewCompat.setTranslationY(view, mFabTransY);
                    }
                })
                .start();
    }

    private RecyclerView mNotifyList; // Recycler view containing notification list
    private NotifyRecyclerViewAdapter mNotifyAdapter; // Recycler view adapter (with cursor management)

    //////
    private class NotifyRecyclerViewAdapter extends RecyclerAdapter implements View.OnClickListener {
        public NotifyRecyclerViewAdapter(@LayoutRes int layout, int key) {
            super(layout, key);
        }

        private void displayDate(TextView viewDate, String date) {
            // Display the notification date separator (with notification date formatted as locale user)

            //Logs.add(Logs.Type.V, "viewDate: " + viewDate + ";date: " + date);
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.FORMAT_DATE_TIME);
            try {
                Date notifyDate = dateFormat.parse(date);
                DateFormat user = android.text.format.DateFormat.getMediumDateFormat(NotifyActivity.this);
                String userDate = user.format(notifyDate);
                viewDate.setText((userDate.compareTo(user.format(new Date())) != 0) ?
                        userDate : getResources().getString(R.string.today));

            } catch (ParseException e) {
                Logs.add(Logs.Type.E, "Wrong notification date: " + date);
            }
        }
        private int displayReadFlag(boolean unread, View root, ImageView type, TextView time) {
            //Logs.add(Logs.Type.V, "unread: " + unread + ";root: " + root + ";type: " + type +
            //        ";time: " + time);
            if (unread) {

                // Unread notification
                root.findViewById(R.id.layout_data)
                        .setBackground(getResources().getDrawable(R.drawable.notify_unread_background));
                type.setColorFilter(Color.RED);
                ((TextView) root.findViewById(R.id.text_message)).setTypeface(Typeface.DEFAULT_BOLD);
                time.setTypeface(Typeface.DEFAULT_BOLD);
                time.setTextColor(Color.RED);
                return R.color.red;
            }

            // Read notification
            root.findViewById(R.id.layout_data)
                    .setBackground(getResources().getDrawable(R.drawable.notify_read_background));
            type.setColorFilter(Color.BLACK);
            ((TextView) root.findViewById(R.id.text_message)).setTypeface(Typeface.DEFAULT);
            time.setTypeface(Typeface.DEFAULT);
            time.setTextColor(getResources().getColor(R.color.orange));
            return R.color.colorPrimaryProfile;
        }

        ////// View.OnClickListener ////////////////////////////////////////////////////////////////
        @Override
        public void onClick(View sender) {
            Logs.add(Logs.Type.V, "sender: " + sender);

            int position = (int)sender.getTag(R.id.tag_position);
            switch (sender.getId()) {

                case R.id.image_pseudo: {
                    Logs.add(Logs.Type.I, "Pseudo #" + position + " selected");




                    break;
                }
                case R.id.layout_data: {
                    Logs.add(Logs.Type.I, "Notification #" + position + " selected");




                    break;
                }
            }
        }

        //////
        @Override
        public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
            //Logs.add(Logs.Type.V, "holder: " + holder + ";position: " + position);

            // Set notification date
            if (position == 0) {
                displayDate((TextView) holder.rootView.findViewById(R.id.text_date),
                        mDataSource.getString(position, COLUMN_INDEX_DATE));
                holder.rootView.findViewById(R.id.date_separator).setVisibility(View.VISIBLE);

            } else {
                String prevDate = mDataSource.getString(position - 1, COLUMN_INDEX_DATE).substring(0, 10); // YYYY-MM-DD
                String curDate = mDataSource.getString(position, COLUMN_INDEX_DATE);

                if (prevDate.compareTo(curDate.substring(0, 10)) != 0) { // Display the different date (separator)
                    displayDate((TextView) holder.rootView.findViewById(R.id.text_date), curDate);
                    holder.rootView.findViewById(R.id.date_separator).setVisibility(View.VISIBLE);
                } else
                    holder.rootView.findViewById(R.id.date_separator).setVisibility(View.GONE);
            }

            // Set unread notification display (if the case)
            ImageView notifyType = (ImageView) holder.rootView.findViewById(R.id.image_type);
            TextView textTime = (TextView) holder.rootView.findViewById(R.id.text_time);
            int pseudoColor = displayReadFlag(
                    mDataSource.getInt(position, COLUMN_INDEX_LU_FLAG) == Constants.DATA_UNREAD,
                    holder.rootView, notifyType, textTime);

            // Set from pseudo icon
            boolean female = (!mDataSource.isNull(position, COLUMN_INDEX_SEX)) &&
                    (mDataSource.getInt(position, COLUMN_INDEX_SEX) == CamaradesTable.FEMALE);
            String profile = (!mDataSource.isNull(position, COLUMN_INDEX_PROFILE)) ?
                    mDataSource.getString(position, COLUMN_INDEX_PROFILE) : null;
            Tools.setProfile(NotifyActivity.this, (ImageView) holder.rootView.findViewById(R.id.image_pseudo),
                    female, profile, R.dimen.shortcut_content_height, true);

            // Set notification message & info
            char type = mDataSource.getString(position, COLUMN_INDEX_OBJECT_TYPE).charAt(0);

            SpannableStringBuilder message = getNotifyMessage(type,
                    Tools.getNotifyWallType(mDataSource, position, COLUMN_INDEX_LINK, COLUMN_INDEX_FICHIER),
                    mDataSource.getString(position, COLUMN_INDEX_PSEUDO), pseudoColor);
            SpannableStringBuilder info = null;
            switch (type) {

                case NotificationsTable.TYPE_SHARED: { ////// Photo added (into shared album)

                    String album = mDataSource.getString(position, COLUMN_INDEX_ALBUM);
                    info = new SpannableStringBuilder(getString(R.string.notify_shared_info, album));
                    int albumPos = getResources().getInteger(R.integer.notify_shared_info_album_pos);
                    info.setSpan(new ForegroundColorSpan(Color.BLUE), albumPos, albumPos + album.length(), 0);
                    break;
                }
                case NotificationsTable.TYPE_WALL: { ////// Wall publication

                    info = new SpannableStringBuilder((!mDataSource.isNull(position, COLUMN_INDEX_PUB_TEXT)) ?
                            mDataSource.getString(position, COLUMN_INDEX_PUB_TEXT).replaceAll("\\s{2,}", " ") :
                            getString(R.string.notify_no_info));
                    break;
                }
                case NotificationsTable.TYPE_MAIL: { ////// Mail received

                    info = (SpannableStringBuilder) Html.fromHtml(mDataSource.getString(position, COLUMN_INDEX_MSG_TEXT)
                            .replaceAll("\\s{2,}", " "));
                    break;
                }
                case NotificationsTable.TYPE_PUB_COMMENT:
                case NotificationsTable.TYPE_PIC_COMMENT: { ////// Comment added (onto publication or photo)

                    info = new SpannableStringBuilder(mDataSource.getString(position, COLUMN_INDEX_COM_TEXT));
                    break;
                }
            }
            ((TextView) holder.rootView.findViewById(R.id.text_message)).setText(message, TextView.BufferType.SPANNABLE);
            ((TextView) holder.rootView.findViewById(R.id.text_info)).setText(info, TextView.BufferType.SPANNABLE);

            // Set notification type icon & time
            notifyType.setImageDrawable(getResources().getDrawable(Tools.getNotifyIcon(type)));
            textTime.setText(mDataSource.getString(position, COLUMN_INDEX_DATE).substring(11, 16));

            // Set notify synchronization
            Tools.setSyncView(NotifyActivity.this, (TextView) holder.rootView.findViewById(R.id.text_sync_date),
                    (ImageView) holder.rootView.findViewById(R.id.image_sync),
                    mDataSource.getString(position, COLUMN_INDEX_STATUS_DATE),
                    (byte) mDataSource.getInt(position, COLUMN_INDEX_SYNC));

            // Events
            View layoutData = holder.rootView.findViewById(R.id.layout_data);
            View imagePseudo = holder.rootView.findViewById(R.id.image_pseudo);

            layoutData.setTag(R.id.tag_position, position);
            layoutData.setOnClickListener(this);
            imagePseudo.setTag(R.id.tag_position, position);
            imagePseudo.setOnClickListener(this);

            // Add margin bottom at last item position
            ((RecyclerView.LayoutParams)holder.itemView.getLayoutParams()).bottomMargin =
                    (position == (mDataSource.getCount() - 1))?
                            getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin):0;

            ////// Animate item appearance
            animateAppearance(holder, new AppearanceAnimatorMaker() {
                @Override
                public void onAnimate(View item) {
                    //Logs.add(Logs.Type.V, "item: " + item);

                    ViewCompat.animate(item).cancel();
                    ViewCompat.setPivotX(item, item.getWidth());
                    ViewCompat.setScaleX(item, 0.5f);
                    ViewCompat.setAlpha(item, 0.5f);

                    final ViewPropertyAnimatorCompat animation = ViewCompat.animate(item);
                    animation.setDuration(RecyclerItemAnimator.DEFAULT_DURATION >> 1)
                            .scaleX(1)
                            .alpha(1)
                            .setListener(new ViewPropertyAnimatorListener() {
                                @Override
                                public void onAnimationStart(View view) {

                                }

                                @Override
                                public void onAnimationEnd(View view) {
                                    animation.setListener(null);
                                    ViewCompat.setScaleX(view, 1);
                                    ViewCompat.setAlpha(view, 1);
                                }

                                @Override
                                public void onAnimationCancel(View view) {

                                }
                            })
                            .start();
                }

                @Override
                public void onCancel(View item) {
                    Logs.add(Logs.Type.V, "item: " + item);
                    ViewCompat.animate(item).cancel();
                }
            });
        }
    }

    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, final Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);

        if (!cursor.moveToFirst()) return;
        switch (id) {

            case Queries.MAIN_NOTIFY_LIST: {

                mQueryCount = (short)cursor.getCount();
                final View fab = findViewById(R.id.fab);

                // Get if exists unread notification
                int newNotify = 0;
                do {
                    if (cursor.getInt(COLUMN_INDEX_LU_FLAG) == Constants.DATA_UNREAD)
                        ++newNotify; // new notification found (unread)
                    else
                        break;

                } while (cursor.moveToNext());
                cursor.moveToFirst();
                setNewNotifyInfo(newNotify);

                // Check if not the initial query
                if (mNotifyAdapter != null) {
                    Logs.add(Logs.Type.I, "Query update");

                    ////// Update notification list
                    if (mNotifyList.getAdapter() == null)
                        mNotifyList.setAdapter(mNotifyAdapter);
                    RecyclerAdapter.SwapResult swapResult =
                            mNotifyAdapter.getDataSource().swap(mNotifyAdapter, cursor,
                                    new RecyclerAdapter.DataView.OnNotifyChangeListener() {
                                @Override
                                public void onRemoved(ArrayList<ArrayList<Object>> newData, int start, int count) {

                                }

                                @Override
                                public void onInserted(ArrayList<ArrayList<Object>> newData, int start, int count) {
                                    Logs.add(Logs.Type.V, "newData: " + newData + ";start: " + start +
                                            ";count: " + count);
                                    int followed = start + count;

                                    // Notify item that follows last inserted notification if needed
                                    // NB: If notification day is same as last insertion (update day separator)
                                    if ((newData.size() > count) && (((String)newData.get(followed)
                                            .get(COLUMN_INDEX_DATE)).compareTo(((String) newData.get(followed - 1)
                                            .get(COLUMN_INDEX_DATE)).substring(0, 10)) == 0))
                                        mNotifyAdapter.notifyItemChanged(followed);
                                }

                                @Override
                                public void onMoved(ArrayList<ArrayList<Object>> newData, int prevPos, int newPos) {

                                }
                            });

                    // Check if only change notification(s) on synchronization fields
                    boolean syncFieldsOnly = swapResult.countChanged > 0;
                    for (int i = 0; i < swapResult.columnChanged.size(); ++i) {
                        if ((swapResult.columnChanged.get(i) != COLUMN_INDEX_SYNC) &&
                                (swapResult.columnChanged.get(i) != COLUMN_INDEX_STATUS_DATE)) {

                            syncFieldsOnly = false;
                            break;
                        }
                    }
                    if (syncFieldsOnly) // Disable change animations (only sync fields have changed)
                        ((RecyclerItemAnimator)mNotifyList.getItemAnimator())
                                .setDisableChangeAnimations(swapResult.countChanged);

                    // Display/Hide floating action button
                    fab.setVisibility(View.VISIBLE);
                    final ViewPropertyAnimatorCompat animation = ViewCompat.animate(fab);

                    if (newNotify > 0) // Display floating action button
                        animation.setDuration(RecyclerItemAnimator.DEFAULT_DURATION)
                                .translationY(0)
                                .setListener(new ViewPropertyAnimatorListener() {
                                    @Override
                                    public void onAnimationStart(View view) {

                                    }

                                    @Override
                                    public void onAnimationEnd(View view) {
                                        animation.setListener(null);
                                    }

                                    @Override
                                    public void onAnimationCancel(View view) {
                                        ViewCompat.setTranslationY(view, 0);
                                    }
                                })
                                .start();

                    else if (fab.getTranslationY() == 0) { // Hide floating action button

                        Logs.add(Logs.Type.I, "Hide floating action button");
                        if (mFabTransY == Constants.NO_DATA)
                            fab.getViewTreeObserver()
                                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                        @Override
                                        public void onGlobalLayout() {

                                            Logs.add(Logs.Type.V, null);
                                            mFabTransY = fab.getHeight() +
                                                    (getResources().getDimensionPixelSize(R.dimen.fab_margin) << 1);

                                            fab.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                            hideFab(animation);
                                        }
                                    });
                        else
                            hideFab(animation);
                    }

                } else {
                    Logs.add(Logs.Type.I, "Initial query");

                    ////// Fill notification list
                    mNotifyAdapter = new NotifyRecyclerViewAdapter(R.layout.layout_notification_item,
                            COLUMN_INDEX_NOTIFY_ID);
                    mNotifyAdapter.getDataSource().fill(cursor);
                    mNotifyList.setAdapter(mNotifyAdapter);

                    // Display/Hide floating action button
                    if (newNotify > 0)
                        fab.setVisibility(View.VISIBLE);
                }
                break;
            }
            case Queries.MAIN_NOTIFY_MAX: {

                mQueryID = cursor.getShort(0);
                break;
            }
        }
    }

    @Override
    public void onLoaderReset() {

    }

    //////
    private final QueryLoader mListLoader = new QueryLoader(this, this); // User notification list query loader
    private final QueryLoader mMaxLoader = new QueryLoader(this, this); // Max notification Id query loader

    private short mQueryCount; // DB query result count
    private int mQueryID = Constants.NO_DATA; // Max record Id (used to check if new entries)
    private short mQueryOld; // DB old entries requested

    // Query column indexes
    private static final int COLUMN_INDEX_OBJECT_TYPE = 0;
    private static final int COLUMN_INDEX_STATUS_DATE = 1;
    private static final int COLUMN_INDEX_SYNC = 2;
    private static final int COLUMN_INDEX_NOTIFY_ID = 3;
    private static final int COLUMN_INDEX_DATE = 4;
    private static final int COLUMN_INDEX_LU_FLAG = 5;
    private static final int COLUMN_INDEX_PSEUDO = 6;
    private static final int COLUMN_INDEX_SEX = 7;
    private static final int COLUMN_INDEX_PROFILE = 8;
    private static final int COLUMN_INDEX_MEMBER_ID = 9;
    private static final int COLUMN_INDEX_ALBUM = 10;
    private static final int COLUMN_INDEX_PHOTO_ID = 11;
    private static final int COLUMN_INDEX_PUB_TEXT = 12;
    private static final int COLUMN_INDEX_LINK = 13;
    private static final int COLUMN_INDEX_FICHIER = 14;
    private static final int COLUMN_INDEX_PUB_ID = 15;
    private static final int COLUMN_INDEX_MSG_TEXT = 16;
    private static final int COLUMN_INDEX_MSG_ID = 17;
    private static final int COLUMN_INDEX_COM_TEXT = 18;
    private static final int COLUMN_INDEX_COMMENT_ID = 19;

    private void setLoaders() { // Initialize or update queries loaders
        Logs.add(Logs.Type.V, null);
        final String selection = NotificationsTable.COLUMN_PSEUDO + "='" +
                getIntent().getStringExtra(Login.EXTRA_DATA_PSEUDO) + '\'';

        Tools.startProcess(this, new Tools.OnProcessListener() {
            private static final String DATA_KEY_QUERY_LIMIT = "queryLimit";

            @Override
            public Bundle onBackgroundTask() {
                Logs.add(Logs.Type.V, null);

                // Get query limit
                short queryLimit = 0;
                if (mQueryID != Constants.NO_DATA) {

                    queryLimit = mQueryCount;
                    queryLimit += mQueryOld;
                    queryLimit += (short) DataTable.getEntryCount(getContentResolver(),
                            NotificationsTable.TABLE_NAME, selection + " AND " +
                                    IDataTable.DataField.COLUMN_ID + '>' + mQueryID);






                    //mQueryOld = Queries.OLDER_NOTIFICATIONS;
                    //mQueryOld = 0;






                }
                Bundle result = new Bundle();
                result.putShort(DATA_KEY_QUERY_LIMIT, queryLimit);
                return result;
            }

            @Override
            public void onMainNextTask(Bundle backResult) {
                Logs.add(Logs.Type.V, "backResult: " + backResult);

                short queryLimit = backResult.getShort(DATA_KEY_QUERY_LIMIT);
                if (queryLimit < Queries.LIMIT_MAIN_NOTIFY)
                    queryLimit = Queries.LIMIT_MAIN_NOTIFY;

                // Load notification data (using query loader)
                Bundle queryData = new Bundle();
                queryData.putParcelable(QueryLoader.DATA_KEY_URI,
                        getIntent().getParcelableExtra(Login.EXTRA_DATA_NOTIFY_URI));
                queryData.putString(QueryLoader.DATA_KEY_SELECTION,
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
                                " WHERE " + selection +
                                " ORDER BY " + NotificationsTable.COLUMN_DATE + " DESC" +
                                " LIMIT " + queryLimit);

                if (mQueryID != Constants.NO_DATA) // Restart
                    mListLoader.restart(NotifyActivity.this, Queries.MAIN_NOTIFY_LIST, queryData);

                else { // Initialize
                    mListLoader.init(NotifyActivity.this, Queries.MAIN_NOTIFY_LIST, queryData);

                    queryData.putString(QueryLoader.DATA_KEY_SELECTION, "SELECT max(" + IDataTable.DataField.COLUMN_ID +
                            ") FROM " + NotificationsTable.TABLE_NAME + " WHERE " + selection);
                    mMaxLoader.init(NotifyActivity.this, Queries.MAIN_NOTIFY_MAX, queryData);
                }
            }
        });
    }

    ////// NotifyActivity //////////////////////////////////////////////////////////////////////////
    @Override
    protected void onLoggedResume() {

    }

    ////// AppCompatActivity ///////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
        setContentView(R.layout.activity_notify);

        // Set toolbar & default new notification
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setNewNotifyInfo(0);

        // Set recycler view
        final RecyclerItemAnimator itemAnimator = new RecyclerItemAnimator();
        itemAnimator.setAnimationMaker(new RecyclerItemAnimator.ItemAnimatorMaker() {
            @Override
            public void onCancel(RecyclerItemAnimator.AnimType type, View item) {
                Logs.add(Logs.Type.V, "type: " + type + ";item: " + item);
                switch (type) {

                    case ADD: {
                        ViewCompat.setScaleX(item, 1);
                        ViewCompat.setAlpha(item, 1);
                        break;
                    }
                    case CHANGE: {
                        ViewCompat.setTranslationX(item, 0);
                        ViewCompat.setTranslationY(item, 0);
                        ViewCompat.setScaleX(item, 1);
                        ViewCompat.setAlpha(item, 1);
                        break;
                    }
                }
            }

            @Override
            public void onPrepare(RecyclerItemAnimator.AnimInfo info) {
                Logs.add(Logs.Type.V, "info: " + info);
                switch (RecyclerItemAnimator.getAnimType(info)) {

                    case ADD: {
                        ViewCompat.setScaleX(info.mHolder.itemView, 0);
                        ViewCompat.setAlpha(info.mHolder.itemView, 0);
                        break;
                    }
                    case CHANGE: {

                        ////// Previous holder
                        float prevTransX = ViewCompat.getTranslationX(info.mHolder.itemView);
                        float prevTransY = ViewCompat.getTranslationY(info.mHolder.itemView);
                        float prevAlpha = ViewCompat.getAlpha(info.mHolder.itemView);
                        itemAnimator.endAnimation(info.mHolder); // Clear any animation & settings

                        RecyclerItemAnimator.ChangeInfo changeInfo = (RecyclerItemAnimator.ChangeInfo) info;
                        int deltaX = (int) (changeInfo.mToX - changeInfo.mFromX - prevTransX);
                        int deltaY = (int) (changeInfo.mToY - changeInfo.mFromY - prevTransY);

                        // Restore previous settings
                        ViewCompat.setTranslationX(info.mHolder.itemView, prevTransX);
                        ViewCompat.setTranslationY(info.mHolder.itemView, prevTransY);
                        ViewCompat.setAlpha(info.mHolder.itemView, prevAlpha);

                        ////// New holder
                        if ((changeInfo.mNewHolder != null) && (changeInfo.mNewHolder.itemView != null)) {
                            itemAnimator.endAnimation(changeInfo.mNewHolder);

                            ViewCompat.setTranslationX(changeInfo.mNewHolder.itemView, -deltaX);
                            ViewCompat.setTranslationY(changeInfo.mNewHolder.itemView, -deltaY);
                            ViewCompat.setAlpha(changeInfo.mNewHolder.itemView, 0);
                        }
                        break;
                    }
                }
            }

            @Override
            public ViewPropertyAnimatorCompat onAnimate(RecyclerItemAnimator.AnimInfo info, boolean changeNew) {
                Logs.add(Logs.Type.V, "info: " + info + ";changeNew: " + changeNew);

                View itemView = info.mHolder.itemView;
                switch (RecyclerItemAnimator.getAnimType(info)) {

                    case REMOVE: {
                        ViewCompat.setPivotX(itemView, 0);
                        ViewCompat.animate(itemView).scaleX(0).alpha(0);
                        break;
                    }
                    case CHANGE: {
                        RecyclerItemAnimator.ChangeInfo changeInfo = (RecyclerItemAnimator.ChangeInfo) info;
                        if (!changeNew) {
                            ViewCompat.setPivotX(itemView, 0);

                            // Same as remove animation
                            ViewCompat.animate(itemView)
                                    .translationX(changeInfo.mToX - changeInfo.mFromX)
                                    .translationY(changeInfo.mToY - changeInfo.mFromY)
                                    .alpha(0)
                                    .scaleX(0);

                        } else {
                            itemView = changeInfo.mNewHolder.itemView;
                            ViewCompat.setPivotX(itemView, itemView.getWidth());
                            ViewCompat.setScaleX(itemView, 0);

                            // Same as add animation
                            ViewCompat.animate(itemView)
                                    .translationX(0)
                                    .translationY(0)
                                    .alpha(1)
                                    .scaleX(1);
                        }
                        break;
                    }
                    case ADD: {
                        ViewCompat.setPivotX(itemView, itemView.getWidth());
                        ViewCompat.animate(itemView).scaleX(1).alpha(1);
                        break;
                    }
                }
                return ViewCompat.animate(itemView);
            }
        });
        LinearLayoutManager linearManager = new LinearLayoutManager(this);
        linearManager.setOrientation(LinearLayoutManager.VERTICAL);

        mNotifyList = (RecyclerView) findViewById(R.id.list_notification);
        mNotifyList.setLayoutManager(linearManager);
        mNotifyList.setItemAnimator(itemAnimator);

        // Initialize notification list (set query loaders)
        setLoaders();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logs.add(Logs.Type.V, "item: " + item);
        if (item.getItemId() == android.R.id.home) {

            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
