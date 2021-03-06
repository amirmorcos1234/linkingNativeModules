/**
 * API-60 Loyalty Program
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 8.0.1
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.loyaltyProgram;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * object containing data Loyalty Program
 **/
@ApiModel(description = "object containing data Loyalty Program")
public class OptOutSuccess {
  
  @SerializedName("setOptOutState")
  private String setOptOutState = null;

  /**
   * Loyalty Program Message Type
   **/
  @ApiModelProperty(value = "Loyalty Program Message Type")
  public String getSetOptOutState() {
    return setOptOutState;
  }
  public void setSetOptOutState(String setOptOutState) {
    this.setOptOutState = setOptOutState;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OptOutSuccess optOutSuccess = (OptOutSuccess) o;
    return (this.setOptOutState == null ? optOutSuccess.setOptOutState == null : this.setOptOutState.equals(optOutSuccess.setOptOutState));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.setOptOutState == null ? 0: this.setOptOutState.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class OptOutSuccess {\n");
    
    sb.append("  setOptOutState: ").append(setOptOutState).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
