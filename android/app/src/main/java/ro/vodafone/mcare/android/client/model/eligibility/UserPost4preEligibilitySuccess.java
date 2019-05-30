/**
 * API-31  User post4pre eligibility
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 5.0.2
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.eligibility;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Response object with data for bill recharges eligibility.
 **/
@ApiModel(description = "Response object with data for bill recharges eligibility.")
public class UserPost4preEligibilitySuccess {
  
  @SerializedName("eligibleBanList")
  private List<String> eligibleBanList = null;

  /**
   * BAN list for which user is eligible to make recharges
   **/
  @ApiModelProperty(required = true, value = "BAN list for which user is eligible to make recharges")
  public List<String> getEligibleBanList() {
    return eligibleBanList;
  }
  public void setEligibleBanList(List<String> eligibleBanList) {
    this.eligibleBanList = eligibleBanList;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserPost4preEligibilitySuccess userPost4preEligibilitySuccess = (UserPost4preEligibilitySuccess) o;
    return (this.eligibleBanList == null ? userPost4preEligibilitySuccess.eligibleBanList == null : this.eligibleBanList.equals(userPost4preEligibilitySuccess.eligibleBanList));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.eligibleBanList == null ? 0: this.eligibleBanList.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserPost4preEligibilitySuccess {\n");
    
    sb.append("  eligibleBanList: ").append(eligibleBanList).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
