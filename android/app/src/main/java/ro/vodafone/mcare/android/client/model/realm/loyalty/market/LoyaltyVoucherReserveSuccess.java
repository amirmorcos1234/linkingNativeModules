/**
 * API-69 Loyalty Voucher
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 1.1
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.realm.loyalty.market;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public class LoyaltyVoucherReserveSuccess extends RealmObject {

  @PrimaryKey
  private int id;

  @SerializedName("voucherCode")
  private String voucherCode = null;

  public LoyaltyVoucherReserveSuccess() {
    id = 1;
  }

  /**
   * reserved voucherCode
   **/
  @ApiModelProperty(value = "reserved voucherCode")
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
    LoyaltyVoucherReserveSuccess loyaltyVoucherReserveSuccess = (LoyaltyVoucherReserveSuccess) o;
    return (this.voucherCode == null ? loyaltyVoucherReserveSuccess.voucherCode == null : this.voucherCode.equals(loyaltyVoucherReserveSuccess.voucherCode));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.voucherCode == null ? 0: this.voucherCode.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class LoyaltyVoucherReserveSuccess {\n");
    sb.append("  " + super.toString()).append("\n");
    sb.append("  voucherCode: ").append(voucherCode).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
