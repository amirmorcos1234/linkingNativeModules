/**
 * Phoenix Integration Devices API
 * Phoenix Integration Devices API (new mCare)
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.vodafoneTv;

import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;

@ApiModel(description = "")
public class RenameRequest {
  
  @SerializedName("externalId")
  private String externalId = null;
  @SerializedName("udid")
  private String udid = null;
  @SerializedName("alias")
  private String alias = null;

  /**
   * external Id
   **/
  @ApiModelProperty(value = "external Id")
  public String getExternalId() {
    return externalId;
  }
  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  /**
   * unique device ID
   **/
  @ApiModelProperty(value = "unique device ID")
  public String getUdid() {
    return udid;
  }
  public void setUdid(String udid) {
    this.udid = udid;
  }

  /**
   * device alias
   **/
  @ApiModelProperty(value = "device alias")
  public String getAlias() {
    return alias;
  }
  public void setAlias(String alias) {
    this.alias = alias;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RenameRequest renameRequest = (RenameRequest) o;
    return (this.externalId == null ? renameRequest.externalId == null : this.externalId.equals(renameRequest.externalId)) &&
        (this.udid == null ? renameRequest.udid == null : this.udid.equals(renameRequest.udid)) &&
        (this.alias == null ? renameRequest.alias == null : this.alias.equals(renameRequest.alias));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.externalId == null ? 0: this.externalId.hashCode());
    result = 31 * result + (this.udid == null ? 0: this.udid.hashCode());
    result = 31 * result + (this.alias == null ? 0: this.alias.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class RenameRequest {\n");
    
    sb.append("  externalId: ").append(externalId).append("\n");
    sb.append("  udid: ").append(udid).append("\n");
    sb.append("  alias: ").append(alias).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}