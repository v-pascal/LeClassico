package com.studio.artaban.leclassico.animations;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 29/09/16.
 * Notification list item animator (MainActivity)
 */
public class FadeInRightAnimator extends SimpleItemAnimator {

    private static final int DEFAULT_DURATION_ADD = 1000;
    private static final int DEFAULT_DURATION_CHANGE = 1000;
    private static final int DEFAULT_DURATION_REMOVE = 1000;
    // Default durations (in millisecond)

    private Interpolator mInterpolator; // Animation interpolator

    public FadeInRightAnimator() {
        Logs.add(Logs.Type.V, null);

        setAddDuration(DEFAULT_DURATION_ADD);
        setChangeDuration(DEFAULT_DURATION_CHANGE);
        setRemoveDuration(DEFAULT_DURATION_REMOVE);

        mInterpolator = new OvershootInterpolator(1f);







        setSupportsChangeAnimations(false);







    }
    public FadeInRightAnimator(int durationAdd, int durationChange, int durationRemove,
                               Interpolator interpolator) {

        Logs.add(Logs.Type.V, "durationAdd: " + durationAdd + ";durationChange: " + durationChange +
                ";durationRemove: " + durationRemove + ";interpolator: " + interpolator);

        setAddDuration((durationAdd != Constants.NO_DATA) ? durationAdd : DEFAULT_DURATION_ADD);
        setAddDuration((durationChange != Constants.NO_DATA)? durationChange:DEFAULT_DURATION_CHANGE);
        setAddDuration((durationRemove != Constants.NO_DATA)? durationRemove:DEFAULT_DURATION_REMOVE);

        mInterpolator = (interpolator != null)? interpolator:new OvershootInterpolator(1f);
    }

    ////// SimpleItemAnimator //////////////////////////////////////////////////////////////////////
    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        return false;
    }

    @Override
    public boolean animateAdd(final RecyclerView.ViewHolder holder) {
        Logs.add(Logs.Type.V, "holder: " + holder);
        ViewCompat.animate(holder.itemView).cancel();

        ViewCompat.setTranslationX(holder.itemView, holder.itemView.getRootView().getWidth() * .25f);
        ViewCompat.setAlpha(holder.itemView, 0);

        dispatchAddStarting(holder);
        ViewCompat.animate(holder.itemView)
                .translationX(0)
                .alpha(1)
                .setDuration(getAddDuration())
                .setInterpolator(mInterpolator)




                //.setStartDelay(Math.abs(holder.getAdapterPosition() * getAddDuration() / 4))




                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {

                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        dispatchAddFinished(holder);
                    }

                    @Override
                    public void onAnimationCancel(View view) {

                    }
                })
                .start();

        return false;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        return false;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder,
                                 int fromLeft, int fromTop, int toLeft, int toTop) {
        return false;
    }

    @Override
    public void runPendingAnimations() {

    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {

    }

    @Override
    public void endAnimations() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
