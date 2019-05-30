package ro.vodafone.mcare.android.utils.navigation.notification;

import android.app.Activity;
import android.content.Intent;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.UserDataController;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.ui.activities.SplashScreenActivity;
import ro.vodafone.mcare.android.ui.activities.base.BaseMenuActivity;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

/**
 * Created by Victor Radulescu on 6/22/2017.
 */

public class NotificationNavigationAction {


    private  static long lastTimeWhenAutoLoginStarted = 0;

    private static final long timeToPassBeforeNewNavigationActionStarted = 3000;

    public static void startAction(String smsInfo){
        if(!hasTimePassedFromLastValidNotificationNavigationAction()){
            return;
        }

        NotificationAction notificationAction;

        notificationAction = NotificationParserToAction.parse(smsInfo);
        startNavigationAction(notificationAction);

    }
    public static void startAction(String smsInfo,String offerId){
        if(!hasTimePassedFromLastValidNotificationNavigationAction()){
            return;
        }
        NotificationAction notificationAction = NotificationParserToAction.parseWithOfferId(smsInfo,offerId);
        startNavigationAction(notificationAction);

    }

    public static void startAction(String smsInfo,boolean isSms,String url){
        if(!hasTimePassedFromLastValidNotificationNavigationAction()){
            return;
        }
        NotificationAction notificationAction = NotificationParserToAction.parse(smsInfo,url);

        startNavigationAction(notificationAction);
    }
    public static void  startNavigationAction(NotificationAction notificationAction ){
        if(notificationAction==null){
            return;
        }

        setLastTimeWhenValidNotificationNavigationActionStarted();
        User user = VodafoneController.getInstance().getUser();
        Activity currentActivity= VodafoneController.currentActivity();

        //no user created ( no login)
        if(user==null && currentActivity!=null){
            UserDataController.getInstance().setCurrentNotificationDashboardAction(notificationAction.getIntentActionName());
        }else{
            if(currentActivity==null){
                UserDataController.getInstance().setCurrentNotificationDashboardAction(notificationAction.getIntentActionName());
                Intent intent = new Intent(VodafoneController.getInstance(), SplashScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                VodafoneController.getInstance().startActivity(intent);
                return;
            }

            UserDataController.getInstance().setCurrentNotificationDashboardAction(notificationAction.getIntentActionName());
        }
    }

    public static NotificationAction getNotificationAction(String smsInfo, String url){
        return NotificationParserToAction.parse(smsInfo,url);
    }

    public static NotificationAction getNotificationActionWithOfferId(String smsInfo, String url){
        return NotificationParserToAction.parseWithOfferId(smsInfo,url);
    }

    public static synchronized  void setLastTimeWhenValidNotificationNavigationActionStarted() {
        lastTimeWhenAutoLoginStarted = System.currentTimeMillis();
    }
    private static synchronized  boolean hasTimePassedFromLastValidNotificationNavigationAction(){
        return System.currentTimeMillis()-lastTimeWhenAutoLoginStarted > timeToPassBeforeNewNavigationActionStarted;
    }
}
