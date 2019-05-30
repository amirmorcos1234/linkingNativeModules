package ro.vodafone.mcare.android.client.model.realm.system;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Labels extends RealmObject {

    @PrimaryKey
    private long id;

    @SerializedName("updates")
    private Integer updates = null;

    @SerializedName("systemProperties")
    private RealmList<SystemProperty> systemProperties = new RealmList<>();

    public Labels(){
        this.id = 1;
    }
    public Labels(Integer updates, RealmList<SystemProperty> systemProperties) {
        this.updates = updates;
        this.systemProperties = systemProperties;
    }

    public Integer getUpdates() {
        return updates;
    }

    public void setUpdates(Integer updates) {
        this.updates = updates;
    }

    public RealmList<SystemProperty> getSystemProperties() {
        return systemProperties;
    }

    public void setSystemProperties(RealmList<SystemProperty> systemProperties) {
        this.systemProperties = systemProperties;
    }
}
