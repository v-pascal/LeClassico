package com.studio.artaban.leclassico;

import android.animation.ObjectAnimator;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.studio.artaban.leclassico.components.LimitlessViewPager;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 15/07/16.
 * Introduction activity class
 */
public class IntroActivity extends AppCompatActivity {

    private static final String DATA_KEY_INTRO_DISPLAYED = "introDisplayed";
    // Data keys

    private static final int INTRO_PAGE_COUNT = 5;

    //
    public static class PlaceholderFragment extends Fragment {

        public static PlaceholderFragment newInstance(int position) {

            Bundle args = new Bundle();
            args.putInt(DATA_KEY_POSITION, position);

            PlaceholderFragment fragment = new PlaceholderFragment();
            fragment.setArguments(args);
            return fragment;
        }

        //
        private static final String DATA_KEY_POSITION = "position";

        //////
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Logs.add(Logs.Type.V, null);
            View rootView = (getArguments().getInt(DATA_KEY_POSITION) == 0)?
                    inflater.inflate(R.layout.fragment_welcome, container, false):
                    inflater.inflate(R.layout.fragment_intro, container, false);

            // Assign left & right page tag
            if (getArguments().getInt(DATA_KEY_POSITION) == 0)
                rootView.setTag(LimitlessViewPager.TAG_PAGE_LEFT_TOP);
            else if (getArguments().getInt(DATA_KEY_POSITION) == (INTRO_PAGE_COUNT - 1))
                rootView.setTag(LimitlessViewPager.TAG_PAGE_RIGHT_BOTTOM);








            if (getArguments().getInt(DATA_KEY_POSITION) != 0) {
                TextView framePosition = (TextView) rootView.findViewById(R.id.section_label);
                framePosition.setText("#" + getArguments().getInt(DATA_KEY_POSITION));
                //mFrameImage = (ImageView)rootView.findViewById(R.id.frame_image);
            }







            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Logs.add(Logs.Type.V, null);





            /*
            Bitmap bitmap = openBitmapFile(getArguments().getInt(DATA_KEY_SYNCHRO_OFFSET),
                    getArguments().getBoolean(DATA_KEY_SYNCHRO_LOCAL));
            if (bitmap != null)
                mFrameImage.setImageBitmap(bitmap);
                */





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
    private LimitlessViewPager mViewPager;
    private boolean mIntroDisplayed;

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
        if (savedInstanceState != null)
            mIntroDisplayed = savedInstanceState.getBoolean(DATA_KEY_INTRO_DISPLAYED, false);

        // Check to display intro
        if (mIntroDisplayed) {
            ((CoordinatorLayout)findViewById(R.id.main_content)).setVisibility(View.GONE);
            return;
        }

        final ImageButton skip = (ImageButton)findViewById(R.id.image_skip);
        final ImageView step1 = (ImageView)findViewById(R.id.image_step_1);
        final ImageView step2 = (ImageView)findViewById(R.id.image_step_2);
        final ImageView step3 = (ImageView)findViewById(R.id.image_step_3);
        final ImageView step4 = (ImageView)findViewById(R.id.image_step_4);

        // Set up the ViewPager
        mViewPager = (LimitlessViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return INTRO_PAGE_COUNT;
            }

            @Override
            public Fragment getItem(int position) {
                return PlaceholderFragment.newInstance(position);
            }
        });
        mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {

            private void scroll(View page, float position) { // Apply a scrolling to the concert stage images
                if ((mViewPager.getCurrentItem() == 0) && (position < 0f)) {







                    ImageView ball = (ImageView)page.findViewById(R.id.image_ball);
                    ball.setTranslationX(position * 120f);






                }
            }

            @Override
            public void transformPage(View page, float position) {

                if (position <= 0f) { // This page is moving out to the left

                    scroll(page, position);
                    page.setAlpha(1f + (position * 2f));

                } else if (position <= 1f) { // This page is moving in from the right
                    page.setAlpha(1f - (position * 2f));
                }
            }
        });
        mViewPager.setOnLimitlessListener(new LimitlessViewPager.OnLimitCrossedListener() {

            private static final float RATIO_ROTATION = 0.06f;
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

                    mRotation = deltaX * RATIO_ROTATION;
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

                        skip.setAlpha(positionOffset);
                        step1.setAlpha(positionOffset);
                        step2.setAlpha(positionOffset * ALPHA_NO_SELECTION);
                        step3.setAlpha(positionOffset * ALPHA_NO_SELECTION);
                        step4.setAlpha(positionOffset * ALPHA_NO_SELECTION);
                        break;
                    }
                    case 1: { // 1 -> 2

                        if (positionOffset > 0f) {
                            step1.setAlpha((positionOffset * (ALPHA_NO_SELECTION - 1f)) + 1f);
                            step2.setAlpha((positionOffset * (1f - ALPHA_NO_SELECTION)) + ALPHA_NO_SELECTION);
                        }
                        break;
                    }
                    case 2: { // 2 -> 3

                        if (positionOffset > 0f) {
                            step2.setAlpha((positionOffset * (ALPHA_NO_SELECTION - 1f)) + 1f);
                            step3.setAlpha((positionOffset * (1f - ALPHA_NO_SELECTION)) + ALPHA_NO_SELECTION);
                        }
                        break;
                    }
                    case 3: { // 3 -> 4

                        if (positionOffset > 0f) {
                            step3.setAlpha((positionOffset * (ALPHA_NO_SELECTION - 1f)) + 1f);
                            step4.setAlpha((positionOffset * (1f - ALPHA_NO_SELECTION)) + ALPHA_NO_SELECTION);
                        }
                        break;
                    }
                }
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

        Logs.add(Logs.Type.V, "outState: " + outState);
        super.onSaveInstanceState(outState);
    }
}
