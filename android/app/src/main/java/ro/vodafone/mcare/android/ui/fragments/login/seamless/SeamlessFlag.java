package ro.vodafone.mcare.android.ui.fragments.login.seamless;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by user on 09.02.2017.
 */
public class SeamlessFlag extends RealmObject {

    @PrimaryKey
    int id_seamlessFlag;

    @SerializedName("seamlessFlag")
    boolean seamlessFlag;

    public SeamlessFlag() {
        this.id_seamlessFlag = 1;
    }

    public SeamlessFlag(String msisdn, boolean seamlessFlag) {
        this.seamlessFlag = seamlessFlag;
    }

    public boolean isSeamlessFlag() {
        return seamlessFlag;
    }

    public void setSeamlessFlag(boolean flag) {
        this.seamlessFlag = seamlessFlag;
    }

    public boolean getSeamlessFlag() {
        return seamlessFlag;
    }
}
