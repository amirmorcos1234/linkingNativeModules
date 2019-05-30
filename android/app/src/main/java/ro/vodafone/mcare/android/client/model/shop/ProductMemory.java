/**
 * API-47 Shop Products
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 7.0.1
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.shop;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * object containing data about ProductMemory
 **/
@ApiModel(description = "object containing data about ProductMemory")
public class ProductMemory extends RealmObject
{

  @SerializedName("memoryTitle")
  private String memory = null;
  @SerializedName("phoneSkuId")
  private String phoneSkuId = null;
  @SerializedName("pricePlanSkuId")
  private String pricePlanSkuId = null;

  /**
   * size 32,64,128,256
   **/
  @ApiModelProperty(value = "size 32,64,128,256")
  public String getMemory() {
    return memory;
  }
  public void setMemory(String memory) {
    this.memory = memory;
  }

  /**
   * unique identifier for phone product
   **/
  @ApiModelProperty(value = "unique identifier for phone product")
  public String getPhoneSkuId() {
    return phoneSkuId;
  }
  public void setPhoneSkuId(String phoneSkuId) {
    this.phoneSkuId = phoneSkuId;
  }

  /**
   * unique identifier for price plan
   **/
  @ApiModelProperty(value = "unique identifier for price plan")
  public String getPricePlanSkuId() {
    return pricePlanSkuId;
  }
  public void setPricePlanSkuId(String pricePlanSkuId) {
    this.pricePlanSkuId = pricePlanSkuId;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProductMemory productMemory = (ProductMemory) o;
    return (this.memory == null ? productMemory.memory == null : this.memory.equals(productMemory.memory)) &&
        (this.phoneSkuId == null ? productMemory.phoneSkuId == null : this.phoneSkuId.equals(productMemory.phoneSkuId)) &&
        (this.pricePlanSkuId == null ? productMemory.pricePlanSkuId == null : this.pricePlanSkuId.equals(productMemory.pricePlanSkuId));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.memory == null ? 0: this.memory.hashCode());
    result = 31 * result + (this.phoneSkuId == null ? 0: this.phoneSkuId.hashCode());
    result = 31 * result + (this.pricePlanSkuId == null ? 0: this.pricePlanSkuId.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProductMemory {\n");
    
    sb.append("  memory: ").append(memory).append("\n");
    sb.append("  phoneSkuId: ").append(phoneSkuId).append("\n");
    sb.append("  pricePlanSkuId: ").append(pricePlanSkuId).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}