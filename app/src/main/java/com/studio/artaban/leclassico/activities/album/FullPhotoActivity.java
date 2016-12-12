package com.studio.artaban.leclassico.activities.album;

import android.os.Bundle;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.LoggedActivity;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 12/12/16.
 * Display photo in full screen activity
 */
public class FullPhotoActivity extends LoggedActivity {

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





    }
}
