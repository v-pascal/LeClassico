package com.studio.artaban.leclassico.tools;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.components.RecyclerAdapter;
import com.studio.artaban.leclassico.data.Constants;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.tables.ActualitesTable;
import com.studio.artaban.leclassico.data.tables.NotificationsTable;
import com.studio.artaban.leclassico.helpers.Glider;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.Storage;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pascal on 10/09/16.
 * Tools class containing tool methods
 */
public final class Tools { /////////////////////////////////////////////////////////////////////////

    private static float PROFILE_SIZE_RADIUS_FACTOR = 80f;
    public static void setProfile(final Activity activity, ImageView view, final boolean female,
                                  String profile, final int size, final boolean clickable) {
    // Display profile image using 'Glider' class (expected user profile & gender)

        Logs.add(Logs.Type.V, "activity: " + activity + ";view: " + view + ";female: " + female +
                ";profile: " + profile + ";clickable: " + clickable);
        if (profile != null)
            Glider.with(activity)
                    .load(Storage.FOLDER_PROFILES +
                                    File.separator + profile,
                            Constants.APP_URL_PROFILES + '/' + profile)
                    .placeholder((female) ? R.drawable.woman : R.drawable.man)
                    .into(view, new Glider.OnLoadListener() {

                                @Override
                                public void onLoadFailed(ImageView imageView) {

                                    //Logs.add(Logs.Type.V, "imageView: " + imageView);
                                    if (clickable) {
                                        imageView.setImageDrawable(null); // Remove placeholder
                                        imageView.setBackground(
                                                new RippleDrawable(ColorStateList.valueOf(activity
                                                        .getResources().getColor(R.color.black_transparent)),
                                                activity.getDrawable((female) ? R.drawable.woman : R.drawable.man),
                                                activity.getDrawable(R.drawable.man)));
                                    }
                                }

                                @Override
                                public boolean onSetResource(Bitmap resource, ImageView imageView) {
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

                                    if (clickable) { // Check to display a ripple effect
                                        imageView.setImageDrawable(null); // Remove placeholder
                                        imageView.setBackground(
                                                new RippleDrawable(ColorStateList.valueOf(activity
                                                        .getResources().getColor(R.color.black_transparent)),
                                                radiusBmp, radiusBmp));
                                    } else
                                        imageView.setImageDrawable(radiusBmp);
                                    return true;
                                }
                            });
        else if (!clickable)
            view.setImageDrawable(activity.getDrawable((female) ? R.drawable.woman : R.drawable.man));
        else
            view.setBackground(new RippleDrawable(ColorStateList.valueOf(activity.getResources()
                    .getColor(R.color.black_transparent)),
                    activity.getDrawable((female) ? R.drawable.woman : R.drawable.man),
                    activity.getDrawable(R.drawable.man)));
    }

    //////
    public static void setDateTime(Context context, TextView date, TextView time, String dateTime) {
    // Fill date & time text views according a date & time parameter (in query date & time format)
    // i.e: --/-- for the date & --:-- for the time

        Logs.add(Logs.Type.V, "context: " + context + ";date: " + date + ";time: " + time);
        DateFormat paramFormat = new SimpleDateFormat(Constants.FORMAT_DATE_TIME);
        try {
            Date paramDate = paramFormat.parse(dateTime);
            DateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.format_date));
            DateFormat timeFormat = new SimpleDateFormat(context.getString(R.string.format_time));

            String dateText = dateFormat.format(paramDate);
            date.setText((dateText.compareTo(dateFormat.format(new Date())) != 0)?
                    dateText : context.getString(R.string.today));
            time.setText(timeFormat.format(paramDate));

        } catch (ParseException e) {
            Logs.add(Logs.Type.E, "Wrong query date & time format: " + dateTime);
        }
    }

    //////
    public static void criticalError(final Activity activity, @StringRes int error) {
    // Display a critical error message B4 application close

        Logs.add(Logs.Type.V, "activity: " + activity + ";error: " + error);
        new AlertDialog.Builder(activity)
                .setIcon(R.drawable.error_red)
                .setTitle(R.string.error)
                .setMessage(error)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        Logs.add(Logs.Type.V, "dialog: " + dialog);
                        activity.finishAffinity();
                    }
                })
                .create()
                .show();
    }

    //////
    public interface OnProcessListener {

        Bundle onBackgroundTask();
        void onMainNextTask(Bundle backResult);
    }
    public static void startProcess(final Activity activity, final OnProcessListener listener) {
    // Do a background process B4 executing a followed task on main thread (with background result)

        Logs.add(Logs.Type.V, "listener: " + listener);
        new Thread(new Runnable() {
            @Override
            public void run() {

                //Logs.add(Logs.Type.V, null);
                final Bundle result = listener.onBackgroundTask();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //Logs.add(Logs.Type.V, null);
                        listener.onMainNextTask(result);
                    }
                });
            }
        }).start();
    }

    //////
    public static RotateAnimation getProgressAnimation() {
    // Return rotate animation used to animate a progression status

        Logs.add(Logs.Type.V, null);
        RotateAnimation anim = new RotateAnimation(0f, 350f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);
        return anim;
    }

    ////////////////////////////////////////////////////////////////////////////////// Notifications

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

    /////////////////////////////////////////////////////////////////////////////////// Publications

    public static @DrawableRes int getPubIcon(short type) {
    // Return the drawable ID of the publication icon type passed as parameter

        Logs.add(Logs.Type.V, "type: " + type);
        switch (type) {
            case ActualitesTable.TYPE_TEXT: return R.drawable.ic_message_white_18dp;
            case ActualitesTable.TYPE_LINK: return R.drawable.ic_web_white_18dp;
            case ActualitesTable.TYPE_IMAGE: return R.drawable.ic_photo_white_18dp;

            default:
                throw new IllegalArgumentException("Unexpected publication type: " + type);
        }
    }

    //////
    public static short getPubType(RecyclerAdapter.DataView data, int rank, int linkIndex, int imageIndex) {
    // Return the wall type resource string ID according notification link & image fields

        Logs.add(Logs.Type.V, "data: " + data + ";rank: " + rank + ";linkIndex: " + linkIndex +
                ";imageIndex: " + imageIndex);
        if (!data.isNull(rank, linkIndex))
            return ActualitesTable.TYPE_LINK;

        if (!data.isNull(rank, imageIndex))
            return ActualitesTable.TYPE_IMAGE;

        return ActualitesTable.TYPE_TEXT;
    }

    //////////////////////////////////////////////////////////////////////////////// Synchronization

    public static void setSyncView(Context context, TextView text, ImageView icon, String date, byte status) {
    // Fill synchronization UI components according its status

        Logs.add(Logs.Type.V, "context: " + context + ";date: " + date + ";status: " + status +
                ";text: " + text + ";icon: " + icon);
        icon.setColorFilter(Color.BLACK);
        icon.clearAnimation();

        if ((status & DataTable.Synchronized.IN_PROGRESS.getValue()) == DataTable.Synchronized.IN_PROGRESS.getValue()) {
            // Synchronizing (in progress)

            text.setText(context.getString(R.string.synchronizing));
            icon.setColorFilter(Color.TRANSPARENT);
            icon.setImageDrawable(context.getDrawable(R.drawable.spinner_black_16));
            icon.startAnimation(getProgressAnimation());

        } else if (status == DataTable.Synchronized.DONE.getValue()) { // Synchronized

            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.FORMAT_DATE_TIME);
            try {
                Date syncDate = dateFormat.parse(date);
                DateFormat userDate = android.text.format.DateFormat.getMediumDateFormat(context);
                DateFormat userTime = android.text.format.DateFormat.getTimeFormat(context);

                text.setText(userDate.format(syncDate) + ' ' + userTime.format(syncDate));

            } catch (ParseException e) {
                Logs.add(Logs.Type.E, "Wrong status date format: " + date);
            }
            icon.setImageDrawable(context.getDrawable(R.drawable.ic_check_white_18dp));

        } else { // To synchronize

            text.setText(context.getString(R.string.to_synchronize));
            icon.setImageDrawable(context.getDrawable(R.drawable.ic_sync_white_18dp));
        }
    }
}
