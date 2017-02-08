package com.studio.artaban.leclassico.activities.event;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.LoggedActivity;
import com.studio.artaban.leclassico.activities.album.FullPhotoActivity;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;
import com.studio.artaban.leclassico.helpers.Storage;
import com.studio.artaban.leclassico.tools.Tools;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pascal on 05/02/17.
 * Event display activity
 */
public class EventDisplayActivity extends LoggedActivity {

    // Extra data keys (see 'LoggedActivity' & 'Login' extra data keys)

    public void onEntry(View sender) { // Click event to set user present flag
        Logs.add(Logs.Type.V, "sender: " + sender);







    }

    public static SpannableStringBuilder getHourly(Context context, String from, String to) {
    // Return formatted string with from & to date info (date & time)

        //Logs.add(Logs.Type.V, "context: " + context + ";from: " + from + ";to: " + to);
        SpannableStringBuilder hourly = new SpannableStringBuilder();
        DateFormat selected = new SimpleDateFormat(Constants.FORMAT_DATE_TIME);

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        try {
            Date startDate = selected.parse(from);
            hourly.append(context.getString(R.string.from, dateFormat.format(startDate)));
            hourly.append(' ');
            hourly.append(timeFormat.format(startDate));

        } catch (ParseException e) {
            Logs.add(Logs.Type.E, "Wrong start date & time format: " + from);
            hourly.append(context.getString(R.string.from, from));
        }
        hourly.append(' ');
        hourly.setSpan(new StyleSpan(Typeface.BOLD), 0,
                context.getResources().getInteger(R.integer.from_pos), 0);
        int endPos = hourly.length();
        try {
            Date endDate = selected.parse(to);
            hourly.append(context.getString(R.string.to, dateFormat.format(endDate)));
            hourly.append(' ');
            hourly.append(timeFormat.format(endDate));

        } catch (ParseException e) {
            Logs.add(Logs.Type.E, "Wrong end date & time format: " + to);
            hourly.append(context.getString(R.string.to, to));
        }
        hourly.setSpan(new StyleSpan(Typeface.BOLD), endPos,
                endPos + context.getResources().getInteger(R.integer.to_pos), 0);
        return hourly;
    }

    private RecyclerView mPresentsList; // Recycler view containing present members list

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

            // Set event info
            ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar))
                    .setTitle(cursor.getString(COLUMN_INDEX_NOM));
            if (!cursor.isNull(COLUMN_INDEX_FLYER)) {
                Bitmap bmpFlyer = BitmapFactory.decodeFile(Storage.get() + Storage.FOLDER_FLYERS +
                            File.separator + cursor.getString(COLUMN_INDEX_FLYER));
                if (bmpFlyer != null)
                    ((ImageView) findViewById(R.id.image_flyer)).setImageBitmap(bmpFlyer);
                else
                    Logs.add(Logs.Type.E, "Failed to open flyer: " + cursor.getString(COLUMN_INDEX_FLYER));
            }
            ((TextView) findViewById(R.id.text_location)).setText(cursor.getString(COLUMN_INDEX_LIEU));
            ((TextView) findViewById(R.id.text_hourly))
                    .setText(getHourly(this, cursor.getString(COLUMN_INDEX_DATE), cursor.getString(COLUMN_INDEX_DATE_END)));
            if (!cursor.isNull(COLUMN_INDEX_REMARK))
                ((TextView) findViewById(R.id.text_info)).setText(cursor.getString(COLUMN_INDEX_REMARK));








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
    private static final int COLUMN_INDEX_PSEUDO = 0;
    private static final int COLUMN_INDEX_NOM = 1;
    private static final int COLUMN_INDEX_DATE = 2;
    private static final int COLUMN_INDEX_DATE_END = 3;
    private static final int COLUMN_INDEX_LIEU = 4;
    private static final int COLUMN_INDEX_FLYER = 5;
    private static final int COLUMN_INDEX_REMARK = 6;
    private static final int COLUMN_INDEX_ENTRY_PSEUDO = 7;
    private static final int COLUMN_INDEX_ENTRY_SEX = 8;
    private static final int COLUMN_INDEX_ENTRY_PROFILE = 9;
    private static final int COLUMN_INDEX_ENTRY_PHONE = 10;
    private static final int COLUMN_INDEX_ENTRY_EMAIL = 11;
    private static final int COLUMN_INDEX_ENTRY_TOWN = 12;
    private static final int COLUMN_INDEX_ENTRY_NAME = 13;
    private static final int COLUMN_INDEX_ENTRY_ADDRESS = 14;
    private static final int COLUMN_INDEX_ENTRY_ADMIN = 15;

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
        ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar)).setTitle(getString(R.string.title_activity_event));

        // Get event ID
        mId = getIntent().getIntExtra(EXTRA_DATA_ID, Constants.NO_DATA);
        Logs.add(Logs.Type.I, "Event #" + mId);

        // Set URI to observe DB changes
        mEventUri = Uris.getUri(Uris.ID_MAIN_EVENTS);

        // Load event info
        Bundle eventData = new Bundle();
        eventData.putParcelable(QueryLoader.DATA_KEY_URI, mEventUri);
        eventData.putLong(QueryLoader.DATA_KEY_URI_FILTER, mId);
        mEventLoader.init(this, Queries.EVENTS_DISPLAY, eventData);

        // Set members list height
        mPresentsList = (RecyclerView) findViewById(R.id.list_members);
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPresentsList.getLayoutParams();
            params.height = screenSize.y - Tools.getStatusBarHeight(getResources()) - Tools.getActionBarHeight(this) -
                    (getResources().getDimensionPixelSize(R.dimen.event_info_height) << 1) -
                    (getResources().getDimensionPixelSize(R.dimen.event_info_margin) * 3);
            mPresentsList.setLayoutParams(params);

        } else {





        }
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
        if ((mCursor != null) && (mCursor.isNull(COLUMN_INDEX_FLYER)))
            menu.findItem(R.id.mnu_notification).setVisible(false);
            // NB: No flyer to display in full screen mode

        return super.onPrepareOptionsMenu(menu);
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
                if (mCursor == null)
                    break;

                ////// Start full screen photo activity
                Intent fullScreen = new Intent(this, FullPhotoActivity.class);
                fullScreen.putExtra(FullPhotoActivity.EXTRA_DATA_TITLE, mCursor.getString(COLUMN_INDEX_NOM));
                fullScreen.putExtra(FullPhotoActivity.EXTRA_DATA_NAME, mCursor.getString(COLUMN_INDEX_FLYER));
                fullScreen.putExtra(FullPhotoActivity.EXTRA_DATA_FLYER, true);
                Login.copyExtraData(getIntent(), fullScreen);

                startActivity(fullScreen);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
