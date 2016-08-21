package com.studio.artaban.leclassico.activities.introduction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.components.RevealFragment;
import com.studio.artaban.leclassico.connection.DataService;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 20/08/16.
 * Progress fragment class
 */
public class ProgressFragment extends RevealFragment {

    public static final String TAG = "progress";

    private static final String DATA_KEY_STEP = "step";
    private static final String DATA_KEY_PROGRESS = "progress";
    private static final String DATA_KEY_ONLINE = "online";
    // Data keys

    private byte mStep = DataService.LOGIN_STEP_CHECK_INTERNET; // Progression step
    private int mProgress; // Progress status
    private boolean mOnline; // Internet connection check result
    private void reset() {

        Logs.add(Logs.Type.V, null);
        ((ImageView)mRootView.findViewById(R.id.checked_internet))
                .setImageDrawable(getResources().getDrawable(R.drawable.forward_purple));

        ImageView pin = (ImageView)mRootView.findViewById(R.id.checked_identification);
        pin.setImageDrawable(getResources().getDrawable(R.drawable.forward_purple));
        pin.setVisibility(View.INVISIBLE);
        pin = (ImageView)mRootView.findViewById(R.id.checked_synchro);
        pin.setImageDrawable(getResources().getDrawable(R.drawable.forward_purple));
        pin.setVisibility(View.INVISIBLE);
        pin = (ImageView)mRootView.findViewById(R.id.checked_notification);
        pin.setImageDrawable(getResources().getDrawable(R.drawable.forward_purple));
        pin.setVisibility(View.INVISIBLE);

        ((ProgressBar)mRootView.findViewById(R.id.progress_view)).setIndeterminate(true);
        ((TextView)mRootView.findViewById(R.id.progress_percentage)).setText(null);
    }

    //
    public void update(byte step, int progress) { // Update connection progress according current step

        Logs.add(Logs.Type.V, "step: " + step + ";progress: " + progress);
        mProgress = progress;
        mStep = step;

        switch (step) {
            case DataService.LOGIN_STEP_CHECK_INTERNET: {
                reset();
                break;
            }
            case DataService.LOGIN_STEP_ONLINE_IDENTIFICATION: {
                mOnline = true;
                reset();

                ((ImageView)mRootView.findViewById(R.id.checked_internet))
                        .setImageDrawable(getResources().getDrawable(R.drawable.checked_orange));
                mRootView.findViewById(R.id.checked_identification).setVisibility(View.VISIBLE);
                break;
            }
            case DataService.LOGIN_STEP_OFFLINE_IDENTIFICATION: {
                mOnline = false;
                reset();

                ((ImageView)mRootView.findViewById(R.id.checked_internet))
                        .setImageDrawable(getResources().getDrawable(R.drawable.cancel_orange));
                mRootView.findViewById(R.id.checked_identification).setVisibility(View.VISIBLE);
                break;
            }
            case DataService.LOGIN_STEP_SUCCEEDED:
            case DataService.SYNCHRONIZATION_STEP_IN_PROGRESS: {
                mProgress = progress;
                reset();

                ImageView pin = (ImageView)mRootView.findViewById(R.id.checked_identification);
                pin.setImageDrawable(getResources().getDrawable(R.drawable.checked_orange));
                pin.findViewById(R.id.checked_identification).setVisibility(View.VISIBLE);
                mRootView.findViewById(R.id.checked_synchro).setVisibility(View.VISIBLE);

                ProgressBar progressBar = (ProgressBar)mRootView.findViewById(R.id.progress_view);
                progressBar.setProgress(progress);
                progressBar.setIndeterminate(false);
                ((TextView)mRootView.findViewById(R.id.progress_percentage))
                        .setText(String.format("%d%%", (byte) (progress * 100f / 13f)));
                break;
            }
        }
    }

    //////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);
        mRootView = inflater.inflate(R.layout.layout_progress, container, false);
        ((ProgressBar)mRootView.findViewById(R.id.progress_view)).setMax(Tables.ID_LAST);

        // Restore data
        if (savedInstanceState != null) {
            byte step = savedInstanceState.getByte(DATA_KEY_STEP);
            int progress = savedInstanceState.getInt(DATA_KEY_PROGRESS);
            mOnline = savedInstanceState.getBoolean(DATA_KEY_ONLINE);

            update(step, progress);
        }
        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putByte(DATA_KEY_STEP, mStep);
        outState.putInt(DATA_KEY_PROGRESS, mProgress);
        outState.putBoolean(DATA_KEY_ONLINE, mOnline);

        Logs.add(Logs.Type.V, "outState: " + outState);
        super.onSaveInstanceState(outState);
    }
}
