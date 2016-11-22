package com.studio.artaban.leclassico.activities.main;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.album.PhotoFragment;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.data.tables.MessagerieTable;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;
import com.studio.artaban.leclassico.tools.SizeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        infoBuilder.setSpan(new ForegroundColorSpan(Color.WHITE), mailPos, mailPos + mailsLen, 0);
        infoBuilder.setSpan(new ForegroundColorSpan(Color.WHITE), notifyPos, notifyPos +
                String.valueOf(mNewNotification).length(), 0);
        try {
            mListener.onGetShortcut(Constants.MAIN_SECTION_HOME, false).setInfo(infoBuilder);
        } catch (NullPointerException e) {
            Logs.add(Logs.Type.F, "Activity not attached");
        }
    }

    private QueryLoader mMailLoader; // Shortcut new mail query loader
    private QueryLoader mNewNotifyLoader; // Shortcut new notification query loader

    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        switch (id) {

            case Queries.MAIN_SHORTCUT_MAIL_COUNT: { ////// New mail count
                mNewMail = 0;
                if (!cursor.moveToFirst())
                    return;

                mNewMail = cursor.getInt(0);
                break;
            }
            case Queries.MAIN_SHORTCUT_NOTIFY_COUNT: { ////// New notification count
                mNewNotification = 0;
                if (!cursor.moveToFirst())
                    return;

                do {
                    if (cursor.getInt(0) == Constants.DATA_UNREAD)
                        ++mNewNotification;
                    else
                        break;

                } while (cursor.moveToNext());
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
        String pseudo = getActivity().getIntent().getStringExtra(Login.EXTRA_DATA_PSEUDO);
        int pseudoPos = getResources().getInteger(R.integer.home_connected_pseudo_pos);
        SpannableStringBuilder msgBuilder =
                new SpannableStringBuilder(getString(R.string.home_connected, pseudo));
        msgBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccentMain)),
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
                "SELECT " + NotificationsTable.COLUMN_LU_FLAG + " FROM " + NotificationsTable.TABLE_NAME +
                        " WHERE " + NotificationsTable.COLUMN_PSEUDO + "='" + pseudo +
                        "' ORDER BY " + NotificationsTable.COLUMN_DATE + " DESC");
        mNewNotifyLoader.init(getActivity(), Queries.MAIN_SHORTCUT_NOTIFY_COUNT, notifyData);

        // Display pseudo into title (colored)
        SpannableStringBuilder welcome = new SpannableStringBuilder(getString(R.string.main_welcome, pseudo));
        pseudoPos = getResources().getInteger(R.integer.home_welcome_pseudo_pos);
        welcome.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimarySetting)),
                pseudoPos, pseudoPos + pseudo.length(), 0);
        ((TextView)rootView.findViewById(R.id.text_welcome)).setText(welcome, TextView.BufferType.SPANNABLE);

        // Set image size of the light
        SizeUtils.screenRatio(getActivity(), rootView.findViewById(R.id.image_light), true, 1f/7f);

        // Add introduction link
        Linkify.addLinks((TextView) rootView.findViewById(R.id.text_intro),
                Pattern.compile("LeClassico"), "http");

        // Fill best photo container
        PhotoFragment photo = new PhotoFragment();
        Bundle data = new Bundle();
        data.putInt(PhotoFragment.ARG_KEY_TYPE, PhotoFragment.Type.BEST.getValue());
        photo.setArguments(data);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.photo_container, photo).commit();

        // Format comrade definition (add color)
        TextView comrade = (TextView)rootView.findViewById(R.id.text_comrade);
        SpannableStringBuilder definition = new SpannableStringBuilder(comrade.getText());
        int comradeLen = getResources().getInteger(R.integer.home_comrade_lenght);
        definition.setSpan(new ForegroundColorSpan(Color.RED), 0, comradeLen, 0);
        comrade.setText(definition);

        // Add shortcuts links
        ((TextView)rootView.findViewById(R.id.text_shortcut_web)).setText(Constants.APP_WEBSITE);

        TextView mailbox = (TextView)rootView.findViewById(R.id.text_shortcut_mailbox);
        Linkify.addLinks(mailbox, Pattern.compile(mailbox.getText().toString(), 0), Constants.DATA_CONTENT_SCHEME,
                null, new Linkify.TransformFilter() {
                    @Override
                    public String transformUrl(Matcher match, String url) {

                        // content://com.studio.artaban.provider.leclassico/User/#/Messagerie
                        return Uris.getUri(Uris.ID_USER_MAILBOX, String.valueOf(getActivity().getIntent()
                                .getIntExtra(Login.EXTRA_DATA_PSEUDO_ID, Constants.NO_DATA))).toString();
                    }
                });

        TextView location = (TextView)rootView.findViewById(R.id.text_shortcut_location);
        Linkify.addLinks(location, Pattern.compile(location.getText().toString(), 0), Constants.DATA_CONTENT_SCHEME,
                null, new Linkify.TransformFilter() {
                    @Override
                    public String transformUrl(Matcher match, String url) {

                        // content://com.studio.artaban.provider.leclassico/User/#/Location
                        return Uris.getUri(Uris.ID_USER_LOCATIONS, String.valueOf(getActivity().getIntent()
                                .getIntExtra(Login.EXTRA_DATA_PSEUDO_ID, Constants.NO_DATA))).toString();
                    }
                });

        return rootView;
    }
}
