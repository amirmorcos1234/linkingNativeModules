package ro.vodafone.mcare.android.interfaces.fragment.loyalty;

import ro.vodafone.mcare.android.interfaces.fragment.base.FragmentLifeCycleListener;

/**
 * Created by Serban Radulescu on 9/28/2017.
 */

public interface LoyaltyVoucherCommunicationListener extends FragmentLifeCycleListener{

    public void reloadVouchers();

    public void setRefreshVoucherDetails(boolean refresh);
}
