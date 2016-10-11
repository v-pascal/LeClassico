package com.studio.artaban.leclassico.activities.main;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Queries;
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

    public static final String TAG = "home";

    private int mNewMail; // New mail count
    private int mNewNotification; // New notification count

    //////
    private void setShortcutInfo() { // Set shortcut text info with colors (new mail & notification)

        Logs.add(Logs.Type.V, null);
        SpannableStringBuilder infoBuilder =
                new SpannableStringBuilder(getString(R.string.home_info, mNewMail, mNewNotification));

        int mailsLen = String.valueOf(mNewMail).length();
        int mailPos = getResources().getInteger(R.integer.home_info_mail_pos);
        int notifyPos = getResources().getInteger(R.integer.home_info_notify_pos) + mailsLen;

        infoBuilder.setSpan(new ForegroundColorSpan(Color.BLUE), mailPos, mailPos + mailsLen, 0);
        infoBuilder.setSpan(new ForegroundColorSpan(Color.BLUE), notifyPos, notifyPos +
                String.valueOf(mNewNotification).length(), 0);
        mListener.onGetShortcut(Constants.MAIN_SECTION_HOME).setInfo(infoBuilder);
    }

    private QueryLoader mMailLoader; // Shortcut new mail query loader
    private QueryLoader mNewNotifyLoader; // Shortcut new notification query loader


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
            case Tables.ID_MESSAGERIE: { ////// New mail count
                mNewMail = cursor.getInt(0);
                break;
            }
            case Queries.MAIN_NOTIFICATION_COUNT: { ////// New notification count
                mNewNotification = cursor.getInt(0);
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
        mNewNotifyLoader = new QueryLoader(context, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        rootView.setTag(Constants.MAIN_SECTION_HOME);

        // Set shortcut data
        String pseudo = getActivity().getIntent().getStringExtra(MainActivity.EXTRA_DATA_KEY_PSEUDO);
        int pseudoPos = getResources().getInteger(R.integer.home_connected_pseudo_pos);
        SpannableStringBuilder msgBuilder =
                new SpannableStringBuilder(getString(R.string.home_connected, pseudo));
        msgBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimarySetting)),
                pseudoPos, pseudoPos + pseudo.length(), 0);

        mListener.onGetShortcut(Constants.MAIN_SECTION_HOME).setMessage(msgBuilder);
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
        mNewNotifyLoader.restart(getActivity(), Queries.MAIN_NOTIFICATION_COUNT, shortcutData);














        return rootView;
    }
}
