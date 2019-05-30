package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Bogdan Marica on 16.01.2017.
 */

/**
 * Request object with data required for recover password.
 **/
@ApiModel(description = "Request object with data required for recover password.")
public class RecoverPasswordRequest extends RestApiRequest {

    @SerializedName("phoneNumber")
    private String phoneNumber = null;
    @SerializedName("email")
    private String email = null;
    @SerializedName("customerType")
    private String customerType = null;
    @SerializedName("cui")
    private String cui = null;
    @SerializedName("username")
    private String username = null;
    @SerializedName("stepParam")
    private String stepParam = null;

    /**
     * format 0xxxxxxxxx
     **/
    @ApiModelProperty(required = true, value = "format 0xxxxxxxxx")
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     **/
    @ApiModelProperty(required = true, value = "")
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * CustomerType for specific user for EBUMigrated = ClientVodafone, for CorpUser or CorpSubUser = Business, for CBUL = Consumer, for non Vodafone = nonVF.
     **/
    @ApiModelProperty(value = "CustomerType for specific user for EBUMigrated = ClientVodafone, for CorpUser or CorpSubUser = Business, for CBUL = Consumer, for non Vodafone = nonVF.")
    public String getCustomerType() {
        return customerType;
    }
    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    /**
     * Fiscale Code for specific user, required only user is CorpUser or CorpSubUser
     **/
    @ApiModelProperty(value = "Fiscale Code for specific user, required only user is CorpUser or CorpSubUser")
    public String getCui() {
        return cui;
    }
    public void setCui(String cui) {
        this.cui = cui;
    }

    /**
     * required only if multiple accounts detect
     **/
    @ApiModelProperty(value = "required only if multiple accounts detect")
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Request Step
     **/
    @ApiModelProperty(value = "Request Step")
    public String getStepParam() {
        return stepParam;
    }
    public void setStepParam(String stepParam) {
        this.stepParam = stepParam;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecoverPasswordRequest recoverPasswordRequest = (RecoverPasswordRequest) o;
        return (this.phoneNumber == null ? recoverPasswordRequest.phoneNumber == null : this.phoneNumber.equals(recoverPasswordRequest.phoneNumber)) &&
                (this.email == null ? recoverPasswordRequest.email == null : this.email.equals(recoverPasswordRequest.email)) &&
                (this.customerType == null ? recoverPasswordRequest.customerType == null : this.customerType.equals(recoverPasswordRequest.customerType)) &&
                (this.cui == null ? recoverPasswordRequest.cui == null : this.cui.equals(recoverPasswordRequest.cui)) &&
                (this.username == null ? recoverPasswordRequest.username == null : this.username.equals(recoverPasswordRequest.username)) &&
                (this.stepParam == null ? recoverPasswordRequest.stepParam == null : this.stepParam.equals(recoverPasswordRequest.stepParam));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.phoneNumber == null ? 0: this.phoneNumber.hashCode());
        result = 31 * result + (this.email == null ? 0: this.email.hashCode());
        result = 31 * result + (this.customerType == null ? 0: this.customerType.hashCode());
        result = 31 * result + (this.cui == null ? 0: this.cui.hashCode());
        result = 31 * result + (this.username == null ? 0: this.username.hashCode());
        result = 31 * result + (this.stepParam == null ? 0: this.stepParam.hashCode());
        return result;
    }

    @Override
    public String toString()  {
        StringBuilder sb = new StringBuilder();
        sb.append("class RecoverPasswordRequest {\n");
        sb.append("  " + super.toString()).append("\n");
        sb.append("  phoneNumber: ").append(phoneNumber).append("\n");
        sb.append("  email: ").append(email).append("\n");
        sb.append("  customerType: ").append(customerType).append("\n");
        sb.append("  cui: ").append(cui).append("\n");
        sb.append("  username: ").append(username).append("\n");
        sb.append("  stepParam: ").append(stepParam).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}