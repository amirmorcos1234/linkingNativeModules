package ro.vodafone.mcare.android.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Victor Radulescu on 12/29/2016.
 */

public class SettingUtils {
    public static void openNetworkSettings(Context context){
        Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        //intent.setClassName("com.android.phone", "com.android.phone.ACTION_WIRELESS_SETTINGS");
        context.startActivity(intent);
    }

}
