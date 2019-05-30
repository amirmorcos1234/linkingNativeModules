package ro.vodafone.mcare.android.interfaces;

import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;

/**
 * Created by alexandrulepadatu on 3/1/18.
 */

public interface ActivityFragmentInterface
{
	void attachFragment(BaseFragment fragment);
    NavigationHeader getNavigationHeader();
}
