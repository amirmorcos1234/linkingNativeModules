package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Bogdan Marica on 16.01.2017.
 */
/**
 * API-15 Recover Username for EBU and CBU user
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 10.0.3
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
@ApiModel(description = "Request object with data required for recover username.")
public class RecoverUsernameRequest extends RestApiRequest {

    @SerializedName("phoneNumber")
    private String phoneNumber = null;
    @SerializedName("email")
    private String email = null;
    @SerializedName("customerType")
    private String customerType = null;
    @SerializedName("cui")
    private String cui = null;
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
     * CustomerType for specific user for EBUMigrated = ClientVodafone, for CorpUser or CorpSubUser = Business, for CBUL = Consumer, for non Vodafone = nonVFl
     **/
    @ApiModelProperty(value = "CustomerType for specific user for EBUMigrated = ClientVodafone, for CorpUser or CorpSubUser = Business, for CBUL = Consumer, for non Vodafone = nonVFl")
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
        RecoverUsernameRequest recoverUsernameRequest = (RecoverUsernameRequest) o;
        return (this.phoneNumber == null ? recoverUsernameRequest.phoneNumber == null : this.phoneNumber.equals(recoverUsernameRequest.phoneNumber)) &&
                (this.email == null ? recoverUsernameRequest.email == null : this.email.equals(recoverUsernameRequest.email)) &&
                (this.customerType == null ? recoverUsernameRequest.customerType == null : this.customerType.equals(recoverUsernameRequest.customerType)) &&
                (this.cui == null ? recoverUsernameRequest.cui == null : this.cui.equals(recoverUsernameRequest.cui)) &&
                (this.stepParam == null ? recoverUsernameRequest.stepParam == null : this.stepParam.equals(recoverUsernameRequest.stepParam));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.phoneNumber == null ? 0: this.phoneNumber.hashCode());
        result = 31 * result + (this.email == null ? 0: this.email.hashCode());
        result = 31 * result + (this.customerType == null ? 0: this.customerType.hashCode());
        result = 31 * result + (this.cui == null ? 0: this.cui.hashCode());
        result = 31 * result + (this.stepParam == null ? 0: this.stepParam.hashCode());
        return result;
    }

    @Override
    public String toString()  {
        StringBuilder sb = new StringBuilder();
        sb.append("class RecoverUsernameRequest {\n");
        sb.append("  " + super.toString()).append("\n");
        sb.append("  phoneNumber: ").append(phoneNumber).append("\n");
        sb.append("  email: ").append(email).append("\n");
        sb.append("  customerType: ").append(customerType).append("\n");
        sb.append("  cui: ").append(cui).append("\n");
        sb.append("  stepParam: ").append(stepParam).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
