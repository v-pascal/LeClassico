package com.studio.artaban.leclassico.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.data.Constants;

/**
 * Created by pascal on 22/08/16.
 * Notification helper class
 */
public class Notify {

    // Data keys
    public static final String DATA_KEY_ICON = "icon";
    public static final String DATA_KEY_TICKER = "ticker";
    public static final String DATA_KEY_TITLE = "title";
    public static final String DATA_KEY_WHEN = "when";
    public static final String DATA_KEY_DEFAULTS = "defaults";
    public static final String DATA_KEY_SOUND = "sound";
    public static final String DATA_KEY_VIBRATE = "vibrate";
    public static final String DATA_KEY_ALERT_ONCE = "alertOnce";
    public static final String DATA_KEY_AUTO_CANCEL = "autoCancel";

    public static final String DATA_KEY_TEXT = "text"; // Subtitle
    public static final String DATA_KEY_INFO = "info";
    public static final String DATA_KEY_NUMBER = "number";

    public static final String DATA_KEY_PROGRESS_MAX = "maxProgress";
    public static final String DATA_KEY_PROGRESS = "progress";

    // Type
    public enum Type { // Display notification with...
        EVENT, // Small icon + title + text
        NUMBER, // Small icon + ticker + title + text + number
        PROGRESS // Small icon + title + progress bar

        // TODO: Implement additional notification type
    }

    //////
    private static Notify ourInstance = new Notify();
    public static void update(Context context, Type type, PendingIntent intent, Bundle data) {
        ourInstance.show(context, type, intent, data);
    }
    public static void cancel(Context context) {
        ourInstance.hide(context);
    }

    private static final int NOTIFICATION_REF = 1; // Notification reference
    private static final String NOTIFICATION_TICKER = "Notification"; // Default notification ticker
    private Notify() { }

    //
    private void show(Context context, Type type, PendingIntent intent, Bundle data) {
    // Add or update an existing notification (according its key)

        Logs.add(Logs.Type.V, "context: " + context + ";type: " + type + ";intent: " + intent +
                ";data: " + data);
        int icon = R.mipmap.ic_launcher;
        String ticker = NOTIFICATION_TICKER;
        String title = Constants.APP_NAME;
        long when = System.currentTimeMillis();
        int defaults = Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE;
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] vibrate = new long[]{1000, 1000, 1000};
        boolean alertOnce = false;
        boolean autoCancel = false;

        String text = null;
        String info = null;
        int number = 0;

        int progressMax = 100;
        int progress = 0;

        if (data != null) { // Assign data (if any)

            icon = data.getInt(DATA_KEY_ICON, icon);
            ticker = data.getString(DATA_KEY_TICKER, ticker);
            title = data.getString(DATA_KEY_TITLE, title);
            when = data.getLong(DATA_KEY_WHEN, when);
            defaults = data.getInt(DATA_KEY_DEFAULTS, defaults);
            if (data.containsKey(DATA_KEY_SOUND))
                sound = Uri.parse(data.getString(DATA_KEY_SOUND));
            if (data.containsKey(DATA_KEY_VIBRATE))
                vibrate = data.getLongArray(DATA_KEY_VIBRATE);
            alertOnce = data.getBoolean(DATA_KEY_ALERT_ONCE, false);
            autoCancel = data.getBoolean(DATA_KEY_AUTO_CANCEL, false);

            text = data.getString(DATA_KEY_TEXT, null);
            info = data.getString(DATA_KEY_INFO, null);
            number = data.getInt(DATA_KEY_NUMBER, number);

            progressMax = data.getInt(DATA_KEY_PROGRESS_MAX, progressMax);
            progress = data.getInt(DATA_KEY_PROGRESS, progress);
        }
        Notification notify;
        switch (type) {

            case EVENT: { // Deprecated 'setLatestEventInfo' method
                notify = new Notification.Builder(context)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setSmallIcon(icon)
                        .setContentTitle(title)
                        .setContentText(text)
                        .build();
                break;
            }
            case NUMBER: {
                notify = new Notification.Builder(context)
                        .setSmallIcon(icon)
                        .setTicker(ticker)
                        .setWhen(when)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setNumber(number)
                        .build();
                break;
            }
            //case PROGRESS:
            default: {
                throw new IllegalArgumentException("Not implemented yet");
            }
        }
        if (intent != null) // Add pending intent (if any)
            notify.contentIntent = intent;

        //
        ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(NOTIFICATION_REF, notify);
    }
    private void hide(Context context) { // Cancel notification
        Logs.add(Logs.Type.V, "context: " + context);
        ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIFICATION_REF);
    }
}
