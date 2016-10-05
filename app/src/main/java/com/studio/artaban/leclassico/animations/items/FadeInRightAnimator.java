package com.studio.artaban.leclassico.animations.items;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 03/10/16.
 * Fade & translate from the right recycler view item
 */
public class FadeInRightAnimator extends BaseItemAnimator {

    @Override
    protected void cancelAnimation(AnimInfo info) {

        //Logs.add(Logs.Type.V, "info: " + info);
        if (info.mAnimation.isRunning())
            info.mAnimation.cancel();

        View itemView = info.mHolder.itemView;
        switch (getAnimType(info)) {

            case REMOVE: {


                break;
            }
            case MOVE: {

                itemView.setTranslationX(0);
                itemView.setTranslationY(0);
                break;
            }
            case CHANGE: {





                //if (info.mNewAnimation.isRunning())
                //    info.mNewAnimation.cancel();





                break;
            }
            case ADD: {

                itemView.setTranslationX(0);
                itemView.setAlpha(1);
                break;
            }
        }
    }

    @Override
    protected boolean prepareAnimation(AnimInfo info) {
        //Logs.add(Logs.Type.V, "info: " + info);

        View itemView = info.mHolder.itemView;
        switch (getAnimType(info)) {

            case REMOVE: {


                break;
            }
            case MOVE: {

                MoveInfo moveInfo = (MoveInfo)info;
                moveInfo.mFromX += itemView.getTranslationX();
                moveInfo.mFromY += itemView.getTranslationY();
                endAnimation(info.mHolder);
                int deltaX = moveInfo.mToX - moveInfo.mFromX;
                int deltaY = moveInfo.mToY - moveInfo.mFromY;
                if ((deltaX == 0) && (deltaY == 0)) {
                    dispatchMoveFinished(info.mHolder);
                    return false;
                }
                if (deltaX != 0) itemView.setTranslationX(-deltaX);
                if (deltaY != 0) itemView.setTranslationY(-deltaY);
                break;
            }
            case CHANGE: {


                break;
            }
            case ADD: {

                itemView.setTranslationX(itemView.getRootView().getWidth() * 0.25f);
                itemView.setAlpha(0);
                break;
            }
        }
        return true;
    }

    @Override
    protected void implementAnimation(final AnimInfo info) {
        //Logs.add(Logs.Type.V, "info: " + info);

        final AnimType type = getAnimType(info);
        switch (type) {

            case REMOVE: {




                info.mAnimation.setDuration(getRemoveDuration());
                mStartedRemovals.add(info);
                break;
            }
            case MOVE: {

                info.mAnimation.play(ObjectAnimator.ofFloat(info.mHolder.itemView, "translateX", 0))
                        .with(ObjectAnimator.ofFloat(info.mHolder.itemView, "translateY", 0));
                info.mAnimation.setDuration(getMoveDuration());
                mStartedMoves.add(info);
                break;
            }
            case CHANGE: {





                //info.mAnimation.setDuration(getChangeDuration());
                //mStartedChanges.add(info);






                return;
            }
            case ADD: {

                info.mAnimation.play(ObjectAnimator.ofFloat(info.mHolder.itemView,
                        "translateX", info.mHolder.itemView.getRootView().getWidth() * 0.25f, 0f))
                        .with(ObjectAnimator.ofFloat(info.mHolder.itemView,
                                "alpha", 0f, 1f));
                info.mAnimation.setDuration(getAddDuration());
                mStartedAdditions.add(info);
                break;
            }
        }
        info.mAnimation.setTarget(info.mHolder.itemView);
        info.mAnimation.setInterpolator(mInterpolator);
        info.mAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                switch (type) {
                    case REMOVE: dispatchRemoveStarting(info.mHolder); break;
                    case MOVE: dispatchMoveStarting(info.mHolder); break;
                    case ADD: dispatchAddStarting(info.mHolder); break;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                info.mAnimation.removeListener(this);
                switch (type) {

                    case REMOVE: {
                        dispatchRemoveFinished(info.mHolder);
                        mStartedRemovals.remove(info);
                        break;
                    }
                    case MOVE: {
                        dispatchMoveFinished(info.mHolder);
                        mStartedMoves.remove(info);
                        break;
                    }
                    case ADD: {
                        dispatchAddFinished(info.mHolder);
                        mStartedAdditions.remove(info);
                        break;
                    }
                }
                if (!isRunning())
                    dispatchAnimationsFinished();
            }
        });
        info.mAnimation.start();
    }
}
