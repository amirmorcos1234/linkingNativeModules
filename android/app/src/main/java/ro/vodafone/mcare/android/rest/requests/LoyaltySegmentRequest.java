package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Serban Radulescu on 2/13/2018.
 */

public class LoyaltySegmentRequest extends RestApiRequest {

    @SerializedName("banList")
    private String banList[] = null;

    public LoyaltySegmentRequest(String[] banList) {
        this.banList = banList;
    }

    public String[] getBanList() {
        return banList;
    }

    public void setBanList(String[] banList) {
        this.banList = banList;
    }
}
