package com.studio.artaban.leclassico;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.studio.artaban.leclassico.components.LimitlessViewPager;
import com.studio.artaban.leclassico.connection.DataService;
import com.studio.artaban.leclassico.connection.ServiceBinder;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataProvider;
import com.studio.artaban.leclassico.data.tables.CamaradesTable;
import com.studio.artaban.leclassico.helpers.Internet;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.tools.SizeUtils;
import com.studio.artaban.leclassico.tools.WaitUiThread;

/**
 * Created by pascal on 15/07/16.
 * Introduction & connection activity class
 */
public class IntroActivity extends AppCompatActivity {

    private static final String DATA_KEY_INTRO_DISPLAYED = "introDisplayed";

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
    private void quit() { // Quit application (stop service)

        Logs.add(Logs.Type.V, null);
        mIntroDone = false;

        if (DataService.isRunning())
            stopService(new Intent(IntroActivity.this, DataService.class));
            // NB: Will call "DataService.stop" method when the service connection will disconnect
            //     (see "onServiceDisconnected" in "ServiceBinder" class), and this B4 the service
            //     is stopped. No need to call "DataService.stop" method here.

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

    ////////////////////////////////////////////////////////////////////////////////////////// Login

    private ProgressDialog mProgressDialog; // Progress dialog for connection & synchronization

    private final ServiceBinder mDataService = new ServiceBinder(); // Data service accessor
    private class DataReceiver extends BroadcastReceiver { // Data receiver

        @Override
        public void onReceive(Context context, Intent intent) {

            Logs.add(Logs.Type.V, "context: " + context + ";intent: " + intent);
            if (intent.getAction().equals(DataService.STATUS_CONNECTION)) {

                byte connectState = intent.getByteExtra(DataService.CONNECTION_STATE, DataService.STATE_ERROR);
                if (connectState == DataService.CONNECTION_STATE_CONNECTED) {
                    Logs.add(Logs.Type.I, "Data synchronization");

                    // Data synchronization
                    mProgressDialog.setMessage(getString(R.string.data_synchro));
                    mDataService.get().synchronize();

                } else {

                    mProgressDialog.setIndeterminateDrawable(getDrawable(R.drawable.error_red));
                    mProgressDialog.setTitle(getString(R.string.error));
                    mProgressDialog.setMessage(getString(
                            (connectState == DataService.CONNECTION_STATE_LOGIN_FAILED)?
                                    R.string.login_failed : R.string.webservice_error));
                }

            } else if (intent.getAction().equals(DataService.STATUS_SYNCHRONIZATION)) {








                //mProgressDialog.cancel();
                //startActivity








            }
        }
    };
    private DataReceiver mDataReceiver;

    //
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
    public void onConnection(View sender) { // Connection

        Logs.add(Logs.Type.V, "sender: " + sender);
        mProgressDialog = ProgressDialog.show(this, getString(R.string.wait),
                getString(R.string.check_internet), true, true);

        final EditText pseudoText = (EditText)findViewById(R.id.edit_pseudo);
        final EditText passwordText = (EditText)findViewById(R.id.edit_password);

        new Thread(new Runnable() {

            private void displayOfflineError() {
                Logs.add(Logs.Type.V, null);

                try { mProgressDialog.setIndeterminateDrawable(getDrawable(R.drawable.warning_red));
                } catch (Exception e) {
                    Logs.add(Logs.Type.W, "Failed to display warning icon");
                }
                IntroActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.setIndeterminateDrawable(getDrawable(R.drawable.warning_red));
                        mProgressDialog.setTitle(getString(R.string.error));
                        mProgressDialog.setMessage(getString(R.string.no_internet));
                    }
                });
            }

            @Override
            public void run() {
                Logs.add(Logs.Type.V, null);

                // Check Internet connection
                boolean connected = Internet.isOnline(IntroActivity.this);
                if (!mProgressDialog.isShowing()) return; // Cancelled
                try {

                    if (!connected) {

                        // No Internet connection so check existing DB to work offline
                        ContentResolver cr = getContentResolver();
                        Cursor result = cr.query(Uri.parse(DataProvider.CONTENT_URI + CamaradesTable.TABLE_NAME),
                                new String[]{ "count(*)" }, null, null, null);
                        int membersCount = 0;
                        if (result.getCount() > 0) {
                            result.moveToFirst();
                            membersCount = result.getInt(0);
                        }
                        result.close();

                        if (!mProgressDialog.isShowing()) return; // Cancelled
                        if (membersCount > 0) { // Found existing DB (work offline)

                            // Offline identification
                            WaitUiThread.run(IntroActivity.this, new WaitUiThread.TaskToRun() {
                                @Override
                                public void proceed() {
                                    mProgressDialog.setMessage(getString(R.string.offline_identification));
                                }
                            });
                            mDataService.get().login(
                                    pseudoText.getText().toString(),
                                    passwordText.getText().toString());












                        } else
                            displayOfflineError();

                    } else {

                        // Identification
                        WaitUiThread.run(IntroActivity.this, new WaitUiThread.TaskToRun() {
                            @Override
                            public void proceed() {
                                mProgressDialog.setMessage(getString(R.string.identification));
                            }
                        });
                        if (!mDataService.get().login(pseudoText.getText().toString(),
                                passwordText.getText().toString()))
                            displayOfflineError();
                    }

                } catch (NullPointerException e) {

                    Logs.add(Logs.Type.E, "Unexpected service disconnection");
                    try { mProgressDialog.setIndeterminateDrawable(getDrawable(R.drawable.error_red));
                    } catch (Exception x) {
                        Logs.add(Logs.Type.W, "Failed to display error icon");
                    }
                    IntroActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.setIndeterminateDrawable(getDrawable(R.drawable.error_red));
                            mProgressDialog.setTitle(getString(R.string.error));
                            mProgressDialog.setMessage(getString(R.string.service_unavailable));
                        }
                    });
                }
            }

        }).start();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private LimitlessViewPager mViewPager; // Introduction view pager component
    private boolean mIntroDone; // Introduction flag

    //////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);

        // Restore data
        SharedPreferences settings = getSharedPreferences(Constants.APP_PREFERENCE, 0);
        mIntroDone = settings.getBoolean(Constants.APP_PREFERENCE_INTRO_DONE, false);
        if (savedInstanceState != null) {

            mIntroDone = savedInstanceState.getBoolean(DATA_KEY_INTRO_DISPLAYED);

            mAlphaSkip = savedInstanceState.getFloat(DATA_KEY_ALPHA_SKIP);
            mAlphaStep1 = savedInstanceState.getFloat(DATA_KEY_ALPHA_STEP1);
            mAlphaStep2 = savedInstanceState.getFloat(DATA_KEY_ALPHA_STEP2);
            mAlphaStep3 = savedInstanceState.getFloat(DATA_KEY_ALPHA_STEP3);
            mAlphaStep4 = savedInstanceState.getFloat(DATA_KEY_ALPHA_STEP4);

        } else if (mIntroDone)
            setTheme(R.style.ConnectAppTheme); // To display white background

        setContentView(R.layout.activity_intro);

        // Avoid to display keyboard automatically
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Set action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.connection));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Check to start data service
        if (!DataService.isRunning())
            startService(new Intent(IntroActivity.this, DataService.class));

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

            private void anim(boolean toTheLeft, View page, float position) {
            // Apply animation to the representation images

                //Logs.add(Logs.Type.V, "toTheLeft: " + toTheLeft + ";page: " + page +
                //        ";position: " + position);
                if (((toTheLeft) && (position < 0f)) || (((!toTheLeft) && (position < 1f)))) {
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
                    position *=  translateRatio.getFloat();

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
            }

            @Override
            public void transformPage(View page, float position) {

                if (position <= 0f) { // This page is moving out to the left
                    anim(true, page, position);
                } else if (position <= 1f) { // This page is moving in from the right
                    anim(false, page, position);
                }
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
    protected void onResume() {
        super.onResume();

        Logs.add(Logs.Type.V, null);
        mDataService.bind(this); // Bind data service

        // Register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(DataService.STATUS_CONNECTION);
        filter.addAction(DataService.STATUS_SYNCHRONIZATION);
        registerReceiver(mDataReceiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Logs.add(Logs.Type.V, "menu: " + menu);
        getMenuInflater().inflate(R.menu.menu_connect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Logs.add(Logs.Type.V, "item: " + item);
        switch (item.getItemId()) {
            case android.R.id.home: {

                // Back to introduction layout
                mIntroDone = false;

                displayConnection(false);
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

                quit();
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

        outState.putFloat(DATA_KEY_ALPHA_SKIP, mAlphaSkip);
        outState.putFloat(DATA_KEY_ALPHA_STEP1, mAlphaStep1);
        outState.putFloat(DATA_KEY_ALPHA_STEP2, mAlphaStep2);
        outState.putFloat(DATA_KEY_ALPHA_STEP3, mAlphaStep3);
        outState.putFloat(DATA_KEY_ALPHA_STEP4, mAlphaStep4);

        Logs.add(Logs.Type.V, "outState: " + outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Logs.add(Logs.Type.V, null);
        mDataService.unbind(this); // Unbind data service
        unregisterReceiver(mDataReceiver); // Unregister broadcast receiver
    }

    @Override
    protected void onStop(){
        super.onStop();

        Logs.add(Logs.Type.V, null);
        SharedPreferences prefs = getSharedPreferences(Constants.APP_PREFERENCE, 0);
        if (prefs != null) {

            Logs.add(Logs.Type.I, "mIntroDone: " + mIntroDone);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(Constants.APP_PREFERENCE_INTRO_DONE, mIntroDone);
            editor.apply();
        }
    }
}
