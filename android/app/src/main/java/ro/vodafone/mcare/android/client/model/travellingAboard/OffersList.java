/**
 * API-35  Active offers prepaid
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 6.0.8
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
 * Offers List Object
 **/
@ApiModel(description = "Offers List Object")
public class OffersList {
  
  @SerializedName("srv_id")
  private Integer srvId = null;
  @SerializedName("offerCategory")
  private String offerCategory = null;
  @SerializedName("offerPrice")
  private Double offerPrice = null;

  /**
   * Offer Id
   **/
  @ApiModelProperty(required = true, value = "Offer Id")
  public Integer getSrvId() {
    return srvId;
  }
  public void setSrvId(Integer srvId) {
    this.srvId = srvId;
  }

  /**
   * Offer Category
   **/
  @ApiModelProperty(required = true, value = "Offer Category")
  public String getOfferCategory() {
    return offerCategory;
  }
  public void setOfferCategory(String offerCategory) {
    this.offerCategory = offerCategory;
  }

  /**
   * Offer Price
   **/
  @ApiModelProperty(required = true, value = "Offer Price")
  public Double getOfferPrice() {
    return offerPrice;
  }
  public void setOfferPrice(Double offerPrice) {
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
    OffersList offersList = (OffersList) o;
    return (this.srvId == null ? offersList.srvId == null : this.srvId.equals(offersList.srvId)) &&
        (this.offerCategory == null ? offersList.offerCategory == null : this.offerCategory.equals(offersList.offerCategory)) &&
        (this.offerPrice == null ? offersList.offerPrice == null : this.offerPrice.equals(offersList.offerPrice));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.srvId == null ? 0: this.srvId.hashCode());
    result = 31 * result + (this.offerCategory == null ? 0: this.offerCategory.hashCode());
    result = 31 * result + (this.offerPrice == null ? 0: this.offerPrice.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class OffersList {\n");
    
    sb.append("  srvId: ").append(srvId).append("\n");
    sb.append("  offerCategory: ").append(offerCategory).append("\n");
    sb.append("  offerPrice: ").append(offerPrice).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
