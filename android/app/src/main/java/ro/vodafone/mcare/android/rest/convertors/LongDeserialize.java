package ro.vodafone.mcare.android.rest.convertors;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

import io.realm.RealmList;
import io.realm.RealmObject;
import ro.vodafone.mcare.android.client.model.travellingAboard.PrepaidRealmLong;

/**
 * Created by OsMattar on 11-Jan-19.
 */

public class LongDeserialize {
    private static Type token = new TypeToken<RealmList<PrepaidRealmLong>>() {
    }.getType();
    public static Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getDeclaringClass().equals(RealmObject.class);
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }).registerTypeAdapter(token, new TypeAdapter<RealmList<PrepaidRealmLong>>() {
        @Override
        public void write(JsonWriter out, RealmList<PrepaidRealmLong> value) throws IOException {

        }

        @Override
        public RealmList<PrepaidRealmLong> read(JsonReader in) throws IOException {
            RealmList<PrepaidRealmLong> list = new RealmList<>();
            in.beginArray();
            while (in.hasNext()) {
                list.add(new PrepaidRealmLong(in.nextLong()));
            }
            in.endArray();
            return list;
        }
    }).create();
}
