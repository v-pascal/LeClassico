package com.studio.artaban.leclassico.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.helpers.Logs;

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





        addView(rootView);

        // Size calendar
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Logs.add(Logs.Type.V, null);
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);




                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rootView.getLayoutParams();
                params.height = 300;
                rootView.setLayoutParams(params);




            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////



    ////// OnClickListener /////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View v) {




    }
}
