package com.studio.artaban.leclassico.components;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by pascal on 04/11/16.
 * Recycler request class is a recycler view that can notify user has reached the end of the list
 * To request old entries from DB
 */
public class RecyclerRequest extends RecyclerView {

    public interface OnRequestListener { ///////////////////////////////////////////////////////////
        boolean onRequestOld();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private OnRequestListener mListener; // Parent request listener
    private boolean mLoading; // Old data requested flag

    public void setOnRequestListener(OnRequestListener listener) {
        mListener = listener;
    }
    public void enableRequestListener() { // Allow process to request old data
        mLoading = false;
    }

    //////
    public RecyclerRequest(Context context, AttributeSet attrs) {
        super(context, attrs);
        addOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //Logs.add(Logs.Type.V, "recyclerView: " + recyclerView + ";dx: " + dx + ";dy: " + dy);

                if (mListener == null)
                    throw new IllegalStateException("Missing request listener");
                if ((mLoading) || (dy < 0))
                    return;

                int visibleCount = (getLayoutManager() instanceof LinearLayoutManager) ?
                        ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition() :
                        ((GridLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();

                if ((getLayoutManager().getChildCount() + visibleCount) >= getLayoutManager().getItemCount())
                    mLoading = mListener.onRequestOld();
            }
        });
    }
}
