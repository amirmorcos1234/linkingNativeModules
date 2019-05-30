package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alex on 1/18/2017.
 */

public class PhoneCheckRequest extends BaseRequest {


    @SerializedName("phoneNumber")
    private String phoneNumber ;

//{"requesterId":"mcare","requesterPassword":"mcare","username":"alinrescorp","password":"12345678aA"}


    public PhoneCheckRequest(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
