package com.studio.artaban.leclassico.activities.location;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.studio.artaban.leclassico.LeClassicoApp;
import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.LoggedActivity;
import com.studio.artaban.leclassico.activities.profile.ProfileActivity;
import com.studio.artaban.leclassico.animations.InOutScreen;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;
import com.studio.artaban.leclassico.tools.Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pascal on 22/11/16.
 * Location activity
 */
public class LocationActivity extends LoggedActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    // Extra data keys (see 'LoggedActivity' & 'Login' extra data keys)

    private static final String DATA_KEY_TODAY_FLAG = "todayFlag";
    // Data keys

    private static final int DELAY_DISPLAY_MEMBER = 300; // Delay of displaying or hiding member info
    private static final float[] MARKER_COLORS = new float[]{ // Marker colors array
        BitmapDescriptorFactory.HUE_RED,
        BitmapDescriptorFactory.HUE_ORANGE,
        BitmapDescriptorFactory.HUE_YELLOW,
        BitmapDescriptorFactory.HUE_GREEN,
        BitmapDescriptorFactory.HUE_CYAN,
        BitmapDescriptorFactory.HUE_AZURE,
        BitmapDescriptorFactory.HUE_BLUE,
        BitmapDescriptorFactory.HUE_VIOLET,
        BitmapDescriptorFactory.HUE_MAGENTA,
        BitmapDescriptorFactory.HUE_ROSE
    };
    private static final @ColorRes int[] MEMBER_COLORS = new int[]{ // Member map color IDs
        R.color.map_red,
        R.color.map_orange,
        R.color.map_yellow,
        R.color.map_green,
        R.color.map_cyan,
        R.color.map_azure,
        R.color.map_blue,
        R.color.map_violet,
        R.color.map_magenta,
        R.color.map_rose
    };
    private GoogleMap mMap; // Google map
    private boolean mToday; // Today filter flag
    private boolean mSelected; // Member selection flag

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

    ////// OnMarkerClickListener ///////////////////////////////////////////////////////////////////
    @Override
    public boolean onMarkerClick(Marker marker) {
        Logs.add(Logs.Type.V, "marker: " + marker);

        // Select member info
        MarkerInfo data = (MarkerInfo) marker.getTag();
        findViewById(R.id.layout_member).setBackgroundColor(getResources().getColor(data.color));
        ((TextView) findViewById(R.id.text_pseudo)).setText(data.pseudo);
        ((TextView) findViewById(R.id.text_info)).setText(data.info);

        ImageView image = (ImageView) findViewById(R.id.image_pseudo);
        image.setTag(R.id.tag_pseudo_id, data.id);
        Tools.setProfile(this, image, data.female, data.profile, R.dimen.user_item_height, true);

        if (!mSelected) // Display selection with animation
            InOutScreen.with(this)
                    .setLocation(InOutScreen.Location.LEFT)
                    .setDuration(DELAY_DISPLAY_MEMBER)
                    .in(findViewById(R.id.layout_member));

        mSelected = true;
        return false;
    }

    //////
    private static class MarkerInfo {
        public MarkerInfo(int id, String profile, boolean female, String pseudo,
                          String info, @ColorRes int color) {

            Logs.add(Logs.Type.V, "id: " + id + ";profile: " + profile + ";female: " + female +
                    ";pseudo: " + pseudo + ";info: " + info);
            this.id = id;
            this.profile = profile;
            this.female = female;
            this.pseudo = pseudo;
            this.info = info;
            this.color = color;
        }
        public final int id;
        public final String profile;
        public final boolean female;
        public final String pseudo;
        public final String info;
        public final @ColorRes int color;
    }

    ////// OnMapClickListener //////////////////////////////////////////////////////////////////////
    @Override
    public void onMapClick(LatLng latLng) {
        Logs.add(Logs.Type.V, "latLng: " + latLng);
        if (mSelected)
            InOutScreen.with(this) // Hide selection
                    .setLocation(InOutScreen.Location.LEFT)
                    .setDuration(DELAY_DISPLAY_MEMBER)
                    .out(findViewById(R.id.layout_member));

        mSelected = false;
    }

    ////// OnMapReadyCallback //////////////////////////////////////////////////////////////////////
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Logs.add(Logs.Type.V, "googleMap: " + googleMap);
        mMap = googleMap;

        // Initialize map (position, zoom, etc.)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46, 2.2), 5)); // Europe (France)
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
    }

    ////// LoggedActivity //////////////////////////////////////////////////////////////////////////
    @Override
    protected void onLoggedResume() {

    }

    //////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        if ((!cursor.moveToFirst()) || (onNotifyLoadFinished(id, cursor)))
            return;

        switch (id) {
            case Queries.LOCATION_USER_INFO: { // Location share info
                ((ImageView)findViewById(R.id.image_share))
                        .setImageDrawable(getDrawable((cursor.isNull(0)) ?
                                R.drawable.ic_location_off_white_36dp : R.drawable.ic_location_on_white_36dp));
                break;
            }
            case Queries.LOCATION_FOLLOWERS: {
                do {
                    int colorIdx = LeClassicoApp.getRandom().nextInt(MARKER_COLORS.length);

                    StringBuilder title = new StringBuilder();
                    SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.FORMAT_DATE_TIME);
                    String date = (cursor.getString(COLUMN_INDEX_LATITUDE_UPD)
                            .compareTo(cursor.getString(COLUMN_INDEX_LONGITUDE_UPD)) > 0)?
                            cursor.getString(COLUMN_INDEX_LATITUDE_UPD) :
                            cursor.getString(COLUMN_INDEX_LONGITUDE_UPD);
                    try {
                        Date locationDate = dateFormat.parse(date);
                        if (!mToday) {
                            title.append(DateFormat.getDateFormat(this).format(locationDate));
                            title.append(' ');
                        }
                        title.append(DateFormat.getTimeFormat(this).format(locationDate));

                    } catch (ParseException e) {
                        Logs.add(Logs.Type.E, "Wrong location date format: " + date);
                        title.append(date);
                    }
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(MARKER_COLORS[colorIdx]))
                            .title(title.toString())
                            .position(new LatLng(cursor.getDouble(COLUMN_INDEX_LATITUDE),
                                    cursor.getDouble(COLUMN_INDEX_LONGITUDE))));

                    // Add marker info
                    boolean female = (!cursor.isNull(COLUMN_INDEX_SEXE)) &&
                            (cursor.getInt(COLUMN_INDEX_SEXE) == CamaradesTable.GENDER_FEMALE);
                    String profile = (!cursor.isNull(COLUMN_INDEX_PROFILE))?
                            cursor.getString(COLUMN_INDEX_PROFILE) : null;

                    marker.setTag(new MarkerInfo(cursor.getInt(COLUMN_INDEX_ID), profile, female,
                            cursor.getString(COLUMN_INDEX_PSEUDO),
                            Tools.getUserInfo(getResources(), cursor, COLUMN_INDEX_PHONE),
                            MEMBER_COLORS[colorIdx]));

                } while (cursor.moveToNext());
                cursor.moveToFirst();
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
    //private static final int COLUMN_INDEX_EMAIL = 5;
    //private static final int COLUMN_INDEX_VILLE = 6;
    //private static final int COLUMN_INDEX_NOM = 7;
    //private static final int COLUMN_INDEX_ADRESSE = 8;
    //private static final int COLUMN_INDEX_ADMIN = 9;
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

        // Restore data
        if (savedInstanceState != null)
            mToday = savedInstanceState.getBoolean(DATA_KEY_TODAY_FLAG);
        if (mToday)
            ((ImageView)findViewById(R.id.image_today))
                    .setImageDrawable(getDrawable(R.drawable.ic_today_white_36dp));

        // Get pseudo
        String pseudo;
        if (getIntent().getData() != null)
            pseudo = Uri.decode(getIntent().getData().getPathSegments().get(1));
        else
            pseudo = getIntent().getStringExtra(Login.EXTRA_DATA_PSEUDO);
        Logs.add(Logs.Type.I, "Pseudo: " + pseudo);

        // Set map
        SupportMapFragment map = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        if (map == null) {
            map = new SupportMapFragment();
            map.getMapAsync(this);
            getSupportFragmentManager().beginTransaction().add(R.id.map, map).commit();

        } else
            map.getMapAsync(this);

        // Position map & control panel
        ((RelativeLayout.LayoutParams) findViewById(R.id.layout_panel).getLayoutParams())
                .setMargins(0, Tools.getStatusBarHeight(getResources()) + Tools.getActionBarHeight(this), 0, 0);

        // Display member layout on half screen (horizontally)
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        int padding = (int) getResources().getDimension(R.dimen.location_panel_padding);
        ((RelativeLayout.LayoutParams) findViewById(R.id.layout_member).getLayoutParams())
                .width =  (screenSize.x >> 1) - (padding + (padding >> 1));

        findViewById(R.id.image_pseudo).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View sender) {
                Logs.add(Logs.Type.V, "sender: " + sender);

                int pseudoId = (int) sender.getTag(R.id.tag_pseudo_id);
                Logs.add(Logs.Type.V, "Display profile #" + pseudoId);

                ////// Start profile activity
                Intent profile = new Intent(LocationActivity.this, ProfileActivity.class);
                profile.putExtra(LoggedActivity.EXTRA_DATA_ID, pseudoId);
                startActivity(profile);
            }
        }); // To display member profile

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
    protected void onSaveInstanceState(Bundle outState) {

        outState.putBoolean(DATA_KEY_TODAY_FLAG, mToday);

        Logs.add(Logs.Type.V, "outState: " + outState);
        super.onSaveInstanceState(outState);
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
