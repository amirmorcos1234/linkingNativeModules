package ro.vodafone.mcare.android.application;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import ro.vodafone.mcare.android.application.controllers.DashboardController;
import ro.vodafone.mcare.android.application.controllers.UserDataController;
import ro.vodafone.mcare.android.ui.activities.BaseActivity;
import ro.vodafone.mcare.android.ui.activities.MenuActivity;
import ro.vodafone.mcare.android.utils.VodafoneNotificationManager;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;

public class ActivityTracker implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d(ActivityTracker.class.getSimpleName(), "Created: " + activity.getClass().getSimpleName());
        VodafoneController.runningActivities.addFirst(activity);
        if(!VodafoneController.getInstance().isLibrariesInitialized()){
            VodafoneController.getInstance().initializeLibraries(activity);
            DashboardController.reloadDashboardOnResume();
        }
        if(!VodafoneController.getInstance().isUserInitialized()){
            VodafoneController.getInstance().initializeUserIfDoesntExists(activity);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d(ActivityTracker.class.getSimpleName(), "Started: " + activity.getClass().getSimpleName());
        VodafoneController.setCurrentActivity(activity);

        if(!VodafoneController.getInstance().isUserInitialized()){
            VodafoneController.getInstance().initializeUserIfDoesntExists(activity);
        }

    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(ActivityTracker.class.getSimpleName(), "Resumed: " + activity.getClass().getSimpleName());
        //we set the current activity here as well, in case of opening a transparent activity on top of the current one

        if(!VodafoneController.getInstance().isUserInitialized()){
            VodafoneController.getInstance().initializeUserIfDoesntExists(activity);
        }

        //If currentActivity is null, the app comme from background
        if(VodafoneController.currentActivity() == null){
            VodafoneController.getInstance().setBadgeCount(0);
        }

        VodafoneController.setCurrentActivity(activity);
        if(activity instanceof BaseActivity){
            VodafoneController.activityVisible = true;
            if(VodafoneController.getInstance().getDoIntentActionIfDisableByAppGoingBackground()
                    !=IntentActionName.NONE &&
                    VodafoneController.currentActivity() instanceof MenuActivity &&
                    UserDataController.getInstance().getCurrentDashboardAction().equals(IntentActionName.NONE)){
             //   new NavigationAction(VodafoneController.currentActivity).startAction(VodafoneController.getInstance().getDoIntentActionIfDisableByAppGoingBackground());
             //   VodafoneController.getInstance().setDoIntentActionIfDisableByAppGoingBackground(IntentActionName.NONE);
            }
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d(ActivityTracker.class.getSimpleName(), "Paused: " + activity.getClass().getSimpleName());
        if(activity instanceof BaseActivity)
            VodafoneController.activityVisible = false;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(ActivityTracker.class.getSimpleName(), "Stopped: " + activity.getClass().getSimpleName());
        if(VodafoneController.currentActivity() == activity)
            VodafoneController.setCurrentActivity(null);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d(ActivityTracker.class.getSimpleName(), "Saved: " + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(ActivityTracker.class.getSimpleName(), "Destroyed: " + activity.getClass().getSimpleName());
        VodafoneController.runningActivities.remove(activity);
//        VodafoneController.supportWindows.remove(activity);
    }
}
