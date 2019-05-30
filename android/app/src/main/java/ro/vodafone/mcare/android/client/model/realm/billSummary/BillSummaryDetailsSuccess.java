/**
 * API-56 Bill Summary
 * Rest web service exposed by Vodafone API application and consumed by mobile device applications.
 * <p>
 * OpenAPI spec version: 6.0.2
 * <p>
 * <p>
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ro.vodafone.mcare.android.client.model.realm.billSummary;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Object containing data about bill summary for one subscriber
 **/
@ApiModel(description = "Object containing data about bill summary for one subscriber")
public class BillSummaryDetailsSuccess extends RealmObject {

    @PrimaryKey
    private long id;

    @SerializedName("billSummaryDetailsList")
    private RealmList<SummaryDetails> billSummaryDetailsList = null;

    public BillSummaryDetailsSuccess() {
        this.id = 1;
    }

    /**
     * list of bill summary details
     **/
    @ApiModelProperty(value = "list of bill summary details")
    public RealmList<SummaryDetails> getBillSummaryDetailsList() {
        return billSummaryDetailsList;
    }

    public void setBillSummaryDetailsList(RealmList<SummaryDetails> billSummaryDetailsList) {
        this.billSummaryDetailsList = billSummaryDetailsList;
    }


}