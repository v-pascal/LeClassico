package com.studio.artaban.leclassico.activities.main;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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

    private static final String DATA_KEY_QUERY_COUNT = "queryCount";
    private static final String DATA_KEY_QUERY_ID = "queryID";
    private static final String DATA_KEY_QUERY_OLD = "queryOld";
    // Data keys

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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected short mQueryCount; // DB query result count
    protected int mQueryID = Constants.NO_DATA; // Max record Id (used to check if new entries)
    protected short mQueryOld; // DB old entries requested

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore data
        if (savedInstanceState != null) {
            mQueryCount = savedInstanceState.getShort(DATA_KEY_QUERY_COUNT);
            mQueryID = savedInstanceState.getInt(DATA_KEY_QUERY_ID);
            mQueryOld = savedInstanceState.getShort(DATA_KEY_QUERY_OLD);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putShort(DATA_KEY_QUERY_COUNT, mQueryCount);
        outState.putInt(DATA_KEY_QUERY_ID, mQueryID);
        outState.putShort(DATA_KEY_QUERY_OLD, mQueryOld);

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
