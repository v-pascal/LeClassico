package com.studio.artaban.leclassico.activities.main;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.animations.RecyclerItemAnimator;
import com.studio.artaban.leclassico.components.RecyclerAdapter;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.IDataTable;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.tables.ActualitesTable;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.data.tables.CommentairesTable;
import com.studio.artaban.leclassico.data.tables.MessagerieTable;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.data.tables.PhotosTable;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;
import com.studio.artaban.leclassico.tools.Tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pascal on 05/09/16.
 * Notifications fragment class (MainActivity)
 */
public class NotificationsFragment extends MainFragment implements QueryLoader.OnResultListener {









    public void read() { // Mark unread notification(s) as read
        Logs.add(Logs.Type.V, null);






        ContentValues values = new ContentValues();
        for (int i = 0; i < 15; ++i) {

            values.put(NotificationsTable.COLUMN_LU_FLAG, 1);
            //values.put(NotificationsTable.COLUMN_STATUS_DATE, "2016-09-08 08:35:38");
            values.put(NotificationsTable.COLUMN_STATUS_DATE, "2016-09-08 08:" + String.format("%02d", i) + ":00");
            values.put(Constants.DATA_COLUMN_SYNCHRONIZED, DataProvider.Synchronized.DONE.getValue());
            values.put(NotificationsTable.COLUMN_PSEUDO, "Testers");

            values.put(NotificationsTable.COLUMN_DATE, "2016-09-07 12:05:43");
            //values.put(NotificationsTable.COLUMN_DATE, "2016-09-08 12:05:43");

            values.put(NotificationsTable.COLUMN_OBJECT_TYPE, "W");
            values.put(NotificationsTable.COLUMN_OBJECT_ID, 63);
            values.put(NotificationsTable.COLUMN_OBJECT_FROM, "Julie");
            temp = getContext().getContentResolver().insert(Uri.parse(DataProvider.CONTENT_URI + NotificationsTable.TABLE_NAME), values);
        }

        /*
        ContentValues valuesA = new ContentValues();
        valuesA.put(NotificationsTable.COLUMN_LU_FLAG, testage);
        int res =getContext().getContentResolver().update(Uri.parse(DataProvider.CONTENT_URI + NotificationsTable.TABLE_NAME), valuesA,
                "_id=2", null);
        //Logs.add(Logs.Type.E, "res: " + res);
        if (testage == 0)
            testage = 1;
        else
            testage = 0;
            */




        // Get query limit
        short queryLimit = 0;
        String selection = NotificationsTable.COLUMN_PSEUDO + "='" +
                getActivity().getIntent().getStringExtra(MainActivity.EXTRA_DATA_KEY_PSEUDO) + '\'';
        if (mQueryID != Constants.NO_DATA) {

            queryLimit = mQueryCount;
            queryLimit += mQueryOld;
            queryLimit += (short)Tools.getEntryCount(getContext().getContentResolver(),
                    NotificationsTable.TABLE_NAME, selection + " AND " +
                            IDataTable.DataField.COLUMN_ID + '>' + mQueryID);
            // NB: The new DB entry count query just above should be executed quickly (UI thread)



            //mQueryOld = Queries.OLDER_NOTIFICATIONS;
            //mQueryOld = 0;



        }
        if (queryLimit < Queries.LIMIT_NOTIFICATIONS)
            queryLimit = Queries.LIMIT_NOTIFICATIONS;

        // Load notification data (using query loader)
        Bundle queryData = new Bundle();
        queryData.putString(QueryLoader.DATA_KEY_SELECTION,
                "SELECT " +
                        NotificationsTable.COLUMN_OBJECT_TYPE + ',' + // COLUMN_INDEX_OBJECT_TYPE
                        NotificationsTable.COLUMN_STATUS_DATE + ',' + // COLUMN_INDEX_STATUS_DATE
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
        mListLoader.restart(getActivity(), Queries.MAIN_NOTIFICATIONS, queryData);

        queryData.putString(QueryLoader.DATA_KEY_SELECTION, selection);
        queryData.putStringArray(QueryLoader.DATA_KEY_PROJECTION,
                new String[]{"max(" + IDataTable.DataField.COLUMN_ID + ')'});
        mMaxLoader.restart(getActivity(), Queries.MAIN_NOTIFICATION_MAX, queryData);






    }

    private int testage = 0;
    private Uri temp;
    @Override
    public void onDestroy() {
        super.onDestroy();
        Logs.add(Logs.Type.V, null);
        if (temp != null) {
            ContentValues values = new ContentValues();
            values.put(Constants.DATA_COLUMN_SYNCHRONIZED, DataProvider.Synchronized.TO_DELETE.getValue());
            getContext().getContentResolver().update(temp, values, null, null);
            getContext().getContentResolver().delete(temp, Constants.DATA_DELETE_SELECTION, null);
        }
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

    private RecyclerView mNotifyList; // Recycler view containing notification list
    private NotifyRecyclerViewAdapter mNotifyAdapter; // Recycler view adapter (with cursor management)

    private class NotifyRecyclerViewAdapter extends RecyclerAdapter implements View.OnClickListener {
        public NotifyRecyclerViewAdapter(@LayoutRes int layout, int key) {
            super(layout, key);
        }

        private void displayDate(TextView viewDate, String date) {
        // Display the notification date separator (with notification date formatted as locale user)

            //Logs.add(Logs.Type.V, "viewDate: " + viewDate + ";date: " + date);
            SimpleDateFormat dateFormat = new SimpleDateFormat(Queries.FORMAT_DATE_TIME);
            try {
                Date notifyDate = dateFormat.parse(date);
                DateFormat user = android.text.format.DateFormat.getMediumDateFormat(getContext());
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
            switch (sender.getId()) {

                case R.id.layout_data: {


                    break;
                }
                case R.id.image_pseudo: {


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
                displayDate((TextView) holder.itemView.findViewById(R.id.text_date),
                        mDataSource.getString(position, COLUMN_INDEX_DATE));
                holder.itemView.findViewById(R.id.date_separator).setVisibility(View.VISIBLE);

            } else {
                String prevDate = mDataSource.getString(position - 1, COLUMN_INDEX_DATE).substring(0, 10); // YYYY-MM-DD
                String curDate = mDataSource.getString(position, COLUMN_INDEX_DATE);

                if (prevDate.compareTo(curDate.substring(0, 10)) != 0) { // Display the different date (separator)
                    displayDate((TextView) holder.itemView.findViewById(R.id.text_date), curDate);
                    holder.itemView.findViewById(R.id.date_separator).setVisibility(View.VISIBLE);
                } else
                    holder.itemView.findViewById(R.id.date_separator).setVisibility(View.GONE);
            }

            // Set unread notification display (if the case)
            ImageView notifyType = (ImageView) holder.itemView.findViewById(R.id.image_type);
            TextView textTime = (TextView) holder.itemView.findViewById(R.id.text_time);
            int pseudoColor = displayReadFlag(
                    mDataSource.getInt(position, COLUMN_INDEX_LU_FLAG) == Constants.DATA_UNREAD,
                    holder.itemView, notifyType, textTime);

            // Set from pseudo icon
            boolean female = (!mDataSource.isNull(position, COLUMN_INDEX_SEX)) &&
                    (mDataSource.getInt(position, COLUMN_INDEX_SEX) == CamaradesTable.FEMALE);
            String profile = (!mDataSource.isNull(position, COLUMN_INDEX_PROFILE)) ?
                    mDataSource.getString(position, COLUMN_INDEX_PROFILE) : null;
            Tools.setProfile(getActivity(), (ImageView) holder.itemView.findViewById(R.id.image_pseudo),
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
            ((TextView) holder.itemView.findViewById(R.id.text_message)).setText(message, TextView.BufferType.SPANNABLE);
            ((TextView) holder.itemView.findViewById(R.id.text_info)).setText(info, TextView.BufferType.SPANNABLE);

            // Set notification type icon & time
            notifyType.setImageDrawable(getResources().getDrawable(Tools.getNotifyIcon(type)));
            textTime.setText(mDataSource.getString(position, COLUMN_INDEX_DATE).substring(11, 16));

            // Set notify synchronization
            Tools.setSyncView(getContext(), (TextView) holder.itemView.findViewById(R.id.text_sync_date),
                    (ImageView) holder.itemView.findViewById(R.id.image_sync),
                    mDataSource.getString(position, COLUMN_INDEX_STATUS_DATE),
                    (byte) mDataSource.getInt(position, COLUMN_INDEX_SYNC));

            // Events
            holder.itemView.findViewById(R.id.layout_data).setOnClickListener(this);
            holder.itemView.findViewById(R.id.image_pseudo).setOnClickListener(this);

            // Add margin bottom at last item position
            ((RecyclerView.LayoutParams)holder.itemView.getLayoutParams()).bottomMargin =
                    (position == (mDataSource.getCount() - 1))?
                            getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin):0;

            ////// Animate item appearance
            if (position > mLastPosition) {
                mLastPosition = position;

                ViewCompat.animate(holder.itemView).cancel();
                ViewCompat.setScaleX(holder.itemView, 0.7f);
                ViewCompat.setScaleY(holder.itemView, 0.7f);
                ViewCompat.animate(holder.itemView)
                        .setDuration(500)
                        .scaleX(1)
                        .scaleY(1)
                        .start();
            }
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            ViewCompat.animate(holder.itemView).cancel();
            super.onViewDetachedFromWindow(holder);
        }
    }

    ////// OnContentListener ///////////////////////////////////////////////////////////////////////
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        // WARNING: Not in UI thread







    }

    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);

        cursor.moveToFirst();
        switch (id) {

            case Queries.MAIN_NOTIFICATIONS: {
                mQueryCount = (short)cursor.getCount();

                // Register content observer on each notification
                mDataObserver.register(getContext().getContentResolver(), cursor,
                        NotificationsTable.TABLE_NAME, COLUMN_INDEX_NOTIFY_ID);

                // Check if not the initial query
                if (mNotifyAdapter != null) {
                    Logs.add(Logs.Type.I, "Query update");

                    // Update shortcut




                    // Update notification list
                    mNotifyAdapter.getDataSource().swap(mNotifyAdapter, cursor, true);
                    mNotifyList.setAdapter(mNotifyAdapter);

                } else {
                    Logs.add(Logs.Type.I, "Initial query");

                    // Fill shortcut
                    boolean unread = cursor.getInt(COLUMN_INDEX_LU_FLAG) == Constants.DATA_UNREAD;
                    SpannableStringBuilder info = new SpannableStringBuilder(getString(R.string.notification_info));
                    if (unread) {

                        int unreadPos = info.length() + 2; // ' ' + '(' = 2
                        String notRead = getString(R.string.unread);
                        info.append(" (" + notRead + ")");
                        info.setSpan(new StyleSpan(Typeface.BOLD), unreadPos, unreadPos + notRead.length(), 0);
                    }
                    boolean female = (!cursor.isNull(COLUMN_INDEX_SEX)) &&
                            (cursor.getInt(COLUMN_INDEX_SEX) == CamaradesTable.FEMALE);
                    String profile = (!cursor.isNull(COLUMN_INDEX_PROFILE)) ? cursor.getString(COLUMN_INDEX_PROFILE) : null;
                    String pseudo = cursor.getString(COLUMN_INDEX_PSEUDO);
                    char type = cursor.getString(COLUMN_INDEX_OBJECT_TYPE).charAt(0);

                    mListener.onSetNotify(type, !unread);
                    mListener.onSetInfo(Constants.MAIN_SECTION_NOTIFICATIONS, info);
                    mListener.onSetDate(Constants.MAIN_SECTION_NOTIFICATIONS, false, cursor.getString(COLUMN_INDEX_DATE));
                    mListener.onSetIcon(Constants.MAIN_SECTION_NOTIFICATIONS, female, profile, R.dimen.shortcut_content_height);

                    // Fill notification list
                    mNotifyAdapter = new NotifyRecyclerViewAdapter(R.layout.layout_notification_item, COLUMN_INDEX_NOTIFY_ID);
                    mNotifyAdapter.getDataSource().fill(cursor);
                    mListener.onSetMessage(Constants.MAIN_SECTION_NOTIFICATIONS,
                            getNotifyMessage(type, Tools.getNotifyWallType(mNotifyAdapter.getDataSource(),
                                    0, COLUMN_INDEX_LINK, COLUMN_INDEX_FICHIER), pseudo, R.color.colorPrimaryProfile));
                    mNotifyList.setAdapter(mNotifyAdapter);
                }
                break;
            }
            case Queries.MAIN_NOTIFICATION_MAX: {

                mQueryID = cursor.getShort(0);
                break;
            }
        }
        cursor.close();
    }

    @Override
    public void onLoaderReset() {

    }

    //////
    private QueryLoader mListLoader; // User notification list query loader
    private QueryLoader mMaxLoader; // Max notification Id query loader

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

    ////// MainFragment ////////////////////////////////////////////////////////////////////////////
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logs.add(Logs.Type.V, "context: " + context);

        mListLoader = new QueryLoader(context, this);
        mMaxLoader = new QueryLoader(context, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);
        View rootView = inflater.inflate(R.layout.layout_notifications, container, false);
        rootView.setTag(Constants.MAIN_SECTION_NOTIFICATIONS);

        // Set recycler view
        final RecyclerItemAnimator itemAnimator = new RecyclerItemAnimator();
        itemAnimator.setAnimationMaker(new RecyclerItemAnimator.ItemAnimatorMaker() {
            @Override
            public void onCancel(RecyclerItemAnimator.AnimType type, View item) {
                switch (type) {

                    case ADD: {
                        ViewCompat.setAlpha(item, 1);
                        ViewCompat.setScaleX(item, 1);
                        break;
                    }
                    case CHANGE: {
                        ViewCompat.setTranslationX(item, 0);
                        ViewCompat.setTranslationY(item, 0);
                        ViewCompat.setAlpha(item, 1);
                        break;
                    }
                }
            }

            @Override
            public void onPrepare(RecyclerItemAnimator.AnimInfo info) {
                switch (RecyclerItemAnimator.getAnimType(info)) {

                    case ADD: {
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
                        ViewCompat.setPivotX(itemView, itemView.getWidth());
                        ViewCompat.animate(itemView).scaleX(0).alpha(0);
                        break;
                    }
                    case CHANGE: {
                        RecyclerItemAnimator.ChangeInfo changeInfo = (RecyclerItemAnimator.ChangeInfo) info;
                        if (!changeNew) {
                            ViewCompat.setPivotX(itemView, itemView.getWidth());

                            // Same as remove animation
                            ViewCompat.animate(itemView)
                                    .translationX(changeInfo.mToX - changeInfo.mFromX)
                                    .translationY(changeInfo.mToY - changeInfo.mFromY)
                                    .alpha(0)
                                    .scaleX(0);

                        } else {
                            itemView = changeInfo.mNewHolder.itemView;
                            ViewCompat.setPivotX(itemView, 0);
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
                        ViewCompat.setPivotX(itemView, 0);
                        ViewCompat.setScaleX(itemView, 0);
                        ViewCompat.setAlpha(itemView, 0);

                        ViewCompat.animate(itemView).scaleX(1).alpha(1);
                        break;
                    }
                }
                return ViewCompat.animate(itemView);
            }
        });
        mNotifyList = (RecyclerView) rootView.findViewById(R.id.list_notification);
        mNotifyList.setItemAnimator(itemAnimator);

        LinearLayoutManager linearManager = new LinearLayoutManager(getContext());
        linearManager.setOrientation(LinearLayoutManager.VERTICAL);
        mNotifyList.setLayoutManager(linearManager);

        // Set shortcut data (default)
        SpannableStringBuilder data = new SpannableStringBuilder(getString(R.string.no_notification));
        mListener.onSetMessage(Constants.MAIN_SECTION_NOTIFICATIONS, data);
        data = new SpannableStringBuilder(getString(R.string.no_notification_info));
        mListener.onSetInfo(Constants.MAIN_SECTION_NOTIFICATIONS, data);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Logs.add(Logs.Type.V, null);

        // Get query limit
        short queryLimit = 0;
        String selection = NotificationsTable.COLUMN_PSEUDO + "='" +
                getActivity().getIntent().getStringExtra(MainActivity.EXTRA_DATA_KEY_PSEUDO) + '\'';
        if (mQueryID != Constants.NO_DATA) {

            queryLimit = mQueryCount;
            queryLimit += mQueryOld;
            queryLimit += (short)Tools.getEntryCount(getContext().getContentResolver(),
                    NotificationsTable.TABLE_NAME, selection + " AND " +
                            IDataTable.DataField.COLUMN_ID + '>' + mQueryID);
            // NB: The new DB entry count query just above should be executed quickly (UI thread)






            //mQueryOld = Queries.OLDER_NOTIFICATIONS;
            //mQueryOld = 0;






        }
        if (queryLimit < Queries.LIMIT_NOTIFICATIONS)
            queryLimit = Queries.LIMIT_NOTIFICATIONS;

        // Load notification data (using query loader)
        Bundle queryData = new Bundle();
        queryData.putString(QueryLoader.DATA_KEY_SELECTION,
                "SELECT " +
                        NotificationsTable.COLUMN_OBJECT_TYPE + ',' + // COLUMN_INDEX_OBJECT_TYPE
                        NotificationsTable.COLUMN_STATUS_DATE + ',' + // COLUMN_INDEX_STATUS_DATE
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
        mListLoader.restart(getActivity(), Queries.MAIN_NOTIFICATIONS, queryData);

        queryData.putString(QueryLoader.DATA_KEY_SELECTION, selection);
        queryData.putStringArray(QueryLoader.DATA_KEY_PROJECTION,
                new String[]{"max(" + IDataTable.DataField.COLUMN_ID + ')'});
        mMaxLoader.restart(getActivity(), Queries.MAIN_NOTIFICATION_MAX, queryData);
    }

    @Override
    public void onPause() {
        super.onPause();
        Logs.add(Logs.Type.V, null);

        // Unregister all content observer
        mDataObserver.unregister(getContext().getContentResolver());
    }
}
