package com.studio.artaban.leclassico;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.studio.artaban.leclassico.components.CurvedViewPager;
import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 15/07/16.
 * Introduction activity class
 */
public class IntroActivity extends AppCompatActivity {

    private static final String DATA_KEY_ALPHA = "alpha";
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
            View rootView = inflater.inflate(R.layout.fragment_intro, container, false);

            // Assign left & right page tag
            if (getArguments().getInt(DATA_KEY_POSITION) == 0)
                rootView.setTag(CurvedViewPager.TAG_LEFT_PAGE);
            else if (getArguments().getInt(DATA_KEY_POSITION) == (INTRO_PAGE_COUNT - 1))
                rootView.setTag(CurvedViewPager.TAG_RIGHT_PAGE);








            TextView framePosition = (TextView) rootView.findViewById(R.id.section_label);
            framePosition.setText("#" + getArguments().getInt(DATA_KEY_POSITION));
            //mFrameImage = (ImageView)rootView.findViewById(R.id.frame_image);







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
    private CurvedViewPager mViewPager;
    private float mAlphaFab;

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







        mViewPager = (CurvedViewPager) findViewById(R.id.container);
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
            @Override
            public void transformPage(View page, float position) {

                if (position <= 0) // This page is moving out to the left
                    page.setAlpha(1.f + (position * 2.f));
                else if (position <= 1) // This page is moving in from the right
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
                if (position == (INTRO_PAGE_COUNT - 2)) {

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
