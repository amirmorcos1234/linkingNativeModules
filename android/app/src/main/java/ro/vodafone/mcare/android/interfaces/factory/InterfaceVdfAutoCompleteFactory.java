package ro.vodafone.mcare.android.interfaces.factory;

import ro.vodafone.mcare.android.presenter.adapterelements.VdfAutoCompleteTvElement;
import ro.vodafone.mcare.android.ui.views.autocomplete.VodafoneAutoCompleteTextView;

/**
 * Created by Victor Radulescu on 2/12/2018.
 */

public interface InterfaceVdfAutoCompleteFactory {

    VodafoneAutoCompleteTextView getAutoCompleteView();
    VdfAutoCompleteTvElement createAutoCompleteElement(int positionInAdapter);
    VdfAutoCompleteTvElement getAutoCompleteElement();
}
