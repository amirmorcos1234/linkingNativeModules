package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bivol Pavel on 19.01.2017.
 */
public class ConfirmProfileRequest {

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("email")
    private String email;

    public ConfirmProfileRequest(String phoneNumber, String email) {
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
