package com.studio.artaban.leclassico;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.ViewStub;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.studio.artaban.leclassico.components.LimitlessViewPager;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

import java.util.regex.Pattern;

/**
 * Created by pascal on 15/07/16.
 * Introduction activity class
 */
public class IntroActivity extends AppCompatActivity {

    private static final String DATA_KEY_INTRO_DISPLAYED = "introDisplayed";

    private static final String DATA_KEY_ALPHA_SKIP = "alphaSkip";
    private static final String DATA_KEY_ALPHA_STEP1 = "alphaStep1";
    private static final String DATA_KEY_ALPHA_STEP2 = "alphaStep2";
    private static final String DATA_KEY_ALPHA_STEP3 = "alphaStep3";
    private static final String DATA_KEY_ALPHA_STEP4 = "alphaStep4";
    // Data keys

    private static final int INTRO_PAGE_COUNT = 5;

    //
    public static class IntroFragment extends Fragment {

        public static final String DATA_KEY_POSITION = "position";
        // Data keys

        public static Fragment newInstance(int position) {

            Bundle args = new Bundle();
            args.putInt(DATA_KEY_POSITION, position);

            IntroFragment fragment = new IntroFragment();
            fragment.setArguments(args);
            return fragment;
        }

        public static float getSizeRatio(Activity activity) {
        // Return size ratio for all representation images

            Point screenSize = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(screenSize);

            int backHeight = screenSize.y;
            if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                backHeight >>= 1; // Half height for portrait
            else // Include layout padding for landscape
                backHeight -= activity.getResources().getDimensionPixelSize(R.dimen.intro_padding_bottom);

            // Include background image padding
            backHeight -= activity.getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin) << 1;

            return (float)backHeight / (float)Constants.INTRO_BACKGROUND_IMAGE_HEIGHT;
        }

        //
        public static final int INTRO_LIGHT_1_TRANS_X = -143; // Light #1 image horizontal position (from middle screen)
        private static final int INTRO_LIGHT_1_TRANS_Y = 32; // Light #1 image vertical position (from screen top)
        public static final int INTRO_LIGHT_2_TRANS_X = 132; // Light #2 image horizontal position (from middle screen)
        private static final int INTRO_LIGHT_2_TRANS_Y = 106; // Light #2 image vertical position (from screen top)
        public static final int INTRO_DISK_TRAY_TRANS_X = -89; // Disk tray image horizontal position (from middle screen)
        private static final int INTRO_DISK_TRAY_TRANS_Y = 243; // Disk tray image vertical position (from screen top)
        public static final int INTRO_SOUND_SPEAKER_TRANS_X = 128; // Sound speaker image horizontal position (from middle screen)
        private static final int INTRO_SOUND_SPEAKER_TRANS_Y = 273; // Sound speaker image vertical position (from screen top)
        public static final int INTRO_SMILEY_TRANS_X = -124; // Smiley image horizontal position (from middle screen)
        private static final int INTRO_SMILEY_TRANS_Y = 193; // Smiley image vertical position (from screen top)
        public static final int INTRO_UN_SMILEY_TRANS_X = 64; // Un smiley image horizontal position (from middle screen)
        private static final int INTRO_UN_SMILEY_TRANS_Y = 225; // Un smiley image vertical position (from screen top)

        public static final int INTRO_LINK_TRANS_X = -71; // Publication link image horizontal position (from middle screen)
        public static final int INTRO_LINK_TRANS_Y = 255; // Publication link image vertical position (from screen top)
        public static final int INTRO_PHOTO_TRANS_X = 60; // Publication photo horizontal position (from middle screen)
        public static final int INTRO_PHOTO_TRANS_Y = 73; // Publication photo vertical position (from screen top)
        public static final int INTRO_FRIEND_TRANS_X = -118; // Publication friend image horizontal position (from middle screen)
        public static final int INTRO_FRIEND_TRANS_Y = 142; // Publication friend image vertical position (from screen top)

        private static final int INTRO_GIRLS_TRANS_X = -128; // Girls photo horizontal position (from middle screen)
        private static final int INTRO_GIRLS_TRANS_Y = 253; // Girls photo vertical position (from screen top)
        public static final int INTRO_GIRLS_ROTATION_Y = 30; // Girls photo vertical rotation
        private static final int INTRO_COUPLE_TRANS_X = 94; // Couple photo horizontal position (from middle screen)
        private static final int INTRO_COUPLE_TRANS_Y = 137; // Couple photo vertical position (from screen top)
        public static final int INTRO_COUPLE_ROTATION_Y = -25; // Couple photo vertical rotation
        private static final int INTRO_OUTDOOR_TRANS_X = -118; // Outdoor party photo horizontal position (from middle screen)
        private static final int INTRO_OUTDOOR_TRANS_Y = 141; // Outdoor party photo vertical position (from screen top)
        private static final int INTRO_DJ_TRANS_X = 59; // DJ photo horizontal position (from middle screen)
        private static final int INTRO_DJ_TRANS_Y = 38; // DJ photo vertical position (from screen top)
        private static final int INTRO_INDOOR_TRANS_X = -73; // Indoor party photo horizontal position (from middle screen)
        private static final int INTRO_INDOOR_TRANS_Y = 50; // Indoor party photo vertical position (from screen top)
        public static final int INTRO_INDOOR_ROTATION_Y = -35; // Indoor party photo vertical rotation

        private static final int INTRO_EVENTS_TRANS_X = -90; // Events image horizontal position (from middle screen)
        private static final int INTRO_EVENTS_TRANS_Y = 172; // Events image vertical position (from screen top)
        public static final float INTRO_EVENTS_SCALE = 0.7846f; // Events image scale
        private static final int INTRO_FLYER_TRANS_X = -40; // Calendar image horizontal position (from middle screen)
        private static final int INTRO_FLYER_TRANS_Y = -57; // Calendar image vertical position (from screen top)
        public static final float INTRO_FLYER_SCALE = 0.7942f; // Flyer image scale
        private static final int INTRO_CALENDAR_TRANS_X = 109; // Flyer image horizontal position (from middle screen)
        private static final int INTRO_CALENDAR_TRANS_Y = 218; // Flyer image vertical position (from screen top)

        private void position(View root) { // Position the representation images

            float sizeRatio = getSizeRatio(getActivity());
            ImageView container = (ImageView)root.findViewById(R.id.image_container);
            ((RelativeLayout.LayoutParams)container.getLayoutParams()).height =
                    (int)(Constants.INTRO_CONTAINER_IMAGE_HEIGHT * sizeRatio);

            switch (getArguments().getInt(DATA_KEY_POSITION)) {

                case 0: { // Welcome

                    ImageView ball = (ImageView)root.findViewById(R.id.image_ball);
                    ((RelativeLayout.LayoutParams)ball.getLayoutParams()).height =
                            (int)(Constants.INTRO_BALL_IMAGE_HEIGHT * sizeRatio);

                    ImageView light1 = (ImageView)root.findViewById(R.id.image_light1);
                    ((RelativeLayout.LayoutParams)light1.getLayoutParams()).height =
                            (int)(Constants.INTRO_LIGHT_IMAGE_HEIGHT * sizeRatio);
                    light1.setTranslationX(INTRO_LIGHT_1_TRANS_X * sizeRatio);
                    light1.setTranslationY(INTRO_LIGHT_1_TRANS_Y * sizeRatio);

                    ImageView light2 = (ImageView)root.findViewById(R.id.image_light2);
                    ((RelativeLayout.LayoutParams)light2.getLayoutParams()).height =
                            (int)(Constants.INTRO_LIGHT_IMAGE_HEIGHT * sizeRatio);
                    light2.setTranslationX(INTRO_LIGHT_2_TRANS_X * sizeRatio);
                    light2.setTranslationY(INTRO_LIGHT_2_TRANS_Y * sizeRatio);

                    ImageView diskTray = (ImageView)root.findViewById(R.id.image_disk_tray);
                    ((RelativeLayout.LayoutParams)diskTray.getLayoutParams()).height =
                            (int)(Constants.INTRO_DISK_TRAY_IMAGE_HEIGHT * sizeRatio);
                    diskTray.setTranslationX(INTRO_DISK_TRAY_TRANS_X * sizeRatio);
                    diskTray.setTranslationY(INTRO_DISK_TRAY_TRANS_Y * sizeRatio);

                    ImageView speaker = (ImageView)root.findViewById(R.id.image_sound_speaker);
                    ((RelativeLayout.LayoutParams)speaker.getLayoutParams()).height =
                            (int)(Constants.INTRO_SOUND_SPEAKER_IMAGE_HEIGHT * sizeRatio);
                    speaker.setTranslationX(INTRO_SOUND_SPEAKER_TRANS_X * sizeRatio);
                    speaker.setTranslationY(INTRO_SOUND_SPEAKER_TRANS_Y * sizeRatio);

                    ImageView smiley = (ImageView)root.findViewById(R.id.image_smiley);
                    ((RelativeLayout.LayoutParams)smiley.getLayoutParams()).height =
                            (int)(Constants.INTRO_SMILEY_IMAGE_HEIGHT * sizeRatio);
                    smiley.setTranslationX(INTRO_SMILEY_TRANS_X * sizeRatio);
                    smiley.setTranslationY(INTRO_SMILEY_TRANS_Y * sizeRatio);

                    ImageView unSmiley = (ImageView)root.findViewById(R.id.image_un_smiley);
                    ((RelativeLayout.LayoutParams)unSmiley.getLayoutParams()).height =
                            (int)(Constants.INTRO_SMILEY_IMAGE_HEIGHT * sizeRatio);
                    unSmiley.setTranslationX(INTRO_UN_SMILEY_TRANS_X * sizeRatio);
                    unSmiley.setTranslationY(INTRO_UN_SMILEY_TRANS_Y * sizeRatio);
                    break;
                }
                case 1: { // Publications

                    ImageView link = (ImageView)root.findViewById(R.id.image_link);
                    ((RelativeLayout.LayoutParams)link.getLayoutParams()).height =
                            (int)(Constants.INTRO_LINK_IMAGE_HEIGHT * sizeRatio);
                    link.setTranslationX(INTRO_LINK_TRANS_X * sizeRatio);
                    link.setTranslationY(INTRO_LINK_TRANS_Y * sizeRatio);

                    ImageView photo = (ImageView)root.findViewById(R.id.image_photo);
                    ((RelativeLayout.LayoutParams)photo.getLayoutParams()).height =
                            (int)(Constants.INTRO_PHOTO_IMAGE_HEIGHT * sizeRatio);
                    photo.setTranslationX(INTRO_PHOTO_TRANS_X * sizeRatio);
                    photo.setTranslationY(INTRO_PHOTO_TRANS_Y * sizeRatio);

                    ImageView friend = (ImageView)root.findViewById(R.id.image_friend);
                    ((RelativeLayout.LayoutParams)friend.getLayoutParams()).height =
                            (int)(Constants.INTRO_FRIEND_IMAGE_HEIGHT * sizeRatio);
                    friend.setTranslationX(INTRO_FRIEND_TRANS_X * sizeRatio);
                    friend.setTranslationY(INTRO_FRIEND_TRANS_Y * sizeRatio);
                    break;
                }
                case 2: { // Album photos

                    ImageView girls = (ImageView)root.findViewById(R.id.image_girls);
                    ((RelativeLayout.LayoutParams)girls.getLayoutParams()).height =
                            (int)(Constants.INTRO_GIRLS_IMAGE_HEIGHT * sizeRatio);
                    girls.setTranslationX(INTRO_GIRLS_TRANS_X * sizeRatio);
                    girls.setTranslationY(INTRO_GIRLS_TRANS_Y * sizeRatio);
                    girls.setRotationY(INTRO_GIRLS_ROTATION_Y);

                    ImageView couple = (ImageView)root.findViewById(R.id.image_couple);
                    ((RelativeLayout.LayoutParams)couple.getLayoutParams()).height =
                            (int)(Constants.INTRO_COUPLE_IMAGE_HEIGHT * sizeRatio);
                    couple.setTranslationX(INTRO_COUPLE_TRANS_X * sizeRatio);
                    couple.setTranslationY(INTRO_COUPLE_TRANS_Y * sizeRatio);
                    couple.setRotationY(INTRO_COUPLE_ROTATION_Y);

                    ImageView outdoor = (ImageView)root.findViewById(R.id.image_outdoor);
                    ((RelativeLayout.LayoutParams)outdoor.getLayoutParams()).height =
                            (int)(Constants.INTRO_OUTDOOR_IMAGE_HEIGHT * sizeRatio);
                    outdoor.setTranslationX(INTRO_OUTDOOR_TRANS_X * sizeRatio);
                    outdoor.setTranslationY(INTRO_OUTDOOR_TRANS_Y * sizeRatio);

                    ImageView dj = (ImageView)root.findViewById(R.id.image_dj);
                    ((RelativeLayout.LayoutParams)dj.getLayoutParams()).height =
                            (int)(Constants.INTRO_DJ_IMAGE_HEIGHT * sizeRatio);
                    dj.setTranslationX(INTRO_DJ_TRANS_X * sizeRatio);
                    dj.setTranslationY(INTRO_DJ_TRANS_Y * sizeRatio);

                    ImageView indoor = (ImageView)root.findViewById(R.id.image_indoor);
                    ((RelativeLayout.LayoutParams)indoor.getLayoutParams()).height =
                            (int)(Constants.INTRO_INDOOR_IMAGE_HEIGHT * sizeRatio);
                    indoor.setTranslationX(INTRO_INDOOR_TRANS_X * sizeRatio);
                    indoor.setTranslationY(INTRO_INDOOR_TRANS_Y * sizeRatio);
                    indoor.setRotationY(INTRO_INDOOR_ROTATION_Y);
                    break;
                }
                case 3: { // Location






                    break;
                }
                case 4: { // Events

                    ImageView events = (ImageView)root.findViewById(R.id.image_events);
                    ((RelativeLayout.LayoutParams)events.getLayoutParams()).height =
                            (int)(Constants.INTRO_EVENTS_IMAGE_HEIGHT * sizeRatio);
                    events.setTranslationX(INTRO_EVENTS_TRANS_X * sizeRatio);
                    events.setTranslationY(INTRO_EVENTS_TRANS_Y * sizeRatio);
                    events.setScaleX(IntroFragment.INTRO_EVENTS_SCALE);
                    events.setScaleY(IntroFragment.INTRO_EVENTS_SCALE);

                    ImageView calendar = (ImageView)root.findViewById(R.id.image_calendar);
                    ((RelativeLayout.LayoutParams)calendar.getLayoutParams()).height =
                            (int)(Constants.INTRO_CALENDAR_IMAGE_HEIGHT * sizeRatio);
                    calendar.setTranslationX(INTRO_CALENDAR_TRANS_X * sizeRatio);
                    calendar.setTranslationY(INTRO_CALENDAR_TRANS_Y * sizeRatio);

                    ImageView flyer = (ImageView)root.findViewById(R.id.image_flyer);
                    ((RelativeLayout.LayoutParams)flyer.getLayoutParams()).height =
                            (int)(Constants.INTRO_FLYER_IMAGE_HEIGHT * sizeRatio);
                    flyer.setTranslationX(INTRO_FLYER_TRANS_X * sizeRatio);
                    flyer.setTranslationY(INTRO_FLYER_TRANS_Y * sizeRatio);
                    flyer.setScaleX(IntroFragment.INTRO_FLYER_SCALE);
                    flyer.setScaleY(IntroFragment.INTRO_FLYER_SCALE);
                    break;
                }
            }
        }

        //////
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Logs.add(Logs.Type.V, null);
            View rootView = inflater.inflate(R.layout.fragment_intro, container, false);
            ViewStub representation = (ViewStub)rootView.findViewById(R.id.representation_container);
            switch (getArguments().getInt(DATA_KEY_POSITION)) {

                case 0: { // Welcome
                    representation.setLayoutResource(R.layout.layout_intro_welcome);
                    break;
                }
                case 1: { // Publications
                    representation.setLayoutResource(R.layout.layout_intro_publications);
                    break;
                }
                case 2: { // Album photos
                    representation.setLayoutResource(R.layout.layout_intro_albums);
                    break;
                }
                case 3: { // Location
                    representation.setLayoutResource(R.layout.layout_intro_location);
                    break;
                }
                case 4: { // Events
                    representation.setLayoutResource(R.layout.layout_intro_events);
                    break;
                }
            }
            representation.inflate();

            // Assign left & right page tag
            if (getArguments().getInt(DATA_KEY_POSITION) == 0)
                rootView.setTag(LimitlessViewPager.TAG_PAGE_LEFT_TOP);
            else if (getArguments().getInt(DATA_KEY_POSITION) == (INTRO_PAGE_COUNT - 1))
                rootView.setTag(LimitlessViewPager.TAG_PAGE_RIGHT_BOTTOM);

            // Position representation images
            position(rootView);

            // Configure comments
            TextView title = (TextView)rootView.findViewById(R.id.title);
            TextView description = (TextView)rootView.findViewById(R.id.description);
            switch (getArguments().getInt(DATA_KEY_POSITION)) {

                case 0: { // Welcome

                    title.setText(getResources().getString(R.string.welcome));
                    description.setText(getResources().getString(R.string.welcome_text, Constants.APP_NAME));

                    // Add web site link to "LeClassico"
                    Pattern linkMatcher = Pattern.compile(Constants.APP_NAME);
                    String urlLeClassico = Constants.APP_WEBSITE;
                    Linkify.addLinks(description, linkMatcher, urlLeClassico);
                    break;
                }
                case 1: { // Publications

                    title.setText(getResources().getString(R.string.publications));
                    description.setText(getResources().getString(R.string.publications_text));
                    break;
                }
                case 2: { // Album photos

                    title.setText(getResources().getString(R.string.albums));
                    description.setText(getResources().getString(R.string.albums_text));
                    break;
                }
                case 3: { // Location

                    title.setText(getResources().getString(R.string.location));
                    description.setText(getResources().getString(R.string.location_text));
                    break;
                }
                case 4: { // Events

                    title.setText(getResources().getString(R.string.events));
                    description.setText(getResources().getString(R.string.events_text));
                    break;
                }
            }
            return rootView;
        }
    }

    private void skipIntro() { // Display connection layout

        Logs.add(Logs.Type.V, null);
        CoordinatorLayout layout = ((CoordinatorLayout) mViewPager.getParent());
        layout.clearAnimation();
        layout.setTranslationX(0);
        TranslateAnimation anim = new TranslateAnimation(0f, -mViewPager.getWidth(), 0f, 0f);
        anim.setFillAfter(true);
        anim.setDuration(300);
        layout.startAnimation(anim);

        mIntroDisplayed = true;
    }

    //
    private LimitlessViewPager mViewPager; // Introduction view pager component
    private boolean mIntroDisplayed; // Introduction display flag

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

    public void onNextStep(View sender) {

        Logs.add(Logs.Type.V, "sender: " + sender);
        if (mViewPager.getCurrentItem() < (INTRO_PAGE_COUNT - 1))
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);

        else
            skipIntro();
    }
    public void onSkipIntro(View sender) {

        Logs.add(Logs.Type.V, "sender: " + sender);
        skipIntro();
    }

    //////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Restore data
        if (savedInstanceState != null) {

            mIntroDisplayed = savedInstanceState.getBoolean(DATA_KEY_INTRO_DISPLAYED);

            mAlphaSkip = savedInstanceState.getFloat(DATA_KEY_ALPHA_SKIP);
            mAlphaStep1 = savedInstanceState.getFloat(DATA_KEY_ALPHA_STEP1);
            mAlphaStep2 = savedInstanceState.getFloat(DATA_KEY_ALPHA_STEP2);
            mAlphaStep3 = savedInstanceState.getFloat(DATA_KEY_ALPHA_STEP3);
            mAlphaStep4 = savedInstanceState.getFloat(DATA_KEY_ALPHA_STEP4);
        }

        // Check to display intro
        if (mIntroDisplayed) {
            ((CoordinatorLayout)findViewById(R.id.main_content)).setVisibility(View.GONE);
            return;
        }

        // Set up controls panel
        final ImageButton skip = (ImageButton)findViewById(R.id.image_skip);
        final ImageView step1 = (ImageView)findViewById(R.id.image_step_1);
        final ImageView step2 = (ImageView)findViewById(R.id.image_step_2);
        final ImageView step3 = (ImageView)findViewById(R.id.image_step_3);
        final ImageView step4 = (ImageView)findViewById(R.id.image_step_4);
        applyAlpha(skip, step1, step2, step3, step4);

        // Set up the ViewPager
        mViewPager = (LimitlessViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return INTRO_PAGE_COUNT;
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

            private static final float TRANSLATE_RATIO_LINK_X = 25f;
            private static final float TRANSLATE_RATIO_LINK_Y = 50;
            private static final float TRANSLATE_RATIO_PHOTO_X = -40f;
            private static final float TRANSLATE_RATIO_PHOTO_Y = 50f;
            private static final float TRANSLATE_RATIO_FRIEND_X = 40f;
            private static final float TRANSLATE_RATIO_FRIEND_Y = 50f;

            private static final float ROTATION_RATIO_GIRLS_Y = -180f;
            private static final float ROTATION_RATIO_COUPLE_Y = -120f;
            private static final float ROTATION_RATIO_INDOOR_Y = -65f;
            private static final float ROTATION_RATIO_OUTDOOR_Y = 90f;
            private static final float ROTATION_RATIO_DJ_Y = -165f;

            private static final float SCALE_RATIO_EVENTS = 0.5f;
            private static final float SCALE_RATIO_CALENDAR = 0.6f;
            private static final float SCALE_RATIO_FLYER = 0.3f;

            private void scroll(boolean toTheLeft, View page, float position) {
            // Apply a scrolling to the representation images

                if (((toTheLeft) && (position < 0f)) || (((!toTheLeft) && (position < 1f)))) {
                    float sizeRatio = IntroFragment.getSizeRatio(IntroActivity.this);

                    ////// Welcome
                    ImageView light1 = (ImageView)page.findViewById(R.id.image_light1);
                    if (light1 != null)
                        light1.setTranslationX((IntroFragment.INTRO_LIGHT_1_TRANS_X * sizeRatio) +
                                (position * TRANSLATE_RATIO_LIGHT_1));
                    ImageView ball = (ImageView)page.findViewById(R.id.image_ball);
                    if (ball != null)
                        ball.setTranslationX(position * TRANSLATE_RATIO_BALL);
                    ImageView light2 = (ImageView)page.findViewById(R.id.image_light2);
                    if (light2 != null)
                        light2.setTranslationX((IntroFragment.INTRO_LIGHT_2_TRANS_X * sizeRatio) +
                                (position * TRANSLATE_RATIO_LIGHT_2));
                    ImageView diskTray = (ImageView)page.findViewById(R.id.image_disk_tray);
                    if (diskTray != null)
                        diskTray.setTranslationX((IntroFragment.INTRO_DISK_TRAY_TRANS_X * sizeRatio) +
                                (position * TRANSLATE_RATIO_DISK_TRAY));
                    ImageView speaker = (ImageView)page.findViewById(R.id.image_sound_speaker);
                    if (speaker != null)
                        speaker.setTranslationX((IntroFragment.INTRO_SOUND_SPEAKER_TRANS_X * sizeRatio) +
                                (position * TRANSLATE_RATIO_SOUND_SPEAKER));
                    ImageView smiley = (ImageView)page.findViewById(R.id.image_smiley);
                    if (smiley != null)
                        smiley.setTranslationX((IntroFragment.INTRO_SMILEY_TRANS_X * sizeRatio) +
                                (position * TRANSLATE_RATIO_SMILEY));
                    ImageView unSmiley = (ImageView)page.findViewById(R.id.image_un_smiley);
                    if (unSmiley != null)
                        unSmiley.setTranslationX((IntroFragment.INTRO_UN_SMILEY_TRANS_X * sizeRatio) +
                                (position * TRANSLATE_RATIO_UN_SMILEY));

                    ////// Publications
                    ImageView link = (ImageView)page.findViewById(R.id.image_link);
                    if (link != null) {
                        link.setTranslationX((IntroFragment.INTRO_LINK_TRANS_X * sizeRatio) +
                                (position * TRANSLATE_RATIO_LINK_X));
                        link.setTranslationY((IntroFragment.INTRO_LINK_TRANS_Y * sizeRatio) +
                                (position * TRANSLATE_RATIO_LINK_Y));
                    }
                    ImageView photo = (ImageView)page.findViewById(R.id.image_photo);
                    if (photo != null) {
                        photo.setTranslationX((IntroFragment.INTRO_PHOTO_TRANS_X * sizeRatio) +
                                (position * TRANSLATE_RATIO_PHOTO_X));
                        photo.setTranslationY((IntroFragment.INTRO_PHOTO_TRANS_Y * sizeRatio) +
                                (position * TRANSLATE_RATIO_PHOTO_Y));
                    }
                    ImageView friend = (ImageView)page.findViewById(R.id.image_friend);
                    if (friend != null) {
                        friend.setTranslationX((IntroFragment.INTRO_FRIEND_TRANS_X * sizeRatio) +
                                (position * TRANSLATE_RATIO_FRIEND_X));
                        friend.setTranslationY((IntroFragment.INTRO_FRIEND_TRANS_Y * sizeRatio) +
                                (position * TRANSLATE_RATIO_FRIEND_Y));
                    }

                    ////// Album photos
                    ImageView girls = (ImageView)page.findViewById(R.id.image_girls);
                    if (girls != null)
                        girls.setRotationY(IntroFragment.INTRO_GIRLS_ROTATION_Y +
                                (position * ROTATION_RATIO_GIRLS_Y));
                    ImageView couple = (ImageView)page.findViewById(R.id.image_couple);
                    if (couple != null)
                        couple.setRotationY(IntroFragment.INTRO_COUPLE_ROTATION_Y +
                                (position * ROTATION_RATIO_COUPLE_Y));
                    ImageView indoor = (ImageView)page.findViewById(R.id.image_indoor);
                    if (indoor != null)
                        indoor.setRotationY(IntroFragment.INTRO_INDOOR_ROTATION_Y +
                                (position * ROTATION_RATIO_INDOOR_Y));
                    ImageView outdoor = (ImageView)page.findViewById(R.id.image_outdoor);
                    if (outdoor != null)
                        outdoor.setRotationY(position * ROTATION_RATIO_OUTDOOR_Y);
                    ImageView dj = (ImageView)page.findViewById(R.id.image_dj);
                    if (dj != null)
                        dj.setRotationY(position * ROTATION_RATIO_DJ_Y);

                    ////// Location










                    ////// Events
                    ImageView events = (ImageView) page.findViewById(R.id.image_events);
                    if (events != null) {
                        events.setScaleX(IntroFragment.INTRO_EVENTS_SCALE +
                                (position * SCALE_RATIO_EVENTS));
                        events.setScaleY(IntroFragment.INTRO_EVENTS_SCALE +
                                (position * SCALE_RATIO_EVENTS));
                    }
                    ImageView calendar = (ImageView)page.findViewById(R.id.image_calendar);
                    if (calendar != null) {
                        calendar.setScaleX(1f + (position * SCALE_RATIO_CALENDAR));
                        calendar.setScaleY(1f + (position * SCALE_RATIO_CALENDAR));
                    }
                    ImageView flyer = (ImageView)page.findViewById(R.id.image_flyer);
                    if (flyer != null) {
                        flyer.setScaleX(IntroFragment.INTRO_FLYER_SCALE +
                                (position * SCALE_RATIO_FLYER));
                        flyer.setScaleY(IntroFragment.INTRO_FLYER_SCALE +
                                (position * SCALE_RATIO_FLYER));
                    }
                }
            }

            @Override
            public void transformPage(View page, float position) {

                if (position <= 0f) { // This page is moving out to the left
                    scroll(true, page, position);
                } else if (position <= 1f) { // This page is moving in from the right
                    scroll(false, page, position);
                }
            }
        });
        mViewPager.setOnLimitlessListener(new LimitlessViewPager.OnLimitCrossedListener() {

            private static final float RATIO_ROTATION = 0.05f / 800f;
            private float mRotation;

            private static final float RATIO_START_BEHAVIOR = 0.25f;
            // Start to translate ViewPager when this % of the screen width is reached
            private static final float RATIO_DISPLAY_CONNECT = 0.4f;
            // Display connection layout when this % of the screen width is reached

            private boolean mScrolling; // Right page scrolling flag
            private float mTranslate;

            @Override
            public boolean onStartBehavior(boolean leftTop, float originX, float originY) {
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

                View page = mViewPager.findViewWithTag((leftTop) ?
                        LimitlessViewPager.TAG_PAGE_LEFT_TOP : LimitlessViewPager.TAG_PAGE_RIGHT_BOTTOM);
                if (leftTop) {

                    if ((mRotation > 0f) || (mRotation < 0f)) {

                        ObjectAnimator animation = ObjectAnimator.ofFloat(page, "rotationY",
                                mRotation, 0f);
                        animation.setDuration(500);
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

                        anim = new TranslateAnimation(mTranslate, -mViewPager.getWidth(), 0f, 0f);
                        anim.setFillAfter(true);
                        anim.setDuration(200);

                        mIntroDisplayed = true;

                    } else {
                        anim = new TranslateAnimation(mTranslate, 0f, 0f, 0f);
                        anim.setDuration(500);
                    }
                    layout.startAnimation(anim);
                }
                mScrolling = false;
                return true;
            }

            @Override
            public boolean onLeftTopLimitCrossed(View page, float deltaX, float deltaY) {
                if (deltaX > 0f) {

                    float screenRatio = mViewPager.getWidth() * RATIO_ROTATION;
                    mRotation = deltaX * screenRatio;
                    page.setRotationY(mRotation);
                }
                return false;
            }

            @Override
            public boolean onRightBottomLimitCrossed(View page, float deltaX, float deltaY) {
                if (!mScrolling)
                    return false;

                if (deltaX < 0f) {

                    mTranslate += deltaX;
                    ((CoordinatorLayout) page.getParent().getParent()).setTranslationX(mTranslate);
                }
                return true;
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private static final float ALPHA_NO_SELECTION = 0.3f;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
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
    protected void onSaveInstanceState(Bundle outState) {

        outState.putBoolean(DATA_KEY_INTRO_DISPLAYED, mIntroDisplayed);

        outState.putFloat(DATA_KEY_ALPHA_SKIP, mAlphaSkip);
        outState.putFloat(DATA_KEY_ALPHA_STEP1, mAlphaStep1);
        outState.putFloat(DATA_KEY_ALPHA_STEP2, mAlphaStep2);
        outState.putFloat(DATA_KEY_ALPHA_STEP3, mAlphaStep3);
        outState.putFloat(DATA_KEY_ALPHA_STEP4, mAlphaStep4);

        Logs.add(Logs.Type.V, "outState: " + outState);
        super.onSaveInstanceState(outState);
    }
}
