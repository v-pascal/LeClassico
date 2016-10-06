package com.studio.artaban.leclassico.animations.items;

import android.animation.AnimatorSet;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pascal on 03/10/16.
 * Base animator class for recycler list items
 */
public abstract class BaseItemAnimator extends SimpleItemAnimator {

    private static final int DEFAULT_DURATION = 500; // Default animation duration (in millisecond)

    protected Interpolator mInterpolator; // Animation interpolator
    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    public BaseItemAnimator() {
        Logs.add(Logs.Type.V, null);

        setAddDuration(DEFAULT_DURATION);
        setChangeDuration(DEFAULT_DURATION);
        setRemoveDuration(DEFAULT_DURATION);

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

        cancelAnimation(changeInfo);
        dispatchChangeFinished(item, oldItem);
        return true;
    }

    ////// BaseItemAnimator ////////////////////////////////////////////////////////////////////////

    protected abstract void cancelAnimation(AnimInfo info);
    protected abstract boolean prepareAnimation(AnimInfo info);
    protected abstract void implementAnimation(AnimInfo info);

    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected enum AnimType { REMOVE, MOVE, CHANGE, ADD };
    protected AnimType getAnimType(AnimInfo info) {
        //Logs.add(Logs.Type.V, "info: " + info);
        if (info instanceof RemoveInfo)
            return AnimType.REMOVE;
        else if (info instanceof MoveInfo)
            return AnimType.MOVE;
        else if (info instanceof ChangeInfo)
            return AnimType.CHANGE;

        return AnimType.ADD;
    }

    protected ArrayList<AnimInfo> mStartedRemovals = new ArrayList<>();
    protected ArrayList<AnimInfo> mStartedMoves = new ArrayList<>();
    protected ArrayList<AnimInfo> mStartedChanges = new ArrayList<>();
    protected ArrayList<AnimInfo> mStartedAdditions = new ArrayList<>();
    // Started animation holder

    protected static class AnimInfo {
        public ViewHolder mHolder;
        public final AnimatorSet mAnimation = new AnimatorSet();
        public final AnimatorSet mNewAnimation = new AnimatorSet();

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
    protected static class RemoveInfo extends AnimInfo {
        private RemoveInfo(ViewHolder holder) {
            super(holder);
        }
    }
    protected static class AddInfo extends AnimInfo {
        private AddInfo(ViewHolder holder) {
            super(holder);
        }
    }
    protected static class MoveInfo extends AnimInfo {
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
    protected static class ChangeInfo extends MoveInfo {

        public ViewHolder mNewHolder;
        private ChangeInfo(ViewHolder oldHolder, ViewHolder newHolder,
                           int fromX, int fromY, int toX, int toY) {
            super(oldHolder, fromX, fromY, toX, toY);
            mNewHolder = newHolder;
        }
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
        prepareAnimation(addInfo);
        mPendingAdditions.add(addInfo);
        return true;
    }
    @Override
    public boolean animateMove(ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        //Logs.add(Logs.Type.V, "holder: " + holder + ";fromX: " + fromX + ";fromY: " + fromY +
        //        ";toX: " + toX + ";toY: " + toY);
        MoveInfo moveInfo = new MoveInfo(holder, fromX, fromY, toX, toY);
        mPendingMoves.add(moveInfo);
        return prepareAnimation(moveInfo);
    }
    @Override
    public boolean animateChange(ViewHolder oldHolder, ViewHolder newHolder,
                                 int fromLeft, int fromTop, int toLeft, int toTop) {
        //Logs.add(Logs.Type.V, "oldHolder: " + oldHolder + ";newHolder: " + newHolder +
        //        ";fromLeft: " + fromLeft + ";fromTop: " + fromTop + ";toLeft: " + toLeft +
        //        ";toTop: " + toTop);
        ChangeInfo chaneInfo = new ChangeInfo(oldHolder, newHolder, fromLeft, fromTop, toLeft, toTop);
        prepareAnimation(chaneInfo);
        mPendingChanges.add(chaneInfo);
        return true;
    }

    @Override
    public void runPendingAnimations() {
        //Logs.add(Logs.Type.V, null);

        boolean removalsPending = !mPendingRemovals.isEmpty();
        boolean movesPending = !mPendingMoves.isEmpty();
        boolean changesPending = !mPendingChanges.isEmpty();
        boolean additionsPending = !mPendingAdditions.isEmpty();
        if (!removalsPending && !movesPending && !additionsPending && !changesPending)
            return; // Nothing to do

        //// Remove
        if (removalsPending) {
            for (RemoveInfo removeInfo : mPendingRemovals)
                implementAnimation(removeInfo);
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
                        implementAnimation(moveInfo);
                    moves.clear();
                    mProcessingMoves.remove(moves);
                }
            };
            if (removalsPending) // Check if needed to wait previous remove stuff
                moves.get(0).mHolder.itemView.postDelayed(mover, getRemoveDuration());
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
                    for (ChangeInfo change : changes)
                        implementAnimation(change);
                    changes.clear();
                    mProcessingChanges.remove(changes);
                }
            };
            if (removalsPending)
                changes.get(0).mHolder.itemView.postDelayed(changer, getRemoveDuration());
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
                    for (ViewHolder holder : additions)
                        implementAnimation(new AddInfo(holder));
                    additions.clear();
                    mProcessingAdditions.remove(additions);
                }
            };
            if ((removalsPending) || (movesPending) || (changesPending)) { // Check to wait previous stuff
                long removeDuration = (removalsPending)? getRemoveDuration() : 0;
                long moveDuration = (movesPending)? getMoveDuration() : 0;
                long changeDuration = (changesPending)? getChangeDuration() : 0;
                long totalDelay = removeDuration + Math.max(moveDuration, changeDuration);

                additions.get(0).itemView.postDelayed(adder, totalDelay);

            } else
                adder.run();
        }
    }

    @Override
    public void endAnimation(ViewHolder item) {
        //Logs.add(Logs.Type.V, "item: " + item);

        ////// Terminate pending animation
        // Move
        for (int i = mPendingMoves.size() - 1; i > Constants.NO_DATA; --i) {
            if (mPendingMoves.get(i).mHolder == item) {
                cancelAnimation(mPendingMoves.get(i));
                dispatchMoveFinished(item);
                mPendingMoves.remove(i);
            }
        }

        // Change
        endChangeAnimation(mPendingChanges, item);

        // Remove
        int itemIdx = AnimInfo.find(mPendingRemovals, item);
        if (itemIdx != Constants.NO_DATA) {
            cancelAnimation(mPendingMoves.get(itemIdx));
            mPendingMoves.remove(itemIdx);
            dispatchRemoveFinished(item);
        }

        // Add
        itemIdx = AnimInfo.find(mPendingAdditions, item);
        if (itemIdx != Constants.NO_DATA) {
            cancelAnimation(mPendingAdditions.get(itemIdx));
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
                    cancelAnimation(moveInfo);
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
                cancelAnimation(new AddInfo(item));
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
            cancelAnimation(mPendingMoves.get(i));
            dispatchMoveFinished(mPendingMoves.get(i).mHolder);
            mPendingMoves.remove(i);
        }
        for (int i = mPendingRemovals.size() - 1; i > Constants.NO_DATA; --i) {
            dispatchRemoveFinished(mPendingRemovals.get(i).mHolder);
            mPendingRemovals.remove(i);
        }
        for (int i = mPendingAdditions.size() - 1; i > Constants.NO_DATA; --i) {
            cancelAnimation(mPendingAdditions.get(i));
            dispatchAddFinished(mPendingAdditions.get(i).mHolder);
            mPendingAdditions.remove(i);
        }
        for (int i = mPendingChanges.size() - 1; i > Constants.NO_DATA; --i)
            endChangeAnimationIfNeeded(mPendingChanges.get(i));
        mPendingChanges.clear();

        // Remove processing animations
        for (int i = mProcessingMoves.size() - 1; i > Constants.NO_DATA; --i) {
            ArrayList<MoveInfo> moves = mProcessingMoves.get(i);
            for (int j = moves.size() - 1; j > Constants.NO_DATA; --j) {
                cancelAnimation(moves.get(j));
                dispatchMoveFinished(moves.get(j).mHolder);
                moves.remove(j);
                if (moves.isEmpty())
                    mProcessingMoves.remove(moves);
            }
        }
        for (int i = mProcessingAdditions.size() - 1; i > Constants.NO_DATA; --i) {
            ArrayList<ViewHolder> additions = mProcessingAdditions.get(i);
            for (int j = additions.size() - 1; j > Constants.NO_DATA; --j) {
                cancelAnimation(new AddInfo(additions.get(j)));
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

        // Stop current animations (started)
        for (AnimInfo remove : mStartedRemovals)
            cancelAnimation(remove);
        for (AnimInfo move : mStartedMoves)
            cancelAnimation(move);
        for (AnimInfo change : mStartedChanges)
            cancelAnimation(change);
        for (AnimInfo add : mStartedAdditions)
            cancelAnimation(add);

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
