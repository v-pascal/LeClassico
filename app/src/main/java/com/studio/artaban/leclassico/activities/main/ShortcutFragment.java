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

    public void setInfo(String info) {
        Logs.add(Logs.Type.V, "info: " + info);
        ((TextView) mRootView.findViewById(R.id.text_info)).setText(info);
    }

    private View mRootView; // Fragment root view

    //////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);

        mRootView = inflater.inflate(R.layout.fragment_shortcut, container, false);
        switch (getId()) {
            case R.id.shortcut_home: {

                mRootView.findViewById(R.id.layout_start_date).setVisibility(View.GONE);
                ((ImageView)mRootView.findViewById(R.id.image_icon)).setImageDrawable(
                        getResources().getDrawable(R.mipmap.ic_launcher));
                ((TextView)mRootView.findViewById(R.id.text_pseudo)).setText(
                        getActivity().getIntent().getStringExtra(MainActivity.EXTRA_DATA_KEY_PSEUDO));
                mRootView.findViewById(R.id.layout_date).setVisibility(View.GONE);
                break;
            }
            case R.id.shortcut_publications: {




                break;
            }
            case R.id.shortcut_events: {




                break;
            }
            case R.id.shortcut_members: {




                break;
            }
            case R.id.shortcut_notifications: {




                break;
            }
        }
        return mRootView;
    }
}
