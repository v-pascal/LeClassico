package com.studio.artaban.leclassico.activities.main;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.data.tables.AbonnementsTable;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;

/**
 * Created by pascal on 05/09/16.
 * Members fragment class (MainActivity)
 */
public class MembersFragment extends MainFragment implements QueryLoader.OnResultListener {

    private QueryLoader mLastMemberLoader; // Shortcut last followed member query loader

    // Query column indexes
    private static final int COLUMN_INDEX_PSEUDO = 1;
    private static final int COLUMN_INDEX_SEX = 2;
    private static final int COLUMN_INDEX_PROFILE = 3;

    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        if (!cursor.moveToFirst())
            return;

        if (id == Queries.MAIN_SHORTCUT_LAST_FOLLOWED) {
            if (cursor.getString(COLUMN_INDEX_PSEUDO) == null)
                return; // No data found yet

            try {
                // Set message
                SpannableStringBuilder member = new SpannableStringBuilder(cursor.getString(COLUMN_INDEX_PSEUDO));
                mListener.onGetShortcut(Constants.MAIN_SECTION_MEMBERS, false).setMessage(member);

                // Set profile icon
                boolean female = (!cursor.isNull(COLUMN_INDEX_SEX)) &&
                        (cursor.getInt(COLUMN_INDEX_SEX) == CamaradesTable.FEMALE);
                String profile = (!cursor.isNull(COLUMN_INDEX_PROFILE))?
                        cursor.getString(COLUMN_INDEX_PROFILE) : null;
                mListener.onGetShortcut(Constants.MAIN_SECTION_MEMBERS, false).setIcon(female, profile, R.dimen.shortcut_content_height);

            } catch (NullPointerException e) {
                Logs.add(Logs.Type.F, "Activity not attached");
            }
        }
    }

    @Override
    public void onLoaderReset() {

    }

    ////// MainFragment ////////////////////////////////////////////////////////////////////////////
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logs.add(Logs.Type.V, "context: " + context);
        mLastMemberLoader = new QueryLoader(context, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_members, container, false);
        rootView.setTag(Constants.MAIN_SECTION_MEMBERS);

        // Set shortcut data
        SpannableStringBuilder data = new SpannableStringBuilder(getString(R.string.last_followed_info));
        mListener.onGetShortcut(Constants.MAIN_SECTION_MEMBERS, false).setInfo(data);

        // Load shortcut info (using query loaders)
        String pseudo = getActivity().getIntent().getStringExtra(Login.EXTRA_DATA_PSEUDO);

        Bundle shortcutData = new Bundle();
        shortcutData.putParcelable(QueryLoader.DATA_KEY_URI,
                Uris.getUri(Uris.ID_RAW_QUERY)); // To allow raw query without any notification
        shortcutData.putString(QueryLoader.DATA_KEY_SELECTION,
                "SELECT max(" + AbonnementsTable.TABLE_NAME + '.' + Constants.DATA_COLUMN_STATUS_DATE + ")," +
                        CamaradesTable.COLUMN_PSEUDO + ',' + // COLUMN_INDEX_PSEUDO
                        CamaradesTable.COLUMN_SEXE + ',' + // COLUMN_INDEX_SEX
                        CamaradesTable.COLUMN_PROFILE + // COLUMN_INDEX_PROFILE
                        " FROM " + AbonnementsTable.TABLE_NAME +
                        " LEFT JOIN " + CamaradesTable.TABLE_NAME + " ON " +
                        AbonnementsTable.COLUMN_CAMARADE + '=' + CamaradesTable.COLUMN_PSEUDO + " AND " +
                        AbonnementsTable.COLUMN_CAMARADE + "<>'" + pseudo + '\'' +
                        " WHERE " + AbonnementsTable.COLUMN_PSEUDO + "='" + pseudo + '\'');
        mLastMemberLoader.init(getActivity(), Queries.MAIN_SHORTCUT_LAST_FOLLOWED, shortcutData);
















        return rootView;
    }
}
