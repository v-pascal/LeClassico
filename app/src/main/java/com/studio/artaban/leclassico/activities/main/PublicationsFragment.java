package com.studio.artaban.leclassico.activities.main;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 05/09/16.
 * Publications fragment class (MainActivity)
 */
public class PublicationsFragment extends MainFragment {

    ////// MainFragment ////////////////////////////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_publications, container, false);
        rootView.setTag(Constants.MAIN_SECTION_PUBLICATIONS);

        // Set shortcut data (default)
        SpannableStringBuilder data = new SpannableStringBuilder(getString(R.string.no_publication));
        mListener.onGetShortcut(Constants.MAIN_SECTION_PUBLICATIONS, false).setMessage(data);
        data = new SpannableStringBuilder(getString(R.string.no_publication_info));
        mListener.onGetShortcut(Constants.MAIN_SECTION_PUBLICATIONS, false).setInfo(data);











        return rootView;
    }
}
