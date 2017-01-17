package com.studio.artaban.leclassico.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
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
        final View rootView = LayoutInflater.from(context).inflate(R.layout.layout_calendar, null);
        rootView.findViewById(R.id.layout_days_update).setVisibility(GONE);

        init((TableLayout) rootView.findViewById(R.id.layout_days));
        init((TableLayout) rootView.findViewById(R.id.layout_days_update));
        addView(rootView); // Add calendar layout

        // Size calendar
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Logs.add(Logs.Type.V, null);
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);



                /*
                TableLayout.LayoutParams params = (TableLayout.LayoutParams) week.getLayoutParams();
                params.height = (int)(week.getWidth() / 7.f);
                week.setLayoutParams(params);
                week.requestLayout();
                week.invalidate();
                */


                //FrameLayout.LayoutParams paramsA = (FrameLayout.LayoutParams) rootView.getLayoutParams();
                //paramsA.height = 300;
                //rootView.setLayoutParams(paramsA);




            }
        });
    }

    //////
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
        ((TableRow) layoutDays.getChildAt(5)).getChildAt(first)
                .setBackground(getResources().getDrawable(R.drawable.calendar_ripple_weekend));
        ((TableRow) layoutDays.getChildAt(5)).getChildAt(last)
                .setBackground(getResources().getDrawable(R.drawable.calendar_ripple_weekend));

        // Set days label
        TableRow daysLabel = (TableRow) layoutDays.getChildAt(0);
        ((TextView) daysLabel.getChildAt(0)).setText(getResources().getStringArray(R.array.calendar_days)[0]);
        ((TextView) daysLabel.getChildAt(1)).setText(getResources().getStringArray(R.array.calendar_days)[1]);
        ((TextView) daysLabel.getChildAt(2)).setText(getResources().getStringArray(R.array.calendar_days)[2]);
        ((TextView) daysLabel.getChildAt(3)).setText(getResources().getStringArray(R.array.calendar_days)[3]);
        ((TextView) daysLabel.getChildAt(4)).setText(getResources().getStringArray(R.array.calendar_days)[4]);
        ((TextView) daysLabel.getChildAt(5)).setText(getResources().getStringArray(R.array.calendar_days)[5]);
        ((TextView) daysLabel.getChildAt(6)).setText(getResources().getStringArray(R.array.calendar_days)[6]);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////



    ////// OnClickListener /////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View v) {




    }
}
