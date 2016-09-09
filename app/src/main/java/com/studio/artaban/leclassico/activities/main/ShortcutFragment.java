package com.studio.artaban.leclassico.activities.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
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

    public void setMessage(SpannableStringBuilder message) {
        Logs.add(Logs.Type.V, "message: " + message);
        ((TextView) mRootView.findViewById(R.id.text_message))
                .setText(message, TextView.BufferType.SPANNABLE);
    }
    public void setInfo(SpannableStringBuilder info) {
        Logs.add(Logs.Type.V, "info: " + info);
        ((TextView) mRootView.findViewById(R.id.text_info))
                .setText(info, TextView.BufferType.SPANNABLE);
    }

    private View mRootView; // Fragment root view

    //////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);

        // Set UI components according section displayed
        mRootView = inflater.inflate(R.layout.fragment_shortcut, container, false);
        switch (getId()) {
            case R.id.shortcut_home: { // Home
                mRootView.findViewById(R.id.layout_start_date).setVisibility(View.GONE);
                mRootView.findViewById(R.id.layout_end_date).setVisibility(View.GONE);
                break;
            }
            case R.id.shortcut_publications: { // Publications
                mRootView.findViewById(R.id.layout_start_date).setVisibility(View.GONE);
                mRootView.findViewById(R.id.layout_data).setBackground(getResources()
                        .getDrawable(R.drawable.publication_background));
                ((TextView)mRootView.findViewById(R.id.text_message)).setTextColor(Color.WHITE);
                ((TextView)mRootView.findViewById(R.id.text_info)).setTextColor(Color.WHITE);
                break;
            }










            default: {
                mRootView.findViewById(R.id.layout_start_date).setVisibility(View.GONE);
                ((ImageView)mRootView.findViewById(R.id.image_icon)).setImageDrawable(
                        getResources().getDrawable(R.mipmap.ic_launcher));
                mRootView.findViewById(R.id.layout_end_date).setVisibility(View.GONE);
                ((TextView)mRootView.findViewById(R.id.text_message)).setText("ID: " + getId());
                break;
            }

            /*
            case R.id.shortcut_events: {




                break;
            }
            case R.id.shortcut_members: {




                break;
            }
            case R.id.shortcut_notifications: {




                break;
            }
            */
        }
        return mRootView;
    }
}
