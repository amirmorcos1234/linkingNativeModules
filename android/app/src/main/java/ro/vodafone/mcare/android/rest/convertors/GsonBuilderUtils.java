package ro.vodafone.mcare.android.rest.convertors;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Victor Radulescu on 9/20/2017.
 */

public class GsonBuilderUtils {

    public static GsonBuilder getAllDateFormatsGson(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new DateTypeDeserializer());
        return gsonBuilder;
    }

    public static class DateTypeDeserializer implements JsonDeserializer<Date> {
        private static final String[] DATE_FORMATS = new String[]{
                "yyyy-MM-dd'T'HH:mm:ssZ",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd",
                "EEE MMM dd HH:mm:ss z yyyy",
                "HH:mm:ss",
                "MM/dd/yyyy",
                "MM/dd/yyyy HH:mm:ss aaa",
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS",
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'",
                "MMM d',' yyyy H:mm:ss a"
        };

        @Override
        public Date deserialize(JsonElement jsonElement, Type typeOF, JsonDeserializationContext context) throws JsonParseException {
            for (String format : DATE_FORMATS) {
                try {
                    return new SimpleDateFormat(format).parse(jsonElement.getAsString());
                } catch (ParseException e) {
                }
            }
            throw new JsonParseException("Unparseable date: \"" + jsonElement.getAsString()
                    + "\". Supported formats: \n" + Arrays.toString(DATE_FORMATS));
        }
    }
}
