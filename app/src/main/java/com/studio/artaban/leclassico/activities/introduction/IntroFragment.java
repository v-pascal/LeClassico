package com.studio.artaban.leclassico.activities.introduction;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.components.LimitlessViewPager;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Logs;

import java.util.regex.Pattern;

/**
 * Created by pascal on 05/08/16.
 * Introduction fragment class
 */
public class IntroFragment extends Fragment {

    public static final String DATA_KEY_POSITION = "position";
    // Data keys

    public static Fragment newInstance(int position) {

        Logs.add(Logs.Type.V, "position: " + position);
        Bundle args = new Bundle();
        args.putInt(DATA_KEY_POSITION, position);

        IntroFragment fragment = new IntroFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static float getSizeRatio(Activity activity) {
    // Return size ratio for all representation images

        //Logs.add(Logs.Type.V, "activity: " + activity);
        Point screenSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(screenSize);

        int backHeight = screenSize.y;
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            backHeight >>= 1; // Half height for portrait
        else // Include layout padding for landscape
            backHeight -= activity.getResources().getDimensionPixelSize(R.dimen.intro_padding_bottom);

        // Include background image padding
        backHeight -= activity.getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin) << 1;

        return (float)backHeight / (float) Constants.INTRO_BACKGROUND_IMAGE_HEIGHT;
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

    private static final float INTRO_EVENTS_TRANS_RATIO_X = 0.18f;
    // Events image horizontal position (from left linear parent item  & according screen width)
    private static final int INTRO_EVENTS_TRANS_Y = 172; // Events image vertical position (from screen top)
    public static final float INTRO_EVENTS_SCALE = 0.7846f; // Events image scale
    private static final float INTRO_FLYER_TRANS_RATIO_X = 0.27f;
    // Calendar image horizontal position (from left linear parent item  & according screen width)
    private static final int INTRO_FLYER_TRANS_Y = -57; // Calendar image vertical position (from screen top)
    public static final float INTRO_FLYER_SCALE = 0.7942f; // Flyer image scale
    private static final float INTRO_CALENDAR_TRANS_RATIO_X = 0.45f;
    // Flyer image horizontal position (from left linear parent item & according screen width)
    private static final int INTRO_CALENDAR_TRANS_Y = 218; // Flyer image vertical position (from screen top)

    private static final int INTRO_GREEN_MARKER_TRANS_X = 94; // Green marker image horizontal position (from middle screen)
    public static final int INTRO_GREEN_MARKER_TRANS_Y = 7; // Green marker image vertical position (from screen top)
    public static final int INTRO_RED_MARKER_TRANS_X = -83; // Red marker image horizontal position (from middle screen)
    public static final int INTRO_RED_MARKER_TRANS_Y = 82; // Red marker image vertical position (from screen top)
    public static final int INTRO_BLUE_MARKER_TRANS_X = 136; // Blue marker image horizontal position (from middle screen)
    public static final int INTRO_BLUE_MARKER_TRANS_Y = 148; // Blue marker image vertical position (from screen top)
    public static final int INTRO_YELLOW_MARKER_TRANS_X = 2; // Yellow marker image horizontal position (from middle screen)
    private static final int INTRO_YELLOW_MARKER_TRANS_Y = 167; // Yellow marker image vertical position (from screen top)

    private void position(View root) { // Position the representation images

        //Logs.add(Logs.Type.V, "root: " + root);
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

                ImageView greenMark = (ImageView)root.findViewById(R.id.image_green_marker);
                ((RelativeLayout.LayoutParams)greenMark.getLayoutParams()).height =
                        (int)(Constants.INTRO_MARKER_IMAGE_HEIGHT * sizeRatio);
                greenMark.setTranslationX(INTRO_GREEN_MARKER_TRANS_X * sizeRatio);
                greenMark.setTranslationY(INTRO_GREEN_MARKER_TRANS_Y * sizeRatio);

                ImageView redMark = (ImageView)root.findViewById(R.id.image_red_marker);
                ((RelativeLayout.LayoutParams)redMark.getLayoutParams()).height =
                        (int)(Constants.INTRO_MARKER_IMAGE_HEIGHT * sizeRatio);
                redMark.setTranslationX(INTRO_RED_MARKER_TRANS_X * sizeRatio);
                redMark.setTranslationY(INTRO_RED_MARKER_TRANS_Y * sizeRatio);

                ImageView blueMark = (ImageView)root.findViewById(R.id.image_blue_marker);
                ((RelativeLayout.LayoutParams)blueMark.getLayoutParams()).height =
                        (int)(Constants.INTRO_MARKER_IMAGE_HEIGHT * sizeRatio);
                blueMark.setTranslationX(INTRO_BLUE_MARKER_TRANS_X * sizeRatio);
                blueMark.setTranslationY(INTRO_BLUE_MARKER_TRANS_Y * sizeRatio);

                ImageView yellowMark = (ImageView)root.findViewById(R.id.image_yellow_marker);
                ((RelativeLayout.LayoutParams)yellowMark.getLayoutParams()).height =
                        (int)(Constants.INTRO_MARKER_IMAGE_HEIGHT * sizeRatio);
                yellowMark.setTranslationX(INTRO_YELLOW_MARKER_TRANS_X * sizeRatio);
                yellowMark.setTranslationY(INTRO_YELLOW_MARKER_TRANS_Y * sizeRatio);
                break;
            }
            case 4: { // Events

                Point screenSize = new Point();
                getActivity().getWindowManager().getDefaultDisplay().getSize(screenSize);
                int width = screenSize.x;
                if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    width = (int)((float)(screenSize.x << 1) / 3f);
                    // Representation takes 2/3 of the screen in landscape mode

                ImageView events = (ImageView)root.findViewById(R.id.image_events);
                ((RelativeLayout.LayoutParams) events.getLayoutParams()).height =
                        (int)(Constants.INTRO_EVENTS_IMAGE_HEIGHT * sizeRatio);
                ((RelativeLayout.LayoutParams) events.getLayoutParams()).leftMargin =
                        (int)(INTRO_EVENTS_TRANS_RATIO_X * width);
                ((RelativeLayout.LayoutParams) events.getLayoutParams()).topMargin =
                        (int)(INTRO_EVENTS_TRANS_Y * sizeRatio);
                events.setScaleX(IntroFragment.INTRO_EVENTS_SCALE);
                events.setScaleY(IntroFragment.INTRO_EVENTS_SCALE);

                ImageView calendar = (ImageView)root.findViewById(R.id.image_calendar);
                ((RelativeLayout.LayoutParams) calendar.getLayoutParams()).height =
                        (int)(Constants.INTRO_CALENDAR_IMAGE_HEIGHT * sizeRatio);
                ((RelativeLayout.LayoutParams) calendar.getLayoutParams()).leftMargin =
                        (int)(INTRO_CALENDAR_TRANS_RATIO_X * width);
                ((RelativeLayout.LayoutParams) calendar.getLayoutParams()).topMargin =
                        (int)(INTRO_CALENDAR_TRANS_Y * sizeRatio);

                ImageView flyer = (ImageView)root.findViewById(R.id.image_flyer);
                ((RelativeLayout.LayoutParams)flyer.getLayoutParams()).height =
                        (int)(Constants.INTRO_FLYER_IMAGE_HEIGHT * sizeRatio);
                ((RelativeLayout.LayoutParams) flyer.getLayoutParams()).leftMargin =
                        (int)(INTRO_FLYER_TRANS_RATIO_X * width);
                ((RelativeLayout.LayoutParams) flyer.getLayoutParams()).topMargin =
                        (int)(INTRO_FLYER_TRANS_Y * sizeRatio);
                flyer.setScaleX(IntroFragment.INTRO_FLYER_SCALE);
                flyer.setScaleY(IntroFragment.INTRO_FLYER_SCALE);
                break;
            }
        }
    }

    //////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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
        else if (getArguments().getInt(DATA_KEY_POSITION) == (Constants.INTRO_PAGE_COUNT - 1))
            rootView.setTag(LimitlessViewPager.TAG_PAGE_RIGHT_BOTTOM);

        // Position representation images
        position(rootView);

        // Configure comments
        TextView title = (TextView)rootView.findViewById(R.id.title);
        TextView description = (TextView)rootView.findViewById(R.id.description);
        switch (getArguments().getInt(DATA_KEY_POSITION)) {

            case 0: { // Welcome

                title.setText(getResources().getString(R.string.intro_welcome));
                String appName = getResources().getString(R.string.app_name);
                description.setText(getResources().getString(R.string.intro_welcome_text, appName));

                // Add web site link to "LeClassico"
                Pattern linkMatcher = Pattern.compile(appName);
                String urlLeClassico = Constants.APP_WEBSITE + "index.php?lnk=";
                Linkify.addLinks(description, linkMatcher, urlLeClassico);
                break;
            }
            case 1: { // Publications

                title.setText(getResources().getString(R.string.intro_publications));
                description.setText(getResources().getString(R.string.intro_publications_text));
                break;
            }
            case 2: { // Album photos

                title.setText(getResources().getString(R.string.intro_albums));
                description.setText(getResources().getString(R.string.intro_albums_text));
                break;
            }
            case 3: { // Location

                title.setText(getResources().getString(R.string.intro_location));
                description.setText(getResources().getString(R.string.intro_location_text));
                break;
            }
            case 4: { // Events

                title.setText(getResources().getString(R.string.intro_events));
                description.setText(getResources().getString(R.string.intro_events_text));
                break;
            }
        }
        return rootView;
    }
}
