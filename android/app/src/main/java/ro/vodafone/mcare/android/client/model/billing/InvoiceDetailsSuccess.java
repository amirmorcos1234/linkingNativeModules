package ro.vodafone.mcare.android.client.model.billing;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by user on 02.02.2017.
 */
public class InvoiceDetailsSuccess extends RealmObject {

    @PrimaryKey
    long id;

    @SerializedName("invoiceNo")
    String invoiceNo;

    @SerializedName("accountNo")
    String accountNo;

    @SerializedName("invoiceAmount")
    String invoiceAmount;

    @SerializedName("issueDate")
    String issueDate;

    public InvoiceDetailsSuccess() {
        this.id = 1;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }
}
