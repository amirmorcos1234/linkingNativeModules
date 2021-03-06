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
public class BillingCustomer extends RealmObject implements EntityInterface {
  
  @SerializedName("displayName")
  private String displayName = null;
  @PrimaryKey
  @SerializedName("entityId")
  private String entityId = null;
  @SerializedName("crmRole")
  private String crmRole = null;
  @SerializedName("vfOdsCui")
  private String vfOdsCui = null;
  @SerializedName("vfOdsSsn")
  private String vfOdsSsn = null;
  @SerializedName("vfOdsSsnType")
  private String vfOdsSsnType = null;
  @SerializedName("vfOdsCid")
  private String vfOdsCid = null;
  @SerializedName("treatmentSegment")
  private String treatmentSegment = null;
  @SerializedName("microSegment")
  private String microSegment = null;
  @SerializedName("cuiName")
  private String cuiName = null;
  @SerializedName("childList")
  private RealmList<FinancialAccount> childList = null;

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
   * CUI belonging to parent AccountCui entity
   **/
  @ApiModelProperty(required = true, value = "CUI belonging to parent AccountCui entity")
  public String getVfOdsCui() {
    return vfOdsCui;
  }
  public void setVfOdsCui(String vfOdsCui) {
    this.vfOdsCui = vfOdsCui;
  }

  /**
   * SSN belonging to parent AccountCui entity
   **/
  @ApiModelProperty(required = true, value = "SSN belonging to parent AccountCui entity")
  public String getVfOdsSsn() {
    return vfOdsSsn;
  }
  public void setVfOdsSsn(String vfOdsSsn) {
    this.vfOdsSsn = vfOdsSsn;
  }

  /**
   * Type of social security number belonging to current entity
   **/
  @ApiModelProperty(value = "Type of social security number belonging to current entity")
  public String getVfOdsSsnType() {
    return vfOdsSsnType;
  }
  public void setVfOdsSsnType(String vfOdsSsnType) {
    this.vfOdsSsnType = vfOdsSsnType;
  }

  /**
   * CID representing current entity
   **/
  @ApiModelProperty(required = true, value = "CID representing current entity")
  public String getVfOdsCid() {
    return vfOdsCid;
  }
  public void setVfOdsCid(String vfOdsCid) {
    this.vfOdsCid = vfOdsCid;
  }

  /**
   * Treatment segment
   **/
  @ApiModelProperty(required = true, value = "Treatment segment")
  public String getTreatmentSegment() {
    return treatmentSegment;
  }
  public void setTreatmentSegment(String treatmentSegment) {
    this.treatmentSegment = treatmentSegment;
  }

  /**
   * Micro segment
   **/
  @ApiModelProperty(value = "Micro segment")
  public String getMicroSegment() {
    return microSegment;
  }
  public void setMicroSegment(String microSegment) {
    this.microSegment = microSegment;
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

  /**
   * financialAccount list under current Customer
   **/
  @ApiModelProperty(required = true, value = "financialAccount list under current Customer")
  public RealmList<FinancialAccount> getChildList() {
    return childList;
  }
  public void setChildList(RealmList<FinancialAccount> childList) {
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
    BillingCustomer billingCustomer = (BillingCustomer) o;
    return (this.displayName == null ? billingCustomer.displayName == null : this.displayName.equals(billingCustomer.displayName)) &&
        (this.entityId == null ? billingCustomer.entityId == null : this.entityId.equals(billingCustomer.entityId)) &&
        (this.crmRole == null ? billingCustomer.crmRole == null : this.crmRole.equals(billingCustomer.crmRole)) &&
        (this.vfOdsCui == null ? billingCustomer.vfOdsCui == null : this.vfOdsCui.equals(billingCustomer.vfOdsCui)) &&
        (this.vfOdsSsn == null ? billingCustomer.vfOdsSsn == null : this.vfOdsSsn.equals(billingCustomer.vfOdsSsn)) &&
        (this.vfOdsSsnType == null ? billingCustomer.vfOdsSsnType == null : this.vfOdsSsnType.equals(billingCustomer.vfOdsSsnType)) &&
        (this.vfOdsCid == null ? billingCustomer.vfOdsCid == null : this.vfOdsCid.equals(billingCustomer.vfOdsCid)) &&
        (this.treatmentSegment == null ? billingCustomer.treatmentSegment == null : this.treatmentSegment.equals(billingCustomer.treatmentSegment)) &&
        (this.microSegment == null ? billingCustomer.microSegment == null : this.microSegment.equals(billingCustomer.microSegment)) &&
        (this.cuiName == null ? billingCustomer.cuiName == null : this.cuiName.equals(billingCustomer.cuiName)) &&
        (this.childList == null ? billingCustomer.childList == null : this.childList.equals(billingCustomer.childList));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.displayName == null ? 0: this.displayName.hashCode());
    result = 31 * result + (this.entityId == null ? 0: this.entityId.hashCode());
    result = 31 * result + (this.crmRole == null ? 0: this.crmRole.hashCode());
    result = 31 * result + (this.vfOdsCui == null ? 0: this.vfOdsCui.hashCode());
    result = 31 * result + (this.vfOdsSsn == null ? 0: this.vfOdsSsn.hashCode());
    result = 31 * result + (this.vfOdsSsnType == null ? 0: this.vfOdsSsnType.hashCode());
    result = 31 * result + (this.vfOdsCid == null ? 0: this.vfOdsCid.hashCode());
    result = 31 * result + (this.treatmentSegment == null ? 0: this.treatmentSegment.hashCode());
    result = 31 * result + (this.microSegment == null ? 0: this.microSegment.hashCode());
    result = 31 * result + (this.cuiName == null ? 0: this.cuiName.hashCode());
    result = 31 * result + (this.childList == null ? 0: this.childList.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class BillingCustomer {\n");
    sb.append("  " + super.toString()).append("\n");
    sb.append("  displayName: ").append(displayName).append("\n");
    sb.append("  entityId: ").append(entityId).append("\n");
    sb.append("  crmRole: ").append(crmRole).append("\n");
    sb.append("  vfOdsCui: ").append(vfOdsCui).append("\n");
    sb.append("  vfOdsSsn: ").append(vfOdsSsn).append("\n");
    sb.append("  vfOdsSsnType: ").append(vfOdsSsnType).append("\n");
    sb.append("  vfOdsCid: ").append(vfOdsCid).append("\n");
    sb.append("  treatmentSegment: ").append(treatmentSegment).append("\n");
    sb.append("  microSegment: ").append(microSegment).append("\n");
    sb.append("  cuiName: ").append(cuiName).append("\n");
    sb.append("  childList: ").append(childList).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
