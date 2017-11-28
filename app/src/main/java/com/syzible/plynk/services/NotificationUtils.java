package com.syzible.plynk.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.syzible.plynk.R;
import com.syzible.plynk.activities.MainActivity;
import com.syzible.plynk.network.GetImage;
import com.syzible.plynk.network.NetworkCallback;
import com.syzible.plynk.network.interfaces.OnIntentCallback;
import com.syzible.plynk.objects.Conversation;
import com.syzible.plynk.objects.Message;
import com.syzible.plynk.objects.User;
import com.syzible.plynk.utils.BitmapUtils;
import com.syzible.plynk.utils.CachingUtils;
import com.syzible.plynk.utils.EncodingUtils;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ed on 22/05/2017.
 */

public class NotificationUtils {

    private static final int VIBRATION_INTENSITY = 150;

    private static void vibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator != null)
            vibrator.vibrate(VIBRATION_INTENSITY);
    }

    private static int generateUniqueId() {
        Date now = new Date();
        return Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.getDefault()).format(now));
    }

    public static void generatePushNotification(final Context context, String title, String content, final String url, final String notificationId) {
        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                        .setAutoCancel(true);

        final Intent[] resultingIntent = new Intent[1];
        final OnIntentCallback onIntentCallback = new OnIntentCallback() {
            @Override
            public void onCallback(Intent intent) {
                generatePushNotification(intent, context, notificationBuilder, notificationId);
            }
        };

        resultingIntent[0] = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            onIntentCallback.onCallback(resultingIntent[0]);
    }

    private static void generatePushNotification(Intent resultingIntent, Context context, NotificationCompat.Builder notificationBuilder, String notificationId) {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultingIntent);

        PendingIntent resultingPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultingPendingIntent);

        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager != null)
            manager.notify(generateUniqueId(), notificationBuilder.build());
    }

    private static String getPageName(String url) {
        return url.split("/")[3];
    }

    private static boolean isFacebookLink(String url) {
        return url.split("https://www.facebook.com/").length > 1;
    }

    public static void generateMessageNotification(final Context context, final User user,
                                                   final Message message) throws JSONException {
        if (CachingUtils.doesImageExist(context, user.getId())) {
            Bitmap avatar = CachingUtils.getCachedImage(user.getId());
            notifyUser(context, avatar, user, message);
        } else {
            new GetImage(new NetworkCallback<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    Bitmap scaledAvatar = BitmapUtils.generateMetUserAvatar(response);
                    CachingUtils.cacheImage(user.getId(), scaledAvatar);
                    notifyUser(context, scaledAvatar, user, message);
                }

                @Override
                public void onFailure() {

                }
            }, user.getAvatar()).execute();
        }
    }

    private static void notifyUser(Context context, Bitmap avatar, User user, Message message) {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setLargeIcon(avatar)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(user.getName())
                        .setContentText(EncodingUtils.decodeText(message.getText()));

        if (!MainActivity.isActivityVisible()) {
            notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
            vibrate(context);
        }

        Intent resultingIntent = new Intent(context, MainActivity.class);
        resultingIntent.putExtra("invoker", "notification");
        resultingIntent.putExtra("user", user.getId());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultingIntent);

        PendingIntent resultingPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultingPendingIntent);

        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager != null)
            manager.notify(getNotificationId(user), notificationBuilder.build());
    }

    public static void dismissNotifications(Context context, ArrayList<Conversation> conversations) {
        for (Conversation c : conversations) {
            dismissNotification(context, (User) c.getUsers().get(0));
        }
    }

    public static void dismissNotification(Context context, User user) {
        if (context != null) {
            NotificationManager manager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null)
                manager.cancel(getNotificationId(user));
        }
    }

    private static int getNotificationId(User user) {
        long originalId = Long.parseLong(user.getId());
        return (int) originalId;
    }
}
