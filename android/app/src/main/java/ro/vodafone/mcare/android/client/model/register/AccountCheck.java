package ro.vodafone.mcare.android.client.model.register;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import ro.vodafone.mcare.android.client.model.AbstractResponse;

/**
 * Created by Alex on 1/18/2017.
 */

public class AccountCheck extends AbstractResponse implements Serializable {

    @SerializedName("customerType")
    private String customerType;

    @SerializedName("vfProvider")
    private String vfProvider;

    @SerializedName("subscriberType")
    private String subscriberType;

    @SerializedName("isMigrated")
    private boolean isMigrated;

    public boolean isMigrated() {
        return isMigrated;
    }

    public void setMigrated(boolean migrated) {
        isMigrated = migrated;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getVfProvider() {
        return vfProvider;
    }

    public void setVfProvider(String vfProvider) {
        this.vfProvider = vfProvider;
    }

    public String getSubscriberType() {
        return subscriberType;
    }

    public void setSubscriberType(String subscriberType) {
        this.subscriberType = subscriberType;
    }
}
