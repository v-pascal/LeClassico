package com.studio.artaban.leclassico.animations.items;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pascal on 03/10/16.
 * Base animator class for recycler list items
 */
public abstract class BaseItemAnimator extends SimpleItemAnimator {

    private static final int DEFAULT_DURATION_ADD = 500;
    private static final int DEFAULT_DURATION_CHANGE = 500;
    private static final int DEFAULT_DURATION_REMOVE = 500;
    // Default durations (in millisecond)

    private Interpolator mInterpolator; // Animation interpolator

    public BaseItemAnimator() {
        Logs.add(Logs.Type.V, null);

        setAddDuration(DEFAULT_DURATION_ADD);
        setChangeDuration(DEFAULT_DURATION_CHANGE);
        setRemoveDuration(DEFAULT_DURATION_REMOVE);

        mInterpolator = new OvershootInterpolator(1f);
    }
    public BaseItemAnimator(int durationAdd, int durationChange, int durationRemove, Interpolator interpolator) {

        Logs.add(Logs.Type.V, "durationAdd: " + durationAdd + ";durationChange: " + durationChange +
                ";durationRemove: " + durationRemove + ";interpolator: " + interpolator);

        setAddDuration((durationAdd != Constants.NO_DATA) ? durationAdd : DEFAULT_DURATION_ADD);
        setAddDuration((durationChange != Constants.NO_DATA) ? durationChange : DEFAULT_DURATION_CHANGE);
        setAddDuration((durationRemove != Constants.NO_DATA) ? durationRemove : DEFAULT_DURATION_REMOVE);

        mInterpolator = (interpolator != null)? interpolator:new OvershootInterpolator(1f);
    }

    private ArrayList<RemoveInfo> mPendingRemovals = new ArrayList<>();
    private ArrayList<AddInfo> mPendingAdditions = new ArrayList<>();
    private ArrayList<MoveInfo> mPendingMoves = new ArrayList<>();
    private ArrayList<ChangeInfo> mPendingChanges = new ArrayList<>();
    // Pending animation info

    private ArrayList<ArrayList<RecyclerView.ViewHolder>> mAdditionsList = new ArrayList<>();
    private ArrayList<ArrayList<MoveInfo>> mMovesList = new ArrayList<>();
    private ArrayList<ArrayList<ChangeInfo>> mChangesList = new ArrayList<>();
    // Processing animation info

    protected ArrayList<RecyclerView.ViewHolder> mAddAnimations = new ArrayList<>();
    protected ArrayList<RecyclerView.ViewHolder> mMoveAnimations = new ArrayList<>();
    protected ArrayList<RecyclerView.ViewHolder> mRemoveAnimations = new ArrayList<>();
    protected ArrayList<RecyclerView.ViewHolder> mChangeAnimations = new ArrayList<>();
    // Processing animation holder

    private void endChangeAnimation(List<ChangeInfo> changeList, RecyclerView.ViewHolder item) {
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
    private boolean endChangeAnimationIfNeeded(ChangeInfo changeInfo, RecyclerView.ViewHolder item) {
        //Logs.add(Logs.Type.V, "changeInfo: " + changeInfo + ";item: " + item);
        boolean oldItem = false;

        if (changeInfo.mNewHolder == item) changeInfo.mNewHolder = null;
        else if (changeInfo.mHolder == item) {
            changeInfo.mHolder = null;
            oldItem = true;
        } else
            return false;





        /*
        ViewCompat.setAlpha(item.itemView, 1);
        ViewCompat.setTranslationX(item.itemView, 0);
        ViewCompat.setTranslationY(item.itemView, 0);
        */




        cancelAnimation(changeInfo);
        dispatchChangeFinished(item, oldItem);
        return true;
    }

    //////
    protected abstract void cancelAnimation(AnimInfo info);
    protected abstract AnimInfo prepareAnimation(AnimInfo info);
    protected abstract void implementAnimation(AnimInfo info);

    //
    protected static class AnimInfo {
        public RecyclerView.ViewHolder mHolder;
        public static <T> int find(ArrayList<T> animList, RecyclerView.ViewHolder holder) {
            for (int i = 0; i < animList.size(); ++i)
                if (((AnimInfo)animList.get(i)).mHolder == holder)
                    return i;

            return Constants.NO_DATA;
        }
        private AnimInfo(RecyclerView.ViewHolder holder) {
            mHolder = holder;
        }
    }
    protected static class RemoveInfo extends AnimInfo {
        private RemoveInfo(RecyclerView.ViewHolder holder) {
            super(holder);
        }
    }
    protected static class AddInfo extends AnimInfo {
        private AddInfo(RecyclerView.ViewHolder holder) {
            super(holder);
        }
    }
    protected static class MoveInfo extends AnimInfo {
        public int mFromX, mFromY, mToX, mToY;

        private MoveInfo(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
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

        public RecyclerView.ViewHolder mNewHolder;
        private ChangeInfo(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder,
                           int fromX, int fromY, int toX, int toY) {
            super(oldHolder, fromX, fromY, toX, toY);
            mNewHolder = newHolder;
        }
    }

    ////// SimpleItemAnimator //////////////////////////////////////////////////////////////////////
    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        //Logs.add(Logs.Type.V, "holder: " + holder);

        endAnimation(holder);
        mPendingRemovals.add(new RemoveInfo(holder));
        return true;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        //Logs.add(Logs.Type.V, "holder: " + holder);

        endAnimation(holder);
        mPendingAdditions.add((AddInfo)prepareAnimation(new AddInfo(holder)));
        return true;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        //Logs.add(Logs.Type.V, "holder: " + holder + ";fromX: " + fromX + ";fromY: " + fromY +
        //        ";toX: " + toX + ";toY: " + toY);






        /*
        fromX += ViewCompat.getTranslationX(holder.itemView);
        fromY += ViewCompat.getTranslationY(holder.itemView);
        endAnimation(holder);
        int deltaX = toX - fromX;
        int deltaY = toY - fromY;
        if (deltaX == 0 && deltaY == 0) {
            dispatchMoveFinished(holder);
            return false;
        }
        if (deltaX != 0) ViewCompat.setTranslationX(holder.itemView, -deltaX);
        if (deltaY != 0) ViewCompat.setTranslationY(holder.itemView, -deltaY);
        return new MoveInfo(holder, fromX, fromY, toX, toY);
        */





        mPendingMoves.add((MoveInfo)prepareAnimation(new MoveInfo(holder, fromX, fromY, toX, toY)));
        return true;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder,
                                 int fromLeft, int fromTop, int toLeft, int toTop) {
        //Logs.add(Logs.Type.V, "oldHolder: " + oldHolder + ";newHolder: " + newHolder +
        //        ";fromLeft: " + fromLeft + ";fromTop: " + fromTop + ";toLeft: " + toLeft +
        //        ";toTop: " + toTop);




        /*
        final float prevTranslationX = ViewCompat.getTranslationX(oldHolder.itemView);
        final float prevTranslationY = ViewCompat.getTranslationY(oldHolder.itemView);
        final float prevAlpha = ViewCompat.getAlpha(oldHolder.itemView);
        endAnimation(oldHolder);
        int deltaX = (int) (toX - fromX - prevTranslationX);
        int deltaY = (int) (toY - fromY - prevTranslationY);
        // recover prev translation state after ending animation
        ViewCompat.setTranslationX(oldHolder.itemView, prevTranslationX);
        ViewCompat.setTranslationY(oldHolder.itemView, prevTranslationY);
        ViewCompat.setAlpha(oldHolder.itemView, prevAlpha);
        if (newHolder != null && newHolder.itemView != null) {
            // carry over translation values
            endAnimation(newHolder);
            ViewCompat.setTranslationX(newHolder.itemView, -deltaX);
            ViewCompat.setTranslationY(newHolder.itemView, -deltaY);
            ViewCompat.setAlpha(newHolder.itemView, 0);
        }
        */




        mPendingChanges.add((ChangeInfo)prepareAnimation(new ChangeInfo(oldHolder, newHolder,
                fromLeft, fromTop, toLeft, toTop)));
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
                ViewCompat.postOnAnimationDelayed(moves.get(0).mHolder.itemView, mover, getRemoveDuration());
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
                ViewCompat.postOnAnimationDelayed(changes.get(0).mHolder.itemView, changer, getRemoveDuration());
            else
                changer.run();
        }

        ////// Add
        if (additionsPending) {
            final ArrayList<RecyclerView.ViewHolder> additions = new ArrayList<>();
            for (AddInfo addInfo : mPendingAdditions)
                additions.add(addInfo.mHolder);
            mAdditionsList.add(additions);
            mPendingAdditions.clear();
            Runnable adder = new Runnable() {
                public void run() {
                    for (RecyclerView.ViewHolder holder : additions)
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

                ViewCompat.postOnAnimationDelayed(additions.get(0).itemView, adder, totalDelay);

            } else
                adder.run();
        }
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {

        Logs.add(Logs.Type.V, "item: " + item);
        ViewCompat.animate(item.itemView).cancel();

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
            ArrayList<RecyclerView.ViewHolder> additions = mAdditionsList.get(i);
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





        /*
        int count = mPendingMoves.size();
        for (int i = count - 1; i >= 0; i--) {
            MoveInfo item = mPendingMoves.get(i);
            View view = item.holder.itemView;
            ViewCompat.setTranslationY(view, 0);
            ViewCompat.setTranslationX(view, 0);
            dispatchMoveFinished(item.holder);
            mPendingMoves.remove(i);
        }


        count = mPendingRemovals.size();
        for (int i = count - 1; i >= 0; i--) {
            RecyclerView.ViewHolder item = mPendingRemovals.get(i);
            dispatchRemoveFinished(item);
            mPendingRemovals.remove(i);
        }



        count = mPendingAdditions.size();
        for (int i = count - 1; i >= 0; i--) {
            RecyclerView.ViewHolder item = mPendingAdditions.get(i);
            View view = item.itemView;
            ViewCompat.setAlpha(view, 1);
            dispatchAddFinished(item);
            mPendingAdditions.remove(i);
        }


        count = mPendingChanges.size();
        for (int i = count - 1; i >= 0; i--) {
            endChangeAnimationIfNecessary(mPendingChanges.get(i));
        }
        mPendingChanges.clear();




        //if (!isRunning()) {
        //    return;
        //}



        int listCount = mMovesList.size();
        for (int i = listCount - 1; i >= 0; i--) {
            ArrayList<MoveInfo> moves = mMovesList.get(i);
            count = moves.size();
            for (int j = count - 1; j >= 0; j--) {
                MoveInfo moveInfo = moves.get(j);
                RecyclerView.ViewHolder item = moveInfo.holder;
                View view = item.itemView;
                ViewCompat.setTranslationY(view, 0);
                ViewCompat.setTranslationX(view, 0);
                dispatchMoveFinished(moveInfo.holder);
                moves.remove(j);
                if (moves.isEmpty()) {
                    mMovesList.remove(moves);
                }
            }
        }
        listCount = mAdditionsList.size();
        for (int i = listCount - 1; i >= 0; i--) {
            ArrayList<RecyclerView.ViewHolder> additions = mAdditionsList.get(i);
            count = additions.size();
            for (int j = count - 1; j >= 0; j--) {
                RecyclerView.ViewHolder item = additions.get(j);
                View view = item.itemView;
                ViewCompat.setAlpha(view, 1);
                dispatchAddFinished(item);
                additions.remove(j);
                if (additions.isEmpty()) {
                    mAdditionsList.remove(additions);
                }
            }
        }
        listCount = mChangesList.size();
        for (int i = listCount - 1; i >= 0; i--) {
            ArrayList<ChangeInfo> changes = mChangesList.get(i);
            count = changes.size();
            for (int j = count - 1; j >= 0; j--) {
                endChangeAnimationIfNecessary(changes.get(j));
                if (changes.isEmpty()) {
                    mChangesList.remove(changes);
                }
            }
        }
        */







        for (RecyclerView.ViewHolder holder : mRemoveAnimations)
            ViewCompat.animate(holder.itemView).cancel();
        for (RecyclerView.ViewHolder holder : mMoveAnimations)
            ViewCompat.animate(holder.itemView).cancel();
        for (RecyclerView.ViewHolder holder : mAddAnimations)
            ViewCompat.animate(holder.itemView).cancel();
        for (RecyclerView.ViewHolder holder : mChangeAnimations)
            ViewCompat.animate(holder.itemView).cancel();

        dispatchAnimationsFinished();
    }

    @Override
    public boolean isRunning() {
        return ((!mPendingAdditions.isEmpty()) || (!mPendingChanges.isEmpty()) ||
                (!mPendingMoves.isEmpty()) || (!mPendingRemovals.isEmpty()) ||
                (!mMoveAnimations.isEmpty()) || (!mRemoveAnimations.isEmpty()) ||
                (!mAddAnimations.isEmpty()) || (!mChangeAnimations.isEmpty()) ||
                (!mMovesList.isEmpty()) || (!mAdditionsList.isEmpty()) || (!mChangesList.isEmpty()));
    }
}
