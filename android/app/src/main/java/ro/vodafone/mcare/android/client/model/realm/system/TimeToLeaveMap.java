package ro.vodafone.mcare.android.client.model.realm.system;

import android.util.Log;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ro.vodafone.mcare.android.utils.RealmManager;

/**
 * Created by Victor Radulescu on 2/23/2017.
 */

public class TimeToLeaveMap extends RealmObject {

    //private String
    @PrimaryKey
    private String key;
    private long value;

    private boolean isUserData = false;

    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String IS_USER_DATA = "isUserData";


    public TimeToLeaveMap() {
    }

    public TimeToLeaveMap(String key, long value,boolean isUserData) {
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

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public boolean isUserData() {
        return isUserData;
    }

    public void setUserData(boolean userData) {
        isUserData = userData;
    }

    public static long getTimeToLiveForClass(Class className){
        String classNameString = className.getName();
        TimeToLeaveMap timeToLive=  ((TimeToLeaveMap) RealmManager.getRealmObjectAfterStringField(TimeToLeaveMap.class,"key",classNameString));
        Log.d("TimeToLeaveMap",timeToLive!=null?timeToLive.getKey():" no time to live");
        return timeToLive!=null ? timeToLive.getValue():0;
    }

    public static long getTimeToLiveForClassAfterKey(Class className, int key) {
        String classNameString = className.getName() + key;
        TimeToLeaveMap timeToLive=  ((TimeToLeaveMap) RealmManager.getRealmObjectAfterStringField(TimeToLeaveMap.class,"key",classNameString));
        Log.d("TimeToLeaveMap",timeToLive!=null?timeToLive.getKey():" no time to live");
        return timeToLive!=null ? timeToLive.getValue():0;
    }


}
