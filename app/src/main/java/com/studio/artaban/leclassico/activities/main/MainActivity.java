package com.studio.artaban.leclassico.activities.main;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.connection.DataService;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.helpers.Glider;
import com.studio.artaban.leclassico.helpers.Logs;

import com.studio.artaban.leclassico.helpers.QueryLoader;
import com.studio.artaban.leclassico.helpers.Storage;
import com.studio.artaban.leclassico.tools.Tools;

import java.io.File;

/**
 * Created by pascal on 08/08/16.
 * Main activity class
 */
public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, QueryLoader.OnResultListener,
        MainFragment.OnFragmentListener {

    public static final String EXTRA_DATA_KEY_ONLINE = "online";
    public static final String EXTRA_DATA_KEY_PSEUDO = "pseudo";
    // Extra data keys

    private static int getShortcutID(int section) {
    // Return shortcut fragment container ID according selected section

        Logs.add(Logs.Type.V, null);
        switch (section) {
            case Constants.MAIN_SECTION_HOME: return R.id.shortcut_home;
            case Constants.MAIN_SECTION_PUBLICATIONS: return R.id.shortcut_publications;
            case Constants.MAIN_SECTION_EVENTS: return R.id.shortcut_events;
            case Constants.MAIN_SECTION_MEMBERS: return R.id.shortcut_members;
            case Constants.MAIN_SECTION_NOTIFICATIONS: return R.id.shortcut_notifications;
            default:
                throw new IllegalArgumentException("Unexpected section");
        }
    }

    ////// OnFragmentListener //////////////////////////////////////////////////////////////////////
    @Override
    public void onSetMessage(final int section, final SpannableStringBuilder message) {
        //Logs.add(Logs.Type.V, "section: " + section + ";message: " + message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Logs.add(Logs.Type.V, null);
                ((ShortcutFragment) getSupportFragmentManager()
                        .findFragmentById(getShortcutID(section))).setMessage(message);
            }
        });
    }
    @Override
    public void onSetInfo(final int section, final SpannableStringBuilder info) {
        //Logs.add(Logs.Type.V, "section: " + section + ";info: " + info);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Logs.add(Logs.Type.V, null);
                ((ShortcutFragment) getSupportFragmentManager()
                        .findFragmentById(getShortcutID(section))).setInfo(info);
            }
        });
    }
    @Override
    public void onSetIcon(final int section, final boolean female, final String profile, final int size) {
        //Logs.add(Logs.Type.V, "section: " + section + ";female: " + female + ";profile: " + profile);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Logs.add(Logs.Type.V, null);
                ((ShortcutFragment) getSupportFragmentManager()
                        .findFragmentById(getShortcutID(section))).setIcon(female, profile, size);
            }
        });
    }
    @Override
    public void onSetDate(final int section, final boolean start, final String dateTime) {
        //Logs.add(Logs.Type.V, "section: " + section + ";start: " + start + ";dateTime: " + dateTime);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Logs.add(Logs.Type.V, null);
                ((ShortcutFragment) getSupportFragmentManager()
                        .findFragmentById(getShortcutID(section))).setDate(start, dateTime);
            }
        });
    }

    @Override
    public void onSetNotify(final char type, final boolean read) {
        //Logs.add(Logs.Type.V, "type: " + type + ";read: " + read);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Logs.add(Logs.Type.V, null);
                ((ShortcutFragment) getSupportFragmentManager()
                        .findFragmentById(getShortcutID(Constants.MAIN_SECTION_NOTIFICATIONS)))
                        .setNotify(type, read);
            }
        });
    }

    //
    private boolean mNewNotification; // New notification flag

    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);

        cursor.moveToFirst();
        switch (id) {
            case Tables.ID_CAMARADES: { ////// User info

                // Get DB data
                final boolean female = (!cursor.isNull(COLUMN_INDEX_SEX)) &&
                        (cursor.getInt(COLUMN_INDEX_SEX) == CamaradesTable.FEMALE);
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
                        Tools.setProfile(MainActivity.this,
                                (ImageView)navHeader.findViewById(R.id.image_profile),
                                female, profile, R.dimen.profile_size, false);
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
            case Queries.MAIN_NOTIFICATION_FLAG: {

                Logs.add(Logs.Type.I, "New notification(s): " + cursor.getInt(0));
                if (cursor.getInt(0) > 0) {

                    mNewNotification = true; // New notification(s)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            invalidateOptionsMenu();
                        }
                    });
                }
                break;
            }
        }
        cursor.close();
    }

    @Override
    public void onLoaderReset() {

    }

    private static final byte COLUMN_INDEX_SEX = 0;
    private static final byte COLUMN_INDEX_PROFILE = 1;
    private static final byte COLUMN_INDEX_BANNER = 2;
    // Query column index

    private final QueryLoader mUserLoader = new QueryLoader(this, this); // User data query loader
    private final QueryLoader mNewNotifyLoader = new QueryLoader(this, this); // New notification query loader

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

    //////
    private ViewPager mViewPager; // Content view pager

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

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            // Display shortcut below action bar title & icons
            findViewById(R.id.text_title).setVisibility(View.VISIBLE);

            Point screenSize = new Point();
            getWindowManager().getDefaultDisplay().getSize(screenSize);
            int margin = getResources().getDimensionPixelSize(R.dimen.shortcut_margin_horizontal);

            View toolbarLayout = findViewById(R.id.layout_toolbar);
            toolbarLayout.setTranslationX(margin - getResources().getDimensionPixelSize(R.dimen.appbar_padding_title));
            toolbarLayout.getLayoutParams().width = screenSize.x -(margin << 1);
        }

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

                CamaradesTable.COLUMN_SEXE, // COLUMN_INDEX_SEX
                CamaradesTable.COLUMN_PROFILE, // COLUMN_INDEX_PROFILE
                CamaradesTable.COLUMN_BANNER // COLUMN_INDEX_BANNER
        });
        userData.putString(QueryLoader.DATA_KEY_SELECTION, CamaradesTable.COLUMN_PSEUDO + "='" + pseudo + "'");
        mUserLoader.restart(this, Tables.ID_CAMARADES, userData);

        userData.putBoolean(QueryLoader.DATA_KEY_URI_SINGLE, false);
        userData.putStringArray(QueryLoader.DATA_KEY_PROJECTION, new String[]{"count(*)"});
        userData.putString(QueryLoader.DATA_KEY_SELECTION,
                NotificationsTable.COLUMN_PSEUDO + "='" + pseudo + "' AND " +
                        NotificationsTable.COLUMN_LU_FLAG + "=0");
        mNewNotifyLoader.restart(this, Queries.MAIN_NOTIFICATION_FLAG, userData);

        // Set content view pager
        mViewPager = (ViewPager) findViewById(R.id.container);










        //viewPager.setOffscreenPageLimit(2);











        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int section) {
                Logs.add(Logs.Type.V, "section: " + section);
                return MainFragment.newInstance(section);
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
        mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {

            private int mShortcutWidth = Constants.NO_DATA; // Shortcut fragment width
            private int mShortcutHeight = Constants.NO_DATA; // Shortcut fragment height

            private int mShortcut; // Selected shortcut index

            private int mPositionHome;
            private int mPositionPublications;
            private int mPositionEvents;
            private int mPositionMembers;
            private int mPositionNotifications;
            // Shortcut positions

            private void translateShortcut(int section) { // Update & translate shortcut position

                //Logs.add(Logs.Type.V, "section: " + section);
                mShortcut = section;

                switch (section) {
                    case 0: { ////// Home

                        mPositionHome = 0;
                        mPositionPublications = mShortcutWidth;
                        mPositionEvents = mShortcutWidth * 2;
                        mPositionMembers = mShortcutWidth * 3;
                        mPositionNotifications = mShortcutWidth * 4;
                        break;
                    }
                    case 1: { ////// Publications

                        mPositionHome = -mShortcutWidth;
                        mPositionPublications = 0;
                        mPositionEvents = mShortcutWidth;
                        mPositionMembers = mShortcutWidth * 2;
                        mPositionNotifications = mShortcutWidth * 3;
                        break;
                    }
                    case 2: { ////// Events

                        mPositionHome = mShortcutWidth * -2;
                        mPositionPublications = -mShortcutWidth;
                        mPositionEvents = 0;
                        mPositionMembers = mShortcutWidth;
                        mPositionNotifications = mShortcutWidth * 2;
                        break;
                    }
                    case 3: { ////// Members

                        mPositionHome = mShortcutWidth * -3;
                        mPositionPublications = mShortcutWidth * -2;
                        mPositionEvents = -mShortcutWidth;
                        mPositionMembers = 0;
                        mPositionNotifications = mShortcutWidth;
                        break;
                    }
                    case 4: { ////// Notifications

                        mPositionHome = mShortcutWidth * -4;
                        mPositionPublications = mShortcutWidth * -3;
                        mPositionEvents = mShortcutWidth * -2;
                        mPositionMembers = -mShortcutWidth;
                        mPositionNotifications = 0;
                        break;
                    }
                }
            }
            private void positionShortcut(final int section) {
            // Position shortcut according selected section

                //Logs.add(Logs.Type.V, "section: " + section);
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

                                    translateShortcut(section);

                                    findViewById(R.id.shortcut_home).setTranslationX(mPositionHome);
                                    findViewById(R.id.shortcut_publications).setTranslationX(mPositionPublications);
                                    findViewById(R.id.shortcut_new_publication).setTranslationX(mPositionPublications);
                                    findViewById(R.id.shortcut_events).setTranslationX(mPositionEvents);
                                    findViewById(R.id.shortcut_new_event).setTranslationX(mPositionEvents);
                                    findViewById(R.id.shortcut_members).setTranslationX(mPositionMembers);
                                    findViewById(R.id.shortcut_notifications).setTranslationX(mPositionNotifications);
                                    findViewById(R.id.shortcut_new_notification).setTranslationX(mPositionNotifications);

                                    findViewById(R.id.shortcut_new_publication).setTranslationY(-mShortcutHeight);
                                    findViewById(R.id.shortcut_new_event).setTranslationY(-mShortcutHeight);
                                    findViewById(R.id.shortcut_new_notification).setTranslationY(-mShortcutHeight);
                                }
                            });
                } else
                    translateShortcut(section);
            }

            //////
            @Override
            public void transformPage(View page, float position) {

                //Logs.add(Logs.Type.V, "page: " + page.getTag() + ";position: " + position +
                //        ";current: " + mViewPager.getCurrentItem());
                if ((position == 1) || (position == 0) || (position == -1) ||
                        ((mShortcut + 2) == (int)page.getTag()) || ((mShortcut - 2) == (int)page.getTag()))
                    positionShortcut(mViewPager.getCurrentItem());

                if (mShortcut == (int)page.getTag()) {
                    findViewById(R.id.shortcut_home).setTranslationX(mPositionHome +
                            (position * mShortcutWidth));
                    findViewById(R.id.shortcut_publications).setTranslationX(mPositionPublications +
                            (position * mShortcutWidth));
                    findViewById(R.id.shortcut_new_publication).setTranslationX(mPositionPublications +
                            (position * mShortcutWidth));
                    findViewById(R.id.shortcut_events).setTranslationX(mPositionEvents +
                            (position * mShortcutWidth));
                    findViewById(R.id.shortcut_new_event).setTranslationX(mPositionEvents +
                            (position * mShortcutWidth));
                    findViewById(R.id.shortcut_members).setTranslationX(mPositionMembers +
                            (position * mShortcutWidth));
                    findViewById(R.id.shortcut_notifications).setTranslationX(mPositionNotifications +
                            (position * mShortcutWidth));
                    findViewById(R.id.shortcut_new_notification).setTranslationX(mPositionNotifications +
                            (position * mShortcutWidth));
                }
            }
        });

        // Set tabulation layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(
                (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ?
                        TabLayout.MODE_SCROLLABLE : TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Logs.add(Logs.Type.V, null);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        if (mNewNotification)
            menu.findItem(R.id.mnu_notification).setIcon(getDrawable(R.drawable.ic_notifications_info_24dp));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logs.add(Logs.Type.V, "item: " + item);
        if (item.getItemId() == R.id.mnu_notification) {

            mViewPager.setCurrentItem(Constants.MAIN_SECTION_NOTIFICATIONS);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
