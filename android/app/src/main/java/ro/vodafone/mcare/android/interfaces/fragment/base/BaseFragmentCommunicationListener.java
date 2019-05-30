package ro.vodafone.mcare.android.interfaces.fragment.base;

import android.os.Bundle;

/**
 * Created by Serban Radulescu on 9/24/2018.
 */
public interface BaseFragmentCommunicationListener {

	public void sendFirebaseEvent(String firebaseEvent, Bundle bundle);
}
