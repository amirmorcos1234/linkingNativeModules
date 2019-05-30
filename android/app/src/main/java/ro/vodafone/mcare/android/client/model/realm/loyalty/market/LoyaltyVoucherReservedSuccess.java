/**
 * API-69 Loyalty Voucher
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 0.1
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.realm.loyalty.market;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import ro.vodafone.mcare.android.utils.RealmManager;

/**
 * Object containing data associated with successful transaction
 **/
@ApiModel(description = "Object containing data associated with successful transaction")
public class LoyaltyVoucherReservedSuccess extends RealmObject {

  @PrimaryKey
  private long id;
  @SerializedName("sysdate")
  private Long sysdate = null;
  @SerializedName("promotions")
  private RealmList<ReservedPromotion> promotions = null;

  public LoyaltyVoucherReservedSuccess() {
    this.id = 1;
  }

  /**
   * Reserved promotion list returned by ATG
   **/
  @ApiModelProperty(value = "Reserved promotion list returned by ATG")
  public RealmList<ReservedPromotion> getPromotions() {
    return promotions;
  }
  public void setPromotions(RealmList<ReservedPromotion> promotions) {
    this.promotions = promotions;
  }
  public static boolean haveValidData(){
    try{
      List<ReservedPromotion> promotions = ((LoyaltyVoucherReservedSuccess)RealmManager.getRealmObject(LoyaltyVoucherReservedSuccess.class)).getPromotions();
      if((promotions!=null &&  !promotions.isEmpty())){
        return true;
      }
    }catch (Exception e){
      e.printStackTrace();
    }
    return false;
  }

  public Long getSysdate() {
    return sysdate;
  }

  public void setSysdate(Long sysdate) {
    this.sysdate = sysdate;
  }
}
