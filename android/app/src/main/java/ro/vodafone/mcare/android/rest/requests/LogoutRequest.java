package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Victor Radulescu on 1/13/2017.
 */

public class LogoutRequest extends BaseRequest {

    @SerializedName("ssoTokenId")
    private String ssoTokenId;

    public LogoutRequest(String ssoTokenId) {
        this.ssoTokenId = ssoTokenId;
    }

    public String getSsoTokenId() {
        return ssoTokenId;
    }

    public void setSsoTokenId(String ssoTokenId) {
        this.ssoTokenId = ssoTokenId;
    }
}
