package ro.vodafone.mcare.android.client.model.billing;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 02.02.2017.
 */
public class GetPaymentInputsResponse {

    @SerializedName("htmlForm")
    String htmlForm;

    @SerializedName("successUrl")
    String successUrl;

    @SerializedName("successScreen")
    String successScreen;

    @SerializedName("successMessage")
    String successMessage;

    public String getHtmlForm() {
        return htmlForm;
    }

    public void setHtmlForm(String htmlForm) {
        this.htmlForm = htmlForm;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getSuccessScreen() {
        return successScreen;
    }

    public void setSuccessScreen(String successScreen) {
        this.successScreen = successScreen;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }
}
