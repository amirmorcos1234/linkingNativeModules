package ro.vodafone.mcare.android.utils.firebaseAnalytics;

import android.os.Bundle;

import java.util.HashMap;

/**
 * Created by Serban Radulescu on 9/27/2018.
 */
public class FirebaseAnalyticsUtils {

	public static Bundle getBundleFromParams(HashMap<String, String> firebaseAnalyticsHashmap) {
		Bundle bundle = new Bundle();
		for (String key : firebaseAnalyticsHashmap.keySet()) {
			bundle.putString(key, firebaseAnalyticsHashmap.get(key));
		}
		return bundle;
	}
}
