package ro.vodafone.mcare.android.client.model.dashboard.vov;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.utils.navigation.notification.NotificationAction;

public enum VoiceOfVodafoneType{
    Notification(R.layout.voice_of_vodafone_notification),
    Dialog(R.layout.voice_of_vodafone_dialog),
    AddToFavoritesNumbers(R.layout.voice_of_vodafone_overlay_dialog_layout);

    private int layout;

    VoiceOfVodafoneType(int layout) {
        this.layout = layout;
    }

    public int getLayout() {
        return layout;
    }

    public static VoiceOfVodafoneType getVoiceOfVodafoneTypeAfterString(String type){
        if("notification".equals(type)){
            return Notification;
        }else if("dialog".equals(type)){
            return Dialog;
        }else{
            return Notification;
        }

    }
}
