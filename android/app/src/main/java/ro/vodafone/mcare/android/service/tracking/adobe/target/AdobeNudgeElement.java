package ro.vodafone.mcare.android.service.tracking.adobe.target;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.utils.SurveyOverlayDialog;

/**
 * Created by Prodan Pavel on 22.04.2018.
 */

public class AdobeNudgeElement extends AdobeBaseElement {
    public AdobeNudgeElement(AdobeTargetResponseElement adobeTargetResponseElement) {
        super(adobeTargetResponseElement);
    }

    @Override
    public void display() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("adobeResponse", new Gson().toJson(adobeTargetResponseElement));

        Intent intent = new Intent(VodafoneController.currentActivity(), SurveyOverlayDialog.class);
        intent.putExtras(bundle);
        VodafoneController.currentActivity().startActivity(intent);

    }
}
