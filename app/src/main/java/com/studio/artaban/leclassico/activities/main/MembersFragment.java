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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.Constants;
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

    private Uri mListUri; // Member list query URI
    private Uri mLastUri; // Shortcut last followed member query URI
    private QueryLoader mListLoader; // Member list query loader
    private QueryLoader mLastLoader; // Shortcut last followed member query loader

    private static class ViewHolder {

        TextView character; // Alphabetic char view
        ImageView profile; // Profile image view
        LinearLayout layout; // User info layout (selectable)
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
                case R.id.layout_pseudo:
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
            holder.character = (TextView) rootView.findViewById(R.id.text_letter);
            holder.profile = (ImageView) rootView.findViewById(R.id.image_pseudo);
            holder.layout = (LinearLayout) rootView.findViewById(R.id.layout_pseudo);
            holder.pseudo = (TextView) rootView.findViewById(R.id.text_pseudo);
            holder.info = (TextView) rootView.findViewById(R.id.text_info);
            holder.followed = (ImageView) rootView.findViewById(R.id.image_followed);

            ////// Events
            holder.profile.setOnClickListener(this);
            holder.layout.setOnClickListener(this);
            holder.followed.setOnClickListener(this);

            rootView.setTag(holder);
            return rootView;
        }
        @Override
        public void bindView(final View view, Context context, Cursor cursor) {
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

            holder.character.setTranslationY(0);
            if (cursor.getPosition() > 0) {

                cursor.move(-1);
                holder.character.setText(letter);
                holder.character.setVisibility((String.valueOf(cursor.getString(COLUMN_INDEX_PSEUDO)
                        .charAt(0)).toUpperCase().charAt(0) != letter.charAt(0))? View.VISIBLE:View.INVISIBLE);

                cursor.move(1); // Back to current position

            } else {
                holder.character.setText(letter);
                holder.character.setVisibility(View.VISIBLE);
            }
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
            int colorId = (cursor.isNull(COLUMN_INDEX_FOLLOWED))?
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
            holder.layout.setTag(R.id.tag_pseudo_id, pseudoId);
            holder.followed.setTag(R.id.tag_pseudo_id, pseudoId);
        }
    }

    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        if (!cursor.moveToFirst())
            return;

        switch (id) {
            case Queries.MAIN_SHORTCUT_LAST_FOLLOWED: {
                Logs.add(Logs.Type.I, "Last followed member");
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
            case Queries.MAIN_MEMBERS_LIST: {
                Logs.add(Logs.Type.I, "Refresh members list");

                if (getListAdapter() == null) {
                    setListAdapter(new MembersAdapter(getContext(), cursor, false));

                    getListView().setClipChildren(false);
                    getListView().setClipToPadding(false);
                    getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
                        // Needed to move alphabetic char order on displayed on top (according user scroll)

                        private ViewHolder mCharItem; // Item holder that displays the alphabetic char
                        private int mCharPosition; // Item position that displays the alphabetic char
                        private char mCharLetter; // Alphabetic letter displayed (on top)

                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {

                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                             int totalItemCount) {
                            //Logs.add(Logs.Type.V, "view: " + view + ";firstVisibleItem: " + firstVisibleItem +
                            //        ";visibleItemCount: " + visibleItemCount +
                            //        ";totalItemCount: " + totalItemCount);

                            if (view.getChildAt(0) != null) {
                                View itemView = view.getChildAt(0);
                                View nextView = view.getChildAt(1);

                                if ((mCharItem == null) || (mCharPosition != firstVisibleItem)) {
                                    mCharItem = (ViewHolder) itemView.getTag();
                                    mCharItem.character.setVisibility(View.VISIBLE);
                                    mCharLetter = mCharItem.character.getText().charAt(0);

                                    if ((mCharPosition >= firstVisibleItem) && (nextView != null) && (((ViewHolder)nextView.getTag()).character.getText().charAt(0) == mCharLetter))
                                        ((ViewHolder)nextView.getTag()).character.setVisibility(View.INVISIBLE);
                                }
                                mCharItem.character.setTranslationY(((nextView != null) &&
                                        (((ViewHolder)nextView.getTag()).character.getText().charAt(0) == mCharLetter))?
                                        -itemView.getY() : 0);
                                mCharPosition = firstVisibleItem;
                            }
                        }
                    });

                } else
                    ((CursorAdapter) getListAdapter()).notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onLoaderReset() {
        Logs.add(Logs.Type.V, null);
        setListAdapter(null);
    }

    // Query column indexes (both query loaders)
    private static final int COLUMN_INDEX_ID = 0; // & Shortcut status date
    private static final int COLUMN_INDEX_PSEUDO = 1;
    private static final int COLUMN_INDEX_SEX = 2;
    private static final int COLUMN_INDEX_PROFILE = 3;
    private static final int COLUMN_INDEX_PHONE = 4;
    private static final int COLUMN_INDEX_EMAIL = 5;
    private static final int COLUMN_INDEX_TOWN = 6;
    private static final int COLUMN_INDEX_NAME = 7;
    private static final int COLUMN_INDEX_ADDRESS = 8;
    private static final int COLUMN_INDEX_ADMIN = 9;
    private static final int COLUMN_INDEX_FOLLOWED = 10;

    ////// MainFragment ////////////////////////////////////////////////////////////////////////////
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Logs.add(Logs.Type.V, "context: " + context);
        mLastLoader = new QueryLoader(context, this);
        mListLoader = new QueryLoader(context, this);

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

        // Set URIs to observe shortcut DB changes
        int pseudoId = getActivity().getIntent().getIntExtra(Login.EXTRA_DATA_PSEUDO_ID, Constants.NO_DATA);
        mLastUri = Uris.getUri(Uris.ID_MAIN_SHORTCUT_MEMBER, String.valueOf(pseudoId));
        mListUri = Uris.getUri(Uris.ID_USER_MEMBERS, String.valueOf(pseudoId));

        // Load shortcut & members info (using query loaders)
        String pseudo = getActivity().getIntent().getStringExtra(Login.EXTRA_DATA_PSEUDO);

        Bundle shortcutData = new Bundle();
        shortcutData.putParcelable(QueryLoader.DATA_KEY_URI, mLastUri);
        shortcutData.putString(QueryLoader.DATA_KEY_SELECTION,
                "SELECT max(" + AbonnementsTable.TABLE_NAME + '.' + Constants.DATA_COLUMN_STATUS_DATE + ")," + // COLUMN_INDEX_ID
                        CamaradesTable.COLUMN_PSEUDO + ',' + // COLUMN_INDEX_PSEUDO
                        CamaradesTable.COLUMN_SEXE + ',' + // COLUMN_INDEX_SEX
                        CamaradesTable.COLUMN_PROFILE + // COLUMN_INDEX_PROFILE
                        " FROM " + AbonnementsTable.TABLE_NAME +
                        " LEFT JOIN " + CamaradesTable.TABLE_NAME + " ON " +
                        AbonnementsTable.COLUMN_CAMARADE + '=' + CamaradesTable.COLUMN_PSEUDO + " AND " +
                        AbonnementsTable.COLUMN_CAMARADE + "<>'" + pseudo + '\'' +
                        " WHERE " + AbonnementsTable.COLUMN_PSEUDO + "='" + pseudo + '\'');
        mLastLoader.init(getActivity(), Queries.MAIN_SHORTCUT_LAST_FOLLOWED, shortcutData);

        Bundle membersData = new Bundle();
        membersData.putParcelable(QueryLoader.DATA_KEY_URI, mListUri);
        mListLoader.init(getActivity(), Queries.MAIN_MEMBERS_LIST, membersData);





        /*
        Bundle membersData = new Bundle();
        membersData.putParcelable(QueryLoader.DATA_KEY_URI, mListUri);
        membersData.putString(QueryLoader.DATA_KEY_URI_FILTER, "Pasc");
        mLastLoader.restart(getActivity(), Queries.MAIN_MEMBERS_LIST, membersData);
        */





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
