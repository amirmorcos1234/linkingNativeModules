package ro.vodafone.mcare.android.client.model.recover;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Bogdan Marica on 11/1/2017.
 */

@ApiModel(description = "Response object with specific information about step and user.")
public class RecoverPasswordCorrectResponse {

    @SerializedName("username")
    private String username = null;
    @SerializedName("customerType")
    private String customerType = null;
    @SerializedName("isMigrated")
    private Boolean isMigrated = null;

    /**
     * Response username From IDM
     **/
    @ApiModelProperty(value = "Response username From IDM")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * IDM customerType for recovery Password
     **/
    @ApiModelProperty(value = "IDM customerType for recovery Password")
    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public Boolean getIsMigrated() {
        return isMigrated;
    }

    public void setIsMigrated(Boolean isMigrated) {
        this.isMigrated = isMigrated;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecoverPasswordCorrectResponse recoverPasswordResponse = (RecoverPasswordCorrectResponse) o;
        return (this.username == null ? recoverPasswordResponse.customerType == null : this.username.equals(recoverPasswordResponse.username)) &&
                (this.customerType == null ? recoverPasswordResponse.customerType == null : this.customerType.equals(recoverPasswordResponse.customerType));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.username == null ? 0 : this.username.hashCode());
        result = 31 * result + (this.customerType == null ? 0 : this.customerType.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RecoverPasswordResponse {\n");

        sb.append("  username: ").append(username).append("\n");
        sb.append("  customerType: ").append(customerType).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
