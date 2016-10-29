package com.studio.artaban.leclassico.activities.main;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.LoggedActivity;
import com.studio.artaban.leclassico.activities.notification.NotifyActivity;
import com.studio.artaban.leclassico.animations.RecyclerItemAnimator;
import com.studio.artaban.leclassico.connection.DataService;
import com.studio.artaban.leclassico.connection.ServiceBinder;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Queries;
import com.studio.artaban.leclassico.data.codes.Requests;
import com.studio.artaban.leclassico.data.codes.Tables;
import com.studio.artaban.leclassico.data.codes.Uris;
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
public class MainActivity extends LoggedActivity implements
        NavigationView.OnNavigationItemSelectedListener, QueryLoader.OnResultListener,
        MainFragment.OnFragmentListener {

    public static final String EXTRA_DATA_ONLINE = "online";
    public static final String EXTRA_DATA_PSEUDO = "pseudo";
    public static final String EXTRA_DATA_PSEUDO_ID = "pseudoId";
    // Extra data keys

    private static int getShortcutID(int section, boolean newItem) {
    // Return shortcut fragment container ID according selected section

        //Logs.add(Logs.Type.V, "section: " + section + ";newItem: " + newItem);
        switch (section) {

            case Constants.MAIN_SECTION_HOME:
                return R.id.shortcut_home;
            case Constants.MAIN_SECTION_PUBLICATIONS:
                return (newItem)? R.id.shortcut_new_publication:R.id.shortcut_publications;
            case Constants.MAIN_SECTION_EVENTS:
                return (newItem)? R.id.shortcut_new_event:R.id.shortcut_events;
            case Constants.MAIN_SECTION_MEMBERS:
                return R.id.shortcut_members;
            case Constants.MAIN_SECTION_NOTIFICATIONS:
                return (newItem)? R.id.shortcut_new_notification:R.id.shortcut_notifications;
            default:
                throw new IllegalArgumentException("Unexpected section");
        }
    }

    ////// OnFragmentListener //////////////////////////////////////////////////////////////////////
    @Override
    public ShortcutFragment onGetShortcut(final int section, boolean newItem) {
        //Logs.add(Logs.Type.V, "section: " + section + ";newItem: " + newItem);
        return ((ShortcutFragment) getSupportFragmentManager().findFragmentById(getShortcutID(section, newItem)));
    }
    @Override
    public void onAnimateShortcut(final int section, final OnAnimationListener listener) {
    // Display the new fragment (using an animation)

        Logs.add(Logs.Type.V, "section: " + section);
        final ViewPropertyAnimatorCompat prevAnim = ViewCompat.animate(findViewById(getShortcutID(section, false)));
        final ViewPropertyAnimatorCompat newAnim = ViewCompat.animate(findViewById(getShortcutID(section, true)));

        prevAnim.setDuration(RecyclerItemAnimator.DEFAULT_DURATION << 1) // x2 coz move + add/change
                .translationY(mShortcutHeight)
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {

                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        prevAnim.setListener(null);

                        // Place new shortcut fragment into previous shortcut
                        listener.onCopyNewToPrevious(onGetShortcut(section, false));
                        ViewCompat.setTranslationY(view, 0);
                    }

                    @Override
                    public void onAnimationCancel(View view) {

                    }
                });
        newAnim.setDuration(RecyclerItemAnimator.DEFAULT_DURATION << 1)
                .translationY(0)
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {

                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        newAnim.setListener(null);
                        ViewCompat.setTranslationY(view, -mShortcutHeight);
                    }

                    @Override
                    public void onAnimationCancel(View view) {

                    }
                });
        newAnim.start();
        prevAnim.start();
    }

    @Override
    public DataService onGetService() {
        return mDataService.get();
    }

    @Override
    public Uri onGetShortcutURI() {
        return mShortcutUri;
    }
    @Override
    public Uri onGetNotifyURI() {
        return mNotifyUri;
    }

    //
    private int mShortcutWidth = Constants.NO_DATA; // Shortcut fragment width
    private int mShortcutHeight = Constants.NO_DATA; // Shortcut fragment height

    private boolean mNewNotification; // New notification flag (unread)
    private int mFabTransY = Constants.NO_DATA; // Vertical floating action button translation

    ////// OnResultListener ////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        Logs.add(Logs.Type.V, "id: " + id + ";cursor: " + cursor);

        cursor.moveToFirst();
        switch (id) {
            case Queries.MAIN_DATA_USER: { ////// User info

                // Get DB data
                boolean female = (!cursor.isNull(COLUMN_INDEX_SEX)) &&
                        (cursor.getInt(COLUMN_INDEX_SEX) == CamaradesTable.FEMALE);
                String profile = (!cursor.isNull(COLUMN_INDEX_PROFILE))?
                        cursor.getString(COLUMN_INDEX_PROFILE) : null;
                String banner = (!cursor.isNull(COLUMN_INDEX_BANNER))?
                        cursor.getString(COLUMN_INDEX_BANNER) : null;
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
                                    Constants.APP_URL_PROFILES + '/' + banner)
                            .placeholder(R.drawable.banner)
                            .into((ImageView) navHeader.findViewById(R.id.image_banner), null);
                break;
            }
            case Queries.MAIN_DATA_NEW_NOTIFY: {

                Logs.add(Logs.Type.I, "New notification(s): " + cursor.getInt(0));
                boolean prevNewFlag = mNewNotification;

                mNewNotification = cursor.getInt(0) > 0;
                if (prevNewFlag != mNewNotification) {
                    invalidateOptionsMenu();

                    if (mViewPager.getCurrentItem() == Constants.MAIN_SECTION_NOTIFICATIONS) {
                        Logs.add(Logs.Type.I, "Display/Hide floating action button");

                        View fab = findViewById(R.id.fab);
                        fab.setVisibility(View.VISIBLE);
                        final ViewPropertyAnimatorCompat animation = ViewCompat.animate(fab);

                        if (mNewNotification) // Display floating action button
                            animation.setDuration(RecyclerItemAnimator.DEFAULT_DURATION)
                                    .translationY(0)
                                    .setListener(new ViewPropertyAnimatorListener() {
                                        @Override
                                        public void onAnimationStart(View view) {

                                        }

                                        @Override
                                        public void onAnimationEnd(View view) {
                                            animation.setListener(null);
                                        }

                                        @Override
                                        public void onAnimationCancel(View view) {
                                            ViewCompat.setTranslationY(view, 0);
                                        }
                                    })
                                    .start();

                        else // Hide floating action button
                            animation.setDuration(RecyclerItemAnimator.DEFAULT_DURATION)
                                    .translationY(mFabTransY)
                                    .setListener(new ViewPropertyAnimatorListener() {
                                        @Override
                                        public void onAnimationStart(View view) {

                                        }

                                        @Override
                                        public void onAnimationEnd(View view) {
                                            animation.setListener(null);
                                        }

                                        @Override
                                        public void onAnimationCancel(View view) {
                                            ViewCompat.setTranslationY(view, mFabTransY);
                                        }
                                    })
                                    .start();
                    }
                }
                break;
            }
        }
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
                Logs.add(Logs.Type.I, "Display profile");





                break;
            }
            case R.id.navig_location: { // Display location activity
                Logs.add(Logs.Type.I, "Display location");





                break;
            }
            case R.id.navig_settings: { // Display settings
                Logs.add(Logs.Type.I, "Display settings");




                break;
            }
            case R.id.navig_logout: { // Logout
                Logs.add(Logs.Type.I, "Logout");

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
                Logs.add(Logs.Type.I, "Quit application");

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
    private final ServiceBinder mDataService = new ServiceBinder(); // Data service accessor

    private Uri mShortcutUri; // Shortcut URI (new mail & notifications)
    private Uri mNotifyUri; // User notifications URI

    public void onAction(View sender) { // Floating action button click event
        Logs.add(Logs.Type.V, "sender: " + sender);
        switch (mViewPager.getCurrentItem()) {

            case Constants.MAIN_SECTION_NOTIFICATIONS: { ////// Mark notifications as read

                Logs.add(Logs.Type.I, "Mark notification(s) as read");
                ((NotificationsFragment) MainFragment.getBySection(Constants.MAIN_SECTION_NOTIFICATIONS))
                        .read();
                break;
            }
            case Constants.MAIN_SECTION_PUBLICATIONS: { ////// Add publication
                Logs.add(Logs.Type.I, "Add publication");





                break;
            }
        }
    }

    ////// LoggedActivity //////////////////////////////////////////////////////////////////////////
    @Override
    protected void onLoggedResume() {
        Logs.add(Logs.Type.V, null);

        // Bind data service
        if ((DataService.isRunning()) && (!mDataService.bind(this, null)))
            Tools.criticalError(this, R.string.error_service_unavailable);

        // Register shortcut data service
        sendBroadcast(DataService.getIntent(Boolean.TRUE, Tables.ID_NOTIFICATIONS, mShortcutUri));
        sendBroadcast(DataService.getIntent(Boolean.TRUE, Tables.ID_MESSAGERIE, mShortcutUri));
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

                        if (appBarLayout.findViewById(R.id.shortcut) != null) {
                            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

                                int actionHeight = -getResources().getDimensionPixelSize(R.dimen.appbar_padding_title);
                                verticalOffset = (verticalOffset < actionHeight)? verticalOffset - actionHeight:0;
                            }
                            appBarLayout.findViewById(R.id.shortcut).setTranslationY(verticalOffset);
                        }
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
        String pseudo = getIntent().getStringExtra(EXTRA_DATA_PSEUDO);
        ((TextView)navigation.getHeaderView(0).findViewById(R.id.text_pseudo)).setText(pseudo);

        // Set URI to observe DB changes
        mShortcutUri = Uris.getUri(Uris.ID_MAIN_SHORTCUT, String.valueOf(getIntent()
                .getIntExtra(MainActivity.EXTRA_DATA_PSEUDO_ID, 0)));
        mNotifyUri = Uris.getUri(Uris.ID_USER_NOTIFICATIONS, String.valueOf(getIntent()
                .getIntExtra(MainActivity.EXTRA_DATA_PSEUDO_ID, 0)));

        // Set content view pager
        mViewPager = (ViewPager) findViewById(R.id.container);
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
                    case Constants.MAIN_SECTION_HOME: { ////// Home

                        mPositionHome = 0;
                        mPositionPublications = mShortcutWidth;
                        mPositionEvents = mShortcutWidth * 2;
                        mPositionMembers = mShortcutWidth * 3;
                        mPositionNotifications = mShortcutWidth * 4;
                        break;
                    }
                    case Constants.MAIN_SECTION_PUBLICATIONS: { ////// Publications

                        mPositionHome = -mShortcutWidth;
                        mPositionPublications = 0;
                        mPositionEvents = mShortcutWidth;
                        mPositionMembers = mShortcutWidth * 2;
                        mPositionNotifications = mShortcutWidth * 3;
                        break;
                    }
                    case Constants.MAIN_SECTION_EVENTS: { ////// Events

                        mPositionHome = mShortcutWidth * -2;
                        mPositionPublications = -mShortcutWidth;
                        mPositionEvents = 0;
                        mPositionMembers = mShortcutWidth;
                        mPositionNotifications = mShortcutWidth * 2;
                        break;
                    }
                    case Constants.MAIN_SECTION_MEMBERS: { ////// Members

                        mPositionHome = mShortcutWidth * -3;
                        mPositionPublications = mShortcutWidth * -2;
                        mPositionEvents = -mShortcutWidth;
                        mPositionMembers = 0;
                        mPositionNotifications = mShortcutWidth;
                        break;
                    }
                    case Constants.MAIN_SECTION_NOTIFICATIONS: { ////// Notifications

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

            private void translateFab(final int section, final float factor) {
                // Show/Hide & translate floating action button according section and display factor

                //Logs.add(Logs.Type.V, "section: " + section + ";factor: " + factor);
                final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setImageDrawable(getDrawable((section == Constants.MAIN_SECTION_PUBLICATIONS) ?
                        R.drawable.ic_add_white_36dp : R.drawable.ic_check_white_36dp));
                fab.setVisibility(((section == Constants.MAIN_SECTION_PUBLICATIONS) || (mNewNotification)) ?
                        View.VISIBLE : View.GONE);

                ViewCompat.animate(fab).cancel();
                if (mFabTransY == Constants.NO_DATA)
                    fab.getViewTreeObserver()
                            .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                                @Override
                                public void onGlobalLayout() {
                                    Logs.add(Logs.Type.V, null);

                                    mFabTransY = fab.getHeight() +
                                            (getResources().getDimensionPixelSize(R.dimen.fab_margin) << 1);

                                    fab.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                    fab.setTranslationY(factor * mFabTransY);
                                    fab.setVisibility(((section == Constants.MAIN_SECTION_PUBLICATIONS) ||
                                            (mNewNotification)) ? View.VISIBLE : View.GONE);
                                }
                            });
                else
                    fab.setTranslationY(factor * mFabTransY);
            }

            //////
            @Override
            public void transformPage(View page, float position) {

                //Logs.add(Logs.Type.V, "page: " + page.getTag() + ";position: " + position +
                //        ";current: " + mViewPager.getCurrentItem());
                if ((position >= 0) && (position <= 1)) {

                    // Translate floating action button accordingly (for publications & notifications)
                    switch ((int) page.getTag()) {

                        case Constants.MAIN_SECTION_PUBLICATIONS:
                        case Constants.MAIN_SECTION_NOTIFICATIONS: {
                            translateFab((int) page.getTag(), position);
                            break;
                        }
                        case (Constants.MAIN_SECTION_PUBLICATIONS + 1): {
                            translateFab(Constants.MAIN_SECTION_PUBLICATIONS, 1 - position);
                            break;
                        }
                    }
                }
                if ((position == 1) || (position == 0) || (position == -1) ||
                        ((mShortcut + 2) == (int) page.getTag()) || ((mShortcut - 2) == (int) page.getTag()))
                    positionShortcut(mViewPager.getCurrentItem());

                if (mShortcut == (int) page.getTag()) {
                    View newPub = findViewById(R.id.shortcut_new_publication);
                    View newEvent = findViewById(R.id.shortcut_new_event);
                    View newNotify = findViewById(R.id.shortcut_new_notification);
                    View publications = findViewById(R.id.shortcut_publications);
                    View events = findViewById(R.id.shortcut_events);
                    View notifications = findViewById(R.id.shortcut_notifications);

                    ViewCompat.animate(newPub).cancel();
                    ViewCompat.animate(newEvent).cancel();
                    ViewCompat.animate(newNotify).cancel();
                    ViewCompat.animate(publications).cancel();
                    ViewCompat.animate(events).cancel();
                    ViewCompat.animate(notifications).cancel();

                    findViewById(R.id.shortcut_home).setTranslationX(mPositionHome +
                            (position * mShortcutWidth));
                    publications.setTranslationX(mPositionPublications + (position * mShortcutWidth));
                    newPub.setTranslationX(mPositionPublications + (position * mShortcutWidth));
                    events.setTranslationX(mPositionEvents + (position * mShortcutWidth));
                    newEvent.setTranslationX(mPositionEvents + (position * mShortcutWidth));
                    findViewById(R.id.shortcut_members).setTranslationX(mPositionMembers +
                            (position * mShortcutWidth));
                    notifications.setTranslationX(mPositionNotifications + (position * mShortcutWidth));
                    newNotify.setTranslationX(mPositionNotifications + (position * mShortcutWidth));
                }
            }
        });

        // Set tabulation layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(
                (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ?
                        TabLayout.MODE_SCROLLABLE : TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(mViewPager);

        // Load user & notification data (using query loaders)
        Bundle userData = new Bundle();
        userData.putStringArray(QueryLoader.DATA_KEY_PROJECTION, new String[]{

                CamaradesTable.COLUMN_SEXE, // COLUMN_INDEX_SEX
                CamaradesTable.COLUMN_PROFILE, // COLUMN_INDEX_PROFILE
                CamaradesTable.COLUMN_BANNER // COLUMN_INDEX_BANNER
        });
        userData.putString(QueryLoader.DATA_KEY_SELECTION, CamaradesTable.COLUMN_PSEUDO + "='" + pseudo + '\'');
        mUserLoader.init(this, Queries.MAIN_DATA_USER, userData);

        Bundle notifyData = new Bundle();
        notifyData.putParcelable(QueryLoader.DATA_KEY_URI, mShortcutUri);
        notifyData.putString(QueryLoader.DATA_KEY_SELECTION,
                "SELECT count(*) FROM " + NotificationsTable.TABLE_NAME +
                        " WHERE " + NotificationsTable.COLUMN_PSEUDO + "='" + pseudo +
                        "' AND " + NotificationsTable.COLUMN_LU_FLAG + '=' + Constants.DATA_UNREAD);
        mNewNotifyLoader.init(this, Queries.MAIN_DATA_NEW_NOTIFY, notifyData);

        // Register new user notifications service
        sendBroadcast(DataService.getIntent(Boolean.TRUE, Tables.ID_NOTIFICATIONS, mNotifyUri));
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

            ////// Start notification activity
            startActivityForResult(new Intent(this, NotifyActivity.class), Requests.NOTIFY_2_MAIN.CODE,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logs.add(Logs.Type.V, "requestCode: " + requestCode + ";resultCode: " + resultCode + ";data: " + data);

        if (requestCode == Requests.NOTIFY_2_MAIN.CODE) { // Notification activity result
            switch (resultCode) {
                case Requests.NOTIFY_2_MAIN.RESULT_LOGOUT: { ////// Logout requested

                    supportFinishAfterTransition();
                    break;
                }
            }
        }
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

    @Override
    protected void onPause() {
        super.onPause();
        Logs.add(Logs.Type.V, null);

        // Unregister shortcut data service
        sendBroadcast(DataService.getIntent(Boolean.FALSE, Tables.ID_NOTIFICATIONS, mShortcutUri));
        sendBroadcast(DataService.getIntent(Boolean.FALSE, Tables.ID_MESSAGERIE, mShortcutUri));

        // Unbind data service
        mDataService.unbind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logs.add(Logs.Type.V, null);

        // Unregister new user notifications service
        sendBroadcast(DataService.getIntent(Boolean.FALSE, Tables.ID_NOTIFICATIONS, mNotifyUri));

        sendBroadcast(DataService.getIntent(Boolean.FALSE, Tables.ID_NOTIFICATIONS, mShortcutUri));
        sendBroadcast(DataService.getIntent(Boolean.FALSE, Tables.ID_MESSAGERIE, mShortcutUri));
        // NB: Also implemented in 'onPause' method but needed in user kill app case
    }
}
