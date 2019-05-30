package ro.vodafone.mcare.android.application.controllers;

/**
 * Created by Serban Radulescu on 10/6/2017.
 */

public class TemporaryFlagController {

    public static final String TAG = TemporaryFlagController.class.getSimpleName();

    private final boolean hideNonTelco = false;//false == apare nontelco ---- true == nu apare nontelco
    private final boolean shouldEncodeVoucherNames = false;

    private static TemporaryFlagController mInstance;

    private TemporaryFlagController(){
    }

    public static TemporaryFlagController getInstance() {
        if(mInstance==null){
            mInstance = new TemporaryFlagController();
        }
        return mInstance;
    }

    @Deprecated
    public boolean isHideNonTelco() {
        return hideNonTelco;
    }

    public boolean isShouldEncodeVoucherNames() {
        return shouldEncodeVoucherNames;
    }

}
