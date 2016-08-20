package com.studio.artaban.leclassico.activities.introduction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.components.RevealFragment;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 20/08/16.
 * Progress fragment class
 */
public class ProgressFragment extends RevealFragment {

    public static final String TAG = "progress";

    //////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);
        mRootView = inflater.inflate(R.layout.layout_progress, container, false);









        return mRootView;
    }
}
