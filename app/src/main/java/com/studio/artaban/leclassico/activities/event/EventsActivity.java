package com.studio.artaban.leclassico.activities.event;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.LoggedActivity;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 05/02/17.
 * Event activity:
 * _ Display event info
 */
public class EventsActivity extends LoggedActivity {

    // Extra data keys (see 'LoggedActivity' & 'Login' extra data keys)

    ////// LoggedActivity //////////////////////////////////////////////////////////////////////////
    @Override
    protected void onLoggedResume() {

    }

    //////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        if ((!cursor.moveToFirst()) || (onNotifyLoadFinished(id, cursor)))
            return;




    }

    @Override
    public void onLoaderReset() {

    }

    ////// AppCompatActivity ///////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
        setContentView(R.layout.activity_event);

        // Set toolbar




        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        // Get event ID
        mId = getIntent().getIntExtra(EXTRA_DATA_ID, Constants.NO_DATA);
        Logs.add(Logs.Type.I, "Event #" + mId);






    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logs.add(Logs.Type.V, "item: " + item);



        //if (onNotifyItemSelected(item))
        //    return true; // Display notifications



        switch (item.getItemId()) {
            case android.R.id.home: { // Back to previous activity

                supportFinishAfterTransition();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
