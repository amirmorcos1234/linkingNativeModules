package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Victor Radulescu on 1/13/2017.
 */

public class SeamlessLoginRequest extends BaseRequest {

    @SerializedName("initialToken")
    private String initialToken;

    public SeamlessLoginRequest(String initialToken) {
        this.initialToken = initialToken;
    }

    public String getInitialToken() {
        return initialToken;
    }

    public void setInitialToken(String initialToken) {
        this.initialToken = initialToken;
    }
}
