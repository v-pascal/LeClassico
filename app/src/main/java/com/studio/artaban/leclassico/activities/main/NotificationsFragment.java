package com.studio.artaban.leclassico.activities.main;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
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

    private RecyclerView mNotifyList; // Recycler view containing notification list
    private Cursor mNotifyData; // Cursor containing notification data











    public void read() { // Mark unread notification(s) as read
        Logs.add(Logs.Type.V, null);










        //notifyItemChanged(position - 1);
        //notifyItemInserted();
        //notifyItemRemoved();

        //notifyItemRangeChanged();
        //notifyItemRangeInserted();
        //notifyItemRangeRemoved();






    }
















    private SpannableStringBuilder getNotifyMessage(int color) {

        Logs.add(Logs.Type.V, "color: " + color);
        char type = mNotifyData.getString(COLUMN_INDEX_OBJECT_TYPE).charAt(0);
        String pseudo = mNotifyData.getString(COLUMN_INDEX_PSEUDO);

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
                        getResources().getStringArray(R.array.notify_wall_types)[Tools.getNotifyWallType(mNotifyData,
                                COLUMN_INDEX_LINK, COLUMN_INDEX_FICHIER)]));
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

                int typeIdx = (type == NotificationsTable.TYPE_PUB_COMMENT)? 0:1;
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












    private class NotifyRecyclerViewAdapter extends RecyclerView.Adapter<NotifyRecyclerViewAdapter.ViewHolder>
        implements View.OnClickListener {

        private void displayDate(TextView viewDate, String date) {
        // Display the notification date separator (with notification date formatted as locale user)

            Logs.add(Logs.Type.V, "viewDate: " + viewDate + ";date: " + date);
            SimpleDateFormat dateFormat = new SimpleDateFormat(Queries.FORMAT_DATE_TIME);
            try {
                Date notifyDate = dateFormat.parse(date);
                DateFormat user = android.text.format.DateFormat.getMediumDateFormat(getContext());
                String userDate = user.format(notifyDate);
                viewDate.setText((userDate.compareTo(user.format(new Date())) != 0)?
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

        ////// NotifyRecyclerViewAdapter ///////////////////////////////////////////////////////////
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //Logs.add(Logs.Type.V, "parent: " + parent + ";viewType: " + viewType);
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_notification_item,
                    parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Logs.add(Logs.Type.V, "holder: " + holder + ";position: " + position);

            // Set notification date
            if (position == 0) {
                mNotifyData.moveToFirst();
                displayDate(holder.mTextDate, mNotifyData.getString(COLUMN_INDEX_DATE));
                holder.mDateSeparator.setVisibility(View.VISIBLE);

            } else {
                mNotifyData.moveToPosition(position - 1);
                String prevDate = mNotifyData.getString(COLUMN_INDEX_DATE).substring(0, 10); // YYYY-MM-DD
                mNotifyData.moveToNext();
                String curDate = mNotifyData.getString(COLUMN_INDEX_DATE);

                if (prevDate.compareTo(curDate.substring(0, 10)) != 0) {
                    displayDate(holder.mTextDate, curDate); // Display the different date (separator)
                    holder.mDateSeparator.setVisibility(View.VISIBLE);
                }
            }

            // Set unread notification display (if the case)
            int pseudoColor = R.color.colorPrimaryProfile;
            if (mNotifyData.getInt(COLUMN_INDEX_LU_FLAG) == Constants.DATA_UNREAD) {

                holder.mNotifyData.setBackground(getResources().getDrawable(R.drawable.notify_unread_background));
                holder.mNotifyType.setImageTintList(null);
                holder.mNotifyType.setColorFilter(Color.RED);
                holder.mNotifyMessage.setTypeface(Typeface.DEFAULT_BOLD);
                holder.mTextTime.setTypeface(Typeface.DEFAULT_BOLD);
                holder.mTextTime.setTextColor(Color.RED);
                pseudoColor = R.color.red;
            }

            // Set from pseudo icon
            boolean female = (!mNotifyData.isNull(COLUMN_INDEX_SEX)) &&
                    (mNotifyData.getInt(COLUMN_INDEX_SEX) == CamaradesTable.FEMALE);
            String profile = (!mNotifyData.isNull(COLUMN_INDEX_PROFILE))?
                    mNotifyData.getString(COLUMN_INDEX_PROFILE) : null;
            Tools.setProfile(getActivity(), holder.mImagePseudo, female, profile,
                    R.dimen.shortcut_content_height, true);

            // Set notification message & info
            char type = mNotifyData.getString(COLUMN_INDEX_OBJECT_TYPE).charAt(0);

            SpannableStringBuilder message = getNotifyMessage(pseudoColor);
            SpannableStringBuilder info = null;
            switch (type) {

                case NotificationsTable.TYPE_SHARED: { ////// Photo added (into shared album)

                    String album = mNotifyData.getString(COLUMN_INDEX_ALBUM);
                    info = new SpannableStringBuilder(getString(R.string.notify_shared_info, album));
                    int albumPos = getResources().getInteger(R.integer.notify_shared_info_album_pos);
                    info.setSpan(new ForegroundColorSpan(Color.BLUE), albumPos, albumPos + album.length(), 0);
                    break;
                }
                case NotificationsTable.TYPE_WALL: { ////// Wall publication

                    info = new SpannableStringBuilder(
                            (!mNotifyData.isNull(COLUMN_INDEX_PUB_TEXT))?
                                    mNotifyData.getString(COLUMN_INDEX_PUB_TEXT).replaceAll("\\s{2,}", " "):
                                    getString(R.string.notify_no_info));
                    break;
                }
                case NotificationsTable.TYPE_MAIL: { ////// Mail received

                    info = (SpannableStringBuilder)Html.fromHtml(mNotifyData.getString(COLUMN_INDEX_MSG_TEXT)
                            .replaceAll("\\s{2,}", " "));
                    break;
                }
                case NotificationsTable.TYPE_PUB_COMMENT:
                case NotificationsTable.TYPE_PIC_COMMENT: { ////// Comment added (onto publication or photo)

                    info = new SpannableStringBuilder(mNotifyData.getString(COLUMN_INDEX_COM_TEXT));
                    break;
                }
            }
            holder.mNotifyMessage.setText(message, TextView.BufferType.SPANNABLE);
            holder.mNotifyInfo.setText(info, TextView.BufferType.SPANNABLE);

            // Set notification type icon & time
            holder.mNotifyType.setImageDrawable(getResources().getDrawable(Tools.getNotifyIcon(type)));
            holder.mTextTime.setText(mNotifyData.getString(COLUMN_INDEX_DATE).substring(11, 16));

            // Set notify synchronization
            Tools.setSyncView(getContext(), holder.mSyncDate, holder.mSyncIcon,
                    mNotifyData.getString(COLUMN_INDEX_STATUS_DATE),
                    (byte)mNotifyData.getInt(COLUMN_INDEX_SYNC));

            // Events
            holder.mNotifyData.setOnClickListener(this);
            holder.mImagePseudo.setOnClickListener(this);
        }

        @Override
        public int getItemCount() {
            return mNotifyData.getCount();
        }

        //////
        public class ViewHolder extends RecyclerView.ViewHolder {

            public final LinearLayout mDateSeparator; // Date separator view
            public final TextView mTextDate; // Notification date text view
            public final ImageView mImagePseudo; // Pseudo profile image view
            public final RelativeLayout mNotifyData; // Notification data layout view
            public final TextView mNotifyMessage; // Notification message text view
            public final TextView mNotifyInfo; // Notification info text view
            public final ImageView mNotifyType; // Notification type image view
            public final TextView mTextTime; // Notification time text view

            public final TextView mSyncDate; // Notify synchronization date text view
            public final ImageView mSyncIcon; // Notify synchronization image view

            //
            public ViewHolder(View view) {
                super(view);
                //Logs.add(Logs.Type.V, "view: " + view);

                mDateSeparator = (LinearLayout)view.findViewById(R.id.date_separator);
                mTextDate = (TextView)view.findViewById(R.id.text_date);
                mImagePseudo = (ImageView)view.findViewById(R.id.image_pseudo);
                mNotifyData = (RelativeLayout)view.findViewById(R.id.layout_data);
                mNotifyMessage = (TextView)view.findViewById(R.id.text_message);
                mNotifyInfo = (TextView)view.findViewById(R.id.text_info);
                mNotifyType = (ImageView)view.findViewById(R.id.image_type);
                mTextTime = (TextView)view.findViewById(R.id.text_time);

                mSyncDate = (TextView)view.findViewById(R.id.text_sync_date);
                mSyncIcon = (ImageView)view.findViewById(R.id.image_sync);
            }
        }
    }



















    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);

        cursor.moveToFirst();
        if (id == Queries.MAIN_NOTIFICATIONS) {

            // Fill shortcut
            mNotifyData = cursor;
            mListener.onSetMessage(Constants.MAIN_SECTION_NOTIFICATIONS,
                    getNotifyMessage(R.color.colorPrimaryProfile));
            boolean unread = mNotifyData.getInt(COLUMN_INDEX_LU_FLAG) == Constants.DATA_UNREAD;
            char type = mNotifyData.getString(COLUMN_INDEX_OBJECT_TYPE).charAt(0);
            SpannableStringBuilder info = new SpannableStringBuilder(getString(R.string.notification_info));
            if (unread) {

                int unreadPos = info.length() + 2; // ' ' + '(' = 2
                String notRead = getString(R.string.unread);
                info.append(" (" + notRead + ")");
                info.setSpan(new StyleSpan(Typeface.BOLD), unreadPos, unreadPos + notRead.length(), 0);
            }
            boolean female = (!mNotifyData.isNull(COLUMN_INDEX_SEX)) &&
                    (mNotifyData.getInt(COLUMN_INDEX_SEX) == CamaradesTable.FEMALE);
            String profile = (!mNotifyData.isNull(COLUMN_INDEX_PROFILE))?
                    mNotifyData.getString(COLUMN_INDEX_PROFILE) : null;

            mListener.onSetNotify(type, !unread);
            mListener.onSetInfo(Constants.MAIN_SECTION_NOTIFICATIONS, info);
            mListener.onSetDate(Constants.MAIN_SECTION_NOTIFICATIONS, false, mNotifyData.getString(COLUMN_INDEX_DATE));
            mListener.onSetIcon(Constants.MAIN_SECTION_NOTIFICATIONS, female, profile,
                    R.dimen.shortcut_content_height);

            // Fill notification list
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
        mNotifyList = (RecyclerView)rootView.findViewById(R.id.list_notification);

        // Load notification data (using query loader)
        Bundle queryData = new Bundle();
        queryData.putString(QueryLoader.DATA_KEY_SELECTION,
                "SELECT " +
                        NotificationsTable.COLUMN_OBJECT_TYPE + "," + // COLUMN_INDEX_OBJECT_TYPE
                        NotificationsTable.COLUMN_STATUS_DATE + "," + // COLUMN_INDEX_STATUS_DATE
                        NotificationsTable.TABLE_NAME + "." +
                            Constants.DATA_COLUMN_SYNCHRONIZED + "," + // COLUMN_INDEX_SYNC
                        NotificationsTable.TABLE_NAME + "." +
                            IDataTable.DataField.COLUMN_ID + "," + // COLUMN_INDEX_NOTIFY_ID
                        NotificationsTable.COLUMN_DATE + "," + // COLUMN_INDEX_DATE
                        NotificationsTable.COLUMN_LU_FLAG + "," + // COLUMN_INDEX_LU_FLAG
                        CamaradesTable.COLUMN_PSEUDO + "," + // COLUMN_INDEX_PSEUDO
                        CamaradesTable.COLUMN_SEXE + "," + // COLUMN_INDEX_SEX
                        CamaradesTable.COLUMN_PROFILE + "," + // COLUMN_INDEX_PROFILE
                        CamaradesTable.TABLE_NAME + "." +
                            IDataTable.DataField.COLUMN_ID + "," + // COLUMN_INDEX_MEMBER_ID
                        PhotosTable.COLUMN_ALBUM + "," + // COLUMN_INDEX_ALBUM
                        PhotosTable.TABLE_NAME + "." +
                            IDataTable.DataField.COLUMN_ID + "," + // COLUMN_INDEX_PHOTO_ID
                        ActualitesTable.COLUMN_TEXT + "," + // COLUMN_INDEX_PUB_TEXT
                        ActualitesTable.COLUMN_LINK + "," + // COLUMN_INDEX_LINK
                        ActualitesTable.COLUMN_FICHIER + "," + // COLUMN_INDEX_FICHIER
                        ActualitesTable.TABLE_NAME + "." +
                            IDataTable.DataField.COLUMN_ID + "," + // COLUMN_INDEX_PUB_ID
                        MessagerieTable.COLUMN_MESSAGE + "," + // COLUMN_INDEX_MSG_TEXT
                        MessagerieTable.TABLE_NAME + "." +
                            IDataTable.DataField.COLUMN_ID + "," + // COLUMN_INDEX_MSG_ID
                        CommentairesTable.COLUMN_TEXT + "," + // COLUMN_INDEX_COM_TEXT
                        CommentairesTable.TABLE_NAME + "." +
                            IDataTable.DataField.COLUMN_ID + // COLUMN_INDEX_COMMENT_ID

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
                        " LIMIT " + Queries.LIMIT_NOTIFICATIONS);
        mNotifyLoader.restart(getActivity(), Queries.MAIN_NOTIFICATIONS, queryData);

        // Set shortcut data (default)
        SpannableStringBuilder data = new SpannableStringBuilder(getString(R.string.no_notification));
        mListener.onSetMessage(Constants.MAIN_SECTION_NOTIFICATIONS, data);
        data = new SpannableStringBuilder(getString(R.string.no_notification_info));
        mListener.onSetInfo(Constants.MAIN_SECTION_NOTIFICATIONS, data);

        return rootView;
    }
}
