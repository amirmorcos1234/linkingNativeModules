package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user2 on 4/13/2017.
 */

public class SIMChangeStatusRequest extends BaseRequest {

    @SerializedName("sid")
    private String sid;

    @SerializedName("contactNo")
    private String contactNo;

    public SIMChangeStatusRequest(String sid, String contactNo) {
        this.sid = sid;
        this.contactNo = contactNo;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
}
