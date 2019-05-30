package ro.vodafone.mcare.android.client.model.travellingAboard;

import io.realm.RealmObject;

public class PrepaidRealmLong extends RealmObject {
    private Long offerId;

    public PrepaidRealmLong() {
    }

    public PrepaidRealmLong(Long offerId) {
        this.offerId = offerId;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }
}
