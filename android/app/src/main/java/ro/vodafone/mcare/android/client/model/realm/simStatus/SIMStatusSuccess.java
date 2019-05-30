package ro.vodafone.mcare.android.client.model.realm.simStatus;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by user2 on 4/13/2017.
 */

public class SIMStatusSuccess extends RealmObject {

    @SerializedName("isActiv")
    private boolean isActive;

    @PrimaryKey
    private int id_sim_status;

    public boolean isActiv() {
        return isActive;
    }

    public void setActiv(boolean active) {
        isActive = active;
    }

    public int getId_sim_status() {
        return id_sim_status;
    }

    public void setId_sim_status(int id_sim_status) {
        this.id_sim_status = id_sim_status;
    }
}
