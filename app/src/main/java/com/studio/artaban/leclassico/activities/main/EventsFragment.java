package com.studio.artaban.leclassico.activities.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 05/09/16.
 * Events fragment class (MainActivity)
 */
public class EventsFragment extends MainFragment {





    private ViewPager mEventPager; // Events list view
    public static class EventFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_event, container, false);
            ((TextView) rootView.findViewById(R.id.test)).setText(String.valueOf(getArguments().getInt("test")));
            return rootView;
        }
    }




    ////// MainFragment ////////////////////////////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);
        rootView.setTag(Constants.MAIN_SECTION_EVENTS);

        // Set shortcut data (default)
        SpannableStringBuilder data = new SpannableStringBuilder(getString(R.string.no_event));
        mListener.onGetShortcut(Constants.MAIN_SECTION_EVENTS, false).setMessage(data);
        data = new SpannableStringBuilder(getString(R.string.no_event_info));
        mListener.onGetShortcut(Constants.MAIN_SECTION_EVENTS, false).setInfo(data);





        /*
        // Set calendar
        final CalendarView calendar = (CalendarView)rootView.findViewById(R.id.calendar);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            final LinearLayout layout = (LinearLayout)rootView.findViewById(R.id.layout_events);
            layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Logs.add(Logs.Type.V, null);
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)calendar.getLayoutParams();
                    params.height = layout.getHeight();
                    calendar.setLayoutParams(params);
                }
            });
        }
        Calendar date = Calendar.getInstance();
        date.set(Calendar.DAY_OF_MONTH, 1);
        calendar.setMinDate(date.getTimeInMillis());
        date.set(Calendar.DAY_OF_MONTH, 31);
        calendar.setMaxDate(date.getTimeInMillis());
        */






        // Set calendar
        EventCalendar calendar = (EventCalendar) rootView.findViewById(R.id.event_calendar);
        calendar.selectPeriod("2017-01-24 11:15:30", "2017-02-18 11:15:30");




        mEventPager = (ViewPager) rootView.findViewById(R.id.pager_events);
        mEventPager.setAdapter(new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int section) {
                Logs.add(Logs.Type.V, "section: " + section);

                EventFragment fragment = new EventFragment();
                Bundle data = new Bundle();
                data.putInt("test", section);
                fragment.setArguments(data);

                return fragment;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });






        // Set bottom shortcut
        if (getChildFragmentManager().findFragmentById(R.id.shortcut_events_bottom) == null) {
            getChildFragmentManager().beginTransaction().add(R.id.shortcut_events_bottom, new ShortcutFragment()).commit();
            getChildFragmentManager().executePendingTransactions();
        }
        return rootView;
    }
}
