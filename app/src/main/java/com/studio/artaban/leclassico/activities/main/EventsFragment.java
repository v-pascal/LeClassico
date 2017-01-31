package com.studio.artaban.leclassico.activities.main;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.album.FullPhotoActivity;
import com.studio.artaban.leclassico.components.EventCalendar;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.helpers.Glider;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;
import com.studio.artaban.leclassico.helpers.Storage;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by pascal on 05/09/16.
 * Events fragment class (MainActivity)
 */
public class EventsFragment extends MainFragment implements QueryLoader.OnResultListener,
        EventCalendar.OnSelectListener, ViewPager.OnPageChangeListener, View.OnClickListener,
        Internet.OnConnectivityListener {

    private EventCalendar mCalendar; // Event calendar view
    private ViewPager mEventsPager; // Events list view

    //////
    public static class EventFragment extends Fragment implements View.OnClickListener { ///////////

        public static final String ARG_KEY_POSITION = "position";
        public static final String ARG_KEY_EVENT_ID = "eventID";
        public static final String ARG_KEY_FLYER = "flyer";
        public static final String ARG_KEY_TITLE = "title";
        public static final String ARG_KEY_DATE_START = "dateStart";
        public static final String ARG_KEY_DATE_END = "dateEnd";
        public static final String ARG_KEY_LOCATION = "location";
        public static final String ARG_KEY_MEMBERS = "members";
        public static final String ARG_KEY_REMARK = "remark";

        public static void createView(Context context, Resources resources,
                                      View rootView, final Bundle data) { // Fill event info into UI
            //Logs.add(Logs.Type.V, "context: " + context + ";resources: " + resources +
            //        ";rootView: " + rootView + ";data: " + data);

            ImageView flyer = (ImageView) rootView.findViewById(R.id.image_flyer);
            flyer.setTag(R.id.tag_flyer, data.getString(ARG_KEY_FLYER));
            flyer.setTag(R.id.tag_title, data.getString(ARG_KEY_TITLE));

            DateFormat selected = new SimpleDateFormat(Constants.FORMAT_DATE_TIME);
            if (data.getInt(ARG_KEY_EVENT_ID) == Constants.NO_DATA) { // No event on selected date

                rootView.findViewById(R.id.layout_data).setVisibility(View.GONE);
                rootView.findViewById(R.id.layout_flyer).setBackground(null);

                // Display "no event" image (as flyer)
                int padding = (int)resources.getDimension(R.dimen.event_no_padding);
                flyer.setScaleType(ImageView.ScaleType.FIT_CENTER);
                flyer.setImageDrawable(resources.getDrawable(R.drawable.no_event));
                flyer.setPadding(padding, padding, padding, padding);
                flyer.setClickable(false);

                // Display selected date with "no event" message
                StringBuilder info = new StringBuilder();
                DateFormat display = android.text.format.DateFormat.getLongDateFormat(context);
                try {
                    Date selectedDate = selected.parse(data.getString(ARG_KEY_DATE_START));
                    info.append(display.format(selectedDate));
                    info.append('\n');

                } catch (ParseException e) {
                    Logs.add(Logs.Type.E, "Wrong selected date & time format: " +
                            data.getString(ARG_KEY_DATE_START));
                }
                info.append(context.getString(R.string.no_event));

                //
                TextView noEvent = (TextView) rootView.findViewById(R.id.text_no_event);
                noEvent.setVisibility(View.VISIBLE);
                noEvent.setText(info.toString());

            } else {
                rootView.findViewById(R.id.layout_data).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.text_no_event).setVisibility(View.GONE);
                rootView.findViewById(R.id.layout_flyer)
                        .setBackground(resources.getDrawable(R.color.light_gray));
                flyer.setScaleType(ImageView.ScaleType.CENTER_CROP);
                flyer.setImageDrawable(resources.getDrawable(R.drawable.no_flyer));
                flyer.setPadding(0, 0, 0, 0);
                flyer.setClickable(true);

                // Flyer
                final String flyerName = data.getString(ARG_KEY_FLYER);
                if (flyerName != null)
                    Glider.with(context).placeholder(R.drawable.no_flyer)
                            .load(Storage.FOLDER_FLYERS + File.separator + flyerName,
                                    Constants.APP_URL_FLYERS + '/' + flyerName)
                            .into(flyer, new Glider.OnLoadListener() {
                                        @Override
                                        public void onLoadFailed(ImageView imageView) {

                                        }

                                        @Override
                                        public boolean onSetResource(Bitmap resource, ImageView imageView) {
                                            imageView.setTransitionName(flyerName);
                                            return false;
                                        }
                                    });

                // Schedule (e.i from start date to end date)
                SpannableStringBuilder hourly = new SpannableStringBuilder();
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
                DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
                try {
                    Date startDate = selected.parse(data.getString(ARG_KEY_DATE_START));
                    hourly.append(context.getString(R.string.from, dateFormat.format(startDate)));
                    hourly.append(' ');
                    hourly.append(timeFormat.format(startDate));

                } catch (ParseException e) {
                    Logs.add(Logs.Type.E, "Wrong start date & time format: " + data.getString(ARG_KEY_DATE_START));
                    hourly.append(context.getString(R.string.from, data.getString(ARG_KEY_DATE_START)));
                }
                hourly.append(' ');
                hourly.setSpan(new StyleSpan(Typeface.BOLD), 0, resources.getInteger(R.integer.from_pos), 0);
                int endPos = hourly.length();
                try {
                    Date endDate = selected.parse(data.getString(ARG_KEY_DATE_END));
                    hourly.append(context.getString(R.string.to, dateFormat.format(endDate)));
                    hourly.append(' ');
                    hourly.append(timeFormat.format(endDate));

                } catch (ParseException e) {
                    Logs.add(Logs.Type.E, "Wrong end date & time format: " + data.getString(ARG_KEY_DATE_END));
                    hourly.append(context.getString(R.string.to, data.getString(ARG_KEY_DATE_END)));
                }
                hourly.setSpan(new StyleSpan(Typeface.BOLD), endPos, endPos + resources.getInteger(R.integer.to_pos), 0);
                ((TextView) rootView.findViewById(R.id.text_hourly)).setText(hourly, TextView.BufferType.SPANNABLE);

                // Set info: Title, location, members count & remark
                ((TextView) rootView.findViewById(R.id.text_title)).setText(data.getString(ARG_KEY_TITLE));
                ((TextView) rootView.findViewById(R.id.text_location)).setText(data.getString(ARG_KEY_LOCATION));
                ((TextView) rootView.findViewById(R.id.text_info)).setText(data.getString(ARG_KEY_REMARK));
                ((TextView) rootView.findViewById(R.id.text_members))
                        .setText(String.valueOf(data.getInt(ARG_KEY_MEMBERS)));
            }
        }

        ////// OnClickListener
        @Override
        public void onClick(View sender) {
            //Logs.add(Logs.Type.V, "sender: " + sender);
            switch (sender.getId()) {

                case R.id.image_flyer: {
                    Logs.add(Logs.Type.I, "Display flyer in fullscreen");

                    String name = (String) sender.getTag(R.id.tag_flyer);
                    String title = (String) sender.getTag(R.id.tag_title);
                    if ((name == null) || (!new File(Storage.get() + Storage.FOLDER_FLYERS +
                            File.separator + name).exists()))
                        return; // No flyer | Flyer image not loaded

                    ////// Start full screen photo activity
                    Intent fullScreen = new Intent(getContext(), FullPhotoActivity.class);
                    fullScreen.putExtra(FullPhotoActivity.EXTRA_DATA_TITLE, title);
                    fullScreen.putExtra(FullPhotoActivity.EXTRA_DATA_NAME, name);
                    fullScreen.putExtra(FullPhotoActivity.EXTRA_DATA_FLYER, true);
                    Login.copyExtraData(getActivity().getIntent(), fullScreen);

                    ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(getActivity(), sender, name);
                    startActivity(fullScreen, options.toBundle());
                    break;
                }
                case R.id.image_display: {
                    Logs.add(Logs.Type.I, "Display event");



                    //getArguments().getInt(ARG_KEY_EVENT_ID)




                    break;
                }
            }
        }

        ////// Fragment
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            //Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
            //        ";savedInstanceState: " + savedInstanceState);

            ////// Create event view
            Bundle data = getArguments();
            View rootView = inflater.inflate(R.layout.fragment_event, container, false);
            createView(getContext(), getResources(), rootView, data);

            ////// Events
            rootView.findViewById(R.id.image_flyer).setOnClickListener(this);
            rootView.findViewById(R.id.image_display).setOnClickListener(this);

            rootView.setTag(getArguments().getInt(ARG_KEY_POSITION)); // Add tag to be able to find it
            return rootView;
        }
    }
    private EventPagerAdapter mAdapter; // Events list adapter
    private class EventPagerAdapter extends FragmentStatePagerAdapter { ////////////////////////////
        public EventPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private boolean isEventExists() { // Return if one event exists at selected date
            //Logs.add(Logs.Type.V, null);
            boolean found = false;
            do {
                if (mEventDate.substring(0, 10)
                        .compareTo(mCursor.getString(COLUMN_INDEX_DATE).substring(0, 10)) == 0) {
                    found = true;
                    break;
                }

            } while (mCursor.moveToNext());
            mCursor.moveToFirst();

            return found;
        }

        //////
        @Override
        public Fragment getItem(int position) {
            Logs.add(Logs.Type.V, "section: " + position);

            EventFragment event = new EventFragment();
            event.setArguments(getEventData(position));
            return event;
        }

        @Override
        public int getCount() {
            return (mCursor == null)? 0:mCursor.getCount() + ((!isEventExists())? 1:0);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
            // NB: Needed to refresh all page at data change notification (e.g Internet connection)
        }
    }

    //////
    private Bundle getEventData(int position) { // Return event info data
        Logs.add(Logs.Type.V, "position: " + position);
        Bundle data = new Bundle();

        data.putInt(EventFragment.ARG_KEY_POSITION, position);
        if (mEventLag == position) {
            // "no event" page found
            data.putInt(EventFragment.ARG_KEY_EVENT_ID, Constants.NO_DATA);
            data.putString(EventFragment.ARG_KEY_DATE_START, mEventDate);

        } else {
            // Position cursor offset to event position (according date selection)
            if ((mEventLag != Constants.NO_DATA) && (mEventLag < position))
                --position; // -1 to skip "no event" page
            mCursor.move(position);

            data.putInt(EventFragment.ARG_KEY_EVENT_ID, mCursor.getInt(COLUMN_INDEX_ID));
            data.putString(EventFragment.ARG_KEY_FLYER, mCursor.getString(COLUMN_INDEX_FLYER));
            data.putString(EventFragment.ARG_KEY_TITLE, mCursor.getString(COLUMN_INDEX_NOM));
            data.putString(EventFragment.ARG_KEY_DATE_START, mCursor.getString(COLUMN_INDEX_DATE));
            data.putString(EventFragment.ARG_KEY_DATE_END, mCursor.getString(COLUMN_INDEX_DATE_END));
            data.putString(EventFragment.ARG_KEY_LOCATION, mCursor.getString(COLUMN_INDEX_LIEU));
            data.putInt(EventFragment.ARG_KEY_MEMBERS, mCursor.getInt(COLUMN_INDEX_PRESENT_MEMBERS));
            data.putString(EventFragment.ARG_KEY_REMARK, mCursor.getString(COLUMN_INDEX_REMARK));
            mCursor.moveToFirst();
        }
        return data;
    }

    ////// OnConnectivityListener //////////////////////////////////////////////////////////////////
    @Override
    public void onConnection() {
        Logs.add(Logs.Type.V, null);
        mAdapter.notifyDataSetChanged();
        // NB: Needed to refresh events with flyer not loaded yet
    }

    @Override
    public void onDisconnection() {

    }

    ////// OnClickListener /////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View sender) {
        Logs.add(Logs.Type.V, "sender: " + sender);
        if (mAdapter.getCount() == 0)
            return; // No event

        switch (sender.getId()) {

            case R.id.image_prev_event: {
                Logs.add(Logs.Type.I, "Previous event selected");
                if (mEventsPager.getCurrentItem() != 0)
                    mEventsPager.setCurrentItem(mEventsPager.getCurrentItem() - 1, false);
                else {
                    // Inform user no previous event
                    Toast.makeText(getContext(), R.string.no_previous_event, Toast.LENGTH_SHORT).show();

                    // TODO: Get previous event from remote DB
                    return;
                }
                break;
            }
            case R.id.image_next_event: {
                Logs.add(Logs.Type.I, "Next event selected");
                if (mAdapter.getCount() > (mEventsPager.getCurrentItem() + 1))
                    mEventsPager.setCurrentItem(mEventsPager.getCurrentItem() + 1, false);
                else {
                    // Inform user no next event
                    Toast.makeText(getContext(), R.string.no_next_event, Toast.LENGTH_SHORT).show();

                    // TODO: Get next event from remote DB
                    return;
                }
                break;
            }
        }
        // Select date into calendar
        selectCalendarDate();
    }

    ////// OnPageChangeListener ////////////////////////////////////////////////////////////////////
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //Logs.add(Logs.Type.V, "position: " + position);

        mCurrentItem = position;
        if ((!refreshEventPage(position + 1)) && (position > 0))
            refreshEventPage(position - 1);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //Logs.add(Logs.Type.V, "state: " + state);
        switch (state) {
            case ViewPager.SCROLL_STATE_DRAGGING: {
                mDraggingItem = mEventsPager.getCurrentItem();
                break;
            }
            case ViewPager.SCROLL_STATE_IDLE: {
                if (mDraggingItem != mEventsPager.getCurrentItem())
                    selectCalendarDate();
                break;
            }
        }
    }

    //
    private int mDraggingItem; // Item index when user starts changing event by dragging pager view
    private int mCurrentItem; // Current event index selected

    private int mRefreshItem = Constants.NO_DATA; // Event page index to refresh
    // NB: Member needed to fix issue that store fragment state of the last selected page
    //     with oldest content (need to refresh page below)

    private boolean refreshEventPage(int nextSelection) { // Refresh next page B4 selection (see above)
        //Logs.add(Logs.Type.V, "nextSelection: " + nextSelection);

        if ((mCursor != null) && (mRefreshItem == nextSelection)) {
            mRefreshItem = Constants.NO_DATA; // Stop refreshing pages

            Logs.add(Logs.Type.I, "Refresh event page: " + nextSelection);
            ViewGroup page = (ViewGroup) mEventsPager.findViewWithTag(nextSelection);
            EventFragment.createView(getContext(), getResources(), page, getEventData(nextSelection));
            return true;
        }
        return false;
    }

    private void selectCalendarDate() { // Select date period into calendar (according selected event)
        Logs.add(Logs.Type.V, null);

        if (mEventsPager.getCurrentItem() == mEventLag) {
            mCalendar.selectPeriod(mEventDate, null);
            resetShortcut();

        } else {
            // Position cursor offset to event position (according date selection)
            int cursorPos = mEventsPager.getCurrentItem();
            if ((mEventLag != Constants.NO_DATA) && (mEventLag < cursorPos))
                --cursorPos; // -1 to skip "no event" page

            if (!mCursor.move(cursorPos))
                throw new RuntimeException("Unexpected event selected");

            mCalendar.selectPeriod(mCursor.getString(COLUMN_INDEX_DATE), mCursor.getString(COLUMN_INDEX_DATE_END));

            // Update bottom shortcut info
            updateShortcut((ShortcutFragment) getChildFragmentManager().findFragmentById(R.id.shortcut_events_bottom));
            mCursor.moveToFirst();
        }
    }

    private void updateShortcut(ShortcutFragment shortcut) { // Update shortcut info with cursor info
        Logs.add(Logs.Type.V, "shortcut: " + shortcut);

        shortcut.setDate(true, mCursor.getString(COLUMN_INDEX_DATE));
        shortcut.setDate(false, mCursor.getString(COLUMN_INDEX_DATE_END));
        shortcut.setMessage(new SpannableStringBuilder(mCursor.getString(COLUMN_INDEX_NOM)));
        shortcut.setInfo(new SpannableStringBuilder(mCursor.getString(COLUMN_INDEX_LIEU)));
    }
    private void resetShortcut() { // Display no event info into bottom shortcut
        Logs.add(Logs.Type.V, null);

        ShortcutFragment shortcut = (ShortcutFragment) getChildFragmentManager()
                .findFragmentById(R.id.shortcut_events_bottom);
        shortcut.setDate(true, null);
        shortcut.setDate(false, null);
        shortcut.setMessage(new SpannableStringBuilder(getString(R.string.no_event)));
        shortcut.setInfo(new SpannableStringBuilder(getString(R.string.no_event_selection)));
    }

    ////// OnSelectListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onDateSelected(String date) {
        Logs.add(Logs.Type.V, "date: " + date);
        mEventDate = date;
        refresh();
    }

    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        if (!cursor.moveToFirst())
            return;

        if (id == Queries.MAIN_EVENTS_LIST) {
            mCursor = cursor;

            Calendar calendar = Calendar.getInstance();
            String today = String.valueOf(calendar.get(Calendar.YEAR)) + '-' +
                        String.format("%02d", calendar.get(Calendar.MONTH) + 1) + '-' +
                        String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)) + Constants.NO_TIME;

            if (mEventDate == null) // Select today (default)
                mEventDate = today;

            // Check existing event at selected date
            String endDate = null;
            mEventLag = 0;

            int eventPosition = 0; // Event position in the list of the selected date (even if not exists)
            do {
                int compare = mEventDate.substring(0, 10)
                        .compareTo(cursor.getString(COLUMN_INDEX_DATE).substring(0, 10));
                if (compare == 0) {
                    mEventLag = Constants.NO_DATA; // Event found (no events cursor lag)
                    endDate = cursor.getString(COLUMN_INDEX_DATE_END); // NB: Always != NULL

                    // Set shortcuts info (bottom shortcut as well)
                    if (mEventDate.compareTo(today) == 0) { // Set top shortcut info (today)
                        try {
                            updateShortcut(mListener.onGetShortcut(Constants.MAIN_SECTION_EVENTS, false));
                        } catch (NullPointerException e) {
                            Logs.add(Logs.Type.F, "Activity not attached");
                        }
                    }
                    updateShortcut((ShortcutFragment) getChildFragmentManager()
                            .findFragmentById(R.id.shortcut_events_bottom));
                    break; // Stop at first event found
                }
                if (compare < 0)
                    break;

                ++mEventLag;
                ++eventPosition;

            } while (cursor.moveToNext());
            cursor.moveToFirst();

            // Select date (calendar & shortcuts)
            mCalendar.selectPeriod(mEventDate, endDate);

            if (endDate == null) { // Check set shortcuts info with "no event" (bottom shortcut as well)
                if (mEventDate.compareTo(today) == 0) { // Set top shortcut info (today)
                    try {
                        ShortcutFragment shortcut = mListener.onGetShortcut(Constants.MAIN_SECTION_EVENTS, false);
                        shortcut.setDate(true, null);
                        shortcut.setDate(false, null);
                        shortcut.setMessage(new SpannableStringBuilder(getString(R.string.no_event)));
                        shortcut.setInfo(new SpannableStringBuilder(getString(R.string.no_event_info)));

                    } catch (NullPointerException e) {
                        Logs.add(Logs.Type.F, "Activity not attached");
                    }
                }
                resetShortcut();
            }
            mAdapter.notifyDataSetChanged();
            refreshEventPage(eventPosition); // Do it now (B4 selection)
            mEventsPager.setCurrentItem(eventPosition, false);
        }
    }

    @Override
    public void onLoaderReset() {
        Logs.add(Logs.Type.V, null);
        mCursor = null;
        mAdapter.notifyDataSetChanged();
    }

    //////
    private Uri mListUri; // Events list query URI
    private QueryLoader mEventsLoader; // Events query loader
    private Cursor mCursor; // Events list query cursor

    private String mEventDate; // Selected event date (today as default)
    private int mEventLag; // Index from which a cursor lag is needed when no selected date event is found
    // NB: Member above needed coz if no event is found at the selected date an event item with
    //     the "no event" info is added (adding an index lag between events list & events cursor)

    // Query column indexes
    private static final int COLUMN_INDEX_ID = 0;
    private static final int COLUMN_INDEX_FLYER = 1;
    private static final int COLUMN_INDEX_NOM = 2;
    private static final int COLUMN_INDEX_DATE = 3;
    private static final int COLUMN_INDEX_DATE_END = 4;
    private static final int COLUMN_INDEX_LIEU = 5;
    private static final int COLUMN_INDEX_REMARK = 6;
    private static final int COLUMN_INDEX_PRESENT_MEMBERS = 7;

    private void refresh() { // Refresh query loader
        Logs.add(Logs.Type.V, null);

        Bundle eventsData = new Bundle();
        eventsData.putParcelable(QueryLoader.DATA_KEY_URI, mListUri);
        eventsData.putString(QueryLoader.DATA_KEY_URI_FILTER, mEventDate);
        mEventsLoader.restart(getActivity(), Queries.MAIN_EVENTS_LIST, eventsData);
    }

    ////// MainFragment ////////////////////////////////////////////////////////////////////////////
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logs.add(Logs.Type.V, "context: " + context);

        mEventsLoader = new QueryLoader(context, this);
        mAdapter = new EventPagerAdapter(getChildFragmentManager());
        Internet.addConnectivityListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);
        rootView.setTag(Constants.MAIN_SECTION_EVENTS);

        mEventDate = null;
        mCursor = null;
        mCalendar = (EventCalendar) rootView.findViewById(R.id.event_calendar);
        mCalendar.setOnSelectListener(this);
        mEventsPager = (ViewPager) rootView.findViewById(R.id.pager_events);
        mEventsPager.addOnPageChangeListener(this);
        mEventsPager.setAdapter(mAdapter);

        // Set shortcut data (default)
        SpannableStringBuilder data = new SpannableStringBuilder(getString(R.string.no_event));
        mListener.onGetShortcut(Constants.MAIN_SECTION_EVENTS, false).setMessage(data);
        data = new SpannableStringBuilder(getString(R.string.no_event_info));
        mListener.onGetShortcut(Constants.MAIN_SECTION_EVENTS, false).setInfo(data);

        // Add event selection listener
        rootView.findViewById(R.id.image_prev_event).setOnClickListener(this);
        rootView.findViewById(R.id.image_next_event).setOnClickListener(this);

        // Set URI to observe DB changes
        mListUri = Uris.getUri(Uris.ID_MAIN_EVENTS);

        // Load events info
        Bundle eventsData = new Bundle();
        eventsData.putParcelable(QueryLoader.DATA_KEY_URI, mListUri);
        mEventsLoader.init(getActivity(), Queries.MAIN_EVENTS_LIST, eventsData);

        // Set bottom shortcut
        if (getChildFragmentManager().findFragmentById(R.id.shortcut_events_bottom) == null) {
            getChildFragmentManager().beginTransaction().add(R.id.shortcut_events_bottom, new ShortcutFragment()).commit();
            getChildFragmentManager().executePendingTransactions();
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logs.add(Logs.Type.V, null);

        mRefreshItem = mCurrentItem;
        // NB: See assigned member declaration comments
    }
}
