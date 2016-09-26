package com.studio.artaban.leclassico.activities.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

import java.lang.ref.WeakReference;
import java.util.Hashtable;

/**
 * Created by pascal on 05/09/16.
 * Main fragment class (MainActivity parent fragment)
 */
public class MainFragment extends Fragment {

    public static Fragment newInstance(int section) {

        Logs.add(Logs.Type.V, "section: " + section);
        Fragment newFragment;
        switch (section) {
            case Constants.MAIN_SECTION_HOME: newFragment = new HomeFragment(); break;
            case Constants.MAIN_SECTION_PUBLICATIONS: newFragment = new PublicationsFragment(); break;
            case Constants.MAIN_SECTION_EVENTS: newFragment = new EventsFragment(); break;
            case Constants.MAIN_SECTION_MEMBERS: newFragment = new MembersFragment(); break;
            case Constants.MAIN_SECTION_NOTIFICATIONS: newFragment = new NotificationsFragment(); break;
            default:
                throw new IllegalArgumentException("Unexpected section");
        }
        fragmentInstances.put(section, new WeakReference<>(newFragment));
        return newFragment;
    }

    private static Hashtable<Integer, WeakReference<Fragment>> fragmentInstances = new Hashtable<>();
    public static Fragment getBySection(int section) {
    // Return fragment according section (or null if not exist)

        WeakReference<Fragment> reference = fragmentInstances.get(section);
        return (reference != null)? reference.get():null;
    }

    //
    public interface OnFragmentListener {

        ////// Shortcut
        void onSetMessage(int section, SpannableStringBuilder message);
        void onSetInfo(int section, SpannableStringBuilder info);
        void onSetIcon(int section, boolean female, String profile, int size);
        void onSetDate(int section, boolean start, String dateTime);

        void onSetNotify(char type, boolean read);
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
