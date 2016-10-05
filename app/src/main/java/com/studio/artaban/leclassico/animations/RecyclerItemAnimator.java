package com.studio.artaban.leclassico.animations;

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
 * Item animator for recycler view
 */
public class RecyclerItemAnimator extends SimpleItemAnimator {

    private static final int DEFAULT_DURATION_ADD = 500;
    private static final int DEFAULT_DURATION_CHANGE = 500;
    private static final int DEFAULT_DURATION_REMOVE = 500;
    // Default durations (in millisecond)

    private Interpolator mInterpolator; // Animation interpolator
    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    private AnimatorSet mAnimatorRemove;
    private AnimatorSet mAnimatorMove;
    private AnimatorSet mAnimatorChange;
    private AnimatorSet mAnimatorAdd;
    private AnimatorSet mAnimatorAppear;
    // Item animations

    public RecyclerItemAnimator() {
        Logs.add(Logs.Type.V, null);

        setAddDuration(DEFAULT_DURATION_ADD);
        setChangeDuration(DEFAULT_DURATION_CHANGE);
        setRemoveDuration(DEFAULT_DURATION_REMOVE);

        mInterpolator = new LinearInterpolator();










    }

    private ArrayList<RemoveInfo> mPendingRemovals = new ArrayList<>();
    private ArrayList<AddInfo> mPendingAdditions = new ArrayList<>();
    private ArrayList<MoveInfo> mPendingMoves = new ArrayList<>();
    private ArrayList<ChangeInfo> mPendingChanges = new ArrayList<>();
    // Pending animation info

    private ArrayList<ArrayList<ViewHolder>> mAdditionsList = new ArrayList<>();
    private ArrayList<ArrayList<MoveInfo>> mMovesList = new ArrayList<>();
    private ArrayList<ArrayList<ChangeInfo>> mChangesList = new ArrayList<>();
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














    //////
    private void cancelAnimation(AnimInfo info) {




    }
    private AnimInfo prepareAnimation(AnimInfo info) {





        return info;
    }
    private void implementAnimation(AnimInfo info) {






    }












    //
    private static class AnimInfo {
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
    private static class AddInfo extends AnimInfo {
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
    private static class ChangeInfo extends MoveInfo {

        public ViewHolder mNewHolder;
        private ChangeInfo(ViewHolder oldHolder, ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
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
        mPendingAdditions.add((AddInfo)prepareAnimation(new AddInfo(holder)));
        return true;
    }

    @Override
    public boolean animateMove(ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        //Logs.add(Logs.Type.V, "holder: " + holder + ";fromX: " + fromX + ";fromY: " + fromY +
        //        ";toX: " + toX + ";toY: " + toY);
        mPendingMoves.add((MoveInfo)prepareAnimation(new MoveInfo(holder, fromX, fromY, toX, toY)));
        return true;
    }

    @Override
    public boolean animateChange(ViewHolder oldHolder, ViewHolder newHolder,
                                 int fromLeft, int fromTop, int toLeft, int toTop) {
        //Logs.add(Logs.Type.V, "oldHolder: " + oldHolder + ";newHolder: " + newHolder +
        //        ";fromLeft: " + fromLeft + ";fromTop: " + fromTop + ";toLeft: " + toLeft +
        //        ";toTop: " + toTop);
        mPendingChanges.add((ChangeInfo)prepareAnimation(new ChangeInfo(oldHolder, newHolder,
                fromLeft, fromTop, toLeft, toTop)));
        return true;
    }

    @Override
    public boolean animateAppearance (@NonNull ViewHolder holder, ItemHolderInfo preIHI,
                                      @NonNull ItemHolderInfo postIHI) {
        //Logs.add(Logs.Type.V, "holder: " + holder + ";preIHI: " + preIHI + ";postIHI: " + postIHI);










        return super.animateAppearance(holder, preIHI, postIHI);











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
            final ArrayList<MoveInfo> moves = new ArrayList<MoveInfo>();
            moves.addAll(mPendingMoves);
            mMovesList.add(moves);
            mPendingMoves.clear();
            Runnable mover = new Runnable() {
                @Override
                public void run() {
                    for (MoveInfo moveInfo : moves)
                        implementAnimation(moveInfo);
                    moves.clear();
                    mMovesList.remove(moves);
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
            mChangesList.add(changes);
            mPendingChanges.clear();
            Runnable changer = new Runnable() {
                @Override
                public void run() {
                    for (ChangeInfo change : changes)
                        implementAnimation(change);
                    changes.clear();
                    mChangesList.remove(changes);
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
            mAdditionsList.add(additions);
            mPendingAdditions.clear();
            Runnable adder = new Runnable() {
                public void run() {
                    for (ViewHolder holder : additions)
                        implementAnimation(new AddInfo(holder));
                    additions.clear();
                    mAdditionsList.remove(additions);
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
        Logs.add(Logs.Type.V, "item: " + item);

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
        for (int i = mChangesList.size() - 1; i > Constants.NO_DATA; --i) {
            ArrayList<ChangeInfo> changes = mChangesList.get(i);
            endChangeAnimation(changes, item);
            if (changes.isEmpty())
                mChangesList.remove(changes);
        }
        for (int i = mMovesList.size() - 1; i > Constants.NO_DATA; --i) {
            ArrayList<MoveInfo> moves = mMovesList.get(i);
            for (int j = moves.size() - 1; j > Constants.NO_DATA; --j) {
                MoveInfo moveInfo = moves.get(j);
                if (moveInfo.mHolder == item) {
                    cancelAnimation(moveInfo);
                    dispatchMoveFinished(item);
                    moves.remove(j);
                    if (moves.isEmpty())
                        mMovesList.remove(moves);
                    break;
                }
            }
        }
        for (int i = mAdditionsList.size() - 1; i >= 0; i--) {
            ArrayList<ViewHolder> additions = mAdditionsList.get(i);
            if (additions.remove(item)) {
                cancelAnimation(new AddInfo(item));
                dispatchAddFinished(item);
                if (additions.isEmpty())
                    mAdditionsList.remove(additions);
            }
        }

        //////
        if (!isRunning())
            dispatchAnimationsFinished();
    }

    @Override
    public void endAnimations() {
        Logs.add(Logs.Type.V, null);

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

        for (int i = mMovesList.size() - 1; i > Constants.NO_DATA; --i) {
            ArrayList<MoveInfo> moves = mMovesList.get(i);
            for (int j = moves.size() - 1; j > Constants.NO_DATA; --j) {
                cancelAnimation(moves.get(j));
                dispatchMoveFinished(moves.get(j).mHolder);
                moves.remove(j);
                if (moves.isEmpty())
                    mMovesList.remove(moves);
            }
        }
        for (int i = mAdditionsList.size() - 1; i > Constants.NO_DATA; --i) {
            ArrayList<ViewHolder> additions = mAdditionsList.get(i);
            for (int j = additions.size() - 1; j > Constants.NO_DATA; --j) {
                cancelAnimation(new AddInfo(additions.get(j)));
                dispatchAddFinished(additions.get(j));
                additions.remove(j);
                if (additions.isEmpty())
                    mAdditionsList.remove(additions);
            }
        }
        for (int i = mChangesList.size() - 1; i > Constants.NO_DATA; --i) {
            ArrayList<ChangeInfo> changes = mChangesList.get(i);
            for (int j = changes.size() - 1; j > Constants.NO_DATA; --j) {
                endChangeAnimationIfNeeded(changes.get(j));
                if (changes.isEmpty())
                    mChangesList.remove(changes);
            }
        }
        dispatchAnimationsFinished();
    }

    @Override
    public boolean isRunning() {
        return ((!mPendingAdditions.isEmpty()) || (!mPendingChanges.isEmpty()) ||
                (!mPendingMoves.isEmpty()) || (!mPendingRemovals.isEmpty()) ||
                (!mMovesList.isEmpty()) || (!mAdditionsList.isEmpty()) || (!mChangesList.isEmpty()));
    }
}
