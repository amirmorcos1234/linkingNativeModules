package ro.vodafone.mcare.android.interfaces.factory;

import ro.vodafone.mcare.android.card.VodafoneGenericCard;

/**
 * Created by Victor Radulescu on 9/5/2017.
 */

public interface InterfaceErrorCardFactory {
    VodafoneGenericCard getErrorCardView();

    VodafoneGenericCard getErrorCardWithTextView(String errorMessage);
}
