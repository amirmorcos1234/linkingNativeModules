package ro.vodafone.mcare.android.client.model.realm.system;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by user on 06.04.2017.
 */

public class SystemProperty extends RealmObject{
    @PrimaryKey
    @SerializedName("key")
    private String key;

    @SerializedName("value")
    private String value;

    public SystemProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public SystemProperty(){
        this.key = null;
        this.value = null;
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

}
