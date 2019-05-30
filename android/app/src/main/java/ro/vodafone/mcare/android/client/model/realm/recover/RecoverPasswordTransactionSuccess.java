package ro.vodafone.mcare.android.client.model.realm.recover;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bivol Pavel on 19.01.2017.
 */
public class RecoverPasswordTransactionSuccess {

    @SerializedName("username")
    private String username;

    @SerializedName("customerType")
    private String customerType;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }
}
