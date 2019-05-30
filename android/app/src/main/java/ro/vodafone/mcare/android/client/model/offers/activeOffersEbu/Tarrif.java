package ro.vodafone.mcare.android.client.model.offers.activeOffersEbu;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * object containing data about promotion
 */
@ApiModel(description = "object containing data about promotion")

public class Tarrif   {
  @SerializedName("tarrifDescription")
  private String tarrifDescription = null;

  @SerializedName("tarrifCategory")
  private String tarrifCategory = null;

  public Tarrif tarrifDescription(String tarrifDescription) {
    this.tarrifDescription = tarrifDescription;
    return this;
  }

   /**
   * promo name
   * @return tarrifDescription
  **/
  @ApiModelProperty(required = true, value = "promo name")
  public String getTarrifDescription() {
    return tarrifDescription;
  }

  public void setTarrifDescription(String tarrifDescription) {
    this.tarrifDescription = tarrifDescription;
  }

  public Tarrif tarrifCategory(String tarrifCategory) {
    this.tarrifCategory = tarrifCategory;
    return this;
  }

   /**
   * name of category to which the promo belongs
   * @return tarrifCategory
  **/
  @ApiModelProperty(required = true, value = "name of category to which the promo belongs")
  public String getTarrifCategory() {
    return tarrifCategory;
  }

  public void setTarrifCategory(String tarrifCategory) {
    this.tarrifCategory = tarrifCategory;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Tarrif {\n");
    
    sb.append("    tarrifDescription: ").append(toIndentedString(tarrifDescription)).append("\n");
    sb.append("    tarrifCategory: ").append(toIndentedString(tarrifCategory)).append("\n");
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

