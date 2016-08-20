package com.studio.artaban.leclassico.tools;

import android.app.Activity;
import android.graphics.Point;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 20/08/16.
 * In|Out of screen class
 * Move a view from its position to outside & back inside the screen
 */
public final class InOutScreen {

    private Activity mActivity; // View activity
    private static InOutScreen getInstance(Activity activity) {
        return new InOutScreen(activity);
    }
    private InOutScreen(Activity activity) {
        mActivity = activity;
    }

    //////
    public static InOutScreen with(Activity activity) {
        return getInstance(activity);
    }

    // Move location
    public enum Location { TOP, BOTTOM, LEFT, RIGHT }
    private static final Location DEFAULT_MOVE_LOCATION = Location.BOTTOM;
    private Location mLocation = DEFAULT_MOVE_LOCATION; // Location where to move

    public InOutScreen setLocation(Location location) {
        mLocation = location;
        return this;
    }

    // Animation duration
    private static final int DEFAULT_MOVE_DURATION = 500;
    private int mDuration = DEFAULT_MOVE_DURATION; // Move duration (in ms)

    public InOutScreen setDuration(int duration) {
        mDuration = duration;
        return this;
    }

    public interface OnInOutListener { //////////////////////////////////////////////////////////////
        void onAnimationEnd();
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private OnInOutListener mListener; // Animation listener
    public InOutScreen addListener(OnInOutListener listener) {
        mListener = listener;
        return this;
    }

    //////
    public void out(View view) {

        Logs.add(Logs.Type.V, "view: " + view);
        Point screenSize = new Point();
        mActivity.getWindowManager().getDefaultDisplay().getSize(screenSize);

        view.clearAnimation();
        TranslateAnimation anim;
        switch (mLocation) {

            case BOTTOM: {
                anim = new TranslateAnimation(0, 0, 0, screenSize.y - view.getY());
                break;
            }
            case TOP: {
                anim = new TranslateAnimation(0, 0, 0, -view.getY() - view.getHeight());
                break;
            }
            case LEFT: {
                anim = new TranslateAnimation(0, -view.getX() - view.getWidth(), 0, 0);
                break;
            }
            //case RIGHT:
            default: {
                anim = new TranslateAnimation(0, screenSize.x - view.getX(), 0, 0);
                break;
            }
        }
        if (mListener != null)
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Logs.add(Logs.Type.V, "animation: " + animation);
                    mListener.onAnimationEnd();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        anim.setDuration(mDuration);
        anim.setFillAfter(true);
        view.startAnimation(anim);
    }
    public void in(View view) {

        Logs.add(Logs.Type.V, "view: " + view);
        Point screenSize = new Point();
        mActivity.getWindowManager().getDefaultDisplay().getSize(screenSize);

        TranslateAnimation anim;
        switch (mLocation) {

            case BOTTOM: {
                view.setTranslationY(0);
                anim = new TranslateAnimation(0, 0, screenSize.y - view.getY(), 0);
                break;
            }
            case TOP: {
                view.setTranslationY(0);
                anim = new TranslateAnimation(0, 0, -view.getY() - view.getHeight(), 0);
                break;
            }
            case LEFT: {
                view.setTranslationX(0);
                anim = new TranslateAnimation(-view.getX() - view.getWidth(), 0, 0, 0);
                break;
            }
            //case RIGHT:
            default: {
                view.setTranslationX(0);
                anim = new TranslateAnimation(screenSize.x - view.getX(), 0, 0, 0);
                break;
            }
        }
        if (mListener != null)
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Logs.add(Logs.Type.V, "animation: " + animation);
                    mListener.onAnimationEnd();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        anim.setDuration(mDuration);
        view.clearAnimation();
        view.startAnimation(anim);
    }
}
