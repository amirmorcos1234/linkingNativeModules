package ro.vodafone.mcare.android.utils;

import android.app.Activity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Created by User on 27.11.2017.
 */

public class CallStateListener extends PhoneStateListener {

    private boolean callStateFlag = false;
    private Activity activity;

    public CallStateListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);

        switch (state){
            case TelephonyManager.CALL_STATE_IDLE:
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if(!callStateFlag){
                    activity.finish();
                }
                callStateFlag = true;
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                break;
        }
    }
}
