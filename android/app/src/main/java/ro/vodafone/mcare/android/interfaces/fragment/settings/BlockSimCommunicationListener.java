package ro.vodafone.mcare.android.interfaces.fragment.settings;

/**
 * Created by Serban Radulescu on 3/20/2018.
 */

public interface BlockSimCommunicationListener {
	public void checkSimStatus();

	public void sendBlockSimRequest(String alternativePhoneNumber);

	public void sendUnblockSimRequest(String alternativePhoneNumber);

	public void checkEbuSimStatus();

	public void sendEbuBlockSimRequest();

	public void sendEbuUnblockSimRequest();

}
