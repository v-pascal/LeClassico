package com.studio.artaban.leclassico.activities.introduction;

import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.main.MainActivity;
import com.studio.artaban.leclassico.components.LimitlessViewPager;
import com.studio.artaban.leclassico.components.RevealFragment;
import com.studio.artaban.leclassico.connection.DataService;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.codes.Preferences;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.Storage;
import com.studio.artaban.leclassico.animations.InOutScreen;
import com.studio.artaban.leclassico.tools.SizeUtils;

/**
 * Created by pascal on 15/07/16.
 * Introduction & connection activity class
 */
public class IntroActivity extends AppCompatActivity implements ConnectFragment.OnConnectListener {

    private static final String DATA_KEY_INTRO_DISPLAYED = "introDisplayed";
    private static final String DATA_KEY_LOGOUT_REQUESTED = "logoutRequested";

    private static final String DATA_KEY_ALPHA_SKIP = "alphaSkip";
    private static final String DATA_KEY_ALPHA_STEP1 = "alphaStep1";
    private static final String DATA_KEY_ALPHA_STEP2 = "alphaStep2";
    private static final String DATA_KEY_ALPHA_STEP3 = "alphaStep3";
    private static final String DATA_KEY_ALPHA_STEP4 = "alphaStep4";
    // Data keys

    private static final int ANIMATION_DURATION_SHOW_CONNECT = 200; // Display connection layout partially duration
    private static final int ANIMATION_DURATION_HIDE_CONNECT = 300; // Hide connection layout duration
    private static final int ANIMATION_DURATION_DISPLAY = 400; // Display connection/introduction layout entirely duration

    private void skipIntro() { // Display connection layout

        Logs.add(Logs.Type.V, null);
        final CoordinatorLayout layout = ((CoordinatorLayout) mViewPager.getParent());
        layout.animate()
                .setDuration(ANIMATION_DURATION_DISPLAY)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .translationX(-mViewPager.getWidth())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {

                        Logs.add(Logs.Type.V, null);
                        displayConnection(true);
                    }
                });

        mIntroDone = true;
    }
    private void displayConnection(boolean show) {

        Logs.add(Logs.Type.V, "show: " + show);
        if (show) { // Show connection layout (hide introduction)

            ((CoordinatorLayout) findViewById(R.id.intro_content)).setVisibility(View.GONE);
            View connect = findViewById(R.id.connect_content);
            connect.bringToFront();

        } else { // Hide connection layout (show introduction)

            final View intro = findViewById(R.id.intro_content);
            Point screenSize = new Point();
            getWindowManager().getDefaultDisplay().getSize(screenSize);

            intro.setVisibility(View.VISIBLE);
            intro.setTranslationX(-screenSize.x);
            intro.bringToFront();
            intro.animate()
                    .setDuration(ANIMATION_DURATION_DISPLAY)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .translationX(0f);
        }
    }
    private void replaceButtonIcon(boolean cancel) {

        Logs.add(Logs.Type.V, "cancel: " + cancel);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        invalidateOptionsMenu();

        if (cancel) { // Display close icon

            fab.setImageDrawable(getDrawable(R.drawable.ic_close_white_36dp));
            fab.setBackgroundTintList(new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_enabled},
                            new int[]{android.R.attr.state_enabled},
                            new int[]{-android.R.attr.state_checked},
                            new int[]{android.R.attr.state_pressed}
                    },
                    new int[]{Color.RED, Color.RED, Color.RED, Color.RED}
            ));

        } else { // Display start icon

            fab.setImageDrawable(getDrawable(android.R.drawable.ic_lock_power_off));
            fab.setBackgroundTintList(new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_enabled},
                            new int[]{android.R.attr.state_enabled},
                            new int[]{-android.R.attr.state_checked},
                            new int[]{android.R.attr.state_pressed}
                    },
                    new int[]{
                            getResources().getColor(R.color.colorAccentMain),
                            getResources().getColor(R.color.colorAccentMain),
                            getResources().getColor(R.color.colorAccentMain),
                            getResources().getColor(R.color.colorAccentMain)
                    }
            ));
        }
    }
    private void replaceFloatingButton(boolean hide) {
    // Hide FAB under navigation bar then change icon & color B4 showing it again (expected parameter)
    // NB: From login fragment to progress fragment

        Logs.add(Logs.Type.V, "hide: " + hide);
        if (hide) // Hide start button
            InOutScreen.with(this)
                    .setDuration(DELAY_REVEAL_FRAGMENT)
                    .out(findViewById(R.id.fab));

        else { // Show cancel button

            replaceButtonIcon(true);
            InOutScreen.with(this)
                    .setDuration(DELAY_REVEAL_FRAGMENT)
                    .in(findViewById(R.id.fab));
        }
    }
    private void finishApplication(boolean displayIntro) { // Quit application (stop service)
        Logs.add(Logs.Type.V, null);

        mIntroDone = displayIntro;
        DataService.stop(this);
        finish();
    }

    private float mAlphaSkip;
    private float mAlphaStep1;
    private float mAlphaStep2;
    private float mAlphaStep3;
    private float mAlphaStep4;
    // Controls panel alpha

    private void applyAlpha(ImageView skip, ImageView step1, ImageView step2, ImageView step3,
                            ImageView step4) {

        //Logs.add(Logs.Type.V, "skip: " + skip + "step1: " + step1 + "step2: " + step2 +
        //        "step3: " + step3 + "step4: " + step4);
        skip.setAlpha(mAlphaSkip);
        step1.setAlpha(mAlphaStep1);
        step2.setAlpha(mAlphaStep2);
        step3.setAlpha(mAlphaStep3);
        step4.setAlpha(mAlphaStep4);
    }

    private static final float SCALE_RATIO_EVENTS = 0.3f;
    private static final float SCALE_RATIO_CALENDAR = 0.6f;
    private static final float SCALE_RATIO_FLYER = 0.5f;
    // Scale ratio of the events representation elements: scale calendar

    private void animEvents(View page, float position) {
    // Anim events elements according the scrolling position

        //Logs.add(Logs.Type.V, "page: " + page + ";position: " + position);
        ImageView events = (ImageView) page.findViewById(R.id.image_events);
        if (events != null) {
            events.clearAnimation();
            events.setScaleX(IntroFragment.INTRO_EVENTS_SCALE +
                    (position * SCALE_RATIO_EVENTS));
            events.setScaleY(IntroFragment.INTRO_EVENTS_SCALE +
                    (position * SCALE_RATIO_EVENTS));
        }
        ImageView calendar = (ImageView) page.findViewById(R.id.image_calendar);
        if (calendar != null) {
            calendar.clearAnimation();
            calendar.setScaleX(1f + (position * SCALE_RATIO_CALENDAR));
            calendar.setScaleY(1f + (position * SCALE_RATIO_CALENDAR));
        }
        ImageView flyer = (ImageView) page.findViewById(R.id.image_flyer);
        if (flyer != null) {
            flyer.clearAnimation();
            flyer.setScaleX(IntroFragment.INTRO_FLYER_SCALE +
                    (position * SCALE_RATIO_FLYER));
            flyer.setScaleY(IntroFragment.INTRO_FLYER_SCALE +
                    (position * SCALE_RATIO_FLYER));
        }
    }
    private void animEvents(View page) {
    // Anim events elements to reset scale changes (when cancelling to display connection activity)

        //Logs.add(Logs.Type.V, "page: " + page);
        ImageView events = (ImageView) page.findViewById(R.id.image_events);
        if (events != null) {
            events.clearAnimation();
            ScaleAnimation anim = new ScaleAnimation(1f, IntroFragment.INTRO_EVENTS_SCALE / events.getScaleX(),
                    1f, IntroFragment.INTRO_EVENTS_SCALE / events.getScaleY(),
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setFillAfter(true);
            anim.setDuration(ANIMATION_DURATION_HIDE_CONNECT);
            events.startAnimation(anim);
        }
        ImageView calendar = (ImageView) page.findViewById(R.id.image_calendar);
        if (calendar != null) {
            calendar.clearAnimation();
            ScaleAnimation anim = new ScaleAnimation(1f, 1f / calendar.getScaleX(),
                    1f, 1f / calendar.getScaleY(),
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setFillAfter(true);
            anim.setDuration(ANIMATION_DURATION_HIDE_CONNECT);
            calendar.startAnimation(anim);
        }
        ImageView flyer = (ImageView) page.findViewById(R.id.image_flyer);
        if (flyer != null) {
            flyer.clearAnimation();
            ScaleAnimation anim = new ScaleAnimation(1f, IntroFragment.INTRO_FLYER_SCALE / flyer.getScaleX(),
                    1f, IntroFragment.INTRO_FLYER_SCALE / flyer.getScaleY(),
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setFillAfter(true);
            anim.setDuration(ANIMATION_DURATION_HIDE_CONNECT);
            flyer.startAnimation(anim);
        }
    }

    //////
    private AlertDialog mErrorDialog; // Alert dialog that displays connection error messages
    private void displayError(byte error) { // Show alert dialog to display en error message

        Logs.add(Logs.Type.V, "error: " + error);
        int errorIcon, errorMsg;

        switch (error) {
            case ConnectFragment.STEP_LOGIN_FAILED: {

                errorIcon = R.drawable.warning_red;
                errorMsg = R.string.login_failed;
                break;
            }
            case ConnectFragment.STEP_INTERNET_NEEDED: {

                errorIcon = R.drawable.warning_red;
                errorMsg = R.string.no_internet;
                break;
            }
            //case ConnectFragment.STEP_ERROR:
            default: {

                errorIcon = R.drawable.error_red;
                errorMsg = R.string.error_webservice;
                break;
            }
        }

        // Display error message
        mErrorDialog.setIcon(getDrawable(errorIcon));
        mErrorDialog.setMessage(getString(errorMsg));
        mErrorDialog.show();
    }

    ////// OnConnectListener ///////////////////////////////////////////////////////////////////////
    @Override
    public void onError(byte error) {
        Logs.add(Logs.Type.V, "error: " + error);

        // Save persistent data coz unable to use save instance state behavior (not working if in pause)
        SharedPreferences prefs = getSharedPreferences(Constants.APP_PREFERENCE, 0);
        prefs.edit().putBoolean(Preferences.INTRODUCTION_ERROR_DISPLAY, true).apply();

        displayError(error);
    }

    @Override
    public void onConnected(String pseudo, boolean online) {

        Logs.add(Logs.Type.V, "online: " + online);
        if (getSupportFragmentManager().findFragmentByTag(ConnectFragment.TAG) == null)
            return; // Connection task stopped

        if (!online) // Inform user if working offline
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(IntroActivity.this, R.string.working_offline, Toast.LENGTH_LONG).show();
                }
            }, 1200);

        mLogoutRequested = true; // Needed if back to activity (logout requested)
        // NB: The only reason to be back from main activity to this activity is that the user has
        //     requested a logout operation (see main activity)

        ////// Start main activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_DATA_KEY_ONLINE, online);
        intent.putExtra(MainActivity.EXTRA_DATA_KEY_PSEUDO, pseudo);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int DELAY_REVEAL_FRAGMENT = 300; // Delay of displaying or hiding fragment

    //////
    public void onNextStep(View sender) { // Next step click event

        Logs.add(Logs.Type.V, "sender: " + sender);
        if (mViewPager.getCurrentItem() < (Constants.INTRO_PAGE_COUNT - 1))
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
        else // Last representation (events)
            skipIntro();
    }
    public void onSkipIntro(View sender) { // Cancel introduction click event

        Logs.add(Logs.Type.V, "sender: " + sender);
        skipIntro();
    }
    public void onAction(View sender) { // Login or Cancel connection

        Logs.add(Logs.Type.V, "sender: " + sender);
        if (getSupportFragmentManager().findFragmentByTag(ConnectFragment.TAG) != null) {

            // Cancel connection task
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().executePendingTransactions();
            replaceButtonIcon(false);

        } else {

            LoginFragment login = (LoginFragment)getSupportFragmentManager().findFragmentByTag(LoginFragment.TAG);
            final String pseudo = login.getPseudo();
            final String password = login.getPassword();

            // Check valid login (defined)
            if ((pseudo.length() == 0) || (password.length() == 0)) {

                new AlertDialog.Builder(this)
                        .setTitle(R.string.warning)
                        .setIcon(getDrawable(R.drawable.warning_red))
                        .setMessage(R.string.login_missing)
                        .show();
                return;
            }
            replaceFloatingButton(true);
            login.reveal(false, new RevealFragment.OnRevealListener() {
                @Override
                public void onRevealEnd() {
                    Logs.add(Logs.Type.V, null);

                    Bundle data = new Bundle();
                    data.putString(ConnectFragment.ARGS_KEY_PSEUDO, pseudo);
                    data.putString(ConnectFragment.ARGS_KEY_PASSWORD, password);

                    ConnectFragment progress = new ConnectFragment();
                    progress.setArguments(data);
                    progress.reveal(true, null, DELAY_REVEAL_FRAGMENT);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.connect_container, progress, ConnectFragment.TAG)
                            .addToBackStack(null)
                            .commit();
                    //
                    replaceFloatingButton(false);
                    getSupportFragmentManager().executePendingTransactions();
                }

            }, DELAY_REVEAL_FRAGMENT);
        }
    }

    private LimitlessViewPager mViewPager; // Introduction view pager component

    private boolean mIntroDone; // Introduction displayed flag
    private boolean mLogoutRequested; // Flag used to logout when back to this activity after logged

    ////// AppCompatActivity ///////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);

        // Restore data
        SharedPreferences settings = getSharedPreferences(Constants.APP_PREFERENCE, 0);
        mIntroDone = settings.getBoolean(Preferences.INTRODUCTION_DONE, false);
        byte error = (byte)settings.getInt(Preferences.CONNECTION_STEP, 0);
        boolean errorDisplay = settings.getBoolean(Preferences.INTRODUCTION_ERROR_DISPLAY, false);

        if (savedInstanceState != null) {
            mIntroDone = savedInstanceState.getBoolean(DATA_KEY_INTRO_DISPLAYED);
            mLogoutRequested = savedInstanceState.getBoolean(DATA_KEY_LOGOUT_REQUESTED);

            mAlphaSkip = savedInstanceState.getFloat(DATA_KEY_ALPHA_SKIP);
            mAlphaStep1 = savedInstanceState.getFloat(DATA_KEY_ALPHA_STEP1);
            mAlphaStep2 = savedInstanceState.getFloat(DATA_KEY_ALPHA_STEP2);
            mAlphaStep3 = savedInstanceState.getFloat(DATA_KEY_ALPHA_STEP3);
            mAlphaStep4 = savedInstanceState.getFloat(DATA_KEY_ALPHA_STEP4);

        } else if (mIntroDone)
            setTheme(R.style.ConnectAppTheme); // To display white background

        //
        setContentView(R.layout.activity_intro);

        // Add login fragment (if not already done)
        if (getSupportFragmentManager().findFragmentByTag(LoginFragment.TAG) == null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.connect_container, new LoginFragment(), LoginFragment.TAG)
                    .commit();
        if (getSupportFragmentManager().findFragmentByTag(ConnectFragment.TAG) != null)
            replaceButtonIcon(true);

        // Create & restore error dialog (if needed)
        mErrorDialog = new AlertDialog.Builder(this).create();
        mErrorDialog.setTitle(R.string.error);
        mErrorDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Logs.add(Logs.Type.V, "dialog: " + dialog);

                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().executePendingTransactions();
                replaceButtonIcon(false);

                // Save persistent data
                SharedPreferences prefs = getSharedPreferences(Constants.APP_PREFERENCE, 0);
                prefs.edit().putBoolean(Preferences.INTRODUCTION_ERROR_DISPLAY, false).apply();
            }
        });
        if (errorDisplay)
            displayError(error);

        // Avoid to display keyboard automatically
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Set action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.connection));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create working sub folders
        if (!Storage.createWorkingFolders(this)) {

            // Quit application if shared storage is not currently available
            Toast.makeText(this, R.string.error_no_storage, Toast.LENGTH_LONG);
            finish();
            return;
        }

        // Check to display intro
        if (mIntroDone)
            displayConnection(true);

        // Set application icon size (connection layout)
        ImageView appIcon = (ImageView)findViewById(R.id.image_app);
        SizeUtils.screenRatio(this, appIcon, true,
                (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ?
                        0.5f : 0.25f);

        // Set controls panel
        final ImageButton skip = (ImageButton)findViewById(R.id.image_skip);
        final ImageView step1 = (ImageView)findViewById(R.id.image_step_1);
        final ImageView step2 = (ImageView)findViewById(R.id.image_step_2);
        final ImageView step3 = (ImageView)findViewById(R.id.image_step_3);
        final ImageView step4 = (ImageView)findViewById(R.id.image_step_4);
        applyAlpha(skip, step1, step2, step3, step4);

        // Set the introduction limitless view pager
        mViewPager = (LimitlessViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return Constants.INTRO_PAGE_COUNT;
            }

            @Override
            public Fragment getItem(int position) {
                return IntroFragment.newInstance(position);
            }
        });
        mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {

            private static final float TRANSLATE_RATIO_LIGHT_1 = 170f;
            private static final float TRANSLATE_RATIO_BALL = 130f;
            private static final float TRANSLATE_RATIO_LIGHT_2 = 100f;
            private static final float TRANSLATE_RATIO_DISK_TRAY = 90f;
            private static final float TRANSLATE_RATIO_SOUND_SPEAKER = 70f;
            private static final float TRANSLATE_RATIO_SMILEY = 50f;
            private static final float TRANSLATE_RATIO_UN_SMILEY = 40f;
            // Translation ratio of the welcome representation elements: scroll concert scene elements.

            private static final float TRANSLATE_RATIO_LINK_X = 25f;
            private static final float TRANSLATE_RATIO_LINK_Y = 50;
            private static final float TRANSLATE_RATIO_PHOTO_X = -40f;
            private static final float TRANSLATE_RATIO_PHOTO_Y = 50f;
            private static final float TRANSLATE_RATIO_FRIEND_X = 40f;
            private static final float TRANSLATE_RATIO_FRIEND_Y = 50f;
            // Translation ratio of the publications representation elements: move publications.

            private static final float ROTATION_RATIO_GIRLS_Y = -220f;
            private static final float ROTATION_RATIO_COUPLE_Y = -120f;
            private static final float ROTATION_RATIO_INDOOR_Y = -65f;
            private static final float ROTATION_RATIO_OUTDOOR_Y = 90f;
            private static final float ROTATION_RATIO_DJ_Y = -165f;
            // Rotation ratio of the albums representation elements (photos): flip photos.

            private static final float TRANSLATE_RATIO_GREEN_MARKER_Y = 100f;
            private static final float TRANSLATE_RATIO_YELLOW_MARKER_X = 130f;
            private static final float TRANSLATE_RATIO_BLUE_MARKER_X = -90f;
            private static final float TRANSLATE_RATIO_BLUE_MARKER_Y = -20f;
            private static final float TRANSLATE_RATIO_RED_MARKER_X = -90f;
            private static final float TRANSLATE_RATIO_RED_MARKER_Y = 20f;
            // Translation ratio of the location representation elements (markers): follow map.

            private void anim(View page, float position) {
            // Apply animation to the representation images

                //Logs.add(Logs.Type.V, "page: " + page + ";position: " + position);
                float sizeRatio = IntroFragment.getSizeRatio(IntroActivity.this);

                ////// Album photos: flip photos!
                ImageView girls = (ImageView) page.findViewById(R.id.image_girls);
                if (girls != null)
                    girls.setRotationY(IntroFragment.INTRO_GIRLS_ROTATION_Y +
                            (position * ROTATION_RATIO_GIRLS_Y));
                ImageView couple = (ImageView) page.findViewById(R.id.image_couple);
                if (couple != null)
                    couple.setRotationY(IntroFragment.INTRO_COUPLE_ROTATION_Y +
                            (position * ROTATION_RATIO_COUPLE_Y));
                ImageView indoor = (ImageView) page.findViewById(R.id.image_indoor);
                if (indoor != null)
                    indoor.setRotationY(IntroFragment.INTRO_INDOOR_ROTATION_Y +
                            (position * ROTATION_RATIO_INDOOR_Y));
                ImageView outdoor = (ImageView) page.findViewById(R.id.image_outdoor);
                if (outdoor != null)
                    outdoor.setRotationY(position * ROTATION_RATIO_OUTDOOR_Y);
                ImageView dj = (ImageView) page.findViewById(R.id.image_dj);
                if (dj != null)
                    dj.setRotationY(position * ROTATION_RATIO_DJ_Y);

                TypedValue translateRatio = new TypedValue();
                getResources().getValue(R.dimen.translation_ratio, translateRatio, true);
                position *= translateRatio.getFloat();

                ////// Welcome: scroll elements!
                ImageView light1 = (ImageView) page.findViewById(R.id.image_light1);
                if (light1 != null)
                    light1.setTranslationX((IntroFragment.INTRO_LIGHT_1_TRANS_X * sizeRatio) +
                            (position * TRANSLATE_RATIO_LIGHT_1));
                ImageView ball = (ImageView) page.findViewById(R.id.image_ball);
                if (ball != null)
                    ball.setTranslationX(position * TRANSLATE_RATIO_BALL);
                ImageView light2 = (ImageView) page.findViewById(R.id.image_light2);
                if (light2 != null)
                    light2.setTranslationX((IntroFragment.INTRO_LIGHT_2_TRANS_X * sizeRatio) +
                            (position * TRANSLATE_RATIO_LIGHT_2));
                ImageView diskTray = (ImageView) page.findViewById(R.id.image_disk_tray);
                if (diskTray != null)
                    diskTray.setTranslationX((IntroFragment.INTRO_DISK_TRAY_TRANS_X * sizeRatio) +
                            (position * TRANSLATE_RATIO_DISK_TRAY));
                ImageView speaker = (ImageView) page.findViewById(R.id.image_sound_speaker);
                if (speaker != null)
                    speaker.setTranslationX((IntroFragment.INTRO_SOUND_SPEAKER_TRANS_X * sizeRatio) +
                            (position * TRANSLATE_RATIO_SOUND_SPEAKER));
                ImageView smiley = (ImageView) page.findViewById(R.id.image_smiley);
                if (smiley != null)
                    smiley.setTranslationX((IntroFragment.INTRO_SMILEY_TRANS_X * sizeRatio) +
                            (position * TRANSLATE_RATIO_SMILEY));
                ImageView unSmiley = (ImageView) page.findViewById(R.id.image_un_smiley);
                if (unSmiley != null)
                    unSmiley.setTranslationX((IntroFragment.INTRO_UN_SMILEY_TRANS_X * sizeRatio) +
                            (position * TRANSLATE_RATIO_UN_SMILEY));

                ////// Publications: move publications!
                ImageView link = (ImageView) page.findViewById(R.id.image_link);
                if (link != null) {
                    link.setTranslationX((IntroFragment.INTRO_LINK_TRANS_X * sizeRatio) +
                            (position * TRANSLATE_RATIO_LINK_X));
                    link.setTranslationY((IntroFragment.INTRO_LINK_TRANS_Y * sizeRatio) +
                            (position * TRANSLATE_RATIO_LINK_Y));
                }
                ImageView photo = (ImageView) page.findViewById(R.id.image_photo);
                if (photo != null) {
                    photo.setTranslationX((IntroFragment.INTRO_PHOTO_TRANS_X * sizeRatio) +
                            (position * TRANSLATE_RATIO_PHOTO_X));
                    photo.setTranslationY((IntroFragment.INTRO_PHOTO_TRANS_Y * sizeRatio) +
                            (position * TRANSLATE_RATIO_PHOTO_Y));
                }
                ImageView friend = (ImageView) page.findViewById(R.id.image_friend);
                if (friend != null) {
                    friend.setTranslationX((IntroFragment.INTRO_FRIEND_TRANS_X * sizeRatio) +
                            (position * TRANSLATE_RATIO_FRIEND_X));
                    friend.setTranslationY((IntroFragment.INTRO_FRIEND_TRANS_Y * sizeRatio) +
                            (position * TRANSLATE_RATIO_FRIEND_Y));
                }

                ////// Location: follow the map!
                ImageView greenMark = (ImageView) page.findViewById(R.id.image_green_marker);
                if (greenMark != null)
                    greenMark.setTranslationY((IntroFragment.INTRO_GREEN_MARKER_TRANS_Y * sizeRatio) +
                            (position * TRANSLATE_RATIO_GREEN_MARKER_Y));
                ImageView yellowMark = (ImageView) page.findViewById(R.id.image_yellow_marker);
                if (yellowMark != null)
                    yellowMark.setTranslationX((IntroFragment.INTRO_YELLOW_MARKER_TRANS_X * sizeRatio) +
                            (position * TRANSLATE_RATIO_YELLOW_MARKER_X));
                ImageView blueMark = (ImageView) page.findViewById(R.id.image_blue_marker);
                if (blueMark != null) {
                    blueMark.setTranslationX((IntroFragment.INTRO_BLUE_MARKER_TRANS_X * sizeRatio) +
                            (position * TRANSLATE_RATIO_BLUE_MARKER_X));
                    blueMark.setTranslationY((IntroFragment.INTRO_BLUE_MARKER_TRANS_Y * sizeRatio) +
                            (position * TRANSLATE_RATIO_BLUE_MARKER_Y));
                }
                ImageView redMark = (ImageView) page.findViewById(R.id.image_red_marker);
                if (redMark != null) {

                    redMark.setTranslationX((IntroFragment.INTRO_RED_MARKER_TRANS_X * sizeRatio) +
                            (position * TRANSLATE_RATIO_RED_MARKER_X));
                    redMark.setTranslationY((IntroFragment.INTRO_RED_MARKER_TRANS_Y * sizeRatio) +
                            (position * TRANSLATE_RATIO_RED_MARKER_Y));
                }

                ////// Events: scale calendar!
                animEvents(page, position);
            }

            @Override
            public void transformPage(View page, float position) {
                //Logs.add(Logs.Type.V, "page: " + page + ";position: " + position);
                if (position < 1)
                    anim(page, position);
            }
        });
        mViewPager.setOnLimitlessListener(new LimitlessViewPager.OnLimitCrossedListener() {

            private static final int DURATION_CANCEL_ROTATION = 500;
            private float mRotation; // Left limitless page rotation

            private static final float RATIO_START_BEHAVIOR = 0.25f;
            // Start to translate ViewPager when this % of the screen width is reached
            private static final float RATIO_DISPLAY_CONNECT = 0.4f;
            // Display connection layout when this % of the screen width is reached

            private boolean mScrolling; // Scrolling flag
            private float mTranslate; // Horizontal translation (in pixels)
            private static final float SCALE_DELTA_RATIO_EVENTS = 0.5f; // Ratio between the horizontal
            // translation & the scale of events elements (according the screen width)

            @Override
            public boolean onStartBehavior(boolean leftTop, float originX, float originY) {

                Logs.add(Logs.Type.V, "leftTop: " + leftTop + ";originX: " + originX +
                        ";originY: " + originX);
                if ((!leftTop) && (originX > (mViewPager.getWidth() - (mViewPager.getWidth() *
                        RATIO_START_BEHAVIOR)))) {

                    mTranslate = 0f;
                    mScrolling = true;
                    return true;
                }
                mRotation = 0f;
                return false;
            }

            @Override
            public boolean onFinishBehavior(boolean leftTop) {

                Logs.add(Logs.Type.V, "leftTop: " + leftTop);
                final View page = mViewPager.findViewWithTag((leftTop) ?
                        LimitlessViewPager.TAG_PAGE_LEFT_TOP : LimitlessViewPager.TAG_PAGE_RIGHT_BOTTOM);
                if (leftTop) {

                    if ((mRotation > 0f) || (mRotation < 0f)) {

                        ObjectAnimator animation = ObjectAnimator.ofFloat(page, "rotationY",
                                mRotation, 0f);
                        animation.setDuration(DURATION_CANCEL_ROTATION);
                        animation.start();
                    }
                    return false;
                }
                if (!mScrolling)
                    return false;

                if (mTranslate < 0f) {

                    CoordinatorLayout layout = ((CoordinatorLayout) page.getParent().getParent());
                    layout.clearAnimation();
                    layout.setTranslationX(0);
                    TranslateAnimation anim;
                    if (mTranslate < (-mViewPager.getWidth() * RATIO_DISPLAY_CONNECT)) {
                        // Display connection layout

                        anim = new TranslateAnimation(mTranslate, -mViewPager.getWidth(), 0f, 0f);
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                Logs.add(Logs.Type.V, "animation: " + animation);

                                animEvents(page, 0f);
                                // Reset any scale changes (needed for further display)

                                displayConnection(true);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        anim.setFillAfter(true);
                        anim.setDuration(ANIMATION_DURATION_SHOW_CONNECT);

                        mIntroDone = true;

                    } else { // Cancel displaying connection layout

                        anim = new TranslateAnimation(mTranslate, 0f, 0f, 0f);
                        anim.setDuration(ANIMATION_DURATION_HIDE_CONNECT);
                        animEvents(page); // Cancel events elements scale changes
                    }
                    layout.startAnimation(anim);
                }
                mScrolling = false;
                return true;
            }

            @Override
            public boolean onLeftTopLimitCrossed(View page, float deltaX, float deltaY) {

                //Logs.add(Logs.Type.V, "page: " + page + ";deltaX: " + deltaX + ";deltaY: " + deltaY);
                if (deltaX > 0f) {

                    TypedValue screenRatio = new TypedValue();
                    getResources().getValue(R.dimen.limitless_rotation_ratio, screenRatio, true);
                    mRotation = deltaX * screenRatio.getFloat();
                    page.setRotationY(mRotation);
                }
                return false;
            }

            @Override
            public boolean onRightBottomLimitCrossed(View page, float deltaX, float deltaY) {

                //Logs.add(Logs.Type.V, "page: " + page + ";deltaX: " + deltaX + ";deltaY: " + deltaY);
                if (!mScrolling)
                    return false;

                mTranslate += deltaX;
                if (mTranslate > 0f)
                    mTranslate = 0f;
                ((CoordinatorLayout) page.getParent().getParent()).setTranslationX(mTranslate);

                ////// Events: scale calendar!
                animEvents(page, mTranslate * SCALE_DELTA_RATIO_EVENTS / (float) mViewPager.getWidth());
                return true;
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private static final float ALPHA_NO_SELECTION = 0.3f;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Display or hide panel controls according current scroll position

                //Logs.add(Logs.Type.V, "position: " + position);
                switch (position) {
                    case 0: { // 0 -> 1

                        mAlphaSkip = positionOffset;
                        mAlphaStep1 = positionOffset;
                        mAlphaStep2 = positionOffset * ALPHA_NO_SELECTION;
                        mAlphaStep3 = positionOffset * ALPHA_NO_SELECTION;
                        mAlphaStep4 = positionOffset * ALPHA_NO_SELECTION;
                        break;
                    }
                    case 1: { // 1 -> 2

                        if (positionOffset > 0f) {
                            mAlphaStep1 = (positionOffset * (ALPHA_NO_SELECTION - 1f)) + 1f;
                            mAlphaStep2 = (positionOffset * (1f - ALPHA_NO_SELECTION)) + ALPHA_NO_SELECTION;
                        }
                        break;
                    }
                    case 2: { // 2 -> 3

                        if (positionOffset > 0f) {
                            mAlphaStep2 = (positionOffset * (ALPHA_NO_SELECTION - 1f)) + 1f;
                            mAlphaStep3 = (positionOffset * (1f - ALPHA_NO_SELECTION)) + ALPHA_NO_SELECTION;
                        }
                        break;
                    }
                    case 3: { // 3 -> 4

                        if (positionOffset > 0f) {
                            mAlphaStep3 = (positionOffset * (ALPHA_NO_SELECTION - 1f)) + 1f;
                            mAlphaStep4 = (positionOffset * (1f - ALPHA_NO_SELECTION)) + ALPHA_NO_SELECTION;
                            mAlphaSkip = 1f - positionOffset;
                        }
                        break;
                    }
                }
                applyAlpha(skip, step1, step2, step3, step4);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Logs.add(Logs.Type.V, null);
        if (mLogoutRequested) { // Logout requested (back to connect activity)
            mLogoutRequested = false;

            getSupportFragmentManager().popBackStack(); // Remove progress fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.connect_container, new LoginFragment(), LoginFragment.TAG)
                    .commit(); // Reset login fragment
            getSupportFragmentManager().executePendingTransactions();
            replaceButtonIcon(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Logs.add(Logs.Type.V, "menu: " + menu);
        getMenuInflater().inflate(R.menu.menu_connect, menu);

        // Disable menu when connecting
        menu.setGroupEnabled(0, getSupportFragmentManager().findFragmentByTag(ConnectFragment.TAG) == null);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Logs.add(Logs.Type.V, "item: " + item);
        switch (item.getItemId()) {
            case android.R.id.home: {

                if (getSupportFragmentManager().findFragmentByTag(ConnectFragment.TAG) != null) {

                    // Cancel connection task
                    getSupportFragmentManager().popBackStack();
                    getSupportFragmentManager().executePendingTransactions();
                    replaceButtonIcon(false);

                } else { // Back to introduction layout
                    mIntroDone = false;
                    displayConnection(false);
                }
                return true;
            }
            case R.id.menu_about: {

                // Display about application info
                new AlertDialog.Builder(this)
                        .setTitle(R.string.mnu_about)
                        .setMessage(getString(R.string.app_about, getString(R.string.app_name)))
                        .show();
                return true;
            }
            case R.id.menu_quit: {

                // Quit application
                finishApplication(false);
                return true;
            }
            case R.id.menu_help: {

                // Display connection help info
                new AlertDialog.Builder(this)
                        .setTitle(R.string.help)
                        .setIcon(getDrawable(R.drawable.info_orange))
                        .setMessage(R.string.help_connection)
                        .setCancelable(true)
                        .create().show();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putBoolean(DATA_KEY_INTRO_DISPLAYED, mIntroDone);
        outState.putBoolean(DATA_KEY_LOGOUT_REQUESTED, mLogoutRequested);

        outState.putFloat(DATA_KEY_ALPHA_SKIP, mAlphaSkip);
        outState.putFloat(DATA_KEY_ALPHA_STEP1, mAlphaStep1);
        outState.putFloat(DATA_KEY_ALPHA_STEP2, mAlphaStep2);
        outState.putFloat(DATA_KEY_ALPHA_STEP3, mAlphaStep3);
        outState.putFloat(DATA_KEY_ALPHA_STEP4, mAlphaStep4);

        Logs.add(Logs.Type.V, "outState: " + outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {

        Logs.add(Logs.Type.V, null);
        if (getSupportFragmentManager().findFragmentByTag(ConnectFragment.TAG) != null)
            replaceButtonIcon(false);

        super.onBackPressed();
    }

    @Override
    protected void onStop(){
        super.onStop();
        Logs.add(Logs.Type.V, null);

        // Store persistent data
        SharedPreferences prefs = getSharedPreferences(Constants.APP_PREFERENCE, 0);
        prefs.edit().putBoolean(Preferences.INTRODUCTION_DONE, mIntroDone).apply();
    }
}
