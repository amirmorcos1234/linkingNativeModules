/**
 * API-35  Active offers prepaid
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 6.0.8
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
package ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Contains additional info about child offers of active offer
 **/
@ApiModel(description = "Contains additional info about child offers of active offer")
public class BalanceInfo extends RealmObject{

  @SerializedName("balanceId")
  private Long balanceId = null;
  @SerializedName("balanceDescription")
  private String balanceDescription = null;
  public enum BalanceTypeEnum {
    data,  voice,  sms,  vas,  cvt,
  };
  @Ignore
  private transient BalanceTypeEnum balanceType = null;

  @SerializedName("balanceType")
  private String balanceTypeString;

  public enum BalanceUnitEnum {
    num,  sec,  min,  kb,  mb,  gb,  unl,
  };
  @Ignore
  private transient BalanceUnitEnum balanceUnit = null;

  @SerializedName("balanceUnit")
  private String balanceUnitString;

  @SerializedName("balanceRemaining")
  private Double balanceRemaining = null;
  @SerializedName("balanceTotal")
  private Double balanceTotal = null;
  @SerializedName("additionalInfo")
  private String additionalInfo = null;

  /**
   * child offer id
   **/
  @ApiModelProperty(required = true, value = "child offer id")
  public Long getBalanceId() {
    return balanceId;
  }
  public void setBalanceId(Long balanceId) {
    this.balanceId = balanceId;
  }

  /**
   * description for current balance
   **/
  @ApiModelProperty(required = true, value = "description for current balance")
  public String getBalanceDescription() {
    return balanceDescription;
  }
  public void setBalanceDescription(String balanceDescription) {
    this.balanceDescription = balanceDescription;
  }

  /**
   * mobile data - \"data\" (prepaid and postpaid), minutes - \"voice\" (prepaid and postpaid), messages - \"sms\" (postpaid), minutes and messages - \"vas\" (prepaid), ce vrei tu - \"cvt\" (prepaid), data chunks - \"chunk\"
   **/
  @ApiModelProperty(value = "mobile data - \"data\" (prepaid and postpaid), minutes - \"voice\" (prepaid and postpaid), messages - \"sms\" (postpaid), minutes and messages - \"vas\" (prepaid), ce vrei tu - \"cvt\" (prepaid), data chunks - \"chunk\"")
  public BalanceTypeEnum getBalanceType() {
            if(balanceType!=null){
                return balanceType;
            }
          return (balanceTypeString != null) ? BalanceTypeEnum.valueOf(balanceTypeString) : null;
  }
  public void setBalanceType(BalanceTypeEnum balanceType) {
    this.balanceType = balanceType;
  }

  /**
   * avaiable values \"num\" - numeric, \"sec\" - seconds, \"min\" - minutes, \"kb\" - kilobytes, \"mb\" - megabytes, \"gb\" - gigabytes, \"unl\" - unlimited
   **/
  @ApiModelProperty(value = "avaiable values \"num\" - numeric, \"sec\" - seconds, \"min\" - minutes, \"kb\" - kilobytes, \"mb\" - megabytes, \"gb\" - gigabytes, \"unl\" - unlimited")
  public BalanceUnitEnum getBalanceUnit() {
      if(balanceUnit!=null){
          return balanceUnit;
      }
      return (balanceUnitString != null) ? BalanceUnitEnum.valueOf(balanceUnitString) : null;
  }
  public void setBalanceUnit(BalanceUnitEnum balanceUnit) {
    this.balanceUnit = balanceUnit;
  }

  /**
   * remaning amount
   **/
  @ApiModelProperty(required = true, value = "remaning amount")
  public Double getBalanceRemaining() {
    return balanceRemaining;
  }
  public void setBalanceRemaining(Double balanceRemaining) {
    this.balanceRemaining = balanceRemaining;
  }

  /**
   * total amount
   **/
  @ApiModelProperty(required = true, value = "total amount")
  public Double getBalanceTotal() {
    return balanceTotal;
  }
  public void setBalanceTotal(Double balanceTotal) {
    this.balanceTotal = balanceTotal;
  }

  /**
   * child offer description from Offer Catalog
   **/
  @ApiModelProperty(value = "child offer description from Offer Catalog")
  public String getAdditionalInfo() {
    return additionalInfo;
  }
  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

    public String getBalanceTypeString() {
        return balanceTypeString;
    }

    public void setBalanceTypeString(String balanceTypeString) {
        this.balanceTypeString = balanceTypeString;
    }

    public String getBalanceUnitString() {
        return balanceUnitString;
    }

    public void setBalanceUnitString(String balanceUnitString) {
        this.balanceUnitString = balanceUnitString;
    }
}
