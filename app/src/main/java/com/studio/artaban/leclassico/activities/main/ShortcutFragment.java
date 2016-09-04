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

    //////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_shortcut, container, false);
        switch (getId()) {
            case R.id.shortcut_home: {
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
            case R.id.shortcut_publications: {







                rootView.findViewById(R.id.layout_start_date).setVisibility(View.GONE);
                ((ImageView)rootView.findViewById(R.id.image_icon)).setImageDrawable(
                        getResources().getDrawable(R.mipmap.ic_launcher));
                ((TextView)rootView.findViewById(R.id.text_pseudo)).setText("Publication");
                ((TextView)rootView.findViewById(R.id.text_info)).setText(
                        getString(R.string.home_info, 0, 0));
                rootView.findViewById(R.id.layout_date).setVisibility(View.GONE);









                break;
            }
            case R.id.shortcut_events: {







                rootView.findViewById(R.id.layout_start_date).setVisibility(View.GONE);
                ((ImageView)rootView.findViewById(R.id.image_icon)).setImageDrawable(
                        getResources().getDrawable(R.mipmap.ic_launcher));
                ((TextView)rootView.findViewById(R.id.text_pseudo)).setText("Events");
                ((TextView)rootView.findViewById(R.id.text_info)).setText(
                        getString(R.string.home_info, 0, 0));
                rootView.findViewById(R.id.layout_date).setVisibility(View.GONE);







                break;
            }
            case R.id.shortcut_members: {







                rootView.findViewById(R.id.layout_start_date).setVisibility(View.GONE);
                ((ImageView)rootView.findViewById(R.id.image_icon)).setImageDrawable(
                        getResources().getDrawable(R.mipmap.ic_launcher));
                ((TextView)rootView.findViewById(R.id.text_pseudo)).setText("Members");
                ((TextView)rootView.findViewById(R.id.text_info)).setText(
                        getString(R.string.home_info, 0, 0));
                rootView.findViewById(R.id.layout_date).setVisibility(View.GONE);







                break;
            }
            case R.id.shortcut_notifications: {







                rootView.findViewById(R.id.layout_start_date).setVisibility(View.GONE);
                ((ImageView)rootView.findViewById(R.id.image_icon)).setImageDrawable(
                        getResources().getDrawable(R.mipmap.ic_launcher));
                ((TextView)rootView.findViewById(R.id.text_pseudo)).setText("Notifications");
                ((TextView)rootView.findViewById(R.id.text_info)).setText(
                        getString(R.string.home_info, 0, 0));
                rootView.findViewById(R.id.layout_date).setVisibility(View.GONE);







                break;
            }
        }
        return rootView;
    }
}
