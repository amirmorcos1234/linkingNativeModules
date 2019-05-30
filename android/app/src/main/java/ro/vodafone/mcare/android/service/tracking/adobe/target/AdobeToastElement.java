package ro.vodafone.mcare.android.service.tracking.adobe.target;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.ui.utils.CustomToast;

/**
 * Created by Bivol Pavel on 22.04.2018.
 */

public class AdobeToastElement extends AdobeBaseElement {


    public AdobeToastElement(AdobeTargetResponseElement adobeTargetResponseElement) {
        super(adobeTargetResponseElement);
    }

    @Override
    public void display() {

        new CustomToast.Builder(VodafoneController.currentActivity())
                .message(adobeTargetResponseElement.getContent())
                .success(adobeTargetResponseElement.isToastTypeSuccess())
                .show();
    }
}
