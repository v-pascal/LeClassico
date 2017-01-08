package com.studio.artaban.leclassico.activities.location;

import android.database.Cursor;
import android.os.Bundle;

import com.studio.artaban.leclassico.activities.LoggedActivity;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 22/11/16.
 * Location activity
 */
public class LocationActivity extends LoggedActivity {

    // Extra data keys (see 'LoggedActivity' & 'Login' extra data keys)

    ////// LoggedActivity //////////////////////////////////////////////////////////////////////////
    @Override
    protected void onLoggedResume() {

    }

    //////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        onNotifyLoadFinished(id, cursor); // Refresh notification info
    }

    @Override
    public void onLoaderReset() {

    }

    ////// AppCompatActivity ///////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);





        // Get pseudo ID
        if (getIntent().getData() != null)
            mId = Integer.valueOf(getIntent().getData().getPathSegments().get(1));
        else
            mId = getIntent().getIntExtra(EXTRA_DATA_ID, Constants.NO_DATA);
        Logs.add(Logs.Type.I, "Pseudo #" + mId);




    }
}
