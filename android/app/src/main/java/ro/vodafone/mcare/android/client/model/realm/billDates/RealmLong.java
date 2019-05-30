package ro.vodafone.mcare.android.client.model.realm.billDates;

import io.realm.RealmObject;

/**
 * Created by Bivol Pavel on 09.05.2017.
 */

public class RealmLong extends RealmObject {

    private Long value;

    public RealmLong() {
    }

    public RealmLong(Long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
