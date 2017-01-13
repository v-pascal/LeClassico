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

    //////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);
        mRootView = inflater.inflate(R.layout.layout_login, container, false);








        ((EditText)mRootView.findViewById(R.id.edit_pseudo)).setText("testers");
        ((EditText)mRootView.findViewById(R.id.edit_password)).setText("terstes");









        return mRootView;
    }
}
