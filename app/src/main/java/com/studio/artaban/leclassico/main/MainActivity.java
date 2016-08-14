package com.studio.artaban.leclassico.main;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Requests;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.helpers.Logs;

import com.bumptech.glide.Glide;
import com.studio.artaban.leclassico.helpers.QueryLoader;

/**
 * Created by pascal on 08/08/16.
 * Main activity class
 */
public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, QueryLoader.OnResultListener {

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
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //////
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
    }

    //
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;














    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {

        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);
        switch (id) {
            case Tables.ID_CAMARADES: { ////// User info






                /*
                ((ImageView)navigation.getHeaderView(0).findViewById(R.id.image_banner))
                        .setImageDrawable(getDrawable(R.drawable.banner));
                ((ImageView)navigation.getHeaderView(0).findViewById(R.id.image_profile))
                        .setImageDrawable(getDrawable(R.drawable.man));



                Glide.with(context)
                    .load(url)
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.banner)
                    .into(new BitmapImageViewTarget(navigation.getHeaderView(0).findViewById(R.id.image_banner)) {

                    @Override
                    protected void setResource(Bitmap resource) {



                        RoundedBitmapDrawable radiusBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        radiusBitmapDrawable.setCornerRadius(0.25f);
                        imageView.setImageDrawable(radiusBitmapDrawable);
                    }
                });
                */






                break;
            }
            case Tables.ID_NOTIFICATIONS: { ////// Notifications



                break;
            }
        }
    }

    @Override
    public void onLoaderReset() {

    }

    //
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

                setResult(Requests.MAIN_TO_INTRO.RESULT_LOGOUT);
                supportFinishAfterTransition();
                break;
            }
            case R.id.navig_quit: { // Quit application

                setResult(Requests.MAIN_TO_INTRO.RESULT_QUIT);
                supportFinishAfterTransition();
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

        // Set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        navigation.setItemIconTintList(null);
        navigation.getMenu().findItem(R.id.navig_profile).getIcon()
                .setColorFilter(getResources().getColor(R.color.colorAccentProfile), PorterDuff.Mode.SRC_ATOP);
        navigation.getMenu().findItem(R.id.navig_location).getIcon()
                .setColorFilter(getResources().getColor(R.color.colorAccentLocation), PorterDuff.Mode.SRC_ATOP);
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

                CamaradesTable.COLUMN_SEXE,
                CamaradesTable.COLUMN_PROFILE,
                CamaradesTable.COLUMN_BANNER
        });
        userData.putString(QueryLoader.DATA_KEY_SELECTION, "CAM_Pseudo = '" + pseudo + "'");
        mUserLoader.init(this, Tables.ID_CAMARADES, userData);

        Bundle notificationData = new Bundle();
        notificationData.putBoolean(QueryLoader.DATA_KEY_URI_SINGLE, false);
        notificationData.putStringArray(QueryLoader.DATA_KEY_PROJECTION, new String[]{ "count(*)" });
        notificationData.putString(QueryLoader.DATA_KEY_SELECTION, NotificationsTable.COLUMN_LU_FLAG + "=0");
        mNotificationLoader.init(this, Tables.ID_NOTIFICATIONS, notificationData);













        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });











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
