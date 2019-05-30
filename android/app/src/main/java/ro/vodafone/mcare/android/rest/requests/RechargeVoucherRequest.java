/**
 * API-34 Recharge Voucher
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 5.0.2
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Request object with data required for recharge with voucher.
 **/
@ApiModel(description = "Request object with data required for recharge with voucher.")
public class RechargeVoucherRequest extends BaseRequest{
  
  @SerializedName("prepaidMsisdn")
  private String prepaidMsisdn = null;
  @SerializedName("voucherCode")
  private String voucherCode = null;

  /**
   * prepaid msisdn to be recharged with voucher
   **/
  @ApiModelProperty(required = true, value = "prepaid msisdn to be recharged with voucher")
  public String getPrepaidMsisdn() {
    return prepaidMsisdn;
  }
  public void setPrepaidMsisdn(String prepaidMsisdn) {
    this.prepaidMsisdn = prepaidMsisdn;
  }

  /**
   * voucher code to be used for recharge payment
   **/
  @ApiModelProperty(required = true, value = "voucher code to be used for recharge payment")
  public String getVoucherCode() {
    return voucherCode;
  }
  public void setVoucherCode(String voucherCode) {
    this.voucherCode = voucherCode;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RechargeVoucherRequest rechargeVoucherRequest = (RechargeVoucherRequest) o;
    return (this.prepaidMsisdn == null ? rechargeVoucherRequest.prepaidMsisdn == null : this.prepaidMsisdn.equals(rechargeVoucherRequest.prepaidMsisdn)) &&
        (this.voucherCode == null ? rechargeVoucherRequest.voucherCode == null : this.voucherCode.equals(rechargeVoucherRequest.voucherCode));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.prepaidMsisdn == null ? 0: this.prepaidMsisdn.hashCode());
    result = 31 * result + (this.voucherCode == null ? 0: this.voucherCode.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class RechargeVoucherRequest {\n");
    
    sb.append("  prepaidMsisdn: ").append(prepaidMsisdn).append("\n");
    sb.append("  voucherCode: ").append(voucherCode).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}