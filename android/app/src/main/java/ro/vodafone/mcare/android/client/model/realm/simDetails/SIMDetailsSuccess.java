package ro.vodafone.mcare.android.client.model.realm.simDetails;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by user2 on 4/14/2017.
 */

public class SIMDetailsSuccess extends RealmObject {

    @SerializedName("displaySim")
    private boolean displaySim;

    @SerializedName("puk")
    private String puk;

    @SerializedName("sim")
    private String sim;

    @PrimaryKey
    private int id_sim_details;

    public boolean isDisplaySim() {
        return displaySim;
    }

    public int getId_sim_details() {
        return id_sim_details;
    }

    public void setId_sim_details(int id_sim_details) {
        this.id_sim_details = id_sim_details;
    }

    public void setDisplaySim(boolean displaySim) {
        this.displaySim = displaySim;
    }

    public String getPuk() {
        return puk;
    }

    public void setPuk(String puk) {
        this.puk = puk;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }
}
