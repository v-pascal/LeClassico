package com.studio.artaban.leclassico.activities.main;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.tools.Tools;

/**
 * Created by pascal on 03/09/16.
 * Main activity shortcut fragment class
 */
public class ShortcutFragment extends Fragment implements View.OnClickListener {

    public void setMessage(SpannableStringBuilder message) { // Set message text
        Logs.add(Logs.Type.V, "message: " + message);
        ((TextView) mRootView.findViewById(R.id.text_message))
                .setText(message, TextView.BufferType.SPANNABLE);
    }
    public void setInfo(SpannableStringBuilder info) { // Set info text
        Logs.add(Logs.Type.V, "info: " + info);
        ((TextView) mRootView.findViewById(R.id.text_info))
                .setText(info, TextView.BufferType.SPANNABLE);
    }
    public void setIcon(boolean female, String profile, int size) { // Set profile icon
        Logs.add(Logs.Type.V, "female: " + female + ";profile: " + profile);
        Tools.setProfile(getActivity(), (ImageView) mRootView.findViewById(R.id.image_icon),
                female, profile, size, false);
    }
    public void setDate(boolean start, String dateTime) { // Set start or end date
        Logs.add(Logs.Type.V, "start: " + start + ";dateTime: " + dateTime);
        TextView date = (start)?
                (TextView)mRootView.findViewById(R.id.start_date):(TextView)mRootView.findViewById(R.id.end_date);
        TextView time = (start)?
                (TextView)mRootView.findViewById(R.id.start_time):(TextView)mRootView.findViewById(R.id.end_time);
        Tools.setDateTime(getContext(), date, time, dateTime);
    }

    //////
    private View mRootView; // Fragment root view
    private boolean mSearching; // Search flag (search edit box display)

    ////// OnClickListener /////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View sender) { // Search clicked (display or hide search edit box)

        Logs.add(Logs.Type.V, "sender: " + sender);
        View search = mRootView.findViewById(R.id.edit_search);
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        if ((sender.getId() == R.id.button_search) && (mSearching)) {
            mSearching = false;

            ((ImageView)mRootView.findViewById(R.id.button_search))
                    .setImageDrawable(getResources().getDrawable(R.drawable.search));

            imm.hideSoftInputFromWindow(search.getWindowToken(), 0); // Hide keyboard (if displayed)
            ((EditText) search).setText(null);
            search.clearFocus();
            search.setVisibility(View.INVISIBLE);

        } else {
            mSearching = true;

            ((ImageView)mRootView.findViewById(R.id.button_search))
                    .setImageDrawable(getResources().getDrawable(R.drawable.close));
            search.setVisibility(View.VISIBLE);
            search.requestFocus();

            // Force displaying keyboard
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    ////// ShortcutFragment ////////////////////////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);
        mRootView = inflater.inflate(R.layout.fragment_shortcut, container, false);

        // Set UI components according section displayed (id)
        switch (getId()) {
            case R.id.shortcut_home: { ////// Home
                mRootView.findViewById(R.id.layout_start_date).setVisibility(View.GONE);
                mRootView.findViewById(R.id.layout_end_date).setVisibility(View.GONE);
                break;
            }
            case R.id.shortcut_new_publication:
            case R.id.shortcut_publications: { ////// Publications (+new)
                mRootView.findViewById(R.id.layout_start_date).setVisibility(View.GONE);
                mRootView.findViewById(R.id.layout_data).setBackground(getResources()
                        .getDrawable(R.drawable.publication_background));
                ((TextView)mRootView.findViewById(R.id.text_message)).setTextColor(Color.WHITE);
                ((TextView)mRootView.findViewById(R.id.text_info)).setTextColor(Color.WHITE);
                break;
            }
            case R.id.shortcut_events_bottom:
            case R.id.shortcut_new_event:
            case R.id.shortcut_events: { ////// Events (+new)
                mRootView.findViewById(R.id.image_icon).setVisibility(View.GONE);
                mRootView.findViewById(R.id.layout_start_date).setBackground(getResources()
                        .getDrawable(R.drawable.event_date_background));
                mRootView.findViewById(R.id.layout_end_date).setBackground(getResources()
                        .getDrawable(R.drawable.event_date_background));
                ((TextView)mRootView.findViewById(R.id.end_date)).setTextColor(Color.WHITE);
                ((TextView)mRootView.findViewById(R.id.end_time)).setTextColor(Color.WHITE);
                ((TextView)mRootView.findViewById(R.id.text_message)).setGravity(Gravity.CENTER);
                ((TextView)mRootView.findViewById(R.id.text_info)).setGravity(Gravity.CENTER);
                break;
            }
            case R.id.shortcut_members: { ////// Members
                mRootView.findViewById(R.id.layout_start_date).setVisibility(View.GONE);
                mRootView.findViewById(R.id.layout_end_date).setVisibility(View.GONE);
                LinearLayout search = (LinearLayout)mRootView.findViewById(R.id.layout_search);
                ((LinearLayout.LayoutParams)search.getLayoutParams()).weight = 2;
                ((LinearLayout.LayoutParams)mRootView.findViewById(R.id.layout_data).getLayoutParams()).weight = 1;
                ((LinearLayout.LayoutParams)search.getLayoutParams()).width = 0;
                search.setVisibility(View.VISIBLE);
                mRootView.findViewById(R.id.button_search).setOnClickListener(this);
                mRootView.findViewById(R.id.layout_search).setOnClickListener(this);
                break;
            }
        }
        return mRootView;
    }
}
