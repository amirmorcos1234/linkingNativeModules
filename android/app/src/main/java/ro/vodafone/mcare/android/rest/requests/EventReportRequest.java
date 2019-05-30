package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Victor Radulescu on 1/13/2017.
 */

public class EventReportRequest extends BaseRequest {

    @SerializedName("networkType")
    private String networkType;

    @SerializedName("actionDate")
    private long actionDate;

    @SerializedName("referrerId")
    private String referrerId;

    @SerializedName("actionType")
    private String actionType;


    public EventReportRequest(String networkType, long actionDate, String referrerId, String actionType) {
        this.networkType = networkType;
        this.actionDate = actionDate;
        this.referrerId = referrerId;
        this.actionType = actionType;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public long getActionDate() {
        return actionDate;
    }

    public void setActionDate(long actionDate) {
        this.actionDate = actionDate;
    }

    public String getReferrerId() {
        return referrerId;
    }

    public void setReferrerId(String referrerId) {
        this.referrerId = referrerId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}
