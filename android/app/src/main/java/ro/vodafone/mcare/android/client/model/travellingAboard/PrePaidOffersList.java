package ro.vodafone.mcare.android.client.model.travellingAboard;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PrePaidOffersList extends RealmObject {
    @SerializedName("offerName")
    private String offerName;
    @SerializedName("offerPrice")
    private Integer offerPrice;
    @SerializedName("offerShortDescription")
    private String offerShortDescription;
    @PrimaryKey
    @SerializedName("offerId")
    private Long offerId;

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public Integer getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(Integer offerPrice) {
        this.offerPrice = offerPrice;
    }

    public String getOfferShortDescription() {
        return offerShortDescription;
    }

    public void setOfferShortDescription(String offerShortDescription) {
        this.offerShortDescription = offerShortDescription;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }
}
