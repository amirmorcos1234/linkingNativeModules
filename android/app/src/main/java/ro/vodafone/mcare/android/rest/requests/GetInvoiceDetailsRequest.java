package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bivol Pavel on 02.02.2017.
 */
public class GetInvoiceDetailsRequest extends BaseRequest{

    @SerializedName("msisdn")
    String msisdn;

    @SerializedName("ban")
    String ban;

    public GetInvoiceDetailsRequest(String msisdn, String ban) {
        this.msisdn = msisdn;
        this.ban = ban;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getBan() {
        return ban;
    }

    public void setBan(String ban) {
        this.ban = ban;
    }
}
