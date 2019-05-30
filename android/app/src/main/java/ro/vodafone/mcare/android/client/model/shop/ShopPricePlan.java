/**
 * API-49 Shop PricePlans
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 7.0.1
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.shop;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * object containing data about ShopProduct
 **/
@ApiModel(description = "object containing data about ShopProduct")
public class ShopPricePlan extends RealmObject{

  public static final String PRICE_PLAN_MONTHLY_FEE = "pricePlanMothlyFee";
  public static final String PRICE_PLAN_CONTRACT_PERIOD = "pricePlanContractPeriod";

  @PrimaryKey
  @SerializedName("pricePlanSkuId")
  private String pricePlanSkuId = null;
  @SerializedName("phoneSkuId")
  private String phoneSkuId = null;
  @SerializedName("productId")
  private String productId = null;
  @SerializedName("cfgSkuId")
  private String cfgSkuId = null;
  @SerializedName("pricePlanDisplayName")
  private String pricePlanDisplayName = null;
  @SerializedName("pricePlanBenefits")
  private String pricePlanBenefits = null;
  @SerializedName("pricePlanDetailsHtml")
  private String pricePlanDetailsHtml = null;
  @SerializedName("pricePlanMothlyFee")
  private Float pricePlanMothlyFee = null;
  @SerializedName("pricePlanContractPeriod")
  private Integer pricePlanContractPeriod = null;
  @SerializedName("eligibleSimOnly")
  private Boolean eligibleSimOnly = false;
  @SerializedName("eligibleWithDevice")
  private Boolean eligibleWithDevice = false;
  @SerializedName("bundlePrice")
  private Float bundlePrice = null;
  @SerializedName("discountedPrice")
  private Float discountedPrice = null;
  @SerializedName("additionalBenefits")
  private RealmList<AdditionalBenefits> additionalBenefits = null;


  public RealmList<AdditionalBenefits> getAdditionalBenefits() {
    return additionalBenefits;
  }

  public void setAdditionalBenefits(RealmList<AdditionalBenefits> additionalBenefits) {
    this.additionalBenefits = additionalBenefits;
  }

  /**
   * unique identifier for phone product
   **/
  @ApiModelProperty(required = true, value = "unique identifier for phone product")
  public String getPhoneSkuId() {
    return phoneSkuId;
  }
  public void setPhoneSkuId(String phoneSkuId) {
    this.phoneSkuId = phoneSkuId;
  }

  /**
   * unique identifier for price plan
   **/
  @ApiModelProperty(required = true, value = "unique identifier for price plan")
  public String getPricePlanSkuId() {
    return pricePlanSkuId;
  }
  public void setPricePlanSkuId(String pricePlanSkuId) {
    this.pricePlanSkuId = pricePlanSkuId;
  }

  /**
   * unique identifier for phone product
   **/
  @ApiModelProperty(required = true, value = "unique identifier for phone product")
  public String getProductId() {
    return productId;
  }
  public void setProductId(String productId) {
    this.productId = productId;
  }

  /**
   * unique identifier for configuration
   **/
  @ApiModelProperty(value = "unique identifier for configuration")
  public String getCfgSkuId() {
    return cfgSkuId;
  }
  public void setCfgSkuId(String cfgSkuId) {
    this.cfgSkuId = cfgSkuId;
  }

  /**
   * price plan display name
   **/
  @ApiModelProperty(value = "price plan display name")
  public String getPricePlanDisplayName() {
    return pricePlanDisplayName;
  }
  public void setPricePlanDisplayName(String pricePlanDisplayName) {
    this.pricePlanDisplayName = pricePlanDisplayName;
  }

  /**
   * price plan benefits
   **/
  @ApiModelProperty(value = "price plan benefits")
  public String getPricePlanBenefits() {
    return pricePlanBenefits;
  }
  public void setPricePlanBenefits(String pricePlanBenefits) {
    this.pricePlanBenefits = pricePlanBenefits;
  }

  /**
   * encoded html content
   **/
  @ApiModelProperty(value = "encoded html content")
  public String getPricePlanDetailsHtml() {
    return pricePlanDetailsHtml;
  }
  public void setPricePlanDetailsHtml(String pricePlanDetailsHtml) {
    this.pricePlanDetailsHtml = pricePlanDetailsHtml;
  }

  /**
   * average spent per month
   **/
  @ApiModelProperty(value = "average spent per month")
  public Float getPricePlanMothlyFee() {
    return pricePlanMothlyFee;
  }
  public void setPricePlanMothlyFee(Float pricePlanMothlyFee) {
    this.pricePlanMothlyFee = pricePlanMothlyFee;
  }

  /**
   * contract period (months)
   **/
  @ApiModelProperty(value = "contract period (months)")
  public Integer getPricePlanContractPeriod() {
    return pricePlanContractPeriod;
  }
  public void setPricePlanContractPeriod(Integer pricePlanContractPeriod) {
    this.pricePlanContractPeriod = pricePlanContractPeriod;
  }

  /**
   * eligibleSimOnly flag
   **/
  @ApiModelProperty(value = "eligibleSimOnly flag")
  public Boolean getEligibleSimOnly() {
    return eligibleSimOnly;
  }
  public void setEligibleSimOnly(Boolean eligibleSimOnly) {
    this.eligibleSimOnly = eligibleSimOnly;
  }

  /**
   * eligibleWithDevice flag
   **/
  @ApiModelProperty(value = "eligibleWithDevice flag")
  public Boolean getEligibleWithDevice() {
    return eligibleWithDevice;
  }
  public void setEligibleWithDevice(Boolean eligibleWithDevice) {
    this.eligibleWithDevice = eligibleWithDevice;
  }

  /**
   * price for bundle
   **/
  @ApiModelProperty(value = "price for bundle")
  public Float getBundlePrice() {
    return bundlePrice;
  }
  public void setBundlePrice(Float bundlePrice) {
    this.bundlePrice = bundlePrice;
  }

  /**
   * price with discount
   **/
  @ApiModelProperty(value = "price with discount")
  public Float getDiscountedPrice() {
    return discountedPrice;
  }
  public void setDiscountedPrice(Float discountedPrice) {
    this.discountedPrice = discountedPrice;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class ShopPricePlan {\n");
    
    sb.append("  phoneSkuId: ").append(phoneSkuId).append("\n");
    sb.append("  pricePlanSkuId: ").append(pricePlanSkuId).append("\n");
    sb.append("  productId: ").append(productId).append("\n");
    sb.append("  cfgSkuId: ").append(cfgSkuId).append("\n");
    sb.append("  pricePlanDisplayName: ").append(pricePlanDisplayName).append("\n");
    sb.append("  pricePlanBenefits: ").append(pricePlanBenefits).append("\n");
    sb.append("  pricePlanDetailsHtml: ").append(pricePlanDetailsHtml).append("\n");
    sb.append("  pricePlanMothlyFee: ").append(pricePlanMothlyFee).append("\n");
    sb.append("  pricePlanContractPeriod: ").append(pricePlanContractPeriod).append("\n");
    sb.append("  eligibleSimOnly: ").append(eligibleSimOnly).append("\n");
    sb.append("  eligibleWithDevice: ").append(eligibleWithDevice).append("\n");
    sb.append("  bundlePrice: ").append(bundlePrice).append("\n");
    sb.append("  discountedPrice: ").append(discountedPrice).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
