package com.studio.artaban.leclassico.activities.main;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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

    private static class ViewHolder {

        TextView letter; // First letter view
        ImageView profile; // Profile image view
        TextView pseudo; // Pseudo text view
        TextView info; // Info text view (phone, email, or...)
        ImageView followed; // Image view to display if he's followed by connected user
    }
    private class MembersAdapter extends CursorAdapter implements View.OnClickListener { ///////////
        public MembersAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        ////// OnClickListener /////////////////////////////////////////////////////////////////////
        @Override
        public void onClick(View sender) {
            Logs.add(Logs.Type.V, "sender: " + sender);
            int pseudoId = (int) sender.getTag(R.id.tag_pseudo_id);

            switch (sender.getId()) {
                case R.id.image_pseudo: { // Display profile
                    Logs.add(Logs.Type.V, "Display profile #" + pseudoId);





                    break;
                }
                case R.id.image_followed: { // Change followed status
                    Logs.add(Logs.Type.V, "Change followed status #" + pseudoId);





                    break;
                }
            }
        }

        ////// CursorAdapter ///////////////////////////////////////////////////////////////////////
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            //Logs.add(Logs.Type.V, "context: " + context + ";cursor: " + cursor + ";parent: " + parent);
            View rootView = LayoutInflater.from(context).inflate(R.layout.layout_member_item, null);

            ViewHolder holder = new ViewHolder();
            holder.letter = (TextView) rootView.findViewById(R.id.text_letter);
            holder.profile = (ImageView) rootView.findViewById(R.id.image_pseudo);
            holder.pseudo = (TextView) rootView.findViewById(R.id.text_pseudo);
            holder.info = (TextView) rootView.findViewById(R.id.text_info);
            holder.followed = (ImageView) rootView.findViewById(R.id.image_followed);

            ////// Events
            holder.profile.setOnClickListener(this);
            holder.followed.setOnClickListener(this);

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
            Tools.setProfile(getActivity(), holder.profile, female, profile,
                    R.dimen.user_item_height, true);

            // Set & display letter
            String pseudo = cursor.getString(COLUMN_INDEX_PSEUDO);
            String letter = String.valueOf(pseudo.charAt(0)).toUpperCase();

            if (cursor.getPosition() > 0) {
                cursor.move(-1);
                if (String.valueOf(cursor.getString(COLUMN_INDEX_PSEUDO).charAt(0)).toUpperCase()
                        .charAt(0) != letter.charAt(0))
                    holder.letter.setText(letter);
                else
                    holder.letter.setText(null);

                // Back to current position
                cursor.move(1);
            }
            else
                holder.letter.setText(letter);

            // Set pseudo & info
            holder.pseudo.setText(pseudo);
            if (!cursor.isNull(COLUMN_INDEX_PHONE))
                holder.info.setText(cursor.getString(COLUMN_INDEX_PHONE));
            else if (!cursor.isNull(COLUMN_INDEX_EMAIL))
                holder.info.setText(cursor.getString(COLUMN_INDEX_EMAIL));
            else if (!cursor.isNull(COLUMN_INDEX_NAME))
                holder.info.setText(cursor.getString(COLUMN_INDEX_NAME));
            else if (!cursor.isNull(COLUMN_INDEX_TOWN))
                holder.info.setText(cursor.getString(COLUMN_INDEX_TOWN));
            else if (!cursor.isNull(COLUMN_INDEX_ADDRESS))
                holder.info.setText(cursor.getString(COLUMN_INDEX_ADDRESS));
            else
                holder.info.setText(getString((cursor.getInt(COLUMN_INDEX_ADMIN) == 1)?
                        R.string.admin_privilege:R.string.no_admin_privilege));

            // Set followed info
            int colorId = (cursor.isNull(COLUMN_INDEX_FOLLOWED_DATE))?
                    getResources().getColor(R.color.light_gray):Color.BLACK;
            holder.followed.setImageTintList(new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_enabled},
                            new int[]{android.R.attr.state_enabled},
                            new int[]{-android.R.attr.state_checked},
                            new int[]{android.R.attr.state_pressed}
                    },
                    new int[]{colorId, colorId, colorId, colorId}));

            ////// Events
            int pseudoId = cursor.getInt(COLUMN_INDEX_ID);
            holder.profile.setTag(R.id.tag_pseudo_id, pseudoId);
            holder.followed.setTag(R.id.tag_pseudo_id, pseudoId);
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
            if (getListAdapter() == null) {
                setListAdapter(new MembersAdapter(getContext(), cursor, false));
                getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
                    private int mLetterItem; // Item position that displays the letter on top

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        //Logs.add(Logs.Type.V, "view: " + view + ";firstVisibleItem: " + firstVisibleItem +
                        //        ";visibleItemCount: " + visibleItemCount + ";totalItemCount: " + totalItemCount);




                        if (view.getChildAt(0) != null) {
                            //Logs.add(Logs.Type.E, "item: " + firstVisibleItem + " Y:" + view.getChildAt(0).getY());
                        }



                        /*
01-03 17:06:58.703 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:0.0
01-03 17:06:58.728 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:0.0
01-03 17:07:08.265 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:0.0
01-03 17:07:09.469 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-2.0
01-03 17:07:09.524 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-7.0
01-03 17:07:09.593 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-10.0
01-03 17:07:09.620 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-14.0
01-03 17:07:09.648 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-19.0
01-03 17:07:09.688 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-23.0
01-03 17:07:09.705 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-26.0
01-03 17:07:09.756 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-27.0
01-03 17:07:09.771 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-28.0
01-03 17:07:09.785 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-28.0
01-03 17:07:09.799 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-29.0
01-03 17:07:09.813 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-29.0
01-03 17:07:09.832 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-29.0
01-03 17:07:09.845 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-29.0
01-03 17:07:09.856 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-29.0
01-03 17:07:09.868 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-29.0
01-03 17:07:09.884 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-29.0
01-03 17:07:21.199 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-29.0
01-03 17:07:21.607 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-30.0
01-03 17:07:21.622 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-33.0
01-03 17:07:21.662 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-35.0
01-03 17:07:21.717 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-39.0
01-03 17:07:21.745 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-44.0
01-03 17:07:21.774 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 0 Y:-51.0
01-03 17:07:21.788 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 1 Y:-3.0
01-03 17:07:21.815 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 1 Y:-6.0
01-03 17:07:21.829 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 1 Y:-10.0
01-03 17:07:21.856 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 1 Y:-14.0
01-03 17:07:21.884 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 1 Y:-17.0
01-03 17:07:21.943 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 1 Y:-22.0
01-03 17:07:21.993 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 1 Y:-25.0
01-03 17:07:22.034 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 1 Y:-30.0
01-03 17:07:22.054 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 1 Y:-33.0
01-03 17:07:22.063 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 1 Y:-38.0
01-03 17:07:22.077 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 1 Y:-44.0
01-03 17:07:22.086 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 1 Y:-47.0
01-03 17:07:22.093 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 1 Y:-49.0
01-03 17:07:22.103 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-3.0
01-03 17:07:22.116 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-8.0
01-03 17:07:22.133 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-13.0
01-03 17:07:22.144 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-17.0
01-03 17:07:22.178 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-21.0
01-03 17:07:22.185 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-28.0
01-03 17:07:22.197 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-30.0
01-03 17:07:22.204 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-30.0
01-03 17:07:22.220 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-34.0
01-03 17:07:22.233 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-36.0
01-03 17:07:22.244 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-37.0
01-03 17:07:22.261 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-39.0
01-03 17:07:22.278 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-41.0
01-03 17:07:22.291 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-42.0
01-03 17:07:22.304 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-43.0
01-03 17:07:22.321 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-44.0
01-03 17:07:22.340 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-45.0
01-03 17:07:22.356 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-45.0
01-03 17:07:22.371 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-46.0
01-03 17:07:22.384 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-46.0
01-03 17:07:22.396 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-47.0
01-03 17:07:22.410 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-47.0
01-03 17:07:22.421 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-47.0
01-03 17:07:22.434 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-47.0
01-03 17:07:22.446 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-47.0
01-03 17:07:22.461 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-47.0
01-03 17:07:22.473 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-47.0
01-03 17:07:34.684 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 2 Y:-51.0
01-03 17:07:34.699 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 3 Y:-11.0
01-03 17:07:34.722 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 3 Y:-27.0
01-03 17:07:34.735 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 3 Y:-51.0
01-03 17:07:34.740 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 3 Y:-51.0
01-03 17:07:34.765 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 4 Y:-33.0
01-03 17:07:34.803 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 6 Y:-40.0
01-03 17:07:34.806 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 6 Y:-44.0
01-03 17:07:34.809 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 6 Y:-44.0
01-03 17:07:34.830 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 8 Y:-4.0
01-03 17:07:34.869 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 9 Y:-31.0
01-03 17:07:34.875 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 9 Y:-31.0
01-03 17:07:34.934 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 13 Y:-43.0
01-03 17:07:34.971 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 14 Y:-19.0
01-03 17:07:34.976 28686-28686/ E/LeClassico: [.activities.main.MembersFragment$1]{onScroll} item: 14 Y:-19.0

                         */



                    }
                });

            } else
                ((CursorAdapter) getListAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset() {
        Logs.add(Logs.Type.V, null);
        setListAdapter(null);
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
                "SELECT " + CamaradesTable.TABLE_NAME + '.' + IDataTable.DataField.COLUMN_ID + ',' + // COLUMN_INDEX_ID
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
                        " WHERE " + CamaradesTable.COLUMN_PSEUDO + "<>'" + pseudo + '\'' +
                        " ORDER BY " + CamaradesTable.COLUMN_PSEUDO + " ASC");
        mMembersLoader.init(getActivity(), Queries.MAIN_MEMBERS_LIST, membersData);

        setListAdapter(null);
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logs.add(Logs.Type.V, null);
        mListener = null;
    }
}
