package com.studio.artaban.leclassico.activities.profile;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.LoggedActivity;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 27/12/16.
 * Profile activity
 */
public class ProfileActivity extends LoggedActivity {

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
        setContentView(R.layout.activity_profile);




        // Set action bar
        /*
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_activity_profile));
        actionBar.setDisplayHomeAsUpEnabled(true);
        */


        // Get pseudo ID
        if (getIntent().getData() != null)
            mId = Integer.valueOf(getIntent().getData().getPathSegments().get(1));
        else
            mId = getIntent().getIntExtra(EXTRA_DATA_ID, Constants.NO_DATA);
        Logs.add(Logs.Type.I, "Pseudo #" + mId);








    }
}
