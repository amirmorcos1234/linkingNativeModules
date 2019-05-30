package ro.vodafone.mcare.android.client.model.gdpr;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by cosmin deliu on 2/6/2018.
 */

public class GdprGetResponse extends RealmObject {

    @PrimaryKey
    private int id;

    @SerializedName("gpdrPermissions")
    private GdprPermissions gdprPermissions;

    public GdprGetResponse() {
        id = 1;
    }

    public int getId_gdpr() {
        return id;
    }
    public void setId_gdpr(int id_gdpr) {
        this.id = id_gdpr;
    }

    public GdprPermissions getGdprPermissions() {
        return gdprPermissions;
    }
    public void setGdprPermissions(GdprPermissions gdprPermissions) {
        this.gdprPermissions = gdprPermissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GdprGetResponse gdprGetResponse = (GdprGetResponse) o;

        return this.gdprPermissions == null ? gdprGetResponse.gdprPermissions == null : this.gdprPermissions.equals(gdprGetResponse.gdprPermissions);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.gdprPermissions == null ? 0: this.gdprPermissions.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class GdprGetResponse {\n");

        sb.append("  gdprPermissions: ").append(gdprPermissions).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
