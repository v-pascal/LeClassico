package com.studio.artaban.leclassico.activities.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
        mRootView = inflater.inflate(R.layout.fragment_shortcut, container, false);

        // Set UI components according section displayed
        switch (getId()) {
            case R.id.shortcut_home: { ////// Home
                mRootView.findViewById(R.id.layout_start_date).setVisibility(View.GONE);
                mRootView.findViewById(R.id.layout_end_date).setVisibility(View.GONE);
                break;
            }
            case R.id.shortcut_publications: { ////// Publications
                mRootView.findViewById(R.id.layout_start_date).setVisibility(View.GONE);
                mRootView.findViewById(R.id.layout_data).setBackground(getResources()
                        .getDrawable(R.drawable.publication_background));
                ((TextView)mRootView.findViewById(R.id.text_message)).setTextColor(Color.WHITE);
                ((TextView)mRootView.findViewById(R.id.text_info)).setTextColor(Color.WHITE);
                break;
            }
            case R.id.shortcut_events: { ////// Events
                mRootView.findViewById(R.id.image_icon).setVisibility(View.GONE);
                mRootView.findViewById(R.id.layout_start_date).setBackground(getResources()
                        .getDrawable(R.drawable.event_date_background));
                mRootView.findViewById(R.id.layout_end_date).setBackground(getResources()
                        .getDrawable(R.drawable.event_date_background));
                ((TextView)mRootView.findViewById(R.id.end_date)).setTextColor(Color.WHITE);
                ((TextView)mRootView.findViewById(R.id.end_time)).setTextColor(Color.WHITE);
                ((LinearLayout)mRootView.findViewById(R.id.layout_data)).setGravity(Gravity.CENTER);
                break;
            }
            case R.id.shortcut_members: { ////// Members
                mRootView.findViewById(R.id.layout_start_date).setVisibility(View.GONE);
                mRootView.findViewById(R.id.layout_end_date).setVisibility(View.GONE);
                ((LinearLayout.LayoutParams)mRootView.findViewById(R.id.layout_data).getLayoutParams()).weight = 0;
                ((LinearLayout.LayoutParams)mRootView.findViewById(R.id.layout_data).getLayoutParams())
                        .width = ViewGroup.LayoutParams.WRAP_CONTENT;
                break;
            }
            case R.id.shortcut_notifications: {
                mRootView.findViewById(R.id.layout_start_date).setVisibility(View.GONE);
                mRootView.findViewById(R.id.layout_end_date).setBackground(getResources()
                        .getDrawable(R.drawable.notifcation_date_background));
                ((TextView)mRootView.findViewById(R.id.end_date)).setTextColor(Color.WHITE);
                ((TextView)mRootView.findViewById(R.id.end_time)).setTextColor(Color.WHITE);
                break;
            }
        }
        return mRootView;
    }
}
