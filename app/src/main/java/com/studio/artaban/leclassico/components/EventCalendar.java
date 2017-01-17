package com.studio.artaban.leclassico.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

import java.util.Calendar;

/**
 * Created by pascal on 16/01/17.
 * Calendar view
 */
public class EventCalendar extends FrameLayout implements View.OnClickListener {

    public EventCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        Logs.add(Logs.Type.V, "context: " + context + ";attrs: " + attrs);

        // Inflate calendar layout
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_calendar, null);
        rootView.findViewById(R.id.layout_days_update).setVisibility(GONE);

        init((TableLayout) rootView.findViewById(R.id.layout_days));
        init((TableLayout) rootView.findViewById(R.id.layout_days_update));

        // Set days label
        LinearLayout daysLabel = (LinearLayout) rootView.findViewById(R.id.layout_days_label);
        ((TextView) daysLabel.getChildAt(0)).setText(getResources().getStringArray(R.array.calendar_days)[0]);
        ((TextView) daysLabel.getChildAt(1)).setText(getResources().getStringArray(R.array.calendar_days)[1]);
        ((TextView) daysLabel.getChildAt(2)).setText(getResources().getStringArray(R.array.calendar_days)[2]);
        ((TextView) daysLabel.getChildAt(3)).setText(getResources().getStringArray(R.array.calendar_days)[3]);
        ((TextView) daysLabel.getChildAt(4)).setText(getResources().getStringArray(R.array.calendar_days)[4]);
        ((TextView) daysLabel.getChildAt(5)).setText(getResources().getStringArray(R.array.calendar_days)[5]);
        ((TextView) daysLabel.getChildAt(6)).setText(getResources().getStringArray(R.array.calendar_days)[6]);

        // Add click listener to be able to change month...
        rootView.findViewById(R.id.image_prev_month).setOnClickListener(this);
        rootView.findViewById(R.id.image_next_month).setOnClickListener(this);

        // ...and to select a date
        TableLayout month = (TableLayout) rootView.findViewById(R.id.layout_days);
        for (int j = 0; j < 5; ++j) {
            TableRow week = (TableRow) month.getChildAt(j);
            for (int i = 0; i < 7; ++i)
                week.getChildAt(i).setOnClickListener(this);
        }
        addView(rootView); // Add calendar layout

        // Size calendar days (circle)
        final TableLayout layoutDays = (TableLayout) rootView.findViewById(R.id.layout_days);
        layoutDays.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                Logs.add(Logs.Type.V, null);
                layoutDays.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                for (int i = 0; i < 5; ++i) { // Loop on 5 weeks
                    TableRow week = (TableRow) layoutDays.getChildAt(i);
                    TableRow.LayoutParams params = (TableRow.LayoutParams) week.getChildAt(0).getLayoutParams();
                    params.height = layoutDays.getWidth() / 7;

                    week.getChildAt(0).setLayoutParams(params);
                    // NB: Only set first day height coz the others will match parent height accordingly
                }
            }
        });
    }

    //////
    private byte mMonth = Constants.NO_DATA; // Month displayed
    private short mYear = Constants.NO_DATA; // Year of the month displayed

    private void init(TableLayout layoutDays) { // Initialize table layout containing calendar days
        Logs.add(Logs.Type.V, "layoutDays: " + layoutDays);

        // Set weekend days background
        int first,last;
        if (Calendar.getInstance().getFirstDayOfWeek() == Calendar.SUNDAY) {
            first = 0;
            last = 6;

        } else {
            first = 5;
            last = 6;
        }
        ((TableRow) layoutDays.getChildAt(0)).getChildAt(first)
                .setBackground(getResources().getDrawable(R.drawable.calendar_ripple_weekend));
        ((TableRow) layoutDays.getChildAt(0)).getChildAt(last)
                .setBackground(getResources().getDrawable(R.drawable.calendar_ripple_weekend));
        ((TableRow) layoutDays.getChildAt(1)).getChildAt(first)
                .setBackground(getResources().getDrawable(R.drawable.calendar_ripple_weekend));
        ((TableRow) layoutDays.getChildAt(1)).getChildAt(last)
                .setBackground(getResources().getDrawable(R.drawable.calendar_ripple_weekend));
        ((TableRow) layoutDays.getChildAt(2)).getChildAt(first)
                .setBackground(getResources().getDrawable(R.drawable.calendar_ripple_weekend));
        ((TableRow) layoutDays.getChildAt(2)).getChildAt(last)
                .setBackground(getResources().getDrawable(R.drawable.calendar_ripple_weekend));
        ((TableRow) layoutDays.getChildAt(3)).getChildAt(first)
                .setBackground(getResources().getDrawable(R.drawable.calendar_ripple_weekend));
        ((TableRow) layoutDays.getChildAt(3)).getChildAt(last)
                .setBackground(getResources().getDrawable(R.drawable.calendar_ripple_weekend));
        ((TableRow) layoutDays.getChildAt(4)).getChildAt(first)
                .setBackground(getResources().getDrawable(R.drawable.calendar_ripple_weekend));
        ((TableRow) layoutDays.getChildAt(4)).getChildAt(last)
                .setBackground(getResources().getDrawable(R.drawable.calendar_ripple_weekend));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void selectPeriod(@NonNull String start, @Nullable String end) {
        Logs.add(Logs.Type.V, "start: " + start + ";end: " + end);






        ((TextView) findViewById(R.id.text_month)).setText("MARS - 2017");






    }

    ////// OnClickListener /////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View sender) {
        Logs.add(Logs.Type.V, "sender: " + sender);
        switch (sender.getId()) {

            case R.id.image_prev_month: {




                break;
            }
            case R.id.image_next_month: {




                break;
            }
            default: {
                if (sender.getTag() == null)
                    throw new IllegalStateException("No selected period found");
                    // NB: Call 'selectPeriod' method one time (at least)

                short day = (short)sender.getTag();






                break;
            }
        }
    }
}
