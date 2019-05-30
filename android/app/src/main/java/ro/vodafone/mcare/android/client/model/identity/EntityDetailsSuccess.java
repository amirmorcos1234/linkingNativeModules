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

/**
 * object containing data associated with successful response
 **/
@ApiModel(description = "object containing data associated with successful response")
public class EntityDetailsSuccess extends RealmObject {

  @PrimaryKey
  private long id;

  @SerializedName("dataSpecialist")
  private AccountSpecialist dataSpecialist = null;
  @SerializedName("voiceSpecialist")
  private AccountSpecialist voiceSpecialist = null;

  @SerializedName("billCycle")
  private Integer billCycle = null;

  public EntityDetailsSuccess() {
    this.id = 1;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public AccountSpecialist getDataSpecialist() {
    return dataSpecialist;
  }
  public void setDataSpecialist(AccountSpecialist dataSpecialist) {
    this.dataSpecialist = dataSpecialist;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public AccountSpecialist getVoiceSpecialist() {
    return voiceSpecialist;
  }
  public void setVoiceSpecialist(AccountSpecialist voiceSpecialist) {
    this.voiceSpecialist = voiceSpecialist;
  }

  /**
   * returns day of month when invoice is issued
   **/
  @ApiModelProperty(value = "returns day of month when invoice is issued")
  public Integer getBillCycle() {
    return billCycle;
  }
  public void setBillCycle(Integer billCycle) {
    this.billCycle = billCycle;
  }

}