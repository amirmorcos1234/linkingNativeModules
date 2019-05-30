package ro.vodafone.mcare.android.ui.utils;

import android.util.Log;

import java.util.Calendar;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.utils.RealmManager;

/**
 * Created by Andrei DOLTU on 1/27/2017.
 */

public class WallpaperConfig {

    public static final String TAG = WallpaperConfig.class.getSimpleName();

    /*
 Backgroud display will change on configurable timeframes .
 Ex:
- Morning wallpaper (6AM- 11:AM)
- Noon wallpaper (11AM - 6PM)
- Evening/Night wallpaper (6PM - 6AM)


Note: The background wallpaper list will be configurable by the Digital team, without the need for app release.


format in 24H
    * */

    public static int morningStart = 600;
    public static int morningEnd = 1100;
    public static int noonStart = 1101;
    public static int noonEnd = 1800;
    public static int eveningNightStart = 1801;
    public static int eveningNightEnd = 559;
    public static String[] bgId;

    public static String morning_bg_url;
    public static String noon_bg_url;
    public static String evening_bg_url;

    //retrieve the configs



    public static void getTimeFrames() {
        //time frames are retrieved from ("/api/system/configurations/")
        AppConfiguration configurations = (AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class);
        if (configurations==null){
            return; //break logic in Configurations is empty
        }
        try {
            //check morning
            if (!Integer.valueOf(AppConfiguration.getDashboardWallpaperMorningStart()).equals(null)) {

                morningStart = Integer.valueOf(AppConfiguration.getDashboardWallpaperMorningStart());
            }
            if (!Integer.valueOf(configurations.getDashboardWallpaperMorningEnd()).equals(null)) {

                morningEnd = Integer.valueOf(AppConfiguration.getDashboardWallpaperMorningEnd());
            }

            //check noon
            if (!Integer.valueOf(configurations.getDashboardWallpaperNoonStart()).equals(null)) {

                noonStart = Integer.valueOf(AppConfiguration.getDashboardWallpaperNoonStart());
            }
            if (!Integer.valueOf(configurations.getDashboardWallpaperNoonEnd()).equals(null)) {

                noonEnd = Integer.valueOf(AppConfiguration.getDashboardWallpaperNoonEnd());
            }

            //check evening
            if (!Integer.valueOf(configurations.getDashboardWallpaperEveningStart()).equals(null)) {

                eveningNightStart = Integer.valueOf(configurations.getDashboardWallpaperEveningStart());
            }
            if (!Integer.valueOf(configurations.getDashboardWallpaperEveningEnd()).equals(null)) {

                eveningNightEnd = Integer.valueOf(configurations.getDashboardWallpaperEveningEnd());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static int getCurrentTime() {

        //TimeZone timeZone = TimeZone.getTimeZone("Europe/Bucharest");
        //timeZone
        Calendar c = Calendar.getInstance();

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        String hourString = String.format("%02d", hour);
        String minuteString = String.format("%02d", minute);

        Log.d(TAG, "c.get(Calendar.HOUR_OF_DAY: " + hourString);
        Log.d(TAG, "c.get(Calendar.MINUTE: " + minuteString);

        String time = hourString + minuteString;
        Log.d(TAG, "Wallpaper - current time is: " + time);
        return Integer.parseInt(time);
    }

    public static void getWallpapers() {
        //wallpaper urls are retrieved from ("/api/system/configurations/")
        AppConfiguration configurations = (AppConfiguration) RealmManager.getRealmObject(AppConfiguration.class);
        if(configurations==null ){
            return;
        }
        try {
            // check morning url
            if (AppConfiguration.getDashboardWallpaperMorningImageUrl() != null) {

                morning_bg_url = configurations.getDashboardWallpaperMorningImageUrl();
                Log.d(TAG, "morning_bg_url is: " + morning_bg_url);
            }

            // check noon url
            if (AppConfiguration.getDashboardWallpaperNoonImageUrl() != null) {
                noon_bg_url = configurations.getDashboardWallpaperNoonImageUrl();
                Log.d(TAG, "noon_bg_url is: " + noon_bg_url);
            }

            // check evening url
            if (AppConfiguration.getDashboardWallpaperEveningImageUrl() != null) {
                evening_bg_url = configurations.getDashboardWallpaperEveningImageUrl();
                Log.d(TAG, "evening_bg_url is: " + evening_bg_url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String[] setWallpaper () {
        //wallpaper urls are retrieved from ("/api/system/configurations/")

        getTimeFrames();
        getWallpapers();

        bgId = new String[2];
        //avoid null strings
        bgId[0] = String.valueOf(R.drawable.bg_morning);
        bgId[1] = "";

        int timeNow = getCurrentTime();
        Log.d(TAG, "timeNow is: " + timeNow);

        //hardcoding them for now
        Log.d(TAG, "bgId initial is: " + bgId[0] + " and " + bgId[1]);

        //check morning
        try {
            if (timeNow >= morningStart && timeNow <= morningEnd) {
                if(morning_bg_url != null){
                    bgId[0] = String.valueOf(R.drawable.bg_morning);
                    bgId[1] = morning_bg_url;
                }else {
                    bgId[0] = String.valueOf(R.drawable.bg_morning);
                    bgId[1] = "";
                }
                Log.d(TAG, "bgId morning is: " + bgId[0] + " and " + bgId[1]);
            }
            else if (timeNow >= noonStart && timeNow <= noonEnd) {
                if (noon_bg_url != null) {
//                    bgId[0] = String.valueOf(R.drawable.bg_day);
                    bgId[0] = String.valueOf(R.drawable.bg_morning);
                    bgId[1] = noon_bg_url;
                }else {
//                    bgId[0] = String.valueOf(R.drawable.bg_day);
                    bgId[0] = String.valueOf(R.drawable.bg_morning);
                    bgId[1] = "";
                }
                Log.d(TAG, "bgId noon is: " + bgId[0] + " and " + bgId[1]);
            }
            else {
                if (evening_bg_url != null) {
                    bgId[0] = String.valueOf(R.drawable.bg_evening);
                    bgId[1] = evening_bg_url;
                }else {
                    bgId[0] = String.valueOf(R.drawable.bg_evening);
                    bgId[1] = "";
                }
                Log.d(TAG, "bgId evening is: " + bgId[0] + " and " + bgId[1]);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return bgId;
    }

    public static TimePeriod getTimePeriod(){
        int timeNow = getCurrentTime();
        if (timeNow >= morningStart && timeNow <= morningEnd) {
          return TimePeriod.Morning;
        }
        else if (timeNow >= noonStart && timeNow <= noonEnd) {
            return TimePeriod.Noon;
        } else {
            return TimePeriod.Evening;
        }
    }

    public enum TimePeriod{
        Morning,
        Noon,
        Evening
    }
}