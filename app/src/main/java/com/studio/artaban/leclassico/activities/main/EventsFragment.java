package com.studio.artaban.leclassico.activities.main;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.components.EventCalendar;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;

import java.util.Calendar;

/**
 * Created by pascal on 05/09/16.
 * Events fragment class (MainActivity)
 */
public class EventsFragment extends MainFragment implements QueryLoader.OnResultListener,
        EventCalendar.OnSelectListener, ViewPager.OnPageChangeListener, View.OnClickListener {

    private EventCalendar mCalendar; // Event calendar view
    private ViewPager mEventsPager; // Events list view

    //////
    public static class EventFragment extends Fragment { ///////////////////////////////////////////

        public static final String ARG_KEY_EVENT_ID = "eventID";
        public static final String ARG_KEY_FLYER = "flyer";
        public static final String ARG_KEY_TITLE = "title";
        public static final String ARG_KEY_DATE_START = "dateStart";
        public static final String ARG_KEY_DATE_END = "dateEnd";
        public static final String ARG_KEY_LOCATION = "location";
        public static final String ARG_KEY_MEMBERS = "members";
        public static final String ARG_KEY_REMARK = "remark";

        //////
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            //Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
            //        ";savedInstanceState: " + savedInstanceState);
            View rootView = inflater.inflate(R.layout.fragment_event, container, false);

            if (getArguments().getInt(ARG_KEY_EVENT_ID) == Constants.NO_DATA) { // No event on selected date
                // Flyer




                // Schedule





            } else {
                // Flyer




                // Schedule




            }
            // Set event info: Title, location, members count & remark
            ((TextView) rootView.findViewById(R.id.text_title)).setText(getArguments().getString(ARG_KEY_TITLE));
            ((TextView) rootView.findViewById(R.id.text_location)).setText(getArguments().getString(ARG_KEY_LOCATION));
            ((TextView) rootView.findViewById(R.id.text_info)).setText(getArguments().getString(ARG_KEY_REMARK));
            ((TextView) rootView.findViewById(R.id.text_members))
                    .setText(String.valueOf(getArguments().getInt(ARG_KEY_MEMBERS)));

            return rootView;
        }
    }
    private EventPagerAdapter mAdapter; // Events list adapter
    private class EventPagerAdapter extends FragmentPagerAdapter { /////////////////////////////////
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
            Bundle data = new Bundle();

            if (mEventLag == position) { // Check "no event" page
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
            EventFragment event = new EventFragment();
            event.setArguments(data);
            return event;
        }

        @Override
        public int getCount() {
            return (mCursor == null)? 0:mCursor.getCount() + ((!isEventExists())? 1:0);
        }
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
    private void selectCalendarDate() { // Select date period into calendar (according selected event)
        Logs.add(Logs.Type.V, null);

        if (mEventsPager.getCurrentItem() == mEventLag) {
            mCalendar.selectPeriod(mEventDate, null);
            resetShortcut();

        } else {
            if (!mCursor.move(mEventsPager.getCurrentItem()))
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
        shortcut.setInfo(new SpannableStringBuilder(getString(R.string.no_event_info)));
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
                        calendar.get(Calendar.DAY_OF_MONTH) + " 00:00:00";

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
        //Logs.add(Logs.Type.V, "date: " + date);

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
        mAdapter = new EventPagerAdapter(getActivity().getSupportFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);
        rootView.setTag(Constants.MAIN_SECTION_EVENTS);

        mCalendar = (EventCalendar) rootView.findViewById(R.id.event_calendar);
        mCalendar.setOnSelectListener(this);
        mEventsPager = (ViewPager) rootView.findViewById(R.id.pager_events);
        mEventsPager.setAdapter(mAdapter);
        mEventsPager.addOnPageChangeListener(this);

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
}
