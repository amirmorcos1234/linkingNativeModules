package ro.vodafone.mcare.android.ui.utils;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by User on 30.10.2017.
 */

public class PhoneNumberUtils {

    public static String checkNumberMsisdnFormat(String msisdn){
        String msisdnFinal = msisdn;
        Log.d("", "lenght msisdn is :" +msisdnFinal.length());

       if( msisdn.startsWith("4"))
           msisdnFinal =  msisdn.substring(1);

        else if (msisdn.length() == 9 )
            msisdnFinal = "0" + msisdn;

        return msisdnFinal;
    }

    public static String checkVfNumberFormat(String msisdn){
        if(msisdn.length() == 10 && msisdn.startsWith("0"))
            return "4" + msisdn;
        else if(msisdn.length() == 9 && msisdn.startsWith("7"))
            return "40" + msisdn;

        return msisdn;
    }

    public static boolean isValidPhoneNumber(String value) {
        boolean isValid = false;

        if (null != value && !"".equals(value)) {

            if (value.length() == 10) {
                Pattern pattern = Pattern.compile("^0[0-9]{9}");
                Matcher matcher = pattern.matcher(value);

                isValid = matcher.matches() && value.substring(0, 2).equals("07");
            } else
                isValid = false;
        }
        return isValid;
    }

    public static String checkSidFormat(String sid) {

        if (sid != null && sid.startsWith("-")) {
            sid = sid.replaceFirst("-", "");
        }
        return  sid;
    }
}
