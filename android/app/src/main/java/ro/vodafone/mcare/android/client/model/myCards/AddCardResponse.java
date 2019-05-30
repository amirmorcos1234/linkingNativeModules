package ro.vodafone.mcare.android.client.model.myCards;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddCardResponse implements Serializable {
    @SerializedName("htmlForm")
    private String htmlForm;
    @SerializedName("successMessage")
    private String successMessage;
    @SerializedName("successScreen")
    private String successScreen;
    @SerializedName("successUrl")
    private String successUrl;

    public String getHtmlForm() {
        return htmlForm;
    }

    public void setHtmlForm(String htmlForm) {
        this.htmlForm = htmlForm;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public String getSuccessScreen() {
        return successScreen;
    }

    public void setSuccessScreen(String successScreen) {
        this.successScreen = successScreen;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }
}
