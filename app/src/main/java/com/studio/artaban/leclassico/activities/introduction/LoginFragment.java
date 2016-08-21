package com.studio.artaban.leclassico.activities.introduction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.components.RevealFragment;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 20/08/16.
 * Login fragment class
 */
public class LoginFragment extends RevealFragment {

    public static final String TAG = "login";
    public String getPseudo() {
        return ((EditText)mRootView.findViewById(R.id.edit_pseudo)).getText().toString();
    }
    public String getPassword() {
        return ((EditText)mRootView.findViewById(R.id.edit_password)).getText().toString();
    }

    //
    private boolean mResetRequested; // Reset request flag
    public void reset() { // Clear pseudo & password entered by user

        Logs.add(Logs.Type.V, null);
        try {
            ((EditText) mRootView.findViewById(R.id.edit_pseudo)).setText(null);
            ((EditText) mRootView.findViewById(R.id.edit_password)).setText(null);

        } catch (NullPointerException e) {
            Logs.add(Logs.Type.W, "Root view not defined yet");
            mResetRequested = true;
        }
    }

    //////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);
        mRootView = inflater.inflate(R.layout.layout_login, container, false);
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Logs.add(Logs.Type.V, null);

        if (mResetRequested) {
            mResetRequested = false;

            Logs.add(Logs.Type.I, "Clear login info");
            ((EditText) mRootView.findViewById(R.id.edit_pseudo)).setText(null);
            ((EditText) mRootView.findViewById(R.id.edit_password)).setText(null);
        }
    }
}
