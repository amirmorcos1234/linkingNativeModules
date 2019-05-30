package ro.vodafone.mcare.android.ui.activities.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.AdobeTargetController;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;
import ro.vodafone.mcare.android.utils.TealiumHelper;

public class TermsAndConditionsActivity extends WebviewActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callForAdobeTarget(AdobePageNamesConstants.TERMS);
    }

    @Override
    public void initWebView() {
        webviewUrl = getString(R.string.terms_and_conditions_url);
        super.initWebView();

        //Tealium Track view
        Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put("screen_name","terms & conditions");
        tealiumMapView.put("journey_name","privacy");
        TealiumHelper.trackView("screen_name", tealiumMapView);

        TermsAndConditionsTrackingEvent event = new TermsAndConditionsTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        VodafoneController.getInstance().setFromBackPress(true);
    }

    public static class TermsAndConditionsTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "terms & conditions";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "terms & conditions");


            s.channel = "privacy";
            s.getContextData().put("&&channel", s.channel);
        }
    }

}
