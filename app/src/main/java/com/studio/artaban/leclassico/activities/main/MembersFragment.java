package com.studio.artaban.leclassico.activities.main;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.tables.AbonnementsTable;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;

/**
 * Created by pascal on 05/09/16.
 * Members fragment class (MainActivity)
 */
public class MembersFragment extends MainFragment implements QueryLoader.OnResultListener {

    private QueryLoader mLastMemberLoader; // Shortcut last followed member query loader

    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        cursor.moveToFirst();

        if ((byte)id == Tables.ID_ABONNEMENTS) {




            /*
            ((ImageView)mRootView.findViewById(R.id.image_icon))
                    .setImageDrawable(getResources().getDrawable(R.drawable.man));
            */




            SpannableStringBuilder member = new SpannableStringBuilder(cursor.getString(1));
            mListener.onSetMessage(Constants.MAIN_SECTION_MEMBERS, member);
        }
        cursor.close();
    }

    @Override
    public void onLoaderReset() {

    }

    ////// MainFragment ////////////////////////////////////////////////////////////////////////////
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logs.add(Logs.Type.V, "context: " + context);
        mLastMemberLoader = new QueryLoader(context, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_members, container, false);
        rootView.setTag(Constants.MAIN_SECTION_MEMBERS);

        // Set shortcut data
        SpannableStringBuilder data = new SpannableStringBuilder(getString(R.string.last_followed_info));
        mListener.onSetInfo(Constants.MAIN_SECTION_MEMBERS, data);

        // Load shortcut info (using query loaders)
        Bundle shortcutData = new Bundle();
        shortcutData.putBoolean(QueryLoader.DATA_KEY_URI_SINGLE, false);
        shortcutData.putStringArray(QueryLoader.DATA_KEY_PROJECTION, new String[]{
                "max(" + AbonnementsTable.COLUMN_STATUS_DATE + ")",
                AbonnementsTable.COLUMN_CAMARADE
        });
        shortcutData.putString(QueryLoader.DATA_KEY_SELECTION, AbonnementsTable.COLUMN_PSEUDO + "='" +
                        getActivity().getIntent().getStringExtra(MainActivity.EXTRA_DATA_KEY_PSEUDO) + "'");
        mLastMemberLoader.restart(getActivity(), Tables.ID_ABONNEMENTS, shortcutData);












        return rootView;
    }
}
