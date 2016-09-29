package com.studio.artaban.leclassico.animations;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;

import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 29/09/16.
 * Notification list item animator (MainActivity)
 */
public class NotifyItemAnimator extends SimpleItemAnimator {

    private static final int DEFAULT_DURATION_ADD = 1000;
    private static final int DEFAULT_DURATION_CHANGE = 1000;
    private static final int DEFAULT_DURATION_REMOVE = 1000;
    // Default durations (in millisecond)

    public NotifyItemAnimator() {
        Logs.add(Logs.Type.V, null);

        setAddDuration(DEFAULT_DURATION_ADD);
        setChangeDuration(DEFAULT_DURATION_CHANGE);
        setRemoveDuration(DEFAULT_DURATION_REMOVE);
    }
    public NotifyItemAnimator(int durationAdd, int durationChange, int durationRemove) {
        Logs.add(Logs.Type.V, "durationAdd: " + durationAdd + ";durationChange: " + durationChange +
                ";durationRemove: " + durationRemove);

        setAddDuration(durationAdd);
        setChangeDuration(durationChange);
        setRemoveDuration(durationRemove);
    }

    ////// SimpleItemAnimator //////////////////////////////////////////////////////////////////////
    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        return false;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {






        ViewCompat.setTranslationX(holder.itemView, 0);







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
