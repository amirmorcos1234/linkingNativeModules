package ro.vodafone.mcare.android.client.model.dashboard.vov;

/**
 * Created by Victor Radulescu on 1/27/2017.
 */

public enum VoiceOfVodafoneAction {

    Dismiss("dismiss"),
    Redirect("redirect"),
    GoToSupportChat("support"),
    Activate_Offer("activateOffer"),
    AddToFavoritesNumbers("addToFavoritesNumbers"),
    RedirectToRoaming("redirectToRoaming"),
    OpenUrl("openUrl"),
    RedirectWithIntent("redirectWithIntent"),
    RedirectToBrowser("redirectToBrowser"),
    DeepLinkAction("deepLinkAction"),
    ExternalAppRedirect("redirectToExternalApp"),
    ReloadAPI19("reloadAPI19");

    private String actionName;

    VoiceOfVodafoneAction(String actionName) {
        this.actionName = actionName;
    }

    public String getActionName() {
        return actionName;
    }

    public static VoiceOfVodafoneAction fromString(String x) {
        VoiceOfVodafoneAction[] values = VoiceOfVodafoneAction.values();
        for (int i = 0; i < values.length; i++)
            if (values[i].actionName.equalsIgnoreCase(x)) return values[i];
        return null;
    }
}
