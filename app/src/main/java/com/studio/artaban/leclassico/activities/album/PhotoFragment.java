package com.studio.artaban.leclassico.activities.album;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.helpers.Logs;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by pascal on 19/11/16.
 * Fragment to display a photo with comments
 */
public class PhotoFragment extends Fragment {

    public static final String ARG_KEY_TYPE = "type";
    // Argument keys

    public enum Type {
        BEST((byte)1);

        //
        private final byte id;
        Type(byte id) { this.id = id; }
        public byte getValue() { return this.id; }
    }

    ////// Fragment ////////////////////////////////////////////////////////////////////////////////
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logs.add(Logs.Type.V, "inflater: " + inflater + ";container: " + container +
                ";savedInstanceState: " + savedInstanceState);

        // Set backgrounds
        View rootView = inflater.inflate(R.layout.fragment_photo, container, false);
        try {
            float radius = getResources().getDimensionPixelSize(R.dimen.photo_background_radius);
            GradientDrawable background = (GradientDrawable) Drawable.createFromXml(getResources(),
                    getResources().getXml(R.xml.photo_background));
            background.setCornerRadii(new float[]{
                    radius, radius, radius, radius, 0, 0, 0, 0
            });
            rootView.findViewById(R.id.layout_header).setBackground(background);
            background = (GradientDrawable) Drawable.createFromXml(getResources(),
                    getResources().getXml(R.xml.photo_background));
            background.setCornerRadii(new float[]{
                    0, 0, radius, radius, 0, 0, 0, 0
            });
            rootView.findViewById(R.id.view_middle).setBackground(background);
            background = (GradientDrawable) Drawable.createFromXml(getResources(),
                    getResources().getXml(R.xml.photo_background));
            background.setCornerRadii(new float[]{
                    0, 0, 0, 0, radius, radius, radius, radius
            });
            rootView.findViewById(R.id.view_bottom).setBackground(background);

        } catch (XmlPullParserException e) {
            Logs.add(Logs.Type.E, "Failed to add background (parser): " + e.getMessage());
        } catch (IOException e) {
            Logs.add(Logs.Type.E, "Failed to add background (IO): " + e.getMessage());
        }





        ((TextView)rootView.findViewById(R.id.text_title)).setText("LC0180.jpg");
        ((TextView)rootView.findViewById(R.id.text_info_album)).setText("Tillate.com");
        ((TextView)rootView.findViewById(R.id.text_info_provider)).setText("Pascal");
        ((TextView)rootView.findViewById(R.id.text_info_range)).setText("1/258");






        return rootView;
    }
}
