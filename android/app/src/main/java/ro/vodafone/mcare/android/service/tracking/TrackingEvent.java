package ro.vodafone.mcare.android.service.tracking;

import android.content.Context;
import android.os.Build;

import io.realm.Realm;
import ro.vodafone.mcare.android.BuildConfig;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.gdpr.GdprPermissions;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.utils.NetworkUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.StringMsisdnCrypt;
import ro.vodafone.mcare.android.utils.StringUtils;


/**
 * Model class for a tracking event.
 * 
 * @author Andrei DOLTU
 */
public abstract class TrackingEvent {

    public static String TAG = "TrackingEventDefault";


    protected static final String PAGE_TYPE_SELF_SERVICE = "self service";
	protected static final String PAGE_TYPE_CONTENT = "content";
	protected static final String PAGE_TYPE_ASSISTANCE = "asistenta";
	protected static final String CHANNEL = "mCare";
	protected static final String MARKET = "RO";
	protected static final String PLATFORM = "Android";

    private Context context = VodafoneController.getInstance().getBaseContext();

	private String errorMessage;
	private String swipeAction;

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public void setSwipeAction(String swipeAction) {
		this.swipeAction = swipeAction;
	}
	
	public String getSwipeAction() {
		return this.swipeAction;
	}

	/**
	 * Define the tracking properties. This method is invoked when the event is
	 * actually tracked.
	 * 
	 * @param s
	 *            the object on which the tracking properties should be set.
	 */
	protected void defineTrackingProperties(TrackingAppMeasurement s) {
		/*s.channel = CHANNEL;
		s.getContextData().put(TrackingVariable.P_CHANNEL, CHANNEL);*/
		
		s.prop6 = MARKET;
		s.getContextData().put(TrackingVariable.P_LOCAL_MARKET, MARKET);

        //to do: check values
		//get current user attempt
		try {
		UserProfile userProfile = VodafoneController.getInstance().getUserProfile();
        if (userProfile != null) {
			if (userProfile.getCustomerType().toLowerCase().contains("nonvodafone") && !userProfile.isMigrated()) {
				s.prop9 = "other";
				s.getContextData().put("prop9", s.prop9);
				s.eVar9 = "other";
				s.getContextData().put("eVar9", s.eVar9);
			} else if (userProfile.getCustomerType().toLowerCase().contains("prepaid")) {
				s.prop9 = "payg";
                s.getContextData().put("prop9", s.prop9);
                s.eVar9 = "payg";
                s.getContextData().put("eVar9", s.eVar9);
			} else {
				s.prop9 = "paym";
				s.getContextData().put("prop9", s.prop9);
                s.eVar9 = "paym";
                s.getContextData().put("eVar9", s.eVar9);
			}
            s.eVar15 = userProfile.getSsoId();
            s.getContextData().put("eVar15", s.eVar15);

			try {
			s.prop8 = userProfile.getUserRole().getDescription();
			s.getContextData().put("prop8", s.prop8);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
			s.eVar8 = userProfile.getUserRole().getDescription();
			s.getContextData().put("eVar8", s.eVar8);
			} catch (Exception e) {
				e.printStackTrace();
			}

			StringMsisdnCrypt msisdnCrypt = new StringMsisdnCrypt();
                /* Encrypt */
            String encryptedMsisdn = null;
            try {
                encryptedMsisdn = StringMsisdnCrypt.bytesToHex(msisdnCrypt.encrypt(VodafoneController.getInstance().getUserProfile().getMsisdn()));
                //Log.d(TAG, "encryptedMsisdn = " + encryptedMsisdn);
            } catch (Exception e) {
                e.printStackTrace();
            }

            s.prop17 = encryptedMsisdn;
            //Log.d(TAG, "s.prop17 = encryptedMsisdn = " + encryptedMsisdn);
            s.getContextData().put("prop17", s.prop17);

            if (VodafoneController.getInstance().isSeamless()){
                s.prop7 = "seamless";
                s.getContextData().put("prop7", s.prop7);
                s.eVar7 = "seamless";
                s.getContextData().put("eVar7", s.eVar7);
                s.prop46 = encryptedMsisdn;
                s.getContextData().put(TrackingVariable.P_PROP46, s.prop46);
                s.eVar15 = encryptedMsisdn;
                s.getContextData().put("eVar15", s.eVar15);
            }else{
                s.prop7 = "logged in";
                s.getContextData().put("prop7", s.prop7);
                s.eVar7 = "logged in";
                s.getContextData().put("eVar7", s.eVar7);
                s.prop46 = encryptedMsisdn;
                s.getContextData().put(TrackingVariable.P_PROP46, s.prop46);
                s.eVar15 = encryptedMsisdn;
                s.getContextData().put("eVar15", s.eVar15);
            }

		}
		}catch (Exception e){
			e.printStackTrace();
		}

		s.prop16 = errorMessage;
		s.getContextData().put(TrackingVariable.P_PAGE_ERROR, errorMessage);
		s.prop19 = Build.MODEL;
		s.getContextData().put(TrackingVariable.P_DEVICE_NAME, Build.MODEL);

		s.prop21 = "mcare:";
		s.getContextData().put(TrackingVariable.P_PAGE_LEVEL_2, "mcare:");

		s.prop28 = PLATFORM;
		s.getContextData().put(TrackingVariable.P_OS_VERSION, PLATFORM);
		s.prop75 = s.prop28;
		s.getContextData().put(TrackingVariable.P_PROP75, PLATFORM);

		s.prop13 = BuildConfig.VERSION_NAME;
		s.getContextData().put(TrackingVariable.P_PROP13, BuildConfig.VERSION_NAME);

		s.prop66 = NetworkUtils.getNetworkType(context);
		s.getContextData().put(TrackingVariable.P_PROP66,s.prop66 );

		s.prop69 = NetworkUtils.getNetworkType(context);
		s.getContextData().put(TrackingVariable.P_PROP69,s.prop69);

		//s.prop71 = NetworkUtils.isNetworkRoaming();
		s.getContextData().put(TrackingVariable.P_PROP71,s.prop71);

		//s.prop70 = MainActivity.Instance.isLocationPermitted()? networkUtils.getCellLocation():null;
        //s.prop72 = MainActivity.Instance.isReadPhoneStatePermitted() ? networkUtils.getDeviceSoftwareVersion():null;
        s.getContextData().put(TrackingVariable.P_PROP72,s.prop72);

        //s.prop73= MainActivity.Instance.pingTime > 0 ? String.valueOf(MainActivity.Instance.pingTime+"ms") : null;
		s.getContextData().put(TrackingVariable.P_PROP73,s.prop73);
        //s.prop74=

        //new sdr general vars
        s.event3 = "event3";
        s.getContextData().put("event3", s.event3);

        s.prop31 = "mcare";
        s.getContextData().put("prop31", s.prop31);

        s.prop20 = "mcare";
        s.getContextData().put("prop20", s.prop20);

		s.prop5 = "content";
		s.getContextData().put("prop5", s.prop5);

		try {
			Realm realm = Realm.getDefaultInstance();
			GdprPermissions gdprPermissions = (GdprPermissions) RealmManager.getUnManagedRealmObject(realm, GdprPermissions.class);
			s.prop55 = StringUtils.getFieldFromJson(gdprPermissions, AppConfiguration.getGdprPropVariable());
			s.getContextData().put("prop55", s.prop55);
			realm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		s.eVar63 = "mcare";
        s.getContextData().put("eVar63", s.eVar63);

        s.eVar24 = "mcare";
        s.getContextData().put("eVar24", s.eVar24);

        s.eVar6 = MARKET;
        s.getContextData().put("eVar6", MARKET);

        s.eVar73 = s.channel;
        s.getContextData().put("eVar73", s.eVar73);

		s.eVar81 = com.adobe.mobile.Config.getVersion();
		s.getContextData().put("eVar81", s.eVar81);


	}
}
