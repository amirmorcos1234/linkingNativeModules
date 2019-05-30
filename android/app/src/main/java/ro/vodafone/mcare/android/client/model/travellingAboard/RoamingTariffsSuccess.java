package ro.vodafone.mcare.android.client.model.travellingAboard;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ro.vodafone.mcare.android.ui.utils.TextUtils;
import ro.vodafone.mcare.android.utils.RealmManager;

/**
 * Created by Alex on 4/28/2017.
 */

public class RoamingTariffsSuccess extends RealmObject {

    @SerializedName("countryList")
    public RealmList<CountryList> countryList;
    @SerializedName("zonesList")
    public RealmList<ZonesList> zonesList;
    @PrimaryKey
    private int id;
    @SerializedName("offersList")
    private RealmList<PrePaidOffersList> offersList;

    public RoamingTariffsSuccess() {
        id = 1;
    }

    public RoamingTariffsSuccess(RealmList<CountryList> countryList, RealmList<ZonesList> zonesList, RealmList<PrePaidOffersList> offersList) {
        this.countryList = countryList;
        this.zonesList = zonesList;
        this.offersList = offersList;
    }

    public int getId() {
        return id;
    }

    public RealmList<CountryList> getCountryList() {
        return countryList;
    }

    public void setCountryList(RealmList<CountryList> countryList) {
        this.countryList = countryList;
    }

    public RealmList<ZonesList> getZonesList() {
        return zonesList;
    }

    public void setZonesList(RealmList<ZonesList> zonesList) {
        this.zonesList = zonesList;
    }

    public RealmList<PrePaidOffersList> getOffersList() {
        return offersList;
    }

    public void setOffersList(RealmList<PrePaidOffersList> offersList) {
        this.offersList = offersList;
    }

    public void setTextsInHTML() {
        RealmManager.startTransaction();
        for (int i = 0; i < this.countryList.size(); i++) {
            countryList.get(i).setCountryName(TextUtils.fromHtml(countryList.get(i).getCountryName()).toString());
            countryList.get(i).setMessageRo(TextUtils.fromHtml(countryList.get(i).getMessageRo()).toString());
        }
        RealmManager.commitTransaction();
    }
}
