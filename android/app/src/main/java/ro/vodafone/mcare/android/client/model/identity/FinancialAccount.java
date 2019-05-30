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

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public class FinancialAccount extends RealmObject implements EntityInterface {
  
  @SerializedName("displayName")
  private String displayName = null;
  @PrimaryKey
  @SerializedName("entityId")
  private String entityId = null;
  @SerializedName("crmRole")
  private String crmRole = null;
  @SerializedName("vfOdsBan")
  private String vfOdsBan = null;
  @SerializedName("vfOdsBen")
  private String vfOdsBen = null;
  @SerializedName("cuiName")
  private String cuiName = null;

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
   * BAN for current entity
   **/
  @ApiModelProperty(required = true, value = "BAN for current entity")
  public String getVfOdsBan() {
    return vfOdsBan;
  }
  public void setVfOdsBan(String vfOdsBan) {
    this.vfOdsBan = vfOdsBan;
  }

  /**
   * BEN for current entity
   **/
  @ApiModelProperty(required = true, value = "BEN for current entity")
  public String getVfOdsBen() {
    return vfOdsBen;
  }
  public void setVfOdsBen(String vfOdsBen) {
    this.vfOdsBen = vfOdsBen;
  }

  /**
   * Name belonging to parent AccountCui entity
   **/
  @ApiModelProperty(value = "Name belonging to parent AccountCui entity")
  public String getCuiName() {
    return cuiName;
  }
  public void setCuiName(String cuiName) {
    this.cuiName = cuiName;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FinancialAccount financialAccount = (FinancialAccount) o;
    return (this.displayName == null ? financialAccount.displayName == null : this.displayName.equals(financialAccount.displayName)) &&
        (this.entityId == null ? financialAccount.entityId == null : this.entityId.equals(financialAccount.entityId)) &&
        (this.crmRole == null ? financialAccount.crmRole == null : this.crmRole.equals(financialAccount.crmRole)) &&
        (this.vfOdsBan == null ? financialAccount.vfOdsBan == null : this.vfOdsBan.equals(financialAccount.vfOdsBan)) &&
        (this.vfOdsBen == null ? financialAccount.vfOdsBen == null : this.vfOdsBen.equals(financialAccount.vfOdsBen)) &&
        (this.cuiName == null ? financialAccount.cuiName == null : this.cuiName.equals(financialAccount.cuiName));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.displayName == null ? 0: this.displayName.hashCode());
    result = 31 * result + (this.entityId == null ? 0: this.entityId.hashCode());
    result = 31 * result + (this.crmRole == null ? 0: this.crmRole.hashCode());
    result = 31 * result + (this.vfOdsBan == null ? 0: this.vfOdsBan.hashCode());
    result = 31 * result + (this.vfOdsBen == null ? 0: this.vfOdsBen.hashCode());
    result = 31 * result + (this.cuiName == null ? 0: this.cuiName.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class FinancialAccount {\n");
    sb.append("  " + super.toString()).append("\n");
    sb.append("  displayName: ").append(displayName).append("\n");
    sb.append("  entityId: ").append(entityId).append("\n");
    sb.append("  crmRole: ").append(crmRole).append("\n");
    sb.append("  vfOdsBan: ").append(vfOdsBan).append("\n");
    sb.append("  vfOdsBen: ").append(vfOdsBen).append("\n");
    sb.append("  cuiName: ").append(cuiName).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
