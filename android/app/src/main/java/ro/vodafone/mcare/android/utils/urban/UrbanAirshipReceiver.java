package ro.vodafone.mcare.android.utils.urban;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.urbanairship.AirshipReceiver;
import com.urbanairship.push.PushMessage;

import ro.vodafone.mcare.android.utils.navigation.notification.NotificationNavigationAction;

/**
 * Created by user on 20.06.2017.
 */

public class UrbanAirshipReceiver extends AirshipReceiver {

    private static final String TAG = "UrbanAirshipReceiver";

    @Override
    protected void onChannelCreated(@NonNull Context context, @NonNull String channelId) {
        Log.i(TAG, "Channel created. Channel Id:" + channelId + ".");
    }

    @Override
    protected void onChannelUpdated(@NonNull Context context, @NonNull String channelId) {
        Log.i(TAG, "Channel updated. Channel Id:" + channelId + ".");
    }

    @Override
    protected void onChannelRegistrationFailed(Context context) {
        Log.i(TAG, "Channel registration failed.");
    }

    @Override
    protected void onPushReceived(@NonNull Context context, @NonNull PushMessage message, boolean notificationPosted) {
        Log.i(TAG, "Received push message. Alert: " + message.getAlert() + ". posted notification: " + notificationPosted);

        // Return false here to allow Urban Airship to auto launch the launcher activity
        Bundle pushBundle = message.getPushBundle();
        Log.i(TAG, "Received push message. pushBundle: " + pushBundle);
        Log.i(TAG, "Received push message. pushBundle custom: " + pushBundle.getString("custom"));
        Log.i(TAG, "Received push message. pushBundle test: " + pushBundle.getString("test"));

      //  NotificationNavigationAction.startAction(pushBundle.getString("page_id"),false,pushBundle.getString("url"));

    }

    @Override
    protected void onNotificationPosted(@NonNull Context context, @NonNull NotificationInfo notificationInfo) {
        Log.i(TAG, "Notification posted. Alert: " + notificationInfo.getMessage().getAlert() + ". NotificationId: " + notificationInfo.getNotificationId());
    }

    @Override
    protected boolean onNotificationOpened(@NonNull Context context, @NonNull NotificationInfo notificationInfo) {
        Log.i(TAG, "Notification opened. Alert: " + notificationInfo.getMessage().getAlert() + ". NotificationId: " + notificationInfo.getNotificationId());

        // Return false here to allow Urban Airship to auto launch the launcher activity
        Bundle pushBundle = notificationInfo.getMessage().getPushBundle();
        Log.i(TAG, "Notification opened. pushBundle: " + pushBundle);
        Log.i(TAG, "Notification opened. pushBundle custom: " + pushBundle.getString("custom"));
        Log.i(TAG, "Notification opened. pushBundle test: " + pushBundle.getString("test"));
        Log.i(TAG, "Notification opened. pushBundle test: " + pushBundle.getString("page_id"));

        String offerId = pushBundle.getString("offer_id");
        String pageId = pushBundle.getString("page_id");

        if(offerId==null){
            NotificationNavigationAction.startAction(pageId,false, pushBundle.getString("url"));
        }else{
            NotificationNavigationAction.startAction(pageId, offerId);
        }

        return true;
    }

    @Override
    protected boolean onNotificationOpened(@NonNull Context context, @NonNull NotificationInfo notificationInfo, @NonNull ActionButtonInfo actionButtonInfo) {
        Log.i(TAG, "Notification action button opened. Button ID: " + actionButtonInfo.getButtonId() + ". NotificationId: " + notificationInfo.getNotificationId());


        // Return false here to allow Urban Airship to auto launch the launcher
        // activity for foreground notification action buttons
        return false;
    }

    @Override
    protected void onNotificationDismissed(@NonNull Context context, @NonNull NotificationInfo notificationInfo) {
        Log.i(TAG, "Notification dismissed. Alert: " + notificationInfo.getMessage().getAlert() + ". Notification ID: " + notificationInfo.getNotificationId());
    }
}
