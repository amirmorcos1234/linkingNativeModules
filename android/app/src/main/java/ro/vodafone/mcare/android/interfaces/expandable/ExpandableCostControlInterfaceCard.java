package ro.vodafone.mcare.android.interfaces.expandable;

import ro.vodafone.mcare.android.client.model.costControl.AdditionalCost;
import ro.vodafone.mcare.android.interfaces.CardModelViewInterface;

/**
 * Created by Victor Radulescu on 2/6/2018.
 */

public interface ExpandableCostControlInterfaceCard extends CardModelViewInterface {
    void onDataLoaded(AdditionalCost additionalCost);
}
