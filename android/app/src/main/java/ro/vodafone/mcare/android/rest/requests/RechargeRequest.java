/**
 * API-24  Payment Request
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 3.0.6
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.rest.requests;


import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Request object with data required for payment.
 **/
@ApiModel(description = "Request object with data required for payment.")
public class RechargeRequest extends RestApiRequest {
  
  @SerializedName("phoneNumber")
  private String phoneNumber = null;
  @SerializedName("amount")
  private Float amount = null;
  @SerializedName("email")
  private String email = null;
  @SerializedName("offerId")
  private String offerId = null;
  @SerializedName("subscriberId")
  private String subscriberId = null;

  public RechargeRequest(String phoneNumber, Float amount, String email, String offerId, String subscriberId) {
    this.phoneNumber = phoneNumber;
    this.amount = amount;
    this.email = email;
    this.offerId = offerId;
    this.subscriberId = subscriberId;
  }


  /**
   * prepaid phone number to be recharged
   **/
  @ApiModelProperty(required = true, value = "prepaid phone number to be recharged")
  public String getPhoneNumber() {
    return phoneNumber;
  }
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  /**
   * amount to pay
   * minimum: 0
   **/
  @ApiModelProperty(required = true, value = "amount to pay")
  public Float getAmount() {
    return amount;
  }
  public void setAmount(Float amount) {
    this.amount = amount;
  }

  /**
   * user input email
   **/
  @ApiModelProperty(required = true, value = "user input email")
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * offer id for Recharge with extraoptions
   **/
  @ApiModelProperty(value = "offer id for Recharge with extraoptions")
  public String getOfferId() {
    return offerId;
  }
  public void setOfferId(String offerId) {
    this.offerId = offerId;
  }

  /**
   * subscriber id for Recharge with extraoptions
   **/
  @ApiModelProperty(required = true, value = "subscriber id for Recharge with extraoptions")
  public String getSubscriberId() {
    return subscriberId;
  }
  public void setSubscriberId(String subscriberId) {
    this.subscriberId = subscriberId;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RechargeRequest rechargeRequest = (RechargeRequest) o;
    return (this.phoneNumber == null ? rechargeRequest.phoneNumber == null : this.phoneNumber.equals(rechargeRequest.phoneNumber)) &&
        (this.amount == null ? rechargeRequest.amount == null : this.amount.equals(rechargeRequest.amount)) &&
        (this.email == null ? rechargeRequest.email == null : this.email.equals(rechargeRequest.email)) &&
        (this.offerId == null ? rechargeRequest.offerId == null : this.offerId.equals(rechargeRequest.offerId)) &&
        (this.subscriberId == null ? rechargeRequest.subscriberId == null : this.subscriberId.equals(rechargeRequest.subscriberId));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.phoneNumber == null ? 0: this.phoneNumber.hashCode());
    result = 31 * result + (this.amount == null ? 0: this.amount.hashCode());
    result = 31 * result + (this.email == null ? 0: this.email.hashCode());
    result = 31 * result + (this.offerId == null ? 0: this.offerId.hashCode());
    result = 31 * result + (this.subscriberId == null ? 0: this.subscriberId.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class RechargeRequest {\n");
    sb.append("  " + super.toString()).append("\n");
    sb.append("  phoneNumber: ").append(phoneNumber).append("\n");
    sb.append("  amount: ").append(amount).append("\n");
    sb.append("  email: ").append(email).append("\n");
    sb.append("  offerId: ").append(offerId).append("\n");
    sb.append("  subscriberId: ").append(subscriberId).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}