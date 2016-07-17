package com.studio.artaban.leclassico.components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 17/07/16.
 * CurvedViewPager component: ViewPager which displays a page rotation when user has reached the
 *                            left or right limits.
 */
public class CurvedViewPager extends ViewPager {

    public static final String TAG_LEFT_PAGE = "left";
    public static final String TAG_RIGHT_PAGE = "right";

    private static final float RATIO_ROTATION = 0.06f;

    //
    public CurvedViewPager(Context context) {
        super(context);
    }

    public CurvedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private int mPointerId = Constants.NO_DATA;
    private float mPrevX;
    private float mRotation;

    //////
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((getCurrentItem() == 0)||(getCurrentItem() == (getAdapter().getCount() - 1))) {
            switch (event.getActionMasked()) {

                case MotionEvent.ACTION_DOWN: {
                    if (mPointerId == Constants.NO_DATA) {

                        Logs.add(Logs.Type.I, "Start limit scroll");
                        mPointerId = event.getPointerId(0);
                        mPrevX = event.getX(0);
                        mRotation = 0.f;
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    if (mPointerId == Constants.NO_DATA)
                        break;

                    for (int i = 0; i < event.getPointerCount(); ++i) {
                        if (event.getPointerId(i) == mPointerId) {

                            Logs.add(Logs.Type.I, "Finish limit scroll");
                            mPointerId = Constants.NO_DATA;
                            mPrevX = 0f;

                            // Get page according left or right pages
                            View page = (getCurrentItem() == 0)?
                                    findViewWithTag(TAG_LEFT_PAGE):findViewWithTag(TAG_RIGHT_PAGE);

                            if (page == null) {

                                Logs.add(Logs.Type.F, "Tag of the left or right pages not defined");
                                mPointerId = Constants.NO_DATA;

                            } else if ((mRotation > 0.f)||(mRotation < 0.f)) {

                                ObjectAnimator animation = ObjectAnimator.ofFloat(page, "rotationY",
                                        mRotation, 0.f);
                                animation.setDuration(500);
                                animation.start();
                            }
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
                                    findViewWithTag(TAG_LEFT_PAGE):findViewWithTag(TAG_RIGHT_PAGE);

                            int deltaX = (int)(event.getX(i) - mPrevX);
                            if (page == null) {

                                Logs.add(Logs.Type.F, "Tag of the left or right pages not defined");
                                mPointerId = Constants.NO_DATA;

                            } else {

                                mRotation = deltaX * RATIO_ROTATION;
                                page.setRotationY(mRotation);
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
