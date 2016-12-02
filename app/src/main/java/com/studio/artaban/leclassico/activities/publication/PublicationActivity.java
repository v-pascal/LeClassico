package com.studio.artaban.leclassico.activities.publication;

import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.LoggedActivity;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 02/12/16.
 * Publication activity
 */
public class PublicationActivity extends LoggedActivity {

    private Uri mPubUri; // Publication URI (needed to update cursor URI)

    ////// LoggedActivity //////////////////////////////////////////////////////////////////////////
    @Override
    protected void onLoggedResume() {

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
}
