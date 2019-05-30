package ro.vodafone.mcare.android.client.model.offers.activeOffersEbu;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * object containing data about promotion
 */
@ApiModel(description = "object containing data about promotion")

public class Benefit   {
  @SerializedName("benefitDescription")
  private String benefitDescription = null;

  @SerializedName("seqNumber")
  private String seqNumber = null;

  public Benefit benefitDescription(String benefitDescription) {
    this.benefitDescription = benefitDescription;
    return this;
  }

   /**
   * promo name
   * @return Description
  **/
  @ApiModelProperty(required = true, value = "promo name")
  public String getBenefitDescription() {
    return benefitDescription;
  }

  public void setBenefitDescription(String benefitDescription) {
    this.benefitDescription = benefitDescription;
  }

  public Benefit seqNumber(String seqNumber) {
    this.seqNumber = seqNumber;
    return this;
  }

   /**
   * name of category to which the promo belongs
   * @return seqNumber
  **/
  @ApiModelProperty(required = true, value = "name of category to which the promo belongs")
  public String getseqNumberString() {
    return seqNumber;
  }


  public Long getseqNumber() {
    if(seqNumber != null){
      return Long.valueOf(seqNumber);
    }else{
      return 0L;
    }
  }

  public void setseqNumber(String seqNumber) {
    this.seqNumber = seqNumber;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Tarrif {\n");
    
    sb.append("    description: ").append(toIndentedString(benefitDescription)).append("\n");
    sb.append("    seqNumber: ").append(toIndentedString(seqNumber)).append("\n");
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

