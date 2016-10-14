package com.studio.artaban.leclassico.components;

import android.database.Cursor;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by pascal on 26/09/16.
 * Recycler adapter class with DB cursor a data source
 */
public abstract class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    @LayoutRes private final int mItemLayout; // Holder view layout Id
    protected final DataView mDataSource; // Data source

    ////// AppearanceAnimatorMaker /////////////////////////////////////////////////////////////////

    private int mLastPosition = Constants.NO_DATA; // Last display item position
    private AppearanceAnimatorMaker mMaker; // Appearance animator maker

    public interface AppearanceAnimatorMaker {
        void onAnimate(View item);
        void onCancel(View item);
    }
    public void animateAppearance(RecyclerAdapter.ViewHolder holder, AppearanceAnimatorMaker maker) {
    // Animate item appearance

        Logs.add(Logs.Type.V, "holder: " + holder);
        mMaker = maker;
        if (holder.getAdapterPosition() > mLastPosition) {

            mLastPosition = holder.getAdapterPosition();
            if (!(Boolean)holder.itemView.getTag())
                mMaker.onAnimate(holder.itemView);
        }
        if ((Boolean)holder.itemView.getTag())
            holder.itemView.setTag(Boolean.FALSE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public RecyclerAdapter(@LayoutRes int layout, int key) {
        Logs.add(Logs.Type.V, "layout: " + layout);

        mItemLayout = layout;
        mDataSource = new DataView(key);
    }

    public DataView getDataSource() {
        return mDataSource;
    }

    ////// SwapResult //////////////////////////////////////////////////////////////////////////////
    public static class SwapResult {

        public boolean firstChanged = false; // First item change flag
        public int countChanged = 0; // Item change only count (NO_DATA when item added or removed)
        public final ArrayList<Integer> columnChanged = new ArrayList<>(); // Item column change indexes
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
        private static void fill(Cursor cursor, ArrayList<ArrayList<Object>> data) {
        // Fill data array according DB cursor

            Logs.add(Logs.Type.V, "cursor: " + cursor + ";data: " + data);
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
                    data.add(record);

                } while (cursor.moveToNext());
            }
            cursor.moveToFirst();
        }
        private static boolean equal(Object a, Object b) { // Compare data values (with same type)
            //Logs.add(Logs.Type.V, "a: " + a + ";b: " + b);
            try {
                if (((a instanceof Integer) && (((Integer) a).compareTo((Integer)b)) == 0) ||
                        ((a instanceof String) && (((String) a).compareTo((String)b)) == 0) ||
                        ((a instanceof Float) && (((Float) a).compareTo((Float)b)) == 0) ||
                        ((a == null) && (b == null)))
                    return true;

            } catch (ClassCastException e) {
                Logs.add(Logs.Type.F, "Failed to compare data with different types: " + e.getMessage());
            }
            return false;
        }
        private static int find(ArrayList<ArrayList<Object>> data, int columnValue, Object value) {
        // Find key into data array and return its index (NO_DATA is returned if not found)

            //Logs.add(Logs.Type.V, "data: " + data + ";columnValue: " + columnValue + ";value: " + value);
            for (int i = 0; i < data.size(); ++i) {
                if (equal(data.get(i).get(columnValue), value))
                    return i;
            }
            return Constants.NO_DATA;
        }

        //
        public void fill(Cursor cursor) { // Fill data according initial cursor
            Logs.add(Logs.Type.V, "cursor: " + cursor);

            clear();
            fill(cursor, mData);
        }
        public SwapResult swap(RecyclerAdapter adapter, Cursor cursor, boolean below) {
        // Swap data with new cursor then notify recycler adapter accordingly

            Logs.add(Logs.Type.V, "adapter: " + adapter + ";cursor: " + cursor + ";below: " + below);
            ArrayList<ArrayList<Object>> newData = new ArrayList<>();
            fill(cursor, newData);

            SwapResult swapResult = new SwapResult();

            ////// Remove entries (if needed)
            ArrayList<Integer> toDo = new ArrayList<>();
            for (int i = 0; i < mData.size(); ++i) {
                if (find(newData, mColumnKey, mData.get(i).get(mColumnKey)) == Constants.NO_DATA) {
                    swapResult.countChanged = Constants.NO_DATA;
                    if (i == 0)
                        swapResult.firstChanged = true;

                    toDo.add(i);
                }
            }
            for (int i = 0; i < toDo.size(); ++i) {
                int j = i + 1;

                // Check to remove range
                for (int k = 0; j < toDo.size(); ++j, ++k) {
                    if (toDo.get(i + k) == (toDo.get(j) - 1))
                        mData.remove((int)toDo.get(i));
                    else
                        break;
                }
                int removedLast = toDo.get(i);
                mData.remove(removedLast);
                if (j != (i + 1)) { // Range
                    adapter.notifyItemRangeRemoved(toDo.get(i), toDo.get(j - 1));
                    i = j - 1;

                } else // Single
                    adapter.notifyItemRemoved(toDo.get(i));

                // Check to update item below the last entry removed
                if ((below) && (mData.size() > removedLast))
                    adapter.notifyItemChanged(removedLast);
            }

            ////// Add entries (if needed)
            toDo.clear();
            for (int i = 0; i < newData.size(); ++i) {
                if (find(mData, mColumnKey, newData.get(i).get(mColumnKey)) == Constants.NO_DATA) {
                    swapResult.countChanged = Constants.NO_DATA;
                    if (i == 0)
                        swapResult.firstChanged = true;

                    toDo.add(i);
                }
            }
            for (int i = 0; i < toDo.size(); ++i) {

                // Check to insert range
                int count = 1;
                for (int j = i + 1; j < toDo.size(); ++j) {
                    if (toDo.get(i + count - 1) == (toDo.get(j) - 1))
                        ++count;
                    else
                        break;
                }
                for (int j = 0; j < count; ++j)
                    mData.add(toDo.get(i + j), newData.get(toDo.get(i + j)));
                if (count > 1) // Range
                    adapter.notifyItemRangeInserted(toDo.get(i), count);
                else // Single
                    adapter.notifyItemInserted(toDo.get(i));

                // Check to update item below the last entry inserted
                int insertedLast = toDo.get(i) + count;
                if ((below) && (mData.size() > insertedLast))
                    adapter.notifyItemChanged(insertedLast);

                i = i + count - 1;
            }

            ////// Move entries (if needed)
            for (int i = 0; i < mData.size(); ++i) {
                if (!equal(mData.get(i).get(mColumnKey), newData.get(i).get(mColumnKey))) {
                    // Entry not in same position

                    swapResult.countChanged = Constants.NO_DATA;
                    if (i == 0)
                        swapResult.firstChanged = true;

                    int newPosition = find(newData, mColumnKey, mData.get(i).get(mColumnKey));
                    Collections.swap(mData, i, newPosition);
                    adapter.notifyItemMoved(i, newPosition);

                    // Check to update item below moved entries
                    if (below) {
                        if (1 != (newPosition - i))
                            adapter.notifyItemChanged(i + 1);
                        if (mData.size() > ++newPosition)
                            adapter.notifyItemChanged(newPosition);
                    }
                }
            }

            ////// Update entries (if needed)
            toDo.clear();
            for (int i = 0; i < mData.size(); ++i) {
                for (int j = 0; j < cursor.getColumnCount(); ++j) {

                    // Check if entry has changed
                    if (!equal(mData.get(i).get(j), newData.get(i).get(j))) {
                        if ((swapResult.countChanged > Constants.NO_DATA) &&
                                (!swapResult.columnChanged.contains(j)))
                            swapResult.columnChanged.add(j);
                        if (i == 0)
                            swapResult.firstChanged = true;

                        mData.get(i).set(j, newData.get(i).get(j));
                        if (!toDo.contains(i)) {
                            if (swapResult.countChanged > Constants.NO_DATA)
                                ++swapResult.countChanged;

                            toDo.add(i);
                        }
                    }
                }
            }
            for (int i = 0; i < toDo.size(); ++i) {

                // Check to update range
                int count = 1;
                for (int j = i + 1; j < toDo.size(); ++j) {
                    if (toDo.get(i + count - 1) == (toDo.get(j) - 1))
                        ++count;
                    else
                        break;
                }
                if (count > 1) // Range
                    adapter.notifyItemRangeChanged(toDo.get(i), count);
                else // Single
                    adapter.notifyItemChanged(toDo.get(i));
            }
            return swapResult;
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
        view.setTag(Boolean.TRUE); // Needed to avoid first display animation
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mDataSource.getCount();
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        if (!(Boolean)holder.itemView.getTag())
            mMaker.onCancel(holder.itemView);
        super.onViewDetachedFromWindow(holder);
    }

    //////
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }
    }
}
