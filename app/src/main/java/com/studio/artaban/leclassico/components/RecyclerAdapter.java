package com.studio.artaban.leclassico.components;

import android.database.Cursor;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studio.artaban.leclassico.data.DataView;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 26/09/16.
 * Recycler adapter class (with DB cursor a data source)
 */
public abstract class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    @LayoutRes private final int mItemLayout; // Holder view layout Id
    protected final DataView mDataSource; // Data source

    public RecyclerAdapter(@LayoutRes int layout, int key) {
        Logs.add(Logs.Type.V, "layout: " + layout);

        mItemLayout = layout;
        mDataSource = new DataView(key);
    }

    //
    public void setSource(Cursor cursor) {
        mDataSource.fill(cursor);
    }
    public void swapSource(RecyclerView view, Cursor cursor) {
        mDataSource.swap(view, cursor);
    }

    public DataView getDataSource() {
        return mDataSource;
    }

    //////
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mRootView; // Holder root view

        public ViewHolder(View view) {
            super(view);
            //Logs.add(Logs.Type.V, "view: " + view);
            mRootView = view;
        }
    }

    ////// RecyclerView.Adapter ////////////////////////////////////////////////////////////////////
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Logs.add(Logs.Type.V, "parent: " + parent + ";viewType: " + viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(mItemLayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mDataSource.getCount();
    }
}
