package ro.vodafone.mcare.android.client.model.dashboard.vov;

/**
 * Created by Victor Radulescu on 1/31/2017.
 */

public enum  VoiceOfVodafoneParameter {

    Paybill_page("paybill_page"),
    Support_page("support_page");

    private String className;

    VoiceOfVodafoneParameter(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
