package com.studio.artaban.leclassico.connection;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.RemoteViews;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.introduction.IntroActivity;
import com.studio.artaban.leclassico.data.DataTable;
import com.studio.artaban.leclassico.data.codes.Preferences;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.services.DataService;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by pascal on 28/10/16.
 * Service notification helpers (with logout & display management)
 */
public final class ServiceNotify {

    private static final int SERVICE_NOTIFICATION_REF = 303; // Notification ID
    private static Notification Notify; // Notification

    private static void setDateTime(Context context, RemoteViews views) { // Set current date & time
        Logs.add(Logs.Type.V, "context: " + context + ";views: " + views);

        Date now = new Date();
        DateFormat notifyDate = android.text.format.DateFormat.getMediumDateFormat(context);
        android.text.format.DateFormat notifyTime = new android.text.format.DateFormat();

        views.setTextViewText(R.id.notify_time, notifyTime.format("h:mm a", now));
        views.setTextViewText(R.id.notify_date, notifyDate.format(now));
    }
    private static void setTextInfo(Context context, int newNotify) { // Set text info (new notification count)
        Logs.add(Logs.Type.V, "context: " + context + ";newNotify: " + newNotify);

        SpannableStringBuilder textBuilder =
                new SpannableStringBuilder(context.getString(R.string.service_new_notify, newNotify));
        int newNotifyLen = String.valueOf(newNotify).length();
        textBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0, newNotifyLen, 0);
        textBuilder.setSpan(new ForegroundColorSpan(Color.BLACK), newNotifyLen, textBuilder.length(), 0);

        int defaults = 0;
        if (Preferences.getBoolean(Preferences.SETTINGS_NOTIFY_SOUND))
            defaults |= Notification.DEFAULT_SOUND;
        if (Preferences.getBoolean(Preferences.SETTINGS_NOTIFY_VIBRATE))
            defaults |= Notification.DEFAULT_VIBRATE;
        if (Preferences.getBoolean(Preferences.SETTINGS_NOTIFY_LIGHT))
            defaults |= Notification.DEFAULT_LIGHTS;

        Notify.bigContentView.setTextViewText(R.id.notify_text, textBuilder.subSequence(0, textBuilder.length()));
        Notify.defaults = (newNotify > 0)? defaults:0;
    }

    //////
    public static void create(Service service, String pseudo) {
        // Create foreground service notification (at connection)

        Logs.add(Logs.Type.V, "service: " + service + ";pseudo: " + pseudo);
        String title = service.getString(R.string.pseudo_connected, pseudo);

        RemoteViews notifyViews = new RemoteViews(service.getPackageName(), R.layout.notification_service);
        setDateTime(service, notifyViews); // Set current date & time

        // Set title info (connected user)
        SpannableStringBuilder textBuilder = new SpannableStringBuilder(title);
        textBuilder.setSpan(new ForegroundColorSpan(Color.BLACK), pseudo.length(), textBuilder.length(), 0);
        textBuilder.setSpan(new ForegroundColorSpan(service.getResources().getColor(R.color.colorAccentLocation)),
                0, pseudo.length(), 0);
        notifyViews.setTextViewText(R.id.notify_title, textBuilder.subSequence(0, textBuilder.length()));

        // Add action event (via pending intents)
        Intent displayActivity = new Intent(service, IntroActivity.class);
        displayActivity.setAction(Intent.ACTION_MAIN);
        displayActivity.addCategory(Intent.CATEGORY_LAUNCHER);
        displayActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent displayIntent = PendingIntent.getActivity(service, 0, displayActivity, 0);
        PendingIntent logoutIntent = PendingIntent.getBroadcast(service, 0,
                new Intent(DataService.REQUEST_LOGOUT), 0);

        notifyViews.setOnClickPendingIntent(R.id.image_logout, logoutIntent);
        notifyViews.setOnClickPendingIntent(R.id.text_logout, logoutIntent);

        //
        Notify = new Notification.Builder(service)
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle(service.getString(R.string.app_name))
                        .setContentText(title)
                        .setColor(service.getResources().getColor(R.color.colorPrimaryMain))
                        .setContentIntent(displayIntent)
                        .build();

        Notify.bigContentView = notifyViews;
        setTextInfo(service, DataTable.getNewNotification(service.getContentResolver(), pseudo));
        Notify.defaults = Notification.DEFAULT_ALL; // Always at connection
        service.startForeground(SERVICE_NOTIFICATION_REF, Notify);
    }

    public static void update(Context context, int newNotify) {
        // Update new notification info (count)

        Logs.add(Logs.Type.V, "newNotify: " + newNotify);
        if (Notify == null)
            throw new IllegalStateException("Unexpected update call (not created)");

        setDateTime(context, Notify.bigContentView); // Update current date & time
        setTextInfo(context, newNotify); // Update text info (new notification count)

        ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(SERVICE_NOTIFICATION_REF, Notify);
    }

    public static void remove(Service service) {
        Logs.add(Logs.Type.V, "service: " + service);

        service.stopForeground(true);
        Notify = null;
    }
}
