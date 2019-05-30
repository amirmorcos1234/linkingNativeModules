package ro.vodafone.mcare.android.client.model.realm.shop;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Victor Radulescu on 4/27/2017.
 */

public class ShopCurrentSelection extends RealmObject {

    @PrimaryKey
    long id;

    @SerializedName("pricePlanSkuId")
    private String pricePlanSkuId = null;
    @SerializedName("phoneSkuId")
    private String phoneSkuId = null;

    public ShopCurrentSelection() {
        id = 1;
    }

    public String getPricePlanSkuId() {
        return pricePlanSkuId;
    }

    public void setPricePlanSkuId(String pricePlanSkuId) {
        this.pricePlanSkuId = pricePlanSkuId;
    }

    public String getPhoneSkuId() {
        return phoneSkuId;
    }

    public void setPhoneSkuId(String phoneSkuId) {
        this.phoneSkuId = phoneSkuId;
    }
}
