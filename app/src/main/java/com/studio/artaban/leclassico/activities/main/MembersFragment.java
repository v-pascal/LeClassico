package com.studio.artaban.leclassico.activities.main;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.IDataTable;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.data.tables.AbonnementsTable;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;
import com.studio.artaban.leclassico.tools.Tools;

/**
 * Created by pascal on 05/09/16.
 * Members fragment class (MainActivity)
 */
public class MembersFragment extends ListFragment implements QueryLoader.OnResultListener {

    private MainFragment.OnFragmentListener mListener; // Activity listener
    private Uri mMembersUri; // Member list query URI

    private QueryLoader mMembersLoader; // Member list query loader
    private Cursor mListCursor; // Members list cursor

    private MembersAdapter mListAdapter;
    private static class ViewHolder {

        TextView letter; // First letter view

        ImageView profile; // Profile image view
        TextView pseudo; // Pseudo text view
        TextView info; // Info text view (phone, email, or...)
        ImageView followed; // Image view to display if he's followed by connected user
    }
    private class MembersAdapter extends CursorAdapter { ///////////////////////////////////////////
        public MembersAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            //Logs.add(Logs.Type.V, "context: " + context + ";cursor: " + cursor + ";parent: " + parent);
            View rootView = LayoutInflater.from(context).inflate(R.layout.layout_member_item, parent);

            ViewHolder holder = new ViewHolder();
            holder.letter = (TextView) rootView.findViewById(R.id.text_letter);
            holder.profile = (ImageView) rootView.findViewById(R.id.image_pseudo);
            holder.pseudo = (TextView) rootView.findViewById(R.id.text_pseudo);
            holder.info = (TextView) rootView.findViewById(R.id.text_info);
            holder.followed = (ImageView) rootView.findViewById(R.id.image_followed);

            rootView.setTag(holder);
            return rootView;
        }
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            //Logs.add(Logs.Type.V, "view: " + view + ";context: " + context + ";cursor: " + cursor);
            ViewHolder holder = (ViewHolder) view.getTag();

            // Set pseudo icon
            boolean female = (!cursor.isNull(COLUMN_INDEX_SEX)) &&
                    (cursor.getInt(COLUMN_INDEX_SEX) == CamaradesTable.GENDER_FEMALE);
            String profile = (!cursor.isNull(COLUMN_INDEX_PROFILE)) ?
                    cursor.getString(COLUMN_INDEX_PROFILE) : null;
            Tools.setProfile(getActivity(), (ImageView) holder.profile, female,
                    profile, R.dimen.user_item_height, true);

            // Set pseudo & info








            holder.pseudo.setText(cursor.getString(COLUMN_INDEX_PSEUDO));








        }
    }

    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        if (!cursor.moveToFirst())
            return;

        if (id == Queries.MAIN_MEMBERS_LIST) {
            String lastMember = null;

            // Find the last member followed (using status date)
            do {
                if (!cursor.isNull(COLUMN_INDEX_FOLLOWED_DATE)) {
                    String statusDate = cursor.getString(COLUMN_INDEX_FOLLOWED_DATE);
                    if ((lastMember == null) || ((statusDate.compareTo(lastMember)) > 0))
                        lastMember = statusDate;
                }

            } while (cursor.moveToNext());
            cursor.moveToFirst();

            // Display last member into shortcut
            do {
                if ((!cursor.isNull(COLUMN_INDEX_FOLLOWED_DATE)) &&
                        (cursor.getString(COLUMN_INDEX_FOLLOWED_DATE).compareTo(lastMember) == 0)) {
                    try {

                        // Set message
                        SpannableStringBuilder member = new SpannableStringBuilder(cursor.getString(COLUMN_INDEX_PSEUDO));
                        mListener.onGetShortcut(Constants.MAIN_SECTION_MEMBERS, false).setMessage(member);

                        // Set profile icon
                        boolean female = (!cursor.isNull(COLUMN_INDEX_SEX)) &&
                                (cursor.getInt(COLUMN_INDEX_SEX) == CamaradesTable.GENDER_FEMALE);
                        String profile = (!cursor.isNull(COLUMN_INDEX_PROFILE))?
                                cursor.getString(COLUMN_INDEX_PROFILE) : null;
                        mListener.onGetShortcut(Constants.MAIN_SECTION_MEMBERS, false).setIcon(female, profile,
                                R.dimen.shortcut_height);

                    } catch (NullPointerException e) {
                        Logs.add(Logs.Type.F, "Activity not attached");
                    }
                    break;
                }

            } while (cursor.moveToNext());
            cursor.moveToFirst();

            // Refresh members list
            mListCursor = cursor;
            mListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset() {
        Logs.add(Logs.Type.V, null);
        mListCursor = null;
    }

    // Query column indexes
    private static final int COLUMN_INDEX_ID = 0;
    private static final int COLUMN_INDEX_PSEUDO = 1;
    private static final int COLUMN_INDEX_SEX = 2;
    private static final int COLUMN_INDEX_PROFILE = 3;
    private static final int COLUMN_INDEX_PHONE = 4;
    private static final int COLUMN_INDEX_EMAIL = 5;
    private static final int COLUMN_INDEX_TOWN = 6;
    private static final int COLUMN_INDEX_NAME = 7;
    private static final int COLUMN_INDEX_ADDRESS = 8;
    private static final int COLUMN_INDEX_ADMIN = 9;
    private static final int COLUMN_INDEX_FOLLOWED_DATE = 10;

    ////// MainFragment ////////////////////////////////////////////////////////////////////////////
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Logs.add(Logs.Type.V, "context: " + context);
        mMembersLoader = new QueryLoader(context, this);
        mListAdapter = new MembersAdapter(context, mListCursor, false);

        if (context instanceof MainFragment.OnFragmentListener)
            mListener = (MainFragment.OnFragmentListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement 'OnFragmentListener'");
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

        // Set URI to observe shortcut DB changes
        mMembersUri = Uris.getUri(Uris.ID_USER_MEMBERS,
                String.valueOf(getActivity().getIntent().getIntExtra(Login.EXTRA_DATA_PSEUDO_ID, Constants.NO_DATA)));

        // Load shortcut & members info (using query loaders)
        String pseudo = getActivity().getIntent().getStringExtra(Login.EXTRA_DATA_PSEUDO);

        Bundle membersData = new Bundle();
        membersData.putParcelable(QueryLoader.DATA_KEY_URI, mMembersUri);
        membersData.putString(QueryLoader.DATA_KEY_SELECTION,
                "SELECT " + CamaradesTable.TABLE_NAME + '.' + IDataTable.DataField.COLUMN_ID + // COLUMN_INDEX_ID
                        CamaradesTable.COLUMN_PSEUDO + ',' + // COLUMN_INDEX_PSEUDO
                        CamaradesTable.COLUMN_SEXE + ',' + // COLUMN_INDEX_SEX
                        CamaradesTable.COLUMN_PROFILE + ',' + // COLUMN_INDEX_PROFILE
                        CamaradesTable.COLUMN_PHONE + ',' + // COLUMN_INDEX_PHONE
                        CamaradesTable.COLUMN_EMAIL + ',' + // COLUMN_INDEX_EMAIL
                        CamaradesTable.COLUMN_VILLE + ',' + // COLUMN_INDEX_TOWN
                        CamaradesTable.COLUMN_NOM + ',' + // COLUMN_INDEX_NAME
                        CamaradesTable.COLUMN_ADRESSE + ',' + // COLUMN_INDEX_ADDRESS
                        CamaradesTable.COLUMN_ADMIN + ',' + // COLUMN_INDEX_ADMIN
                        AbonnementsTable.TABLE_NAME + '.' + Constants.DATA_COLUMN_STATUS_DATE + // COLUMN_INDEX_FOLLOWED_DATE
                        " FROM " + CamaradesTable.TABLE_NAME +
                        " LEFT JOIN " + AbonnementsTable.TABLE_NAME + " ON " +
                        AbonnementsTable.COLUMN_CAMARADE + '=' + CamaradesTable.COLUMN_PSEUDO + " AND " +
                        AbonnementsTable.COLUMN_PSEUDO + "='" + pseudo + '\'' +
                        " ORDER BY " + CamaradesTable.COLUMN_PSEUDO + " ASC");
        mMembersLoader.init(getActivity(), Queries.MAIN_MEMBERS_LIST, membersData);

        // Add members list adapter
        getListView().setAdapter(mListAdapter);

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logs.add(Logs.Type.V, null);
        mListener = null;
    }
}
