package com.studio.artaban.leclassico.activities.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
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
import com.studio.artaban.leclassico.activities.LoggedActivity;
import com.studio.artaban.leclassico.activities.profile.ProfileActivity;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.tools.Tools;

/**
 * Created by pascal on 03/09/16.
 * Main activity shortcut fragment class
 */
public class ShortcutFragment extends Fragment implements View.OnClickListener, TextWatcher {

    private static final String DATA_KEY_SEARCHING = "searching";
    // Data keys

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
    public void setIcon(int pseudoId, boolean female, String profile) { // Set profile icon info
        Logs.add(Logs.Type.V, "pseudoId: " + pseudoId + ";female: " + female + ";profile: " + profile);
        ImageView iconPseudo = (ImageView) mRootView.findViewById(R.id.image_icon);

        iconPseudo.setTag(R.id.tag_pseudo_id, pseudoId);
        iconPseudo.setOnClickListener(this);
        Tools.setProfile(getActivity(), iconPseudo, female, profile, R.dimen.shortcut_height, true);
    }
    public void setDate(boolean start, String dateTime) { // Set start or end date
        Logs.add(Logs.Type.V, "start: " + start + ";dateTime: " + dateTime);
        TextView date = (start)?
                (TextView)mRootView.findViewById(R.id.start_date):(TextView)mRootView.findViewById(R.id.end_date);
        TextView time = (start)?
                (TextView)mRootView.findViewById(R.id.start_time):(TextView)mRootView.findViewById(R.id.end_time);
        Tools.setDateTime(getContext(), date, time, dateTime);
    }

    public String getFilter() { // Return current member filter (if any)
        Logs.add(Logs.Type.V, null);
        return ((EditText) mRootView.findViewById(R.id.edit_search)).getText().toString();
    }

    //////
    private View mRootView; // Fragment root view
    private boolean mSearching; // Search flag (search edit box display)

    private void searching(View search) { // Start searching member (filter on pseudo)
        Logs.add(Logs.Type.V, "search: " + search);
        mSearching = true;

        ((ImageView)mRootView.findViewById(R.id.button_search))
                .setImageDrawable(getResources().getDrawable(R.drawable.close));
        search.setVisibility(View.VISIBLE);
        search.requestFocus();
    }

    ////// OnClickListener /////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View sender) { // Search clicked (display or hide search edit box)

        Logs.add(Logs.Type.V, "sender: " + sender);
        switch (sender.getId()) {
            case R.id.image_icon: { ////// Display last member followed profile

                int pseudoId = (int)sender.getTag(R.id.tag_pseudo_id);
                Logs.add(Logs.Type.I, "Display last member followed #" + pseudoId);

                ////// Start profile activity
                Intent profile = new Intent(getActivity(), ProfileActivity.class);
                profile.putExtra(LoggedActivity.EXTRA_DATA_ID, pseudoId);
                startActivity(profile);
                break;
            }
            default: {
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
                    searching(search);

                    // Force displaying keyboard
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
                break;
            }
        }
    }

    ////// TextWatcher /////////////////////////////////////////////////////////////////////////////
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable edit) {
        //Logs.add(Logs.Type.V, "edit: " + edit);
        if (mListener != null)
            mListener.onMemberSearch(edit.toString());
    }

    //////
    private OnShortcutListener mListener; // Activity listener
    public interface OnShortcutListener {
        void onMemberSearch(String filter);
    }

    ////// ShortcutFragment ////////////////////////////////////////////////////////////////////////
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logs.add(Logs.Type.V, null);

        if (context instanceof OnShortcutListener)
            mListener = (OnShortcutListener)context;
        else
            throw new RuntimeException(context.toString() + " must implement 'OnShortcutListener'");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);
        mRootView = inflater.inflate(R.layout.fragment_shortcut, container, false);

        // Restore data
        if ((savedInstanceState != null) && (savedInstanceState.getBoolean(DATA_KEY_SEARCHING)))
            searching(mRootView.findViewById(R.id.edit_search));

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
                ((EditText)mRootView.findViewById(R.id.edit_search)).addTextChangedListener(this);
                break;
            }
        }
        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putBoolean(DATA_KEY_SEARCHING, mSearching);

        Logs.add(Logs.Type.V, "outState: " + outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logs.add(Logs.Type.V, null);
        mListener = null;
    }
}
