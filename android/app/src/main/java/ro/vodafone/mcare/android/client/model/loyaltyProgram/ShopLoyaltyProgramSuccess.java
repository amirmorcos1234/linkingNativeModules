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

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * object containing data associated with successful loyalty program
 **/
@ApiModel(description = "object containing data associated with successful loyalty program")
public class ShopLoyaltyProgramSuccess extends RealmObject {

    @PrimaryKey
    private long id;

    public ShopLoyaltyProgramSuccess() {
        id = 1;
    }

  @SerializedName("LPSMessageList")
  private RealmList<LPSMessage> lPSMessageList = null;
  @SerializedName("lpsSegment")
  private String lpsSegment = null;
  @SerializedName("allowedToUnsubscribe")
  private Boolean allowedToUnsubscribe = null;
  @SerializedName("state")
  private String state = null;
  @SerializedName("lastUpdate")
  private String lastUpdate = null;
  @SerializedName("accountBalance")
  private String accountBalance = null;
  @SerializedName("selectedBan")
  private String selectedBan = null;

  /**
   * list of Shop Amount from Loyalty
   **/
  @ApiModelProperty(value = "list of Shop Amount from Loyalty")
  public RealmList<LPSMessage> getLPSMessageList() {
    return lPSMessageList;
  }
  public void setLPSMessageList(RealmList<LPSMessage> lPSMessageList) {
    this.lPSMessageList = lPSMessageList;
  }

  /**
   * Loyalty Program Segment
   **/
  @ApiModelProperty(value = "Loyalty Program Segment")
  public String getLpsSegment() {
    return lpsSegment;
  }
  public void setLpsSegment(String lpsSegment) {
    this.lpsSegment = lpsSegment;
  }

  /**
   * Allowed To Unsubscribe Loyalty Program
   **/
  @ApiModelProperty(value = "Allowed To Unsubscribe Loyalty Program")
  public Boolean getAllowedToUnsubscribe() {
    return allowedToUnsubscribe;
  }
  public void setAllowedToUnsubscribe(Boolean allowedToUnsubscribe) {
    this.allowedToUnsubscribe = allowedToUnsubscribe;
  }

  /**
   * State of Loyalty Program
   **/
  @ApiModelProperty(value = "State of Loyalty Program")
  public String getState() {
    return state;
  }
  public void setState(String state) {
    this.state = state;
  }

  /**
   * Last Update of Loyalty Program
   **/
  @ApiModelProperty(value = "Last Update of Loyalty Program")
  public String getLastUpdate() {
    return lastUpdate;
  }
  public void setLastUpdate(String lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  /**
   * Shop Account Balance
   **/
  @ApiModelProperty(value = "Shop Account Balance")
  public String getAccountBalance() {
    return accountBalance;
  }
  public void setAccountBalance(String accountBalance) {
    this.accountBalance = accountBalance;
  }

  /**
   * Shop Selected Ban
   **/
  @ApiModelProperty(value = "Shop Selected Ban")
  public String getSelectedBan() {
    return selectedBan;
  }
  public void setSelectedBan(String selectedBan) {
    this.selectedBan = selectedBan;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ShopLoyaltyProgramSuccess shopLoyaltyProgramSuccess = (ShopLoyaltyProgramSuccess) o;
    return (this.lPSMessageList == null ? shopLoyaltyProgramSuccess.lPSMessageList == null : this.lPSMessageList.equals(shopLoyaltyProgramSuccess.lPSMessageList)) &&
        (this.lpsSegment == null ? shopLoyaltyProgramSuccess.lpsSegment == null : this.lpsSegment.equals(shopLoyaltyProgramSuccess.lpsSegment)) &&
        (this.allowedToUnsubscribe == null ? shopLoyaltyProgramSuccess.allowedToUnsubscribe == null : this.allowedToUnsubscribe.equals(shopLoyaltyProgramSuccess.allowedToUnsubscribe)) &&
        (this.state == null ? shopLoyaltyProgramSuccess.state == null : this.state.equals(shopLoyaltyProgramSuccess.state)) &&
        (this.lastUpdate == null ? shopLoyaltyProgramSuccess.lastUpdate == null : this.lastUpdate.equals(shopLoyaltyProgramSuccess.lastUpdate)) &&
        (this.accountBalance == null ? shopLoyaltyProgramSuccess.accountBalance == null : this.accountBalance.equals(shopLoyaltyProgramSuccess.accountBalance)) &&
        (this.selectedBan == null ? shopLoyaltyProgramSuccess.selectedBan == null : this.selectedBan.equals(shopLoyaltyProgramSuccess.selectedBan));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.lPSMessageList == null ? 0: this.lPSMessageList.hashCode());
    result = 31 * result + (this.lpsSegment == null ? 0: this.lpsSegment.hashCode());
    result = 31 * result + (this.allowedToUnsubscribe == null ? 0: this.allowedToUnsubscribe.hashCode());
    result = 31 * result + (this.state == null ? 0: this.state.hashCode());
    result = 31 * result + (this.lastUpdate == null ? 0: this.lastUpdate.hashCode());
    result = 31 * result + (this.accountBalance == null ? 0: this.accountBalance.hashCode());
    result = 31 * result + (this.selectedBan == null ? 0: this.selectedBan.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class ShopLoyaltyProgramSuccess {\n");
    
    sb.append("  lPSMessageList: ").append(lPSMessageList).append("\n");
    sb.append("  lpsSegment: ").append(lpsSegment).append("\n");
    sb.append("  allowedToUnsubscribe: ").append(allowedToUnsubscribe).append("\n");
    sb.append("  state: ").append(state).append("\n");
    sb.append("  lastUpdate: ").append(lastUpdate).append("\n");
    sb.append("  accountBalance: ").append(accountBalance).append("\n");
    sb.append("  selectedBan: ").append(selectedBan).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
