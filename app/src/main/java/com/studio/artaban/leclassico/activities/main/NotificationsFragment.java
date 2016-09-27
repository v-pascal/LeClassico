package com.studio.artaban.leclassico.activities.main;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
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
import com.studio.artaban.leclassico.components.RecyclerAdapter;
import com.studio.artaban.leclassico.data.Constants;
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

    private static final String DATA_KEY_QUERY_LIMIT = "queryLimit";
    // Data keys









    public void read() { // Mark unread notification(s) as read
        Logs.add(Logs.Type.V, null);


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
    private short mNotifyLimit = Queries.LIMIT_NOTIFICATIONS; // Notification query limit

    private class NotifyRecyclerViewAdapter extends RecyclerAdapter implements View.OnClickListener {

        public NotifyRecyclerViewAdapter(@LayoutRes int layout, int key) {
            super(layout, key);
        }

        private void displayDate(TextView viewDate, String date) {
            // Display the notification date separator (with notification date formatted as locale user)

            Logs.add(Logs.Type.V, "viewDate: " + viewDate + ";date: " + date);
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
            Logs.add(Logs.Type.V, "holder: " + holder + ";position: " + position);

            // Set notification date
            if (position == 0) {
                displayDate((TextView) holder.mRootView.findViewById(R.id.text_date),
                        mDataSource.getString(position, COLUMN_INDEX_DATE));
                holder.mRootView.findViewById(R.id.date_separator).setVisibility(View.VISIBLE);

            } else {
                String prevDate = mDataSource.getString(position - 1, COLUMN_INDEX_DATE).substring(0, 10); // YYYY-MM-DD
                String curDate = mDataSource.getString(position, COLUMN_INDEX_DATE);

                if (prevDate.compareTo(curDate.substring(0, 10)) != 0) { // Display the different date (separator)
                    displayDate((TextView) holder.mRootView.findViewById(R.id.text_date), curDate);
                    holder.mRootView.findViewById(R.id.date_separator).setVisibility(View.VISIBLE);
                }
            }

            // Set unread notification display (if the case)
            int pseudoColor = R.color.colorPrimaryProfile;
            ImageView notifyType = (ImageView) holder.mRootView.findViewById(R.id.image_type);
            TextView textTime = (TextView) holder.mRootView.findViewById(R.id.text_time);
            if (mDataSource.getInt(position, COLUMN_INDEX_LU_FLAG) == Constants.DATA_UNREAD) {

                holder.mRootView.findViewById(R.id.layout_data)
                        .setBackground(getResources().getDrawable(R.drawable.notify_unread_background));
                notifyType.setImageTintList(null);
                notifyType.setColorFilter(Color.RED);
                ((TextView) holder.mRootView.findViewById(R.id.text_message)).setTypeface(Typeface.DEFAULT_BOLD);
                textTime.setTypeface(Typeface.DEFAULT_BOLD);
                textTime.setTextColor(Color.RED);
                pseudoColor = R.color.red;
            }

            // Set from pseudo icon
            boolean female = (!mDataSource.isNull(position, COLUMN_INDEX_SEX)) &&
                    (mDataSource.getInt(position, COLUMN_INDEX_SEX) == CamaradesTable.FEMALE);
            String profile = (!mDataSource.isNull(position, COLUMN_INDEX_PROFILE)) ?
                    mDataSource.getString(position, COLUMN_INDEX_PROFILE) : null;
            Tools.setProfile(getActivity(), (ImageView) holder.mRootView.findViewById(R.id.image_pseudo),
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
            ((TextView) holder.mRootView.findViewById(R.id.text_message)).setText(message, TextView.BufferType.SPANNABLE);
            ((TextView) holder.mRootView.findViewById(R.id.text_info)).setText(info, TextView.BufferType.SPANNABLE);

            // Set notification type icon & time
            notifyType.setImageDrawable(getResources().getDrawable(Tools.getNotifyIcon(type)));
            textTime.setText(mDataSource.getString(position, COLUMN_INDEX_DATE).substring(11, 16));

            // Set notify synchronization
            Tools.setSyncView(getContext(), (TextView) holder.mRootView.findViewById(R.id.text_sync_date),
                    (ImageView) holder.mRootView.findViewById(R.id.image_sync),
                    mDataSource.getString(position, COLUMN_INDEX_STATUS_DATE),
                    (byte) mDataSource.getInt(position, COLUMN_INDEX_SYNC));

            // Events
            holder.mRootView.findViewById(R.id.layout_data).setOnClickListener(this);
            holder.mRootView.findViewById(R.id.image_pseudo).setOnClickListener(this);
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
        if (id == Queries.MAIN_NOTIFICATIONS) {

            // Register content observer on each notification
            mDataObserver.register(getContext().getContentResolver(), cursor,
                    NotificationsTable.TABLE_NAME, COLUMN_INDEX_NOTIFY_ID);

            // Check if not the initial query
            if (mNotifyAdapter != null) {
                Logs.add(Logs.Type.I, "Query update");

                // Update shortcut




                // Update notification list





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
        }
        cursor.close();
    }

    @Override
    public void onLoaderReset() {

    }

    //////
    private QueryLoader mNotifyLoader; // User notification query loader

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
        mNotifyLoader = new QueryLoader(context, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);
        View rootView = inflater.inflate(R.layout.layout_notifications, container, false);
        rootView.setTag(Constants.MAIN_SECTION_NOTIFICATIONS);
        mNotifyList = (RecyclerView) rootView.findViewById(R.id.list_notification);

        // Restore data
        if (savedInstanceState != null)
            mNotifyLimit = savedInstanceState.getShort(DATA_KEY_QUERY_LIMIT);

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











        // Load notification data (using query loader)
        Bundle queryData = new Bundle();
        queryData.putString(QueryLoader.DATA_KEY_SELECTION,
                "SELECT " +
                        NotificationsTable.COLUMN_OBJECT_TYPE + "," + // COLUMN_INDEX_OBJECT_TYPE
                        NotificationsTable.COLUMN_STATUS_DATE + "," + // COLUMN_INDEX_STATUS_DATE
                        NotificationsTable.TABLE_NAME + "." + Constants.DATA_COLUMN_SYNCHRONIZED + "," + // COLUMN_INDEX_SYNC
                        NotificationsTable.TABLE_NAME + "." + IDataTable.DataField.COLUMN_ID + "," + // COLUMN_INDEX_NOTIFY_ID
                        NotificationsTable.COLUMN_DATE + "," + // COLUMN_INDEX_DATE
                        NotificationsTable.COLUMN_LU_FLAG + "," + // COLUMN_INDEX_LU_FLAG
                        CamaradesTable.COLUMN_PSEUDO + "," + // COLUMN_INDEX_PSEUDO
                        CamaradesTable.COLUMN_SEXE + "," + // COLUMN_INDEX_SEX
                        CamaradesTable.COLUMN_PROFILE + "," + // COLUMN_INDEX_PROFILE
                        CamaradesTable.TABLE_NAME + "." + IDataTable.DataField.COLUMN_ID + "," + // COLUMN_INDEX_MEMBER_ID
                        PhotosTable.COLUMN_ALBUM + "," + // COLUMN_INDEX_ALBUM
                        PhotosTable.TABLE_NAME + "." + IDataTable.DataField.COLUMN_ID + "," + // COLUMN_INDEX_PHOTO_ID
                        ActualitesTable.COLUMN_TEXT + "," + // COLUMN_INDEX_PUB_TEXT
                        ActualitesTable.COLUMN_LINK + "," + // COLUMN_INDEX_LINK
                        ActualitesTable.COLUMN_FICHIER + "," + // COLUMN_INDEX_FICHIER
                        ActualitesTable.TABLE_NAME + "." + IDataTable.DataField.COLUMN_ID + "," + // COLUMN_INDEX_PUB_ID
                        MessagerieTable.COLUMN_MESSAGE + "," + // COLUMN_INDEX_MSG_TEXT
                        MessagerieTable.TABLE_NAME + "." + IDataTable.DataField.COLUMN_ID + "," + // COLUMN_INDEX_MSG_ID
                        CommentairesTable.COLUMN_TEXT + "," + // COLUMN_INDEX_COM_TEXT
                        CommentairesTable.TABLE_NAME + "." + IDataTable.DataField.COLUMN_ID + // COLUMN_INDEX_COMMENT_ID

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
                        NotificationsTable.COLUMN_OBJECT_FROM + "=" + MessagerieTable.COLUMN_FROM + " AND " +
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
                        getActivity().getIntent().getStringExtra(MainActivity.EXTRA_DATA_KEY_PSEUDO) + "'" +
                        " ORDER BY " + NotificationsTable.COLUMN_DATE + " DESC" +
                        " LIMIT " + mNotifyLimit);
        mNotifyLoader.restart(getActivity(), Queries.MAIN_NOTIFICATIONS, queryData);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putShort(DATA_KEY_QUERY_LIMIT, mNotifyLimit);

        Logs.add(Logs.Type.V, "outState: " + outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        Logs.add(Logs.Type.V, null);

        // Unregister all content observer
        mDataObserver.unregister(getContext().getContentResolver());
    }
}
