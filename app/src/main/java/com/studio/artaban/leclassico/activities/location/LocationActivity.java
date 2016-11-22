package com.studio.artaban.leclassico.activities.location;

import android.os.Bundle;

import com.studio.artaban.leclassico.activities.LoggedActivity;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 22/11/16.
 * Location activity
 */
public class LocationActivity extends LoggedActivity {

    ////// LoggedActivity //////////////////////////////////////////////////////////////////////////
    @Override
    protected void onLoggedResume() {

    }

    ////// AppCompatActivity ///////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);



        if (getIntent().getData() != null)
            mPseudoId = Integer.valueOf(getIntent().getData().getPathSegments().get(1));
        else {



        }



    }
}
