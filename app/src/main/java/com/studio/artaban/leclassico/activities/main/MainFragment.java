package com.studio.artaban.leclassico.activities.main;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.studio.artaban.leclassico.services.DataService;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

import java.lang.ref.WeakReference;
import java.util.Hashtable;

/**
 * Created by pascal on 05/09/16.
 * Main fragment class (MainActivity parent fragment)
 */
public abstract class MainFragment extends Fragment {

    public static Fragment newInstance(int section) {

        Logs.add(Logs.Type.V, "section: " + section);
        Fragment newFragment;
        switch (section) {
            case Constants.MAIN_SECTION_HOME: newFragment = new HomeFragment(); break;
            case Constants.MAIN_SECTION_PUBLICATIONS: newFragment = new PublicationsFragment(); break;
            case Constants.MAIN_SECTION_EVENTS: newFragment = new EventsFragment(); break;
            case Constants.MAIN_SECTION_MEMBERS: newFragment = new MembersFragment(); break;
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

    ////// OnFragmentListener //////////////////////////////////////////////////////////////////////

    public interface OnFragmentListener {

        ////// Shortcut
        interface OnAnimationListener {
            void onCopyNewToPrevious(ShortcutFragment previous);
        }
        ShortcutFragment onGetShortcut(int section, boolean newItem);
        void onAnimateShortcut(int section, OnAnimationListener listener);

        ////// Service
        DataService onGetService();

        ////// URI
        Uri onGetShortcutURI();
    }
    protected OnFragmentListener mListener; // Activity listener

    ////// Fragment ////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Logs.add(Logs.Type.V, "context: " + context);
        if (context instanceof OnFragmentListener)
            mListener = (OnFragmentListener) context;
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
