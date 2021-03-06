/**
 * API-55  Bill History
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 * <p>
 * OpenAPI spec version: 6.0.1
 * <p>
 * <p>
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.realm.billHistory;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import ro.vodafone.mcare.android.presenter.adapterelements.billing.UnpayedBillOverviewCardElement;

/**
 * Object containing value, key and priority of certain detail
 **/
@ApiModel(description = "Object containing value, key and priority of certain detail")
public class HistoryDetail extends RealmObject implements UnpayedBillOverviewCardElement.UnpayedBillingOverViewItem {

    public final static String VALUE_OF_DETAIL_KEY = "valueOfDetail";
    public final static String KEY_OF_DETAIL_KEY = "keyOfDetail";
    public final static String LABEL_OF_DETAIL_KEY = "labelOfDetail";

    public final static String PRIORITY_KEY = "priority";


    public final static String TOTAL_MONTHLY_FEE_VALUE = "TotalMonthlyFee";
    public final static String TOTAL_AMOUNT_DUE_VALUE = "TotalAmountDue";
    public final static String L9_UTILIZED_G_VALUE = "GsmUtilizedServices";
    public final static String TERM_ToT_AMOUNT_VALUE = "TermTotAmount";

    @SerializedName(VALUE_OF_DETAIL_KEY)
    private Double valueOfDetail = null;

    @SerializedName(KEY_OF_DETAIL_KEY)
    private String keyOfDetail = null;

    @SerializedName(LABEL_OF_DETAIL_KEY)
    private String labelOfDetail = null;

    @SerializedName("valueTextOfDetail")
    private String valueTextOfDetail = null;

    @SerializedName(PRIORITY_KEY)
    private Long priority = null;


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

    public String getLabelOfDetail() {
        return labelOfDetail;
    }

    public void setLabelOfDetail(String labelOfDetail) {
        this.labelOfDetail = labelOfDetail;
    }

    /**
     * value of detail text
     **/
    @ApiModelProperty(value = "value of detail text")
    public String getValueTextOfDetail() {
        return valueTextOfDetail;
    }
    public void setValueTextOfDetail(String valueTextOfDetail) {
        this.valueTextOfDetail = valueTextOfDetail;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HistoryDetail historyDetail = (HistoryDetail) o;
        return (this.labelOfDetail == null ? historyDetail.labelOfDetail == null : this.labelOfDetail.equals(historyDetail.labelOfDetail)) &&
                (this.valueOfDetail == null ? historyDetail.valueOfDetail == null : this.valueOfDetail.equals(historyDetail.valueOfDetail)) &&
                (this.valueTextOfDetail == null ? historyDetail.valueTextOfDetail == null : this.valueTextOfDetail.equals(historyDetail.valueTextOfDetail)) &&
                (this.keyOfDetail == null ? historyDetail.keyOfDetail == null : this.keyOfDetail.equals(historyDetail.keyOfDetail)) &&
                (this.priority == null ? historyDetail.priority == null : this.priority.equals(historyDetail.priority));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.labelOfDetail == null ? 0: this.labelOfDetail.hashCode());
        result = 31 * result + (this.valueOfDetail == null ? 0: this.valueOfDetail.hashCode());
        result = 31 * result + (this.valueTextOfDetail == null ? 0: this.valueTextOfDetail.hashCode());
        result = 31 * result + (this.keyOfDetail == null ? 0: this.keyOfDetail.hashCode());
        result = 31 * result + (this.priority == null ? 0: this.priority.hashCode());
        return result;
    }

    @Override
    public String toString()  {
        StringBuilder sb = new StringBuilder();
        sb.append("class HistoryDetail {\n");

        sb.append("  labelOfDetail: ").append(labelOfDetail).append("\n");
        sb.append("  valueOfDetail: ").append(valueOfDetail).append("\n");
        sb.append("  valueTextOfDetail: ").append(valueTextOfDetail).append("\n");
        sb.append("  keyOfDetail: ").append(keyOfDetail).append("\n");
        sb.append("  priority: ").append(priority).append("\n");
        sb.append("}\n");
        return sb.toString();
    }

    @Override
    public int getType() {
        return TERM_ToT_AMOUNT_VALUE.equals(keyOfDetail)?
                UnpayedBillOverviewCardElement.UnpayedBillingOverViewItem.TERM_ToT_AMOUNT_ITEM:
                UnpayedBillOverviewCardElement.UnpayedBillingOverViewItem.HISTORY_DETAIL_ITEM;
    }

}
