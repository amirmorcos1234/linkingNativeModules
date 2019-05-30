package ro.vodafone.mcare.android.client.model.realm;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Victor Radulescu on 1/10/2017.
 */

public class TransactionFault {

    @SerializedName("faultCode")
    private String faultCode;

    @SerializedName("faultMessage")
    private String faultMessage;

    @SerializedName("faultParam")
    private String faultParam;

    public String getFaultCode() {
        return faultCode;
    }

    public void setFaultCode(String faultCode) {
        this.faultCode = faultCode;
    }

    public String getFaultMessage() {
        return faultMessage;
    }

    public void setFaultMessage(String faultMessage) {
        this.faultMessage = faultMessage;
    }

    public String getFaultParam() {
        return faultParam;
    }

    public void setFaultParam(String faultParam) {
        this.faultParam = faultParam;
    }
}
