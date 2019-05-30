package ro.vodafone.mcare.android.card;

import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffersSuccess;
import ro.vodafone.mcare.android.client.model.offers.ActiveOffersPostpaidSuccess;

/**
 * Created by user on 28.03.2017.
 */

public interface YourServicesCardController {

    public void setupDataPrepaid(ActiveOffersSuccess activeOffersSuccess);

    public void setupDataPostpaid(ActiveOffersPostpaidSuccess activeOffersPostpaidSuccess);

}
