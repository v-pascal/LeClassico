package com.studio.artaban.leclassico.animations;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pascal on 03/10/16.
 * Item animator for recycler view
 */
public class RecyclerItemAnimator extends SimpleItemAnimator {

    public static final int DEFAULT_DURATION = 500; // Default animation duration (in millisecond)

    protected Interpolator mInterpolator; // Animation interpolator
    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    public RecyclerItemAnimator() {
        Logs.add(Logs.Type.V, null);

        setAddDuration(DEFAULT_DURATION);
        setChangeDuration(DEFAULT_DURATION);
        setRemoveDuration(DEFAULT_DURATION);
        setMoveDuration(DEFAULT_DURATION);

        mInterpolator = new LinearInterpolator();
    }

    private ArrayList<RemoveInfo> mPendingRemovals = new ArrayList<>();
    private ArrayList<MoveInfo> mPendingMoves = new ArrayList<>();
    private ArrayList<ChangeInfo> mPendingChanges = new ArrayList<>();
    private ArrayList<AddInfo> mPendingAdditions = new ArrayList<>();
    // Pending animation info

    private ArrayList<ArrayList<MoveInfo>> mProcessingMoves = new ArrayList<>();
    private ArrayList<ArrayList<ChangeInfo>> mProcessingChanges = new ArrayList<>();
    private ArrayList<ArrayList<ViewHolder>> mProcessingAdditions = new ArrayList<>();
    // Processing animation info

    private void startMoveAnimation(final MoveInfo info) {
        //Logs.add(Logs.Type.V, "info: " + info);

        if ((info.mToX - info.mFromX) != 0) ViewCompat.animate(info.mHolder.itemView).translationX(0);
        if ((info.mToY - info.mFromY) != 0) ViewCompat.animate(info.mHolder.itemView).translationY(0);

        mStartedMoves.add(info.mHolder);
        final ViewPropertyAnimatorCompat animation = ViewCompat.animate(info.mHolder.itemView);
        animation.setDuration(getMoveDuration());
        animation.setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {
                dispatchMoveStarting(info.mHolder);
            }

            @Override
            public void onAnimationEnd(View view) {
                animation.setListener(null);
                ViewCompat.setTranslationX(view, 0);
                ViewCompat.setTranslationY(view, 0);
                dispatchMoveFinished(info.mHolder);
                mStartedMoves.remove(info.mHolder);
                if (!isRunning())
                    dispatchAnimationsFinished();
            }

            @Override
            public void onAnimationCancel(View view) {

            }
        }).start();
    }
    private void startChangeAnimation(ChangeInfo changeInfo, final boolean changeNew) {
        //Logs.add(Logs.Type.V, "changeInfo: " + changeInfo + ";changeNew: " + changeNew);

        final ViewPropertyAnimatorCompat animation = mMaker.onAnimate(changeInfo, changeNew);
        final ViewHolder holder = (changeNew)? changeInfo.mNewHolder:changeInfo.mHolder;
        mStartedChanges.add(holder);
        animation.setDuration(getChangeDuration());
        animation.setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {
                dispatchChangeStarting(holder, !changeNew);
            }

            @Override
            public void onAnimationEnd(View view) {
                animation.setListener(null);
                mMaker.onCancel(AnimType.CHANGE, view);
                dispatchChangeFinished(holder, !changeNew);
                mStartedChanges.remove(holder);
                if (!isRunning())
                    dispatchAnimationsFinished();
            }

            @Override
            public void onAnimationCancel(View view) {

            }
        }).start();
    }

    //
    private void endChangeAnimation(List<ChangeInfo> changeList, ViewHolder item) {
        //Logs.add(Logs.Type.V, "changeList: " + changeList + ";item: " + item);
        for (int i = changeList.size() - 1; i > Constants.NO_DATA; --i) {
            ChangeInfo changeInfo = changeList.get(i);
            if ((endChangeAnimationIfNeeded(changeInfo, item)) &&
                    (changeInfo.mHolder == null) && (changeInfo.mNewHolder == null))
                changeList.remove(changeInfo);
        }
    }
    private void endChangeAnimationIfNeeded(ChangeInfo changeInfo) {
        //Logs.add(Logs.Type.V, "changeInfo: " + changeInfo);
        if (changeInfo.mHolder != null) endChangeAnimationIfNeeded(changeInfo, changeInfo.mHolder);
        if (changeInfo.mNewHolder != null) endChangeAnimationIfNeeded(changeInfo, changeInfo.mNewHolder);
    }
    private boolean endChangeAnimationIfNeeded(ChangeInfo changeInfo, ViewHolder item) {
        //Logs.add(Logs.Type.V, "changeInfo: " + changeInfo + ";item: " + item);
        boolean oldItem = false;

        if (changeInfo.mNewHolder == item) changeInfo.mNewHolder = null;
        else if (changeInfo.mHolder == item) {
            changeInfo.mHolder = null;
            oldItem = true;
        } else
            return false;

        mMaker.onCancel(AnimType.CHANGE, item.itemView);
        dispatchChangeFinished(item, oldItem);
        return true;
    }

    ////// ItemAnimatorMaker ///////////////////////////////////////////////////////////////////////

    public interface ItemAnimatorMaker {

        void onCancel(AnimType type, View item);
        void onPrepare(AnimInfo info);
        ViewPropertyAnimatorCompat onAnimate(AnimInfo info, boolean changeNew);
    }
    private ItemAnimatorMaker mMaker; // Item animator maker
    public void setAnimationMaker(ItemAnimatorMaker maker) {
        mMaker = maker;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public enum AnimType { REMOVE, MOVE, CHANGE, ADD };
    public static AnimType getAnimType(AnimInfo info) {
        //Logs.add(Logs.Type.V, "info: " + info);
        if (info.getClass().isAssignableFrom(RemoveInfo.class))
            return AnimType.REMOVE;
        if (info.getClass().isAssignableFrom(MoveInfo.class))
            return AnimType.MOVE;
        if (info.getClass().isAssignableFrom(ChangeInfo.class))
            return AnimType.CHANGE;

        return AnimType.ADD;
    }

    private ArrayList<ViewHolder> mStartedRemovals = new ArrayList<>();
    private ArrayList<ViewHolder> mStartedMoves = new ArrayList<>();
    private ArrayList<ViewHolder> mStartedChanges = new ArrayList<>();
    private ArrayList<ViewHolder> mStartedAdditions = new ArrayList<>();
    // Started animation holder

    public static class AnimInfo {
        public ViewHolder mHolder;
        public static <T> int find(ArrayList<T> animList, ViewHolder holder) {
            for (int i = 0; i < animList.size(); ++i)
                if (((AnimInfo)animList.get(i)).mHolder == holder)
                    return i;

            return Constants.NO_DATA;
        }
        private AnimInfo(ViewHolder holder) {
            mHolder = holder;
        }
    }
    private static class RemoveInfo extends AnimInfo {
        private RemoveInfo(ViewHolder holder) {
            super(holder);
        }
    }
    public static class AddInfo extends AnimInfo {
        private AddInfo(ViewHolder holder) {
            super(holder);
        }
    }
    private static class MoveInfo extends AnimInfo {
        public int mFromX, mFromY, mToX, mToY;

        private MoveInfo(ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            //Logs.add(Logs.Type.V, "holder: " + holder + ";fromX: " + fromX + ";fromY: " + fromY +
            //        ";toX: " + toX + ";toY: " + toY);
            super(holder);
            mFromX = fromX;
            mFromY = fromY;
            mToX = toX;
            mToY = toY;
        }
    }
    public static class ChangeInfo extends MoveInfo {

        public ViewHolder mNewHolder;
        private ChangeInfo(ViewHolder oldHolder, ViewHolder newHolder,
                           int fromX, int fromY, int toX, int toY) {
            super(oldHolder, fromX, fromY, toX, toY);
            mNewHolder = newHolder;
        }
    }

    //////
    private int mDisableCount = 0; // Change animation count to disable
    public void setDisableChangeAnimations(int count) {

        Logs.add(Logs.Type.V, "count: " + count);
        if (count > 0)
            mDisableCount = count;
        else
            throw new IllegalArgumentException("Disable change animation count must be > 0");
    }

    private boolean decreaseDisableChangeAnimations() {
        //Logs.add(Logs.Type.V, null);
        if (mDisableCount == 0)
            return false; // Enable

        return (mDisableCount-- > 0);
    }

    ////// SimpleItemAnimator //////////////////////////////////////////////////////////////////////
    @Override
    public boolean animateRemove(ViewHolder holder) {
        //Logs.add(Logs.Type.V, "holder: " + holder);

        endAnimation(holder);
        mPendingRemovals.add(new RemoveInfo(holder));
        return true;
    }
    @Override
    public boolean animateAdd(ViewHolder holder) {
        //Logs.add(Logs.Type.V, "holder: " + holder);

        endAnimation(holder);
        AddInfo addInfo = new AddInfo(holder);
        mMaker.onPrepare(addInfo);
        mPendingAdditions.add(addInfo);
        return true;
    }
    @Override
    public boolean animateMove(ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        //Logs.add(Logs.Type.V, "holder: " + holder + ";fromX: " + fromX + ";fromY: " + fromY +
        //        ";toX: " + toX + ";toY: " + toY);

        fromX += ViewCompat.getTranslationX(holder.itemView);
        fromY += ViewCompat.getTranslationY(holder.itemView);
        endAnimation(holder);
        int deltaX = toX - fromX;
        int deltaY = toY - fromY;
        if ((deltaX == 0) && (deltaY == 0)) {
            dispatchMoveFinished(holder);
            return false;
        }
        if (deltaX != 0) ViewCompat.setTranslationX(holder.itemView, -deltaX);
        if (deltaY != 0) ViewCompat.setTranslationY(holder.itemView, -deltaY);
        mPendingMoves.add(new MoveInfo(holder, fromX, fromY, toX, toY));
        return true;
    }
    @Override
    public boolean animateChange(ViewHolder oldHolder, ViewHolder newHolder,
                                 int fromLeft, int fromTop, int toLeft, int toTop) {
        //Logs.add(Logs.Type.V, "oldHolder: " + oldHolder + ";newHolder: " + newHolder +
        //        ";fromLeft: " + fromLeft + ";fromTop: " + fromTop + ";toLeft: " + toLeft +
        //        ";toTop: " + toTop);
        if (decreaseDisableChangeAnimations()) {

            dispatchChangeStarting(oldHolder, true);
            dispatchChangeStarting(newHolder, false);
            dispatchChangeFinished(oldHolder, true);
            dispatchChangeFinished(newHolder, false);
            if (mDisableCount == 0)
                dispatchAnimationsFinished();

            return false;
        }
        ChangeInfo changeInfo = new ChangeInfo(oldHolder, newHolder, fromLeft, fromTop, toLeft, toTop);
        mMaker.onPrepare(changeInfo);
        mPendingChanges.add(changeInfo);
        return true;
    }

    @Override
    public void runPendingAnimations() {
        //Logs.add(Logs.Type.V, null);

        boolean removalsPending = !mPendingRemovals.isEmpty();
        boolean movesPending = !mPendingMoves.isEmpty();
        boolean changesPending = !mPendingChanges.isEmpty();
        boolean additionsPending = !mPendingAdditions.isEmpty();
        if ((!removalsPending) && (!movesPending) && (!additionsPending) && (!changesPending))
            return; // Nothing to do

        //// Remove
        if (removalsPending) {
            for (RemoveInfo removeInfo : mPendingRemovals) {

                final ViewHolder holder = removeInfo.mHolder;
                final ViewPropertyAnimatorCompat animation = mMaker.onAnimate(removeInfo, false);
                mStartedRemovals.add(holder);
                animation.setDuration(getRemoveDuration());
                animation.setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        dispatchRemoveStarting(holder);
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        animation.setListener(null);
                        //mMaker.onCancel(AnimType.REMOVE, view); // Not needed (will be removed)
                        dispatchRemoveFinished(holder);
                        mStartedRemovals.remove(holder);
                        if (!isRunning())
                            dispatchAnimationsFinished();
                    }

                    @Override
                    public void onAnimationCancel(View view) {

                    }
                }).start();
            }
            mPendingRemovals.clear();
        }

        ////// Move
        if (movesPending) {
            final ArrayList<MoveInfo> moves = new ArrayList<>();
            moves.addAll(mPendingMoves);
            mProcessingMoves.add(moves);
            mPendingMoves.clear();
            Runnable mover = new Runnable() {
                @Override
                public void run() {
                    for (MoveInfo moveInfo : moves)
                        startMoveAnimation(moveInfo);
                    moves.clear();
                    mProcessingMoves.remove(moves);
                }
            };
            if (removalsPending) // Check if needed to wait previous remove stuff
                ViewCompat.postOnAnimationDelayed(moves.get(0).mHolder.itemView, mover, getRemoveDuration());
            else
                mover.run();
        }

        ////// Change (run in parallel with move animations)
        if (changesPending) {
            final ArrayList<ChangeInfo> changes = new ArrayList<>();
            changes.addAll(mPendingChanges);
            mProcessingChanges.add(changes);
            mPendingChanges.clear();
            Runnable changer = new Runnable() {
                @Override
                public void run() {
                    for (ChangeInfo changeInfo : changes) {

                        if ((changeInfo.mHolder != null) && (changeInfo.mHolder.itemView != null))
                            startChangeAnimation(changeInfo, false);
                        if ((changeInfo.mNewHolder != null) && (changeInfo.mNewHolder.itemView != null))
                            startChangeAnimation(changeInfo, true);
                    }
                    changes.clear();
                    mProcessingChanges.remove(changes);
                }
            };
            if (removalsPending)
                ViewCompat.postOnAnimationDelayed(changes.get(0).mHolder.itemView, changer, getRemoveDuration());
            else
                changer.run();
        }

        ////// Add
        if (additionsPending) {
            final ArrayList<ViewHolder> additions = new ArrayList<>();
            for (AddInfo addInfo : mPendingAdditions)
                additions.add(addInfo.mHolder);
            mProcessingAdditions.add(additions);
            mPendingAdditions.clear();
            Runnable adder = new Runnable() {
                public void run() {
                    for (final ViewHolder holder : additions) {

                        final ViewPropertyAnimatorCompat animation =
                                mMaker.onAnimate(new AddInfo(holder), false);
                        mStartedAdditions.add(holder);
                        animation.setDuration(getRemoveDuration());
                        animation.setListener(new ViewPropertyAnimatorListener() {
                            @Override
                            public void onAnimationStart(View view) {
                                dispatchAddStarting(holder);
                            }

                            @Override
                            public void onAnimationEnd(View view) {
                                animation.setListener(null);
                                mMaker.onCancel(AnimType.ADD, view);
                                dispatchAddFinished(holder);
                                mStartedAdditions.remove(holder);
                                if (!isRunning())
                                    dispatchAnimationsFinished();
                            }

                            @Override
                            public void onAnimationCancel(View view) {

                            }
                        }).start();
                    }
                    additions.clear();
                    mProcessingAdditions.remove(additions);
                }
            };
            if ((removalsPending) || (movesPending) || (changesPending)) { // Check to wait previous stuff
                long removeDuration = (removalsPending)? getRemoveDuration() : 0;
                long moveDuration = (movesPending)? getMoveDuration() : 0;
                long changeDuration = (changesPending)? getChangeDuration() : 0;
                long totalDelay = removeDuration + Math.max(moveDuration, changeDuration);

                ViewCompat.postOnAnimationDelayed(additions.get(0).itemView, adder, totalDelay);

            } else
                adder.run();
        }
    }

    @Override
    public void endAnimation(ViewHolder item) {
        //Logs.add(Logs.Type.V, "item: " + item);
        ViewCompat.animate(item.itemView).cancel();

        ////// Terminate pending animation
        // Move
        for (int i = mPendingMoves.size() - 1; i > Constants.NO_DATA; --i) {
            if (mPendingMoves.get(i).mHolder == item) {
                ViewCompat.setTranslationY(item.itemView, 0);
                ViewCompat.setTranslationX(item.itemView, 0);
                dispatchMoveFinished(item);
                mPendingMoves.remove(i);
            }
        }

        // Change
        endChangeAnimation(mPendingChanges, item);

        // Remove
        int itemIdx = AnimInfo.find(mPendingRemovals, item);
        if (itemIdx != Constants.NO_DATA) {
            mMaker.onCancel(AnimType.REMOVE, item.itemView);
            mPendingMoves.remove(itemIdx);
            dispatchRemoveFinished(item);
        }

        // Add
        itemIdx = AnimInfo.find(mPendingAdditions, item);
        if (itemIdx != Constants.NO_DATA) {
            mMaker.onCancel(AnimType.ADD, item.itemView);
            mPendingAdditions.remove(itemIdx);
            dispatchAddFinished(item);
        }

        ////// Terminate processing animation
        for (int i = mProcessingChanges.size() - 1; i > Constants.NO_DATA; --i) {
            ArrayList<ChangeInfo> changes = mProcessingChanges.get(i);
            endChangeAnimation(changes, item);
            if (changes.isEmpty())
                mProcessingChanges.remove(changes);
        }
        for (int i = mProcessingMoves.size() - 1; i > Constants.NO_DATA; --i) {
            ArrayList<MoveInfo> moves = mProcessingMoves.get(i);
            for (int j = moves.size() - 1; j > Constants.NO_DATA; --j) {
                MoveInfo moveInfo = moves.get(j);
                if (moveInfo.mHolder == item) {
                    ViewCompat.setTranslationY(item.itemView, 0);
                    ViewCompat.setTranslationX(item.itemView, 0);
                    dispatchMoveFinished(item);
                    moves.remove(j);
                    if (moves.isEmpty())
                        mProcessingMoves.remove(moves);
                    break;
                }
            }
        }
        for (int i = mProcessingAdditions.size() - 1; i >= 0; i--) {
            ArrayList<ViewHolder> additions = mProcessingAdditions.get(i);
            if (additions.remove(item)) {
                mMaker.onCancel(AnimType.ADD, item.itemView);
                dispatchAddFinished(item);
                if (additions.isEmpty())
                    mProcessingAdditions.remove(additions);
            }
        }

        //////
        if (!isRunning())
            dispatchAnimationsFinished();
    }

    @Override
    public void endAnimations() {
        Logs.add(Logs.Type.V, null);

        // Remove pending animations
        for (int i = mPendingMoves.size() - 1; i > Constants.NO_DATA; --i) {
            ViewHolder holder = mPendingMoves.get(i).mHolder;
            ViewCompat.setTranslationY(holder.itemView, 0);
            ViewCompat.setTranslationX(holder.itemView, 0);
            dispatchMoveFinished(holder);
            mPendingMoves.remove(i);
        }
        for (int i = mPendingRemovals.size() - 1; i > Constants.NO_DATA; --i) {
            ViewHolder holder = mPendingRemovals.get(i).mHolder;
            mMaker.onCancel(AnimType.REMOVE, holder.itemView);
            dispatchRemoveFinished(holder);
            mPendingRemovals.remove(i);
        }
        for (int i = mPendingAdditions.size() - 1; i > Constants.NO_DATA; --i) {
            ViewHolder holder = mPendingAdditions.get(i).mHolder;
            mMaker.onCancel(AnimType.ADD, holder.itemView);
            dispatchAddFinished(holder);
            mPendingAdditions.remove(i);
        }
        for (int i = mPendingChanges.size() - 1; i > Constants.NO_DATA; --i)
            endChangeAnimationIfNeeded(mPendingChanges.get(i));

        mPendingMoves.clear();
        mPendingRemovals.clear();
        mPendingAdditions.clear();
        mPendingChanges.clear();

        // Remove processing animations
        for (int i = mProcessingMoves.size() - 1; i > Constants.NO_DATA; --i) {
            ArrayList<MoveInfo> moves = mProcessingMoves.get(i);
            for (int j = moves.size() - 1; j > Constants.NO_DATA; --j) {
                ViewHolder holder = moves.get(j).mHolder;
                ViewCompat.setTranslationY(holder.itemView, 0);
                ViewCompat.setTranslationX(holder.itemView, 0);
                dispatchMoveFinished(holder);
                moves.remove(j);
                if (moves.isEmpty())
                    mProcessingMoves.remove(moves);
            }
        }
        for (int i = mProcessingAdditions.size() - 1; i > Constants.NO_DATA; --i) {
            ArrayList<ViewHolder> additions = mProcessingAdditions.get(i);
            for (int j = additions.size() - 1; j > Constants.NO_DATA; --j) {
                mMaker.onCancel(AnimType.ADD, additions.get(j).itemView);
                dispatchAddFinished(additions.get(j));
                additions.remove(j);
                if (additions.isEmpty())
                    mProcessingAdditions.remove(additions);
            }
        }
        for (int i = mProcessingChanges.size() - 1; i > Constants.NO_DATA; --i) {
            ArrayList<ChangeInfo> changes = mProcessingChanges.get(i);
            for (int j = changes.size() - 1; j > Constants.NO_DATA; --j) {
                endChangeAnimationIfNeeded(changes.get(j));
                if (changes.isEmpty())
                    mProcessingChanges.remove(changes);
            }
        }
        mProcessingMoves.clear();
        mProcessingAdditions.clear();
        mProcessingChanges.clear();

        // Stop current animations (started)
        for (ViewHolder holder : mStartedRemovals)
            mMaker.onCancel(AnimType.REMOVE, holder.itemView);
        for (ViewHolder holder : mStartedMoves)
            ViewCompat.animate(holder.itemView).cancel();
        for (ViewHolder holder : mStartedChanges)
            mMaker.onCancel(AnimType.CHANGE, holder.itemView);
        for (ViewHolder holder : mStartedAdditions)
            mMaker.onCancel(AnimType.ADD, holder.itemView);

        mStartedRemovals.clear();
        mStartedMoves.clear();
        mStartedChanges.clear();
        mStartedAdditions.clear();

        dispatchAnimationsFinished();
    }

    @Override
    public boolean isRunning() {
        return ((!mPendingRemovals.isEmpty()) || (!mPendingMoves.isEmpty()) ||
                (!mPendingChanges.isEmpty()) || (!mPendingAdditions.isEmpty()) ||
                (!mProcessingMoves.isEmpty()) || (!mProcessingChanges.isEmpty()) || (!mProcessingAdditions.isEmpty()) ||
                (!mStartedRemovals.isEmpty()) || (!mStartedMoves.isEmpty()) ||
                (!mStartedChanges.isEmpty()) || (!mStartedAdditions.isEmpty()));
    }
}
