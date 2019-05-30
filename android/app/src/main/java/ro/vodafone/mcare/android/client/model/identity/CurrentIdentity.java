package ro.vodafone.mcare.android.client.model.identity;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Serban Radulescu on 7/25/2017.
 */

public class CurrentIdentity extends RealmObject {


    @PrimaryKey
    long id;

    @SerializedName("entityId")
    private String entityId = null;

    @SerializedName("entityType")
    private String entityType = null;

    @SerializedName("billingCustomerParentId")
    private String billingCustomerParentId = null;

    @SerializedName("accountCuiParentId")
    private String accountCuiParentId = null;

    @SerializedName("accountHolding")
    private String accountHoldingId = null;

    public CurrentIdentity() {
        id = 1;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getBillingCustomerParentId() {
        return billingCustomerParentId;
    }

    public void setBillingCustomerParentId(String billingCustomerParentId) {
        this.billingCustomerParentId = billingCustomerParentId;
    }

    public String getAccountCuiParentId() {
        return accountCuiParentId;
    }

    public void setAccountCuiParentId(String accountCuiParentId) {
        this.accountCuiParentId = accountCuiParentId;
    }

    public String getAccountHoldingId() {
        return accountHoldingId;
    }

    public void setAccountHoldingId(String accountHoldingId) {
        this.accountHoldingId = accountHoldingId;
    }
}
