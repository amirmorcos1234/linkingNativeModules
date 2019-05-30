package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alex on 1/19/2017.
 */

public class ResendCodeRequest extends BaseRequest{


@SerializedName("username")
private String username;

@SerializedName("email")
private String email;

@SerializedName("phoneNumber")
private String phoneNumber;

@SerializedName("customerType")
private String customerType;




public ResendCodeRequest(String username,String email,String phoneNumber,String customerType){
        this.setUsername(username);
        this.setEmail(email);
        this.setPhoneNumber(phoneNumber);
        this.setCustomerType(customerType);

        }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }
}