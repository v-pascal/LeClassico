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

    //
    public void fill(Cursor cursor) { // Fill data according initial cursor
        Logs.add(Logs.Type.V, "cursor: " + cursor);


        //cursor.getColumnCount();

        //cursor.getType(0);
        //Cursor.FIELD_TYPE_FLOAT
        //Cursor.FIELD_TYPE_INTEGER
        //Cursor.FIELD_TYPE_NULL
        //Cursor.FIELD_TYPE_STRING


    }
    public void swap(RecyclerView view, Cursor cursor) {
        // Swap data with new cursor then notify recycler view accordingly

        Logs.add(Logs.Type.V, "view: " + view + ";cursor: " + cursor);





    }

    public boolean isNull(int rank, int column) {




        return false;
    }
    public int getCount() {




        return 0;
    }

    //////
    public String getString(int rank, int column) {




        return null;
    }

    public int getInt(int rank, int column) {




        return 0;
    }

    public float getFloat(int rank, int column) {




        return 0;
    }
}