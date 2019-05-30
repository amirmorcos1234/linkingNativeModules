package ro.vodafone.mcare.android.utils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 05.02.2018.
 */

public class StringUtils {

    public static String concatenateStrings(String... args){
        StringBuilder resultString = new StringBuilder();
        for(String string : args){
            resultString.append(string);
        }
        return String.valueOf(resultString);
    }

    public static String capitalizeFirstLetter(String capString){
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()){
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    public static String getFieldFromJson(Object object, String field) {
        Gson gson = new Gson();
        String fieldValue = null;
        try {
            fieldValue = new JSONObject(gson.toJson(object)).getString(field);
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
        return fieldValue;
    }
}
