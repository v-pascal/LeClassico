package com.studio.artaban.leclassico.connection;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.studio.artaban.leclassico.R;
import com.studio.artaban.leclassico.activities.introduction.IntroActivity;
import com.studio.artaban.leclassico.helpers.Logs;
import com.studio.artaban.leclassico.helpers.Notify;

/**
 * Created by pascal on 28/10/16.
 * Service notification helpers
 */
public final class ServiceNotify {

    private static final int SERVICE_NOTIFICATION_REF = 303; // Notification ID

    //
    private static ServiceNotify ourInstance = new ServiceNotify();
    private ServiceNotify() { }

    private static Notification mNotify; // Notification

    private static void addDateTime(RemoteViews views) {
        Logs.add(Logs.Type.V, "views: " + views);








        views.setTextViewText(R.id.notify_time, notifyData.getString(Notify.DATA_KEY_TEXT));



        views.setTextViewText(R.id.notify_date, notifyData.getString(Notify.DATA_KEY_TEXT));
    }

    //////
    public static void create(Service service, String pseudo) {
        // Create foreground service notification (at connection)

        Logs.add(Logs.Type.V, "service: " + service + ";pseudo: " + pseudo);
        String text = service.getString(R.string.pseudo_connected, pseudo);

        RemoteViews notifyViews = new RemoteViews(service.getPackageName(), R.layout.notification_service);
        addDateTime(notifyViews);









        notifyViews.setTextViewText(R.id.notify_text, notifyData.getString(Notify.DATA_KEY_TEXT));

        Intent displayActivity = new Intent(service, IntroActivity.class);
        displayActivity.setAction(Intent.ACTION_MAIN);
        displayActivity.addCategory(Intent.CATEGORY_LAUNCHER);
        displayActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent displayIntent = PendingIntent.getActivity(service, 0, displayActivity, 0);
        PendingIntent logoutIntent = PendingIntent.getBroadcast(service, 0,
                new Intent(DataService.REQUEST_LOGOUT), 0);

        notifyViews.setOnClickPendingIntent(R.id.image_logout, logoutIntent);
        notifyViews.setOnClickPendingIntent(R.id.text_logout, logoutIntent);
        notifyViews.setOnClickPendingIntent(R.id.image_display, displayIntent);
        notifyViews.setOnClickPendingIntent(R.id.text_display, displayIntent);

        //
        mNotify = new Notification.Builder(service)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle(service.getString(R.string.app_name))
                        .setContentText(text)
                        .build();

        mNotify.bigContentView = notifyViews;
        service.startForeground(SERVICE_NOTIFICATION_REF, mNotify);
    }

    public static void update(Context context, int newNotify) {
        // Update new notification info (count)

        Logs.add(Logs.Type.V, "newNotify: " + newNotify);
        if (mNotify == null)
            throw new IllegalStateException("Unexpected update call (not created)");

        addDateTime(mNotify.bigContentView);









        mNotify.bigContentView.setTextViewText(R.id.notify_title, notifyData.getString(Notify.DATA_KEY_TEXT));

        //
        ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(SERVICE_NOTIFICATION_REF, mNotify);
    }
}
