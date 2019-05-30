
package ro.vodafone.mcare.android.client.model.travellingAboard;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TravallingHintSuccess extends RealmObject {

    @PrimaryKey
    private int id;

    @SerializedName("hintsList")
    private RealmList<HintsList> hintsList = null;

    public TravallingHintSuccess() {
        id = 1;
    }

    public int getId() {
        return id;
    }

    /**
     *
     * @param hintsList
     */
    public TravallingHintSuccess(RealmList<HintsList> hintsList) {
        super();
        this.hintsList = hintsList;
    }

    public RealmList<HintsList> getHintsList() {
        return hintsList;
    }

    public void setHintsList(RealmList<HintsList> hintsList) {
        this.hintsList = hintsList;
    }


}
