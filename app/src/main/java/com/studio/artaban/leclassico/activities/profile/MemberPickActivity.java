package com.studio.artaban.leclassico.activities.profile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 06/04/17.
 * Activity to pick a member
 */
public class MemberPickActivity extends AppCompatActivity {

    ////// AppCompatActivity ///////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
        setContentView(R.layout.activity_pick_member);






    }
}
