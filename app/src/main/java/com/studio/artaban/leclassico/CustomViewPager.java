package com.studio.artaban.leclassico;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 16/07/16.
 */
public class CustomViewPager extends ViewPager {

    public CustomViewPager(Context context) {
        super(context);
    }
    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    private int mDeltaX = 0;
    public int test() {
        return mDeltaX;
    }

    //
    private int mPointerId = Constants.NO_DATA;
    private float mPrevX = 0.f;

    //////
    @Override
    public boolean onTouchEvent(MotionEvent event) {



        //Logs.add(Logs.Type.V, "X: " + event.getX() + " Y: " + event.getY());
        //Logs.add(Logs.Type.I, "X: " + getX() + " Y: " + getY());
        if (getCurrentItem() == 0) {
            switch (event.getActionMasked()) {

                case MotionEvent.ACTION_DOWN: {
                    if (mPointerId == Constants.NO_DATA) {

                        Logs.add(Logs.Type.I, "Start limit scroll");
                        mPointerId = event.getPointerId(0);
                        mPrevX = event.getX(0);
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
                            mDeltaX = 0;


                            //view.setRotationY(0);


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




                            mDeltaX = (int)(event.getX(i) - mPrevX);
                            /*
                            if (deltaX > 10) {


                                //view.setRotationX(deltaX << 1);
                                Logs.add(Logs.Type.V, "OK: " + deltaX);
                                //view.setRotationY(45);
                                view.setRotationY(deltaX);


                            }
                            else
                                Logs.add(Logs.Type.V, "BAD: " + deltaX);
                                */




                            //mPrevX = event.getX(i);
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
