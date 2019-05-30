/**
 * API-20 Cost Control
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 3.1.8
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.costControl;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * contain information about active extraoptions, depending on current subscription
 **/
@ApiModel(description = "contain information about active extraoptions, depending on current subscription")
public class Extraoption extends RealmObject {
  
  @SerializedName("extendedBalanceList")
  private RealmList<BalanceShowAndNotShown> extendedBalanceList = null;
  @SerializedName("shortBalanceList")
  private RealmList<Balance> shortBalanceList = new RealmList<>();
  @SerializedName("extraoptionAdditionalInfo")
  private String extraoptionAdditionalInfo = "";

  @PrimaryKey
  private long id;
  public Extraoption() {
    id =1;
  }

  /**
   * aggregated balance list for current extraoptions
   **/
  @ApiModelProperty(required = true, value = "aggregated balance list for current extraoptions")
  public RealmList<BalanceShowAndNotShown> getExtendedBalanceList() {
    return extendedBalanceList;
  }
  public void setExtendedBalanceList(RealmList<BalanceShowAndNotShown> extendedBalanceList) {
    this.extendedBalanceList = extendedBalanceList;
  }

  /**
   * aggregated balance list for current extraoptions, grouped for displayed
   **/
  @ApiModelProperty(value = "aggregated balance list for current extraoptions, grouped for displayed")
  public RealmList<Balance> getShortBalanceList() {
    return shortBalanceList;
  }
  public void setShortBalanceList(RealmList<Balance> shortBalanceList) {
    this.shortBalanceList = shortBalanceList!=null?shortBalanceList:new RealmList<Balance>();
  }

  /**
   * additional information associated with current extraoption
   **/
  @ApiModelProperty(value = "additional information associated with current extraoption")
  public String getExtraoptionAdditionalInfo() {
    return extraoptionAdditionalInfo;
  }
  public void setExtraoptionAdditionalInfo(String extraoptionAdditionalInfo) {
    this.extraoptionAdditionalInfo = extraoptionAdditionalInfo;
  }


/*  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.extendedBalanceList == null ? 0: this.extendedBalanceList.hashCode());
    result = 31 * result + (this.shortBalanceList == null ? 0: this.shortBalanceList.hashCode());
    result = 31 * result + (this.extraoptionAdditionalInfo == null ? 0: this.extraoptionAdditionalInfo.hashCode());
    return result;
  }*/

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Extraoption {\n");
    
    sb.append("  extendedBalanceList: ").append(extendedBalanceList).append("\n");
    sb.append("  shortBalanceList: ").append(shortBalanceList).append("\n");
    sb.append("  extraoptionAdditionalInfo: ").append(extraoptionAdditionalInfo).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
