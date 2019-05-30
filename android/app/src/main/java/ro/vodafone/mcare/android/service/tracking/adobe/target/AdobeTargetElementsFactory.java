package ro.vodafone.mcare.android.service.tracking.adobe.target;

import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.TargetElementsConstants;

import static ro.vodafone.mcare.android.service.tracking.adobe.target.constants.TargetElementsConstants.NUDGE;
import static ro.vodafone.mcare.android.service.tracking.adobe.target.constants.TargetElementsConstants.NUDGE_UAT;
import static ro.vodafone.mcare.android.service.tracking.adobe.target.constants.TargetElementsConstants.OVERLAY_1;
import static ro.vodafone.mcare.android.service.tracking.adobe.target.constants.TargetElementsConstants.OVERLAY_1_UAT;
import static ro.vodafone.mcare.android.service.tracking.adobe.target.constants.TargetElementsConstants.OVERLAY_2;
import static ro.vodafone.mcare.android.service.tracking.adobe.target.constants.TargetElementsConstants.OVERLAY_2_UAT;
import static ro.vodafone.mcare.android.service.tracking.adobe.target.constants.TargetElementsConstants.TOAST;
import static ro.vodafone.mcare.android.service.tracking.adobe.target.constants.TargetElementsConstants.TOAST_UAT;
import static ro.vodafone.mcare.android.service.tracking.adobe.target.constants.TargetElementsConstants.VOV;
import static ro.vodafone.mcare.android.service.tracking.adobe.target.constants.TargetElementsConstants.VOV_UAT;
import static ro.vodafone.mcare.android.service.tracking.adobe.target.constants.TargetElementsConstants.Element;

/**
 * Created by Bivol Pavel on 22.04.2018.
 */

public class AdobeTargetElementsFactory {

    public static AdobeBaseElement getElement(AdobeTargetResponseElement adobeTargetResponseElement){

        if (adobeTargetResponseElement == null
                || adobeTargetResponseElement.getElement() == null)
            return null;

        TargetElementsConstants.setTargetElement(adobeTargetResponseElement.getElement().toLowerCase());

        AdobeBaseElement adobeElement = null;

        @Element String targetElementType = TargetElementsConstants.getTargetElement();

        switch (targetElementType.toLowerCase()){
            case VOV: case VOV_UAT:
                adobeElement = new AdobeVovElement(adobeTargetResponseElement);
                break;

            case OVERLAY_1: case OVERLAY_2: case OVERLAY_1_UAT: case OVERLAY_2_UAT:
                adobeElement = new AdobeOverlayElement(adobeTargetResponseElement);
                break;

            case NUDGE:case NUDGE_UAT:
                adobeElement = new AdobeNudgeElement(adobeTargetResponseElement);
                break;

            case TOAST:case TOAST_UAT:
                adobeElement = new AdobeToastElement(adobeTargetResponseElement);
                break;
        }

        return adobeElement;
    }

}
