package com.studio.artaban.leclassico.data;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

import com.studio.artaban.leclassico.helpers.Logs;

import java.util.ArrayList;

/**
 * Created by pascal on 26/09/16.
 * Data class used as DB source of recycler view
 */
public class DataView {

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
    }
    public void swap(RecyclerView view, Cursor cursor) {
        // Swap data with new cursor then notify recycler view accordingly

        Logs.add(Logs.Type.V, "view: " + view + ";cursor: " + cursor);





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