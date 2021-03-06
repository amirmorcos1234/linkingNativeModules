/**
 * API-63 Identity Selector
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 9.0.4
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.identity;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public class AccountHolding extends RealmObject implements EntityInterface {
  
  @SerializedName("displayName")
  private String displayName = null;
  @PrimaryKey
  @SerializedName("entityId")
  private String entityId = null;
  @SerializedName("crmRole")
  private String crmRole = null;
  @SerializedName("childList")
  private RealmList<AccountCui> childList = null;

  /**
   * name which will be displayed to user to be selected as entity
   **/
  @ApiModelProperty(required = true, value = "name which will be displayed to user to be selected as entity")
  public String getDisplayName() {
    return displayName;
  }
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  /**
   * unique ID to identify an entity
   **/
  @ApiModelProperty(required = true, value = "unique ID to identify an entity")
  public String getEntityId() {
    return entityId;
  }
  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }

  /**
   * CRM Role for definied entity
   **/
  @ApiModelProperty(required = true, value = "CRM Role for definied entity")
  public String getCrmRole() {
    return crmRole;
  }
  public void setCrmRole(String crmRole) {
    this.crmRole = crmRole;
  }

  /**
   * accountCUI list under current Holding
   **/
  @ApiModelProperty(required = true, value = "accountCUI list under current Holding")
  public RealmList<AccountCui> getChildList() {
    return childList;
  }
  public void setChildList(RealmList<AccountCui> childList) {
    this.childList = childList;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccountHolding accountHolding = (AccountHolding) o;
    return (this.displayName == null ? accountHolding.displayName == null : this.displayName.equals(accountHolding.displayName)) &&
        (this.entityId == null ? accountHolding.entityId == null : this.entityId.equals(accountHolding.entityId)) &&
        (this.crmRole == null ? accountHolding.crmRole == null : this.crmRole.equals(accountHolding.crmRole)) &&
        (this.childList == null ? accountHolding.childList == null : this.childList.equals(accountHolding.childList));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.displayName == null ? 0: this.displayName.hashCode());
    result = 31 * result + (this.entityId == null ? 0: this.entityId.hashCode());
    result = 31 * result + (this.crmRole == null ? 0: this.crmRole.hashCode());
    result = 31 * result + (this.childList == null ? 0: this.childList.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class AccountHolding {\n");
    sb.append("  " + super.toString()).append("\n");
    sb.append("  displayName: ").append(displayName).append("\n");
    sb.append("  entityId: ").append(entityId).append("\n");
    sb.append("  crmRole: ").append(crmRole).append("\n");
    sb.append("  childList: ").append(childList).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
