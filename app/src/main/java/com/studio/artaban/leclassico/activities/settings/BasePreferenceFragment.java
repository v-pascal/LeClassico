package com.studio.artaban.leclassico.activities.settings;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.studio.artaban.leclassico.data.DataObserver;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 20/03/17.
 * Preference fragment with DB management
 */
public abstract class BasePreferenceFragment extends PreferenceFragment implements
        DataObserver.OnContentListener, Preference.OnPreferenceChangeListener {

    protected Uri mUri; // URI to observe DB
    protected DataObserver mObserver; // DB update observer (observe remote DB updates)

    ////// PreferenceFragment //////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);

        // Create observer
        mObserver = new DataObserver(new Handler(Looper.getMainLooper()), this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Logs.add(Logs.Type.V, null);

        // Register DB observer
        mObserver.register(getActivity().getContentResolver(), mUri);
    }

    @Override
    public void onPause() {
        super.onPause();
        Logs.add(Logs.Type.V, null);

        // Unregister DB observer
        mObserver.unregister(getActivity().getContentResolver());
    }
}
