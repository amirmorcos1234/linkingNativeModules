package ro.vodafone.mcare.android.application.controllers;

/**
 * Created by Victor Radulescu on 7/21/2017.
 */

public class DashboardController {

    public static int API19_ERROR_FLAG = 1;

    public static int API19_TIMEOUT_FLAG = 2;

    private static DashboardController dashboardController;

    private boolean reloadDashboardOnResume = false;

    private boolean recreateMenuOnReload = false;

    private DashboardController(){
    }

    public static DashboardController getInstance() {
        if(dashboardController==null){
            dashboardController = new DashboardController();
        }
        return dashboardController;
    }

    public static boolean shouldReloadDashboardOnResume(){
        return getInstance().reloadDashboardOnResume;
    }
    public static void reloadDashboardOnResume(){
        getInstance().reloadDashboardOnResume = true;
    }

    /**
     * Register at the end of DashboardActivity resume method
     */
    public static void registerAtTheEndOfResumeAction(){
        getInstance().reloadDashboardOnResume = false;
        getInstance().recreateMenuOnReload = false;
    }

    public void setReloadDashboardOnResume(boolean reloadDashboardOnResume) {
        this.reloadDashboardOnResume = reloadDashboardOnResume;
    }

    public  static boolean shouldRecreateMenuOnReload() {
        return getInstance().recreateMenuOnReload;
    }

    public void recreateMenuOnReload() {
        getInstance().recreateMenuOnReload = true;
    }
}
