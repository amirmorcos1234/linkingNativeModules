
package ro.vodafone.mcare.android.client.model.realm.system.mentenance;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SystemStatusSuccess extends RealmObject {

    @PrimaryKey
    private long id;

    @SerializedName("versionAndroid")
    private VersionApp versionApp;
    @SerializedName("versionIOS")
    private VersionApp versionIOS;
    @SerializedName("maintenance")
    private Maintenance maintenance;

    public SystemStatusSuccess() {
        id=1;
    }

    public VersionApp getVersionApp() {
        return versionApp;
    }

    public void setVersionApp(VersionApp versionApp) {
        this.versionApp = versionApp;
    }

    public SystemStatusSuccess withVersionAndroid(VersionApp versionApp) {
        this.versionApp = versionApp;
        return this;
    }

    public VersionApp getVersionIOS() {
        return versionIOS;
    }

    public void setVersionIOS(VersionApp versionIOS) {
        this.versionIOS = versionIOS;
    }

    public SystemStatusSuccess withVersionIOS(VersionApp versionIOS) {
        this.versionIOS = versionIOS;
        return this;
    }

    public Maintenance getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(Maintenance maintenance) {
        this.maintenance = maintenance;
    }

    public SystemStatusSuccess withMaintenance(Maintenance maintenance) {
        this.maintenance = maintenance;
        return this;
    }

}
