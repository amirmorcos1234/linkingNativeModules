package ro.vodafone.mcare.android.client.model.realm.offers;

public enum BannerOfferType {
    WEBVIEW("webview"),
    CPC("cpc"),
    LM("lm"),
    UNICA("unica"),
    WEBVIEW_CAMPAIGN("webviewcampaign"),
    EPC("epc");

    private String type;

    BannerOfferType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
