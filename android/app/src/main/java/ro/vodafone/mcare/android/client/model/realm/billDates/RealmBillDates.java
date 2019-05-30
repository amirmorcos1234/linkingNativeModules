package ro.vodafone.mcare.android.client.model.realm.billDates;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bivol Pavel on 09.05.2017.
 */

public class RealmBillDates extends RealmObject{

    @PrimaryKey
    private long id;

    @SerializedName("billClosedDatesList")
    RealmList<RealmLong> billClosedDatesList;

    public RealmBillDates() {
        id = 1;
    }

    public RealmBillDates(RealmList<RealmLong> billClosedDatesList) {
        this.billClosedDatesList = billClosedDatesList;
    }

    public RealmList<RealmLong> getBillClosedDatesList() {
        return billClosedDatesList;
    }

    public void setBillClosedDatesList(RealmList<RealmLong> billClosedDatesList) {
        this.billClosedDatesList = billClosedDatesList;
    }
}
