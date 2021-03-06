/**
 * API-62 Travelling Abroad Postpaid
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 12.0.1
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.travellingAboard;

import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;

/**
 * Parameters for access type update
 **/
@ApiModel(description = "Parameters for access type update")
public class AccessTypeEbuRequest {
  
  @SerializedName("isInternational")
  private Boolean isInternational = null;
  @SerializedName("isRoaming")
  private Boolean isRoaming = null;
  @SerializedName("operation")
  private String operation = null;
  @SerializedName("phoneNumber")
  private String phoneNumber = null;
  @SerializedName("vfOdsSid")
  private String vfOdsSid = null;
  @SerializedName("vfOdsCid")
  private String vfOdsCid = null;
  @SerializedName("vfOdsBan")
  private String vfOdsBan = null;
  @SerializedName("productId")
  private String productId = null;
  @SerializedName("productSpecName")
  private String productSpecName = null;
  @SerializedName("vfOdsBen")
  private String vfOdsBen = null;

  /**
   * Flag indicating if International access type is enabled for subscriber
   **/
  @ApiModelProperty(required = true, value = "Flag indicating if International access type is enabled for subscriber")
  public Boolean getIsInternational() {
    return isInternational;
  }
  public void setIsInternational(Boolean isInternational) {
    this.isInternational = isInternational;
  }

  /**
   * Flag indicating if Roaming access type is enabled for subscriber
   **/
  @ApiModelProperty(required = true, value = "Flag indicating if Roaming access type is enabled for subscriber")
  public Boolean getIsRoaming() {
    return isRoaming;
  }
  public void setIsRoaming(Boolean isRoaming) {
    this.isRoaming = isRoaming;
  }

  /**
   * Indicates operation type. Possible values are \"activateInternational\", \"activateRoaming\", \"deactivateInternational\", \"deactivateRoaming\"
   **/
  @ApiModelProperty(required = true, value = "Indicates operation type. Possible values are \"activateInternational\", \"activateRoaming\", \"deactivateInternational\", \"deactivateRoaming\"")
  public String getOperation() {
    return operation;
  }
  public void setOperation(String operation) {
    this.operation = operation;
  }

  /**
   **/
  @ApiModelProperty(required = true, value = "")
  public String getPhoneNumber() {
    return phoneNumber;
  }
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  /**
   **/
  @ApiModelProperty(required = true, value = "")
  public String getVfOdsSid() {
    return vfOdsSid;
  }
  public void setVfOdsSid(String vfOdsSid) {
    this.vfOdsSid = vfOdsSid;
  }

  /**
   **/
  @ApiModelProperty(required = true, value = "")
  public String getVfOdsCid() {
    return vfOdsCid;
  }
  public void setVfOdsCid(String vfOdsCid) {
    this.vfOdsCid = vfOdsCid;
  }

  /**
   **/
  @ApiModelProperty(required = true, value = "")
  public String getVfOdsBan() {
    return vfOdsBan;
  }
  public void setVfOdsBan(String vfOdsBan) {
    this.vfOdsBan = vfOdsBan;
  }

  /**
   **/
  @ApiModelProperty(required = true, value = "")
  public String getProductId() {
    return productId;
  }
  public void setProductId(String productId) {
    this.productId = productId;
  }

  /**
   **/
  @ApiModelProperty(required = true, value = "")
  public String getProductSpecName() {
    return productSpecName;
  }
  public void setProductSpecName(String productSpecName) {
    this.productSpecName = productSpecName;
  }

  /**
   **/
  @ApiModelProperty(required = true, value = "")
  public String getVfOdsBen() {
    return vfOdsBen;
  }
  public void setVfOdsBen(String vfOdsBen) {
    this.vfOdsBen = vfOdsBen;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccessTypeEbuRequest accessTypeEbuRequest = (AccessTypeEbuRequest) o;
    return (this.isInternational == null ? accessTypeEbuRequest.isInternational == null : this.isInternational.equals(accessTypeEbuRequest.isInternational)) &&
        (this.isRoaming == null ? accessTypeEbuRequest.isRoaming == null : this.isRoaming.equals(accessTypeEbuRequest.isRoaming)) &&
        (this.operation == null ? accessTypeEbuRequest.operation == null : this.operation.equals(accessTypeEbuRequest.operation)) &&
        (this.phoneNumber == null ? accessTypeEbuRequest.phoneNumber == null : this.phoneNumber.equals(accessTypeEbuRequest.phoneNumber)) &&
        (this.vfOdsSid == null ? accessTypeEbuRequest.vfOdsSid == null : this.vfOdsSid.equals(accessTypeEbuRequest.vfOdsSid)) &&
        (this.vfOdsCid == null ? accessTypeEbuRequest.vfOdsCid == null : this.vfOdsCid.equals(accessTypeEbuRequest.vfOdsCid)) &&
        (this.vfOdsBan == null ? accessTypeEbuRequest.vfOdsBan == null : this.vfOdsBan.equals(accessTypeEbuRequest.vfOdsBan)) &&
        (this.productId == null ? accessTypeEbuRequest.productId == null : this.productId.equals(accessTypeEbuRequest.productId)) &&
        (this.productSpecName == null ? accessTypeEbuRequest.productSpecName == null : this.productSpecName.equals(accessTypeEbuRequest.productSpecName)) &&
        (this.vfOdsBen == null ? accessTypeEbuRequest.vfOdsBen == null : this.vfOdsBen.equals(accessTypeEbuRequest.vfOdsBen));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.isInternational == null ? 0: this.isInternational.hashCode());
    result = 31 * result + (this.isRoaming == null ? 0: this.isRoaming.hashCode());
    result = 31 * result + (this.operation == null ? 0: this.operation.hashCode());
    result = 31 * result + (this.phoneNumber == null ? 0: this.phoneNumber.hashCode());
    result = 31 * result + (this.vfOdsSid == null ? 0: this.vfOdsSid.hashCode());
    result = 31 * result + (this.vfOdsCid == null ? 0: this.vfOdsCid.hashCode());
    result = 31 * result + (this.vfOdsBan == null ? 0: this.vfOdsBan.hashCode());
    result = 31 * result + (this.productId == null ? 0: this.productId.hashCode());
    result = 31 * result + (this.productSpecName == null ? 0: this.productSpecName.hashCode());
    result = 31 * result + (this.vfOdsBen == null ? 0: this.vfOdsBen.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class AccessTypeEbuRequest {\n");
    
    sb.append("  isInternational: ").append(isInternational).append("\n");
    sb.append("  isRoaming: ").append(isRoaming).append("\n");
    sb.append("  operation: ").append(operation).append("\n");
    sb.append("  phoneNumber: ").append(phoneNumber).append("\n");
    sb.append("  vfOdsSid: ").append(vfOdsSid).append("\n");
    sb.append("  vfOdsCid: ").append(vfOdsCid).append("\n");
    sb.append("  vfOdsBan: ").append(vfOdsBan).append("\n");
    sb.append("  productId: ").append(productId).append("\n");
    sb.append("  productSpecName: ").append(productSpecName).append("\n");
    sb.append("  vfOdsBen: ").append(vfOdsBen).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
