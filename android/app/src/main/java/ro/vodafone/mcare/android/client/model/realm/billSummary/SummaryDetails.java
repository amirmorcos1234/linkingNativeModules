/**
 * API-56 Bill Summary
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 *
 * OpenAPI spec version: 6.0.2
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.realm.billSummary;

import java.util.*;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.swagger.annotations.*;

import com.google.gson.annotations.SerializedName;

/**
 * Object containing summary details
 **/
@ApiModel(description = "Object containing summary details")
public class SummaryDetails extends RealmObject{


  public final static String VALUE_OF_DETAIL_KEY = "valueOfDetail";
  public final static String KEY_OF_DETAIL_KEY = "keyOfDetail";
  public final static String LABEL_OF_DETAIL_KEY = "labelOfDetail";
  public final static String PRIORITY_KEY = "priority";
  public final static String TOTAL_AMOUNT_DUE_VALUE = "TotalAmountDue";
  public final static String GSM_BENEFITS = "gsmBenefits";
  public final static String GSM_MONTHLY_FEE = "gsmMonthlyFee";
  /**
   * In abonament - text. Value taken from {@link BillSummaryItem#getAdditionalCost()}
   */
  public final static String SUPLIMENTARY = "additionalCost";
  /**
   * Total abonat fara TVA - text. Value taken from {@link BillSummaryItem#getTotalAmount()}
   */
  public final static String TOTAL_AMOUNT = "totalAmount";// value taken from BillSummaryItem
  public final static String GSM_SPECIAL_DISCOUNT = "gsmSpecialDiscounts";

  @SerializedName(VALUE_OF_DETAIL_KEY)
  private Double valueOfDetail = null;
  @SerializedName(KEY_OF_DETAIL_KEY)
  private String keyOfDetail = null;
  @SerializedName(LABEL_OF_DETAIL_KEY)
  private String labelOfDetail = null;
  @SerializedName(PRIORITY_KEY)
  private Long priority = null;
  @SerializedName("extendedDetails")
  private RealmList<ServiceDetails> extendedDetails = null;

  /**
   * value of detail
   **/
  @ApiModelProperty(value = "value of detail")
  public Double getValueOfDetail() {
    return valueOfDetail;
  }
  public void setValueOfDetail(Double valueOfDetail) {
    this.valueOfDetail = valueOfDetail;
  }

  /**
   * key of detail
   **/
  @ApiModelProperty(value = "key of detail")
  public String getKeyOfDetail() {
    return keyOfDetail;
  }
  public void setKeyOfDetail(String keyOfDetail) {
    this.keyOfDetail = keyOfDetail;
  }

  /**
   * priority of detail
   **/
  @ApiModelProperty(value = "priority of detail")
  public Long getPriority() {
    return priority;
  }
  public void setPriority(Long priority) {
    this.priority = priority;
  }

  /**
   * third party fees details
   **/
  @ApiModelProperty(value = "third party fees details")
  public RealmList<ServiceDetails> getExtendedDetails() {
    return extendedDetails;
  }
  public void setExtendedDetails(RealmList<ServiceDetails> extendedDetails) {
    this.extendedDetails = extendedDetails;
  }

    /**
     * label of detail
     **/
    @ApiModelProperty(value = "label of detail")
    public String getLabelOfDetail() {
        return labelOfDetail;
    }

    public void setLabelOfDetail(String labelOfDetail) {
        this.labelOfDetail = labelOfDetail;
    }

    @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SummaryDetails summaryDetails = (SummaryDetails) o;
    return (this.valueOfDetail == null ? summaryDetails.valueOfDetail == null : this.valueOfDetail.equals(summaryDetails.valueOfDetail)) &&
        (this.keyOfDetail == null ? summaryDetails.keyOfDetail == null : this.keyOfDetail.equals(summaryDetails.keyOfDetail)) &&
        (this.labelOfDetail == null ? summaryDetails.labelOfDetail == null : this.labelOfDetail.equals(summaryDetails.labelOfDetail)) &&
        (this.priority == null ? summaryDetails.priority == null : this.priority.equals(summaryDetails.priority)) &&
        (this.extendedDetails == null ? summaryDetails.extendedDetails == null : this.extendedDetails.equals(summaryDetails.extendedDetails));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.valueOfDetail == null ? 0: this.valueOfDetail.hashCode());
    result = 31 * result + (this.keyOfDetail == null ? 0: this.keyOfDetail.hashCode());
    result = 31 * result + (this.labelOfDetail == null ? 0: this.labelOfDetail.hashCode());
    result = 31 * result + (this.priority == null ? 0: this.priority.hashCode());
    result = 31 * result + (this.extendedDetails == null ? 0: this.extendedDetails.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class SummaryDetails {\n");
    
    sb.append("  valueOfDetail: ").append(valueOfDetail).append("\n");
    sb.append("  keyOfDetail: ").append(keyOfDetail).append("\n");
    sb.append("  labelOfDetail: ").append(labelOfDetail).append("\n");
    sb.append("  priority: ").append(priority).append("\n");
    sb.append("  extendedDetails: ").append(extendedDetails).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
