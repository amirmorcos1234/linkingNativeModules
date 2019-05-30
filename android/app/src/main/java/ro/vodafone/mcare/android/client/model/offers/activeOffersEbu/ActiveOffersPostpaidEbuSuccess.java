package ro.vodafone.mcare.android.client.model.offers.activeOffersEbu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * object containing data associated with successful transaction
 */
@ApiModel(description = "object containing data associated with successful transaction")

public class ActiveOffersPostpaidEbuSuccess implements Serializable{
  @SerializedName("activeOffers")
  private ActiveOffersEbu activeOffers = null;

  @SerializedName("activeServices")
  private ActiveOffersEbu activeServices = null;

  @SerializedName("pendingOffers")
  private ActiveOffersEbu pendingOffers = null;

  @SerializedName("pricePlan")
  private Promo pricePlan = null;

  @SerializedName("tariffsList")
  private List<Tarrif> tariffsList = null;

  @SerializedName("odsCallFailed")
  private Boolean odsCallFailed = null;

  public ActiveOffersPostpaidEbuSuccess activeOffers(ActiveOffersEbu activeOffers) {
    this.activeOffers = activeOffers;
    return this;
  }

   /**
   * Get activeOffers
   * @return activeOffers
  **/
  @ApiModelProperty(value = "")

  public ActiveOffersEbu getActiveOffers() {
    return activeOffers;
  }

  public void setActiveOffers(ActiveOffersEbu activeOffers) {
    this.activeOffers = activeOffers;
  }

  public ActiveOffersPostpaidEbuSuccess activeServices(ActiveOffersEbu activeServices) {
    this.activeServices = activeServices;
    return this;
  }

   /**
   * Get activeServices
   * @return activeServices
  **/
  @ApiModelProperty(value = "")

  public ActiveOffersEbu getActiveServices() {
    return activeServices;
  }

  public void setActiveServices(ActiveOffersEbu activeServices) {
    this.activeServices = activeServices;
  }

  public ActiveOffersPostpaidEbuSuccess pendingOffers(ActiveOffersEbu pendingOffers) {
    this.pendingOffers = pendingOffers;
    return this;
  }

   /**
   * Get pendingOffers
   * @return pendingOffers
  **/
  @ApiModelProperty(value = "")

  public ActiveOffersEbu getPendingOffers() {
    return pendingOffers;
  }

  public void setPendingOffers(ActiveOffersEbu pendingOffers) {
    this.pendingOffers = pendingOffers;
  }

  public ActiveOffersPostpaidEbuSuccess pricePlan(Promo pricePlan) {
    this.pricePlan = pricePlan;
    return this;
  }

   /**
   * Get pricePlan
   * @return pricePlan
  **/
  @ApiModelProperty(value = "")

  public Promo getPricePlan() {
    return pricePlan;
  }

  public void setPricePlan(Promo pricePlan) {
    this.pricePlan = pricePlan;
  }

  public ActiveOffersPostpaidEbuSuccess tariffsList(List<Tarrif> tariffsList) {
    this.tariffsList = tariffsList;
    return this;
  }

  public ActiveOffersPostpaidEbuSuccess addTariffsListItem(Tarrif tariffsListItem) {
    if (this.tariffsList == null) {
      this.tariffsList = new ArrayList<Tarrif>();
    }
    this.tariffsList.add(tariffsListItem);
    return this;
  }

   /**
   * list with active offers
   * @return tariffsList
  **/
  @ApiModelProperty(value = "list with active offers")

  public List<Tarrif> getTariffsList() {
    return tariffsList;
  }

  public void setTariffsList(List<Tarrif> tariffsList) {
    this.tariffsList = tariffsList;
  }


  public Boolean getOdsCallFailed() {
    return odsCallFailed;
  }

  public void setOdsCallFailed(Boolean odsCallFailed) {
    this.odsCallFailed = odsCallFailed;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ActiveOffersPostpaidEbuSuccess {\n");
    
    sb.append("    activeOffers: ").append(toIndentedString(activeOffers)).append("\n");
    sb.append("    activeServices: ").append(toIndentedString(activeServices)).append("\n");
    sb.append("    pendingOffers: ").append(toIndentedString(pendingOffers)).append("\n");
    sb.append("    pricePlan: ").append(toIndentedString(pricePlan)).append("\n");
    sb.append("    tariffsList: ").append(toIndentedString(tariffsList)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

