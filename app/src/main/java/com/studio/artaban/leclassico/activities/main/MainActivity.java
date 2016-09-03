package com.studio.artaban.leclassico.activities.main;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.connection.DataService;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.helpers.Glider;
import com.studio.artaban.leclassico.helpers.Logs;

import com.studio.artaban.leclassico.helpers.QueryLoader;
import com.studio.artaban.leclassico.helpers.Storage;

import java.io.File;

/**
 * Created by pascal on 08/08/16.
 * Main activity class
 */
public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, QueryLoader.OnResultListener,
        SearchView.OnQueryTextListener {

    public static final String EXTRA_DATA_KEY_ONLINE = "online";
    public static final String EXTRA_DATA_KEY_PSEUDO = "pseudo";
    // Extra data keys















    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber + 1);
            fragment.setArguments(args);
            return fragment;
        }

        //////
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
























    //
    private int mShortcutWidth = Constants.NO_DATA; // Shortcut fragment width
    private int mShortcutHeight = Constants.NO_DATA; // Shortcut fragment height

    private void updateShortcut(int section) {
    // Display & position shortcut according selected section

        Logs.add(Logs.Type.V, "section: " + section);
        FragmentManager manager = getSupportFragmentManager();
        switch (section) {

            case 0: { ////// Home
                manager.beginTransaction().replace(R.id.shortcut_center, new ShortcutFragment(),
                        ShortcutFragment.TAG_HOME).commit();
                manager.beginTransaction().replace(R.id.shortcut_right, new ShortcutFragment(),
                        ShortcutFragment.TAG_PUBLICATIONS).commit();
                break;
            }
            case 1: { ////// Publications
                manager.beginTransaction().replace(R.id.shortcut_left, new ShortcutFragment(),
                        ShortcutFragment.TAG_HOME).commit();
                manager.beginTransaction().replace(R.id.shortcut_center, new ShortcutFragment(),
                        ShortcutFragment.TAG_PUBLICATIONS).commit();
                manager.beginTransaction().replace(R.id.shortcut_right, new ShortcutFragment(),
                        ShortcutFragment.TAG_EVENTS).commit();
                break;
            }
            case 2: { ////// Events



                break;
            }
            case 3: { ////// Members



                break;
            }
            case 4: { ////// Notifications



                break;
            }
        }
        manager.executePendingTransactions();

        if (mShortcutWidth == Constants.NO_DATA) {
        // Get shortcut fragment width & height (if not already done)

            final View shortcut = findViewById(R.id.shortcut);
            shortcut.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                        @Override
                        public void onGlobalLayout() {
                            Logs.add(Logs.Type.V, null);

                            shortcut.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            mShortcutWidth = shortcut.getWidth();
                            mShortcutHeight = shortcut.getHeight();

                            findViewById(R.id.shortcut_left).setTranslationX(-mShortcutWidth);
                            findViewById(R.id.shortcut_right).setTranslationX(mShortcutWidth);
                            findViewById(R.id.shortcut_new).setTranslationY(-mShortcutHeight);
                        }
                    });

        } else {
            findViewById(R.id.shortcut_left).setTranslationX(-mShortcutWidth);
            findViewById(R.id.shortcut_center).setTranslationX(0);
            findViewById(R.id.shortcut_right).setTranslationX(mShortcutWidth);
        }
    }

    ////// OnQueryTextListener /////////////////////////////////////////////////////////////////////
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);

        cursor.moveToFirst();
        switch ((byte)id) {
            case Tables.ID_CAMARADES: { ////// User info

                // Get DB data
                final boolean female = (!cursor.isNull(COLUMN_INDEX_SEXE)) &&
                        (cursor.getInt(COLUMN_INDEX_SEXE) == CamaradesTable.FEMALE);
                final String profile = (!cursor.isNull(COLUMN_INDEX_PROFILE))?
                        cursor.getString(COLUMN_INDEX_PROFILE) : null;
                final String banner = (!cursor.isNull(COLUMN_INDEX_BANNER))?
                        cursor.getString(COLUMN_INDEX_BANNER) : null;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Logs.add(Logs.Type.V, null);
                        View navHeader = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);

                        // Profile
                        if (profile != null)
                            Glider.with(MainActivity.this)
                                    .load(Storage.FOLDER_PROFILES +
                                                    File.separator + profile,
                                            Constants.APP_URL_PROFILES + "/" + profile)
                                    .placeholder((female) ? R.drawable.woman : R.drawable.man)
                                    .into((ImageView) navHeader.findViewById(R.id.image_profile),
                                            new Glider.OnLoadListener() {

                                        @Override
                                        public boolean setResource(Bitmap resource, ImageView imageView) {
                                            //Logs.add(Logs.Type.V, "resource: " + resource +
                                            //        ";imageView: " + imageView);

                                            RoundedBitmapDrawable radiusBmp =
                                                    RoundedBitmapDrawableFactory.create(getResources(),
                                                            resource);
                                            TypedValue radius = new TypedValue();
                                            getResources().getValue(R.dimen.profile_radius, radius, true);
                                            radiusBmp.setCornerRadius(radius.getFloat());
                                            imageView.setImageDrawable(radiusBmp);
                                            return true;
                                        }
                                    });
                        else
                            ((ImageView) navHeader.findViewById(R.id.image_profile))
                                    .setImageDrawable(getDrawable((female) ?
                                            R.drawable.woman : R.drawable.man));

                        // Banner
                        if (banner != null)
                            Glider.with(MainActivity.this)
                                    .load(Storage.FOLDER_PROFILES +
                                                    File.separator + banner,
                                            Constants.APP_URL_PROFILES + "/" + banner)
                                    .placeholder(R.drawable.banner)
                                    .into((ImageView) navHeader.findViewById(R.id.image_banner), null);
                    }

                });
                break;
            }
            case Tables.ID_NOTIFICATIONS: { ////// Notifications






                //mNotify = true;
                //invalidateOptionsMenu();







                break;
            }
        }
    }

    @Override
    public void onLoaderReset() {

    }

    private static final byte COLUMN_INDEX_SEXE = 0;
    private static final byte COLUMN_INDEX_PROFILE = 1;
    private static final byte COLUMN_INDEX_BANNER = 2;
    // Query column index

    private final QueryLoader mUserLoader = new QueryLoader(this, this); // User data query loader
    private final QueryLoader mNotificationLoader = new QueryLoader(this, this); // Notification data query loader

    ////// OnNavigationItemSelectedListener ////////////////////////////////////////////////////////
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Logs.add(Logs.Type.V, "item: " + item);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null)
            drawer.closeDrawer(GravityCompat.START);

        mNavItemSelected = item.getItemId();
        // Let's drawer close event do the job (more efficient)

        return true;
    }

    //
    private int mNavItemSelected = Constants.NO_DATA; // Id of the selected navigation item (or -1 if none)
    private void onSelectNavItem() {

        Logs.add(Logs.Type.V, "mNavItemSelected: " + mNavItemSelected);
        switch (mNavItemSelected) {
            case R.id.navig_profile: { // Display user profile








                break;
            }
            case R.id.navig_location: { // Display location activity








                break;
            }
            case R.id.navig_settings: { // Display settings







                break;
            }
            case R.id.navig_logout: { // Logout

                // Confirm logout
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.question_purple)
                        .setTitle(R.string.confirm)
                        .setMessage(R.string.confirm_logout)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Logs.add(Logs.Type.V, "dialog: " + dialog + ";which: " + which);
                                if (which == DialogInterface.BUTTON_POSITIVE) {

                                    DataService.stop(MainActivity.this);
                                    MainActivity.this.supportFinishAfterTransition();
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show();
                break;
            }
            case R.id.navig_quit: { // Quit application

                // Confirm quit
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.question_red)
                        .setTitle(R.string.confirm)
                        .setMessage(R.string.confirm_quit)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Logs.add(Logs.Type.V, "dialog: " + dialog + ";which: " + which);
                                if (which == DialogInterface.BUTTON_POSITIVE) {

                                    DataService.stop(MainActivity.this);
                                    MainActivity.this.finishAffinity();
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show();
                break;
            }
        }
        mNavItemSelected = Constants.NO_DATA;
    }

    ////// AppCompatActivity ///////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set tool & app bars
        ((AppBarLayout)findViewById(R.id.appbar)).addOnOffsetChangedListener(
                new AppBarLayout.OnOffsetChangedListener() {

                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                        //Logs.add(Logs.Type.V, "appBarLayout: " + appBarLayout +
                        //        ";verticalOffset: " + verticalOffset);
                        if (appBarLayout.findViewById(R.id.shortcut) != null)
                            appBarLayout.findViewById(R.id.shortcut).setTranslationY(verticalOffset);
                        //else // NB: Can occur when search operation starts
                    }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set drawer toggle
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                onSelectNavItem();
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Set navigation drawer
        NavigationView navigation = (NavigationView) findViewById(R.id.nav_view);
        navigation.setNavigationItemSelectedListener(this);
        navigation.setItemTextAppearance(R.style.NavDrawerTextStyle);
        navigation.setItemTextColor(new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_enabled},
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_pressed}
                },
                new int[]{Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK}));
        navigation.setItemIconTintList(null);
        navigation.getMenu().findItem(R.id.navig_profile).getIcon()
                .setColorFilter(getResources().getColor(R.color.colorPrimaryProfile), PorterDuff.Mode.SRC_ATOP);
        navigation.getMenu().findItem(R.id.navig_location).getIcon()
                .setColorFilter(getResources().getColor(R.color.colorPrimaryLocation), PorterDuff.Mode.SRC_ATOP);
        navigation.getMenu().findItem(R.id.navig_settings).getIcon()
                .setColorFilter(getResources().getColor(R.color.colorPrimarySetting), PorterDuff.Mode.SRC_ATOP);
        navigation.getMenu().findItem(R.id.navig_logout).getIcon()
                .setColorFilter(getResources().getColor(R.color.colorPrimaryMain), PorterDuff.Mode.SRC_ATOP);
        navigation.getMenu().findItem(R.id.navig_quit).getIcon()
                .setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

        // Display user pseudo, profile icon & banner
        String pseudo = getIntent().getStringExtra(EXTRA_DATA_KEY_PSEUDO);
        ((TextView)navigation.getHeaderView(0).findViewById(R.id.text_pseudo)).setText(pseudo);

        // Load user & notification data (using query loaders)
        Bundle userData = new Bundle();
        userData.putBoolean(QueryLoader.DATA_KEY_URI_SINGLE, false);
        userData.putStringArray(QueryLoader.DATA_KEY_PROJECTION, new String[]{

                CamaradesTable.COLUMN_SEXE, // COLUMN_INDEX_SEXE
                CamaradesTable.COLUMN_PROFILE, // COLUMN_INDEX_PROFILE
                CamaradesTable.COLUMN_BANNER // COLUMN_INDEX_BANNER
        });
        userData.putString(QueryLoader.DATA_KEY_SELECTION, CamaradesTable.COLUMN_PSEUDO + "='" + pseudo + "'");
        mUserLoader.restart(this, Tables.ID_CAMARADES, userData);

        Bundle notificationData = new Bundle();
        notificationData.putBoolean(QueryLoader.DATA_KEY_URI_SINGLE, false);
        notificationData.putStringArray(QueryLoader.DATA_KEY_PROJECTION, new String[]{"count(*)"});
        notificationData.putString(QueryLoader.DATA_KEY_SELECTION, NotificationsTable.COLUMN_LU_FLAG + "=0");
        mNotificationLoader.restart(this, Tables.ID_NOTIFICATIONS, notificationData);

        // Set content view pager
        final ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return PlaceholderFragment.newInstance(position);
            }

            @Override
            public int getCount() {
                return Constants.MAIN_PAGE_COUNT;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                String[] pages = getResources().getStringArray(R.array.main_pages);
                return pages[position];
            }
        });
        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {

            private void animShortcut(boolean toTheLeft, float position) {
            // Animate shortcut according section displayed and its position

                //Logs.add(Logs.Type.V, "toTheLeft: " + toTheLeft + ";position: " + position);
                if (((toTheLeft) && (position < 0f)) || (((!toTheLeft) && (position < 1f)))) {

                    findViewById(R.id.shortcut_left).setTranslationX(-mShortcutWidth + (position * mShortcutWidth));
                    findViewById(R.id.shortcut_center).setTranslationX(position * mShortcutWidth);
                    findViewById(R.id.shortcut_right).setTranslationX(mShortcutWidth + (position * mShortcutWidth));
                }
            }

            @Override
            public void transformPage(View page, float position) {

                if (position < 0f) { // This page is moving out to the left
                    animShortcut(true, position);
                } else if (position < 1f) { // This page is moving in from the right
                    animShortcut(false, position);
                }
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Logs.add(Logs.Type.V, "position: " + position);
                updateShortcut(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Set tabulation layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(
                (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ?
                        TabLayout.MODE_SCROLLABLE : TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logs.add(Logs.Type.V, null);
        updateShortcut(((ViewPager) findViewById(R.id.container)).getCurrentItem());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Logs.add(Logs.Type.V, null);
        getMenuInflater().inflate(R.menu.options_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.mnu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));






        //if (mNotify)
        //    menu.getItem(1).setIcon(getDrawable(R.drawable.ic_notifications_info_24dp));






        return true;
    }

    @Override
    public void onBackPressed() {
        Logs.add(Logs.Type.V, null);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if ((drawer != null) && (drawer.isDrawerOpen(GravityCompat.START)))
            drawer.closeDrawer(GravityCompat.START); // Close drawer
        else
            moveTaskToBack(true); // Put application in pause
    }
}
