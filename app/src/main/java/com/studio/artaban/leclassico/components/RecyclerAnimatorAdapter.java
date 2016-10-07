package com.studio.artaban.leclassico.components;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 06/10/16.
 * Recycler adapter class (with animation)
 */
public class RecyclerAnimatorAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private RecyclerAdapter mInternalAdapter; // Internal recycler adapter
    private int mLastPosition = Constants.NO_DATA; // Last item position displayed with animation

    public RecyclerAnimatorAdapter(RecyclerAdapter adapter, RecyclerAnimationMaker maker) {
        Logs.add(Logs.Type.V, "adapter: " + adapter + ";maker: " + maker);
        mInternalAdapter = adapter;
        mMaker = maker;
    }

    ////// RecyclerAnimMaker ///////////////////////////////////////////////////////////////////////

    public interface RecyclerAnimationMaker {

        void onAppearance(View item);
        void onCancel(View item);
    }
    private RecyclerAnimationMaker mMaker; // Item animator maker

    ////// RecyclerView.Adapter ////////////////////////////////////////////////////////////////////
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mInternalAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
        //Logs.add(Logs.Type.V, "holder: " + holder + ";position: " + position);
        if (mMaker == null)
            throw new InternalError("No animation maker defined");

        mInternalAdapter.onBindViewHolder(holder, position);
        if (holder.getAdapterPosition() > mLastPosition) {

            mMaker.onAppearance(holder.itemView);
            mLastPosition = holder.getAdapterPosition();

        } else
            mMaker.onCancel(holder.itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return mInternalAdapter.getItemViewType(position);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
        mInternalAdapter.setHasStableIds(hasStableIds);
    }

    @Override
    public long getItemId(int position) {
        return mInternalAdapter.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return mInternalAdapter.getItemCount();
    }

    @Override
    public void onViewRecycled(RecyclerAdapter.ViewHolder holder) {
        super.onViewRecycled(holder);
        mInternalAdapter.onViewRecycled(holder);
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        mInternalAdapter.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
        mInternalAdapter.unregisterAdapterDataObserver(observer);
    }
}
