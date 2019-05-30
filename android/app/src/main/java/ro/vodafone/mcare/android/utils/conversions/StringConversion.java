package ro.vodafone.mcare.android.utils.conversions;

/**
 * Created by Victor Radulescu on 4/27/2017.
 */

public class StringConversion {

    public static Float getSafeFloat(String stringValue){
        Float floatNumber = null;
        try{
            floatNumber = Float.valueOf(stringValue);
        }catch (Exception e){
            e.printStackTrace();
        }
        return floatNumber;
    }
}
