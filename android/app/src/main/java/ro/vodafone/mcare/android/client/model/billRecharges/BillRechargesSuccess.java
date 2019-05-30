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

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * object containing data associated with successful transaction
 **/
@ApiModel(description = "object containing data associated with successful transaction")
public class BillRechargesSuccess extends RealmObject{

  @PrimaryKey
  private long id_bilRechargesSuccess;

  @SerializedName("oneTimeRecharges")
  private RealmList<OneTimeRecharge> oneTimeRecharges = null;
  @SerializedName("weeklyRecharges")
  private RealmList<WeeklyRecharge> weeklyRecharges = null;
  @SerializedName("monthlyRecharges")
  private RealmList<MonthlyRecharge> monthlyRecharges = null;

  public BillRechargesSuccess() {
    this.id_bilRechargesSuccess = 1;
  }

  public long getId_bilRechargesSuccess() {
    return id_bilRechargesSuccess;
  }

  /**
   * list with information about one time scheduled recharges
   **/
  @ApiModelProperty(value = "list with information about one time scheduled recharges")
  public List<OneTimeRecharge> getOneTimeRecharges() {
    return oneTimeRecharges;
  }
  public void setOneTimeRecharges(RealmList<OneTimeRecharge> oneTimeRecharges) {
    this.oneTimeRecharges = oneTimeRecharges;
  }

  /**
   * list with information about weekly scheduled recharges
   **/
  @ApiModelProperty(value = "list with information about weekly scheduled recharges")
  public List<WeeklyRecharge> getWeeklyRecharges() {
    return weeklyRecharges;
  }
  public void setWeeklyRecharges(RealmList<WeeklyRecharge> weeklyRecharges) {
    this.weeklyRecharges = weeklyRecharges;
  }

  /**
   * list with information about monthly scheduled recharges
   **/
  @ApiModelProperty(value = "list with information about monthly scheduled recharges")
  public List<MonthlyRecharge> getMonthlyRecharges() {
    return monthlyRecharges;
  }
  public void setMonthlyRecharges(RealmList<MonthlyRecharge> monthlyRecharges) {
    this.monthlyRecharges = monthlyRecharges;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BillRechargesSuccess billRechargesSuccess = (BillRechargesSuccess) o;
    return (this.oneTimeRecharges == null ? billRechargesSuccess.oneTimeRecharges == null : this.oneTimeRecharges.equals(billRechargesSuccess.oneTimeRecharges)) &&
        (this.weeklyRecharges == null ? billRechargesSuccess.weeklyRecharges == null : this.weeklyRecharges.equals(billRechargesSuccess.weeklyRecharges)) &&
        (this.monthlyRecharges == null ? billRechargesSuccess.monthlyRecharges == null : this.monthlyRecharges.equals(billRechargesSuccess.monthlyRecharges));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.oneTimeRecharges == null ? 0: this.oneTimeRecharges.hashCode());
    result = 31 * result + (this.weeklyRecharges == null ? 0: this.weeklyRecharges.hashCode());
    result = 31 * result + (this.monthlyRecharges == null ? 0: this.monthlyRecharges.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class BillRechargesSuccess {\n");
    
    sb.append("  oneTimeRecharges: ").append(oneTimeRecharges).append("\n");
    sb.append("  weeklyRecharges: ").append(weeklyRecharges).append("\n");
    sb.append("  monthlyRecharges: ").append(monthlyRecharges).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
