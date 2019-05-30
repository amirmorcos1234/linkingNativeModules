package ro.vodafone.mcare.android.client.model.payBill;

import java.io.Serializable;

/**
 * Created by Bivol Pavel on 29.06.2017.
 */

public class WebViewBaseModel implements Serializable{

    private String htmlInputs = null;
    private String successUrl;


    public String getHtmlInputs() {
        return htmlInputs;
    }

    public void setHtmlInputs(String htmlInputs) {
        this.htmlInputs = htmlInputs;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

}
