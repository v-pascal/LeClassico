package com.studio.artaban.leclassico.activities.main;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.tables.MessagerieTable;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;

/**
 * Created by pascal on 05/09/16.
 * Home fragment class (MainActivity)
 */
public class HomeFragment extends MainFragment implements QueryLoader.OnResultListener {

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
        mListener.onGetShortcut(Constants.MAIN_SECTION_HOME, false).setInfo(infoBuilder);
    }

    private QueryLoader mMailLoader; // Shortcut new mail query loader
    private QueryLoader mNewNotifyLoader; // Shortcut new notification query loader

    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        cursor.moveToFirst();

        switch (id) {
            case Queries.MAIN_SHORTCUT_MAIL_COUNT: { ////// New mail count
                mNewMail = cursor.getInt(0);
                break;
            }
            case Queries.MAIN_SHORTCUT_NOTIFY_COUNT: { ////// New notification count
                mNewNotification = cursor.getInt(0);
                break;
            }
        }
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
        String pseudo = getActivity().getIntent().getStringExtra(MainActivity.EXTRA_DATA_PSEUDO);
        int pseudoPos = getResources().getInteger(R.integer.home_connected_pseudo_pos);
        SpannableStringBuilder msgBuilder =
                new SpannableStringBuilder(getString(R.string.home_connected, pseudo));
        msgBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimarySetting)),
                pseudoPos, pseudoPos + pseudo.length(), 0);

        mListener.onGetShortcut(Constants.MAIN_SECTION_HOME, false).setMessage(msgBuilder);
        setShortcutInfo();














        // Set home info (using query loaders)
        Bundle mailData = new Bundle();
        mailData.putParcelable(QueryLoader.DATA_KEY_URI, mListener.onGetShortcutURI());
        mailData.putString(QueryLoader.DATA_KEY_SELECTION,
                "SELECT count(*) FROM " + MessagerieTable.TABLE_NAME +
                        " WHERE " + MessagerieTable.COLUMN_PSEUDO + "='" + pseudo +
                        "' AND " + MessagerieTable.COLUMN_LU_FLAG + '=' + Constants.DATA_UNREAD);
        mMailLoader.init(getActivity(), Queries.MAIN_SHORTCUT_MAIL_COUNT, mailData);

        Bundle notifyData = new Bundle();
        notifyData.putParcelable(QueryLoader.DATA_KEY_URI, mListener.onGetShortcutURI());
        notifyData.putString(QueryLoader.DATA_KEY_SELECTION,
                "SELECT count(*) FROM " + NotificationsTable.TABLE_NAME +
                        " WHERE " + NotificationsTable.COLUMN_PSEUDO + "='" + pseudo +
                        "' AND " + NotificationsTable.COLUMN_LU_FLAG + '=' + Constants.DATA_UNREAD);
        mNewNotifyLoader.init(getActivity(), Queries.MAIN_SHORTCUT_NOTIFY_COUNT, notifyData);

        return rootView;
    }
}
