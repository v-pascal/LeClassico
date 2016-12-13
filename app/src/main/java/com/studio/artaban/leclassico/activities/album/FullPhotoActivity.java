package com.studio.artaban.leclassico.activities.album;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.LoggedActivity;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.tools.Tools;

/**
 * Created by pascal on 12/12/16.
 * Display photo in full screen activity
 */
public class FullPhotoActivity extends LoggedActivity {

    public static final String EXTRA_DATA_TITLE = "title";
    public static final String EXTRA_DATA_NAME = "name";
    // Extras data

    ////// LoggedActivity //////////////////////////////////////////////////////////////////////////
    @Override
    protected void onLoggedResume() {

    }

    ////// AppCompatActivity ///////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
        setContentView(R.layout.activity_full_photo);

        // Set tool & app bars
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getIntent().getStringExtra(EXTRA_DATA_TITLE));
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        ((RelativeLayout.LayoutParams)toolBar.getLayoutParams()).setMargins(0,
                Tools.getStatusBarHeight(getResources()), 0, 0);

        // Set transition name to display photo
        ((ImageView)findViewById(R.id.image_full)).setTransitionName(getIntent().getStringExtra(EXTRA_DATA_NAME));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logs.add(Logs.Type.V, "item: " + item);
        if (item.getItemId() == android.R.id.home) {

            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
