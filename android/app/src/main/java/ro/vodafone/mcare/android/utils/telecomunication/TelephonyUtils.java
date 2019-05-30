package ro.vodafone.mcare.android.utils.telecomunication;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by Victor Radulescu on 7/26/2017.
 */

public class TelephonyUtils {

    public static boolean isSimSupport(Context context)
    {   if(context==null){
        return false;
    }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
        return !(tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT);

    }
}
