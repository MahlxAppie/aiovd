package com.infusiblecoder.allinonevideodownloader.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.infusiblecoder.allinonevideodownloader.R;

public class OreoNotification extends ContextWrapper {


    private NotificationManager notificationManager;
    private final Context innercontext;

    public OreoNotification(Context base) {
        super(base);
        innercontext = base;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(base);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel(Context base) {
        NotificationChannel channel = new NotificationChannel(base.getPackageName(),
                innercontext.getResources().getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(false);
        channel.enableVibration(true);
        channel.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + base.getPackageName() + "/raw/notification"), Notification.AUDIO_ATTRIBUTES_DEFAULT);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return notificationManager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getOreoNotification(String title, String body,
                                                    PendingIntent pendingIntent, Uri soundUri, int icon) {
        return new Notification.Builder(getApplicationContext(), innercontext.getPackageName())
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(icon)
                .setSound(soundUri)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
    }
}
