package ro.vodafone.mcare.android.card.additionalPromos;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.BaseCardControllerInterface;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.ActiveOffersPostpaidEbuSuccess;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.BillingOffer;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.Promo;
import ro.vodafone.mcare.android.ui.fragments.yourServices.YourServicesFragment;

/**
 * Created by Bivol Pavel on 18.10.2017.
 */

public class AdditionalPromosCardController implements BaseCardControllerInterface {

    private AdditionalPromosViewGroup viewGroup;
    private YourServicesFragment yourServicesFragment;
    private ActiveOffersPostpaidEbuSuccess activeOffersPostpaidEbuSuccess;
    private CostControl costControl;

    public AdditionalPromosCardController(Context context) {
        this.viewGroup = new AdditionalPromosViewGroup(context, this);
    }

    public AdditionalPromosViewGroup getViewGroup() {
        return viewGroup;
    }

    public void requestData(){
        viewGroup.displayLoadingCard();
        yourServicesFragment = (YourServicesFragment) VodafoneController.findFragment(YourServicesFragment.class);

        if(yourServicesFragment != null){
            yourServicesFragment.requestData();
        }
    }

    @Override
    public void onDataLoaded(Object... args) {
        for (Object value : args) {
            if (value instanceof ActiveOffersPostpaidEbuSuccess) {
                this.activeOffersPostpaidEbuSuccess = (ActiveOffersPostpaidEbuSuccess) value;
            }
            if(value instanceof CostControl){
                this.costControl = (CostControl)value;
            }
        }
        buildCardsList();
    }

    @Override
    public void onRequestFailed() {
        if(yourServicesFragment != null){
            yourServicesFragment.hidePendingOffers();
        }
    }

    private void buildCardsList() {
        if(activeOffersPostpaidEbuSuccess == null || activeOffersPostpaidEbuSuccess.getPendingOffers() == null){
            if(yourServicesFragment != null){
                yourServicesFragment.hidePendingOffers();
            }
        } else {

            viewGroup.removeAllViews();

            List<AdditionalPromosCard> cardList = new ArrayList<>();

            //Create list of future promotions
            if(activeOffersPostpaidEbuSuccess.getPendingOffers().getPromoList() != null &&
                    !activeOffersPostpaidEbuSuccess.getPendingOffers().getPromoList().isEmpty()){
                for (Promo promo : activeOffersPostpaidEbuSuccess.getPendingOffers().getPromoList()) {
                    if(isFutureActivationDate(stringToDate(promo.getPromoActivationDate()))){
                        cardList.add(new AdditionalPromosCard(viewGroup.getContext())
                                .setViewGroup(viewGroup)
                                .setBalanceShowAndNotShownList(costControl)
                                .buildCard(promo, null));
                    }
                }
            }

            //Create additional BOs (ListOfFutureBOs) with future activation date.
            if(activeOffersPostpaidEbuSuccess.getPendingOffers().getBoList() != null &&
                    !activeOffersPostpaidEbuSuccess.getPendingOffers().getBoList().isEmpty()){
                for (BillingOffer billingOffer : activeOffersPostpaidEbuSuccess.getPendingOffers().getBoList()) {
                    if(isFutureActivationDate(stringToDate(billingOffer.getActivationDate()))){
                        cardList.add(new AdditionalPromosCard(viewGroup.getContext())
                                .setViewGroup(viewGroup)
                                .setBalanceShowAndNotShownList(costControl)
                                .buildCard(null, billingOffer));
                    }
                }
            }

            if(!cardList.isEmpty()){
                Collections.sort(cardList, new AdditionalPromosComparator());
                viewGroup.atachCards(cardList);
            }else{
                if(yourServicesFragment != null){
                    yourServicesFragment.hidePendingOffers();
                }
            }
        }
    }

    private Date stringToDate(Long stringDate) {
        return new Date(stringDate);
    }

    private boolean isFutureActivationDate(Date activationDate){
        return activationDate != null && activationDate.after(new Date());
    }

    static class AdditionalPromosComparator implements Comparator<AdditionalPromosCard> {
        public int compare(AdditionalPromosCard c1, AdditionalPromosCard c2)
        {
            if(c1.getPromo() == null || c1.getPromo().getCategoryEnum() == null)
                if(c2.getPromo() == null || c2.getPromo().getCategoryEnum() == null)
                    return 0; //equal
                else
                    return 1; // null is after other strings
            else // this.member != null
                if(c2.getPromo() == null || c2.getPromo().getCategoryEnum() == null)
                    return -1;  // all other strings are before null
                else
                    return c1.getPromo().getCategoryEnum().compareTo(c2.getPromo().getCategoryEnum());
        }
    }
}
