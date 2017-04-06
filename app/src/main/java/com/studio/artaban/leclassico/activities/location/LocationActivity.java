package com.studio.artaban.leclassico.activities.location;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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
import com.studio.artaban.leclassico.activities.settings.PrefsLocationFragment;
import com.studio.artaban.leclassico.animations.InOutScreen;
import com.studio.artaban.leclassico.connection.Login;
import com.studio.artaban.leclassico.connection.requests.CamaradesRequest;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataObserver;
import com.studio.artaban.leclassico.data.codes.Preferences;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Requests;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;
import com.studio.artaban.leclassico.services.DataService;
import com.studio.artaban.leclassico.tools.Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pascal on 22/11/16.
 * Location activity
 */
public class LocationActivity extends LoggedActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, DataObserver.OnContentListener {

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
    private GoogleApiClient mClient; // Google API client

    private LatLng mUserLocation; // User location coordinates
    private Marker mUserMarker; // User location marker

    private void locate() { // Locate connected user
        Logs.add(Logs.Type.V, null);
        try {

            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
            if (lastLocation != null) {
                mUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                displayUserLocation();

            } else
                Toast.makeText(this, R.string.location_user_failed, Toast.LENGTH_LONG).show();

        } catch (SecurityException e) {
            Logs.add(Logs.Type.E, "Location failed: " + e.getMessage());
            Toast.makeText(this, R.string.location_user_failed, Toast.LENGTH_LONG).show();
            mUserLocation = null;
        }
    }
    private void displayUserLocation() { // Display & move camera to user location marker
        Logs.add(Logs.Type.V, null);
        if (mUserMarker != null)
            mUserMarker.remove();

        mUserMarker = mMap.addMarker(new MarkerOptions()
                .position(mUserLocation)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location_black_24dp)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mUserLocation, 18));
    }
    private String mPseudo; // Connected user pseudo
    private boolean mToday; // Today filter flag
    private boolean mSelected; // Member selection flag

    //////
    public void onSearch(View sender) { // On search member requested
        Logs.add(Logs.Type.V, "sender: " + sender);

        ////// Start pick member activity
        Intent intent = new Intent(Intent.ACTION_PICK, Uris.getUri(Uris.ID_PICK_MEMBER));
        startActivityForResult(intent, Requests.PICK_MEMBER.CODE);
        // NB: Activity could be called explicitly
    }
    public void onToday(View sender) { // On today criteria change
        Logs.add(Logs.Type.V, "sender: " + sender);
        mToday = !mToday;

        ((ImageView)findViewById(R.id.image_today)).setImageDrawable(getDrawable((mToday)?
                R.drawable.ic_today_white_36dp : R.drawable.ic_no_today_white_36dp));
        onMapClick(null);

        refresh(null); // Refresh query
        Toast.makeText(this, (mToday)? R.string.location_today_filter:R.string.location_no_filter,
                Toast.LENGTH_SHORT).show(); // Display filter info
    }
    public void onShare(View sender) { // On user location share set
        Logs.add(Logs.Type.V, "sender: " + sender);

        final boolean share = Preferences.getString(Preferences.SETTINGS_LOCATION_DEVICE_ID) != null;
        PrefsLocationFragment.set(this, !share,
                new PrefsLocationFragment.OnSetResult() {
                    @Override
                    public void onSet(boolean newShare) {
                        Logs.add(Logs.Type.V, "newShare: " + newShare);
                        if (share == newShare)
                            return; // No change

                        ((ImageView)findViewById(R.id.image_share))
                                .setImageDrawable(getDrawable((newShare) ?
                                        R.drawable.ic_location_on_white_36dp :
                                        R.drawable.ic_location_off_white_36dp));

                        // Inform user share location changed
                        Toast.makeText(LocationActivity.this, (newShare)?
                                R.string.location_share_enabled:R.string.location_share_disabled,
                                Toast.LENGTH_SHORT).show();
                    }
                }, mUserUri, mUserObserver);
    }
    public void onLocate(View sender) { // On connected user location requested
        Logs.add(Logs.Type.V, "sender: " + sender);
        locate();
    }

    ////// ConnectionCallbacks /////////////////////////////////////////////////////////////////////
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Logs.add(Logs.Type.V, "bundle: " + bundle);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Logs.add(Logs.Type.V, "cause: " + cause);
        mClient.connect();
    }

    ////// OnConnectionFailedListener //////////////////////////////////////////////////////////////
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Logs.add(Logs.Type.E, "connectionResult: " + connectionResult);
    }

    ////// OnMarkerClickListener ///////////////////////////////////////////////////////////////////
    @Override
    public boolean onMarkerClick(Marker marker) {
        Logs.add(Logs.Type.V, "marker: " + marker);
        if (marker.getTag() == null)
            return false; // User location marker

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

    ////// OnContentListener ///////////////////////////////////////////////////////////////////////
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Logs.add(Logs.Type.V, "selfChange: " + selfChange + ";uri: " + uri);

        // Update location share option (in case of change)
        ((ImageView)findViewById(R.id.image_share))
                .setImageDrawable(getDrawable((Preferences.getString(Preferences.SETTINGS_LOCATION_DEVICE_ID) != null) ?
                        R.drawable.ic_location_on_white_36dp : R.drawable.ic_location_off_white_36dp));
    }

    ////// LoggedActivity //////////////////////////////////////////////////////////////////////////
    @Override
    protected void onLoggedResume() {
        Logs.add(Logs.Type.V, null);

        // Register DB observer
        mUserObserver.register(getContentResolver(), mUserUri);

        // Register data service (user info)
        Intent userIntent = DataService.getIntent(true, Tables.ID_CAMARADES, mUserUri);
        userIntent.putExtra(CamaradesRequest.EXTRA_DATA_PSEUDO, mPseudo);
        sendBroadcast(userIntent);

        sendBroadcast(DataService.getIntent(true, Tables.ID_ABONNEMENTS, mFollowersUri));


        // TODO: Register location data service


    }

    //////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        if ((!cursor.moveToFirst()) || (onNotifyLoadFinished(id, cursor)))
            return;

        if (id != Queries.LOCATION_FOLLOWERS)
            throw new IllegalArgumentException("Unexpected query ID");

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.FORMAT_DATE_TIME);
        String today = dateFormat.format(new Date()).substring(0, 10);

        mMap.clear(); // Remove all previous markers
        do {
            if (mPseudo.compareTo(cursor.getString(COLUMN_INDEX_PSEUDO)) == 0)
                continue; // Do not display user location (with common marker)
                // NB: Let user locate himself using the locate option

            String date = (cursor.getString(COLUMN_INDEX_LATITUDE_UPD)
                    .compareTo(cursor.getString(COLUMN_INDEX_LONGITUDE_UPD)) > 0)?
                    cursor.getString(COLUMN_INDEX_LATITUDE_UPD) :
                    cursor.getString(COLUMN_INDEX_LONGITUDE_UPD);
            if ((mToday) && (!date.startsWith(today))) // Apply today filter
                continue;

            int colorIdx = LeClassicoApp.getRandom().nextInt(MARKER_COLORS.length);
            StringBuilder title = new StringBuilder();
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

        // Add user location (if it was requested)
        if (mUserLocation != null)
            displayUserLocation();
        // NB: Needed coz the map clear causes to remove all marker even user location marker
    }

    @Override
    public void onLoaderReset() {

    }

    //////
    private Uri mFollowersUri; // Followers location URI
    private Uri mUserUri; // Connected user URI
    private final QueryLoader mFollowers = new QueryLoader(this, this); // Followers list query loader
    private DataObserver mUserObserver; // User info DB update observer (observe remote DB updates)

    private void refresh(@Nullable Long memberId) { // Refresh followers location query (markers)
        Logs.add(Logs.Type.V, "memberId: " + memberId);

        Bundle followData = new Bundle();
        followData.putParcelable(QueryLoader.DATA_KEY_URI, mFollowersUri);
        if (memberId != null)
            followData.putLong(QueryLoader.DATA_KEY_URI_FILTER, memberId);
        mFollowers.restart(this, Queries.LOCATION_FOLLOWERS, followData);
    }

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
        if (getIntent().getData() != null)
            mPseudo = Uri.decode(getIntent().getData().getPathSegments().get(1));
        else
            mPseudo = getIntent().getStringExtra(Login.EXTRA_DATA_PSEUDO);
        Logs.add(Logs.Type.I, "Pseudo: " + mPseudo);

        // Set map & API client
        SupportMapFragment map = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        if (map == null) {
            map = new SupportMapFragment();
            map.getMapAsync(this);
            getSupportFragmentManager().beginTransaction().add(R.id.map, map).commit();

        } else
            map.getMapAsync(this);

        mClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Position & set control panel
        ((RelativeLayout.LayoutParams) findViewById(R.id.layout_panel).getLayoutParams())
                .setMargins(0, Tools.getStatusBarHeight(getResources()) + Tools.getActionBarHeight(this), 0, 0);
        ((ImageView)findViewById(R.id.image_share))
                .setImageDrawable(getDrawable((Preferences.getString(Preferences.SETTINGS_LOCATION_DEVICE_ID) != null) ?
                        R.drawable.ic_location_on_white_36dp : R.drawable.ic_location_off_white_36dp));

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

        // Set followers & user URI
        mFollowersUri = Uris.getUri(Uris.ID_USER_LOCATION, mPseudo);
        mUserUri = Uris.getUri(Uris.ID_USER_MEMBERS,
                String.valueOf(Preferences.getInt(Preferences.SETTINGS_LOGIN_PSEUDO_ID)));

        mUserObserver = new DataObserver(new Handler(Looper.getMainLooper()), this);

        // Initialize locations loader
        Bundle followData = new Bundle();
        followData.putParcelable(QueryLoader.DATA_KEY_URI, mFollowersUri);
        mFollowers.init(this, Queries.LOCATION_FOLLOWERS, followData);
    }

    @Override
    protected void onStart() {
        Logs.add(Logs.Type.V, null);
        mClient.connect();
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logs.add(Logs.Type.V, "requestCode: " + requestCode + ";resultCode: " + resultCode + ";data: " + data);

        if ((requestCode == Requests.PICK_MEMBER.CODE) && (resultCode == RESULT_OK)) {
            if (!data.hasExtra(Requests.PICK_MEMBER.EXTRA_DATA_ID))
                throw new IllegalArgumentException("Unexpected member pick activity result");

            Logs.add(Logs.Type.I, "Member picked: #" + data.getIntExtra(Requests.PICK_MEMBER.EXTRA_DATA_ID,
                    Constants.NO_DATA));









        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logs.add(Logs.Type.V, null);

        // Unregister data service (user info)
        sendBroadcast(DataService.getIntent(false, Tables.ID_CAMARADES, mUserUri));
        sendBroadcast(DataService.getIntent(false, Tables.ID_ABONNEMENTS, mFollowersUri));


        // TODO: Unregister location data service


        // Unregister DB observer
        mUserObserver.unregister(getContentResolver());
    }

    @Override
    protected void onStop() {
        Logs.add(Logs.Type.V, null);
        mClient.disconnect();
        super.onStop();
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
