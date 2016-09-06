package com.studio.artaban.leclassico.activities.main;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.tables.MessagerieTable;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;

/**
 * Created by pascal on 05/09/16.
 * Home fragment class (MainActivity)
 */
public class HomeFragment extends MainFragment implements QueryLoader.OnResultListener {

    private int mNewMails; // New mail count
    private int mNewNotifications; // New notification count

    private void setShortcutInfo() {
    // Set shortcut text info with colors (new messages & albums)

        Logs.add(Logs.Type.V, null);
        SpannableStringBuilder infoBuilder = new SpannableStringBuilder();
        SpannableString info = new SpannableString(getString(R.string.home_info, mNewMails, mNewNotifications));

        String mails = String.valueOf(mNewMails);
        String notifications = String.valueOf(mNewNotifications);
        int messagesPos = info.toString().indexOf(mails);
        int albumsPos = info.toString().lastIndexOf(notifications);

        infoBuilder.append(info);
        infoBuilder.setSpan(new ForegroundColorSpan(Color.BLUE), messagesPos, messagesPos + mails.length(), 0);
        infoBuilder.setSpan(new ForegroundColorSpan(Color.BLUE), albumsPos, albumsPos + notifications.length(), 0);
        mListener.onSetInfo(Constants.MAIN_SECTION_HOME, infoBuilder);
    }

    private QueryLoader mMailLoader; // Shortcut new mails query loader
    private QueryLoader mNotificationsLoader; // Shortcut new photos query loader

    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);

        cursor.moveToFirst();
        switch ((byte)id) {
            case Tables.ID_MESSAGERIE: { ////// New mail count
                mNewMails = cursor.getInt(0);
                break;
            }
            case Tables.ID_NOTIFICATIONS: { ////// New notification count
                mNewNotifications = cursor.getInt(0);
                break;
            }
        }
        cursor.close();
        setShortcutInfo();
    }

    @Override
    public void onLoaderReset() {

    }

    ////// MainFragment ////////////////////////////////////////////////////////////////////////////
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Logs.add(Logs.Type.V, "context: " + context);
        mMailLoader = new QueryLoader(context, this);
        mNotificationsLoader = new QueryLoader(context, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        rootView.setTag(Constants.MAIN_SECTION_HOME);

        // Set shortcut data
        String pseudo = getActivity().getIntent().getStringExtra(MainActivity.EXTRA_DATA_KEY_PSEUDO);
        SpannableStringBuilder msgBuilder = new SpannableStringBuilder();
        SpannableString msg = new SpannableString(getString(R.string.home_welcome, pseudo));
        msgBuilder.append(msg);
        msgBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimarySetting)),
                msg.length() - pseudo.length() - 1, msg.length() - 1, 0);

        mListener.onSetMessage(Constants.MAIN_SECTION_HOME, msgBuilder);
        setShortcutInfo();

        // Load shortcut info (using query loaders)
        Bundle shortcutData = new Bundle();
        shortcutData.putBoolean(QueryLoader.DATA_KEY_URI_SINGLE, false);
        shortcutData.putStringArray(QueryLoader.DATA_KEY_PROJECTION, new String[]{"count(*)"});
        shortcutData.putString(QueryLoader.DATA_KEY_SELECTION,
                MessagerieTable.COLUMN_PSEUDO + "='" + pseudo + "' AND " +
                        MessagerieTable.COLUMN_LU_FLAG + "=0");
        mMailLoader.restart(getActivity(), Tables.ID_MESSAGERIE, shortcutData);

        shortcutData.putBoolean(QueryLoader.DATA_KEY_URI_SINGLE, false);
        shortcutData.putStringArray(QueryLoader.DATA_KEY_PROJECTION, new String[]{"count(*)"});
        shortcutData.putString(QueryLoader.DATA_KEY_SELECTION,
                NotificationsTable.COLUMN_PSEUDO + "='" + pseudo + "' AND " +
                        NotificationsTable.COLUMN_LU_FLAG + "=0");
        mNotificationsLoader.restart(getActivity(), Tables.ID_NOTIFICATIONS, shortcutData);
















        return rootView;
    }
}
