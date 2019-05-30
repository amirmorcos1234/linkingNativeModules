package ro.vodafone.mcare.android.service.tracking.adobe.target;

import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;

/**
 * Created by user Bivol Pavel 22.04.2018.
 */

public class AdobeVovElement extends AdobeBaseElement {
    public AdobeVovElement(AdobeTargetResponseElement adobeTargetResponseElement) {
        super(adobeTargetResponseElement);
    }

    @Override
    public void display() {

        if (adobeTargetResponseElement == null)
            return;

        int id_vov = 10;
        int priority = adobeTargetResponseElement.getPriority() != null?Integer.valueOf(adobeTargetResponseElement.getPriority()):20;
        String title = adobeTargetResponseElement.getTitle();
        String message = adobeTargetResponseElement.getContent();
        String leftButtonTitle = adobeTargetResponseElement.getButton1label();
        String rightButtonTitle = adobeTargetResponseElement.getButton2label();
        boolean showLeftBtn = adobeTargetResponseElement.isButton1Displayed();
        boolean showRightBtn = adobeTargetResponseElement.isButton2Displayed();

        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(id_vov, priority, VoiceOfVodafoneCategory.ADOBE_TARGET,
                title, message, leftButtonTitle, rightButtonTitle, showLeftBtn, showRightBtn,
                null, null);

        if (showLeftBtn){
            setButtonAction(voiceOfVodafone, true, adobeTargetResponseElement.getButton1urltype(), adobeTargetResponseElement.getButton1action());
        }

        if (showRightBtn){
            setButtonAction(voiceOfVodafone, false, adobeTargetResponseElement.getButton2urltype(), adobeTargetResponseElement.getButton2action());
        }

        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
        VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();
    }

    private void setButtonAction(VoiceOfVodafone voiceOfVodafone, boolean isLeftButton, String urlType, String action){
        VoiceOfVodafoneAction voiceOfVodafoneAction = null;

        switch (urlType){
            case "intern":
                voiceOfVodafoneAction = VoiceOfVodafoneAction.DeepLinkAction;
                break;

            case "extern":
                voiceOfVodafoneAction = VoiceOfVodafoneAction.RedirectToBrowser;
                break;

            case "webview":
                voiceOfVodafoneAction = VoiceOfVodafoneAction.OpenUrl;
                IntentActionName intent = IntentActionName.WEBVIEW;
                voiceOfVodafone.setIntentActionName(intent);
                break;

            case "close":default:
                voiceOfVodafoneAction = VoiceOfVodafoneAction.Dismiss;
        }

        if(isLeftButton){
            voiceOfVodafone.setLeftAction(voiceOfVodafoneAction);
        }else{
            voiceOfVodafone.setRightAction(voiceOfVodafoneAction);
        }

        if(action != null){
            if(isLeftButton){
                voiceOfVodafone.setLeftActionUrl(action);
            }else{
                voiceOfVodafone.setRightActionUrl(action);
            }
        }
    }
}
