package com.studio.artaban.leclassico.activities.main;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import com.studio.artaban.leclassico.activities.LoggedActivity;
import com.studio.artaban.leclassico.activities.profile.ProfileActivity;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.data.tables.AbonnementsTable;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.helpers.Database;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;
import com.studio.artaban.leclassico.services.DataService;
import com.studio.artaban.leclassico.tools.Tools;

/**
 * Created by pascal on 05/09/16.
 * Members fragment class (MainActivity)
 */
public class MembersFragment extends ListFragment implements QueryLoader.OnResultListener {

    public void updateFilter(String filter) { // Update member filter (pseudo)
        Logs.add(Logs.Type.V, "filter: " + filter);
        refresh(filter);
    }
    private void updateUserFollowers(final String comrade) { // Update user follower list
        Logs.add(Logs.Type.V, "comrade: " + comrade);

        // Update DB (background process)
        new Thread(new Runnable() {
            @Override
            public void run() {
                Logs.add(Logs.Type.V, null);

                Uri uri = Uri.parse(DataProvider.CONTENT_URI + AbonnementsTable.TABLE_NAME);
                String pseudo = getActivity().getIntent().getStringExtra(Login.EXTRA_DATA_PSEUDO);
                String selection = AbonnementsTable.COLUMN_PSEUDO + "='" + pseudo + "' AND " +
                        AbonnementsTable.COLUMN_CAMARADE + "='" + comrade + '\'';
                synchronized (Database.getTable(AbonnementsTable.TABLE_NAME)) {

                    Cursor cursor = getActivity().getContentResolver().query(uri,
                            new String[]{Constants.DATA_COLUMN_SYNCHRONIZED}, selection, null, null);
                    byte sync = Constants.NO_DATA;
                    if (cursor.moveToFirst())
                        sync = (byte) cursor.getInt(0);
                    cursor.close();

                    if ((sync != Constants.NO_DATA) &&
                            (sync != DataTable.Synchronized.DELETED.getValue()) &&
                            (sync != DataTable.Synchronized.TO_DELETE.getValue())) // Currently following

                        getActivity().getContentResolver().delete(uri, selection, null);
                        // NB: Mark DB entry as "to delete"

                    else { // Not following (currently)

                        ContentValues values = new ContentValues();
                        values.put(AbonnementsTable.COLUMN_PSEUDO, pseudo);
                        values.put(AbonnementsTable.COLUMN_CAMARADE, comrade);

                        DataTable.addSyncFields(values, DataTable.Synchronized.TO_INSERT.getValue());
                        // NB: Always marked as "to insert" coz update not available for this table

                        if (sync == Constants.NO_DATA) // Insert entry
                            getActivity().getContentResolver().insert(uri, values);
                        else // Update entry (previously marked as to delete)
                            getActivity().getContentResolver().update(uri, values, selection, null);
                    }
                }
                // Notify change on URI cursors
                getActivity().getContentResolver().notifyChange(mLastUri, null);
                getActivity().getContentResolver().notifyChange(mListUri, null);
            }

        }).start();
    }

    //////
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

            switch (sender.getId()) {
                case R.id.layout_pseudo:
                case R.id.image_pseudo: { // Display profile

                    int pseudoId = (int) sender.getTag(R.id.tag_pseudo_id);
                    Logs.add(Logs.Type.V, "Display profile #" + pseudoId);

                    ////// Start profile activity
                    Intent profile = new Intent(getActivity(), ProfileActivity.class);
                    profile.putExtra(LoggedActivity.EXTRA_DATA_ID, pseudoId);
                    startActivity(profile);
                    break;
                }
                case R.id.image_followed: { // Change followed status

                    String pseudo = (String) sender.getTag(R.id.tag_pseudo);
                    Logs.add(Logs.Type.V, "Change followed status: " + pseudo);
                    updateUserFollowers(pseudo);
                    break;
                }
            }
        }

        ////// CursorAdapter ///////////////////////////////////////////////////////////////////////
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            //Logs.add(Logs.Type.V, "context: " + context + ";cursor: " + cursor + ";parent: " + parent);
            View rootView = LayoutInflater.from(context).inflate(R.layout.layout_follower_item, null);

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
            holder.info.setText(Tools.getUserInfo(getResources(), DataTable.getDataType(cursor),
                    Constants.NO_DATA, COLUMN_INDEX_PHONE));

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
            holder.followed.setTag(R.id.tag_pseudo, pseudo);
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
                    mListener.onGetShortcut(Constants.MAIN_SECTION_MEMBERS, false)
                            .setIcon(cursor.getInt(COLUMN_INDEX_ID), female, profile);

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
                    ((CursorAdapter) getListAdapter()).swapCursor(cursor);
                break;
            }
        }
    }

    @Override
    public void onLoaderReset() {
        Logs.add(Logs.Type.V, null);
        setListAdapter(null);
    }

    // Query column indexes for both query loaders (see 'Queries' class ID_USER_MEMBERS query)
    private static final int COLUMN_INDEX_ID = 0; // & Shortcut status date
    private static final int COLUMN_INDEX_PSEUDO = 1;
    private static final int COLUMN_INDEX_SEX = 2;
    private static final int COLUMN_INDEX_PROFILE = 3;
    private static final int COLUMN_INDEX_PHONE = 4;
    //private static final int COLUMN_INDEX_EMAIL = 5;
    //private static final int COLUMN_INDEX_TOWN = 6;
    //private static final int COLUMN_INDEX_NAME = 7;
    //private static final int COLUMN_INDEX_ADDRESS = 8;
    //private static final int COLUMN_INDEX_ADMIN = 9;
    private static final int COLUMN_INDEX_FOLLOWED = 10;

    private void refresh(String filter) { // Refresh query loader
        //Logs.add(Logs.Type.V, "filter: " + filter);

        Bundle membersData = new Bundle();
        membersData.putParcelable(QueryLoader.DATA_KEY_URI, mListUri);
        membersData.putString(QueryLoader.DATA_KEY_URI_FILTER, filter);
        mListLoader.restart(getActivity(), Queries.MAIN_MEMBERS_LIST, membersData);
    }

    ////// ListFragment ////////////////////////////////////////////////////////////////////////////
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
                        AbonnementsTable.COLUMN_CAMARADE + "<>'" + pseudo + "' AND " +
                        DataTable.getNotDeletedCriteria(AbonnementsTable.TABLE_NAME) +
                        " WHERE " + AbonnementsTable.COLUMN_PSEUDO + "='" + pseudo + "' AND " +
                        DataTable.getNotDeletedCriteria(AbonnementsTable.TABLE_NAME));
        mLastLoader.init(getActivity(), Queries.MAIN_SHORTCUT_LAST_FOLLOWED, shortcutData);

        Bundle membersData = new Bundle();
        membersData.putParcelable(QueryLoader.DATA_KEY_URI, mListUri);
        mListLoader.init(getActivity(), Queries.MAIN_MEMBERS_LIST, membersData);

        setListAdapter(null);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Logs.add(Logs.Type.V, null);

        // Refresh query loader with existing filter (if any)
        refresh(mListener.onGetShortcut(Constants.MAIN_SECTION_MEMBERS, false).getFilter());

        // Register data service
        getActivity().sendBroadcast(DataService.getIntent(true, Tables.ID_ABONNEMENTS, mLastUri));
        getActivity().sendBroadcast(DataService.getIntent(true, Tables.ID_ABONNEMENTS, mListUri));
    }

    @Override
    public void onPause() {
        super.onPause();
        Logs.add(Logs.Type.V, null);

        // Unregister data service
        getActivity().sendBroadcast(DataService.getIntent(false, Tables.ID_ABONNEMENTS, mLastUri));
        getActivity().sendBroadcast(DataService.getIntent(false, Tables.ID_ABONNEMENTS, mListUri));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logs.add(Logs.Type.V, null);
        mListener = null;
    }
}
