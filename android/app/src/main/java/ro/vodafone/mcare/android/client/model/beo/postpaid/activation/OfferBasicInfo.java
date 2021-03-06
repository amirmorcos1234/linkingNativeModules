/**
 * API-41  Postpaid offer eligibility and activation
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 6.0.6
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.beo.postpaid.activation;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Offer basic info
 **/
@ApiModel(description = "Offer basic info")
public class OfferBasicInfo {
  
  @SerializedName("offerName")
  private String offerName = null;
  @SerializedName("offerPrice")
  private String offerPrice = null;

  /**
   * Name of incompatible offer
   **/
  @ApiModelProperty(value = "Name of incompatible offer")
  public String getOfferName() {
    return offerName;
  }
  public void setOfferName(String offerName) {
    this.offerName = offerName;
  }

  /**
   * Cost of incompatible offer
   **/
  @ApiModelProperty(value = "Cost of incompatible offer")
  public String getOfferPrice() {
    return offerPrice;
  }
  public void setOfferPrice(String offerPrice) {
    this.offerPrice = offerPrice;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferBasicInfo offerBasicInfo = (OfferBasicInfo) o;
    return (this.offerName == null ? offerBasicInfo.offerName == null : this.offerName.equals(offerBasicInfo.offerName)) &&
        (this.offerPrice == null ? offerBasicInfo.offerPrice == null : this.offerPrice.equals(offerBasicInfo.offerPrice));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.offerName == null ? 0: this.offerName.hashCode());
    result = 31 * result + (this.offerPrice == null ? 0: this.offerPrice.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferBasicInfo {\n");
    
    sb.append("  offerName: ").append(offerName).append("\n");
    sb.append("  offerPrice: ").append(offerPrice).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
