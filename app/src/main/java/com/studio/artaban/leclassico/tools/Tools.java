package com.studio.artaban.leclassico.tools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.TypedValue;
import android.widget.ImageView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.helpers.Glider;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.Storage;

import java.io.File;

/**
 * Created by pascal on 10/09/16.
 * Tools class containing tool methods
 */
public final class Tools {

    public static void setProfile(final Activity activity, ImageView view, boolean female, String profile) {
    // Display profile image using 'Glider' class (expected user profile & gender)

        Logs.add(Logs.Type.V, "activity: " + activity + ";view: " + view + ";female: " + female +
                ";profile: " + profile);
        if (profile != null)
            Glider.with(activity)
                    .load(Storage.FOLDER_PROFILES +
                                    File.separator + profile,
                            Constants.APP_URL_PROFILES + "/" + profile)
                    .placeholder((female) ? R.drawable.woman : R.drawable.man)
                    .into(view, new Glider.OnLoadListener() {

                                @Override
                                public boolean setResource(Bitmap resource, ImageView imageView) {
                                    //Logs.add(Logs.Type.V, "resource: " + resource +
                                    //        ";imageView: " + imageView);

                                    RoundedBitmapDrawable radiusBmp =
                                            RoundedBitmapDrawableFactory.create(activity.getResources(),
                                                    resource);
                                    TypedValue radius = new TypedValue();
                                    activity.getResources().getValue(R.dimen.profile_radius, radius, true);
                                    radiusBmp.setCornerRadius(radius.getFloat());
                                    imageView.setImageDrawable(radiusBmp);
                                    return true;
                                }
                            });
        else
            view.setImageDrawable(activity.getDrawable((female) ? R.drawable.woman : R.drawable.man));
    }
}
