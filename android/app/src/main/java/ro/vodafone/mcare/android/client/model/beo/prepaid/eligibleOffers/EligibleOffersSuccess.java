/**
 * API-37  Eligible offers prepaid
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 6.0.4
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * object containing data associated with successful transaction
 **/
@ApiModel(description = "object containing data associated with successful transaction")
public class EligibleOffersSuccess extends RealmObject  {

  @PrimaryKey
  private long id;

  @SerializedName("eligibleOptionsCategories")
  private RealmList<EligibleCategories> eligibleOptionsCategories = null;
  @SerializedName("eligibleServicesCategories")
  private RealmList<EligibleCategories> eligibleServicesCategories = null;
  @SerializedName("bannerOffers")
  private RealmList<BannerOfferPrepaid> bannerOffers = null;

  public EligibleOffersSuccess() {
    id=1;
  }

  /**
   * list with eligible bonuses and options grouped by their respective category. The categories and the order in which they will be displayed are configurable.
   **/
  @ApiModelProperty(value = "list with eligible bonuses and options grouped by their respective category. The categories and the order in which they will be displayed are configurable.")
  public RealmList<EligibleCategories> getEligibleOptionsCategories() {
    return eligibleOptionsCategories;
  }
  public void setEligibleOptionsCategories(RealmList<EligibleCategories> eligibleOptionsCategories) {
    this.eligibleOptionsCategories = eligibleOptionsCategories;
  }

  /**
   * list with eligible services grouped by their respective category. The categories and the order in which they will be displayed are configurable.
   **/
  @ApiModelProperty(value = "list with eligible services grouped by their respective category. The categories and the order in which they will be displayed are configurable.")
  public RealmList<EligibleCategories> getEligibleServicesCategories() {
    return eligibleServicesCategories;
  }
  public void setEligibleServicesCategories(RealmList<EligibleCategories> eligibleServicesCategories) {
    this.eligibleServicesCategories = eligibleServicesCategories;
  }

  /**
   * Offers for display in banner
   **/
  @ApiModelProperty(value = "Offers for display in banner")
  public RealmList<BannerOfferPrepaid> getBannerOffers() {
    return bannerOffers;
  }
  public void setBannerOffers(RealmList<BannerOfferPrepaid> bannerOffers) {
    this.bannerOffers = bannerOffers;
  }


}
