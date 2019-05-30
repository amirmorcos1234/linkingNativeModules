/**
 * API-47 Shop Products
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 * <p>
 * OpenAPI spec version: 7.0.1
 * <p>
 * <p>
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
public class ShopProduct extends RealmObject {

	public static final String PHONE_SKU_ID = "phoneSkuId";
	public static final String PRICE_PLAN_SKU_ID = "pricePlanSkuId";
	public static final String DISCOUNTED_PRICE = "discountedPrice";
	public static final String GREEN_TAX = "greenTax";
	@PrimaryKey
	@SerializedName("phoneSkuId")
	private String phoneSkuId = null;
	@SerializedName("pricePlanSkuId")
	private String pricePlanSkuId = null;
	@SerializedName("productId")
	private String productId = null;
	@SerializedName("cfgSkuId")
	private String cfgSkuId = null;
	@SerializedName("phoneBrand")
	private String phoneBrand = null;
	@SerializedName("phoneDisplayName")
	private String phoneDisplayName = null;
	@SerializedName("greenTax")
	private Double greenTax = null;
	@SerializedName("pricePlanDisplayName")
	private String pricePlanDisplayName = null;
	@SerializedName("pricePlanDetailsHtml")
	private String pricePlanDetailsHtml = null;
	@SerializedName("pricePlanMothlyFee")
	private Float pricePlanMothlyFee = null;
	@SerializedName("pricePlanContractPeriod")
	private Integer pricePlanContractPeriod = null;
	@SerializedName("bundlePrice")
	private Float bundlePrice = null;
	@SerializedName("discountedPrice")
	private Float discountedPrice = null;
	@SerializedName("tehnicalSpecificationsHtml")
	private String tehnicalSpecificationsHtml = null;
	@SerializedName("descriptionHtml")
	private String descriptionHtml = null;
	@SerializedName("defaultImageUrl")
	private String defaultImageUrl = null;
	@SerializedName("images")
	private RealmList<ProductImage> images = null;
	@SerializedName("colors")
	private RealmList<ProductColor> colors = null;
	@SerializedName("memories")
	private RealmList<ProductMemory> memories = null;
	@SerializedName("stockLevel")
	private Integer stockLevel = null;
	@SerializedName("recommendedPricePlans")
	private RealmList<RecommendedPricePlan> recommendedPricePlans = null;
	@SerializedName("additionalBenefits")
	private RealmList<AdditionalBenefits> additionalBenefits;
	@SerializedName("pricePlanBenefits")
	private String pricePlanBenefits = null;

	public RealmList<AdditionalBenefits> getAdditionalBenefits() {
		return additionalBenefits;
	}

	public void setAdditionalBenefits(RealmList<AdditionalBenefits> additionalBenefits) {
		this.additionalBenefits = additionalBenefits;
	}

	public String getPricePlanBenefits() {
		return pricePlanBenefits;
	}

	public void setPricePlanBenefits(String pricePlanBenefits) {
		this.pricePlanBenefits = pricePlanBenefits;
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
	 * brand
	 **/
	@ApiModelProperty(value = "brand")
	public String getPhoneBrand() {
		return phoneBrand;
	}

	public void setPhoneBrand(String phoneBrand) {
		this.phoneBrand = phoneBrand;
	}

	/**
	 * display name
	 **/
	@ApiModelProperty(value = "display name")
	public String getPhoneDisplayName() {
		return phoneDisplayName;
	}

	public void setPhoneDisplayName(String phoneDisplayName) {
		this.phoneDisplayName = phoneDisplayName;
	}

	/**
	 * green tax
	 **/
	@ApiModelProperty(value = "green tax")
	public Double getGreenTax() {
		return greenTax;
	}

	public void setGreenTax(Double greenTax) {
		this.greenTax = greenTax;
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

	/**
	 * technical specifications html content
	 **/
	@ApiModelProperty(value = "technical specifications html content")
	public String getTehnicalSpecificationsHtml() {
		return tehnicalSpecificationsHtml;
	}

	public void setTehnicalSpecificationsHtml(String tehnicalSpecificationsHtml) {
		this.tehnicalSpecificationsHtml = tehnicalSpecificationsHtml;
	}

	/**
	 * description html content
	 **/
	@ApiModelProperty(value = "description html content")
	public String getDescriptionHtml() {
		return descriptionHtml;
	}

	public void setDescriptionHtml(String descriptionHtml) {
		this.descriptionHtml = descriptionHtml;
	}

	/**
	 * default image url
	 **/
	@ApiModelProperty(value = "default image url")
	public String getDefaultImageUrl() {
		return defaultImageUrl;
	}

	public void setDefaultImageUrl(String defaultImageUrl) {
		this.defaultImageUrl = defaultImageUrl;
	}

	/**
	 * list of images
	 **/
	@ApiModelProperty(value = "list of images")
	public RealmList<ProductImage> getImages() {
		return images;
	}

	public void setImages(RealmList<ProductImage> images) {
		this.images = images;
	}

	/**
	 * list of colors
	 **/
	@ApiModelProperty(value = "list of colors")
	public RealmList<ProductColor> getColors() {
		return colors;
	}

	public void setColors(RealmList<ProductColor> colors) {
		this.colors = colors;
	}

	/**
	 * list of colors
	 **/
	@ApiModelProperty(value = "list of colors")
	public RealmList<ProductMemory> getMemories() {
		return memories;
	}

	public void setMemories(RealmList<ProductMemory> memories) {
		this.memories = memories;
	}

	/**
	 * stock level
	 **/
	@ApiModelProperty(value = "stock level")
	public Integer getStockLevel() {
		return stockLevel;
	}

	public void setStockLevel(Integer stockLevel) {
		this.stockLevel = stockLevel;
	}

	/**
	 * list of colors
	 **/
	@ApiModelProperty(value = "list of colors")
	public RealmList<RecommendedPricePlan> getRecommendedPricePlans() {
		return recommendedPricePlans;
	}

	public void setRecommendedPricePlans(RealmList<RecommendedPricePlan> recommendedPricePlans) {
		this.recommendedPricePlans = recommendedPricePlans;
	}

	public boolean isKeep() {
		return pricePlanSkuId != null && pricePlanSkuId.contains("keep");
	}
}