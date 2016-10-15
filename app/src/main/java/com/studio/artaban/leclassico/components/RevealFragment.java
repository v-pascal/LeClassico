package com.studio.artaban.leclassico.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 20/08/16.
 * Reveal fragment class is a fragment that makes a displaying & hiding animation
 * NB: At the moment it is a circular reveal animation
 */
public class RevealFragment extends Fragment {

    public interface OnRevealListener { ////////////////////////////////////////////////////////////
        void onRevealEnd();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int DEFAULT_REVEAL_DURATION = 700; // In millisecond

    //
    private boolean mReveal; // Reveal animation flag
    private OnRevealListener mListener; // Reveal listener
    private int mDuration; // Reveal duration

    public void reveal(boolean display, final OnRevealListener listener, int duration) {

        Logs.add(Logs.Type.V, "display: " + display + ";listener: " + listener + ";duration: " + duration);
        if (!display) { // Hide

            int deltaX = mRootView.getWidth() >> 1;
            int deltaY = mRootView.getHeight() >> 1;
            Animator anim = ViewAnimationUtils.createCircularReveal(mRootView, deltaX, deltaY,
                    (float)Math.hypot(deltaX, deltaY), 0);
            if (listener != null)
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        Logs.add(Logs.Type.V, "animation: " + animation);
                        mRootView.setVisibility(View.GONE);
                        listener.onRevealEnd();
                    }
                });
            anim.setDuration(duration);
            anim.start();

        } else { // Display

            mReveal = true;
            mDuration = duration;
            if (listener != null)
                mListener = listener;
        }
    }
    public void reveal(boolean display, OnRevealListener listener) {
        reveal(display, listener, DEFAULT_REVEAL_DURATION);
    }

    //
    protected View mRootView; // Fragment root view

    //////
    @Override
    public void onResume() {
        super.onResume();
        if (mReveal) {
            Logs.add(Logs.Type.I, "Reveal fragment");

            mRootView.setVisibility(View.INVISIBLE);
            new Handler().post(new Runnable() {
                @Override
                public void run() {

                    Logs.add(Logs.Type.V, null);
                    int deltaX = mRootView.getWidth() >> 1;
                    int deltaY = mRootView.getHeight() >> 1;
                    try {

                        Animator anim = ViewAnimationUtils.createCircularReveal(mRootView, deltaX, deltaY,
                                0, (float) Math.hypot(deltaX, deltaY));
                        if (mListener != null)
                            anim.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);

                                    Logs.add(Logs.Type.V, "animation: " + animation);
                                    mListener.onRevealEnd();
                                }
                            });

                        anim.setDuration(mDuration);
                        mRootView.setVisibility(View.VISIBLE);
                        anim.start();

                    } catch (IllegalStateException e) {
                        Logs.add(Logs.Type.W, "Fragment root view is not attached yet");

                        // Display fragment without animation
                        mRootView.setVisibility(View.VISIBLE);
                        if (mListener != null)
                            mListener.onRevealEnd();
                    }
                }
            });
            mReveal = false;
        }
    }
}
