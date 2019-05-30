package ro.vodafone.mcare.android.client.model.eligibility;

/**
 * Created by bogdan marica on 3/19/2017.
 */


import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public class EBillSuccess {

    @SerializedName("active")
    private Boolean active = null;
    @SerializedName("email")
    private String email = null;
    @SerializedName("billMediaType")
    private Integer billMediaType = null;

    /**
     * status flag active or inactive
     **/
    @ApiModelProperty(required = true, value = "status flag active or inactive")
    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * email for electronic bill
     **/
    @ApiModelProperty(required = true, value = "email for electronic bill")
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * current billMediaType value
     **/
    @ApiModelProperty(value = "current billMediaType value")
    public Integer getBillMediaType() {
        return billMediaType;
    }
    public void setBillMediaType(Integer billMediaType) {
        this.billMediaType = billMediaType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EBillSuccess eBillSuccess = (EBillSuccess) o;
        return (this.active == null ? eBillSuccess.active == null : this.active.equals(eBillSuccess.active)) &&
                (this.email == null ? eBillSuccess.email == null : this.email.equals(eBillSuccess.email)) &&
                (this.billMediaType == null ? eBillSuccess.billMediaType == null : this.billMediaType.equals(eBillSuccess.billMediaType));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.active == null ? 0: this.active.hashCode());
        result = 31 * result + (this.email == null ? 0: this.email.hashCode());
        result = 31 * result + (this.billMediaType == null ? 0: this.billMediaType.hashCode());
        return result;
    }

    @Override
    public String toString()  {
        StringBuilder sb = new StringBuilder();
        sb.append("class EBillSuccess {\n");

        sb.append("  active: ").append(active).append("\n");
        sb.append("  email: ").append(email).append("\n");
        sb.append("  billMediaType: ").append(billMediaType).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
