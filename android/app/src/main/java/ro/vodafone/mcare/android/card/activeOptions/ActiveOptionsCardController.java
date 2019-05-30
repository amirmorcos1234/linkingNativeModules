package ro.vodafone.mcare.android.card.activeOptions;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.BaseCardControllerInterface;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffersSuccess;
import ro.vodafone.mcare.android.client.model.costControl.BalanceShowAndNotShown;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.offers.ActiveOffersPostpaidSuccess;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.ActiveOffersPostpaidEbuSuccess;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.ui.fragments.yourServices.YourServicesFragment;

public class ActiveOptionsCardController implements BaseCardControllerInterface{
    public static String TAG = "AOCardController";

    private ActiveOptionsViewGroup viewGroup;
    private YourServicesFragment yourServicesFragment;

    private ActiveOffersSuccess activeOffersSuccess;
    private ActiveOffersPostpaidSuccess activeOffersPostpaidSuccess;
    private ActiveOffersPostpaidEbuSuccess activeOffersPostpaidEbuSuccess;
    private CostControl costControl;

    private List<BalanceShowAndNotShown> balanceList = new ArrayList<>();

    public ActiveOptionsCardController(Context context) {
        this.viewGroup = new ActiveOptionsViewGroup(context, this);
    }

    public void setViewGroup(ActiveOptionsViewGroup viewGroup) {
        this.viewGroup = viewGroup;
    }

    public void requestData(){
        viewGroup.displayLoadingCard();

        yourServicesFragment = (YourServicesFragment) VodafoneController.findFragment(YourServicesFragment.class);

        if(yourServicesFragment != null){
            yourServicesFragment.requestData();
        }
    }

    public ActiveOptionsViewGroup getViewGroup(){
        return viewGroup;
    }

    @Override
    public void onDataLoaded(Object... args) {
        for (Object value : args) {
            if(value != null){
                if (value instanceof ActiveOffersSuccess) {
                    this.activeOffersSuccess = (ActiveOffersSuccess)value;
                }
                if (value instanceof ActiveOffersPostpaidSuccess) {
                    this.activeOffersPostpaidSuccess = (ActiveOffersPostpaidSuccess)value;
                }
                if(value instanceof ActiveOffersPostpaidEbuSuccess){
                    this.activeOffersPostpaidEbuSuccess = (ActiveOffersPostpaidEbuSuccess) value;
                }
                if(value instanceof CostControl){
                    this.costControl = (CostControl)value;
                }
            }
        }

        if(VodafoneController.getInstance().getUser() instanceof PrepaidUser){
            buildPrepaidCardsList();
        }else if(VodafoneController.getInstance().getUser() instanceof CBUUser){
            buildCBUCardsList();
        }else if(VodafoneController.getInstance().getUser() instanceof EbuMigrated){
            buildEBUCardsList();
        }
    }

    @Override
    public void onRequestFailed() {
        viewGroup.displayErrorCard();
    }

    private void buildPrepaidCardsList(){
        if(activeOffersSuccess == null || activeOffersSuccess.getActiveOffersList() == null){
            viewGroup.displayErrorCard();
        }else{
            if(activeOffersSuccess.getActiveOffersList().size() == 0){
                viewGroup.displayNoResultCard();
            }else{
                List<ActiveOptionsCard> cardList = new ArrayList<>();
                for (int i = 0; i < activeOffersSuccess.getActiveOffersList().size(); i++){
                    cardList.add(new ActiveOptionsCard(viewGroup.getContext())
                            .setViewGroup(viewGroup)
                            .buildPrepaidCard(activeOffersSuccess.getActiveOffersList().get(i)));
                }
                viewGroup.atachCards(cardList);
            }
        }
    }

    private void buildEBUCardsList(){

        List<BalanceShowAndNotShown> balanceShowAndNotShowns = new ArrayList<>();

        if(costControl != null){
            balanceShowAndNotShowns = costControl.getCurrentExtraoptions().getExtendedBalanceList();
        }

        balanceList.clear();
        balanceList.addAll(balanceShowAndNotShowns);

        if(isEmptyBosAndPromos()){
            if(yourServicesFragment != null){
                yourServicesFragment.hideActiveOptionsCards();
            }
        }else{

            List<ActiveOptionsCard> cardList = new ArrayList<>();

            if(activeOffersPostpaidEbuSuccess.getActiveOffers().getPromoList() != null && !activeOffersPostpaidEbuSuccess.getActiveOffers().getPromoList().isEmpty()){
                for(int i = 0; i < activeOffersPostpaidEbuSuccess.getActiveOffers().getPromoList().size(); i++){
                    cardList.add(new ActiveOptionsCard(viewGroup.getContext())
                            .setViewGroup(viewGroup)
                            .buildEBUCard(activeOffersPostpaidEbuSuccess.getActiveOffers().getPromoList().get(i), null, balanceList));
                }
            }

            if(activeOffersPostpaidEbuSuccess.getActiveOffers().getBoList() != null && !activeOffersPostpaidEbuSuccess.getActiveOffers().getBoList().isEmpty()){
                for(int i = 0; i < activeOffersPostpaidEbuSuccess.getActiveOffers().getBoList().size(); i++){
                    cardList.add(new ActiveOptionsCard(viewGroup.getContext())
                            .setViewGroup(viewGroup)
                            .buildEBUCard(null, activeOffersPostpaidEbuSuccess.getActiveOffers().getBoList().get(i), balanceList));
                }
            }

            if(!cardList.isEmpty()){
                /*Collections.sort(cardList, new ActiveOptionsComparator());*/
                viewGroup.atachCards(cardList);
            }else{
                if(yourServicesFragment != null){
                    yourServicesFragment.hideActiveOptionsCards();
                }
            }
        }
    }

    private boolean isEmptyBosAndPromos(){

        if(activeOffersPostpaidEbuSuccess != null && activeOffersPostpaidEbuSuccess.getActiveOffers() != null){
            if(activeOffersPostpaidEbuSuccess.getActiveOffers().getPromoList() != null &&
                    !activeOffersPostpaidEbuSuccess.getActiveOffers().getPromoList().isEmpty()){
                return false;
            }

            if(activeOffersPostpaidEbuSuccess.getActiveOffers().getBoList() != null &&
                    !activeOffersPostpaidEbuSuccess.getActiveOffers().getBoList().isEmpty()){
                return false;
            }
        }

        return true;
    }

    private void buildCBUCardsList(){
        List<BalanceShowAndNotShown> balanceShowAndNotShowns = new ArrayList<>();

        if(costControl != null){
            balanceShowAndNotShowns = costControl.getCurrentExtraoptions().getExtendedBalanceList();
        }

        balanceList.clear();
        balanceList.addAll(balanceShowAndNotShowns);

        if(activeOffersPostpaidSuccess == null || activeOffersPostpaidSuccess.getActiveOffersList() == null){
            viewGroup.displayErrorCard();
        }else{
            List<ActiveOptionsCard> cardList = new ArrayList<>();

            for(int i = 0; i < activeOffersPostpaidSuccess.getActiveOffersList().size(); i++){
                cardList.add(new ActiveOptionsCard(viewGroup.getContext())
                        .setViewGroup(viewGroup)
                        .buildCBUCard(activeOffersPostpaidSuccess.getActiveOffersList().get(i), balanceList));
            }
            viewGroup.atachCards(cardList);
        }
    }

    public void removeOfferFromBalanceList(BalanceShowAndNotShown balanceShowAndNotShown){
        balanceList.remove(balanceShowAndNotShown);
    }

    static class ActiveOptionsComparator implements Comparator<ActiveOptionsCard> {
        public int compare(ActiveOptionsCard c1, ActiveOptionsCard c2)
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
