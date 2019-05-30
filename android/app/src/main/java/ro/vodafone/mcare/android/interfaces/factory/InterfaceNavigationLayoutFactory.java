package ro.vodafone.mcare.android.interfaces.factory;

import android.support.design.widget.NavigationView;

import ro.vodafone.mcare.android.ui.header.NavigationHeader;

/**
 * Created by Victor Radulescu on 2/5/2018.
 */

public interface InterfaceNavigationLayoutFactory {

    public NavigationHeader initializeNavigationHeader();
    public NavigationHeader getNavigationHeader();
}

