package ro.vodafone.mcare.android.client.model.shop;

/**
 * Created by bogdan marica on 4/4/2017.
 */

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Input parameters for send email request
 **/
@ApiModel(description = "Input parameters for send email request")
public class EmailRequest {

    @SerializedName("senderName")
    private String senderName = null;
    @SerializedName("phoneNumber")
    private String phoneNumber = null;
    @SerializedName("email")
    private String email = null;
    @SerializedName("clientType")
    private String clientType = null;
    @SerializedName("requestType")
    private String requestType = null;
    @SerializedName("description")
    private String description = null;
    @SerializedName("subject")
    private String subject = null;

    /**
     * Sender name
     **/
    @ApiModelProperty(required = true, value = "Sender name")
    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    /**
     * Sender phone number
     **/
    @ApiModelProperty(required = true, value = "Sender phone number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Sender email
     **/
    @ApiModelProperty(required = true, value = "Sender email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    @ApiModelProperty(required = true, value = "Sender email")
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Client type
     **/
    @ApiModelProperty(required = true, value = "Client type")
    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    /**
     * Request type
     **/
    @ApiModelProperty(required = true, value = "Request type")
    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    /**
     * Email description
     **/
    @ApiModelProperty(value = "Email description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EmailRequest emailRequest = (EmailRequest) o;
        return (this.senderName == null ? emailRequest.senderName == null : this.senderName.equals(emailRequest.senderName)) &&
                (this.phoneNumber == null ? emailRequest.phoneNumber == null : this.phoneNumber.equals(emailRequest.phoneNumber)) &&
                (this.email == null ? emailRequest.email == null : this.email.equals(emailRequest.email)) &&
                (this.clientType == null ? emailRequest.clientType == null : this.clientType.equals(emailRequest.clientType)) &&
                (this.requestType == null ? emailRequest.requestType == null : this.requestType.equals(emailRequest.requestType)) &&
                (this.description == null ? emailRequest.description == null : this.description.equals(emailRequest.description));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.senderName == null ? 0 : this.senderName.hashCode());
        result = 31 * result + (this.phoneNumber == null ? 0 : this.phoneNumber.hashCode());
        result = 31 * result + (this.email == null ? 0 : this.email.hashCode());
        result = 31 * result + (this.clientType == null ? 0 : this.clientType.hashCode());
        result = 31 * result + (this.requestType == null ? 0 : this.requestType.hashCode());
        result = 31 * result + (this.description == null ? 0 : this.description.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class EmailRequest {\n");

        sb.append("  senderName: ").append(senderName).append("\n");
        sb.append("  phoneNumber: ").append(phoneNumber).append("\n");
        sb.append("  email: ").append(email).append("\n");
        sb.append("  clientType: ").append(clientType).append("\n");
        sb.append("  requestType: ").append(requestType).append("\n");
        sb.append("  description: ").append(description).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}