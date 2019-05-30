package ro.vodafone.mcare.android.service.tracking.adobe.target.constants;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by User on 23.04.2018.
 */

public class TargetElementsConstants {
    public static final String VOV = "vov";
    public static final String VOV_UAT = "vovuat";

    public static final String OVERLAY_1 = "overlay1";
    public static final String OVERLAY_2 = "overlay2";

    public static final String OVERLAY_1_UAT = "overlay1uat";
    public static final String OVERLAY_2_UAT = "overlay2uat";

    public static final String NUDGE = "nudge";
    public static final String NUDGE_UAT = "nudgeuat";

    public static final String TOAST = "toast";
    public static final String TOAST_UAT = "toastuat";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef(value = {VOV, VOV_UAT, OVERLAY_1, OVERLAY_2, OVERLAY_1_UAT, OVERLAY_2_UAT, NUDGE, NUDGE_UAT, TOAST, TOAST_UAT})
    public @interface Element{}

    private static String elementName;

    public static void setTargetElement(@Element String element){
        elementName = element;
    }

    @Element
    public static String getTargetElement(){
        return elementName;
    }


}
