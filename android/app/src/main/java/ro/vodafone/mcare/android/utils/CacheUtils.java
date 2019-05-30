package ro.vodafone.mcare.android.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class CacheUtils {
    public static String TAG = "CacheUtils";

    private Context context;

    public CacheUtils(Context context) {
        this.context = context;
    }

    private static final String APPLICATION_PREFS_NAME = "ApplicationPreferenceName";
    private static final String PREF_VERSION_CODE_KEY = "version_code";
    private static final int DOESNT_EXIST = -1;
    private static final String ACCEPTED_TERMS_AND_CONDITIONS = "acceptedTermsAndCinditions";

    public boolean checkFirstRunOrUpdate() {
        boolean isFirstRunOrUpdate = false;

        // Get current version code
        int currentVersionCode = 0;
        try {
            currentVersionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            // handle exception
            e.printStackTrace();
        }

        // Get saved version code
        SharedPreferences prefs = context.getSharedPreferences(APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            // This is just a normal run
            isFirstRunOrUpdate = false;
        } else if (savedVersionCode == DOESNT_EXIST) {
            // This is a new install (or the user cleared the shared preferences)
            isFirstRunOrUpdate = true;
        } else if (currentVersionCode > savedVersionCode) {
            // This is an upgrade
            isFirstRunOrUpdate = true;
        }
        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();

        return isFirstRunOrUpdate;
    }


    public boolean isAcceptedTermsAndConditions(){
        SharedPreferences prefs = context.getSharedPreferences(APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(ACCEPTED_TERMS_AND_CONDITIONS, false);
    }


    public void setTermsAndConditionsAccepted(){
        SharedPreferences prefs = context.getSharedPreferences(APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(ACCEPTED_TERMS_AND_CONDITIONS, true).apply();
    }


}
