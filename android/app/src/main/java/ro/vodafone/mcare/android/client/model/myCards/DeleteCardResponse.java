package ro.vodafone.mcare.android.client.model.myCards;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DeleteCardResponse implements Serializable {
    @SerializedName("successMessage")
    private String successMessage;

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }
}
