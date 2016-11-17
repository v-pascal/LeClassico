package com.studio.artaban.leclassico.tools;

import android.app.Activity;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.studio.artaban.leclassico.helpers.Logs;

/**
 * Created by pascal on 02/08/16.
 * Tools to size UI components
 */
public final class SizeUtils {

    public static void screenRatio(Activity activity, View toSize, boolean horizontal, float ratio) {
    // Set width/height view size according a screen ratio

        Logs.add(Logs.Type.V, "activity: " + activity + ";toSize: " + toSize + ";horizontal: " + horizontal +
                ";ratio: " + ratio);
        Point screenSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(screenSize);

        if (horizontal)
            toSize.getLayoutParams().width = (int)((float)screenSize.x * ratio);
        else
            toSize.getLayoutParams().height = (int)((float)screenSize.y * ratio);
    }
}
