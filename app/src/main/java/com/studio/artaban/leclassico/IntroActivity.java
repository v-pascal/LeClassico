package com.studio.artaban.leclassico;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 15/07/16.
 * Introduction activity class
 */
public class IntroActivity extends AppCompatActivity {

    private static final String DATA_KEY_ALPHA = "alpha";

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
        private ImageView mFrameImage;




        /*
        private int mPointerId = Constants.NO_DATA;
        private float mPrevX = 0.f;

        private boolean moveLimit(boolean begin, MotionEvent event, View view) {





            switch (event.getActionMasked()) {

                case MotionEvent.ACTION_DOWN: {
                    if (mPointerId == Constants.NO_DATA) {

                        Logs.add(Logs.Type.I, "Start limit scroll");
                        mPointerId = event.getPointerId(0);
                        mPrevX = event.getX(0);
                        //return true;
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    if (mPointerId == Constants.NO_DATA)
                        break;

                    for (int i = 0; i < event.getPointerCount(); ++i) {
                        if (event.getPointerId(i) == mPointerId) {

                            Logs.add(Logs.Type.I, "Finish limit scroll");
                            mPointerId = Constants.NO_DATA;
                            mPrevX = 0f;


                            view.setRotationY(0);


                            break;
                        }
                    }
                    //return true;
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (mPointerId == Constants.NO_DATA)
                        break;

                    for (int i = 0; i < event.getPointerCount(); ++i) {
                        if (event.getPointerId(i) == mPointerId) {




                            int deltaX = (int)(event.getX(i) - mPrevX);
                            if (deltaX > 10) {


                                //view.setRotationX(deltaX << 1);
                                Logs.add(Logs.Type.V, "OK: " + deltaX);
                                //view.setRotationY(45);
                                view.setRotationY(deltaX);


                            }
                            else
                                Logs.add(Logs.Type.V, "BAD: " + deltaX);




                            //mPrevX = event.getX(i);
                            break;
                        }
                    }
                    //return true;
                    break;
                }
            }






            //return false;
            return view.onTouchEvent(event);
        }
        */

        //////
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Logs.add(Logs.Type.V, null);




            View rootView = inflater.inflate(R.layout.fragment_intro, container, false);
            TextView framePosition = (TextView) rootView.findViewById(R.id.section_label);
            framePosition.setText("#" + getArguments().getInt(DATA_KEY_POSITION));
            //mFrameImage = (ImageView)rootView.findViewById(R.id.frame_image);





            /*
            switch (getArguments().getInt(DATA_KEY_POSITION, 0)) {
                case 0: {
                    container.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent event) {

                            //Logs.add(Logs.Type.V, "event: " + event);
                            return moveLimit(true, event, view);
                        }
                    });
                    break;
                }
                case 4: {
                    container.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent event) {

                            //Logs.add(Logs.Type.V, "event: " + event);
                            return moveLimit(false, event, view);
                        }
                    });
                    break;
                }
            }
            */



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











    //
    //private ViewPager mViewPager;
    private CustomViewPager mViewPager;
    private float mAlphaFab = 0.f;





    //////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.add(Logs.Type.V, "savedInstanceState: " + savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Restore data
        if (savedInstanceState != null)
            mAlphaFab = savedInstanceState.getFloat(DATA_KEY_ALPHA);





        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setAlpha(mAlphaFab);






        //mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager = (CustomViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override public int getCount() { return 5; }
            @Override
            public Fragment getItem(int position) {
                return PlaceholderFragment.newInstance(position);
            }
        });
        mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {

                if (position <= 0) { // This page is moving out to the left




                    if ((!(position > 0)) && (!(position < 0))) { // == 0
                        int delta = mViewPager.test();
                        Logs.add(Logs.Type.E, "DeltaX: " + delta);
                        page.setRotationY(delta * 0.1f);
                    }




                    page.setAlpha(1.f + (position * 2.f));
                } else if (position <= 1) // This page is moving in from the right
                    page.setAlpha(1.f - (position * 2.f));
            }
        });
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 3) {

                    mAlphaFab = positionOffset;
                    fab.setAlpha(positionOffset);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putFloat(DATA_KEY_ALPHA, mAlphaFab);

        Logs.add(Logs.Type.V, "outState: " + outState);
        super.onSaveInstanceState(outState);
    }
}
