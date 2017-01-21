package com.studio.artaban.leclassico.components;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by pascal on 16/01/17.
 * Calendar view
 * Managing:
 * _ Date & Period selection
 * _ Previous & next month selection
 */
public class EventCalendar extends FrameLayout implements View.OnClickListener, View.OnTouchListener {

    public EventCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        Logs.add(Logs.Type.V, "context: " + context + ";attrs: " + attrs);

        // Inflate calendar layout
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_calendar, null);
        rootView.findViewById(R.id.layout_days_update).setVisibility(GONE);

        mSundayFirst = Calendar.getInstance().getFirstDayOfWeek() == Calendar.SUNDAY;

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
        rootView.findViewById(R.id.image_prev_month).setOnTouchListener(this);
        rootView.findViewById(R.id.image_next_month).setOnTouchListener(this);

        // ...and to select a date
        final TableLayout month = (TableLayout) rootView.findViewById(R.id.layout_days);
        addDaysListener(month);
        final TableLayout monthUpdate = (TableLayout) rootView.findViewById(R.id.layout_days_update);
        addDaysListener(monthUpdate);

        addView(rootView); // Add calendar layout

        // Size calendar days (circle)
        month.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                Logs.add(Logs.Type.V, null);
                month.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                mDaysWidth = month.getWidth(); // B4 'setDaysSize' calls
                setDaysHeight(month);
                setDaysHeight(monthUpdate);
            }
        });

        // Get month & year text width
        final View monthYear = findViewById(R.id.text_month);
        monthYear.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Logs.add(Logs.Type.V, null);
                monthYear.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                mMonthWidth = monthYear.getWidth();
            }
        });
    }

    //////
    private void addDaysListener(TableLayout month) { // Add click listener on all days
        Logs.add(Logs.Type.V, "month: " + month);

        for (int j = 0; j < 6; ++j) {
            TableRow week = (TableRow) month.getChildAt(j);
            for (int i = 0; i < 7; ++i)
                week.getChildAt(i).setOnClickListener(this);
        }
    }
    private void setDaysHeight(TableLayout month) { // Set days height according calendar width
        Logs.add(Logs.Type.V, "month: " + month);

        for (int i = 0; i < 6; ++i) { // Loop on 6 weeks
            TableRow week = (TableRow) month.getChildAt(i);
            TableRow.LayoutParams params = (TableRow.LayoutParams) week.getChildAt(0).getLayoutParams();
            params.height = mDaysWidth / 7;

            week.getChildAt(0).setLayoutParams(params);
            // NB: Only set first day height coz the others will match parent height accordingly
        }
    }
    private byte mMonth = Constants.NO_DATA; // Month displayed
    private short mYear = Constants.NO_DATA; // Year of the month displayed

    private static final int ANIMATION_DURATION = 300; // Animation duration in ms
    private enum AnimDirection {

        NONE, // Same month & year (no animation)
        BEFORE, // Previous month (right scroll)
        AFTER // Next month (left scroll)
    }
    private int mMonthWidth = Constants.NO_DATA; // Month & Year text view width
    private int mDaysWidth; // Calendar view width
    private boolean mMonthUpdate; // Month view display flag (true for R.id.text_month_update)
    private boolean mDaysUpdate; // Calendar view display flag (true for R.id.layout_days_update)

    private void animateDays(AnimDirection direction) {
        Logs.add(Logs.Type.V, "direction: " + direction);

        TableLayout days = (TableLayout) findViewById((!mDaysUpdate)? R.id.layout_days:R.id.layout_days_update);
        final TableLayout daysUpdate = (TableLayout) findViewById((!mDaysUpdate)? R.id.layout_days_update:R.id.layout_days);

        days.clearAnimation();
        daysUpdate.clearAnimation();
        daysUpdate.setVisibility(VISIBLE);

        //
        TranslateAnimation animDays = new TranslateAnimation(0,
                (direction == AnimDirection.BEFORE) ? mDaysWidth : -mDaysWidth, 0, 0);
        animDays.setDuration(ANIMATION_DURATION);
        animDays.setFillAfter(true);
        animDays.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Logs.add(Logs.Type.V, "animation: " + animation);
                mDaysUpdate = !mDaysUpdate;

                daysUpdate.bringToFront();
                // NB: Needed to be able to display ripple effect correctly
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        days.startAnimation(animDays);

        TranslateAnimation animUpdate = new TranslateAnimation((direction == AnimDirection.BEFORE) ?
                -mDaysWidth : mDaysWidth, 0, 0, 0);
        animUpdate.setDuration(ANIMATION_DURATION);
        animUpdate.setFillAfter(true);
        daysUpdate.startAnimation(animUpdate);
    }
    private void animateMonth(AnimDirection direction) {
        Logs.add(Logs.Type.V, "direction: " + direction);

        TextView month = (TextView) findViewById((!mMonthUpdate)? R.id.text_month:R.id.text_month_update);
        TextView monthUpdate = (TextView) findViewById((!mMonthUpdate)? R.id.text_month_update:R.id.text_month);

        month.clearAnimation();
        monthUpdate.clearAnimation();
        monthUpdate.setVisibility(VISIBLE);

        //
        TranslateAnimation animMonth = new TranslateAnimation(0,
                (direction == AnimDirection.BEFORE) ? mMonthWidth : -mMonthWidth, 0, 0);
        animMonth.setDuration(ANIMATION_DURATION);
        animMonth.setFillAfter(true);
        animMonth.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Logs.add(Logs.Type.V, "animation: " + animation);
                mMonthUpdate = !mMonthUpdate;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        month.startAnimation(animMonth);

        TranslateAnimation animUpdate = new TranslateAnimation((direction == AnimDirection.BEFORE) ?
                -mMonthWidth : mMonthWidth, 0, 0, 0);
        animUpdate.setDuration(ANIMATION_DURATION);
        animUpdate.setFillAfter(true);
        monthUpdate.startAnimation(animUpdate);
    }
    private void animateCalendar(final AnimDirection direction) { // Animate calendar display change
        Logs.add(Logs.Type.V, "direction: " + direction);
        if (direction == AnimDirection.NONE)
            throw new IllegalArgumentException("No animation need");

        animateDays(direction);
        animateMonth(direction);
    }
    private Date mStartDate;
    private Date mEndDate;
    // Selected period dates

    private final boolean mSundayFirst; // First week day (false for Monday)

    private void fillDay(TextView text, int weekDay, int day) { // Set calendar day text value
        //Logs.add(Logs.Type.V, "text: " + text + ";weekDay: " + weekDay + ";day: " + day);

        text.setVisibility(VISIBLE);
        text.setTag((short) day);
        text.setText(String.valueOf(day));
        text.setTextColor(Color.BLACK);
        text.setTypeface(null, Typeface.NORMAL);

        // Set day background
        Calendar calendar = Calendar.getInstance();

        boolean marked = false;
        if ((calendar.get(Calendar.MONTH) == mMonth) && (calendar.get(Calendar.YEAR) == mYear) &&
                (calendar.get(Calendar.DAY_OF_MONTH) == day)) { // Mark current day

            text.setBackground(getResources().getDrawable(R.drawable.calendar_ripple_current));
            text.setTypeface(null, Typeface.BOLD);
            text.setTextColor(Color.WHITE);
            marked = true;
        }
        calendar.setTime(mStartDate);
        if ((calendar.get(Calendar.MONTH) == mMonth) && (calendar.get(Calendar.YEAR) == mYear)) {
            // Mark selected period

            boolean inPeriod = false;
            boolean startPeriod = false;
            if (calendar.get(Calendar.DAY_OF_MONTH) == day)
                startPeriod = true; // Check if period starts today

            else if ((calendar.get(Calendar.DAY_OF_MONTH) < day) && (mEndDate != null)) { // Existing period

                calendar.setTime(mEndDate);
                if ((calendar.get(Calendar.MONTH) != mMonth) || (calendar.get(Calendar.YEAR) != mYear) ||
                        (calendar.get(Calendar.DAY_OF_MONTH) >= day)) // Check if day in period
                inPeriod = true;
            }
            if ((startPeriod) || (inPeriod)) {
                if (marked) // Today
                    text.setTextColor((startPeriod)? Color.RED:getResources().getColor(R.color.colorAccentProfile));
                else {
                    text.setTypeface(null, (startPeriod)? Typeface.BOLD:Typeface.NORMAL);
                    text.setTextColor((startPeriod)? Color.WHITE:Color.BLACK);
                    text.setBackground(getResources().getDrawable((startPeriod) ?
                            R.drawable.calendar_start_background : R.drawable.calendar_ripple_period));
                }
                marked = true; // Done
            }
        }
        if (!marked) // Default background
            text.setBackground(getResources().getDrawable((((!mSundayFirst) && ((weekDay == 5) || (weekDay == 6))) ||
                    ((mSundayFirst) && ((weekDay == 0) || (weekDay == 6))))?
                    R.drawable.calendar_ripple_weekend:R.drawable.calendar_ripple));
    }
    private void fillMonth(boolean current) { // Fill month & Year
        Logs.add(Logs.Type.V, "current: " + current);
        if (mMonthUpdate)
            current = !current;

        ((TextView) findViewById((current)? R.id.text_month:R.id.text_month_update))
                .setText(getResources().getStringArray(R.array.calendar_months)[mMonth] + " - " +
                        String.valueOf(mYear));
    }
    private void fillCalendar(boolean current) {
    // Fill calendar and display selected period and current day (if defined in current month)

        Logs.add(Logs.Type.V, "current: " + current);
        if (mDaysUpdate)
            current = !current;

        Calendar calendar = Calendar.getInstance();
        calendar.set(mYear, mMonth, 1);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK); // First week day (e.g 2 -> Monday)
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 1; // Last day + 1 (e.g 32)
        int day = 1; // Day of month

        // Fill week #1
        TableLayout layout = (TableLayout) findViewById((current)? R.id.layout_days:R.id.layout_days_update);
        TableRow week = (TableRow) layout.getChildAt(0);
        weekDay += (mSundayFirst)? -1:-2; // Start from 0 -> Monday or Sunday (first day of week)
        if (weekDay == Constants.NO_DATA)
            weekDay = 6; // Start week on Sunday with Monday as first week day
        for (int i = 0; i < 7; ++i) {
            if (i < weekDay)
                week.getChildAt(i).setVisibility(INVISIBLE);
            else
                fillDay((TextView) week.getChildAt(i), i, day++);
        }
        // Fill week #2, #3, #4, #5 & #6
        for (int j = 1; j < 6; ++j) {
            week = (TableRow) layout.getChildAt(j);
            for (int i = 0; i < 7; ++i) {
                if (day == lastDay)
                    week.getChildAt(i).setVisibility(INVISIBLE);
                else
                    fillDay((TextView) week.getChildAt(i), i, day++);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void selectPeriod(@NonNull String start, @Nullable String end) {
        Logs.add(Logs.Type.V, "start: " + start + ";end: " + end);

        DateFormat dateFormat = new SimpleDateFormat(Constants.FORMAT_DATE_TIME);
        try {
            mStartDate = dateFormat.parse(start);
            mEndDate = (end != null)? dateFormat.parse(end):null;

            // Display month & year (start date)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mStartDate);
            byte month = (byte)calendar.get(Calendar.MONTH);
            short year = (short)calendar.get(Calendar.YEAR);

            AnimDirection direction = AnimDirection.NONE;
            if ((mMonth != Constants.NO_DATA) && ((mMonth != month) || (mYear != year))) {
                if (mYear == year)
                    direction = (mMonth > month)? AnimDirection.BEFORE:AnimDirection.AFTER;
                else
                    direction = (mYear > year)? AnimDirection.BEFORE:AnimDirection.AFTER;
            }
            mMonth = (byte)calendar.get(Calendar.MONTH);
            mYear = (short)calendar.get(Calendar.YEAR);
            if (direction == AnimDirection.NONE) {
                fillCalendar(true);
                fillMonth(true);
            }
            else { // Animate

                fillCalendar(false);
                fillMonth(false);
                animateCalendar(direction);
            }

        } catch (ParseException e) {
            Logs.add(Logs.Type.E, "Unexpected date format: " + start + " | " + end);
        }
    }

    ////// OnClickListener /////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View sender) {
        Logs.add(Logs.Type.V, "sender: " + sender);

        if (sender.getTag() == null)
            throw new IllegalStateException("No selected period found");
            // NB: Call 'selectPeriod' method one time (at least)

        short day = (short)sender.getTag();






    }

    ////// OnTouchListener /////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onTouch(View sender, MotionEvent event) {
        //Logs.add(Logs.Type.V, "sender: " + sender + ";event: " + event);
        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN: {
                if (mPointerId == Constants.NO_DATA) {
                    mPointerId = event.getPointerId(0);

                    AnimDirection direction;
                    switch (sender.getId()) {

                        case R.id.image_prev_month: {
                            Logs.add(Logs.Type.I, "Select previous month");
                            mFastRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    Logs.add(Logs.Type.V, null);

                                    mFastTask = new ChangeMonthTask();
                                    mFastTask.execute(Boolean.FALSE); // Fast previous
                                }
                            };

                            setPreviousMonth();
                            direction = AnimDirection.BEFORE;
                            break;
                        }
                        case R.id.image_next_month: {
                            Logs.add(Logs.Type.I, "Select next month");
                            mFastRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    Logs.add(Logs.Type.V, null);

                                    mFastTask = new ChangeMonthTask();
                                    mFastTask.execute(Boolean.TRUE); // Fast next
                                }
                            };

                            setNextMonth();
                            direction = AnimDirection.AFTER;
                            break;
                        }
                        default:
                            throw new RuntimeException("Unexpected touch view event");
                    }
                    fillCalendar(false);
                    fillMonth(false);
                    animateCalendar(direction); // Animate selection change

                    // Start fast change request
                    mFastHandler.postDelayed(mFastRunnable, FAST_CHANGE_LIMIT);
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                if (mPointerId == Constants.NO_DATA)
                    break;

                for (int i = 0; i < event.getPointerCount(); ++i) {
                    if (event.getPointerId(i) == mPointerId) {
                        Logs.add(Logs.Type.I, "Finish change month selection");

                        mFastHandler.removeCallbacks(mFastRunnable); // Cancel fast change request
                        if ((mFastTask != null) && (mFastTask.getStatus() == AsyncTask.Status.RUNNING))
                            mFastTask.cancel(true); // Stop fast change feature
                        else // Fast change feature not started
                            mPointerId = Constants.NO_DATA;
                        break;
                    }
                }
                break;
            }
        }
        return false;
    }

    //////
    private void setPreviousMonth() {
        Logs.add(Logs.Type.V, null);

        --mMonth;
        if (mMonth < 0) {
            mMonth = 11;
            --mYear;
        }
    }
    private void setNextMonth() {
        Logs.add(Logs.Type.V, null);

        ++mMonth;
        if (mMonth == 12) {
            mMonth = 0;
            ++mYear;
        }
    }
    private int mPointerId = Constants.NO_DATA; // ID of the touch event pointer

    private final Handler mFastHandler = new Handler(); // Handler used to check fast change limit elapsed
    private Runnable mFastRunnable; // Fast month search process
    private static final long FAST_CHANGE_LIMIT = 1500; // Duration limit B4 starting fast change (in ms)
    // NB: Constant above must be > ANIMATION_DURATION

    private ChangeMonthTask mFastTask; // Task to change month quickly
    private class ChangeMonthTask extends AsyncTask<Boolean, Boolean, Boolean> {

        private static final int FAST_CHANGE_DURATION = 100; // Fast change month duration (in ms)

        @Override
        protected synchronized Boolean doInBackground(Boolean... direction) {
            Logs.add(Logs.Type.V, "direction[0]: " + direction[0]);
            try {
                do {
                    publishProgress(direction[0]); // Change month in UI thread
                    Thread.sleep(FAST_CHANGE_DURATION, 0);

                    // Wait UI thread do the job
                    wait();

                } while (!isCancelled());

            } catch (InterruptedException e) {
                Logs.add(Logs.Type.I, "Fast change feature interrupted");
            }
            return direction[0];
        }

        @Override
        protected void onProgressUpdate(Boolean... direction) {
            //Logs.add(Logs.Type.V, "direction[0]: " + direction[0]);

            if (direction[0])
                setNextMonth();
            else
                setPreviousMonth();
            fillMonth(true);

            // Notify background thread job done
            synchronized (this) {
                notify();
            }
        }

        @Override
        protected void onCancelled(Boolean direction) {
            Logs.add(Logs.Type.V, "direction: " + direction);
            mPointerId = Constants.NO_DATA;

            fillCalendar(false);
            fillMonth(false);

            // Animate selection change (last one)
            animateCalendar((direction) ? AnimDirection.AFTER : AnimDirection.BEFORE);
        }
    }
}
