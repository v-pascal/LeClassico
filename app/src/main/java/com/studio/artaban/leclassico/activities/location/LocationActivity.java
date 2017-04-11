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
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.codes.Preferences;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Requests;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.codes.Uris;
import com.studio.artaban.leclassico.data.tables.AbonnementsTable;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.data.tables.LocationsTable;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.QueryLoader;
import com.studio.artaban.leclassico.services.DataService;
import com.studio.artaban.leclassico.tools.Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pascal on 22/11/16.
 * Location activity
 */
public class LocationActivity extends LoggedActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, DataObserver.OnContentListener {

    // Extra data keys (see 'LoggedActivity' & 'Login' extra data keys)

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
    private final Map<Integer, Marker> mMarkers = new HashMap<>(); // Markers list (Member Id -> Marker)

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
    private enum TodayFilter { ENABLING, ENABLED, DISABLING, DISABLED }
    private TodayFilter mToday = TodayFilter.DISABLED; // Today filter flag
    private boolean mSelected; // Member selection flag

    private void selectMember(Marker marker) { // Select member info (from marker data tag)
        Logs.add(Logs.Type.V, "marker: " + marker);

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
    }

    //////
    public void onSearch(View sender) { // On search member requested
        Logs.add(Logs.Type.V, "sender: " + sender);

        ////// Start pick member activity
        Intent intent = new Intent(Intent.ACTION_PICK, Uris.getUri(Uris.ID_PICK_MEMBER, mPseudo));
        startActivityForResult(intent, Requests.PICK_MEMBER.CODE);
        // NB: Activity could be called explicitly
    }
    public void onToday(View sender) { // On today criteria change
        Logs.add(Logs.Type.V, "sender: " + sender);
        switch (mToday) {
            case ENABLED: {
                mToday = TodayFilter.DISABLING;
                break;
            }
            case DISABLED: {
                mToday = TodayFilter.ENABLING;
                break;
            }
            default:
                break; // Too fast click event
        }
        ((ImageView)findViewById(R.id.image_today))
                .setImageDrawable(getDrawable((mToday == TodayFilter.ENABLING) ?
                    R.drawable.ic_today_white_36dp : R.drawable.ic_no_today_white_36dp));
        onMapClick(null);
        Toast.makeText(this, (mToday == TodayFilter.ENABLING)?
                        R.string.location_today_filter:R.string.location_no_filter,
                Toast.LENGTH_SHORT).show(); // Display filter info

        refresh(); // Refresh map (with today filter)
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
        selectMember(marker);
        return false;
    }

    //////
    private static class MarkerInfo {
        public MarkerInfo(int id, String profile, boolean female, String pseudo,
                          String info, @ColorRes int color, double latitude, double longitude) {

            Logs.add(Logs.Type.V, "id: " + id + ";profile: " + profile + ";female: " + female +
                    ";pseudo: " + pseudo + ";info: " + info + ";color: " + color + ";latitude: " + latitude +
                    ";longitude: " + longitude);
            this.id = id;
            this.profile = profile;
            this.female = female;
            this.pseudo = pseudo;
            this.info = info;
            this.color = color;

            this.latitude = latitude;
            this.longitude = longitude;
        }
        public final int id;
        public final String profile;
        public final boolean female;
        public final String pseudo;
        public final String info;
        public final @ColorRes int color;

        public final double latitude;
        public final double longitude;
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

        mCursor = cursor;
        refresh();
    }

    @Override
    public void onLoaderReset() {
        Logs.add(Logs.Type.V, null);
        mCursor = null;
    }

    //////
    private Uri mFollowersUri; // Followers location URI
    private Uri mUserUri; // Connected user URI (manage location share change)
    private int mMemberId = Constants.NO_DATA; // ID of the member to locate

    private final QueryLoader mFollowers = new QueryLoader(this, this); // Followers list query loader
    private DataObserver mUserObserver; // User info DB update observer (observe remote DB updates)
    private Cursor mCursor; // Followers location cursor

    private void refresh() { // Refresh followers location query (markers)
        Logs.add(Logs.Type.V, null);
        mCursor.moveToFirst();

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.FORMAT_DATE_TIME);
        String today = dateFormat.format(new Date()).substring(0, 10);

        // Check member selection
        if (mMemberId != Constants.NO_DATA) {
            Double memberLat = null, memberLng = null;
            do {
                if (mMemberId == mCursor.getInt(COLUMN_INDEX_ID)) {
                    memberLat = mCursor.getDouble(COLUMN_INDEX_LATITUDE);
                    memberLng = mCursor.getDouble(COLUMN_INDEX_LONGITUDE);

                    mMarkers.get(mMemberId).showInfoWindow();
                    selectMember(mMarkers.get(mMemberId));
                }

            } while (mCursor.moveToNext());
            mCursor.moveToFirst();

            // Zoom on marker if member location filter is activated
            if (memberLat != null)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(memberLat, memberLng), 18));
            else if (mMemberId != Constants.NO_DATA)
                Toast.makeText(this, R.string.location_member_none, Toast.LENGTH_SHORT).show();
            mMemberId = Constants.NO_DATA; // Remove selection

        } else if ((mToday == TodayFilter.ENABLING) || (mToday == TodayFilter.DISABLING)) { // Check today filter
            do {
                String date = mCursor.getString(COLUMN_INDEX_STATUS_DATE);
                if (mToday == TodayFilter.ENABLING) {
                    if (!date.startsWith(today)) // Apply today filter
                        mMarkers.get(mCursor.getInt(COLUMN_INDEX_ID)).setVisible(false);
                } else // Disable today filter
                    mMarkers.get(mCursor.getInt(COLUMN_INDEX_ID)).setVisible(true);

            } while (mCursor.moveToNext());
            mCursor.moveToFirst();

            mToday = (mToday == TodayFilter.ENABLING)? TodayFilter.ENABLED:TodayFilter.DISABLED;

        } else {
            mMarkers.clear();
            mMap.clear(); // Remove all previous markers

            do {
                if (mPseudo.compareTo(mCursor.getString(COLUMN_INDEX_PSEUDO)) == 0)
                    continue; // Do not display user location (with common marker)
                // NB: Let user locate himself using the locate option

                String date = mCursor.getString(COLUMN_INDEX_STATUS_DATE);
                StringBuilder title = new StringBuilder();
                try {
                    Date locationDate = dateFormat.parse(date);
                    title.append(DateFormat.getDateFormat(this).format(locationDate));
                    title.append(' ');
                    title.append(DateFormat.getTimeFormat(this).format(locationDate));

                } catch (ParseException e) {
                    Logs.add(Logs.Type.E, "Wrong location date format: " + date);
                    title.append(date);
                }
                int colorIdx = LeClassicoApp.getRandom().nextInt(MARKER_COLORS.length);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(MARKER_COLORS[colorIdx]))
                        .title(title.toString())
                        .position(new LatLng(mCursor.getDouble(COLUMN_INDEX_LATITUDE),
                                mCursor.getDouble(COLUMN_INDEX_LONGITUDE))));

                // Add marker info
                boolean female = (!mCursor.isNull(COLUMN_INDEX_SEXE)) &&
                        (mCursor.getInt(COLUMN_INDEX_SEXE) == CamaradesTable.GENDER_FEMALE);
                String profile = (!mCursor.isNull(COLUMN_INDEX_PROFILE)) ?
                        mCursor.getString(COLUMN_INDEX_PROFILE) : null;

                marker.setTag(new MarkerInfo(mCursor.getInt(COLUMN_INDEX_ID), profile, female,
                        mCursor.getString(COLUMN_INDEX_PSEUDO),
                        Tools.getUserInfo(getResources(), DataTable.getDataType(mCursor),
                                Constants.NO_DATA, COLUMN_INDEX_PHONE),
                        MEMBER_COLORS[colorIdx], mCursor.getDouble(COLUMN_INDEX_LATITUDE),
                        mCursor.getDouble(COLUMN_INDEX_LONGITUDE)));

                // Add marker into list
                mMarkers.put(mCursor.getInt(COLUMN_INDEX_ID), marker);

            } while (mCursor.moveToNext());
            mCursor.moveToFirst();

            // Add user location (if it was requested)
            if (mUserLocation != null)
                displayUserLocation();
            // NB: Needed coz the map clear causes to remove all marker even user location marker
        }
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
    private static final int COLUMN_INDEX_LONGITUDE = 11;
    private static final int COLUMN_INDEX_STATUS_DATE = 12;

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
        int pseudoId = Preferences.getInt(Preferences.SETTINGS_LOGIN_PSEUDO_ID);
        mFollowersUri = Uris.getUri(Uris.ID_USER_LOCATION, String.valueOf(pseudoId));
        mUserUri = Uris.getUri(Uris.ID_USER_MEMBERS, String.valueOf(pseudoId));

        mUserObserver = new DataObserver(new Handler(Looper.getMainLooper()), this);

        // Initialize locations loader
        Bundle followData = new Bundle();
        followData.putParcelable(QueryLoader.DATA_KEY_URI, mFollowersUri);
        followData.putString(QueryLoader.DATA_KEY_SELECTION,
                "SELECT " +
                        CamaradesTable.TABLE_NAME + '.' + DataTable.DataField.COLUMN_ID + ',' +
                        CamaradesTable.COLUMN_PSEUDO + ',' +
                        CamaradesTable.COLUMN_SEXE + ',' +
                        CamaradesTable.COLUMN_PROFILE + ',' +
                        Tools.getUserInfoFields() + ',' +
                        LocationsTable.COLUMN_LATITUDE + ',' +
                        LocationsTable.COLUMN_LONGITUDE + ',' +
                        LocationsTable.TABLE_NAME + '.' + Constants.DATA_COLUMN_STATUS_DATE +
                        " FROM " + CamaradesTable.TABLE_NAME +
                        " INNER JOIN " + AbonnementsTable.TABLE_NAME + " ON " +
                        AbonnementsTable.COLUMN_PSEUDO + "='" + mPseudo + "' AND " +
                        AbonnementsTable.COLUMN_CAMARADE + '=' + CamaradesTable.COLUMN_PSEUDO +
                        " INNER JOIN " + LocationsTable.TABLE_NAME + " ON " +
                        LocationsTable.COLUMN_PSEUDO + '=' + CamaradesTable.COLUMN_PSEUDO +
                        " WHERE " +
                        DataTable.getNotDeletedCriteria(CamaradesTable.TABLE_NAME) + " AND " +
                        CamaradesTable.COLUMN_DEVICE_ID + " IS NOT NULL");

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
            mMemberId = Integer.valueOf(data.getData().getLastPathSegment());

            Logs.add(Logs.Type.I, "Member picked: #" + mMemberId);
            refresh(); // Implement selection (zoom)
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
