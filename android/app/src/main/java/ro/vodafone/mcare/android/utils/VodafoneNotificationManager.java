package ro.vodafone.mcare.android.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;

import me.leolin.shortcutbadger.ShortcutBadger;
import ro.vodafone.mcare.android.BuildConfig;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.activities.DashboardActivity;

import static android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

/**
 * Created by Bivol Pavel on 27.04.2017.
 */

public class VodafoneNotificationManager {

    private final static int DOWNLOAD_NOTIFICATION_ID = 1;
    private final static String CHAT_CHANNEL = "vdf_chat_channel";

    private static String getAppName(Context context) {
        String appName = null;
        try {
            String packageName = context.getPackageName();
            PackageManager packageManager = context.getApplicationContext().getPackageManager();
            appName = String.valueOf(packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)));
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("VodafoneNotificationM", "getAppName Error", e);
        }
        return appName;
    }

    public static void displayDownloadBillNotification(Context context, File file) {

        try {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context, NotificationChannels.CHANNEL_ID_DOWNLOAD_BILL);
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
                mBuilder.setSmallIcon(R.drawable.ic_not_white_bg);
                mBuilder.setColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setColor(NotificationCompat.COLOR_DEFAULT);
            }
            mBuilder.setContentTitle(getAppName(context))
                    .setContentText("Factura a fost descărcată.");

            MimeTypeMap map = MimeTypeMap.getSingleton();
            String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
            String type = map.getMimeTypeFromExtension(ext);

            if (type == null)
                type = "*/*";

            Intent intent = new Intent(Intent.ACTION_VIEW);

            Uri data;
            if (Build.VERSION.SDK_INT >= 24) {
                data = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
            } else {
                data = Uri.fromFile(file);
            }
            intent.setDataAndType(data, type);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(intent);

            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            android.app.NotificationManager mNotificationManager =
                    (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (mNotificationManager != null)
                mNotificationManager.notify(DOWNLOAD_NOTIFICATION_ID, mBuilder.build());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendNotification(Context context, String message, int messageCount) {

        applyCount(context, messageCount);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, NotificationChannels.CHANNEL_ID_CHAT);
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            mBuilder.setSmallIcon(R.drawable.ic_not_white_bg);
            mBuilder.setColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setColor(NotificationCompat.COLOR_DEFAULT);
        }
        mBuilder.setContentTitle(getAppName(context))
                        .setContentText(message)
                        .setNumber(messageCount)
                        .setAutoCancel(true);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, DashboardActivity.class);
        resultIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        resultIntent.setFlags(FLAG_ACTIVITY_MULTIPLE_TASK);
        resultIntent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
        resultIntent.putExtra("openChat", true);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(DashboardActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //int UNIQUE_INTEGER_NUMBER = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        // Use uniqu integer number to show all notifications in status bar.
        if (mNotificationManager != null)
            mNotificationManager.notify(1, mBuilder.build());
    }

    public static void cancelNotification(int id, Context context) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null)
            mNotificationManager.cancel(id);
    }


    public static void applyCount(Context context, int badgeCount) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            ShortcutBadger.applyCount(context, badgeCount);
        }
    }

    public static void clearBadgeCount(Context context) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            ShortcutBadger.removeCount(context);
        }
        //Todo Uncomment after set target api version 26
        /*else {
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // The user visible name of the channel.
            CharSequence name = "Chat conversation";
            // The user visible description of the channel.
            String description = "Open the app to see response from agent";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(CHAT_CHANNEL, name, importance);

            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.setShowBadge(false);
            mNotificationManager.createNotificationChannel(mChannel);
        }*/
    }
}
