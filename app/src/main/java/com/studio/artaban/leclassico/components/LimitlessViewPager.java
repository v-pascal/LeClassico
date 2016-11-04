package com.studio.artaban.leclassico.components;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 17/07/16.
 * LimitlessViewPager component class:
 * ViewPager with unbounded behaviour, accessing limit crossed position when the user has reached the
 * left/top or right/bottom limits.
 */
public class LimitlessViewPager extends ViewPager {

    public static final String TAG_PAGE_LEFT_TOP = "leftTop";
    public static final String TAG_PAGE_RIGHT_BOTTOM = "rightBottom";
    // Tags

    public interface OnLimitCrossedListener { //////////////////////////////////////////////////////

        boolean onStartBehavior(boolean leftTop, float originX, float originY);
        boolean onFinishBehavior(boolean leftTop);

        boolean onLeftTopLimitCrossed(View page, float deltaX, float deltaY);
        boolean onRightBottomLimitCrossed(View page, float deltaX, float deltaY);
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public LimitlessViewPager(Context context) {
        super(context);
    }
    public LimitlessViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //
    private OnLimitCrossedListener mListener;
    public void setOnLimitlessListener(OnLimitCrossedListener listener) {
        mListener = listener;
    }

    //
    private static final String ERROR_INVALID_PAGE_TAG = "Tag of the left/top or right/bottom pages not defined";
    private static final String ERROR_LISTENER_NOT_DEFINED = "'OnLimitCrossedListener' not defined";

    private int mPointerId = Constants.NO_DATA;
    private float mPrevX;
    private float mPrevY;

    //////
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((getCurrentItem() == 0) || (getCurrentItem() == (getAdapter().getCount() - 1))) {
            switch (event.getActionMasked()) {

                case MotionEvent.ACTION_DOWN: {
                    if (mPointerId == Constants.NO_DATA) {

                        Logs.add(Logs.Type.I, "Start unbounded scroll");
                        mPointerId = event.getPointerId(0);
                        mPrevX = event.getX(0);
                        mPrevY = event.getY(0);

                        if (mListener != null) {

                            // Check page tag definition
                            View page = (getCurrentItem() == 0)?
                                    findViewWithTag(TAG_PAGE_LEFT_TOP):findViewWithTag(TAG_PAGE_RIGHT_BOTTOM);
                            if (page == null) {

                                Logs.add(Logs.Type.F, ERROR_INVALID_PAGE_TAG);
                                mPointerId = Constants.NO_DATA;
                                break;
                            }
                            if (mListener.onStartBehavior(getCurrentItem() == 0, mPrevX, mPrevY))
                                return true;

                        } else {

                            Logs.add(Logs.Type.F, ERROR_LISTENER_NOT_DEFINED);
                            mPointerId = Constants.NO_DATA;
                        }
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    if (mPointerId == Constants.NO_DATA)
                        break;

                    for (int i = 0; i < event.getPointerCount(); ++i) {
                        if (event.getPointerId(i) == mPointerId) {

                            Logs.add(Logs.Type.I, "Finish unbounded scroll");
                            mPointerId = Constants.NO_DATA;
                            mPrevX = 0f;
                            mPrevY = 0f;

                            if (mListener.onFinishBehavior(getCurrentItem() == 0))
                                return true;
                            break;
                        }
                    }
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (mPointerId == Constants.NO_DATA)
                        break;

                    for (int i = 0; i < event.getPointerCount(); ++i) {
                        if (event.getPointerId(i) == mPointerId) {

                            // Get page according left or right pages
                            View page = (getCurrentItem() == 0)?
                                    findViewWithTag(TAG_PAGE_LEFT_TOP):findViewWithTag(TAG_PAGE_RIGHT_BOTTOM);

                            int deltaX = (int)(event.getX(i) - mPrevX);
                            int deltaY = (int)(event.getY(i) - mPrevY);
                            if (getCurrentItem() == 0) {
                                if (mListener.onLeftTopLimitCrossed(page, deltaX, deltaY))
                                    return true;

                            } else {
                                if (mListener.onRightBottomLimitCrossed(page, deltaX, deltaY))
                                    return true;
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
