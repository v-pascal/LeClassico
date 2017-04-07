package com.studio.artaban.leclassico.activities.profile;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.components.RecyclerAdapter;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;
import com.studio.artaban.leclassico.tools.Tools;

/**
 * Created by pascal on 06/04/17.
 * Activity to pick a member
 */
public class MemberPickActivity extends AppCompatActivity implements QueryLoader.OnResultListener {

    private RecyclerView mList; // Member list view
    private final MemberRecyclerAdapter mAdapter = new MemberRecyclerAdapter(); // Member list adapter

    //////
    private class MemberRecyclerAdapter extends RecyclerAdapter { //////////////////////////////////
        public MemberRecyclerAdapter() {
            super(R.layout.layout_member_item, Constants.NO_DATA, true, COLUMN_INDEX_ID);
        }

        ////// View.OnClickListener ////////////////////////////////////////////////////////////////
        @Override
        public void onClick(View sender) {
            Logs.add(Logs.Type.V, "sender: " + sender);
            switch (sender.getId()) {

                case R.id.layout_pseudo:
                case R.id.image_pseudo: {
                    int pseudoId = (int)sender.getTag(R.id.tag_pseudo_id);
                    Logs.add(Logs.Type.I, "Pseudo #" + pseudoId + " selected");





                    break;
                }
            }
        }

        ////// RecyclerAdapter /////////////////////////////////////////////////////////////////////
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Logs.add(Logs.Type.V, "holder: " + holder + ";position: " + position);

            // Set pseudo icon
            boolean female = (!mDataSource.isNull(position, COLUMN_INDEX_SEX)) &&
                    (mDataSource.getInt(position, COLUMN_INDEX_SEX) == CamaradesTable.GENDER_FEMALE);
            String profile = (!mDataSource.isNull(position, COLUMN_INDEX_PROFILE)) ?
                    mDataSource.getString(position, COLUMN_INDEX_PROFILE) : null;
            Tools.setProfile(MemberPickActivity.this, (ImageView) holder.rootView.findViewById(R.id.image_pseudo),
                    female, profile, R.dimen.user_item_height, true);






        }
    }

    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        if ((id != Queries.MEMBER_PICK_LIST) || (!cursor.moveToFirst()))
            return;

        // Fill members list
        mAdapter.getDataSource().fill(cursor, cursor.getCount(), null);
        mList.scrollToPosition(0);
    }
    @Override
    public void onLoaderReset() {

    }

    //////
    private Uri mUri; // Member list URI (to observe DB change)
    private final QueryLoader mLoader = new QueryLoader(this, this); // Member list query loader

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

    ////// AppCompatActivity ///////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
        setContentView(R.layout.activity_pick_member);

        // Set member list view
        LinearLayoutManager linearManager = new LinearLayoutManager(this);
        linearManager.setOrientation(LinearLayoutManager.VERTICAL);

        mList = (RecyclerView) findViewById(R.id.list_members);
        mList.setLayoutManager(linearManager);
        mList.setItemAnimator(new DefaultItemAnimator());
        mList.setAdapter(mAdapter);

        // Set URI to observe DB change
        if (getIntent().getData().getPathSegments().size() > 1) // Check followers filter
            mUri = Uris.getUri(Uris.ID_PICK_MEMBER, getIntent().getData().getPathSegments().get(1));
        else // All members
            mUri = Uris.getUri(Uris.ID_PICK_MEMBER);

        // Load member list query
        Bundle membersData = new Bundle();
        membersData.putParcelable(QueryLoader.DATA_KEY_URI, mUri);
        mLoader.init(this, Queries.MEMBER_PICK_LIST, membersData);
    }
}
