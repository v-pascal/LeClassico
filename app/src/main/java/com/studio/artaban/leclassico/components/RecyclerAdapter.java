package com.studio.artaban.leclassico.components;

import android.database.Cursor;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studio.artaban.leclassico.helpers.Logs;

import java.util.ArrayList;

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

    public DataView getDataSource() {
        return mDataSource;
    }

    ////// DataView ////////////////////////////////////////////////////////////////////////////////
    public static class DataView {

        private ArrayList<ArrayList<Object>> mData = new ArrayList<>(); // Data source
        private int mColumnKey; // Key column index (column index containing unique entries Id)

        public DataView(int columnKey) {
            Logs.add(Logs.Type.V, "columnKey: " + columnKey);
            mColumnKey = columnKey;
        }

        private void clear() { // Clear all data
            Logs.add(Logs.Type.V, null);
            for (ArrayList<Object> data : mData)
                data.clear();
        }

        //
        public void fill(Cursor cursor) { // Fill data according initial cursor
            Logs.add(Logs.Type.V, "cursor: " + cursor);
            clear();

            if (cursor.moveToFirst()) {
                do {
                    ArrayList<Object> record = new ArrayList<>();

                    for (int i = 0; i < cursor.getColumnCount(); ++i) {
                        switch (cursor.getType(i)) {
                            case Cursor.FIELD_TYPE_STRING: record.add(cursor.getString(i)); break;
                            case Cursor.FIELD_TYPE_INTEGER: record.add(cursor.getInt(i)); break;
                            case Cursor.FIELD_TYPE_FLOAT: record.add(cursor.getFloat(i)); break;
                            case Cursor.FIELD_TYPE_NULL: record.add(null); break;
                            case Cursor.FIELD_TYPE_BLOB:
                                throw new IllegalArgumentException("Un-managed field type");
                        }
                    }
                    mData.add(record);

                } while (cursor.moveToNext());
            }
            cursor.moveToFirst();
        }
        public void swap(RecyclerView view, Cursor cursor) {
            // Swap data with new cursor then notify recycler view accordingly

            Logs.add(Logs.Type.V, "view: " + view + ";cursor: " + cursor);
            if (cursor.moveToFirst()) {
                do {






                    //notifyItemChanged(position - 1);
                    //notifyItemInserted();
                    //notifyItemRemoved();

                    //notifyItemRangeChanged();
                    //notifyItemRangeInserted();
                    //notifyItemRangeRemoved();






                } while (cursor.moveToNext());
            }
            cursor.moveToFirst();
        }

        public boolean isNull(int rank, int column) {
            return (mData.get(rank).get(column) == null);
        }
        public int getCount() {
            return mData.size();
        }

        //////
        public String getString(int rank, int column) {
            return (String)mData.get(rank).get(column);
        }
        public int getInt(int rank, int column) {
            return (Integer)mData.get(rank).get(column);
        }
        public float getFloat(int rank, int column) {
            return (Float)mData.get(rank).get(column);
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

    //////
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mRootView; // Holder root view

        public ViewHolder(View view) {
            super(view);
            //Logs.add(Logs.Type.V, "view: " + view);
            mRootView = view;
        }
    }
}
