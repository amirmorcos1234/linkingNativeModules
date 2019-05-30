/**
 * API-32  Bill recharges
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 5.0.8
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.billRecharges;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * object containing data about current weekly schedule recharge
 **/
@ApiModel(description = "object containing data about current weekly schedule recharge")
public class WeeklyRecharge extends RealmObject{

  @PrimaryKey
  @SerializedName("scheduleId")
  private Integer scheduleId = null;
  @SerializedName("msisdn")
  private String msisdn = null;
  @SerializedName("scheduledAmount")
  private Float scheduledAmount = null;
  @SerializedName("weeklyCycle")
  private Integer weeklyCycle = null;

  public WeeklyRecharge() {
  }

  /**
   * phone number for current scheduled recharge
   **/
  @ApiModelProperty(required = true, value = "phone number for current scheduled recharge")
  public String getMsisdn() {
    return msisdn;
  }
  public void setMsisdn(String msisdn) {
    this.msisdn = msisdn;
  }

  /**
   * scheduled amount to be recharged
   **/
  @ApiModelProperty(required = true, value = "scheduled amount to be recharged")
  public Float getScheduledAmount() {
    return scheduledAmount;
  }
  public void setScheduledAmount(Float scheduledAmount) {
    this.scheduledAmount = scheduledAmount;
  }

  /**
   * schedule recharge id
   **/
  @ApiModelProperty(value = "schedule recharge id")
  public Integer getScheduleId() {
    return scheduleId;
  }
  public void setScheduleId(Integer scheduleId) {
    this.scheduleId = scheduleId;
  }

  /**
   * day in week representing current scheduled recharge. 0 - Monday, 1- Tuesday, 2 - Wednesday, 3 -Thursday, 4 - Friday, 5 - Saturday, 6 - Sunday.
   * minimum: 0
   * maximum: 6
   **/
  @ApiModelProperty(required = true, value = "day in week representing current scheduled recharge. 0 - Monday, 1- Tuesday, 2 - Wednesday, 3 -Thursday, 4 - Friday, 5 - Saturday, 6 - Sunday.")
  public Integer getWeeklyCycle() {
    return weeklyCycle;
  }
  public void setWeeklyCycle(Integer weeklyCycle) {
    this.weeklyCycle = weeklyCycle;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WeeklyRecharge weeklyRecharge = (WeeklyRecharge) o;
    return (this.msisdn == null ? weeklyRecharge.msisdn == null : this.msisdn.equals(weeklyRecharge.msisdn)) &&
        (this.scheduledAmount == null ? weeklyRecharge.scheduledAmount == null : this.scheduledAmount.equals(weeklyRecharge.scheduledAmount)) &&
        (this.scheduleId == null ? weeklyRecharge.scheduleId == null : this.scheduleId.equals(weeklyRecharge.scheduleId)) &&
        (this.weeklyCycle == null ? weeklyRecharge.weeklyCycle == null : this.weeklyCycle.equals(weeklyRecharge.weeklyCycle));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.msisdn == null ? 0: this.msisdn.hashCode());
    result = 31 * result + (this.scheduledAmount == null ? 0: this.scheduledAmount.hashCode());
    result = 31 * result + (this.scheduleId == null ? 0: this.scheduleId.hashCode());
    result = 31 * result + (this.weeklyCycle == null ? 0: this.weeklyCycle.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class WeeklyRecharge {\n");
    
    sb.append("  msisdn: ").append(msisdn).append("\n");
    sb.append("  scheduledAmount: ").append(scheduledAmount).append("\n");
    sb.append("  scheduleId: ").append(scheduleId).append("\n");
    sb.append("  weeklyCycle: ").append(weeklyCycle).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}