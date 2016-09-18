package com.studio.artaban.leclassico.tools;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.TypedValue;
import android.widget.ImageView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.helpers.Glider;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.Storage;

import java.io.File;

/**
 * Created by pascal on 10/09/16.
 * Tools class containing tool methods
 */
public final class Tools {

    private static float PROFILE_SIZE_RADIUS_FACTOR = 80f;
    public static void setProfile(final Activity activity, ImageView view, boolean female,
                                  String profile, final int size) {
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
                                    float factor = activity.getResources().getDimension(size) /
                                            activity.getResources().getDisplayMetrics().density;
                                    radiusBmp.setCornerRadius(radius.getFloat() * (factor /
                                            PROFILE_SIZE_RADIUS_FACTOR));
                                    imageView.setImageDrawable(radiusBmp);
                                    return true;
                                }
                            });
        else
            view.setImageDrawable(activity.getDrawable((female) ? R.drawable.woman : R.drawable.man));
    }

    public static @DrawableRes int getNotifyIcon(char type) {
    // Return the drawable ID of the notification icon type passed as parameter

        Logs.add(Logs.Type.V, "type: " + type);
        switch (type) {
            case NotificationsTable.TYPE_SHARED: return R.drawable.ic_add_a_photo_white_18dp;
            case NotificationsTable.TYPE_WALL: return R.drawable.ic_publish_white_18dp;
            case NotificationsTable.TYPE_MAIL: return R.drawable.ic_mail_white_18dp;
            case NotificationsTable.TYPE_PUB_COMMENT:
            case NotificationsTable.TYPE_PIC_COMMENT:
                return R.drawable.ic_message_white_18dp;

            default:
                throw new IllegalArgumentException("Unexpected notification type: " + type);
        }
    }

    private static final short NOTIFY_WALL_TYPE_TEXT = 0;
    private static final short NOTIFY_WALL_TYPE_LINK = 1;
    private static final short NOTIFY_WALL_TYPE_IMAGE = 2;

    public static short getNotifyWallType(Cursor cursor, int linkIndex, int imageIndex) {
    // Return the wall type resource string ID according notification link & image fields

        Logs.add(Logs.Type.V, "cursor: " + cursor + ";linkIndex: " + linkIndex + ";imageIndex: " + imageIndex);
        if (!cursor.isNull(linkIndex))
            return NOTIFY_WALL_TYPE_LINK;

        if (!cursor.isNull(imageIndex))
            return NOTIFY_WALL_TYPE_IMAGE;

        return NOTIFY_WALL_TYPE_TEXT;
    }
}
