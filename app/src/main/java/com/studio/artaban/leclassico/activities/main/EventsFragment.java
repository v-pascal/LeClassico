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

        public static final String ARG_KEY_POSITION = "position";
        public static final String ARG_KEY_CURSOR_POSITION = "cursorPosition";

        //////
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_event, container, false);







            ((TextView) rootView.findViewById(R.id.test1))
                    .setText(String.valueOf(getArguments().getInt(ARG_KEY_POSITION)));
            ((TextView) rootView.findViewById(R.id.test2))
                    .setText(String.valueOf(getArguments().getInt(ARG_KEY_CURSOR_POSITION)));









            return rootView;
        }
    }
    private EventPagerAdapter mAdapter; // Events list adapter
    private class EventPagerAdapter extends FragmentPagerAdapter { /////////////////////////////////
        public EventPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private boolean isEventExists() { // Return if one event exists at selected date
            Logs.add(Logs.Type.V, null);
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
        private int getEventPosition() { // Return cursor position lag according if event does not exist...
            Logs.add(Logs.Type.V, null);
            int position = 0; // ...and return NO_DATA if any event exists at selected date

            // NB: Needed coz cursor entries are not the only items displayed in the list. Item that
            //     marks no event info at selected date must be defined as well (if the case)
            do {
                int compare = mEventDate.substring(0, 10)
                        .compareTo(mCursor.getString(COLUMN_INDEX_DATE).substring(0, 10));
                if (compare == 0) {
                    position = Constants.NO_DATA;
                    break;
                }
                if (compare < 0)
                    break;

                ++position;

            } while (mCursor.moveToNext());
            mCursor.moveToFirst();

            return position;
        }

        //////
        @Override
        public Fragment getItem(int position) {
            Logs.add(Logs.Type.V, "section: " + position);

            EventFragment event = new EventFragment();
            Bundle data = new Bundle();
            data.putInt(EventFragment.ARG_KEY_POSITION, position);
            data.putInt(EventFragment.ARG_KEY_CURSOR_POSITION, getEventPosition());
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
                else { // Inform user no previous event



                }
                break;
            }
            case R.id.image_next_event: {
                Logs.add(Logs.Type.I, "Next event selected");
                if (mAdapter.getCount() > (mEventsPager.getCurrentItem() + 1))
                    mEventsPager.setCurrentItem(mEventsPager.getCurrentItem() + 1, false);
                else { // Inform user no next event



                }
                break;
            }
        }
        // Select date into calendar




        mCalendar.selectPeriod(null, null);





    }

    ////// OnPageChangeListener ////////////////////////////////////////////////////////////////////
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Logs.add(Logs.Type.V, "position: " + position);







    }

    @Override
    public void onPageScrollStateChanged(int state) {

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

            int eventPosition = 0; // Event position of the selected date (in the list)
            if (mEventDate == null) { // Select today (default)

                Calendar calendar = Calendar.getInstance();
                mEventDate = String.valueOf(calendar.get(Calendar.YEAR)) + '-' +
                        String.format("%02d", calendar.get(Calendar.MONTH) + 1) + '-' +
                        calendar.get(Calendar.DAY_OF_MONTH) + " 00:00:00";

            }
            // Check existing event at selected date
            String endDate = null;
            do {
                int compare = mEventDate.substring(0, 10)
                        .compareTo(cursor.getString(COLUMN_INDEX_DATE).substring(0, 10));
                if (compare == 0) {
                    endDate = cursor.getString(COLUMN_INDEX_DATE_END);

                    // Set shortcut info







                    break; // Stop at first event found
                }
                if (compare < 0)
                    break;

                ++eventPosition;

            } while (cursor.moveToNext());
            cursor.moveToFirst();

            // Select date (calendar & shortcut)
            mCalendar.selectPeriod(mEventDate, endDate);
            if (endDate == null) { // Check set shortcut info (no event)






            }
            mAdapter.notifyDataSetChanged();
            mEventsPager.setCurrentItem(eventPosition, false);
        }
    }

    @Override
    public void onLoaderReset() {
        Logs.add(Logs.Type.V, null);
        mCursor = null;
    }

    //////
    private Uri mListUri; // Events list query URI
    private QueryLoader mEventsLoader; // Events query loader
    private Cursor mCursor; // Events list query cursor

    private String mEventDate; // Selected event date (today as default)

    // Query column indexes
    private static final int COLUMN_INDEX_ID = 0;
    private static final int COLUMN_INDEX_NOM = 1;
    private static final int COLUMN_INDEX_DATE = 2;
    private static final int COLUMN_INDEX_DATE_END = 3;
    private static final int COLUMN_INDEX_LIEU = 4;
    private static final int COLUMN_INDEX_REMARK = 5;
    private static final int COLUMN_INDEX_PRESENT_MEMBERS = 6;

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
