package ro.vodafone.mcare.android.utils.firebaseAnalytics;

import java.io.Serializable;
import java.util.HashMap;

import ro.vodafone.mcare.android.utils.StringMsisdnCrypt;

/**
 * Created by Serban Radulescu on 9/27/2018.
 */
public class FirebaseAnalyticsItem implements Serializable {

	private String firebaseAnalyticsEvent;
	private HashMap<String, String> firebaseAnalyticsParams;

	public FirebaseAnalyticsItem() {
		firebaseAnalyticsParams = new HashMap<>();
	}

	public FirebaseAnalyticsItem(String firebaseAnalyticsEvent, HashMap<String, String> firebaseAnalyticsParams) {
		this.firebaseAnalyticsEvent = firebaseAnalyticsEvent;
		this.firebaseAnalyticsParams = firebaseAnalyticsParams;
	}

	public String getFirebaseAnalyticsEvent() {
		return firebaseAnalyticsEvent;
	}

	public void setFirebaseAnalyticsEvent(String firebaseAnalyticsEvent) {
		this.firebaseAnalyticsEvent = firebaseAnalyticsEvent;
	}

	public HashMap<String, String> getFirebaseAnalyticsParams() {
		return firebaseAnalyticsParams;
	}

	public void addFirebaseAnalyticsParam(String paramKey, String paramValue) {
		firebaseAnalyticsParams.put(paramKey, paramValue);
	}

	/**
	 This method should ne use with an even number of arguments in the form (key, value)
	 i.e. addFirebaseAnalyticsParams("top_up_MSISDN", msisdn)
	 or addFirebaseAnalyticsParams("top_up_MSISDN", msisdn, "top_up_XXEUR", amount)
	 */
	public void addFirebaseAnalyticsParams(String... paramValues) {
		String paramKey = "";
		String paramValue = "";
		for (String item : paramValues) {
			if(paramKey.isEmpty()) {
				paramKey = item;
			} else {
				paramValue = item;
				firebaseAnalyticsParams.put(paramKey, paramValue);
				paramKey = "";
			}
		}
	}

	public void addEncryptedFirebaseAnalyticsParam(String paramKey, String paramValue) {
		StringMsisdnCrypt msisdnCrypt = new StringMsisdnCrypt();
		String encryptedParamValue = "";
		try {
			encryptedParamValue = StringMsisdnCrypt.bytesToHex(msisdnCrypt.encrypt(paramValue));
		} catch (Exception e) {
			e.printStackTrace();
		}
		firebaseAnalyticsParams.put(paramKey, encryptedParamValue);
	}
}
