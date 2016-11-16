package com.studio.artaban.leclassico.activities.settings;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.helpers.Logs;

import java.util.List;

/**
 * Created by pascal on 16/11/16.
 * User settings activity
 */
public class SettingsActivity extends PreferenceActivity {





    public static class PrefsNotifyFragment extends PreferenceFragment { ///////////////////////////

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);



            // Make sure default values are applied.  In a real app, you would
            // want this in a shared function that is used to retrieve the
            // SharedPreferences wherever they are needed.

            //PreferenceManager.setDefaultValues(getActivity(),
            //        R.xml.advanced_preferences, false);




            // Set title

            //((Toolbar) getToolbarParent(getActivity()).findViewById(R.id.toolbar))
            //        .setTitle(getResources().getString(R.string.title_activity_notify));
            //((SettingsActivity)getActivity()).toolbar.setTitle("Ollala");





            //
            addPreferencesFromResource(R.xml.settings_fragment_notify);
        }

        @Override
        public void onResume() {
            super.onResume();
            Logs.add(Logs.Type.V, null);

            // Set title
            ((Toolbar) getToolbarParent(getActivity()).findViewById(R.id.toolbar))
                    .setTitle(getResources().getString(R.string.title_activity_notify));
        }
    }







    //////
    private static LinearLayout getToolbarParent(Activity activity) {
        return (LinearLayout) activity.findViewById(android.R.id.list).getParent().getParent().getParent();
    }

    ////// PreferenceActivity //////////////////////////////////////////////////////////////////////
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);

        // Add toolbar
        LinearLayout root = getToolbarParent(this);
        Toolbar toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Logs.add(Logs.Type.V, "view: " + view);
                finish();
            }
        });
        root.addView(toolbar, 0);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        Logs.add(Logs.Type.V, "target: " + target);
        loadHeadersFromResource(R.xml.settings_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }
}
