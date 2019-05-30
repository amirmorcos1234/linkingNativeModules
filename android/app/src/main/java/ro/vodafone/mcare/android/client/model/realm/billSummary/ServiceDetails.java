/**
 * API-56 Bill Summary
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 6.0.2
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.realm.billSummary;

import io.realm.RealmObject;
import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;

/**
 * Object containing details for charges
 **/
@ApiModel(description = "Object containing details for charges")
public class ServiceDetails extends RealmObject{
  
  @SerializedName("serviceDesc")
  private String serviceDesc = null;
  @SerializedName("billingAmount")
  private Double billingAmount = null;

  /**
   * Service description
   **/
  @ApiModelProperty(value = "Service description")
  public String getServiceDesc() {
    return serviceDesc;
  }
  public void setServiceDesc(String serviceDesc) {
    this.serviceDesc = serviceDesc;
  }

  /**
   * Service cost in lei (no VAT)
   **/
  @ApiModelProperty(value = "Service cost in lei (no VAT)")
  public Double getBillingAmount() {
    return billingAmount;
  }
  public void setBillingAmount(Double billingAmount) {
    this.billingAmount = billingAmount;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServiceDetails serviceDetails = (ServiceDetails) o;
    return (this.serviceDesc == null ? serviceDetails.serviceDesc == null : this.serviceDesc.equals(serviceDetails.serviceDesc)) &&
        (this.billingAmount == null ? serviceDetails.billingAmount == null : this.billingAmount.equals(serviceDetails.billingAmount));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.serviceDesc == null ? 0: this.serviceDesc.hashCode());
    result = 31 * result + (this.billingAmount == null ? 0: this.billingAmount.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServiceDetails {\n");
    
    sb.append("  serviceDesc: ").append(serviceDesc).append("\n");
    sb.append("  billingAmount: ").append(billingAmount).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}