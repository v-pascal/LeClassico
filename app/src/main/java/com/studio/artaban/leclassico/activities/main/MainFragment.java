package com.studio.artaban.leclassico.activities.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 05/09/16.
 * Main fragment class (MainActivity parent fragment)
 */
public class MainFragment extends Fragment {

    public static Fragment newInstance(int section) {

        Logs.add(Logs.Type.V, "section: " + section);
        switch (section) {
            case Constants.MAIN_SECTION_HOME: return new HomeFragment();
            case Constants.MAIN_SECTION_PUBLICATIONS: return new PublicationsFragment();
            case Constants.MAIN_SECTION_EVENTS: return new EventsFragment();
            case Constants.MAIN_SECTION_MEMBERS: return new MembersFragment();
            case Constants.MAIN_SECTION_NOTIFICATIONS: return new NotificationsFragment();
            default:
                throw new IllegalArgumentException("Unexpected section");
        }
    }

    //
    public interface OnFragmentListener {

        ////// Shortcut
        void onSetMessage(int section, SpannableStringBuilder message);
        void onSetInfo(int section, SpannableStringBuilder info);
        void onSetIcon(int section, boolean female, String profile, int size);
    }
    protected OnFragmentListener mListener;

    //////
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logs.add(Logs.Type.V, "context: " + context);
        if (context instanceof OnFragmentListener)
            mListener = (OnFragmentListener)context;
        else
            throw new RuntimeException(context.toString() + " must implement 'OnFragmentListener'");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logs.add(Logs.Type.V, null);
        mListener = null;
    }
}
