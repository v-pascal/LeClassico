package com.studio.artaban.leclassico.activities.location;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.LoggedActivity;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;
import com.studio.artaban.leclassico.tools.Tools;

/**
 * Created by pascal on 22/11/16.
 * Location activity
 */
public class LocationActivity extends LoggedActivity implements OnMapReadyCallback {

    // Extra data keys (see 'LoggedActivity' & 'Login' extra data keys)


    //////
    public void onSearch(View sender) {
        Logs.add(Logs.Type.V, "sender: " + sender);



    }
    public void onToday(View sender) {
        Logs.add(Logs.Type.V, "sender: " + sender);



    }
    public void onShare(View sender) {
        Logs.add(Logs.Type.V, "sender: " + sender);



    }
    public void onLocate(View sender) {
        Logs.add(Logs.Type.V, "sender: " + sender);



    }

    ////// OnMapReadyCallback //////////////////////////////////////////////////////////////////////
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Logs.add(Logs.Type.V, "googleMap: " + googleMap);

        // Initialize map (position, zoom, etc.)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46, 2.2), 5)); // Europe (France)
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
    }

    ////// LoggedActivity //////////////////////////////////////////////////////////////////////////
    @Override
    protected void onLoggedResume() {

    }

    //////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        if (onNotifyLoadFinished(id, cursor)) // Refresh notification info
            return;

        switch (id) {
            case Queries.LOCATION_USER_INFO: { // Location share info
                ((ImageView)findViewById(R.id.image_share))
                        .setImageDrawable(getDrawable((cursor.isNull(0)) ?
                                R.drawable.ic_location_off_white_36dp : R.drawable.ic_location_on_white_36dp));
                break;
            }
            case Queries.LOCATION_FOLLOWERS: {






                Logs.add(Logs.Type.I, "Count: " + cursor.getCount());







                break;
            }
        }
    }

    @Override
    public void onLoaderReset() {

    }

    //////
    private final QueryLoader mFollowers = new QueryLoader(this, this); // Followers list query loader
    private final QueryLoader mUser = new QueryLoader(this, this); // User location share info query loader

    // Followers query column indexes
    private static final int COLUMN_INDEX_ID = 0;
    private static final int COLUMN_INDEX_PSEUDO = 1;
    private static final int COLUMN_INDEX_SEXE = 2;
    private static final int COLUMN_INDEX_PROFILE = 3;
    private static final int COLUMN_INDEX_PHONE = 4;
    private static final int COLUMN_INDEX_EMAIL = 5;
    private static final int COLUMN_INDEX_VILLE = 6;
    private static final int COLUMN_INDEX_NOM = 7;
    private static final int COLUMN_INDEX_ADRESSE = 8;
    private static final int COLUMN_INDEX_ADMIN = 9;
    private static final int COLUMN_INDEX_LATITUDE = 10;
    private static final int COLUMN_INDEX_LATITUDE_UPD = 11;
    private static final int COLUMN_INDEX_LONGITUDE = 12;
    private static final int COLUMN_INDEX_LONGITUDE_UPD = 13;

    ////// AppCompatActivity ///////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
        setContentView(R.layout.activity_location);

        // Make navigation bar translucent (window size)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        // Set action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get pseudo
        String pseudo;
        if (getIntent().getData() != null)
            pseudo = Uri.decode(getIntent().getData().getPathSegments().get(1));
        else
            pseudo = getIntent().getStringExtra(Login.EXTRA_DATA_PSEUDO);
        Logs.add(Logs.Type.I, "Pseudo: " + pseudo);

        // Initialize map
        if (getSupportFragmentManager().findFragmentById(R.id.map) == null) {
            SupportMapFragment map = new SupportMapFragment();
            map.getMapAsync(this);
            getSupportFragmentManager().beginTransaction().add(R.id.map, map).commit();
        }
        // Position map & control panel
        ((RelativeLayout.LayoutParams) findViewById(R.id.layout_panel).getLayoutParams())
                .setMargins(0, Tools.getStatusBarHeight(getResources()) + Tools.getActionBarHeight(this), 0, 0);

        // Initialize loaders
        Bundle userData = new Bundle();
        userData.putStringArray(QueryLoader.DATA_KEY_PROJECTION, new String[]{CamaradesTable.COLUMN_DEVICE_ID});
        userData.putString(QueryLoader.DATA_KEY_SELECTION, CamaradesTable.COLUMN_PSEUDO + "='" + pseudo + '\'');
        userData.putParcelable(QueryLoader.DATA_KEY_URI,
                Uri.parse(DataProvider.CONTENT_URI + CamaradesTable.TABLE_NAME));
        mUser.init(this, Queries.LOCATION_USER_INFO, userData);

        Bundle followData = new Bundle();
        followData.putParcelable(QueryLoader.DATA_KEY_URI, Uris.getUri(Uris.ID_USER_LOCATION, pseudo));
        mFollowers.init(this, Queries.LOCATION_FOLLOWERS, followData);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logs.add(Logs.Type.V, "item: " + item);
        if (onNotifyItemSelected(item))
            return true; // Display notifications

        if (item.getItemId() == android.R.id.home) { // Back to previous activity
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
