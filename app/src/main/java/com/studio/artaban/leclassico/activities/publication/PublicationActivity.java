package com.studio.artaban.leclassico.activities.publication;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.LoggedActivity;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 02/12/16.
 * Publication activity
 */
public class PublicationActivity extends LoggedActivity {

    // Extra data keys (see 'LoggedActivity' & 'Login' extra data keys)

    private Uri mPubUri; // Publication URI (needed to update cursor URI)

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
        setContentView(R.layout.activity_publication);

        // Set toolbar




        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        // Get publication ID & cursor URI
        mId = getIntent().getIntExtra(EXTRA_DATA_ID, Constants.NO_DATA);
        mPubUri = getIntent().getParcelableExtra(EXTRA_DATA_URI);






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
