package ro.vodafone.mcare.android.presenter.adapterelements;

import ro.vodafone.mcare.android.presenter.adapterelements.base.StaticAdapterElement;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;

/**
 * Created by Victor Radulescu on 8/25/2017.
 */

public class NavigationHeaderElement extends StaticAdapterElement {

    NavigationHeader navigationHeader;

    public NavigationHeaderElement(int type, int order, NavigationHeader navigationHeader) {
        super(type, order);
        this.navigationHeader = navigationHeader;
    }

    protected NavigationHeaderElement(int type, int order) {
        super(type, order);
    }

    @Override
    public NavigationHeader getView() {
        return navigationHeader;
    }

    @Override
    public boolean hasGeneralBackground(){
        return false;
    }
}
