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
 * Notifications fragment class (MainActivity)
 */
public class NotificationsFragment extends MainFragment {

    //////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        rootView.setTag(Constants.MAIN_SECTION_NOTIFICATIONS);

        // Set shortcut data (default)
        SpannableStringBuilder data = new SpannableStringBuilder(getString(R.string.no_notification));
        mListener.onSetMessage(Constants.MAIN_SECTION_NOTIFICATIONS, data);
        data = new SpannableStringBuilder(getString(R.string.no_notification_info));
        mListener.onSetInfo(Constants.MAIN_SECTION_NOTIFICATIONS, data);









        return rootView;
    }
}
