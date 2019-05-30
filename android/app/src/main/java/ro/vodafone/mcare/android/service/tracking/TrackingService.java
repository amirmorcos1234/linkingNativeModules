package ro.vodafone.mcare.android.service.tracking;

import android.arch.lifecycle.LifecycleOwner;

import com.adobe.mobile.Analytics;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.service.tracking.adobe.target.AdobeTargetController;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.utils.Logger;

/**
 * Class handling the tracking of the application.
 *
 * @author Andrei DOLTU
 */

public class TrackingService {

	public static final Logger LOGGER = Logger.getInstance(TrackingService.class);
	
	private TrackingAppMeasurement s;

	public TrackingService(VodafoneController applicationContext) {
		//s = new AppMeasurement(applicationContext);
		s = new TrackingAppMeasurement();

		/* Turn on and configure debugging here */
		s.debugTracking = true;

		/*
		 * WARNING: Changing any of the below variables will cause drastic
		 * changes to how your visitor data is collected. Changes should only be
		 * made when instructed to do so by your account manager.
		 */
		//s.trackingServer = TRACKING_SERVER;
	}

	public void track(TrackingEvent trackableEvent) {

		try {
			trackEvent(trackableEvent);
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

    public void trackCustom(TrackingEvent trackableEvent) {

        try {
            trackEventCustom(trackableEvent);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

	public void trackEvent(TrackingEvent trackableEvent){

		if (s.getContextData() != null){
			s.getContextData().clear();
		}

		trackEventCustom(trackableEvent);
	}

	public void trackEventCustom(TrackingEvent trackableEvent){

		trackableEvent.defineTrackingProperties(s);

		s.hier1 = s.pageName;
		s.eVar1 = s.prop1;
		s.eVar3 = s.prop3;
		//s.eVar5 = s.prop6;
		//s.eVar8 = s.prop9;
		s.eVar12 = s.prop74;
		s.eVar15 = s.prop46;
		s.eVar23 = s.prop28;
		s.eVar52 = s.pageName;
		s.eVar62 = s.prop19;
		s.eVar65 = s.prop53;


		s.eVar45= s.prop66;
		s.eVar56 = s.prop69;
		//s.eVar71= s.prop70;
		s.eVar76 = s.prop71;
		s.eVar77 = s.prop72;
		s.eVar78 = s.prop73;
		s.eVar79 = s.prop13;
		//s.eVar80 = s.prop74;

		//-----------------------

		s.getContextData().put(TrackingVariable.H_HIER1, s.pageName);
		s.getContextData().put(TrackingVariable.E_SEARCH_TERMS, s.prop1);
		s.getContextData().put(TrackingVariable.E_EVAR3, s.prop3);
		//s.getContextData().put(TrackingVariable.E_PAGE_TYPE, s.prop6);
		//s.getContextData().put(TrackingVariable.E_VISITOR_TYPE, s.prop9);
		s.getContextData().put(TrackingVariable.E_TOOL_NAME, s.prop74);
		s.getContextData().put(TrackingVariable.E_EVAR15, s.prop46);
		s.getContextData().put(TrackingVariable.E_EVAR23, s.prop28);
		s.getContextData().put(TrackingVariable.E_EVAR62, s.prop19);
		s.getContextData().put(TrackingVariable.E_EVAR65, s.prop53);

		s.getContextData().put(TrackingVariable.E_EVAR45,s.prop66);
		s.getContextData().put(TrackingVariable.E_EVAR56,s.prop69);
		//s.getContextData().put(TrackingVariable.E_EVAR71,s.prop70);
		s.getContextData().put(TrackingVariable.E_EVAR76,s.prop71);
		s.getContextData().put(TrackingVariable.E_EVAR77,s.prop72);
		s.getContextData().put(TrackingVariable.E_EVAR78,s.prop73);
		s.getContextData().put(TrackingVariable.E_EVAR79,s.prop13);
		s.getContextData().put(TrackingVariable.P_PROP55,s.prop55);

		//s.getContextData().put(TrackingVariable.E_EVAR80,s.prop74);

		LOGGER.d("Track page name " + s.pageName);
		//LOGGER.d("s.getContextData() =  " + s.getContextData());


		Analytics.trackState(s.pageName, s.getContextData());
		//call Adobe Target
		/*if(AdobePageNamesConstants.isInPageNameList(s.pageName))
			AdobeTargetController.getInstance().trackPage(VodafoneController.getInstance().getLifecycleOwner(), s.pageName);
	*/
	}
}
