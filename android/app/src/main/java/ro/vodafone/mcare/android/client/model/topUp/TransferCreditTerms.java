package ro.vodafone.mcare.android.client.model.topUp;

import com.google.gson.annotations.SerializedName;

public class TransferCreditTerms {


    @SerializedName("transfer_credit_terms")
    private String transferCreditTerms;

    public String getTransferCreditTerms() {
        return transferCreditTerms;
    }

    public void setTransferCreditTerms(String transferCreditTerms) {
        this.transferCreditTerms = transferCreditTerms;
    }
}
