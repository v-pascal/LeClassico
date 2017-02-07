package com.studio.artaban.leclassico.activities.event;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.LoggedActivity;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;

/**
 * Created by pascal on 05/02/17.
 * Event display activity
 */
public class EventDisplayActivity extends LoggedActivity {

    // Extra data keys (see 'LoggedActivity' & 'Login' extra data keys)

    public void onEntry(View sender) { // Click event to set user present flag
        Logs.add(Logs.Type.V, "sender: " + sender);







    }

    ////// LoggedActivity //////////////////////////////////////////////////////////////////////////
    @Override
    protected void onLoggedResume() {

    }

    //////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        if ((!cursor.moveToFirst()) || (onNotifyLoadFinished(id, cursor)))
            return;

        if (id == Queries.EVENTS_DISPLAY) {
            mCursor = cursor;






        }
    }

    @Override
    public void onLoaderReset() {
        Logs.add(Logs.Type.V, null);





    }

    //////
    private final QueryLoader mEventLoader = new QueryLoader(this, this); // Event query loader
    private Uri mEventUri; // Event query URI

    // Query column indexes
    private static final int COLUMN_INDEX_ID = 0;
    private static final int COLUMN_INDEX_EVENT_ID = 1;
    private static final int COLUMN_INDEX_PSEUDO = 2;
    private static final int COLUMN_INDEX_NOM = 3;
    private static final int COLUMN_INDEX_DATE = 4;
    private static final int COLUMN_INDEX_DATE_END = 5;
    private static final int COLUMN_INDEX_LIEU = 6;
    private static final int COLUMN_INDEX_FLYER = 7;
    private static final int COLUMN_INDEX_REMARK = 8;
    private static final int COLUMN_INDEX_ENTRY_PSEUDO = 9;
    private static final int COLUMN_INDEX_ENTRY_SEX = 10;
    private static final int COLUMN_INDEX_ENTRY_PROFILE = 11;
    private static final int COLUMN_INDEX_ENTRY_PHONE = 12;
    private static final int COLUMN_INDEX_ENTRY_EMAIL = 13;
    private static final int COLUMN_INDEX_ENTRY_TOWN = 14;
    private static final int COLUMN_INDEX_ENTRY_NAME = 15;
    private static final int COLUMN_INDEX_ENTRY_ADDRESS = 16;
    private static final int COLUMN_INDEX_ENTRY_ADMIN = 17;

    ////// AppCompatActivity ///////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
        setContentView(R.layout.activity_event_display);

        // Set toolbar & default title
        Toolbar toolbar =   (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.title_activity_event));

        // Get event ID
        mId = getIntent().getIntExtra(EXTRA_DATA_ID, Constants.NO_DATA);
        Logs.add(Logs.Type.I, "Event #" + mId);

        // Set URI to observe DB changes
        mEventUri = Uris.getUri(Uris.ID_EVENTS_DISPLAY);

        // Load event info
        Bundle eventData = new Bundle();
        eventData.putParcelable(QueryLoader.DATA_KEY_URI, mEventUri);
        eventData.putLong(QueryLoader.DATA_KEY_URI_FILTER, mId);
        mEventLoader.init(this, Queries.EVENTS_DISPLAY, eventData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Logs.add(Logs.Type.V, "menu: " + menu);
        getMenuInflater().inflate(R.menu.menu_event_display, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Logs.add(Logs.Type.V, "menu: " + menu);
        if (mCursor != null) {






        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logs.add(Logs.Type.V, "item: " + item);
        if (onNotifyItemSelected(item))
            return true; // Display notifications

        switch (item.getItemId()) {
            case android.R.id.home: { // Back to previous activity

                supportFinishAfterTransition();
                return true;
            }
            case R.id.mnu_full_screen: { // Display flyer in full screen






                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
