package com.studio.artaban.leclassico.activities.album;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.LoggedActivity;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.Storage;
import com.studio.artaban.leclassico.tools.Tools;

import java.io.File;

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
        ImageView photo =(ImageView) findViewById(R.id.image_full);
        photo.setTransitionName(getIntent().getStringExtra(EXTRA_DATA_NAME));
        Bitmap bmp = BitmapFactory.decodeFile(Storage.get() + Storage.FOLDER_PHOTOS +
                File.separator + getIntent().getStringExtra(EXTRA_DATA_NAME));
        if (bmp != null)
            photo.setImageBitmap(bmp);
        else {
            Logs.add(Logs.Type.W, "Failed to load photo: " + getIntent().getStringExtra(EXTRA_DATA_NAME));
            photo.setImageDrawable(getDrawable(R.drawable.photos));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Logs.add(Logs.Type.V, "menu: " + menu);
        getMenuInflater().inflate(R.menu.menu_full_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logs.add(Logs.Type.V, "item: " + item);
        switch (item.getItemId()) {
            case android.R.id.home: { // Back to previous activity

                supportFinishAfterTransition();
                return true;
            }
            case R.id.menu_share: { // Share photo
                Logs.add(Logs.Type.I, "Share the photo");

                Uri photoUri = Uri.parse(Storage.get() + Storage.FOLDER_PHOTOS +
                        File.separator + getIntent().getStringExtra(EXTRA_DATA_NAME));

                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_STREAM, photoUri);
                share.setType(Uris.getMimeType(photoUri));
                startActivity(share);
                return true;
            }
            case R.id.menu_save: { // Save photo
                Logs.add(Logs.Type.I, "Save the photo");




                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
