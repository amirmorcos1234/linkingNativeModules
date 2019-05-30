package ro.vodafone.mcare.android.client.model.offers.activeOffersEbu;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * object containing data about postpaid offer
 */
@ApiModel(description = "object containing data about postpaid offer")
public class ActiveOffersEbu   {
  @SerializedName("promoList")
  private List<Promo> promoList = null;

  @SerializedName("boList")
  private List<BillingOffer> boList = null;

  public ActiveOffersEbu promoList(List<Promo> promoList) {
    this.promoList = promoList;
    return this;
  }

  public ActiveOffersEbu addPromoListItem(Promo promoListItem) {
    if (this.promoList == null) {
      this.promoList = new ArrayList<Promo>();
    }
    this.promoList.add(promoListItem);
    return this;
  }

   /**
   * promotions list
   * @return promoList
  **/
  @ApiModelProperty(value = "promotions list")
  public List<Promo> getPromoList() {
    return promoList;
  }

  public void setPromoList(List<Promo> promoList) {
    this.promoList = promoList;
  }

  public ActiveOffersEbu boList(List<BillingOffer> boList) {
    this.boList = boList;
    return this;
  }

  public ActiveOffersEbu addBoListItem(BillingOffer boListItem) {
    if (this.boList == null) {
      this.boList = new ArrayList<BillingOffer>();
    }
    this.boList.add(boListItem);
    return this;
  }

   /**
   * BO list
   * @return boList
  **/
  @ApiModelProperty(value = "BO list")

  public List<BillingOffer> getBoList() {
    return boList;
  }

  public void setBoList(List<BillingOffer> boList) {
    this.boList = boList;
  }

}

