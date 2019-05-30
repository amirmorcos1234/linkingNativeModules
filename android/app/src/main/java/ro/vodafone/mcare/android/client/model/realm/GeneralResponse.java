package ro.vodafone.mcare.android.client.model.realm;

import com.google.gson.annotations.SerializedName;

/**
 * General response for all requests that return an result with persistent data.
 * @param <TransactionSuccess> a TrasactionSucces class in name,
 *                            may be different from response to response
 *
 */
public class GeneralResponse<TransactionSuccess>  {

    @SerializedName("timeToLive")
    private long timeToLive;

    @SerializedName("transactionStatus")
    private int transactionStatus;

    @SerializedName("transactionSuccess")
    private TransactionSuccess transactionSuccess;

    @SerializedName("transactionFault")
    private TransactionFault transactionFault;

    public GeneralResponse() { }

    public GeneralResponse(TransactionSuccess transactionSuccess){
        this.transactionSuccess = transactionSuccess;
    }

    public void setTransactionStatus(int transactionStatus){
        this.transactionStatus = transactionStatus;
    }
    public int getTransactionStatus(){
        return this.transactionStatus;
    }
    public void setTransactionSuccess(TransactionSuccess transactionSuccess){
        this.transactionSuccess = transactionSuccess;
    }
    public TransactionSuccess getTransactionSuccess(){
        return this.transactionSuccess;
    }
    public TransactionFault getTransactionFault() {
        return transactionFault;
    }

    public void setTransactionFault(TransactionFault transactionFault) {
        this.transactionFault = transactionFault;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }
}

