package ro.vodafone.mcare.android.client.model.gdpr;

import android.util.Log;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ro.vodafone.mcare.android.utils.RealmManager;

/**
 * Created by cosmin deliu on 4/4/2018.
 */

public class SessionIdMap extends RealmObject {

    @PrimaryKey
    private String key;
    private String value;

    private boolean isUserData = false;

    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String IS_USER_DATA = "isUserData";

    public SessionIdMap() {
    }

    public SessionIdMap(String key, String value,boolean isUserData) {
        this.key = key;
        this.value = value;
        this.isUserData = isUserData;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isUserData() {
        return isUserData;
    }

    public void setUserData(boolean userData) {
        isUserData = userData;
    }

    public static String getSessionIdForSid(String name) {
        SessionIdMap sessionId = ((SessionIdMap) RealmManager.getRealmObjectAfterStringField(SessionIdMap.class,"key", name));
        Log.d("SessionIdMap",sessionId != null ? sessionId.getKey() : "no sessionId");
        return sessionId != null ? sessionId.getValue() : null;
    }

}
