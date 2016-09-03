package com.studio.artaban.leclassico.activities.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 03/09/16.
 * Main activity shortcut fragment class
 */
public class ShortcutFragment extends Fragment {

    public static final String TAG_HOME = "home";
    public static final String TAG_PUBLICATIONS = "publications";
    public static final String TAG_EVENTS = "events";
    public static final String TAG_MEMBERS = "members";
    public static final String TAG_NOTIFICATIONS = "notifications";
    // Tags

    //////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_shortcut, container, false);
        switch (getTag()) {

            case TAG_HOME: {
                rootView.findViewById(R.id.layout_start_date).setVisibility(View.GONE);
                ((ImageView)rootView.findViewById(R.id.image_icon)).setImageDrawable(
                        getResources().getDrawable(R.mipmap.ic_launcher));
                ((TextView)rootView.findViewById(R.id.text_pseudo)).setText(
                        getActivity().getIntent().getStringExtra(MainActivity.EXTRA_DATA_KEY_PSEUDO));
                ((TextView)rootView.findViewById(R.id.text_info)).setText(
                        getString(R.string.home_info, 0, 0));
                rootView.findViewById(R.id.layout_date).setVisibility(View.GONE);
                break;
            }
            case TAG_PUBLICATIONS: {





                rootView.findViewById(R.id.layout_start_date).setVisibility(View.GONE);
                ((ImageView)rootView.findViewById(R.id.image_icon)).setImageDrawable(
                        getResources().getDrawable(R.mipmap.ic_launcher));
                ((TextView)rootView.findViewById(R.id.text_pseudo)).setText("BOB");
                ((TextView)rootView.findViewById(R.id.text_info)).setText(
                        getString(R.string.home_info, 0, 0));
                rootView.findViewById(R.id.layout_date).setVisibility(View.GONE);






                break;
            }
        }
        return rootView;
    }
}
