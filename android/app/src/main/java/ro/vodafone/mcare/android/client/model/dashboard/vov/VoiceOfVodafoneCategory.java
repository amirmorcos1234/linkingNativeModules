package ro.vodafone.mcare.android.client.model.dashboard.vov;

/**
 * Created by Victor Radulescu on 7/6/2017.
 */

public enum VoiceOfVodafoneCategory {
    Welcome("Welcome"),
    Pay_Bill("Pay_Bill"),
    Call_Details("Call_Details"),
    Chat("Chat"),
    Recharge("Recharge"),
    Pending("Pending"),
    TransferCredit("TransferCredit"),
    EO_Activation("EO_Activation"),
    EO_Deactivation("EO_Deactivation"),
    Accepted_Offers("Accepted_offers"),
    LP("LP"),
    PA("PA"),
    BAR_UNBAR("BAR/UNBAR"),
    Roaming("Roaming"),
    Travelling_abroad("Travelling_abroad"),
    WebView_Campaign("WebviewCampaign"),
    ADOBE_TARGET("Adobe_Target"),
    Permissions("Permissions"),
    Ion_Unlimited("Ion_Unlimited"),
    UserProfileHierarchy("UserProfileHierarchy"),
    Sim_Replace("Sim_Replace"),
    Vodafone_TV("Vodafone_TV"),
    OTHER("");

    String description;

    VoiceOfVodafoneCategory(String description) {
        this.description = description;
    }

    public static VoiceOfVodafoneCategory fromString(String x) {
        VoiceOfVodafoneCategory[] values = VoiceOfVodafoneCategory.values();
        for (int i = 0; i < values.length; i++)
            if (values[i].description.equalsIgnoreCase(x)) return values[i];
        return null;
    }

    public String getDescription() {
        return description;
    }
}
