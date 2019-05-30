package ro.vodafone.mcare.android.service.tracking.adobe.target;

/**
 * Created by Bivol Pavel on 22.04.2018.
 */

public abstract class AdobeBaseElement {

    protected AdobeTargetResponseElement adobeTargetResponseElement;

    public AdobeBaseElement(AdobeTargetResponseElement adobeTargetResponseElement) {
        this.adobeTargetResponseElement = adobeTargetResponseElement;
    }

    public abstract void display();


}
