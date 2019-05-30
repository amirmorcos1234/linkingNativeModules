package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

import ro.vodafone.mcare.android.client.Hosts;

/**
 * Created by Victor Radulescu on 1/13/2017.
 */

public class BaseRequest {

    @SerializedName("requesterId")
    protected String requesterId = "mcare";

    @SerializedName("requesterPassword")
    protected String requesterPassword = "mcare";

    public BaseRequest() {
        switch (Hosts.getEnviroment()){
            case PET:
            case PET_OT:
            case PET_OT_AUTH:
                this.requesterPassword ="791a69431a976083560e101189d7670cd235d681";
                break;
            case UAT:
                this.requesterPassword ="791a69431a976083560e101189d7670cd235d681";
                break;
            case PROD:
            case PROD_AUTH:
                this.requesterPassword ="791a69431a976083560e101189d7670cd235d681";
                break;
            default:
                this.requesterPassword ="mcare";
                break;
        }
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequesterPassword() {
        return requesterPassword;
    }

    public void setRequesterPassword(String requesterPassword) {
        this.requesterPassword = requesterPassword;

    }
}