package ro.vodafone.mcare.android.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import ro.vodafone.mcare.android.client.model.realm.system.NotificationChannelLabels;

public class NotificationChannels {

	public static final String CHANNEL_ID_DOWNLOAD_BILL = "ro.vodafone.mcare.android.notification.channel.id.download.bill";
	public static void createDownloadBillNotificationChannel(Context context) {
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = NotificationChannelLabels.getDownloadBillChannelName();
			String description = NotificationChannelLabels.getDownloadBillChannelDescription();
			int importance = NotificationManager.IMPORTANCE_LOW;
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID_DOWNLOAD_BILL, name, importance);
			channel.setDescription(description);

			channel.enableLights(true);
			channel.setLightColor(0xff2121);
			channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
			channel.enableVibration(false);


			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}
	}

	public static final String CHANNEL_ID_CHAT = "ro.vodafone.mcare.android.notification.channel.id.chat";
	public static void createChatNotificationChannel(Context context) {
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = NotificationChannelLabels.getChatChannelName();
			String description = NotificationChannelLabels.getChatChannelDescription();
			int importance = NotificationManager.IMPORTANCE_LOW;
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID_CHAT, name, importance);
			channel.setDescription(description);

			channel.enableLights(true);
			channel.setLightColor(0xff2121);
			channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
			channel.enableVibration(false);


			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}
	}

}
