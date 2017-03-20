package com.studio.artaban.leclassico.activities.settings;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.studio.artaban.leclassico.data.DataObserver;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.tools.Tools;

/**
 * Created by pascal on 20/03/17.
 * Preference fragment with DB management (link between DB & Preferences)
 */
public abstract class BasePreferenceFragment extends PreferenceFragment implements
        DataObserver.OnContentListener, Preference.OnPreferenceChangeListener {

    protected Uri mUri; // URI to observe DB
    protected DataObserver mObserver; // DB update observer (observe remote DB updates)

    //////
    protected void updateData(final String key, final Object newValue) {
        Logs.add(Logs.Type.V, "key: " + key + ";newValue: " + newValue);

        new Thread(new Runnable() { // Background process
            @Override
            public void run() {
                //Logs.add(Logs.Type.V, null);
                onUpdateData(key, newValue);
            }
        }).start();
    }

    ////// BasePreferenceFragment //////////////////////////////////////////////////////////////////

    protected abstract void displayData(@Nullable Bundle result); // Set preferences values (on UI)

    protected abstract Bundle onDataChanged(ContentResolver resolver); // To update preferences (DB changed)
    protected abstract void onUpdateData(String key, Object newValue); // To implement DB update

    ////// OnContentListener ///////////////////////////////////////////////////////////////////////
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Logs.add(Logs.Type.V, "selfChange: " + selfChange + ";uri: " + uri);

        final ContentResolver resolver = getActivity().getContentResolver();
        Tools.startProcess(getActivity(), new Tools.OnProcessListener() {
            @Override
            public Bundle onBackgroundTask() {
                Logs.add(Logs.Type.V, null);
                return onDataChanged(resolver);
            }

            @Override
            public void onMainNextTask(Bundle backResult) {
                Logs.add(Logs.Type.V, "backResult: " + backResult);
                displayData(backResult);
            }
        });
    }

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
